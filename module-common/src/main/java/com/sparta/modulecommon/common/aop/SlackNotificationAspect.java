package com.sparta.modulecommon.common.aop;

import com.sparta.modulecommon.common.service.SlackAlertService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class SlackNotificationAspect {

    private final SlackAlertService slackAlertService;

    @AfterReturning("execution(* com.sparta.fitnus.common.exception.GlobalExceptionHandler.handleRuntimeException(..))")
    public void slackNotificationError(JoinPoint joinPoint) {
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[1];

        slackAlertService.execute(request, (RuntimeException) joinPoint.getArgs()[0]);
    }
}
