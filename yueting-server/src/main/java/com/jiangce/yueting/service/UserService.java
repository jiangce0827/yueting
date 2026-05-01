package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.UserBasicUpdateDTO;
import com.jiangce.yueting.domain.dto.UserPageDTO;
import com.jiangce.yueting.domain.dto.UserPrivacyUpdateDTO;
import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;

import java.util.List;

public interface UserService {


    /**
     * 更新用户基本设置
     * @param userBasicUpdateDTO 用户基本设置
     * @return 更新后的用户设置
     */
    UserSettingVO updateUserBasic(UserBasicUpdateDTO userBasicUpdateDTO);

    /**
     * 更新用户头像
     * @param avatarUrl 头像地址
     */
    UserSettingVO updateUserAvatarUrl(String avatarUrl);

    /**
     * 更新用户隐私设置
     * @param userPrivacyUpdateDTO 用户隐私设置
     * @return 更新后的用户设置
     */
    UserSettingVO updateUserPrivacy(UserPrivacyUpdateDTO userPrivacyUpdateDTO);


    /**
     * 获取用户基本信息
     */
    UserSettingVO getUserBasic();

    PageResult searchUserByName(String keyword, Integer pageNum, Integer pageSize);

    UserSettingVO getUserBasic(Long userId);

    PageResult pageUser(UserPageDTO userDTO);

    void updateUser(User user);

    /**
     * 获取用户播放排行
     * @param userId 用户ID
     * @param recentWeek 是否仅查询最近一周 true-最近一周 false-所有时间
     * @return 播放排行列表
     */
    List<com.jiangce.yueting.domain.vo.UserPlayRankingVO> getUserPlayRanking(Long userId, boolean recentWeek);

    /**
     * 关注用户
     * @param followingId 被关注用户ID
     */
    void followUser(Long followingId);

    /**
     * 取消关注用户
     * @param followingId 被取消关注用户ID
     */
    void unfollowUser(Long followingId);

    /**
     * 获取关注列表
     * @param userId 用户ID
     * @return 关注列表
     */
    List<com.jiangce.yueting.domain.vo.UserFollowVO> getFollowingList(Long userId);

    /**
     * 获取粉丝列表
     * @param userId 用户ID
     * @return 粉丝列表
     */
    List<com.jiangce.yueting.domain.vo.UserFollowVO> getFollowerList(Long userId);

    /**
     * 检查是否已关注
     * @param followingId 被关注用户ID
     * @return 是否已关注
     */
    boolean isFollowing(Long followingId);

    /**
     * 获取用户播放历史
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页播放历史
     */
    PageResult getUserPlayHistory(Long userId, Integer pageNum, Integer pageSize);
}
