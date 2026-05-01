package com.jiangce.yueting.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumWithAuthor {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long albumId;
    private String albumName;
    private String artistIds; // 作者id
    private String artistNames; // 作者名称
    private String coverUrl;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
}
