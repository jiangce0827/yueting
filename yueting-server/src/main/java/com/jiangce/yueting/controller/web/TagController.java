package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/tag")
@Tag(name = "标签相关接口")
@RequiredArgsConstructor
public class TagController {
    private final TagMapper tagMapper;


    @PreAuthorize("permitAll()")
    @Operation(summary = "获取所有标签")
    @GetMapping("/all")
    public Result<java.util.List<com.jiangce.yueting.domain.po.Tag>> getAllTags() {
        List<com.jiangce.yueting.domain.po.Tag> tags = tagMapper.selectList(null);
        return Result.success(tags);
    }
}
