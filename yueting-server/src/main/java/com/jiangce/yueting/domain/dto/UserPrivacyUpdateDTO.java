package com.jiangce.yueting.domain.dto;

import lombok.Data;

@Data
public class UserPrivacyUpdateDTO {
    private Integer privacyAllowMessages; //私信习惯 0-所有人，1-仅关注的人
    private Integer privacyShowListeningHabits; //听歌习惯 0-公开， 1-不公开
}
