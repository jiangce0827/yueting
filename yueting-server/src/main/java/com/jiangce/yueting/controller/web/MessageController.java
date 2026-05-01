package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.ArtistNotificationContent;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.PrivateMessageSendDTO;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.NotificationVO;
import com.jiangce.yueting.domain.vo.PrivateMessageVO;
import com.jiangce.yueting.service.NotificationService;
import com.jiangce.yueting.service.PrivateMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/web")
@Tag(name = "消息相关接口")
@RequiredArgsConstructor
public class MessageController {

    private final PrivateMessageService privateMessageService;
    private final NotificationService notificationService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取私信会话列表")
    @GetMapping("/private-message/list")
    public Result<List<PrivateMessageVO>> getPrivateMessageList() {
        Long userId = BaseContext.getCurrentId();
        return Result.success(privateMessageService.getConversationList(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取与某用户的聊天记录")
    @GetMapping("/private-message/chat/{userId}")
    public Result<List<PrivateMessageVO>> getChatHistory(
            @Parameter(description = "对方用户ID") @PathVariable String userId) {
        Long currentUserId = BaseContext.getCurrentId();
        Long targetUserId = new BigInteger(userId).longValue();
        List<PrivateMessageVO> list = privateMessageService.getChatHistory(currentUserId, targetUserId);
        privateMessageService.markAsRead(currentUserId, targetUserId);
        return Result.success(list);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "发送私信")
    @PostMapping("/private-message/send")
    public Result<Void> sendPrivateMessage(@RequestBody PrivateMessageSendDTO sendDTO) {
        Long senderId = BaseContext.getCurrentId();
        privateMessageService.sendMessage(senderId, sendDTO);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "删除私信")
    @DeleteMapping("/private-message/{messageId}")
    public Result<Void> deletePrivateMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        Long userId = BaseContext.getCurrentId();
        privateMessageService.deleteMessage(messageId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "标记私信已读")
    @PostMapping("/private-message/read/{senderId}")
    public Result<Void> markPrivateMessageRead(
            @Parameter(description = "发送者ID") @PathVariable String senderId) {
        Long userId = BaseContext.getCurrentId();
        Long senderIdLong = new BigInteger(senderId).longValue();
        privateMessageService.markAsRead(userId, senderIdLong);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "清除与某用户的对话")
    @DeleteMapping("/private-message/clear/{targetUserId}")
    public Result<Void> clearConversation(
            @Parameter(description = "对方用户ID") @PathVariable String targetUserId) {
        Long userId = BaseContext.getCurrentId();
        Long targetUserIdLong = new BigInteger(targetUserId).longValue();
        privateMessageService.clearConversation(userId, targetUserIdLong);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取通知列表")
    @GetMapping("/notification/list")
    public Result<List<NotificationVO>> getNotificationList(
            @Parameter(description = "通知类型，多个用逗号分隔") @RequestParam(required = false) String type) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(notificationService.getNotificationList(userId, type));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "删除通知")
    @DeleteMapping("/notification/{notificationId}")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Long userId = BaseContext.getCurrentId();
        notificationService.deleteNotification(notificationId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "标记通知已读")
    @PostMapping("/notification/read/{notificationId}")
    public Result<Void> markNotificationRead(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Long userId = BaseContext.getCurrentId();
        notificationService.markAsRead(notificationId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "获取音乐人专属通知列表")
    @GetMapping("/artist-notification/list")
    public Result<List<NotificationVO>> getArtistNotificationList() {
        Long userId = BaseContext.getCurrentId();
        return Result.success(notificationService.getNotificationList(userId, ArtistNotificationContent.TYPE_STRING));
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "获取音乐人专属通知未读数量")
    @GetMapping("/artist-notification/unread-count")
    public Result<Long> getArtistNotificationUnreadCount() {
        Long userId = BaseContext.getCurrentId();
        return Result.success(notificationService.getUnreadCount(userId, ArtistNotificationContent.TYPE_STRING));
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "标记音乐人专属通知已读")
    @PostMapping("/artist-notification/read/{notificationId}")
    public Result<Void> markArtistNotificationRead(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Long userId = BaseContext.getCurrentId();
        notificationService.markAsRead(notificationId, userId);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "一键标记音乐人专属通知已读")
    @PostMapping("/artist-notification/read-all")
    public Result<Void> markAllArtistNotificationsRead() {
        Long userId = BaseContext.getCurrentId();
        notificationService.markAllAsRead(userId, ArtistNotificationContent.TYPE_STRING);
        return Result.success(null);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取未读消息数量")
    @GetMapping("/message/unread-count")
    public Result<Map<String, Long>> getUnreadCount() {
        Long userId = BaseContext.getCurrentId();
        Long privateUnread = privateMessageService.getUnreadCount(userId);
        Long commentUnread = notificationService.getUnreadCount(userId, "3,6,7,8");
        Long notificationUnread = notificationService.getUnreadCount(userId, "1,2,4,5,11,12");

        Map<String, Long> result = new HashMap<>();
        result.put("private", privateUnread);
        result.put("comment", commentUnread);
        result.put("notification", notificationUnread);
        return Result.success(result);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "一键标记所有消息为已读")
    @PostMapping("/message/mark-all-read")
    public Result<Void> markAllAsRead() {
        Long userId = BaseContext.getCurrentId();
        notificationService.markAllAsRead(userId);
        return Result.success(null);
    }
}
