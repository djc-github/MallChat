package com.ruyi.mallchat.common.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.ruyi.mallchat.common.common.utils.RedisUtils;
import org.springframework.data.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: redis string类型的批量缓存框架
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-10
 */
public abstract class AbstractRedisStringCache<IN, OUT> implements BatchCache<IN, OUT> {

    private Class<OUT> outClass;

    protected AbstractRedisStringCache() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
    }

    /**
     * 生成redis键
     *
     * @param req 生成键需要的数据
     * @return redis键
     */
    protected abstract String getKey(IN req);

    /**
     * 获取定好的过期时间
     *
     * @return 过期时间
     */
    protected abstract Long getExpireSeconds();


    /**
     * 获取需要更新的键和真实值对应关系，而非缓存中的值
     *
     * @param req 要更新的键
     * @return 键和真实值的对应map
     */
    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return getBatch(Collections.singletonList(req)).get(req);
    }

    /**
     * redis获取批量
     */
    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        if (CollectionUtil.isEmpty(req)) {//防御性编程
            return new HashMap<>();
        }
        //去重
        req = req.stream().distinct().collect(Collectors.toList());
        //组装key
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        //批量get
        List<OUT> valueList = RedisUtils.mget(keys, outClass);
        //差集计算
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            if (Objects.isNull(valueList.get(i))) {
                loadReqs.add(req.get(i));
            }
        }
        Map<IN, OUT> load = new HashMap<>();
        //不足的重新加载进redis
        if (CollectionUtil.isNotEmpty(loadReqs)) {
            //批量load
            load = load(loadReqs);
            Map<String, OUT> loadMap = load.entrySet().stream()
                    .map(a -> Pair.of(getKey(a.getKey()), a.getValue()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            RedisUtils.mset(loadMap, getExpireSeconds());
        }

        //组装最后的结果
        Map<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(valueList.get(i))
                    .orElse(load.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }

    /**
     * 删除单个redis缓存
     */
    @Override
    public void delete(IN req) {
        deleteBatch(Collections.singletonList(req));
    }

    /**
     * 批量删除redis缓存
     *
     * @param req 多个键
     */
    @Override
    public void deleteBatch(List<IN> req) {
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        RedisUtils.del(keys);
    }
}
