package com.Tienld.diary_project.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "google.cloud")
public class SpeechToTextConfig {
    private Map<String, String> credentials;
    //    private String credentials;
    private Map<String, String> endpoints;

    @Bean
    public SpeechClient speechClient() throws IOException {
        // Lấy đường dẫn file từ map
        String path = credentials.get("file").replace("classpath:", "");

        try (InputStream credentialsStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {
            if (credentialsStream == null) {
                throw new IllegalArgumentException("Không tìm thấy file credentials trong classpath: " + path);
            }
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentialsStream);
            SpeechSettings speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> googleCredentials)
                    .build();
            return SpeechClient.create(speechSettings);

        } catch (IOException e) {
            throw new IOException("Lỗi khi đọc file credentials: " + e.getMessage(), e);
        }
    }
}
