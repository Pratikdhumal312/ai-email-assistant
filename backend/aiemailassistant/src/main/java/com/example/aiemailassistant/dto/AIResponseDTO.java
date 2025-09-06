package com.example.aiemailassistant.dto;

public class AIResponseDTO {
    private String response;
    private boolean success;
    private String message;

    public AIResponseDTO() {}

    public AIResponseDTO(String response) {
        this.response = response;
        this.success = true;
    }

    public AIResponseDTO(String response, boolean success, String message) {
        this.response = response;
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

