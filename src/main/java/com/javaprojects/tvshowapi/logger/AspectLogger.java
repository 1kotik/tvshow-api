package com.javaprojects.tvshowapi.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
@Component
public class AspectLogger {
    private final Logger logger;

    public AspectLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Before("execution(* com.javaprojects.tvshowapi.services.*.*(..))")
    public void logBeforeServiceCommand(JoinPoint joinPoint) {
        logger.info(() -> String.format("Method %s with args %s", joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())));
    }

    @AfterReturning(pointcut = "execution(* com.javaprojects.tvshowapi.services.*.*(..))", returning = "result")
    public void logAfterServiceCommand(JoinPoint joinPoint, Object result) {
            logger.info(() -> String.format("Result of %s with args %s: %s", joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()), result));
    }

    @AfterThrowing(pointcut = "execution(* com.javaprojects.tvshowapi.*.*.*(..))", throwing = "exception")
    public void logAfterError(JoinPoint joinPoint, Exception exception) {
        logger.warning(
                () -> String.format("Error while running %s with args %s: %s", joinPoint.getSignature().getName(),
                        Arrays.toString(joinPoint.getArgs()), exception.getMessage()));
    }
}
