package com.sprint.project.monew.notification.repository;

import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

//  @Modifying(clearAutomatically = true, flushAutomatically = true)
//  @Query("delete NotificationEntity n where n.user_id in :userIds")
//  void deleteAllByUserIds(List<UUID> userIds);
}
