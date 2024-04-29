package org.mtf.index12306.biz.userservice.service;

import org.mtf.index12306.biz.userservice.dto.req.UserDeletionReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserLoginReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserUpdateReqDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserLoginRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryActualRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserRegisterRespDTO;

/**
 * 用户接口层
 */
public interface UserService{
    /**
     * 根据用户名查询用户信息
     * @param username 用户民
     * @return 用户信息响应参数
     */
    UserQueryRespDTO queryUserByUsername(String username);

    /**
     * 根据用户名查询用户无脱敏信息
     * @param username 用户名
     * @return 用户无脱敏信息响应参数
     */
    UserQueryActualRespDTO queryActualUserByUsername(String username);

    /**
     * 判断用户名是否存在
     * @param username 用户名
     * @return true:不存在，用户名可用  false:存在，用户名不可用
     */
    Boolean hasUsername(String username);

    /**
     * 用户注册
     * @param requestParam 用户注册请求参数
     * @return 用户注册响应参数
     */
    UserRegisterRespDTO register(UserRegisterReqDTO requestParam);
    /**
     * 修改用户信息
     * @param requestParam 用户信息修改请求参数
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户注销
     * @param requestParam 用户注销请求参数
     */
    void deletion(UserDeletionReqDTO requestParam);
    /**
     * 用户登录接口
     *
     * @param requestParam 用户登录入参
     * @return 用户登录返回结果
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 通过 Token 检查用户是否登录
     *
     * @param accessToken 用户登录 Token 凭证
     * @return 用户是否登录返回结果
     */
    UserLoginRespDTO checkLogin(String accessToken);

    /**
     * 用户退出登录
     *
     * @param accessToken 用户登录 Token 凭证
     */
    void logout(String accessToken);











    /**
     * 根据证件类型和证件号查询注销次数
     * @param idType 证件类型
     * @param idCard 证件号
     * @return 注销次数
     */
    Integer queryUserDeletionNum(Integer idType, String idCard);



}
