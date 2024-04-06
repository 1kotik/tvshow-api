package com.javaprojects.tvshowapi.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Aspect
@Component
public class AspectLogger {
    private final Logger logger;

    public AspectLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
        try {
            FileHandler fh = new FileHandler("log.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Произошла ошибка при работе с FileHandler.", e);
        }
    }

    @Before("execution(* com.javaprojects.tvshowapi.services.*.*(..))")
    public final void logBeforeServiceCommand(final JoinPoint joinPoint) {
        logger.info(() -> String.format("Running method %s with args %s", joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())));
    }

    @AfterReturning(pointcut = "execution(* com.javaprojects.tvshowapi.services.*.*(..))")
    public final void logAfterServiceCommand(final JoinPoint joinPoint) {
        logger.info(() -> String.format("Result of %s: success ", joinPoint.getSignature().getName()));
    }

    @AfterThrowing(pointcut = "execution(* com.javaprojects.tvshowapi.*.*.*(..))", throwing = "exception")
    public final void logAfterError(final JoinPoint joinPoint, final Exception exception) {
        logger.warning(
                () -> String.format("Error while running %s with args %s: %s", joinPoint.getSignature().getName(),
                        Arrays.toString(joinPoint.getArgs()), exception.getMessage()));
    }
}
