package com.sprint.project.monew.notification.mapper;


import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.notification.dto.NotificationRequest;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.entity.NotificationEntity;
import org.mapstruct.Mapper;

import javax.management.Notification;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    NotificationEntity  toNotification(NotificationRequest request);
    NotificationResponse toNotificationRepose(NotificationEntity notification);

    List<NotificationResponse> toNotificationResponses(List<NotificationEntity> notificationResponses);




}
