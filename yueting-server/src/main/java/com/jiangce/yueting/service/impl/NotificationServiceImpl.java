package com.jiangce.yueting.service.impl;

import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.po.Comment;
import com.jiangce.yueting.domain.po.Notification;
import com.jiangce.yueting.domain.po.Playlist;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.vo.NotificationVO;
import com.jiangce.yueting.mapper.CommentMapper;
import com.jiangce.yueting.mapper.NotificationMapper;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    private final UserMapper userMapper;

    private final SongMapper songMapper;

    private final PlaylistMapper playlistMapper;

    private final CommentMapper commentMapper;

    @Override
    public List<NotificationVO> getNotificationList(Long receiverId, String typeStr) {
        // 解析类型
        List<Integer> types = parseTypes(typeStr);

        List<Notification> notifications = notificationMapper.selectByTypes(receiverId, types);

        return notifications.stream().map(notification -> {
            NotificationVO vo = new NotificationVO();
            vo.setNotificationId(notification.getNotificationId());
            vo.setReceiverId(notification.getReceiverId());
            vo.setSenderId(notification.getSenderId());
            vo.setType(notification.getType());
            vo.setContent(notification.getContent());
            vo.setTargetId(notification.getTargetId());
            vo.setTargetType(notification.getTargetType());
            vo.setTargetSubId(notification.getTargetSubId());
            vo.setIsRead(notification.getIsRead());
            vo.setCreatedAt(notification.getCreatedAt());

            // 获取发送者信息
            if (notification.getSenderId() != null) {
                User user = userMapper.selectById(notification.getSenderId());
                if (user != null) {
                    vo.setSenderName(user.getNickname());
                    vo.setSenderAvatar(user.getAvatarUrl());
                }
            }

            // 获取被评论目标的名称
            vo.setTargetName(getTargetName(notification));

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new CustomException("通知不存在");
        }

        if (!notification.getReceiverId().equals(userId)) {
            throw new CustomException("无权限删除此通知");
        }

        Notification update = new Notification();
        update.setNotificationId(notificationId);
        update.setStatus(1);
        notificationMapper.updateById(update);
        log.info("删除通知成功: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    public Long getUnreadCount(Long receiverId, String typeStr) {
        List<Integer> types = parseTypes(typeStr);
        return notificationMapper.selectUnreadCount(receiverId, types);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long receiverId) {
        notificationMapper.markAllAsRead(receiverId);
        log.info("标记所有通知已读: receiverId={}", receiverId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long receiverId, String typeStr) {
        List<Integer> types = parseTypes(typeStr);
        if (types == null || types.isEmpty()) {
            notificationMapper.markAllAsRead(receiverId);
        } else {
            notificationMapper.markAsReadByTypes(receiverId, types);
        }
        log.info("鎸夌被鍨嬫爣璁伴€氱煡宸茶: receiverId={}, types={}", receiverId, typeStr);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || notification.getStatus() == 1) {
            throw new CustomException("閫氱煡涓嶅瓨鍦?");
        }

        if (!notification.getReceiverId().equals(userId)) {
            throw new CustomException("鏃犳潈闄愭爣璁拌閫氱煡");
        }

        notificationMapper.markOneAsRead(notificationId, userId);
        log.info("鏍囪鍗曟潯閫氱煡宸茶: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    @Transactional
    public void createNotification(Long receiverId, Long senderId, Integer type, String content,
                                   Long targetId, Integer targetType, Long targetSubId) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setContent(content);
        notification.setTargetId(targetId);
        notification.setTargetType(targetType);
        notification.setTargetSubId(targetSubId);
        notification.setIsRead(false);
        notification.setStatus(0);
        notification.setCreatedAt(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("创建通知成功: receiverId={}, senderId={}, type={}", receiverId, senderId, type);
    }

    /**
     * 获取被评论目标的名称
     *
     * @param notification 通知
     * @return 目标名称
     */
    private String getTargetName(Notification notification) {
        // 评论通知和点赞通知：targetSubId 是歌曲/歌单/专辑ID
        if ((notification.getType() == 1 || notification.getType() == 3) && notification.getTargetSubId() != null) {
            // 需要根据目标类型判断，但目前没有直接存储目标类型
            // 尝试查询歌曲
            Song song = songMapper.selectById(notification.getTargetSubId());
            if (song != null) {
                return song.getSongName();
            }
            // 尝试查询歌单
            Playlist playlist = playlistMapper.selectById(notification.getTargetSubId());
            if (playlist != null) {
                return playlist.getPlaylistName();
            }
        }
        return null;
    }

    /**
     * 解析类型字符串
     *
     * @param typeStr 类型字符串，逗号分隔
     * @return 类型列表
     */
    private List<Integer> parseTypes(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return null;
        }
        return Arrays.stream(typeStr.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
