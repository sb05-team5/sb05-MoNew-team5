package com.sprint.project.monew.notification.service;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import com.sprint.project.monew.notification.mapper.NotificationMapper;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private  final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;


    @Transactional
    public NotificationResponse createNotification(NotificationRequest request){
        NotificationEntity notification = notificationMapper.toNotification(request);
        return notificationMapper.toNotificationRepose(notificationRepository.save(notification));
    }



    @Transactional
    public  void allCheckNotification(UUID userId) {
        List<NotificationEntity> notifications = notificationRepository.findAllByUserId(userId);

        for (NotificationEntity notification : notifications) {
            notification.setConfirmed(true); // 확인 상태로 변경
            notification.setUpdatedAt(Instant.now());
        }
        notificationRepository.saveAll(notifications);
    }
    @Transactional
    public void oneCheckNotification(UUID userId , UUID notificationId){

        List<NotificationEntity> list  = notificationRepository.findAllByUserIdAndResourceId(userId, notificationId);
        list.forEach(n ->{
            n.setConfirmed(true);
            n.setUpdatedAt(Instant.now());
        });

        notificationRepository.saveAll(list);


    }




    @Transactional(readOnly = true)
    public CursorPageResponse<NotificationResponse> findAllWithMeta(int limit, String cursor, Instant after, UUID userId) {
        // --- 1) 커서 디코딩 ---
        Instant cursorUpdatedAt = null;
        UUID cursorId = null;
        if (cursor != null && !cursor.isBlank()) {
            try {
                String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
                String[] parts = raw.split(":");
                cursorUpdatedAt = Instant.ofEpochMilli(Long.parseLong(parts[0]));
                cursorId = UUID.fromString(parts[1]);
            } catch (Exception ignored) {}
        }

        int pageSize = Math.min(Math.max(limit, 1), 100);

        // --- 2) DB 조회 ---
        List<NotificationEntity> list = notificationRepository.findUnreadByCursor(
                userId, cursorUpdatedAt, cursorId, PageRequest.of(0, pageSize + 1));

        boolean hasNext = list.size() > pageSize;
        if (hasNext) list = list.subList(0, pageSize);

        // --- 3) nextCursor 생성 ---
        String nextCursor = null;
        if (hasNext && !list.isEmpty()) {
            NotificationEntity last = list.get(list.size() - 1);
            String raw = last.getUpdatedAt().toEpochMilli() + ":" + last.getId();
            nextCursor = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        }

        // --- 4) 매핑 ---
        List<NotificationResponse> responses = list.stream()
                .map(notificationMapper::toNotificationRepose)
                .toList();

        // --- 5) 응답 빌드 ---
        return CursorPageResponse.<NotificationResponse>builder()
                .content(responses)
                .nextCursor(nextCursor)
                .nextAfter(after != null ? after.toString() : null)
                .size(responses.size())
                .hasNext(hasNext)
                .totalElements(null)
                .build();
    }



}
