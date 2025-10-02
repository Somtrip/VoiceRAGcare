package com.customercare.service;

import com.customercare.model.FAQ;
import com.customercare.repository.FAQRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class DataIngestionService {
    
    @Autowired
    private FAQRepository faqRepository;
    
    @Autowired
    private GeminiService geminiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void ingestData() {
        try {
            long existingCount = faqRepository.count();
            if (existingCount > 0) {
                System.out.println("FAQ data already exists (" + existingCount + " records). Skipping ingestion.");
                return;
            }
            System.out.println("Starting FAQ data ingestion...");
            ClassPathResource resource = new ClassPathResource("data/sample-faqs.json");
            InputStream inputStream = resource.getInputStream();
            
            List<Map<String, String>> faqData = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Map<String, String>>>() {}
            );
            
            int processed = 0;
            for (Map<String, String> faqMap : faqData) {
                try {
                    FAQ faq = new FAQ(
                        faqMap.get("question"),
                        faqMap.get("answer"),
                        faqMap.get("category"),
                        faqMap.get("sourceDocument")
                    );
                    try {
                        String textForEmbedding = faq.getQuestion() + " " + faq.getAnswer();
                        List<Double> embedding = geminiService.generateEmbedding(textForEmbedding);
                        String embeddingJson = objectMapper.writeValueAsString(embedding);
                        faq.setEmbedding(embeddingJson);
                        System.out.println("Generated embedding for: " + faq.getQuestion());
                    } catch (Exception embeddingError) {
                        System.out.println("Could not generate embedding for: " + faq.getQuestion() + 
                                         " (will use keyword search fallback)");
                    }
                    faqRepository.save(faq);
                    processed++;
                    System.out.println("Saved FAQ " + processed + "/" + faqData.size() + ": " + faq.getQuestion());
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.err.println("Error processing FAQ: " + faqMap.get("question") + " - " + e.getMessage());
                }
            }
            System.out.println("FAQ data ingestion completed. Processed " + processed + " FAQs.");
        } catch (Exception e) {
            System.err.println("Error during data ingestion: " + e.getMessage());
        }
    }
}
