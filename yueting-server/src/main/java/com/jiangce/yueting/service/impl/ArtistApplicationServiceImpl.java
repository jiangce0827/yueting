package com.jiangce.yueting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangce.yueting.common.ArtistApplicationContent;
import com.jiangce.yueting.common.ArtistNotificationContent;
import com.jiangce.yueting.common.ArtistContent;
import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.common.CustomException;
import com.jiangce.yueting.domain.dto.ArtistApplicationDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationIdentityPageDTO;
import com.jiangce.yueting.domain.dto.ArtistApplicationPageDTO;
import com.jiangce.yueting.domain.dto.ArtistIdentityApplicationDTO;
import com.jiangce.yueting.domain.po.Artist;
import com.jiangce.yueting.domain.po.ArtistApplication;
import com.jiangce.yueting.domain.po.ArtistIdentity;
import com.jiangce.yueting.domain.po.ArtistIdentityApplication;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.ArtistIdentityApplicationVO;
import com.jiangce.yueting.domain.vo.ArtistApplicationVO;
import com.jiangce.yueting.domain.vo.ArtistWithIdentitiesVO;
import com.jiangce.yueting.mapper.*;
import com.jiangce.yueting.service.ArtistApplicationService;
import com.jiangce.yueting.service.AuthService;
import com.jiangce.yueting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArtistApplicationServiceImpl implements ArtistApplicationService {
    private final AuthService authService;
    private final ArtistApplicationMapper artistApplicationMapper;
    private final ArtistMapper artistMapper;
    private final ArtistIdentityApplicationMapper artistIdentityApplicationMapper;
    private final IdentityTypeMapper identityTypeMapper;
    private final ArtistIdentityMapper artistIdentityMapper;
    private final NotificationService notificationService;

    /**
     * 申请成为艺人
     */
    @Override
    public void applyToArtist(ArtistApplicationDTO artistApplicationDTO) {
        //1. 验证邮箱与验证码是否正确
        authService.verifyEmailCode(BaseContext.getCurrentId(), artistApplicationDTO.getCode());

        // 检查用户是否已是艺人
        LambdaQueryWrapper<Artist> artistLambdaQueryWrapper = new LambdaQueryWrapper<>();
        artistLambdaQueryWrapper.eq(Artist::getUserId, BaseContext.getCurrentId());
        if (artistMapper.exists(artistLambdaQueryWrapper)) {
            throw new CustomException("您已经绑定了艺人信息，请勿重复申请");
        }
        artistLambdaQueryWrapper.clear();

        //2. 检查是否提交过申请
        LambdaQueryWrapper<ArtistApplication> artistApplicationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        artistApplicationLambdaQueryWrapper.eq(ArtistApplication::getUserId, BaseContext.getCurrentId())
                .eq(ArtistApplication::getStatus, ArtistApplicationContent.STATUS_UNREVIEWED);
        if (artistApplicationMapper.exists(artistApplicationLambdaQueryWrapper)) {
            throw new CustomException("您的申请正在审核中，请勿重复提交");
        }
        artistApplicationLambdaQueryWrapper.clear();

        // 3.检查身份证是否已绑定艺人，并且当前不是在申领该艺人
        artistLambdaQueryWrapper.eq(Artist::getIdCard, artistApplicationDTO.getIdCard());
        Artist artist = artistMapper.selectOne(artistLambdaQueryWrapper);
        if (artist != null && !(artistApplicationDTO.getApplyType().equals(ArtistApplicationContent.APPLY_TYPE_CLAIM) && artistApplicationDTO.getClaimedArtistId().equals(artist.getArtistId()))) {
            throw new CustomException("该实名已绑定艺人，请勿重复绑定");
        }

        // 4. 插入申请记录
        ArtistApplication artistApplication = artistApplicationDTO.toArtistApplication();
        artistApplication.setUserId(BaseContext.getCurrentId());
        artistApplication.setStatus(ArtistApplicationContent.STATUS_UNREVIEWED);
        artistApplication.setAppliedAt(LocalDateTime.now());
        artistApplicationMapper.insert(artistApplication);

    }

    /**
     * 同意艺人身份申请
     */
    @Override
    @Transactional
    public void approveToArtist(Long id) {
        ArtistApplication artistApplication = artistApplicationMapper.selectById(id);
        if (!artistApplication.getStatus().equals(ArtistApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审批");
        }

        // 检查是否为认领申请，如果是认领申请，则检查是否已经认领
        LambdaQueryWrapper<Artist> queryWrapper = new LambdaQueryWrapper<>();
        if (artistApplication.getApplyType().equals(ArtistApplicationContent.APPLY_TYPE_CLAIM)) {
            queryWrapper.eq(Artist::getArtistId, artistApplication.getClaimedArtistId());
            Artist artist = artistMapper.selectOne(queryWrapper);
            if (artist.getUserId() != null) {
                throw new CustomException("该艺人已被认领");
            }
            // 绑定用户到艺人信息
            artist.setUserId(artistApplication.getUserId());
            artistMapper.updateById(artist);
        }
        // 检查是否为新建艺人，如果是新建艺人，则检查艺人名是否已被使用
        if (artistApplication.getApplyType().equals(ArtistApplicationContent.APPLY_TYPE_NEW)) {
            queryWrapper.eq(Artist::getArtistName, artistApplication.getArtistName());
            if (artistMapper.exists(queryWrapper)) {
                throw new CustomException("该艺人名已被使用");
            }
            // 新建艺人信息并保存
            Artist artist = artistApplication.toArtist();
            artist.setUserId(artistApplication.getUserId());
            artistMapper.insert(artist);
        }

        // 更新申请记录
        artistApplication.setReviewedBy(BaseContext.getCurrentId());
        artistApplication.setReviewedAt(LocalDateTime.now());
        artistApplication.setStatus(ArtistApplicationContent.STATUS_APPROVE);
        artistApplicationMapper.updateById(artistApplication);

        notificationService.createNotification(
                artistApplication.getUserId(),
                null,
                ArtistNotificationContent.TYPE_ARTIST_APPLICATION_APPROVED,
                "你的音乐人申请已审核通过",
                artistApplication.getApplicationId(),
                ArtistNotificationContent.TARGET_TYPE_ARTIST_APPLICATION,
                artistApplication.getClaimedArtistId()
        );
    }

    /**
     * 拒绝艺人身份申请
     *
     * @param rejectionReason 拒绝原因
     */
    @Override
    public void rejectToArtist(Long id, String rejectionReason) {
        ArtistApplication artistApplication = artistApplicationMapper.selectById(id);
        if (!artistApplication.getStatus().equals(ArtistApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审批");
        }
        // 更新申请记录
        artistApplication.setReviewedBy(BaseContext.getCurrentId());
        artistApplication.setReviewedAt(LocalDateTime.now());
        artistApplication.setStatus(ArtistApplicationContent.STATUS_REJECT);
        artistApplication.setRejectionReason(rejectionReason);
        artistApplicationMapper.updateById(artistApplication);

        String reason = rejectionReason == null || rejectionReason.isBlank() ? "无" : rejectionReason;
        notificationService.createNotification(
                artistApplication.getUserId(),
                null,
                ArtistNotificationContent.TYPE_ARTIST_APPLICATION_REJECTED,
                "你的音乐人申请未通过审核，原因：" + reason,
                artistApplication.getApplicationId(),
                ArtistNotificationContent.TARGET_TYPE_ARTIST_APPLICATION,
                artistApplication.getClaimedArtistId()
        );
    }

    /**
     * 申请艺人具体身份
     */
    @Override
    public void applyArtistIdentity(ArtistIdentityApplicationDTO artistIdentityApplicationDTO) {
        ArtistWithIdentitiesVO artistWithIdentitiesVO = artistMapper.selectArtistWithIdentitiesOneByUserId(BaseContext.getCurrentId());
        artistWithIdentitiesVO.getIdentities().forEach(identity -> {
            if (identity.equals(identityTypeMapper.getNameById(artistIdentityApplicationDTO.getIdentityType()))) {
                throw new CustomException("您已经是歌手，无需重复申请");
            }
        });
        LambdaQueryWrapper<ArtistIdentityApplication> artistIdentityApplicationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        artistIdentityApplicationLambdaQueryWrapper.eq(ArtistIdentityApplication::getArtistId, artistWithIdentitiesVO.getArtistId())
                .eq(ArtistIdentityApplication::getIdentityType, artistIdentityApplicationDTO.getIdentityType())
                .eq(ArtistIdentityApplication::getStatus, ArtistApplicationContent.STATUS_UNREVIEWED);
        if (artistIdentityApplicationMapper.exists(artistIdentityApplicationLambdaQueryWrapper)) {
            throw new CustomException("您的申请正在审核中，请勿重复提交");
        }
        LambdaQueryWrapper<Artist> queryWrapper = new LambdaQueryWrapper<>();
        ;
        Long artistId = artistMapper.selectOne(queryWrapper.eq(Artist::getUserId, BaseContext.getCurrentId())).getArtistId();
        ArtistIdentityApplication artistIdentityApplication = artistIdentityApplicationDTO.toArtistIdentityApplication();
        artistIdentityApplication.setArtistId(artistId);
        artistIdentityApplicationMapper.insert(artistIdentityApplication);
    }

    /**
     * 同意艺人具体身份申请
     */
    @Override
    @Transactional
    public void approveArtistIdentity(Long id) {
        // 检查是否已被审批
        ArtistIdentityApplication artistIdentityApplication = artistIdentityApplicationMapper.selectById(id);
        if (!artistIdentityApplication.getStatus().equals(ArtistApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审批");
        }
        // 更新申请记录
        artistIdentityApplication.setReviewedBy(BaseContext.getCurrentId());
        artistIdentityApplication.setReviewedAt(LocalDateTime.now());
        artistIdentityApplication.setStatus(ArtistApplicationContent.STATUS_APPROVE);
        artistIdentityApplicationMapper.updateById(artistIdentityApplication);
        // 添加艺人具体身份
        artistIdentityMapper.insert(new ArtistIdentity(artistIdentityApplication.getArtistId(), artistIdentityApplication.getIdentityType()));
        createArtistIdentityNotification(
                artistIdentityApplication,
                ArtistNotificationContent.TYPE_ARTIST_IDENTITY_APPROVED,
                "你的音乐人身份申请已审核通过"
        );
    }

    @Override
    public void rejectArtistIdentity(Long id, String rejectionReason) {
        // 检查是否已被审批
        ArtistIdentityApplication artistIdentityApplication = artistIdentityApplicationMapper.selectById(id);
        if (!artistIdentityApplication.getStatus().equals(ArtistApplicationContent.STATUS_UNREVIEWED)) {
            throw new CustomException("申请已被审批");
        }
        // 更新申请记录
        artistIdentityApplication.setReviewedBy(BaseContext.getCurrentId());
        artistIdentityApplication.setReviewedAt(LocalDateTime.now());
        artistIdentityApplication.setStatus(ArtistApplicationContent.STATUS_REJECT);
        artistIdentityApplication.setRejectionReason(rejectionReason);
        artistIdentityApplicationMapper.updateById(artistIdentityApplication);
        String reason = rejectionReason == null || rejectionReason.isBlank() ? "无" : rejectionReason;
        createArtistIdentityNotification(
                artistIdentityApplication,
                ArtistNotificationContent.TYPE_ARTIST_IDENTITY_REJECTED,
                "你的音乐人身份申请未通过审核，原因：" + reason
        );
    }

    @Override
    public PageResult pageArtistApplication(ArtistApplicationPageDTO artistApplicationPageDTO) {
        Page<ArtistApplicationVO> page = new Page<>(artistApplicationPageDTO.getPageNum(), artistApplicationPageDTO.getPageSize());
        IPage<ArtistApplicationVO> result = artistApplicationMapper.pageArtistApplication(artistApplicationPageDTO, page);
        List<Long> artistIds = new ArrayList<>();
        for (ArtistApplicationVO artistApplicationVO : result.getRecords()) {
            if (artistApplicationVO.getApplyType().equals(ArtistApplicationContent.APPLY_TYPE_CLAIM)) {
                artistIds.add(artistApplicationVO.getClaimedArtistId());
            }
        }
        if (!artistIds.isEmpty()) {
            Map<Long, Artist> longArtistMap = artistMapper.selectArtistsMapByIds(artistIds);
            result.getRecords().forEach(artistApplicationVO -> {
                if (artistApplicationVO.getApplyType().equals(ArtistApplicationContent.APPLY_TYPE_CLAIM)) {
                    artistApplicationVO.setArtistName(longArtistMap.get(artistApplicationVO.getClaimedArtistId()).getArtistName());
                }
            });
        }
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @Override
    public PageResult pageArtistApplicationIdentity(ArtistApplicationIdentityPageDTO artistApplicationIdentityPageDTO) {
        Page<ArtistIdentityApplicationVO> page = new Page<>(artistApplicationIdentityPageDTO.getPageNum(), artistApplicationIdentityPageDTO.getPageSize());
        IPage<ArtistIdentityApplicationVO> result = artistIdentityApplicationMapper.pageArtistIdentityApplication(artistApplicationIdentityPageDTO, page);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    private void createArtistIdentityNotification(ArtistIdentityApplication application, Integer type, String content) {
        Artist artist = artistMapper.selectById(application.getArtistId());
        if (artist == null || artist.getUserId() == null) {
            return;
        }

        notificationService.createNotification(
                artist.getUserId(),
                null,
                type,
                content,
                application.getArtistIdentityApplicationId(),
                ArtistNotificationContent.TARGET_TYPE_ARTIST_IDENTITY_APPLICATION,
                application.getArtistId()
        );
    }
}
