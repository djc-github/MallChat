package com.ruyi.mallchat.common.common.service.cache;

import java.util.List;
import java.util.Map;

public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);

    /**
     * 删除单个
     */
    void delete(IN req);

    /**
     * 删除多个
     */
    void deleteBatch(List<IN> req);
}
