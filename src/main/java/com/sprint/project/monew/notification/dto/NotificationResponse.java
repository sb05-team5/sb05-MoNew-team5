package com.sprint.project.monew.notification.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean confirmed;
    private String content;
    private String resourceType;
    private UUID resourceId;
    private UUID uuidId;
}
