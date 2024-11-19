package com.sparta.common.aop;

import com.sparta.common.annotation.DistributedLock;
import com.sparta.common.distributedlock.AopForTransaction;
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

import static com.sparta.common.distributedlock.SpELParser.getDynamicValue;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.sparta.common.annotation.DistributedLock)")
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

            // 트랜잭션을 시작하고, 비즈니스 로직을 수행
            Object result = aopForTransaction.proceed(joinPoint);

            // 트랜잭션이 성공적으로 완료된 후에 락을 해제
            return result;

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