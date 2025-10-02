package com.customercare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "faqs")
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String question;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "source_document")
    private String sourceDocument;
    
    @Column(columnDefinition = "TEXT")
    private String embedding;
    
    public FAQ() {}
    
    public FAQ(String question, String answer, String category, String sourceDocument) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.sourceDocument = sourceDocument;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
    
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
}
