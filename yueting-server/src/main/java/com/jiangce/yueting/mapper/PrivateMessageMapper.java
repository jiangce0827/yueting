package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 私信 Mapper
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

    /**
     * 获取用户的会话列表（每个会话显示最新消息）
     *
     * @param userId 当前用户ID
     * @return 会话列表
     */
    @Select("SELECT pm.* FROM private_message pm " +
            "INNER JOIN (" +
            "    SELECT LEAST(sender_id, receiver_id) as user1, " +
            "           GREATEST(sender_id, receiver_id) as user2, " +
            "           MAX(created_at) as max_time " +
            "    FROM private_message " +
            "    WHERE sender_id = #{userId} OR receiver_id = #{userId} " +
            "    GROUP BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id) " +
            ") latest ON ((LEAST(pm.sender_id, pm.receiver_id) = latest.user1 " +
            "    AND GREATEST(pm.sender_id, pm.receiver_id) = latest.user2 " +
            "    AND pm.created_at = latest.max_time)) " +
            "WHERE (pm.sender_id = #{userId} OR pm.receiver_id = #{userId}) " +
            "    AND (pm.status = 0 OR (pm.sender_id = #{userId} AND pm.status != 1) OR (pm.receiver_id = #{userId} AND pm.status != 2)) " +
            "ORDER BY pm.created_at DESC")
    List<PrivateMessage> selectConversationList(@Param("userId") Long userId);

    /**
     * 获取两个用户之间的聊天记录
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 聊天记录列表
     */
    default List<PrivateMessage> selectChatHistory(Long userId1, Long userId2) {
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(PrivateMessage::getSenderId, userId1).eq(PrivateMessage::getReceiverId, userId2))
               .or(w -> w.eq(PrivateMessage::getSenderId, userId2).eq(PrivateMessage::getReceiverId, userId1))
               .and(w -> w.ne(PrivateMessage::getStatus, 3))
               .orderByAsc(PrivateMessage::getCreatedAt);
        return selectList(wrapper);
    }

    /**
     * 获取未读私信数量
     *
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    default Long selectUnreadCount(Long receiverId) {
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getReceiverId, receiverId)
               .eq(PrivateMessage::getIsRead, false)
               .ne(PrivateMessage::getStatus, 2)
               .ne(PrivateMessage::getStatus, 3);
        return selectCount(wrapper);
    }

    /**
     * 将某用户发来的所有私信标记为已读
     *
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     */
    default void markAsReadBySender(Long receiverId, Long senderId) {
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getReceiverId, receiverId)
               .eq(PrivateMessage::getSenderId, senderId)
               .eq(PrivateMessage::getIsRead, false);

        PrivateMessage update = new PrivateMessage();
        update.setIsRead(true);
        update(update, wrapper);
    }
}
