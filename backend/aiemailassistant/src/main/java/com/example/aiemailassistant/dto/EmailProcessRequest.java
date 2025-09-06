package com.example.aiemailassistant.dto;

public class EmailProcessRequest {
    private String sender;
    private String subject;
    private String body;

    public EmailProcessRequest() {}

    public EmailProcessRequest(String sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}