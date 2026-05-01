package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.AIChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AIChatMessageMapper extends BaseMapper<AIChatMessage> {

    @Select("SELECT chat_id FROM ai_chat_message WHERE user_id = #{userId} GROUP BY chat_id ORDER BY MAX(create_time) DESC")
    List<String> selectChatIdsByUserId(@Param("userId") Long userId);
}
