package org.mtf.index12306.biz.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户名复用实体
 */
@Data
@TableName("t_user_reuse")
public class UserReuseDO {
    /**
     * 用户名
     */
    private String username;
}
