package com.loanguard.service;

import com.loanguard.dto.NotificationResponse;
import com.loanguard.model.Notification;
import com.loanguard.model.User;
import com.loanguard.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notifRepo;

    public NotificationService(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    @Transactional
    public void send(User user, String title, String message, String type, String link) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLink(link);
        notifRepo.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll(Long userId) {
        return notifRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notifRepo.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long id) {
        notifRepo.findById(id).ifPresent(n -> { n.setIsRead(true); notifRepo.save(n); });
    }

    @Transactional
    public void markAllRead(Long userId) {
        notifRepo.markAllReadByUserId(userId);
    }
}