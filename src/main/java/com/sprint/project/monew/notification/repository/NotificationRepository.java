package com.sprint.project.monew.notification.repository;

import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  NotificationRepository extends JpaRepository<NotificationEntity, Long> {

}
