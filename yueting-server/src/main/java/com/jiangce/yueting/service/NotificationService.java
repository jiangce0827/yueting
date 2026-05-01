package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.vo.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 获取通知列表
     *
     * @param receiverId 接收者ID
     * @param typeStr 类型字符串，逗号分隔
     * @return 通知列表
     */
    List<NotificationVO> getNotificationList(Long receiverId, String typeStr);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId 当前用户ID
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 获取未读通知数量
     *
     * @param receiverId 接收者ID
     * @param typeStr 类型字符串，逗号分隔
     * @return 未读数量
     */
    Long getUnreadCount(Long receiverId, String typeStr);

    /**
     * 标记所有通知为已读
     *
     * @param receiverId 接收者ID
     */
    void markAllAsRead(Long receiverId);

    void markAllAsRead(Long receiverId, String typeStr);

    void markAsRead(Long notificationId, Long userId);

    /**
     * 创建通知
     *
     * @param receiverId 接收者ID
     * @param senderId 发送者ID（可为null）
     * @param type 通知类型
     * @param content 通知内容
     * @param targetId 关联目标ID
     * @param targetType 关联目标类型
     * @param targetSubId 关联目标父级ID
     */
    void createNotification(Long receiverId, Long senderId, Integer type, String content,
                           Long targetId, Integer targetType, Long targetSubId);
}
