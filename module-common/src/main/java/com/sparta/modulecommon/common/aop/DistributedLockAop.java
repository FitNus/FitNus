package com.sparta.modulecommon.common.aop;

import com.sparta.modulecommon.common.annotation.DistributedLock;
import com.sparta.modulecommon.common.distributedlock.AopForTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.sparta.modulecommon.common.distributedlock.SpELParser.getDynamicValue;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.sparta.modulecommon.common.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String lockKey = LOCK_PREFIX + getDynamicValue(signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key());
        RLock lock = redissonClient.getFairLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!isLocked) {
                throw new IllegalStateException("Failed to acquire lock: " + lockKey);
            }

            log.debug("Acquired lock - key: {}", lockKey);
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock interrupted", e);
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.debug("Released lock - key: {}", lockKey);
                }
            } catch (IllegalMonitorStateException e) {
                log.warn("Lock already released - key: {}", lockKey);
            }
        }
    }
}