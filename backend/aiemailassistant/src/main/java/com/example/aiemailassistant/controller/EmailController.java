package com.example.aiemailassistant.controller;

import com.example.aiemailassistant.dto.*;
import com.example.aiemailassistant.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/emails")
    public ResponseEntity<List<EmailResponseDTO>> getAllEmails() {
        List<EmailResponseDTO> emails = emailService.getAllEmails();
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/emails/{id}")
    public ResponseEntity<EmailResponseDTO> getEmailById(@PathVariable Long id) {
        EmailResponseDTO email = emailService.getEmailById(id);
        return ResponseEntity.ok(email);
    }

    @PostMapping("/emails/{id}/generate-response")
    public ResponseEntity<AIResponseDTO> generateResponse(@PathVariable Long id) {
        try {
            AIResponseDTO response = emailService.generateResponse(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AIResponseDTO(null, false, "Error generating response: " + e.getMessage()));
        }
    }

    @PostMapping("/emails/{id}/respond")
    public ResponseEntity<String> sendResponse(@PathVariable Long id, @RequestBody SendResponseRequest request) {
        try {
            String result = emailService.sendResponse(id, request.getResponse());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending response: " + e.getMessage());
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = emailService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/emails/process")
    public ResponseEntity<EmailResponseDTO> processNewEmail(@RequestBody EmailProcessRequest request) {
        try {
            var email = emailService.processNewEmail(request.getSender(), request.getSubject(), request.getBody());
            return ResponseEntity.ok(new EmailResponseDTO(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
