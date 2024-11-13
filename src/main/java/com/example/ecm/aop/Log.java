package com.example.ecm.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class Log {
    private final ObjectMapper mapper;

    @Pointcut("@annotation(Loggable)")
    public void logExecutionTimeMethod() {
    }

    @Pointcut("@within(Loggable)")
    public void logExecutionTimeClass() {
    }

    @Around("logExecutionTimeMethod() || logExecutionTimeClass()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();


        long executionTime = System.currentTimeMillis() - start;

        log.info("Method {} in class {} executed in {} ms with args {}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                executionTime,
                mapper.writeValueAsString(joinPoint.getArgs()));

        return proceed;
    }
}

