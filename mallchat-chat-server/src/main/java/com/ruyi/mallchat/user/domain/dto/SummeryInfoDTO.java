package com.ruyi.mallchat.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Description: 用户详细信息 包括关联性信息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummeryInfoDTO {
    /**
     * 用户拥有的徽章id列表
     */
    @ApiModelProperty(value = "用户拥有的徽章id列表")
    List<Long> itemIds;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long uid;
    /**
     * 是否需要刷新
     */
    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String name;
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    /**
     * 归属地
     */
    @ApiModelProperty(value = "归属地")
    private String locPlace;
    /**
     * 佩戴的徽章id
     */
    @ApiModelProperty("佩戴的徽章id")
    private Long wearingItemId;

    public static SummeryInfoDTO skip(Long uid) {
        SummeryInfoDTO dto = new SummeryInfoDTO();
        dto.setUid(uid);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }
}
