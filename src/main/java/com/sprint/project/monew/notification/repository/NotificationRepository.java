package com.sprint.project.monew.notification.repository;

import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 단건 확인 처리 (userId 소유권 검증 + 중복 확인 방지)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update NotificationEntity n
           set n.confirmed = true,
               n.updatedAt = :now
         where n.id = :notificationId
           and n.userId = :userId
           and n.confirmed = false
    """)
    int markOneAsConfirmed(@Param("userId") UUID userId,
                           @Param("notificationId") UUID notificationId,
                           @Param("now") Instant now);

    // 첫 페이지: 커서 조건 없이 최신부터
    @Query("""
        select n from NotificationEntity n
        where n.userId = :userId
          and n.confirmed = false
        order by n.updatedAt desc, n.id desc
        """)
    List<NotificationEntity> findUnreadFirstPage(
            @Param("userId") UUID userId,
            Pageable pageable
    );

    // 다음 페이지: 커서(updatedAt, id) 기준 Keyset
    @Query("""
        select n from NotificationEntity n
        where n.userId = :userId
          and n.confirmed = false
          and (
                n.updatedAt < :cursorUpdatedAt
                or (n.updatedAt = :cursorUpdatedAt and n.id < :cursorId)
              )
        order by n.updatedAt desc, n.id desc
        """)
    List<NotificationEntity> findUnreadByCursor(
            @Param("userId") UUID userId,
            @Param("cursorUpdatedAt") Instant cursorUpdatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );


}
