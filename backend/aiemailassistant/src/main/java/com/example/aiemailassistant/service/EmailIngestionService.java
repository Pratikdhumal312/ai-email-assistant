package com.example.aiemailassistant.service;

import com.example.aiemailassistant.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailIngestionService {

    @Autowired
    private EmailService emailService;

    // Initialize with demo data
    @PostConstruct
    public void initializeDemoData() {
        createDemoEmails();
    }

    // Simulate email fetching every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void fetchNewEmails() {
        // Here you would integrate with IMAP/Gmail API/Outlook API
        // For demo purposes, we'll occasionally add a new email
        if (Math.random() < 0.1) { // 10% chance
            createRandomEmail();
        }
    }

    private void createDemoEmails() {
        List<DemoEmailData> demoEmails = Arrays.asList(
            new DemoEmailData("eve@startup.io", "Help required with account verification", 
                "Do you support integration with third-party APIs? Specifically, I'm looking for CRM integration options.", 
                LocalDateTime.now().minusHours(2)),
            new DemoEmailData("diana@client.co", "General query about subscription", 
                "Hi team, I am unable to log into my account since yesterday. Could you please help me resolve this issue?", 
                LocalDateTime.now().minusHours(1)),
            new DemoEmailData("alice@example.com", "Critical help needed for downtime", 
                "Our servers are down, and we need immediate support. This is highly critical.", 
                LocalDateTime.now().minusMinutes(30)),
            new DemoEmailData("bob@customer.com", "Query about product pricing", 
                "Hello, I wanted to understand the pricing tiers better. Could you share a detailed breakdown?", 
                LocalDateTime.now().minusHours(3)),
            new DemoEmailData("charlie@partner.org", "Immediate support needed for billing error", 
                "There is a billing error where I was charged twice. This needs immediate correction.", 
                LocalDateTime.now().minusMinutes(45)),
            new DemoEmailData("sarah@company.com", "Support needed for login issue", 
                "I am facing issues with verifying my account. The verification email never arrived. Can you assist?", 
                LocalDateTime.now().minusHours(4)),
            new DemoEmailData("mike@business.net", "Request for refund process clarification", 
                "Could you clarify the steps involved in requesting a refund? I submitted one last week but have no update.", 
                LocalDateTime.now().minusHours(6)),
            new DemoEmailData("lisa@enterprise.com", "Urgent request: system access blocked", 
                "This is urgent – our system is completely inaccessible, and this is affecting our operations.", 
                LocalDateTime.now().minusMinutes(15))
        );

        for (DemoEmailData demo : demoEmails) {
            Email email = emailService.processNewEmail(demo.sender, demo.subject, demo.body);
            email.setSentDate(demo.sentDate);
        }
    }

    private void createRandomEmail() {
        String[] senders = {"john@example.com", "jane@company.com", "support@client.org"};
        String[] subjects = {"Help needed", "Technical support", "Billing question", "Account access"};
        String[] bodies = {"I need help with my account", "Having technical issues", "Question about billing", "Cannot access my account"};

        String sender = senders[(int)(Math.random() * senders.length)];
        String subject = subjects[(int)(Math.random() * subjects.length)];
        String body = bodies[(int)(Math.random() * bodies.length)];

        emailService.processNewEmail(sender, subject, body);
    }

    private static class DemoEmailData {
        String sender, subject, body;
        LocalDateTime sentDate;

        DemoEmailData(String sender, String subject, String body, LocalDateTime sentDate) {
            this.sender = sender;
            this.subject = subject;
            this.body = body;
            this.sentDate = sentDate;
        }
    }
}

