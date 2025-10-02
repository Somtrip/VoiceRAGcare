package com.customercare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String EMBEDDING_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/embedding-001:embedContent";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                return responseJson.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
            } else {
                throw new RuntimeException("Gemini API error: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini API", e);
        }
    }

    public List<Double> generateEmbedding(String text) {
        try {
            Map<String, Object> requestBody = Map.of(
                "model", "models/embedding-001",
                "content", Map.of(
                    "parts", List.of(
                        Map.of("text", text)
                    )
                )
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EMBEDDING_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                JsonNode valuesNode = responseJson.path("embedding").path("values");

                List<Double> embedding = new ArrayList<>();
                for (JsonNode value : valuesNode) {
                    embedding.add(value.asDouble());
                }
                return embedding;
            } else {
                throw new RuntimeException("Gemini Embedding API error: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini Embedding API", e);
        }
    }

    public double calculateSimilarity(List<Double> embedding1, List<Double> embedding2) {
        if (embedding1.size() != embedding2.size()) {
            throw new IllegalArgumentException("Embeddings must have the same dimension");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < embedding1.size(); i++) {
            double val1 = embedding1.get(i);
            double val2 = embedding2.get(i);

            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
