package org.sopt36.ninedotserver.global.logging;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    @Around("execution(* org.sopt36.ninedotserver..service.*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable {
        String signature = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            log.info("{} success took={}ms args={}", signature,
                System.currentTimeMillis() - start,
                Arrays.toString(redact(pjp.getArgs())));
            return result;
        } catch (Exception e) {
            log.warn("{} failed took={}ms args={} error={}", signature,
                System.currentTimeMillis() - start,
                Arrays.toString(redact(pjp.getArgs())), e.toString());
            throw e;
        }
    }

    private Object[] redact(Object[] args) {
        return Arrays.stream(args)
            .map(a -> (a instanceof String s && s.toLowerCase().contains("token")) ? "***" : a)
            .toArray();
    }
}

