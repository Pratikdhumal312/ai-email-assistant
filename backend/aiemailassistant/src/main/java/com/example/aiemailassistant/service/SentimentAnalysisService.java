package com.example.aiemailassistant.service;

import com.example.aiemailassistant.model.Sentiment;
import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {

    // Simple rule-based sentiment analysis
    // In production, you would integrate with Hugging Face, OpenAI, or other AI services
    public Sentiment analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        String lowerText = text.toLowerCase();
        
        // Positive keywords
        String[] positiveKeywords = {"thank", "appreciate", "great", "excellent", "good", "happy", "satisfied"};
        
        // Negative keywords  
        String[] negativeKeywords = {"urgent", "critical", "problem", "issue", "error", "cannot", "unable", 
                                   "frustrated", "disappointed", "terrible", "awful", "worst", "hate", "angry"};
        
        int positiveScore = 0;
        int negativeScore = 0;
        
        for (String keyword : positiveKeywords) {
            if (lowerText.contains(keyword)) {
                positiveScore++;
            }
        }
        
        for (String keyword : negativeKeywords) {
            if (lowerText.contains(keyword)) {
                negativeScore++;
            }
        }
        
        if (positiveScore > negativeScore) {
            return Sentiment.POSITIVE;
        } else if (negativeScore > positiveScore) {
            return Sentiment.NEGATIVE;
        } else {
            return Sentiment.NEUTRAL;
        }
    }
}
