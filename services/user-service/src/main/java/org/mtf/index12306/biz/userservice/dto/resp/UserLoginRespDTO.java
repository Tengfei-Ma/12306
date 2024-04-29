package org.mtf.index12306.biz.userservice.dto.resp;

import lombok.Data;

/**
 * 用户登录响应参数
 */
@Data
public class UserLoginRespDTO {
    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * Token
     */
    private String accessToken;
}
