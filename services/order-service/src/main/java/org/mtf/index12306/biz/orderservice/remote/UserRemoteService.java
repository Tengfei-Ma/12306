package org.mtf.index12306.biz.orderservice.remote;

import org.mtf.index12306.biz.orderservice.remote.dto.UserQueryActualRespDTO;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户远程服务调用
 */
@FeignClient(value = "index12306-user${unique-name:}-service", url = "${aggregation.remote-url:}")
public interface UserRemoteService {

    /**
     * 根据用户名查询用户无脱敏信息
     */
    @GetMapping("/api/user-service/actual/user")
    Result<UserQueryActualRespDTO> queryActualUserByUsername(@RequestParam("username") String username);
}
