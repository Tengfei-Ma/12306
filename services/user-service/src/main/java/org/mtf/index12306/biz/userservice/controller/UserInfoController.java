package org.mtf.index12306.biz.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.userservice.dto.req.UserDeletionReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserLoginReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserUpdateReqDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserLoginRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryActualRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserRegisterRespDTO;
import org.mtf.index12306.biz.userservice.service.UserService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制层
 */
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/user-service/user")
    public Result<UserQueryRespDTO> queryUserByUsername(@RequestParam("username") @NotEmpty String username){
        return Results.success(userService.queryUserByUsername(username));
    }
    /**
     * 根据用户名查询用户无脱敏信息
     */
    @GetMapping("/api/user-service/actual/user")
    public Result<UserQueryActualRespDTO> queryActualUserByUsername(@RequestParam("username") @NotEmpty String username) {
        return Results.success(userService.queryActualUserByUsername(username));
    }
    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/api/user-service/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") @NotEmpty String username) {
        return Results.success(userService.hasUsername(username));
    }
    /**
     * 注册用户
     */
    @PostMapping("/api/user-service/register")
    public Result<UserRegisterRespDTO> register(@Valid @RequestBody UserRegisterReqDTO requestParam) {
        return Results.success(userService.register(requestParam));
    }
    /**
     * 修改用户
     */
    @PutMapping("/api/user-service/user")
    public Result<Void> update(@Valid @RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }
    /**
     * 注销用户
     */
    @DeleteMapping("/api/user-service/user")
    public Result<Void> deletion(@Valid @RequestBody UserDeletionReqDTO requestParam) {
        userService.deletion(requestParam);
        return Results.success();
    }
    /**
     * 用户登录
     */
    @PostMapping("/api/user-service/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }
    /**
     * 通过 Token 检查用户是否登录
     */
    @GetMapping("/api/user-service/check-login")
    public Result<UserLoginRespDTO> checkLogin(@RequestParam("accessToken") String accessToken) {
        UserLoginRespDTO result = userService.checkLogin(accessToken);
        return Results.success(result);
    }
    /**
     * 用户退出登录
     */
    @GetMapping("/api/user-service/logout")
    public Result<Void> logout(@RequestParam(required = false) String accessToken) {
        userService.logout(accessToken);
        return Results.success();
    }
}
