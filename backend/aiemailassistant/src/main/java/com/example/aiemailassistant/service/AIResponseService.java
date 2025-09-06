package com.example.aiemailassistant.service;

import com.example.aiemailassistant.model.Email;
import org.springframework.stereotype.Service;

@Service
public class AIResponseService {

    // Template-based response generation
    // In production, integrate with OpenAI, Hugging Face, or other AI services
    public String generateResponse(Email email) {
        String senderName = email.getSender().split("@")[0];
        String category = email.getCategory();
        
        switch (category) {
            case "LOGIN":
                return generateLoginResponse(senderName, email);
            case "BILLING":
                return generateBillingResponse(senderName, email);
            case "INTEGRATION":
                return generateIntegrationResponse(senderName, email);
            case "TECHNICAL":
                return generateTechnicalResponse(senderName, email);
            case "PRICING":
                return generatePricingResponse(senderName, email);
            default:
                return generateGeneralResponse(senderName, email);
        }
    }

    private String generateLoginResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "I understand your frustration with the login issue, and I sincerely apologize for the inconvenience this has caused.\n\n" +
            "I've immediately escalated your case to our technical team. As a temporary solution, I'm sending you a secure password reset link that bypasses the current issue.\n\n" +
            "We'll have this resolved within the next 2 hours and will keep you updated.\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName
        );
    }

    private String generateBillingResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "I sincerely apologize for the billing error and understand how concerning duplicate charges can be.\n\n" +
            "I've immediately processed a refund for the duplicate charge, and you should see it reflected in your account within 2-3 business days. I'm also implementing additional safeguards to prevent this from happening again.\n\n" +
            "Reference ID: REF-%d\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName, System.currentTimeMillis()
        );
    }

    private String generateIntegrationResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "Thank you for reaching out regarding integration options. We do support third-party API integrations including popular CRM platforms like Salesforce, HubSpot, and Pipedrive.\n\n" +
            "I'd be happy to schedule a technical consultation to discuss your specific requirements and provide detailed integration documentation.\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName
        );
    }

    private String generateTechnicalResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "I understand this technical issue is critical for your operations, and we're treating this with the highest priority.\n\n" +
            "Our engineering team has been immediately notified and is actively investigating the issue. We'll provide updates every 30 minutes until resolution.\n\n" +
            "Expected resolution time: Within 1 hour\n" +
            "Urgent hotline: +1-800-SUPPORT\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName
        );
    }

    private String generatePricingResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "Thank you for your interest in our pricing structure. I'm pleased to provide you with a comprehensive breakdown of our pricing tiers.\n\n" +
            "I'm attaching our detailed pricing guide and would be happy to schedule a personalized demo to help you choose the best plan for your needs.\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName
        );
    }

    private String generateGeneralResponse(String senderName, Email email) {
        return String.format(
            "Dear %s,\n\n" +
            "Thank you for contacting us. We have received your inquiry and our support team will review your request carefully.\n\n" +
            "We will respond to you within 24 hours with a detailed solution.\n\n" +
            "Best regards,\n" +
            "Support Team",
            senderName
        );
    }
}