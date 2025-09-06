package com.example.aiemailassistant.dto;

public class SendResponseRequest {
    private String response;
    private Long emailId;

    public SendResponseRequest() {}

    public SendResponseRequest(String response, Long emailId) {
        this.response = response;
        this.emailId = emailId;
    }

    // Getters and Setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public Long getEmailId() { return emailId; }
    public void setEmailId(Long emailId) { this.emailId = emailId; }
}