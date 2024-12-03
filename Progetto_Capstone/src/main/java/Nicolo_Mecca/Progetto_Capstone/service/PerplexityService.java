package Nicolo_Mecca.Progetto_Capstone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PerplexityService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${perplexity.api.key}")
    private String apiKey;

    public String getExplanation(String question, String userAnswer, String correctAnswer) throws Exception {
        String prompt = String.format("Question: %s\nUser's answer: %s\nCorrect answer: %s\nProvide a concise but comprehensive explanation of why the user's answer is incorrect .", question, userAnswer, correctAnswer);
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", "llama-3.1-sonar-small-128k-online",  // Updated model
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a concise educational assistant."),
                        Map.of("role", "user", "content", prompt)
                )
        ));
        String response = Unirest.post("https://api.perplexity.ai/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .asString()
                .getBody();

        JsonNode jsonResponse = objectMapper.readTree(response);
        if (jsonResponse.has("choices") && jsonResponse.get("choices").isArray() && jsonResponse.get("choices").size() > 0) {
            JsonNode firstChoice = jsonResponse.get("choices").get(0);
            if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                return firstChoice.get("message").get("content").asText();
            }
        }
        throw new Exception("Invalid API response: " + response);
    }
}