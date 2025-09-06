package com.example.aiemailassistant.service;

import com.example.aiemailassistant.dto.*;
import com.example.aiemailassistant.model.*;
import com.example.aiemailassistant.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AIResponseService aiResponseService;

    public List<EmailResponseDTO> getAllEmails() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return emailRepository.findRecentEmailsOrderByPriority(yesterday)
                .stream()
                .map(EmailResponseDTO::new)
                .collect(Collectors.toList());
    }

    public EmailResponseDTO getEmailById(Long id) {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found with id: " + id));
        return new EmailResponseDTO(email);
    }

    public Email processNewEmail(String sender, String subject, String body) {
        Email email = new Email(sender, subject, body);
        
        // Analyze sentiment
        Sentiment sentiment = sentimentAnalysisService.analyzeSentiment(subject + " " + body);
        email.setSentiment(sentiment);
        
        // Determine priority
        Priority priority = determinePriority(subject, body);
        email.setPriority(priority);
        
        // Extract category and info
        String category = extractCategory(subject, body);
        email.setCategory(category);
        
        Map<String, String> extractedInfo = extractInformation(sender, subject, body);
        email.setExtractedInfo(extractedInfo);
        
        return emailRepository.save(email);
    }

    public AIResponseDTO generateResponse(Long emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email not found with id: " + emailId));
        
        String response = aiResponseService.generateResponse(email);
        email.setAiResponse(response);
        emailRepository.save(email);
        
        return new AIResponseDTO(response);
    }

    public String sendResponse(Long emailId, String responseText) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email not found with id: " + emailId));
        
        // Here you would integrate with your email sending service
        // For now, we'll just mark as resolved
        email.setStatus(Status.RESOLVED);
        email.setAiResponse(responseText);
        emailRepository.save(email);
        
        return "Response sent successfully";
    }

    public AnalyticsDTO getAnalytics() {
        long total = emailRepository.count();
        long urgent = emailRepository.countUrgentEmails();
        long resolved = emailRepository.countResolvedEmails();
        long pending = emailRepository.countPendingEmails();
        
        List<Object[]> sentimentData = emailRepository.countBySentiment();
        Map<String, Long> sentimentCounts = new HashMap<>();
        sentimentCounts.put("positive", 0L);
        sentimentCounts.put("negative", 0L);
        sentimentCounts.put("neutral", 0L);
        
        for (Object[] row : sentimentData) {
            Sentiment sentiment = (Sentiment) row[0];
            Long count = (Long) row[1];
            sentimentCounts.put(sentiment.name().toLowerCase(), count);
        }
        
        return new AnalyticsDTO(total, urgent, resolved, pending, sentimentCounts);
    }

    private Priority determinePriority(String subject, String body) {
        String text = (subject + " " + body).toLowerCase();
        String[] urgentKeywords = {"critical", "urgent", "immediately", "down", "cannot access", 
                                 "billing error", "system access blocked", "servers down", "critical help"};
        
        for (String keyword : urgentKeywords) {
            if (text.contains(keyword)) {
                return Priority.URGENT;
            }
        }
        return Priority.NORMAL;
    }

    private String extractCategory(String subject, String body) {
        String text = (subject + " " + body).toLowerCase();
        
        if (text.contains("login") || text.contains("access") || text.contains("password")) {
            return "LOGIN";
        } else if (text.contains("billing") || text.contains("payment") || text.contains("refund")) {
            return "BILLING";
        } else if (text.contains("api") || text.contains("integration") || text.contains("crm")) {
            return "INTEGRATION";
        } else if (text.contains("server") || text.contains("down") || text.contains("technical")) {
            return "TECHNICAL";
        } else if (text.contains("pricing") || text.contains("plan") || text.contains("subscription")) {
            return "PRICING";
        }
        return "GENERAL";
    }

    private Map<String, String> extractInformation(String sender, String subject, String body) {
        Map<String, String> info = new HashMap<>();
        info.put("contactDetails", sender);
        
        String text = (subject + " " + body).toLowerCase();
        List<String> keywords = new ArrayList<>();
        
        // Extract common keywords
        String[] commonKeywords = {"login", "account", "billing", "api", "integration", "server", 
                                 "pricing", "help", "support", "urgent", "critical"};
        
        for (String keyword : commonKeywords) {
            if (text.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        info.put("keywords", String.join(", ", keywords));
        
        // Extract requirements based on category
        if (text.contains("login")) {
            info.put("requirements", "Account access restoration");
        } else if (text.contains("billing")) {
            info.put("requirements", "Billing issue resolution");
        } else if (text.contains("api") || text.contains("integration")) {
            info.put("requirements", "API/Integration support");
        } else if (text.contains("server")) {
            info.put("requirements", "Technical issue resolution");
        } else if (text.contains("pricing")) {
            info.put("requirements", "Pricing information");
        } else {
            info.put("requirements", "General support");
        }
        
        return info;
    }
}