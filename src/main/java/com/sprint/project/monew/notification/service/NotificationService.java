package com.sprint.project.monew.notification.service;

import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import com.sprint.project.monew.notification.mapper.NotificationMapper;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



}
