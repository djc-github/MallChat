package com.ruyi.mallchat.common.websocket.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
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
public class WSMessageRead {
    @ApiModelProperty("消息")
    private Long msgId;
    @ApiModelProperty("阅读人数（可能为0）")
    private Integer readCount;
}
