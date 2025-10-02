package com.customercare.controller;

import com.customercare.model.FAQ;
import com.customercare.repository.FAQRepository;
import com.customercare.service.IntentClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "*")
public class VoiceAssistantController {
    
    @Autowired
    private IntentClassificationService intentClassificationService;
    
    @Autowired
    private FAQRepository faqRepository;
    
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String userInput = request.get("message");
            if (userInput == null || userInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Message is required"
                ));
            }
            
            System.out.println("[" + LocalDateTime.now() + "] User Query: " + userInput);
            String intent = intentClassificationService.getIntentType(userInput);
            String response = intentClassificationService.processUserInput(userInput);
            
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            
            System.out.println("[" + LocalDateTime.now() + "] Intent: " + intent + ", Latency: " + latency + "ms");
            System.out.println("[" + LocalDateTime.now() + "] Response: " + response);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("response", response);
            responseBody.put("intent", intent);
            responseBody.put("latency", latency);
            responseBody.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(responseBody);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            
            System.err.println("[" + LocalDateTime.now() + "] Error processing request: " + e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("response", "I'm sorry, I'm having trouble processing your request right now. Please try again.");
            errorResponse.put("intent", "error");
            errorResponse.put("latency", latency);
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("service", "Voice RAG Assistant");
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/test-order")
    public ResponseEntity<Map<String, Object>> testOrder(@RequestBody Map<String, String> request) {
        String orderId = request.get("orderId");
        if (orderId == null) {
            orderId = "12345";
        }
        
        String testMessage = "What's the status of order " + orderId + "?";
        String response = intentClassificationService.processUserInput(testMessage);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("query", testMessage);
        responseBody.put("response", response);
        responseBody.put("intent", "order_status");
        
        return ResponseEntity.ok(responseBody);
    }
    
    @GetMapping("/debug/faqs")
    public ResponseEntity<Map<String, Object>> debugFaqs() {
        List<FAQ> faqs = faqRepository.findAll();
        Map<String, Object> debug = new HashMap<>();
        debug.put("total_faqs", faqs.size());
        debug.put("faqs_with_embeddings", faqRepository.findAllWithEmbeddings().size());
        debug.put("sample_questions", faqs.stream().limit(5).map(FAQ::getQuestion).toList());
        
        return ResponseEntity.ok(debug);
    }
    
    @GetMapping("/debug/search")
    public ResponseEntity<Map<String, Object>> debugSearch(@RequestParam String query) {
        List<FAQ> keywordMatches = faqRepository.findByKeyword(query);
        Map<String, Object> debug = new HashMap<>();
        debug.put("query", query);
        debug.put("keyword_matches", keywordMatches.size());
        debug.put("matched_questions", keywordMatches.stream().map(FAQ::getQuestion).toList());
        
        return ResponseEntity.ok(debug);
    }
}
