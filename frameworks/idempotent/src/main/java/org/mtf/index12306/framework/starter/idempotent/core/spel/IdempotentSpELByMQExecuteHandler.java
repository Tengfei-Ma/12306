package org.mtf.index12306.framework.starter.idempotent.core.spel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mtf.index12306.framework.starter.cache.DistributedCache;
import org.mtf.index12306.framework.starter.idempotent.annotation.Idempotent;
import org.mtf.index12306.framework.starter.idempotent.core.*;
import org.mtf.index12306.framework.starter.idempotent.enums.IdempotentMQConsumeStatusEnum;
import org.mtf.index12306.framework.starter.idempotent.toolkit.LogUtil;
import org.mtf.index12306.framework.starter.idempotent.toolkit.SpELUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {

    private final DistributedCache distributedCache;

    private final static int TIMEOUT = 600;
    private final static String WRAPPER = "wrapper:spEL:MQ";

    @SneakyThrows
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        return IdempotentParamWrapper.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        //redis中存储标识该消息消费状态的key
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        //使用setnx命令设置该消息的消费状态为消费中
        Boolean setIfAbsent = ((StringRedisTemplate) distributedCache.getInstance())
                .opsForValue()
                .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), TIMEOUT, TimeUnit.SECONDS);
        //setnx命令执行失败，说明消息已经在消费中或已消费
        if (setIfAbsent != null && !setIfAbsent) {
            String consumeStatus = distributedCache.get(uniqueKey, String.class);
            // 如果该消息在redis中的消费状态是消费中，则抛出重复消费异常，发送延迟消息，过段时间尝试再次消费
            // 如果该消息在redis中的消费状态是已消费，则抛出重复消费异常，直接返回即可
            boolean error = IdempotentMQConsumeStatusEnum.isError(consumeStatus);
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ repeated consumption, {}.", uniqueKey, error ? "Wait for the client to delay consumption" : "Status is completed");
            throw new RepeatConsumptionException(error);
        }
        IdempotentContext.put(WRAPPER, wrapper);
    }

    @Override
    public void exceptionProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.delete(uniqueKey);
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] Failed to del MQ anti-heavy token.", uniqueKey);
            }
        }
    }

    @Override
    public void postProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.put(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), idempotent.keyTimeout(), TimeUnit.SECONDS);
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] Failed to set MQ anti-heavy token.", uniqueKey);
            }
        }
    }
}
