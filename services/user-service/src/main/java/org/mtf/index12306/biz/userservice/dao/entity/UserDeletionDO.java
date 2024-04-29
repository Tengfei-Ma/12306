package org.mtf.index12306.biz.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.mtf.index12306.framework.starter.database.base.BaseDO;

/**
 * 用户注销实体
 */
@Data
@TableName("t_user_deletion")
public class UserDeletionDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号
     */
    private String idCard;
}
