package com.sprint.project.monew.notification.service;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import com.sprint.project.monew.notification.mapper.NotificationMapper;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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


    @Transactional(readOnly = true)
    public CursorPageResponse<NotificationResponse> findAllWithMeta(int limit, String cursor, Instant after, UUID userId) {

        // 실제 데이터 조회
        List<NotificationResponse> all = notificationMapper.toNotificationResponses(notificationRepository.findAll());

        // 총 데이터 수
        int total = all.size();

        // limit 만큼만 잘라내기
        List<NotificationResponse> data = (total > limit) ? all.subList(0, limit) : all;

        // 커서 (다음 페이지용) — 마지막 ID를 nextCursor 로 예시
        String nextCursor = (total > limit) ? data.get(data.size() - 1).getId().toString() : null;

        // hasNext 계산
        boolean hasNext = total > limit;

        return CursorPageResponse.<NotificationResponse>builder()
                .content(data)
                .nextCursor(nextCursor)
                .nextAfter(after != null ? after.toString() : null)
                .size(data.size())
                .hasNext(hasNext)
                .totalElements(total)
                .build();
    }



}
