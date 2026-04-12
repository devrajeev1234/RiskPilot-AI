package com.loanguard.repository;

import com.loanguard.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    List<ChatMessage> findByUserIdOrderByCreatedAtAsc(String userId);

    List<ChatMessage> findTop10BySessionIdOrderByCreatedAtDesc(String sessionId);
}
