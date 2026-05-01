package com.jiangce.yueting.service;

import org.springframework.web.multipart.MultipartFile;

public interface CommonService {


    /**
     * 上传文件到OSS
     * @param file 文件
     * @return 文件访问地址
     */
    String uploadImage(MultipartFile file);
}
