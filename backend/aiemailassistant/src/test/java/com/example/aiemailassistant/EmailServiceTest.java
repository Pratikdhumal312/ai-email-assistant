package com.example.aiemailassistant;

import com.example.aiemailassistant.model.*;
import com.example.aiemailassistant.service.EmailService;
import com.example.aiemailassistant.repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @Test
    void testProcessNewEmail() {
        String sender = "test@example.com";
        String subject = "Urgent: Cannot access account";
        String body = "I cannot access my account and need immediate help";

        Email email = emailService.processNewEmail(sender, subject, body);

        assertNotNull(email.getId());
        assertEquals(sender, email.getSender());
        assertEquals(subject, email.getSubject());
        assertEquals(Priority.URGENT, email.getPriority());
        assertEquals(Sentiment.NEGATIVE, email.getSentiment());
        assertEquals("LOGIN", email.getCategory());
    }

    @Test
    void testAnalytics() {
        // Create test emails
        emailService.processNewEmail("test1@example.com", "Help needed", "I need help");
        emailService.processNewEmail("test2@example.com", "Urgent issue", "Critical problem");

        var analytics = emailService.getAnalytics();

        assertTrue(analytics.getTotal() >= 2);
        assertTrue(analytics.getUrgent() >= 1);
    }
}
