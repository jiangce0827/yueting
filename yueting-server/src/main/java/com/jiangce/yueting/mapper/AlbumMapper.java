package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.vo.AlbumWithAuthor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 专辑 Mapper
 */
@Mapper
public interface AlbumMapper extends BaseMapper<Album> {

    /**
     * 根据歌手ID查询专辑列表
     *
     * @param artistId 歌手ID
     * @return 专辑列表
     */
    List<Album> selectAlbumsByArtistId(Long artistId);

    /**
     * 根据歌手ID分页查询专辑列表
     *
     * @param artistId 歌手ID
     * @param page 分页参数
     * @return 专辑分页列表
     */
    IPage<Album> pageAlbumsByArtistId(Long artistId, Page<Album> page);

    /**
     * 根据专辑ID查询专辑详情（含作者信息）
     *
     * @param albumId 专辑ID
     * @return 专辑详情
     */
    AlbumWithAuthor selectAlbumWithAuthorById(Long albumId);

    /**
     * 根据关键词搜索专辑
     *
     * @param keyword 关键词（专辑名或描述）
     * @param page 分页参数
     * @return 专辑分页列表
     */
    default IPage<Album> selectAlbumsByKeyword(String keyword, Page<Album> page) {
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Album::getAlbumName, keyword)
               .or()
               .like(Album::getDescription, keyword);
        return selectPage(page, wrapper);
    }
}
