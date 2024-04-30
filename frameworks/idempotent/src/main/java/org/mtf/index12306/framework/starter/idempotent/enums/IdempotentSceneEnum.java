package org.mtf.index12306.framework.starter.idempotent.enums;

/**
 * 幂等验证场景枚举
 */
public enum IdempotentSceneEnum {
    
    /**
     * 基于 RestAPI 场景验证，防止重复提交
     */
    RESTAPI,
    
    /**
     * 基于 MQ 场景验证，防止消息重复消费
     */
    MQ
}
