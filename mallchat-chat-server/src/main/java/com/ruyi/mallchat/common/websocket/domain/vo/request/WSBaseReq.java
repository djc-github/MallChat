package com.ruyi.mallchat.common.websocket.domain.vo.request;


import com.ruyi.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import lombok.Data;

/**
 * Description:  websocket前端请求体
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型 1-请求登录二维码，2-心跳检测, 3-登录认证
     *
     * @see WSReqTypeEnum
     */
    private Integer type;
    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private String data;
}
