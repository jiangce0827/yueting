package com.jiangce.yueting.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserBasicUpdateDTO {
    private String nickname;
    private String signature;
    private Integer gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
