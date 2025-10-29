package com.sprint.project.monew.notification.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@SuperBuilder
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity extends BaseEntity {
//
//    updated_at TIMESTAMP NULL,
//    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
//    content VARCHAR(255) NOT NULL,
//    resource_type VARCHAR(10) NOT NULL,
//    resource_id UUID NOT NULL,
//    user_id UUID NOT NULL,


    // updated_at TIMESTAMP WITH TIME ZONE NULL
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone")
    private Instant updatedAt;


    @Column(name = "confirmed", nullable = false)
    @Builder.Default
    private boolean confirmed = false;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "resource_type", nullable = false, length = 10)
    private String resourceType;


    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userId;


}
