package org.sopt36.ninedotserver.global.logging;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    private static final List<String> SENSITIVE_KEYS = List.of(
        "password", "passwd", "pwd",
        "secret", "token",
        "apikey", "api_key",
        "authorization", "auth",
        "bearer", "refresh", "access"
    );
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile(
        "(?i)\\b(" + String.join("|", SENSITIVE_KEYS) + ")\\b\\s*[:=]\\s*[^,\\s]+"
    );

    @Around("execution(* org.sopt36.ninedotserver..service..*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable {
        String signature = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            log.info("{} success took={}ms args={}", signature,
                System.currentTimeMillis() - start,
                Arrays.toString(redact(pjp.getArgs())));
            return result;
        } catch (Throwable e) {
            log.warn("{} failed took={}ms args={} error={}", signature,
                System.currentTimeMillis() - start,
                Arrays.toString(redact(pjp.getArgs())), e.toString());
            throw e;
        }
    }

    private Object[] redact(Object[] args) {
        return Arrays.stream(args)
            .map(this::redactArg)
            .toArray();
    }

    private Object redactArg(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Map<?, ?> map) {
            return redactMap(map);
        }
        if (arg instanceof String s) {
            return redactString(s);
        }
        return arg;
    }

    private Map<Object, Object> redactMap(Map<?, ?> map) {
        Map<Object, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String keyStr && isSensitiveKey(keyStr)) {
                result.put(key, "***");
            } else {
                result.put(key, redactArg(value));
            }
        }
        return result;
    }

    private String redactString(String value) {
        if (!KEY_VALUE_PATTERN.matcher(value).find()) {
            return value;
        }
        return KEY_VALUE_PATTERN.matcher(value).replaceAll("$1=***");
    }

    private boolean isSensitiveKey(String key) {
        String lowerKey = key.toLowerCase();
        return SENSITIVE_KEYS.stream().anyMatch(lowerKey::contains);
    }
}
