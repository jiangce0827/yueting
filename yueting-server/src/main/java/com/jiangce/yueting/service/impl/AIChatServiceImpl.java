package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.domain.po.AIChatMessage;
import com.jiangce.yueting.mapper.AIChatMessageMapper;
import com.jiangce.yueting.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final AIChatMessageMapper aiChatMessageMapper;

    @Override
    public void saveMessage(Long userId, String chatId, String role, String content) {
        AIChatMessage message = AIChatMessage.builder()
                .userId(userId)
                .chatId(chatId)
                .role(role)
                .content(content)
                .build();
        aiChatMessageMapper.insert(message);
    }

    @Override
    public List<AIChatMessage> getHistory(Long userId, String chatId) {
        LambdaQueryWrapper<AIChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIChatMessage::getUserId, userId)
                .eq(AIChatMessage::getChatId, chatId)
                .orderByAsc(AIChatMessage::getCreateTime);
        return aiChatMessageMapper.selectList(wrapper);
    }

    @Override
    public List<String> getChatIds(Long userId) {
        return aiChatMessageMapper.selectChatIdsByUserId(userId);
    }

    @Override
    public void deleteHistory(Long userId, String chatId) {
        LambdaQueryWrapper<AIChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIChatMessage::getUserId, userId)
                .eq(AIChatMessage::getChatId, chatId);
        aiChatMessageMapper.delete(wrapper);
    }
}
