package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.dto.ArtistPageDTO;
import com.jiangce.yueting.domain.dto.ArtistSearchDTO;
import com.jiangce.yueting.domain.dto.ArtistUpdateDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.ArtistBaseVO;
import com.jiangce.yueting.domain.vo.ArtistPageByAdminVO;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;
import com.jiangce.yueting.mapper.ArtistMapper;
import com.jiangce.yueting.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistMapper artistMapper;

    @Override
    public void updateMyArtistInfo(ArtistUpdateDTO artistUpdateDTO) {
        Artist artist = artistUpdateDTO.toArtist();
        artist.setUserId(BaseContext.getCurrentId());
        artistMapper.updateArtistByUserId(artist);
    }

    @Override
    public List<ArtistBaseVO> searchArtistBasic(String keyword, Integer pageNum, Integer pageSize) {
        Page<Artist> page = new Page<>(pageNum, pageSize);
        IPage<Artist> result = artistMapper.pageArtist(new ArtistSearchDTO(keyword,null,null,null,null), page);
        return result.getRecords().stream().map(artist -> {
            ArtistBaseVO artistBaseVO = new ArtistBaseVO();
            artistBaseVO.setArtistId(artist.getArtistId());
            artistBaseVO.setArtistName(artist.getArtistName());
            artistBaseVO.setArtistAvatarUrl(artist.getArtistAvatarUrl());
            return artistBaseVO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult searchArtistBasic(ArtistSearchDTO artistSearchDTO) {
        Page<Artist> page = new Page<>(artistSearchDTO.getPageNum(), artistSearchDTO.getPageSize());
        IPage<Artist> result = artistMapper.pageArtist(artistSearchDTO, page);
        List<ArtistBaseVO> collect = result.getRecords().stream().map(artist -> {
            ArtistBaseVO artistBaseVO = new ArtistBaseVO();
            artistBaseVO.setArtistId(artist.getArtistId());
            artistBaseVO.setArtistName(artist.getArtistName());
            artistBaseVO.setArtistAvatarUrl(artist.getArtistAvatarUrl());
            return artistBaseVO;
        }).collect(Collectors.toList());
        return new PageResult(result.getTotal(), collect);
    }

    @Override
    public PageResult pageSinger(ArtistPageDTO artistPageDTO) {
        Page<ArtistPageByAdminVO> page = new Page<>(artistPageDTO.getPageNum(), artistPageDTO.getPageSize());
        IPage<ArtistPageByAdminVO> result = artistMapper.pageArtistByAdmin(artistPageDTO, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public Artist getCurrentArtist() {
        LambdaQueryWrapper<Artist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artist::getUserId, BaseContext.getCurrentId());
        return artistMapper.selectOne(wrapper);
    }

    @Override
    public ArtistWithIdentitiesVO getCurrentArtistWithIdentities() {
        return artistMapper.selectArtistWithIdentitiesOneByUserId(BaseContext.getCurrentId());
    }

}
