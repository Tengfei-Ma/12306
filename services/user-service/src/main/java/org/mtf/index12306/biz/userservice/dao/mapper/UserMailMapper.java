package org.mtf.index12306.biz.userservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.mtf.index12306.biz.userservice.dao.entity.UserMailDO;

/**
 * 用户邮箱持久层
 */
public interface UserMailMapper extends BaseMapper<UserMailDO> {
    /**
     * 注销用户
     * @param userMailDO 注销用户入参
     */
    void deletionUser(@Param("userMailDO")UserMailDO userMailDO);
}
