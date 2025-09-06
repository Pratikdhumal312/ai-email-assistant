package com.example.aiemailassistant.repository;

import com.example.aiemailassistant.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    
    List<Email> findByStatus(Status status);
    
    List<Email> findByPriority(Priority priority);
    
    List<Email> findBySentiment(Sentiment sentiment);
    
    List<Email> findBySentDateAfter(LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM Email e WHERE e.priority = 'URGENT'")
    long countUrgentEmails();
    
    @Query("SELECT COUNT(e) FROM Email e WHERE e.status = 'RESOLVED'")
    long countResolvedEmails();
    
    @Query("SELECT COUNT(e) FROM Email e WHERE e.status = 'PENDING'")
    long countPendingEmails();
    
    @Query("SELECT e.sentiment, COUNT(e) FROM Email e GROUP BY e.sentiment")
    List<Object[]> countBySentiment();
    
    @Query("SELECT e FROM Email e WHERE e.sentDate >= :startDate ORDER BY " +
           "CASE WHEN e.priority = 'URGENT' THEN 1 ELSE 2 END, e.sentDate DESC")
    List<Email> findRecentEmailsOrderByPriority(LocalDateTime startDate);
}
