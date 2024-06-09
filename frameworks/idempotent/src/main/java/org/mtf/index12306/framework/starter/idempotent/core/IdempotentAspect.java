package org.mtf.index12306.framework.starter.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mtf.index12306.framework.starter.idempotent.annotation.Idempotent;

import java.lang.reflect.Method;

/**
 * 幂等注解 AOP 拦截器
 */
@Aspect
public final class IdempotentAspect {

    /**
     * 增强方法标记 {@link Idempotent} 注解逻辑
     */
    @Around("@annotation(org.mtf.index12306.framework.starter.idempotent.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Idempotent idempotent = getIdempotent(joinPoint);
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent);
            resultObj = joinPoint.proceed();
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            //此次消费为重复消费，redis中消息状态是已消费，直接返回即可
            if (!ex.getError()) {
                return null;
            }
            //此次消费为重复消费，redis中消息状态是消费中，等待 RocketMQ 再次通过重试队列投递
            throw ex;
        } catch (Throwable ex) {
            // 消费业务逻辑抛出异常，删除幂等标识，等待 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
