package com.sprint.project.monew.notification.repository;

import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findAllByUserId(UUID userId);

    List<NotificationEntity> findAllByUserIdAndResourceId(UUID userId, UUID resourceId);


}
