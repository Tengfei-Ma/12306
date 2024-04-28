package org.mtf.index12306.framework.starter.idempotent.config;

import lombok.Data;
import org.mtf.index12306.framework.starter.cache.config.RedisDistributedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 幂等属性配置
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
@ConfigurationProperties(prefix = IdempotentProperties.PREFIX)
public class IdempotentProperties {

    public static final String PREFIX = "congomall.idempotent.token";

    /**
     * Token 幂等 Key 前缀
     */
    private String prefix;

    /**
     * Token 申请后过期时间
     * 单位默认毫秒 {@link TimeUnit#MILLISECONDS}
     * 随着分布式缓存过期时间单位 {@link RedisDistributedProperties#valueTimeUnit} 而变化
     */
    private Long timeout;
}