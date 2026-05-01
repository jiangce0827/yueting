package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangce.yueting.domain.po.UserFollow;
import com.jiangce.yueting.domain.vo.UserFollowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注 Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 插入关注记录
     *
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     */
    void insertFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 删除关注记录
     *
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     */
    void deleteFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 查询用户关注数量
     *
     * @param userId 用户ID
     * @return 关注数量
     */
    default int countFollowing(@Param("userId") Long userId) {
        return selectCount(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFollowerId, userId)).intValue();
    }

    /**
     * 查询用户粉丝数量
     *
     * @param userId 用户ID
     * @return 粉丝数量
     */
    default int countFollowers(@Param("userId") Long userId) {
        return selectCount(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFollowingId, userId)).intValue();
    }

    /**
     * 查询用户关注列表
     *
     * @param userId 用户ID
     * @return 关注列表
     */
    List<UserFollowVO> selectFollowingList(@Param("userId") Long userId);

    /**
     * 查询用户粉丝列表
     *
     * @param userId 用户ID
     * @return 粉丝列表
     */
    List<UserFollowVO> selectFollowerList(@Param("userId") Long userId);
}
