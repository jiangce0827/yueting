package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.config.AIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/web/ai")
@RequiredArgsConstructor
public class AITestController {

    private final ChatClient qwenChatClient;
    private final ChatMemory chatMemory;

    @GetMapping("/chat-test")
    @PreAuthorize("permitAll()")
    public String chatTest(@RequestParam("prompt") String prompt,
                           @RequestParam("chatId") String chatId) {
        log.info("收到 AI 测试请求，prompt={}, chatId={}", prompt, chatId);
        List<Message> history = chatMemory.get(chatId);
        if (history == null || history.isEmpty()) {
            chatMemory.add(chatId, List.of(new SystemMessage(AIConfig.SYSTEM_PROMPT)));
        }
        String response = qwenChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
        log.info("Qwen 返回: {}", response);
        return response == null ? "（空响应）" : response;
    }
}
