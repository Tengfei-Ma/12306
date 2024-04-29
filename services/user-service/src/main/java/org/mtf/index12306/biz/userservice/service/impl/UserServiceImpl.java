package org.mtf.index12306.biz.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtf.index12306.biz.userservice.dao.entity.*;
import org.mtf.index12306.biz.userservice.dao.mapper.*;
import org.mtf.index12306.biz.userservice.dto.req.UserDeletionReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserLoginReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.mtf.index12306.biz.userservice.dto.req.UserUpdateReqDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserLoginRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryActualRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserQueryRespDTO;
import org.mtf.index12306.biz.userservice.dto.resp.UserRegisterRespDTO;
import org.mtf.index12306.biz.userservice.service.UserService;
import org.mtf.index12306.framework.starter.cache.DistributedCache;
import org.mtf.index12306.framework.starter.common.toolkit.BeanUtil;
import org.mtf.index12306.framework.starter.convention.exception.ClientException;
import org.mtf.index12306.framework.starter.convention.exception.ServiceException;
import org.mtf.index12306.framework.starter.designpattern.chain.AbstractChainContext;
import org.mtf.index12306.framework.starter.user.core.UserContext;
import org.mtf.index12306.framework.starter.user.core.UserInfoDTO;
import org.mtf.index12306.framework.starter.user.toolkit.JWTUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mtf.index12306.biz.userservice.common.constant.RedisKeyConstant.*;
import static org.mtf.index12306.biz.userservice.common.enums.UserChainMarkEnum.USER_REGISTER_FILTER;
import static org.mtf.index12306.biz.userservice.common.enums.UserRegisterErrorCodeEnum.*;
import static org.mtf.index12306.biz.userservice.toolkit.UserReuseUtil.hashShardingIdx;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserPhoneMapper userPhoneMapper;
    private final UserMailMapper userMailMapper;
    private final UserReuseMapper userReuseMapper;
    private final UserDeletionMapper userDeletionMapper;

    private final RedissonClient redissonClient;
    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final DistributedCache distributedCache;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Override
    public UserQueryRespDTO queryUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在，请检查用户名是否正确!");
        }
        return BeanUtil.convert(userDO, UserQueryRespDTO.class);
    }

    @Override
    public UserQueryActualRespDTO queryActualUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在，请检查用户名是否正确!");
        }
        return BeanUtil.convert(userDO, UserQueryActualRespDTO.class);
    }

    @Override
    public Boolean hasUsername(String username) {
        boolean hasUsername = userRegisterCachePenetrationBloomFilter.contains(username);
        if(hasUsername){
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            return instance.opsForSet().isMember(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserRegisterRespDTO register(UserRegisterReqDTO requestParam) {

        abstractChainContext.handler(USER_REGISTER_FILTER.name(),requestParam);
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER+requestParam.getUsername());
        boolean tryLock = lock.tryLock();
        if(!tryLock){
            throw new ServiceException(HAS_USERNAME_NOTNULL);
        }
        try {
            try {
                int inserted = userMapper.insert(BeanUtil.convert(requestParam, UserDO.class));
                if (inserted < 1) {
                    throw new ServiceException(USER_REGISTER_FAIL);
                }
            } catch (DuplicateKeyException dke) {
                log.error("用户名 [{}] 重复注册", requestParam.getUsername());
                throw new ServiceException(HAS_USERNAME_NOTNULL);
            }
            UserPhoneDO userPhoneDO = new UserPhoneDO();
            userPhoneDO.setPhone(requestParam.getPhone());
            userPhoneDO.setUsername(requestParam.getUsername());
            try {
                userPhoneMapper.insert(userPhoneDO);
            } catch (DuplicateKeyException dke) {
                log.error("用户 [{}] 注册手机号 [{}] 重复", requestParam.getUsername(), requestParam.getPhone());
                throw new ServiceException(PHONE_REGISTERED);
            }
            if (StrUtil.isNotBlank(requestParam.getMail())) {
                UserMailDO userMailDO =new UserMailDO();
                userMailDO.setMail(requestParam.getMail());
                userMailDO.setUsername(requestParam.getUsername());
                try {
                    userMailMapper.insert(userMailDO);
                } catch (DuplicateKeyException dke) {
                    log.error("用户 [{}] 注册邮箱 [{}] 重复", requestParam.getUsername(), requestParam.getMail());
                    throw new ServiceException(MAIL_REGISTERED);
                }
            }
            String username= requestParam.getUsername();
            UserReuseDO userReuseDO = new UserReuseDO();
            userReuseDO.setUsername(username);
            userReuseMapper.delete(Wrappers.update(userReuseDO));
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.opsForSet().remove(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
            userRegisterCachePenetrationBloomFilter.add(username);
        }
        finally {
            lock.unlock();
        }
        return BeanUtil.convert(requestParam,UserRegisterRespDTO.class);
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        UserQueryRespDTO userQueryRespDTO = queryUserByUsername(requestParam.getUsername());
        UserDO userDO = BeanUtil.convert(requestParam, UserDO.class);
        LambdaUpdateWrapper<UserDO> userUpdateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        userMapper.update(userDO, userUpdateWrapper);
        if (StrUtil.isNotBlank(requestParam.getMail()) && !Objects.equals(requestParam.getMail(), userQueryRespDTO.getMail())) {
            LambdaUpdateWrapper<UserMailDO> updateWrapper = Wrappers.lambdaUpdate(UserMailDO.class)
                    .eq(UserMailDO::getMail, userQueryRespDTO.getMail());
            userMailMapper.delete(updateWrapper);
            UserMailDO userMailDO =new  UserMailDO();
            userMailDO.setMail(requestParam.getMail());
            userMailDO.setUsername(requestParam.getUsername());
            userMailMapper.insert(userMailDO);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletion(UserDeletionReqDTO requestParam) {
        String username = UserContext.getUsername();
        if (!Objects.equals(username, requestParam.getUsername())) {
            // 此处严谨来说，需要上报风控中心进行异常检测
            throw new ClientException("注销账号与登录账号不一致");
        }
        RLock lock = redissonClient.getLock(USER_DELETION + requestParam.getUsername());
        // 加锁为什么放在 try 语句外？https://www.yuque.com/magestack/12306/pu52u29i6eb1c5wh
        lock.lock();
        try {
            UserQueryRespDTO userQueryRespDTO = queryUserByUsername(username);
            UserDeletionDO userDeletionDO =new UserDeletionDO();
            userDeletionDO.setIdType(userQueryRespDTO.getIdType());
            userDeletionDO.setIdCard(userQueryRespDTO.getIdCard());
            userDeletionMapper.insert(userDeletionDO);
            UserDO userDO = new UserDO();
            userDO.setDeletionTime(System.currentTimeMillis());
            userDO.setUsername(username);
            // MyBatis Plus 不支持修改语句变更 del_flag 字段
            userMapper.deletionUser(userDO);
            UserPhoneDO userPhoneDO = new UserPhoneDO();
            userPhoneDO.setPhone(userQueryRespDTO.getPhone());
            userPhoneDO.setDeletionTime(System.currentTimeMillis());
            userPhoneMapper.deletionUser(userPhoneDO);
            if (StrUtil.isNotBlank(userQueryRespDTO.getMail())) {
                UserMailDO userMailDO =new UserMailDO();
                userMailDO.setMail(userQueryRespDTO.getMail());
                userMailDO.setDeletionTime(System.currentTimeMillis());
                userMailMapper.deletionUser(userMailDO);
            }
            distributedCache.delete(UserContext.getToken());
            UserReuseDO userReuseDO = new UserReuseDO();
            userReuseDO.setUsername(username);
            userReuseMapper.insert(userReuseDO);
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.opsForSet().add(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        String usernameOrMailOrPhone = requestParam.getUsernameOrMailOrPhone();
        boolean mailFlag = false;
        // 时间复杂度最佳 O(1)。indexOf or contains 时间复杂度为 O(n)
        for (char c : usernameOrMailOrPhone.toCharArray()) {
            if (c == '@') {
                mailFlag = true;
                break;
            }
        }
        String username;
        if (mailFlag) {
            LambdaQueryWrapper<UserMailDO> queryWrapper = Wrappers.lambdaQuery(UserMailDO.class)
                    .eq(UserMailDO::getMail, usernameOrMailOrPhone);
            username = Optional.ofNullable(userMailMapper.selectOne(queryWrapper))
                    .map(UserMailDO::getUsername)
                    .orElseThrow(() -> new ClientException("用户名/手机号/邮箱不存在"));
        } else {
            LambdaQueryWrapper<UserPhoneDO> queryWrapper = Wrappers.lambdaQuery(UserPhoneDO.class)
                    .eq(UserPhoneDO::getPhone, usernameOrMailOrPhone);
            username = Optional.ofNullable(userPhoneMapper.selectOne(queryWrapper))
                    .map(UserPhoneDO::getUsername)
                    .orElse(null);
        }
        username = Optional.ofNullable(username).orElse(requestParam.getUsernameOrMailOrPhone());
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getPassword, requestParam.getPassword())
                .select(UserDO::getId, UserDO::getUsername, UserDO::getRealName);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO != null) {
            UserInfoDTO userInfo = UserInfoDTO.builder()
                    .userId(String.valueOf(userDO.getId()))
                    .username(userDO.getUsername())
                    .realName(userDO.getRealName())
                    .build();
            String accessToken = JWTUtil.generateAccessToken(userInfo);
            //TODO 重复登陆问题
            UserLoginRespDTO actual = new UserLoginRespDTO();
            actual.setUserId(userInfo.getUserId());
            actual.setUsername(requestParam.getUsernameOrMailOrPhone());
            actual.setRealName(userDO.getRealName());
            actual.setAccessToken(accessToken);
            distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);
            return actual;
        }
        throw new ServiceException("账号不存在或密码错误");
    }

    @Override
    public UserLoginRespDTO checkLogin(String accessToken) {
        return distributedCache.get(accessToken, UserLoginRespDTO.class);
    }

    @Override
    public void logout(String accessToken) {
        if (StrUtil.isNotBlank(accessToken)) {
            distributedCache.delete(accessToken);
        }
    }

    @Override
    public Integer queryUserDeletionNum(Integer idType, String idCard) {
        LambdaQueryWrapper<UserDeletionDO> queryWrapper = Wrappers.lambdaQuery(UserDeletionDO.class)
                .eq(UserDeletionDO::getIdType, idType)
                .eq(UserDeletionDO::getIdCard, idCard);
        // TODO 此处应该先查缓存
        Long deletionCount = userDeletionMapper.selectCount(queryWrapper);
        return Optional.ofNullable(deletionCount).map(Long::intValue).orElse(0);
    }
}
