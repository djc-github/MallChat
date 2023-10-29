package com.ruyi.mallchat.user.service.cache;

import com.ruyi.mallchat.user.dao.ItemConfigDao;
import com.ruyi.mallchat.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 物品缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 */
@Component
public class ItemCache {//todo 多级缓存

    @Autowired
    private ItemConfigDao itemConfigDao;

    /**
     * 通过物品类型查询
     *
     * @param type 物品类型
     * @return 物品列表
     */
    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }

    /**
     * 通过物品id查询
     *
     * @param itemId 物品id
     * @return 物品信息
     */
    @Cacheable(cacheNames = "item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
