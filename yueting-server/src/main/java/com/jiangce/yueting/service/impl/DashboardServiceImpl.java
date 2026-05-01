package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.domain.po.ArtistApplication;
import com.jiangce.yueting.domain.po.ArtistIdentityApplication;
import com.jiangce.yueting.domain.po.SongApplication;
import com.jiangce.yueting.mapper.ArtistApplicationMapper;
import com.jiangce.yueting.mapper.ArtistIdentityApplicationMapper;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.PlaylistMapper;
import com.jiangce.yueting.mapper.SongApplicationMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制台服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;

    private final ArtistMapper artistMapper;

    private final SongApplicationMapper songApplicationMapper;

    private final ArtistApplicationMapper artistApplicationMapper;

    private final ArtistIdentityApplicationMapper artistIdentityApplicationMapper;

    private final PlaylistMapper playlistMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        long userCount = userMapper.selectCount(null);
        stats.put("userCount", userCount);

        // 艺人总数
        long artistCount = artistMapper.selectCount(null);
        stats.put("artistCount", artistCount);

        // 歌曲总数
        long songCount = songApplicationMapper.selectCount(null);
        stats.put("songCount", songCount);

        // 歌单总数
        long playlistCount = playlistMapper.selectCount(null);
        stats.put("playlistCount", playlistCount);

        // 待审批歌曲数量
        long pendingSongReview = songApplicationMapper.selectCount(
                new LambdaQueryWrapper<SongApplication>().eq(SongApplication::getStatus, 0)
        );
        stats.put("pendingSongReview", pendingSongReview);

        // 待审批艺人数量
        long pendingArtistReview = artistApplicationMapper.selectCount(
                new LambdaQueryWrapper<ArtistApplication>().eq(ArtistApplication::getStatus, 0)
        );
        stats.put("pendingArtistReview", pendingArtistReview);

        // 待审批身份数量
        long pendingIdentityReview = artistIdentityApplicationMapper.selectCount(
                new LambdaQueryWrapper<ArtistIdentityApplication>().eq(ArtistIdentityApplication::getStatus, 0)
        );
        stats.put("pendingIdentityReview", pendingIdentityReview);

        return stats;
    }
}