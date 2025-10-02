package com.customercare.service;

import com.customercare.model.FAQ;
import com.customercare.repository.FAQRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGService {

    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private GeminiService geminiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String answerQuestion(String question) {
        try {
            List<FAQ> faqs = faqRepository.findAllWithEmbeddings();

            if (faqs.isEmpty()) {
                faqs = performKeywordSearch(question);
            }

            if (faqs.isEmpty()) {
                return "I don't have enough information to answer that question right now. Please contact our customer service team for assistance.";
            }

            if (!faqs.isEmpty() && faqs.get(0).getEmbedding() != null) {
                return performSemanticSearch(question, faqs);
            } else {
                return performKeywordBasedResponse(question, faqs);
            }

        } catch (Exception e) {
            return "I'm sorry, I'm having trouble processing your question right now. Please try again or contact our customer service team.";
        }
    }

    private String performSemanticSearch(String question, List<FAQ> faqs) {
        try {
            List<Double> questionEmbedding = geminiService.generateEmbedding(question);

            List<FAQWithSimilarity> similarities = new ArrayList<>();

            for (FAQ faq : faqs) {
                try {
                    List<Double> faqEmbedding = objectMapper.readValue(
                        faq.getEmbedding(),
                        new TypeReference<List<Double>>() {}
                    );

                    double similarity = geminiService.calculateSimilarity(questionEmbedding, faqEmbedding);
                    similarities.add(new FAQWithSimilarity(faq, similarity));
                } catch (Exception ignored) {
                }
            }

            List<FAQ> topFAQs = similarities.stream()
                .sorted(Comparator.comparing(FAQWithSimilarity::getSimilarity).reversed())
                .limit(3)
                .map(FAQWithSimilarity::getFaq)
                .collect(Collectors.toList());

            if (topFAQs.isEmpty()) {
                return "I don't have information about that topic. Please contact our customer service team for assistance.";
            }

            StringBuilder context = new StringBuilder();
            for (FAQ faq : topFAQs) {
                context.append("Q: ").append(faq.getQuestion()).append("\n");
                context.append("A: ").append(faq.getAnswer()).append("\n");
                context.append("Source: ").append(faq.getSourceDocument()).append("\n\n");
            }

            String prompt = String.format(
                "You are a helpful customer service assistant. Based on the following context from our knowledge base, " +
                "answer the user's question. Always include a citation to the source document in your response. " +
                "If the question is not covered in the context, politely say you don't have that information.\n\n" +
                "Context:\n%s\n" +
                "User Question: %s\n\n" +
                "Please provide a helpful answer and include the source document reference.",
                context.toString(),
                question
            );

            return geminiService.generateResponse(prompt);

        } catch (Exception e) {
            return "I'm sorry, I'm having trouble processing your question right now. Please try again or contact our customer service team.";
        }
    }

    private List<FAQ> performKeywordSearch(String question) {
        List<FAQ> allFaqs = faqRepository.findAll();

        String[] keywords = question.toLowerCase().split("\\s+");

        List<FAQScored> scoredFaqs = new ArrayList<>();
        for (FAQ faq : allFaqs) {
            int score = calculateKeywordScore(faq, keywords);
            if (score > 0) {
                scoredFaqs.add(new FAQScored(faq, score));
            }
        }

        scoredFaqs.sort((a, b) -> Integer.compare(b.score, a.score));

        List<FAQ> result = scoredFaqs.stream()
            .limit(3)
            .map(fs -> fs.faq)
            .collect(java.util.stream.Collectors.toList());

        return result;
    }

    private int calculateKeywordScore(FAQ faq, String[] keywords) {
        String questionText = faq.getQuestion().toLowerCase();
        String answerText = faq.getAnswer().toLowerCase();

        int score = 0;
        for (String keyword : keywords) {
            if (keyword.length() > 2) {
                if (questionText.contains(keyword)) {
                    score += 3;
                }
                if (answerText.contains(keyword)) {
                    score += 1;
                }
            }
        }

        return score;
    }

    private static class FAQScored {
        final FAQ faq;
        final int score;

        FAQScored(FAQ faq, int score) {
            this.faq = faq;
            this.score = score;
        }
    }

    private boolean containsKeywords(FAQ faq, String[] keywords) {
        String faqText = (faq.getQuestion() + " " + faq.getAnswer()).toLowerCase();

        int matches = 0;
        for (String keyword : keywords) {
            if (keyword.length() > 2 && faqText.contains(keyword)) {
                matches++;
            }
        }

        return matches > 0;
    }

    private String performKeywordBasedResponse(String question, List<FAQ> faqs) {
        try {
            StringBuilder context = new StringBuilder();
            for (FAQ faq : faqs) {
                context.append("Q: ").append(faq.getQuestion()).append("\n");
                context.append("A: ").append(faq.getAnswer()).append("\n");
                context.append("Source: ").append(faq.getSourceDocument()).append("\n\n");
            }

            String prompt = String.format(
                "You are a helpful customer service assistant. Based on the following context from our knowledge base, " +
                "answer the user's question. Always include a citation to the source document in your response. " +
                "If the question is not covered in the context, politely say you don't have that information.\n\n" +
                "Context:\n%s\n" +
                "User Question: %s\n\n" +
                "Please provide a helpful answer and include the source document reference.",
                context.toString(),
                question
            );

            return geminiService.generateResponse(prompt);

        } catch (Exception e) {
            if (!faqs.isEmpty()) {
                FAQ bestMatch = faqs.get(0);
                return bestMatch.getAnswer() + " (from " + bestMatch.getSourceDocument() + ")";
            }
            return "I'm sorry, I'm having trouble processing your question right now. Please try again or contact our customer service team.";
        }
    }

    private static class FAQWithSimilarity {
        private final FAQ faq;
        private final double similarity;

        public FAQWithSimilarity(FAQ faq, double similarity) {
            this.faq = faq;
            this.similarity = similarity;
        }

        public FAQ getFaq() { return faq; }
        public double getSimilarity() { return similarity; }
    }
}
