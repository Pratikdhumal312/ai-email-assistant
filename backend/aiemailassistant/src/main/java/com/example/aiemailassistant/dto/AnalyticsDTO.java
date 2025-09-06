package com.example.aiemailassistant.dto;

import java.util.Map;

public class AnalyticsDTO {
    private long total;
    private long urgent;
    private long resolved;
    private long pending;
    private Map<String, Long> sentimentCounts;

    // Constructors
    public AnalyticsDTO() {}

    public AnalyticsDTO(long total, long urgent, long resolved, long pending, Map<String, Long> sentimentCounts) {
        this.total = total;
        this.urgent = urgent;
        this.resolved = resolved;
        this.pending = pending;
        this.sentimentCounts = sentimentCounts;
    }

    // Getters and Setters
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getUrgent() { return urgent; }
    public void setUrgent(long urgent) { this.urgent = urgent; }

    public long getResolved() { return resolved; }
    public void setResolved(long resolved) { this.resolved = resolved; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public Map<String, Long> getSentimentCounts() { return sentimentCounts; }
    public void setSentimentCounts(Map<String, Long> sentimentCounts) { this.sentimentCounts = sentimentCounts; }
}
