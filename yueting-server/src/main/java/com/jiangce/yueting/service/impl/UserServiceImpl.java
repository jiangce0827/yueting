package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.common.UserContent;
import com.jiangce.yueting.domain.dto.UserBasicUpdateDTO;
import com.jiangce.yueting.domain.dto.UserPageDTO;
import com.jiangce.yueting.domain.dto.UserPrivacyUpdateDTO;
import com.jiangce.yueting.domain.dto.UserRegisterByEmailDTO;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.po.Note;
import com.jiangce.yueting.domain.po.Song;
import com.jiangce.yueting.domain.po.SongArtist;
import com.jiangce.yueting.domain.po.User;
import com.jiangce.yueting.domain.po.UserFollow;
import com.jiangce.yueting.domain.po.UserPlayRecord;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.domain.vo.UserLoginVO;
import com.jiangce.yueting.domain.vo.UserPageVO;
import com.jiangce.yueting.domain.vo.UserPlayHistoryVO;
import com.jiangce.yueting.domain.vo.UserPlayRankingVO;
import com.jiangce.yueting.domain.vo.UserSettingVO;
import com.jiangce.yueting.domain.vo.UserFollowVO;
import com.jiangce.yueting.mapper.AlbumMapper;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.mapper.NoteMapper;
import com.jiangce.yueting.mapper.SongArtistMapper;
import com.jiangce.yueting.mapper.SongMapper;
import com.jiangce.yueting.mapper.UserFollowMapper;
import com.jiangce.yueting.mapper.UserMapper;
import com.jiangce.yueting.mapper.UserPlayRecordMapper;
import com.jiangce.yueting.service.UserService;
import com.jiangce.yueting.utils.BeanUtils;
import com.jiangce.yueting.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserPlayRecordMapper userPlayRecordMapper;
    private final UserFollowMapper userFollowMapper;
    private final NoteMapper noteMapper;
    private final SongMapper songMapper;
    private final SongArtistMapper songArtistMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;


    /**
     * 更新用户基本设置
     *
     * @param userBasicUpdateDTO 用户基本设置
     * @return 更新后的用户设置
     */
    @Override
    public UserSettingVO updateUserBasic(UserBasicUpdateDTO userBasicUpdateDTO) {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        BeanUtils.copyProperties(userBasicUpdateDTO, user);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 更新用户头像
     *
     * @param avatarUrl 头像地址
     */
    @Override
    public UserSettingVO updateUserAvatarUrl(String avatarUrl) {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    /**
     * 更新用户隐私设置
     *
     * @param userPrivacyUpdateDTO 用户隐私设置
     * @return 更新后的用户设置
     */
    @Override
    public UserSettingVO updateUserPrivacy(UserPrivacyUpdateDTO userPrivacyUpdateDTO) {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        BeanUtils.copyProperties(userPrivacyUpdateDTO, user);
        userMapper.updateById(user);
        return user.toUserSettingVO();
    }

    @Override
    public UserSettingVO getUserBasic() {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        return user.toUserSettingVO();
    }

    @Override
    public PageResult searchUserByName(String keyword,  Integer pageNum, Integer pageSize) {
        Page<UserBaseVO> page = new Page<>(pageNum, pageSize);
        IPage<UserBaseVO> result = userMapper.selectUserByKeyword(keyword, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public UserSettingVO getUserBasic(Long userId) {
        User user = userMapper.selectById(userId);
        UserSettingVO vo = user.toUserSettingVO();
        // 查询笔记数量
        Long noteCount = noteMapper.selectCount(new LambdaQueryWrapper<Note>().eq(Note::getUserId, userId));
        vo.setNoteCount(noteCount.intValue());
        return vo;
    }

    @Override
    public PageResult pageUser(UserPageDTO userDTO) {
        Page<UserPageVO> page = new Page<>(userDTO.getPageNum(), userDTO.getPageSize());
        IPage<UserPageVO> result = userMapper.pageUserVO(userDTO, page);
        return new PageResult(result.getTotal(), result.getRecords());

    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public List<UserPlayRankingVO> getUserPlayRanking(Long userId, boolean recentWeek) {
        LocalDateTime startTime = recentWeek ? LocalDateTime.now().minusDays(7) : null;
        List<UserPlayRankingVO> ranking = userPlayRecordMapper.selectUserPlayRanking(userId, startTime);
        // 设置排名
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRank(i + 1);
        }
        return ranking;
    }

    @Override
    public void followUser(Long followingId) {
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId.equals(followingId)) {
            throw new CustomException("不能关注自己");
        }
        // 检查是否已关注
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, currentUserId)
               .eq(UserFollow::getFollowingId, followingId);
        Long count = userFollowMapper.selectCount(wrapper);
        if (count > 0) {
            throw new CustomException("已关注该用户");
        }
        // 添加关注记录
        userFollowMapper.insertFollow(currentUserId, followingId);
        // 更新关注数和粉丝数
        userMapper.addFollowingCount(currentUserId);
        userMapper.addFollowerCount(followingId);
    }

    @Override
    public void unfollowUser(Long followingId) {
        Long currentUserId = BaseContext.getCurrentId();
        userFollowMapper.deleteFollow(currentUserId, followingId);
        // 更新关注数和粉丝数
        userMapper.subtractFollowingCount(currentUserId);
        userMapper.subtractFollowerCount(followingId);
    }

    @Override
    public List<UserFollowVO> getFollowingList(Long userId) {
        return userFollowMapper.selectFollowingList(userId);
    }

    @Override
    public List<UserFollowVO> getFollowerList(Long userId) {
        return userFollowMapper.selectFollowerList(userId);
    }

    @Override
    public boolean isFollowing(Long followingId) {
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            return false;
        }
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, currentUserId)
               .eq(UserFollow::getFollowingId, followingId);
        return userFollowMapper.selectCount(wrapper) > 0;
    }

    @Override
    public PageResult getUserPlayHistory(Long userId, Integer pageNum, Integer pageSize) {
        // 分页查询播放记录
        Page<UserPlayRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserPlayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlayRecord::getUserId, userId)
                .orderByDesc(UserPlayRecord::getPlayTime);
        IPage<UserPlayRecord> recordPage = userPlayRecordMapper.selectPage(page, wrapper);

        log.info("播放记录查询结果: total={}, records={}", recordPage.getTotal(), recordPage.getRecords().size());

        // 转换为 VO
        List<UserPlayHistoryVO> voList = recordPage.getRecords().stream()
                .map(record -> {
                    UserPlayHistoryVO vo = new UserPlayHistoryVO();
                    vo.setRecordId(record.getId());
                    vo.setSongId(record.getSongId());
                    vo.setPlayTime(record.getPlayTime());

                    // 查询歌曲信息
                    Song song = songMapper.selectById(record.getSongId());
                    if (song != null) {
                        vo.setSongName(song.getSongName());
                        // 封面从专辑获取
                        if (song.getAlbumId() != null) {
                            Album album = albumMapper.selectById(song.getAlbumId());
                            if (album != null) {
                                vo.setCoverUrl(album.getCoverUrl());
                            }
                        }
                        log.info("歌曲信息: songId={}, songName={}", song.getSongId(), song.getSongName());
                    }

                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());

        // 填充歌手信息
        for (UserPlayHistoryVO vo : voList) {
            if (vo.getSongId() != null) {
                List<String> artistNames = new java.util.ArrayList<>();
                List<String> artistIds = new java.util.ArrayList<>();
                // 查询歌曲-歌手关联
                List<SongArtist> songArtists = songArtistMapper.selectList(
                        new LambdaQueryWrapper<SongArtist>().eq(SongArtist::getSongId, vo.getSongId())
                );
                for (SongArtist sa : songArtists) {
                    Artist artist = artistMapper.selectById(sa.getArtistId());
                    if (artist != null) {
                        artistNames.add(artist.getArtistName());
                        artistIds.add(String.valueOf(artist.getArtistId()));
                    }
                }
                vo.setArtistNames(String.join("/", artistNames));
                vo.setArtistIds(String.join(",", artistIds));
            }
        }

        PageResult result = new PageResult();
        result.setRecords(voList);
        result.setTotal(recordPage.getTotal());
        return result;
    }
}
