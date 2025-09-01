package com.Tienld.diary_project.service;

import com.Tienld.diary_project.config.GeminiApiConfig;
import com.Tienld.diary_project.enums.Emotion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.io.IOException;
@Service
@Slf4j
public class GeminiAPIService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GeminiApiConfig geminiApiConfig;
    
    // Dự đoán cảm xúc từ text + icon
    public Emotion predictTextEmotion(String text, String icon) {
        if (text == null || text.isEmpty()) {
            log.error("Text input is null or empty");
            throw new IllegalArgumentException("Text input cannot be null or empty");
        }
        
        String prompt = String.format("Phân tích cảm xúc của đoạn văn: '%s' %s. Trả về một trong các từ: POSITIVE, NEGATIVE, NEUTRAL", 
                                    text, icon != null ? "với icon: " + icon : "");
        
        try {
            String response = callGeminiAPI(prompt);
            return parseEmotionFromResponse(response);
        } catch (Exception e) {
            log.error("Error predicting text emotion: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi dự đoán cảm xúc từ text: " + e.getMessage(), e);
        }
    }

    // Dự đoán cảm xúc từ ảnh (base64)
    public Emotion predictImageEmotion(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image bytes cannot be null or empty");
        }
        
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String prompt = "Phân tích cảm xúc từ ảnh này. Trả về một trong các từ: POSITIVE, NEGATIVE, NEUTRAL";
        
        try {
            String response = callGeminiVisionAPI(prompt, base64Image);
            return parseEmotionFromResponse(response);
        } catch (Exception e) {
            log.error("Error predicting image emotion: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi dự đoán cảm xúc từ ảnh: " + e.getMessage(), e);
        }
    }
    
    // Tạo lời khuyên dựa trên cảm xúc
    public String generateAdvice(String text, Emotion emotion) {
        String prompt = String.format("Tạo lời khuyên ngắn gọn và tích cực cho nội dung: '%s' với cảm xúc: %s. Trả về lời khuyên khoảng 2 dòng.",
                                    text, emotion.getDescription());

        try {
            String response = callGeminiAPI(prompt);
            String extractedText = extractTextFromResponse(response);
            return extractedText;
        } catch (Exception e) {
            log.error("Error generating advice: {}", e.getMessage());
            return "Hãy giữ tinh thần tích cực và tiếp tục cố gắng!";
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Parse theo structure: candidates[0].content.parts[0].text
            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode firstCandidate = candidatesNode.get(0);
                JsonNode contentNode = firstCandidate.path("content");
                JsonNode partsNode = contentNode.path("parts");

                if (partsNode.isArray() && partsNode.size() > 0) {
                    JsonNode firstPart = partsNode.get(0);
                    JsonNode textNode = firstPart.path("text");

                    if (!textNode.isMissingNode()) {
                        String extractedText = textNode.asText().trim();
                        log.info("Extracted text: {}", extractedText);
                        return extractedText;
                    }
                }
            }

            log.warn("Could not extract text from response: {}", jsonResponse);
            return "Không thể trích xuất nội dung từ phản hồi AI.";

        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            return "Lỗi khi xử lý phản hồi từ AI: " + e.getMessage();
        }
    }
    // Chat với AI
    public String getChatbotResponse(String userMessage, String context) {
        if (userMessage == null || userMessage.isEmpty()) {
            throw new IllegalArgumentException("User message cannot be null or empty");
        }
        
        String prompt = String.format("Bạn là một người bạn tâm tình thân thiện. Hãy trả lời câu hỏi: '%s'. %s", 
                                    userMessage, 
                                    context != null ? "Context: " + context : "");
        
        try {
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            log.error("Error getting chatbot response: {}", e.getMessage());
            return "Xin lỗi, tôi đang gặp vấn đề kỹ thuật. Bạn có thể thử lại sau.";
        }
    }
    
    // Helper method gọi Gemini Text API
//    private String callGeminiAPI(String prompt) {
//        HttpHeaders headers = new HttpHeaders();
//
//        // Kiểm tra API key có được inject đúng không
//        String apiKey = geminiApiConfig.getKey();
//        log.info("Using API key: {}", apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL");
//
//        // Sửa Authorization header - Gemini cần "Bearer" + API key
//        headers.set("Authorization", "Bearer " + apiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String requestBody = String.format(
//            "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
//            prompt.replace("\"", "\\\"")
//        );
//
//        log.info("Request body: {}", requestBody);
//
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                geminiApiConfig.getEndpoints().get("text-generation"),
//                request, String.class);
//
//            log.info("Response status: {}", response.getStatusCode());
//            log.info("Response body: {}", response.getBody());
//
//            return response.getBody();
//        } catch (Exception e) {
//            log.error("Error calling Gemini API: {}", e.getMessage());
//            throw e;
//        }
//    }
//
//    // Helper method gọi Gemini Vision API
//    private String callGeminiVisionAPI(String prompt, String base64Image) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + geminiApiConfig.getKey());
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String requestBody = String.format(
//            "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}, {\"inlineData\": {\"mimeType\": \"image/jpeg\", \"data\": \"%s\"}}]}]}",
//            prompt.replace("\"", "\\\""), base64Image
//        );
//
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(
//            geminiApiConfig.getEndpoints().get("image-generation"),
//            request, String.class);
//
//        return response.getBody();
//    }
    // Helper method gọi Gemini Text API với đúng authentication
    private String callGeminiAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // BỎ Authorization header - Gemini sử dụng query parameter

        String apiKey = geminiApiConfig.getKey();
        log.info("Using API key: {}", apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL");

        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
                prompt.replace("\"", "\\\"")
        );

        log.info("Request body: {}", requestBody);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            // Thêm API key vào query parameter thay vì header
            String urlWithKey = geminiApiConfig.getEndpoints().get("text-generation") + "?key=" + apiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(
                    urlWithKey,
                    request, String.class);

            log.info("Response status: {}", response.getStatusCode());
            log.info("Response body: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            throw e;
        }
    }

    //Helper method gọi Gemini Vision API
    private String callGeminiVisionAPI(String prompt, String base64Image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiKey = geminiApiConfig.getKey();
        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}, {\"inlineData\": {\"mimeType\": \"image/jpeg\", \"data\": \"%s\"}}]}]}",
                prompt.replace("\"", "\\\""), base64Image
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        //Thêm API key vào query parameter
        String urlWithKey = geminiApiConfig.getEndpoints().get("image-generation") + "?key=" + apiKey;
        ResponseEntity<String> response = restTemplate.postForEntity(
                urlWithKey,
                request, String.class);

        return response.getBody();
    }

    // Parse emotion từ response thực tế
    private Emotion parseEmotionFromResponse(String response) {
        if (response == null) return Emotion.NEUTRAL;
        
        response = response.toLowerCase();
        if (response.contains("positive") || response.contains("tích cực")) {
            return Emotion.POSITIVE;
        } else if (response.contains("negative") || response.contains("tiêu cực")) {
            return Emotion.NEGATIVE;
        } else {
            return Emotion.NEUTRAL;
        }
    }
}
