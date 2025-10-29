package com.sprint.project.monew.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto (
        UUID id,
        Instant  createdAt,
        Instant updatedAt,
        boolean confirmed,
        UUID  userid,
        String content,
        String resourceType,
        UUID resourceId

){
}
