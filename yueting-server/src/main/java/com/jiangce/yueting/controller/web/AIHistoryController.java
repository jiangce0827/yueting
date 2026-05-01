package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.po.AIChatMessage;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/ai")
@RequiredArgsConstructor
public class AIHistoryController {

    private final AIChatService aiChatService;
    private final ChatMemory chatMemory;

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public Result<List<AIChatMessage>> getHistory(@RequestParam("chatId") String chatId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(aiChatService.getHistory(userId, chatId));
    }

    @GetMapping("/chats")
    @PreAuthorize("hasRole('USER')")
    public Result<List<String>> getChatIds() {
        Long userId = BaseContext.getCurrentId();
        return Result.success(aiChatService.getChatIds(userId));
    }

    @DeleteMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteHistory(@RequestParam("chatId") String chatId) {
        Long userId = BaseContext.getCurrentId();
        aiChatService.deleteHistory(userId, chatId);
        chatMemory.clear(chatId);
        return Result.success();
    }
}
