package com.ruyi.mallchat.common.sensitive.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 敏感词库
 *
 * @TableName sensitive_word
 */
@TableName(value = "sensitive_word")
@EqualsAndHashCode(callSuper = false)
@Data
public class SensitiveWord implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 敏感词
     */
    private String word;
}