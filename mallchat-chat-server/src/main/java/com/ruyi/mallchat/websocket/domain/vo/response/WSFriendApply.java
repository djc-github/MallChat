package com.ruyi.mallchat.websocket.domain.vo.response;

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
public class WSFriendApply {
    @ApiModelProperty("申请人")
    private Long uid;
    @ApiModelProperty("申请未读数")
    private Integer unreadCount;
}
