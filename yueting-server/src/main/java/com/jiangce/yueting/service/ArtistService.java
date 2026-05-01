package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.ArtistPageDTO;
import com.jiangce.yueting.domain.dto.ArtistSearchDTO;
import com.jiangce.yueting.domain.dto.ArtistUpdateDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.ArtistBaseVO;
import com.jiangce.yueting.domain.vo.ArtistPageByAdminVO;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;

import java.util.List;

public interface ArtistService {
    /**
     * 更新我的艺人信息
     */
    void updateMyArtistInfo(ArtistUpdateDTO artistUpdateDTO);


    /**
     * 根据关键词搜索艺人
     * @param keyword 关键词
     */
    List<ArtistBaseVO> searchArtistBasic(String keyword,Integer pageNum, Integer pageSize);

    /**
     * 根据条件搜索艺人
     */
    PageResult searchArtistBasic(ArtistSearchDTO artistSearchDTO);

    PageResult pageSinger(ArtistPageDTO artistPageDTO);

    /**
     * 获取当前用户的艺人信息
     * @return 当前用户的Artist对象，如果用户不是艺人则返回null
     */
    Artist getCurrentArtist();

    /**
     * 获取当前用户的艺人信息（包含身份）
     * @return 当前用户的ArtistWithIdentitiesVO对象，如果用户不是艺人则返回null
     */
    ArtistWithIdentitiesVO getCurrentArtistWithIdentities();
}
