package com.sprint.project.monew.notification.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
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


    @CreatedDate
    @Column(columnDefinition = "timestamp with time zone default now()",
            nullable = false)
    private Instant updated_at;


    @Column(nullable = false )
    private  boolean confirmed;

    @Column(nullable = false, length = 255)
    private  String  content;

    @Column(nullable = false, length = 10)
    private String resource_type;

    @Column(nullable = false)
    private UUID resource_id;

    @Column(nullable = false)
    private UUID uuid_id;





}
