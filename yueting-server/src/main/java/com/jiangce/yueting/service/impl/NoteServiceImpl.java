package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.NoteCommentDTO;
import com.jiangce.yueting.domain.dto.NoteCreateDTO;
import com.jiangce.yueting.domain.po.*;
import com.jiangce.yueting.domain.vo.NoteCommentVO;
import com.jiangce.yueting.domain.vo.NoteVO;
import com.jiangce.yueting.domain.vo.SongWithDetail;
import com.jiangce.yueting.domain.vo.UserBaseVO;
import com.jiangce.yueting.mapper.*;
import com.jiangce.yueting.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 笔记服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    private final NoteLikeMapper noteLikeMapper;

    private final NoteCommentMapper noteCommentMapper;

    private final NoteForwardMapper noteForwardMapper;

    private final UserMapper userMapper;

    private final UserFollowMapper userFollowMapper;

    private final SongMapper songMapper;

    private final ArtistMapper artistMapper;

    private final AlbumMapper albumMapper;

    private final PlaylistMapper playlistMapper;

    private final AlbumArtistMapper albumArtistMapper;

    private final NoteImageMapper noteImageMapper;

    @Override
    @Transactional
    public void createNote(Long userId, NoteCreateDTO createDTO) {
        Note note = new Note();
        note.setUserId(userId);
        note.setContent(createDTO.getContent());
        note.setMusicId(createDTO.getMusicId());
        note.setMusicType(createDTO.getMusicType());
        note.setCoverUrl(createDTO.getCoverUrl());
        note.setForwardSourceId(createDTO.getForwardSourceId());
        note.setLikeCount(0);
        note.setCommentCount(0);
        note.setForwardCount(0);
        note.setStatus(0);
        note.setCreatedAt(LocalDateTime.now());
        noteMapper.insert(note);

        // 保存图片
        if (createDTO.getImageUrls() != null && !createDTO.getImageUrls().isEmpty()) {
            for (int i = 0; i < createDTO.getImageUrls().size(); i++) {
                NoteImage noteImage = new NoteImage();
                noteImage.setNoteId(note.getId());
                noteImage.setImageUrl(createDTO.getImageUrls().get(i));
                noteImage.setSortOrder(i);
                noteImage.setCreatedAt(LocalDateTime.now());
                noteImageMapper.insert(noteImage);
            }
        }

        // 如果是转发生成的笔记，更新原笔记的转发数
        if (createDTO.getForwardSourceId() != null) {
            note.setIsForwarded(true);
            noteMapper.updateById(note);
            Note sourceNote = noteMapper.selectById(createDTO.getForwardSourceId());
            if (sourceNote != null) {
                Note updateNote = new Note();
                updateNote.setId(sourceNote.getId());
                updateNote.setForwardCount(sourceNote.getForwardCount() + 1);
                noteMapper.updateById(updateNote);
                log.info("转发笔记成功: userId={}, sourceNoteId={}, newNoteId={}", userId, createDTO.getForwardSourceId(), note.getId());
            }
        } else {
            log.info("发布笔记成功: userId={}, noteId={}", userId, note.getId());
        }
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new CustomException("无权限删除此笔记");
        }
        noteMapper.deleteById(noteId);
        log.info("删除笔记成功: noteId={}", noteId);
    }

    @Override
    public List<NoteVO> getFollowingNotes(Long userId) {
        // 获取用户关注列表
        List<UserFollow> followingList = userFollowMapper.selectList(
                new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFollowerId, userId)
        );

        // 获取关注用户的ID列表（加上自己）
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId); // 包含自己
        if (!followingList.isEmpty()) {
            List<Long> followingIds = followingList.stream()
                    .map(UserFollow::getFollowingId)
                    .collect(Collectors.toList());
            userIds.addAll(followingIds);
        }

        // 获取这些用户发布的笔记
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Note::getUserId, userIds)
                .orderByDesc(Note::getCreatedAt);
        List<Note> notes = noteMapper.selectList(wrapper);

        // 转换为VO
        return notes.stream().map(note -> convertToVO(note, userId)).collect(Collectors.toList());
    }

    @Override
    public List<NoteVO> getMyNotes(Long userId) {
        // 获取当前用户发布的笔记
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId)
                .orderByDesc(Note::getCreatedAt);
        List<Note> notes = noteMapper.selectList(wrapper);

        // 转换为VO
        return notes.stream().map(note -> convertToVO(note, userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        if (noteLikeMapper.existsByNoteIdAndUserId(noteId, userId)) {
            throw new CustomException("已点赞过此笔记");
        }
        NoteLike like = new NoteLike();
        like.setNoteId(noteId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        noteLikeMapper.insert(like);

        // 更新点赞数
        Note updateNote = new Note();
        updateNote.setId(noteId);
        updateNote.setLikeCount(note.getLikeCount() + 1);
        noteMapper.updateById(updateNote);
        log.info("点赞笔记成功: noteId={}, userId={}", noteId, userId);
    }

    @Override
    @Transactional
    public void unlikeNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        if (!noteLikeMapper.existsByNoteIdAndUserId(noteId, userId)) {
            throw new CustomException("未点赞过此笔记");
        }
        noteLikeMapper.deleteByNoteIdAndUserId(noteId, userId);

        // 更新点赞数
        Note updateNote3 = new Note();
        updateNote3.setId(noteId);
        updateNote3.setLikeCount(Math.max(0, note.getLikeCount() - 1));
        noteMapper.updateById(updateNote3);
        log.info("取消点赞笔记成功: noteId={}, userId={}", noteId, userId);
    }

    @Override
    @Transactional
    public void commentNote(Long noteId, Long userId, NoteCommentDTO commentDTO) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        NoteComment comment = new NoteComment();
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setParentId(commentDTO.getParentId());
        comment.setReplyToUserId(commentDTO.getReplyToUserId());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        noteCommentMapper.insert(comment);

        // 更新评论数
        Note updateNote2 = new Note();
        updateNote2.setId(noteId);
        updateNote2.setCommentCount(note.getCommentCount() + 1);
        noteMapper.updateById(updateNote2);
        log.info("评论笔记成功: noteId={}, userId={}", noteId, userId);
    }

    @Override
    @Transactional
    public void deleteComment(Long noteId, Long commentId, Long userId) {
        NoteComment comment = noteCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new CustomException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException("无权限删除此评论");
        }
        // 软删除：更新状态
        NoteComment updateComment = new NoteComment();
        updateComment.setId(commentId);
        updateComment.setStatus(1);
        noteCommentMapper.updateById(updateComment);

        // 更新评论数
        Note note = noteMapper.selectById(noteId);
        if (note != null) {
            Note updateNote = new Note();
            updateNote.setId(noteId);
            updateNote.setCommentCount(Math.max(0, note.getCommentCount() - 1));
            noteMapper.updateById(updateNote);
        }
        log.info("删除评论成功: noteId={}, commentId={}", noteId, commentId);
    }

    @Override
    @Transactional
    public void forwardNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        if (noteForwardMapper.existsByNoteIdAndUserId(noteId, userId)) {
            throw new CustomException("已转发过此笔记");
        }
        NoteForward forward = new NoteForward();
        forward.setNoteId(noteId);
        forward.setUserId(userId);
        forward.setCreatedAt(LocalDateTime.now());
        noteForwardMapper.insert(forward);

        // 更新转发数
        Note updateNote4 = new Note();
        updateNote4.setId(noteId);
        updateNote4.setForwardCount(note.getForwardCount() + 1);
        noteMapper.updateById(updateNote4);
        log.info("转发笔记成功: noteId={}, userId={}", noteId, userId);
    }

    @Override
    public NoteVO getNoteDetail(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new CustomException("笔记不存在");
        }
        return convertToVO(note, userId);
    }

    @Override
    public List<UserBaseVO> getPopularUsers(Long userId, int limit) {
        // 获取粉丝数最多的用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getFollowerCount)
                .ne(userId != null, User::getUserId, userId)
                .last("LIMIT " + limit);
        List<User> users = userMapper.selectList(wrapper);

        return users.stream().map(this::convertToUserBaseVO).collect(Collectors.toList());
    }

    private NoteVO convertToVO(Note note, Long currentUserId) {
        NoteVO vo = new NoteVO();
        vo.setNoteId(note.getId());
        vo.setUserId(note.getUserId());
        vo.setContent(note.getContent());
        vo.setMusicId(note.getMusicId());
        vo.setMusicType(note.getMusicType());
        vo.setCoverUrl(note.getCoverUrl());
        vo.setLikeCount(note.getLikeCount());
        vo.setCommentCount(note.getCommentCount());
        vo.setForwardCount(note.getForwardCount());
        vo.setCreatedAt(note.getCreatedAt());

        // 获取发布用户信息
        User user = userMapper.selectById(note.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatarUrl(user.getAvatarUrl());
        }

        // 获取笔记图片列表
        List<NoteImage> noteImages = noteImageMapper.selectList(
                new LambdaQueryWrapper<NoteImage>()
                        .eq(NoteImage::getNoteId, note.getId())
                        .orderByAsc(NoteImage::getSortOrder)
        );
        if (noteImages != null && !noteImages.isEmpty()) {
            vo.setImageUrls(noteImages.stream()
                    .map(NoteImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        // 如果是转发生成的笔记，获取源笔记详情
        if (Boolean.TRUE.equals(note.getIsForwarded())) {
            vo.setIsForwarded(true);
            // 如果 forwardSourceId 不为空，尝试获取源笔记详情
            if (note.getForwardSourceId() != null) {
                vo.setForwardSourceId(note.getForwardSourceId());
                Note sourceNote = noteMapper.selectById(note.getForwardSourceId());
                if (sourceNote != null) {
                    NoteVO sourceNoteVO = new NoteVO();
                    sourceNoteVO.setNoteId(sourceNote.getId());
                    sourceNoteVO.setUserId(sourceNote.getUserId());
                    sourceNoteVO.setContent(sourceNote.getContent());
                    sourceNoteVO.setMusicId(sourceNote.getMusicId());
                    sourceNoteVO.setMusicType(sourceNote.getMusicType());
                    sourceNoteVO.setCoverUrl(sourceNote.getCoverUrl());
                    sourceNoteVO.setLikeCount(sourceNote.getLikeCount());
                    sourceNoteVO.setCommentCount(sourceNote.getCommentCount());
                    sourceNoteVO.setForwardCount(sourceNote.getForwardCount());
                    // 获取源笔记发布者信息
                    User sourceUser = userMapper.selectById(sourceNote.getUserId());
                    if (sourceUser != null) {
                        sourceNoteVO.setNickname(sourceUser.getNickname());
                        sourceNoteVO.setAvatarUrl(sourceUser.getAvatarUrl());
                    }
                    // 获取源笔记图片列表
                    List<NoteImage> sourceNoteImages = noteImageMapper.selectList(
                            new LambdaQueryWrapper<NoteImage>()
                                    .eq(NoteImage::getNoteId, sourceNote.getId())
                                    .orderByAsc(NoteImage::getSortOrder)
                    );
                    if (sourceNoteImages != null && !sourceNoteImages.isEmpty()) {
                        sourceNoteVO.setImageUrls(sourceNoteImages.stream()
                                .map(NoteImage::getImageUrl)
                                .collect(Collectors.toList()));
                    }
                    // 获取源笔记配乐详细信息
                    if (sourceNote.getMusicId() != null && sourceNote.getMusicType() != null) {
                        sourceNoteVO.setMusicName(getMusicName(sourceNote.getMusicId(), sourceNote.getMusicType()));
                        // 单曲
                        if (sourceNote.getMusicType() == 1) {
                            SongWithDetail song = songMapper.selectWithDetailBySongId(sourceNote.getMusicId());
                            if (song != null) {
                                sourceNoteVO.setSongCoverUrl(song.getCoverUrl());
                                sourceNoteVO.setSongName(song.getSongName());
                                sourceNoteVO.setAlbumId(song.getAlbumId());
                                sourceNoteVO.setAlbumName(song.getAlbumName());
                                if (song.getArtistIds() != null && !song.getArtistIds().isEmpty()) {
                                    String[] artistIdArr = song.getArtistIds().split(",");
                                    if (artistIdArr.length > 0) {
                                        sourceNoteVO.setArtistId(Long.parseLong(artistIdArr[0].trim()));
                                    }
                                    if (song.getArtistNames() != null && !song.getArtistNames().isEmpty()) {
                                        String[] artistNameArr = song.getArtistNames().split(",");
                                        if (artistNameArr.length > 0) {
                                            sourceNoteVO.setArtistName(artistNameArr[0].trim());
                                        }
                                    }
                                }
                            }
                        }
                        // 歌手
                        if (sourceNote.getMusicType() == 2) {
                            Artist artist = artistMapper.selectById(sourceNote.getMusicId());
                            if (artist != null) {
                                sourceNoteVO.setArtistId(artist.getArtistId());
                                sourceNoteVO.setArtistName(artist.getArtistName());
                                sourceNoteVO.setArtistAvatarUrl(artist.getArtistAvatarUrl());
                                sourceNoteVO.setArtistFollowerCount(artist.getHot() != null ? artist.getHot().intValue() : 0);
                            }
                        }
                        // 专辑
                        if (sourceNote.getMusicType() == 3) {
                            Album album = albumMapper.selectById(sourceNote.getMusicId());
                            if (album != null) {
                                sourceNoteVO.setAlbumId(album.getAlbumId());
                                sourceNoteVO.setAlbumName(album.getAlbumName());
                                sourceNoteVO.setAlbumCoverUrl(album.getCoverUrl());
                                AlbumArtist albumArtist = albumArtistMapper.selectOne(
                                        new LambdaQueryWrapper<AlbumArtist>().eq(AlbumArtist::getAlbumId, album.getAlbumId())
                                );
                                if (albumArtist != null) {
                                    Artist artist = artistMapper.selectById(albumArtist.getArtistId());
                                    if (artist != null) {
                                        sourceNoteVO.setAlbumAuthorName(artist.getArtistName());
                                    }
                                }
                            }
                        }
                        // 歌单
                        if (sourceNote.getMusicType() == 4) {
                            Playlist playlist = playlistMapper.selectById(sourceNote.getMusicId());
                            if (playlist != null) {
                                sourceNoteVO.setPlaylistId(playlist.getPlaylistId());
                                sourceNoteVO.setPlaylistName(playlist.getPlaylistName());
                                sourceNoteVO.setPlaylistCoverUrl(playlist.getCoverUrl());
                                if (playlist.getUserId() != null) {
                                    User playlistCreator = userMapper.selectById(playlist.getUserId());
                                    if (playlistCreator != null) {
                                        sourceNoteVO.setPlaylistCreatorName(playlistCreator.getNickname());
                                    }
                                }
                            }
                        }
                    }
                    vo.setSourceNote(sourceNoteVO);
                } else {
                    // 源笔记已被删除
                    vo.setSourceNoteDeleted(true);
                }
            } else {
                // forwardSourceId 为空但 isForwarded 为 true，说明源笔记已被删除
                vo.setSourceNoteDeleted(true);
            }
        }

        // 获取配乐名称和详情
        if (note.getMusicId() != null && note.getMusicType() != null) {
            vo.setMusicName(getMusicName(note.getMusicId(), note.getMusicType()));
            // 当配乐类型为单曲时，获取歌曲详细信息
            if (note.getMusicType() == 1) {
                SongWithDetail song = songMapper.selectWithDetailBySongId(note.getMusicId());
                if (song != null) {
                    vo.setSongCoverUrl(song.getCoverUrl());
                    vo.setSongName(song.getSongName());
                    vo.setAlbumId(song.getAlbumId());
                    vo.setAlbumName(song.getAlbumName());
                    // 获取歌手信息
                    if (song.getArtistIds() != null && !song.getArtistIds().isEmpty()) {
                        String[] artistIdArr = song.getArtistIds().split(",");
                        if (artistIdArr.length > 0) {
                            vo.setArtistId(Long.parseLong(artistIdArr[0].trim()));
                        }
                        if (song.getArtistNames() != null && !song.getArtistNames().isEmpty()) {
                            String[] artistNameArr = song.getArtistNames().split(",");
                            if (artistNameArr.length > 0) {
                                vo.setArtistName(artistNameArr[0].trim());
                            }
                        }
                    }
                }
            }
            // 当配乐类型为歌手时，获取歌手详细信息
            if (note.getMusicType() == 2) {
                Artist artist = artistMapper.selectById(note.getMusicId());
                if (artist != null) {
                    vo.setArtistId(artist.getArtistId());
                    vo.setArtistName(artist.getArtistName());
                    vo.setArtistAvatarUrl(artist.getArtistAvatarUrl());
                    vo.setArtistFollowerCount(artist.getHot() != null ? artist.getHot().intValue() : 0);
                }
            }
            // 当配乐类型为专辑时，获取专辑详细信息
            if (note.getMusicType() == 3) {
                Album album = albumMapper.selectById(note.getMusicId());
                if (album != null) {
                    vo.setAlbumId(album.getAlbumId());
                    vo.setAlbumName(album.getAlbumName());
                    vo.setAlbumCoverUrl(album.getCoverUrl());
                    // 获取专辑作者
                    AlbumArtist albumArtist = albumArtistMapper.selectOne(
                            new LambdaQueryWrapper<AlbumArtist>().eq(AlbumArtist::getAlbumId, album.getAlbumId())
                    );
                    if (albumArtist != null) {
                        Artist artist = artistMapper.selectById(albumArtist.getArtistId());
                        if (artist != null) {
                            vo.setAlbumAuthorName(artist.getArtistName());
                        }
                    }
                }
            }
            // 当配乐类型为歌单时，获取歌单详细信息
            if (note.getMusicType() == 4) {
                Playlist playlist = playlistMapper.selectById(note.getMusicId());
                if (playlist != null) {
                    vo.setPlaylistId(playlist.getPlaylistId());
                    vo.setPlaylistName(playlist.getPlaylistName());
                    vo.setPlaylistCoverUrl(playlist.getCoverUrl());
                    // 获取歌单创建者
                    if (playlist.getUserId() != null) {
                        User playlistCreator = userMapper.selectById(playlist.getUserId());
                        if (playlistCreator != null) {
                            vo.setPlaylistCreatorName(playlistCreator.getNickname());
                        }
                    }
                }
            }
        }

        // 检查当前用户是否点赞和转发
        if (currentUserId != null) {
            vo.setLiked(noteLikeMapper.existsByNoteIdAndUserId(note.getId(), currentUserId));
            vo.setForwarded(noteForwardMapper.existsByNoteIdAndUserId(note.getId(), currentUserId));
        } else {
            vo.setLiked(false);
            vo.setForwarded(false);
        }

        // 获取所有评论（扁平结构，按最新排序，只查询未删除的）
        List<NoteComment> allComments = noteCommentMapper.selectList(
                new LambdaQueryWrapper<NoteComment>()
                        .eq(NoteComment::getNoteId, note.getId())
                        .eq(NoteComment::getStatus, 0)
                        .orderByDesc(NoteComment::getId)
        );
        vo.setComments(allComments.stream()
                .map(c -> convertToCommentVO(c, currentUserId))
                .collect(Collectors.toList()));

        return vo;
    }

    private NoteCommentVO convertToCommentVO(NoteComment comment, Long currentUserId) {
        NoteCommentVO vo = new NoteCommentVO();
        vo.setCommentId(comment.getId());
        vo.setNoteId(comment.getNoteId());
        vo.setUserId(comment.getUserId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setContent(comment.getContent());
        vo.setCreatedAt(comment.getCreatedAt());

        // 获取评论用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatarUrl(user.getAvatarUrl());
        }

        // 获取回复目标用户昵称
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userMapper.selectById(comment.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyToNickname(replyToUser.getNickname());
            }
        }

        // 获取被回复的评论内容（引用）
        if (comment.getParentId() != null) {
            NoteComment parentComment = noteCommentMapper.selectById(comment.getParentId());
            if (parentComment != null && parentComment.getStatus() != null && parentComment.getStatus() == 1) {
                // 父评论已被删除
                vo.setQuotedCommentDeleted(true);
            } else if (parentComment != null) {
                vo.setQuoteContent(parentComment.getContent());
                User parentUser = userMapper.selectById(parentComment.getUserId());
                if (parentUser != null) {
                    vo.setQuoteNickname(parentUser.getNickname());
                }
                vo.setQuotedCommentDeleted(false);
            } else {
                // 父评论不存在（级联删除等情况）
                vo.setQuotedCommentDeleted(true);
            }
        }

        // 扁平结构，不获取子评论
        vo.setChildren(java.util.Collections.emptyList());

        return vo;
    }

    private String getMusicName(Long musicId, Integer musicType) {
        if (musicId == null || musicType == null) {
            return null;
        }
        return switch (musicType) {
            case 1 -> {
                Song song = songMapper.selectById(musicId);
                yield song != null ? song.getSongName() : null;
            }
            case 2 -> {
                Artist artist = artistMapper.selectById(musicId);
                yield artist != null ? artist.getArtistName() : null;
            }
            case 3 -> {
                Album album = albumMapper.selectById(musicId);
                yield album != null ? album.getAlbumName() : null;
            }
            case 4 -> {
                Playlist playlist = playlistMapper.selectById(musicId);
                yield playlist != null ? playlist.getPlaylistName() : null;
            }
            default -> null;
        };
    }

    private UserBaseVO convertToUserBaseVO(User user) {
        UserBaseVO vo = new UserBaseVO();
        vo.setUserId(user.getUserId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setSignature(user.getSignature());
        vo.setFollowingCount(user.getFollowingCount());
        vo.setFollowerCount(user.getFollowerCount());
        return vo;
    }
}