package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.config.AIConfig;
import com.jiangce.yueting.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/web/ai")
@RequiredArgsConstructor
public class AIController {

    private final ChatClient qwenChatClient;
    private final ChatMemory chatMemory;
    private final AIChatService aiChatService;

    @PostMapping(value = "/chat", produces = "text/html;charset=utf-8")
    @PreAuthorize("hasRole('USER')")
    public Flux<String> chatStream(
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId) {
        Long userId = BaseContext.getCurrentId();

        aiChatService.saveMessage(userId, chatId, "user", prompt);

        List<Message> history = chatMemory.get(chatId);
        if (history == null || history.isEmpty()) {
            chatMemory.add(chatId, List.of(new SystemMessage(AIConfig.SYSTEM_PROMPT)));
        }

        StringBuilder aiContent = new StringBuilder();
        String enrichedPrompt = """
                当前登录用户的 userId 是 %d。
                注意：如果用户要求推荐歌曲或查看听歌记录，你必须调用对应工具，并在参数中传入 userId=%d。
                用户的问题是：%s
                """.formatted(userId, userId, prompt);

        return qwenChatClient.prompt()
                .user(enrichedPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content()
                .doOnNext(aiContent::append)
                .doOnComplete(() ->
                        aiChatService.saveMessage(userId, chatId, "assistant", aiContent.toString())
                );
    }
}
