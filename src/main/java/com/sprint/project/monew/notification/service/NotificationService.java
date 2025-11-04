package com.sprint.project.monew.notification.service;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import com.sprint.project.monew.notification.mapper.NotificationMapper;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private  final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;





    @Transactional
    public List<NotificationResponse> notifyInterestArticlesRegistered(
            UUID interestId,
            String interestName,
            int articleCount,
            Collection<UUID> subscriberUserIds
    ) {
        if (subscriberUserIds == null || subscriberUserIds.isEmpty()) return List.of();

        Instant now = Instant.now();
        String content = "[%s]와 관련된 기사가 %d건 등록되었습니다.".formatted(interestName, articleCount);

        List<NotificationEntity> toSave = subscriberUserIds.stream()
                .map(uid -> NotificationEntity.builder()
                        .userId(uid)
                        .confirmed(false)
                        .updatedAt(now)
                        .content(content)
                        .resourceType("INTEREST")
                        .resourceId(interestId)
                        .build()
                )
                .collect(Collectors.toList());
        return notificationRepository.saveAll(toSave)
                .stream()
                .map(notificationMapper::toNotificationRepose)
                .toList();
    }



    @Transactional
    public NotificationResponse notifyCommentLiked(
            UUID commentId,
            UUID commentOwnerUserId,
            String likerDisplayName
    ) {
        Instant now = Instant.now();
        String content = "[%s]님이 나의 댓글을 좋아합니다.".formatted(likerDisplayName);

        NotificationEntity n = NotificationEntity.builder()
                .userId(commentOwnerUserId)
                .confirmed(false)
                .updatedAt(now)
                .content(content)
                .resourceType("COMMENT")
                .resourceId(commentId)
                .build();

        return notificationMapper.toNotificationRepose(notificationRepository.save(n));
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
    public void oneCheckNotification(UUID userId, UUID notificationId) {
            notificationRepository.markOneAsConfirmed(userId, notificationId, Instant.now());
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<NotificationResponse> findAllWithMeta(
            int limit, String cursor, Instant after, UUID userId) {

        // 1) 커서 디코딩
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

        // 2) 페이지 크기 & 정렬
        int pageSize = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(
                0, pageSize + 1, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );

        // 3) DB 조회 (커서 유/무 분기)
        List<NotificationEntity> list;
        if (cursorUpdatedAt == null || cursorId == null) {
            // 첫 페이지: 커서 조건 없이 최신부터
            list = notificationRepository.findUnreadFirstPage(userId, pageable);
        } else {
            // 다음 페이지: 커서 기준으로 keyset
            list = notificationRepository.findUnreadByCursor(userId, cursorUpdatedAt, cursorId, pageable);
        }

        // 4) hasNext & 잘라내기
        boolean hasNext = list.size() > pageSize;
        if (hasNext) {
            list = list.subList(0, pageSize);
        }

        // 5) nextCursor 생성
        String nextCursor = null;
        if (hasNext && !list.isEmpty()) {
            NotificationEntity last = list.get(list.size() - 1);
            String raw = last.getUpdatedAt().toEpochMilli() + ":" + last.getId();
            nextCursor = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        }

        // (선택) after 파라미터를 서버 응답 메타로만 반영 (필터로 쓰려면 리포지토리에 조건 추가)
        List<NotificationResponse> responses = list.stream()
                .map(notificationMapper::toNotificationRepose)
                .toList();

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
