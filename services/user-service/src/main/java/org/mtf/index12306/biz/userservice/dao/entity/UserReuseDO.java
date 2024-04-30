package org.mtf.index12306.biz.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.mtf.index12306.framework.starter.database.base.BaseDO;

/**
 * 用户名复用实体
 */
@Data
@TableName("t_user_reuse")
public class UserReuseDO extends BaseDO {
    /**
     * 用户名
     */
    private String username;
}
