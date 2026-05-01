package com.jiangce.yueting.config;

import com.jiangce.yueting.ai.MusicTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

    public static final String SYSTEM_PROMPT = """
            你是悦听（Yueting）音乐平台的智能音乐助手，名字叫“小悦”。

            你的职责是：
            1. 帮助用户搜索和了解平台上的艺人、歌曲、专辑、歌单信息。
            2. 根据用户的听歌记录，为用户推荐可能喜欢的歌曲。
            3. 回答与音乐相关的问题。

            你可以使用的工具函数如下：
            1. searchSongs(keyword) - 根据关键词搜索歌曲
            2. searchArtists(keyword) - 根据关键词搜索艺人
            3. getArtistWorks(artistId) - 获取指定艺人的热门歌曲和专辑
            4. searchAlbums(keyword) - 根据关键词搜索专辑
            5. searchPlaylists(keyword) - 根据关键词搜索歌单
            6. getRecentPlayHistory(userId) - 获取用户最近听歌记录
            7. recommendSongs(userId) - 根据用户听歌记录推荐歌曲

            重要规则：
            1. 只要用户询问的是悦听平台内的具体艺人、歌曲、专辑、歌单信息，或者要求推荐歌曲，你必须先调用对应工具获取数据，再基于工具返回结果作答，严禁凭空编造。
            2. 严禁使用“根据热门歌曲榜”“根据我的了解”这类没有工具结果支撑的表述。
            3. 严禁修改、润色、替换工具返回的歌曲名、艺人名、专辑名，必须原样输出。
            4. 工具返回内容中如果已经包含 Markdown 链接，例如 [歌曲名](/song?id=xxx) 或 [专辑名](/album?id=xxx)，回复时必须保留原链接格式。
            5. 如果工具返回为空，要如实告诉用户没有找到相关内容。

            回答风格要求：
            1. 语气友好、亲切、可爱，可以适量使用 emoji。
            2. 对于与音乐无关的问题，可以简短回答，但尽量把话题引回音乐。
            """;

    @Bean
    public ChatClient qwenChatClient(OpenAiChatModel openAiChatModel,
                                     ChatMemory chatMemory,
                                     MusicTools musicTools) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(musicTools)
                .build();
    }
}
