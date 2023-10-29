package com.ruyi.mallchat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruyi.mallchat.user.domain.entity.Black;
import com.ruyi.mallchat.user.mapper.BlackMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-21
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

}