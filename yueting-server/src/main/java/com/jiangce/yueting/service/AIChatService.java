package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.po.AIChatMessage;

import java.util.List;

public interface AIChatService {

    void saveMessage(Long userId, String chatId, String role, String content);

    List<AIChatMessage> getHistory(Long userId, String chatId);

    List<String> getChatIds(Long userId);

    void deleteHistory(Long userId, String chatId);
}
