package org.mtf.index12306.biz.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.mtf.index12306.framework.starter.database.base.BaseDO;

/**
 * 用户邮箱实体
 */
@Data
@TableName("t_user_mail")
public class UserMailDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;
}
