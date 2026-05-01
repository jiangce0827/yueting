package com.jiangce.yueting.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationIdentityPageDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationPageDTO;
import com.jiangce.yueting.domain.dto.ArtistPageDTO;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.ArtistPageByAdminVO;
import com.jiangce.yueting.service.ArtistApplicationService;
import com.jiangce.yueting.service.ArtistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("backendArtistController")
@RequestMapping("/admin/artist")
@Tag(name = "艺人相关接口")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistApplicationService artistApplicationService;
    private final ArtistService artistService;


    /**
     * 分页查询歌手列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pageArtist")
    @Operation(summary = "分页查询艺人列表")
    public Result<PageResult> pageSinger(ArtistPageDTO artistPageDTO) {
        log.info("分页查询歌手列表{}", artistPageDTO.toString());
        PageResult pageResult = artistService.pageSinger(artistPageDTO);
        return Result.success(pageResult);
    }

    /**
     * 同意歌手申请
     *
     * @param id 歌手申请id
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "同意艺人申请")
    @PostMapping("/apply/artist/{id}/approve")
    public Result<String> approveToArtist(@PathVariable Long id) {
        log.info("同意歌手申请，id: {}", id);
        artistApplicationService.approveToArtist(id);

        return Result.success("成功");
    }

    /**
     * 拒绝歌手申请
     *
     * @param id 歌手申请id
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "拒绝艺人申请")
    @PostMapping("/apply/artist/{id}/reject")
    public Result<String> rejectToArtist(@PathVariable Long id, @RequestParam("rejectionReason") String rejectionReason) {
        log.info("拒绝歌手申请，id: {},reason:{}", id, rejectionReason);
        artistApplicationService.rejectToArtist(id, rejectionReason);

        return Result.success("成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "同意艺人具体身份申请")
    @PostMapping("/apply/artistIdentity/{id}/approve")
    public Result<String> approveArtistIdentity(@PathVariable Long id) {
        log.info("同意歌手具体身份申请，id: {}", id);
        artistApplicationService.approveArtistIdentity(id);

        return Result.success("成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "拒绝艺人具体身份申请")
    @PostMapping("/apply/artistIdentity/{id}/reject")
    public Result<String> rejectArtistIdentity(@PathVariable Long id, @RequestParam("rejectionReason") String rejectionReason) {
        log.info("拒绝歌手具体身份申请，id: {}", id);
        artistApplicationService.rejectArtistIdentity(id, rejectionReason);

        return Result.success("成功");
    }

    /**
     * 查询待审核的歌手
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pageArtistApplication")
    @Operation(summary = "分页查询待审核的歌手", description = "pageDTO: 分页信息，singerReviewPageDTO: 歌手信息")
    public Result<PageResult> pageSingerReview(ArtistApplicationPageDTO artistApplicationPageDTO) {
        log.info("分页查询审核列表{}", artistApplicationPageDTO.toString());
        PageResult pageResult = artistApplicationService.pageArtistApplication(artistApplicationPageDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询待审核的歌手
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pageArtistIdentityApplication")
    @Operation(summary = "分页查询待审核的歌手具体身份申请")
    public Result<PageResult> pageSingerReviewIdentity(ArtistApplicationIdentityPageDTO artistApplicationIdentityPageDTO) {
        log.info("分页查询歌手身份审核列表{}", artistApplicationIdentityPageDTO.toString());
        PageResult pageResult = artistApplicationService.pageArtistApplicationIdentity(artistApplicationIdentityPageDTO);
        return Result.success(pageResult);
    }
}
