package com.customercare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class IntentClassificationService {
    
    @Autowired
    private RAGService ragService;
    
    @Autowired
    private OrderStatusService orderStatusService;
    
    private static final Pattern ORDER_STATUS_PATTERN = Pattern.compile(
        "(?i).*(order|status|track|tracking|shipment|delivery|where.*order|order.*status).*"
    );
    
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile(
        "(?i).*\\b(\\d{4,})\\b.*"
    );
    
    public String processUserInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "I didn't catch that. Could you please repeat your question?";
        }
        
        String input = userInput.trim().toLowerCase();
        
        if (ORDER_STATUS_PATTERN.matcher(input).matches()) {
            return handleOrderStatusIntent(userInput);
        }
        
        return ragService.answerQuestion(userInput);
    }
    
    private String handleOrderStatusIntent(String userInput) {
        java.util.regex.Matcher matcher = ORDER_NUMBER_PATTERN.matcher(userInput);
        
        if (matcher.find()) {
            String orderId = matcher.group(1);
            return orderStatusService.getOrderStatusResponse(orderId);
        } else {
            return "I'd be happy to help you check your order status. Could you please provide your order number? It's usually a 4-6 digit number.";
        }
    }
    
    public String getIntentType(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "unknown";
        }
        
        String input = userInput.trim().toLowerCase();
        
        if (ORDER_STATUS_PATTERN.matcher(input).matches()) {
            return "order_status";
        }
        
        return "faq";
    }
}
