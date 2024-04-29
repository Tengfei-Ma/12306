package org.mtf.index12306.biz.userservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mtf.index12306.biz.userservice.dao.entity.UserDO;

/**
 * 用户信息持久层
 */
public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * 注销用户
     * @param userDO 注销用户入参
     */
    void deletionUser(@Param("userDO") UserDO userDO);
}
