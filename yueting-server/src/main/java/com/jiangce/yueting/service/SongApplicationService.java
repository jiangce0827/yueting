package com.jiangce.yueting.service;

import com.jiangce.yueting.domain.dto.SongApplicationPageDTO;
import com.jiangce.yueting.domain.result.PageResult;

public interface SongApplicationService {
    /**
     * 通过歌曲上传申请
     */
    void approveSong(Long id);

    /**
     * 拒绝歌曲上传申请
     */
    void rejectSong(Long id, String rejectionReason);

    PageResult pageSongApplication(SongApplicationPageDTO songApplicationPageDTO);

    PageResult pageMySongApplication(SongApplicationPageDTO songApplicationPageDTO);
}
