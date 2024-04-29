package org.mtf.index12306.biz.userservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.mtf.index12306.biz.userservice.dao.entity.UserPhoneDO;

/**
 * 用户手机号持久层
 */
public interface UserPhoneMapper extends BaseMapper<UserPhoneDO> {
    /**
     * 注销用户
     * @param userPhoneDO 注销用户入参
     */
    void deletionUser(@Param("userPhoneDO") UserPhoneDO userPhoneDO);
}
