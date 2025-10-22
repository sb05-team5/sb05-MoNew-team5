package com.sprint.project.monew.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationRequest(
        Instant updatedAt,
        boolean confirmed,
        String content,
        String resourceType,
        UUID resourceId,
        UUID uuidId
) {
}
