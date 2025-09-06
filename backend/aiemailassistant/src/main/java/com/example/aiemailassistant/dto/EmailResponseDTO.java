package com.example.aiemailassistant.dto;

import com.example.aiemailassistant.model.*;
import java.time.LocalDateTime;
import java.util.Map;

public class EmailResponseDTO {
    private Long id;
    private String sender;
    private String subject;
    private String body;
    private LocalDateTime sentDate;
    private Sentiment sentiment;
    private Priority priority;
    private Status status;
    private String category;
    private Map<String, String> extractedInfo;

    // Constructors
    public EmailResponseDTO() {}

    public EmailResponseDTO(Email email) {
        this.id = email.getId();
        this.sender = email.getSender();
        this.subject = email.getSubject();
        this.body = email.getBody();
        this.sentDate = email.getSentDate();
        this.sentiment = email.getSentiment();
        this.priority = email.getPriority();
        this.status = email.getStatus();
        this.category = email.getCategory();
        this.extractedInfo = email.getExtractedInfo();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getSentDate() { return sentDate; }
    public void setSentDate(LocalDateTime sentDate) { this.sentDate = sentDate; }

    public Sentiment getSentiment() { return sentiment; }
    public void setSentiment(Sentiment sentiment) { this.sentiment = sentiment; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Map<String, String> getExtractedInfo() { return extractedInfo; }
    public void setExtractedInfo(Map<String, String> extractedInfo) { this.extractedInfo = extractedInfo; }
}
