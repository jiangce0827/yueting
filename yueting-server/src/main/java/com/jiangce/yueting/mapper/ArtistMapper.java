package com.jiangce.yueting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.domain.dto.ArtistPageDTO;
import com.jiangce.yueting.domain.dto.ArtistSearchDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.vo.ArtistPageByAdminVO;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 艺人 Mapper
 */
@Mapper
public interface ArtistMapper extends BaseMapper<Artist> {

    /**
     * 根据用户ID查询艺人信息
     *
     * @param userId 用户ID
     * @return 艺人信息
     */
    default Artist selectArtistByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapper<Artist>().eq(Artist::getUserId, userId));
    }

    /**
     * 根据用户ID查询艺人详情（含身份信息）
     *
     * @param userId 用户ID
     * @return 艺人详情
     */
    ArtistWithIdentitiesVO selectArtistWithIdentitiesOneByUserId(Long userId);

    /**
     * 根据用户ID更新艺人信息
     *
     * @param artist 艺人信息
     */
    void updateArtistByUserId(Artist artist);

    /**
     * 分页查询艺人列表
     *
     * @param artistSearchDTO 查询条件
     * @param page 分页参数
     * @return 艺人分页列表
     */
    default IPage<Artist> pageArtist(ArtistSearchDTO artistSearchDTO, Page<Artist> page) {
        LambdaQueryWrapper<Artist> wrapper = new LambdaQueryWrapper<>();
        if (artistSearchDTO.getKeyword() != null && !artistSearchDTO.getKeyword().isEmpty()) {
            wrapper.like(Artist::getArtistName, artistSearchDTO.getKeyword());
        }
        if (artistSearchDTO.getGender() != null) {
            wrapper.eq(Artist::getGender, artistSearchDTO.getGender());
        }
        if (artistSearchDTO.getLanguage() != null) {
            wrapper.eq(Artist::getLanguage, artistSearchDTO.getLanguage());
        }
        return selectPage(page, wrapper);
    }

    /**
     * 管理员分页查询艺人列表
     *
     * @param artistPageDTO 查询条件
     * @param page 分页参数
     * @return 艺人分页列表
     */
    IPage<ArtistPageByAdminVO> pageArtistByAdmin(ArtistPageDTO artistPageDTO, Page<ArtistPageByAdminVO> page);

    /**
     * 根据艺人ID列表批量查询艺人
     *
     * @param artistIds 艺人ID列表
     * @return 艺人Map，key为艺人ID
     */
    @MapKey("artistId")
    default Map<Long, Artist> selectArtistsMapByIds(@Param("artistIds") List<Long> artistIds) {
        return selectBatchIds(artistIds).stream().collect(java.util.stream.Collectors.toMap(Artist::getArtistId, artist -> artist));
    }
}
