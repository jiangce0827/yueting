package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.ArtistApplicationDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationIdentityPageDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationPageDTO;
import com.jiangce.yueting.domain.dto.ArtistIdentityApplicationDTO;
import com.jiangce.yueting.domain.result.PageResult;

public interface ArtistApplicationService {
    /**
     * 申请成为艺人
     */
    void applyToArtist(ArtistApplicationDTO artistApplicationDTO);

    /**
     * 审核通过
     */
    void approveToArtist(Long id);

    /**
     * 审核不通过
     */
    void rejectToArtist(Long id, String rejectionReason);


    /**
     * 申请艺人具体身份
     */
    void applyArtistIdentity(ArtistIdentityApplicationDTO artistIdentityApplicationDTO);

    /**
     * 同意艺人具体身份申请
     *
     * @param id 艺人具体身份申请id
     */
    void approveArtistIdentity(Long id);


    /**
     * 拒绝艺人具体身份申请
     *
     * @param id 艺人具体身份申请id
     */
    void rejectArtistIdentity(Long id,String rejectionReason);

    PageResult pageArtistApplication(ArtistApplicationPageDTO artistApplicationPageDTO);

    PageResult pageArtistApplicationIdentity(ArtistApplicationIdentityPageDTO artistApplicationIdentityPageDTO);
}
