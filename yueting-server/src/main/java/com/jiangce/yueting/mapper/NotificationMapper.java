package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知 Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 根据类型查询通知列表
     *
     * @param receiverId 接收者ID
     * @param types 通知类型列表
     * @return 通知列表
     */
    default List<Notification> selectByTypes(Long receiverId, List<Integer> types) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .in(Notification::getType, types)
               .ne(Notification::getStatus, 1)
               .orderByDesc(Notification::getCreatedAt);
        return selectList(wrapper);
    }

    /**
     * 获取未读通知数量
     *
     * @param receiverId 接收者ID
     * @param types 通知类型列表
     * @return 未读数量
     */
    default Long selectUnreadCount(Long receiverId, List<Integer> types) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, false)
               .ne(Notification::getStatus, 1);
        if (types != null && !types.isEmpty()) {
            wrapper.in(Notification::getType, types);
        }
        return selectCount(wrapper);
    }

    /**
     * 将所有通知标记为已读
     *
     * @param receiverId 接收者ID
     */
    @Select("UPDATE notification SET is_read = 1 WHERE receiver_id = #{receiverId} AND is_read = 0")
    void markAllAsRead(@Param("receiverId") Long receiverId);

    /**
     * 将指定类型的通知标记为已读
     *
     * @param receiverId 接收者ID
     * @param types 通知类型列表
     */
    default void markAsReadByTypes(Long receiverId, List<Integer> types) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, false)
               .in(Notification::getType, types);

        Notification update = new Notification();
        update.setIsRead(true);
        update(update, wrapper);
    }

    default void markOneAsRead(Long notificationId, Long receiverId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getNotificationId, notificationId)
               .eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, false)
               .ne(Notification::getStatus, 1);

        Notification update = new Notification();
        update.setIsRead(true);
        update(update, wrapper);
    }
}
