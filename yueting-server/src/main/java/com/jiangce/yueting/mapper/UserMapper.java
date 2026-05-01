package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.UserPageDTO;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.utils.BeanUtils;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.domain.vo.UserPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据关键词搜索用户
     *
     * @param keyword 关键词（昵称）
     * @param page 分页参数
     * @return 用户分页列表
     */
    IPage<UserBaseVO> selectUserByKeyword(String keyword, Page<UserBaseVO> page);

    /**
     * 分页查询用户列表
     *
     * @param userDTO 查询条件
     * @param page 分页参数
     * @return 用户分页列表
     */
    default IPage<UserPageVO> pageUserVO(UserPageDTO userDTO, Page<UserPageVO> page) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (userDTO.getNickname() != null && !userDTO.getNickname().isEmpty()) {
            wrapper.like(User::getNickname, userDTO.getNickname());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            wrapper.eq(User::getPhone, userDTO.getPhone());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            wrapper.eq(User::getEmail, userDTO.getEmail());
        }
        if (userDTO.getGender() != null && userDTO.getGender() != -1) {
            wrapper.eq(User::getGender, userDTO.getGender());
        }
        if (userDTO.getStatus() != null && userDTO.getStatus() != -1) {
            wrapper.eq(User::getStatus, userDTO.getStatus());
        }
        Page<User> userPage = new Page<>(page.getCurrent(), page.getSize());
        return selectPage(userPage, wrapper).convert(user -> BeanUtils.copyProperties(user, UserPageVO.class));
    }

    /**
     * 增加用户关注数量
     *
     * @param userId 用户ID
     */
    void addFollowingCount(@Param("userId") Long userId);

    /**
     * 减少用户关注数量
     *
     * @param userId 用户ID
     */
    void subtractFollowingCount(@Param("userId") Long userId);

    /**
     * 增加用户粉丝数量
     *
     * @param userId 用户ID
     */
    void addFollowerCount(@Param("userId") Long userId);

    /**
     * 减少用户粉丝数量
     *
     * @param userId 用户ID
     */
    void subtractFollowerCount(@Param("userId") Long userId);
}
