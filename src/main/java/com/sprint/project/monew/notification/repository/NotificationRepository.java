package com.sprint.project.monew.notification.repository;

import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete NotificationEntity n where n.userId in :userIds")
  void deleteAllByUserIds(List<UUID> userIds);

    long deleteByConfirmedIsTrueAndUpdatedAtBefore(Instant threshold);


    List<NotificationEntity> findAllByUserId(UUID userId);

    List<NotificationEntity> findAllByUserIdAndResourceId(UUID userId, UUID resourceId);



    @Query("""
        SELECT n
          FROM NotificationEntity n
         WHERE n.userId = :userId
           AND n.confirmed = false
           AND (
                :cursorUpdatedAt IS NULL
                OR n.updatedAt < :cursorUpdatedAt
                OR (n.updatedAt = :cursorUpdatedAt AND n.id < :cursorId)
           )
         ORDER BY n.updatedAt DESC, n.id DESC
    """)
    List<NotificationEntity> findUnreadByCursor(UUID userId,
                                                Instant cursorUpdatedAt,
                                                UUID cursorId,
                                                org.springframework.data.domain.Pageable pageable);

}
