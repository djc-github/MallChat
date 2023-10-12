package com.ruyi.mallchat.common.websocket.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * access_token
     */
    private String token;
    /**
     * 用户名字
     */
    private String name;
    /**
     * 用户权限 0普通用户 1超管
     */
    private Integer power;
}
