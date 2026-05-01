package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.PrivateMessageSendDTO;
import com.jiangce.yueting.domain.vo.PrivateMessageVO;

import java.util.List;

/**
 * 私信服务接口
 */
public interface PrivateMessageService {

    /**
     * 获取私信会话列表
     *
     * @param userId 当前用户ID
     * @return 私信会话列表
     */
    List<PrivateMessageVO> getConversationList(Long userId);

    /**
     * 获取与某用户的聊天记录
     *
     * @param userId 当前用户ID
     * @param targetUserId 对方用户ID
     * @return 聊天记录列表
     */
    List<PrivateMessageVO> getChatHistory(Long userId, Long targetUserId);

    /**
     * 发送私信
     *
     * @param senderId 发送者ID
     * @param sendDTO 私信内容
     */
    void sendMessage(Long senderId, PrivateMessageSendDTO sendDTO);

    /**
     * 删除私信
     *
     * @param messageId 消息ID
     * @param userId 当前用户ID
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * 获取未读私信数量
     *
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    Long getUnreadCount(Long receiverId);

    /**
     * 标记某用户的所有私信为已读
     *
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     */
    void markAsRead(Long receiverId, Long senderId);

    /**
     * 清除与某用户的所有对话记录
     *
     * @param userId 当前用户ID
     * @param targetUserId 对方用户ID
     */
    void clearConversation(Long userId, Long targetUserId);
}
