package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.PrivateMessageSendDTO;
import com.jiangce.yueting.domain.po.PrivateMessage;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.vo.PrivateMessageVO;
import com.jiangce.yueting.mapper.PrivateMessageMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.service.PrivateMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私信服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageMapper privateMessageMapper;

    private final UserMapper userMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<PrivateMessageVO> getConversationList(Long userId) {
        // 获取用户的所有会话（每个会话最新一条消息）
        List<PrivateMessage> messages = privateMessageMapper.selectConversationList(userId);

        return messages.stream().map(msg -> {
            PrivateMessageVO vo = new PrivateMessageVO();
            vo.setMessageId(msg.getMessageId());
            vo.setSenderId(msg.getSenderId());
            vo.setReceiverId(msg.getReceiverId());
            vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead());
            vo.setCreatedAt(msg.getCreatedAt());

            // 确定对方用户ID
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            vo.setOtherUserId(otherUserId); // 设置对方用户ID，用于前端查询聊天记录

            // 获取对方用户信息
            User user = userMapper.selectById(otherUserId);
            if (user != null) {
                vo.setSenderName(user.getNickname());
                vo.setSenderAvatar(user.getAvatarUrl());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PrivateMessageVO> getChatHistory(Long userId, Long targetUserId) {
        // 获取两个用户之间的所有聊天记录
        List<PrivateMessage> messages = privateMessageMapper.selectChatHistory(userId, targetUserId);

        return messages.stream().map(msg -> {
            PrivateMessageVO vo = new PrivateMessageVO();
            vo.setMessageId(msg.getMessageId());
            vo.setSenderId(msg.getSenderId());
            vo.setReceiverId(msg.getReceiverId());
            vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead());
            vo.setCreatedAt(msg.getCreatedAt());

            // 获取发送者信息
            User user = userMapper.selectById(msg.getSenderId());
            if (user != null) {
                vo.setSenderName(user.getNickname());
                vo.setSenderAvatar(user.getAvatarUrl());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendMessage(Long senderId, PrivateMessageSendDTO sendDTO) {
        Long receiverId = null;
        String receiverIdStr = sendDTO.getReceiverId();
        String receiverName = sendDTO.getReceiverName();

        // 如果提供了receiverId（String类型），转换为Long
        if (receiverIdStr != null && !receiverIdStr.isEmpty()) {
            try {
                receiverId = new java.math.BigInteger(receiverIdStr).longValue();
            } catch (NumberFormatException e) {
                throw new CustomException("接收者ID格式错误");
            }
        }

        // 如果提供了receiverName，通过昵称查找用户ID
        if (receiverId == null && receiverName != null && !receiverName.isEmpty()) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getNickname, receiverName);
            User user = userMapper.selectOne(wrapper);
            if (user == null) {
                throw new CustomException("用户不存在");
            }
            receiverId = user.getUserId();
        }

        if (receiverId == null) {
            throw new CustomException("请选择收件人");
        }

        if (receiverId.equals(senderId)) {
            throw new CustomException("不能给自己发送私信");
        }

        if (sendDTO.getContent() == null || sendDTO.getContent().trim().isEmpty()) {
            throw new CustomException("私信内容不能为空");
        }

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(sendDTO.getContent().trim());
        message.setIsRead(false);
        message.setStatus(0);
        message.setCreatedAt(LocalDateTime.now());

        privateMessageMapper.insert(message);
        log.info("发送私信成功: senderId={}, receiverId={}", senderId, receiverId);

        // 构建消息 VO 用于推送
        PrivateMessageVO messageVO = new PrivateMessageVO();
        messageVO.setMessageId(message.getMessageId());
        messageVO.setSenderId(senderId);
        messageVO.setReceiverId(receiverId);
        messageVO.setContent(message.getContent());
        messageVO.setIsRead(false);
        messageVO.setCreatedAt(message.getCreatedAt());

        // 获取发送者信息
        User sender = userMapper.selectById(senderId);
        if (sender != null) {
            messageVO.setSenderName(sender.getNickname());
            messageVO.setSenderAvatar(sender.getAvatarUrl());
        }
        messageVO.setOtherUserId(senderId); // 对方看到这条消息时，对方ID是发送者ID

        // 通过 WebSocket 推送给接收者（使用广播topic方式）
        messagingTemplate.convertAndSend(
                "/topic/private-message." + receiverId,
                messageVO
        );
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        PrivateMessage message = privateMessageMapper.selectById(messageId);
        if (message == null) {
            throw new CustomException("消息不存在");
        }

        // 根据用户角色更新状态
        Integer newStatus;
        if (message.getSenderId().equals(userId)) {
            // 发送者删除
            newStatus = message.getStatus() == 2 ? 3 : 1;
        } else if (message.getReceiverId().equals(userId)) {
            // 接收者删除
            newStatus = message.getStatus() == 1 ? 3 : 2;
        } else {
            throw new CustomException("无权限删除此消息");
        }

        PrivateMessage update = new PrivateMessage();
        update.setMessageId(messageId);
        update.setStatus(newStatus);
        privateMessageMapper.updateById(update);
        log.info("删除私信成功: messageId={}, userId={}", messageId, userId);
    }

    @Override
    public Long getUnreadCount(Long receiverId) {
        return privateMessageMapper.selectUnreadCount(receiverId);
    }

    @Override
    @Transactional
    public void markAsRead(Long receiverId, Long senderId) {
        privateMessageMapper.markAsReadBySender(receiverId, senderId);
        log.info("标记私信已读: receiverId={}, senderId={}", receiverId, senderId);
    }

    @Override
    @Transactional
    public void clearConversation(Long userId, Long targetUserId) {
        // 获取两个用户之间的所有消息
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(PrivateMessage::getSenderId, userId).eq(PrivateMessage::getReceiverId, targetUserId))
               .or(w -> w.eq(PrivateMessage::getSenderId, targetUserId).eq(PrivateMessage::getReceiverId, userId));

        List<PrivateMessage> messages = privateMessageMapper.selectList(wrapper);

        for (PrivateMessage message : messages) {
            Integer newStatus;
            if (message.getSenderId().equals(userId)) {
                // 当前用户是发送者
                newStatus = message.getStatus() == 2 ? 3 : 1;
            } else {
                // 当前用户是接收者
                newStatus = message.getStatus() == 1 ? 3 : 2;
            }

            PrivateMessage update = new PrivateMessage();
            update.setMessageId(message.getMessageId());
            update.setStatus(newStatus);
            privateMessageMapper.updateById(update);
        }

        log.info("清除对话成功: userId={}, targetUserId={}, count={}", userId, targetUserId, messages.size());
    }
}
