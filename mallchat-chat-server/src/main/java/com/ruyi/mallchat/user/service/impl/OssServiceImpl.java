package com.ruyi.mallchat.user.service.impl;

import com.ruyi.mallchat.common.utils.AssertUtil;
import com.ruyi.mallchat.oss.MinIOTemplate;
import com.ruyi.mallchat.oss.domain.OssReq;
import com.ruyi.mallchat.oss.domain.OssResp;
import com.ruyi.mallchat.user.domain.enums.OssSceneEnum;
import com.ruyi.mallchat.user.domain.vo.request.oss.UploadUrlReq;
import com.ruyi.mallchat.user.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-20
 */
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;

    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
