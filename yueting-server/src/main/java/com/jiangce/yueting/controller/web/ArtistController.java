package com.jiangce.yueting.controller.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.ArtistApplicationDTO;
import com.jiangce.yueting.domain.dto.ArtistIdentityApplicationDTO;
import com.jiangce.yueting.domain.dto.ArtistSearchDTO;
import com.jiangce.yueting.domain.dto.ArtistUpdateDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.ArtistBaseVO;
import com.jiangce.yueting.domain.vo.ArtistInfoVO;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.service.ArtistApplicationService;
import com.jiangce.yueting.service.ArtistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("webArtistController")
@RequestMapping("/web/artist")
@Tag(name = "艺人相关接口")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistApplicationService artistApplicationService;
    private final ArtistMapper artistMapper;
    private final ArtistService artistService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "用户申请成为艺人")
    @PostMapping("/me/apply/artist")
    public Result<String> applyToArtist(@RequestBody ArtistApplicationDTO artistApplicationDTO) {
        log.info("{} 申请成为 artist，{}", BaseContext.getCurrentId(), artistApplicationDTO);
        artistApplicationService.applyToArtist(artistApplicationDTO);

        return Result.success("审核中");
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "用户申请艺人具体身份")
    @PostMapping("/me/apply/artistIdentity")
    public Result<String> applyArtistIdentity(@RequestBody ArtistIdentityApplicationDTO artistIdentityApplicationDTO) {
        log.info("{} 申请歌手身份：{}", BaseContext.getCurrentId(),artistIdentityApplicationDTO);
        artistApplicationService.applyArtistIdentity(artistIdentityApplicationDTO);
        return Result.success("审核中");
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "获取我的艺人基本信息")
    @GetMapping("/me/info")
    public Result<ArtistWithIdentitiesVO> getMyArtistInfo() {
        log.info("获取我的艺人基本信息{}", BaseContext.getCurrentId());
        ArtistWithIdentitiesVO artistWithIdentitiesVO = artistMapper.selectArtistWithIdentitiesOneByUserId(BaseContext.getCurrentId());
        return Result.success(artistWithIdentitiesVO);
    }

    @PreAuthorize("hasRole('ARTIST')")
    @Operation(summary = "修改艺人信息")
    @PutMapping("/me/info")
    public Result<String> updateMyArtistInfo(@RequestBody ArtistUpdateDTO artistUpdateDTO) {
        log.info("修改艺人信息，{}", artistUpdateDTO);
        artistService.updateMyArtistInfo(artistUpdateDTO);
        return Result.success("修改成功");
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "根据id获取艺人信息")
    @GetMapping("/{id}/basic")
    public Result<ArtistBaseVO> getArtistInfoById(@PathVariable Long id) {
        log.info("根据艺人id获取艺人信息，{}", id);
        Artist artist = artistMapper.selectById(id);
        return Result.success(artist.toArtistBaseVO());
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "根据用户id获取艺人信息")
    @GetMapping("/user/{userId}/basic")
    public Result<ArtistBaseVO> getArtistInfoByUserId(@PathVariable Long userId) {
        log.info("根据用户id获取艺人信息，{}", userId);
        Artist artist = artistMapper.selectArtistByUserId(userId);
        if (artist == null) {
            return Result.success(null);
        }
        return Result.success(artist.toArtistBaseVO());
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "搜索艺人信息")
    @GetMapping("/search")
    public Result<PageResult> searchArtist(ArtistSearchDTO artistSearchDTO) {
        log.info("搜索艺人信息，{}", artistSearchDTO);
        PageResult pageResult = artistService.searchArtistBasic(artistSearchDTO);
        return Result.success(pageResult);
    }
}
