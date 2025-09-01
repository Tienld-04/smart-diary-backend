package com.Tienld.diary_project.service;

import com.Tienld.diary_project.config.GeminiApiConfig;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;

@Service
@Slf4j
public class SpeekToTextService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GeminiApiConfig geminiApiConfig;

    @Autowired
    private SpeechClient speechClient;

    // Chuyển voice -> text
    public String convertVoiceToText(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new IllegalArgumentException("Audio data không được null hoặc rỗng");
        }
        
        try {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("vi-VN")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioData))
                    .build();

            RecognizeRequest request = RecognizeRequest.newBuilder()
                    .setConfig(config)
                    .setAudio(audio)
                    .build();

            RecognizeResponse response = speechClient.recognize(request);
            StringBuilder transcribedText = new StringBuilder();
            
            for (SpeechRecognitionResult result : response.getResultsList()) {
                if (!result.getAlternativesList().isEmpty()) {
                    transcribedText.append(result.getAlternativesList().get(0).getTranscript());
                }
            }
            
            return transcribedText.toString();
        } catch (Exception e) {
            log.error("Lỗi khi chuyển giọng nói thành văn bản", e);
            throw new RuntimeException("Lỗi khi chuyển giọng nói thành văn bản: " + e.getMessage());
        }
    }

    public void streamVoiceToText(WebSocketSession session, InputStream audioStream) {
        if (session == null || audioStream == null) {
            throw new IllegalArgumentException("Session và audioStream không được null");
        }
        
        try {
            // Cấu hình nhận diện
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("vi-VN")
                    .build();

            StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(config)
                    .setInterimResults(true) // cho phép trả kết quả tạm thời
                    .build();
            // Observer để nhận kết quả từ Google API
            ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<>() {
                @Override
                public void onStart(StreamController controller) {
                    log.info("Bắt đầu streaming speech recognition");
                }

                @Override
                public void onResponse(StreamingRecognizeResponse response) {
                    for (StreamingRecognitionResult result : response.getResultsList()) {
                        if (result.getAlternativesCount() > 0) {
                            String transcript = result.getAlternatives(0).getTranscript();
                            try {
                                session.sendMessage(new TextMessage(transcript)); // gửi realtime về client
                            } catch (Exception e) {
                                log.error("Lỗi khi gửi message qua WebSocket", e);
                            }
                        }
                    }
                }
                @Override
                public void onComplete() {
                    log.info("Hoàn thành streaming speech recognition");
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Lỗi trong streaming speech recognition", t);
                }
            };

            // Mở stream tới Google API
            ClientStream<StreamingRecognizeRequest> clientStream =
                    speechClient.streamingRecognizeCallable().splitCall(responseObserver);

            // Gửi config trước
            clientStream.send(StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingConfig)
                    .build());

            // Đọc audio chunk từ inputStream và gửi lên API
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                clientStream.send(StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(buffer, 0, bytesRead))
                        .build());
            }
            // Kết thúc gửi
            clientStream.closeSend();
        } catch (Exception e) {
            log.error("Lỗi khi xử lý streaming speech-to-text", e);
            throw new RuntimeException("Lỗi khi xử lý streaming speech-to-text: " + e.getMessage(), e);
        }
    }
}
