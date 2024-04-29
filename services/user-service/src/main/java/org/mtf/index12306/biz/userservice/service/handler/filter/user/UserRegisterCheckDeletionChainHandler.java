package org.mtf.index12306.biz.userservice.service.handler.filter.user;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.mtf.index12306.biz.userservice.service.UserService;
import org.mtf.index12306.framework.starter.convention.exception.ClientException;
import org.springframework.stereotype.Component;

import static org.mtf.index12306.biz.userservice.common.constant.Index12306Constant.USER_DELETION_MAX_TIMES;

/**
 * 用户注册检查证件号是否多次注销
 */
@Component
@RequiredArgsConstructor
public final class UserRegisterCheckDeletionChainHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO> {

    private final UserService userService;

    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        Integer userDeletionNum = userService.queryUserDeletionNum(requestParam.getIdType(), requestParam.getIdCard());
        if (userDeletionNum >= USER_DELETION_MAX_TIMES) {
            throw new ClientException("多次注销账号，已被加入黑名单");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
