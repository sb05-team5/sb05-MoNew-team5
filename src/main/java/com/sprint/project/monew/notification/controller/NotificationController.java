package com.sprint.project.monew.notification.controller;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.notification.dto.NotificationDto;
import com.sprint.project.monew.notification.dto.NotificationResponse;
import com.sprint.project.monew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

//    private final NotificationService notificationService;
//
//    // 알림 조회
//    @GetMapping("")
//    public ResponseEntity<CursorPageResponse<NotificationResponse>> findAllNotifications(
//            @RequestParam(required = false) String cursor,
//            @RequestParam(required = false) Instant after,
//            @RequestParam(required = true, defaultValue = "50") int limit,
//            @RequestHeader("Monew-Request-User-ID") UUID userId
//    ){
//
//        return ResponseEntity.ok(
//                notificationService.findAllWithMeta(limit, cursor, after, userId)
//        );
//    }
//
//        // 전체 알림 확인
//    @PatchMapping("")
//    public ResponseEntity<Void> allCheckNotifications(
//            @RequestHeader("Monew-Request-User-ID") UUID userId
//    ){
//        // 알림 전체 DB  확인변경
//        notificationService.allCheckNotification(userId);
//        return ResponseEntity.noContent().build();
//
//    }
//
//    @PatchMapping("/{notificationId}")
//    public  ResponseEntity<Void> oneCheckNotification(
//            @PathVariable UUID notificationId,
//            @RequestHeader("Monew-Request-User-ID") UUID userId
//    ){
//        //알림 한개만 변경되도록
//        notificationService.oneCheckNotification(userId, notificationId);
//        return ResponseEntity.noContent().build();
//    }




}
