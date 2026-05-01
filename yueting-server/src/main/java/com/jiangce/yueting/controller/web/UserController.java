package com.jiangce.yueting.controller.web;

import com.jiangce.yueting.domain.dto.UserBasicUpdateDTO;
import com.jiangce.yueting.domain.dto.UserPrivacyUpdateDTO;
import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.result.Result;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserPlayRankingVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;
import com.jiangce.yueting.domain.vo.UserFollowVO;
import com.jiangce.yueting.service.CommonService;
import com.jiangce.yueting.service.UserService;
import com.jiangce.yueting.utils.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/web/user")
@Slf4j
@Tag(name = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取用户基本设置")
    @GetMapping("/me/basic")
    public Result<UserSettingVO> getUserBasic() {
        UserSettingVO userSettingVO = userService.getUserBasic();
        return Result.success(userSettingVO);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取用户基本设置")
    @GetMapping("/basic/{userId}")
    public Result<UserSettingVO> getUserBasic(@PathVariable Long userId) {
        UserSettingVO userSettingVO = userService.getUserBasic(userId);
        return Result.success(userSettingVO);
    }

    /**
     * 更新用户基本设置
     *
     * @param userBasicUpdateDTO 用户基本设置
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "更改用户基本设置")
    @PutMapping("/me/basic")
    public Result<UserSettingVO> updateUserBasic(@RequestBody UserBasicUpdateDTO userBasicUpdateDTO) {
        log.info("更新用户信息，{}", userBasicUpdateDTO);
        UserSettingVO userSettingVO = userService.updateUserBasic(userBasicUpdateDTO);
        return Result.success(userSettingVO);
    }

    /**
     * 更新用户头像
     *
     * @param avatarUrl 头像地址
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "更新用户头像")
    @PutMapping("/me/avatar")
    public Result<UserSettingVO> apiUpdateAvatarUrl(@RequestParam("avatarUrl") String avatarUrl) {
        log.info("更新用户头像{}", avatarUrl);
        UserSettingVO userSettingVO = userService.updateUserAvatarUrl(avatarUrl);
        return Result.success(userSettingVO);
    }

    /**
     * 更新用户隐私设置
     *
     * @param userPrivacyUpdateDTO 用户隐私设置
     * @return 用户设置
     */
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "更新用户隐私设置")
    @PutMapping("/me/privacy")
    public Result<UserSettingVO> updateUserPrivacy(@RequestBody UserPrivacyUpdateDTO userPrivacyUpdateDTO) {
        log.info("更新用户隐私设置，{}", userPrivacyUpdateDTO);
        UserSettingVO userSettingVO = userService.updateUserPrivacy(userPrivacyUpdateDTO);
        return Result.success(userSettingVO);
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "根据关键字搜索用户")
    @GetMapping("/search/keyword")
    public Result<PageResult> searchUserByName(@RequestParam("keyword") String keyword, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        log.info("根据关键字搜索用户，{}", keyword);
        return Result.success(userService.searchUserByName(keyword,pageNum,pageSize));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取用户播放排行")
    @GetMapping("/me/play-ranking")
    public Result<List<UserPlayRankingVO>> getUserPlayRanking(@RequestParam(value = "recentWeek", defaultValue = "true") boolean recentWeek) {
        log.info("获取用户播放排行，recentWeek={}", recentWeek);
        Long userId = com.jiangce.yueting.common.BaseContext.getCurrentId();
        return Result.success(userService.getUserPlayRanking(userId, recentWeek));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "关注用户")
    @PostMapping("/me/follow/{followingId}")
    public Result<String> followUser(@PathVariable Long followingId) {
        log.info("关注用户，followingId={}", followingId);
        userService.followUser(followingId);
        return Result.success("关注成功");
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "取消关注用户")
    @DeleteMapping("/me/follow/{followingId}")
    public Result<String> unfollowUser(@PathVariable Long followingId) {
        log.info("取消关注用户，followingId={}", followingId);
        userService.unfollowUser(followingId);
        return Result.success("取消关注成功");
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取关注列表")
    @GetMapping("/me/following/{userId}")
    public Result<List<UserFollowVO>> getFollowingList(@PathVariable Long userId) {
        log.info("获取关注列表，userId={}", userId);
        return Result.success(userService.getFollowingList(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取粉丝列表")
    @GetMapping("/me/followers/{userId}")
    public Result<List<UserFollowVO>> getFollowerList(@PathVariable Long userId) {
        log.info("获取粉丝列表，userId={}", userId);
        return Result.success(userService.getFollowerList(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "检查是否已关注")
    @GetMapping("/me/is-following/{followingId}")
    public Result<Boolean> isFollowing(@PathVariable Long followingId) {
        return Result.success(userService.isFollowing(followingId));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取用户播放历史")
    @GetMapping("/me/play-history")
    public Result<PageResult> getUserPlayHistory(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize) {
        log.info("获取用户播放历史，pageNum={}, pageSize={}", pageNum, pageSize);
        Long userId = com.jiangce.yueting.common.BaseContext.getCurrentId();
        return Result.success(userService.getUserPlayHistory(userId, pageNum, pageSize));
    }

}
