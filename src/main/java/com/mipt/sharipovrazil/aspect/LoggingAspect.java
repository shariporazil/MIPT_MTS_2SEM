package com.mipt.sharipovrazil.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Начало выполнения метода {}.{}()", className, methodName);
        if (args.length > 0) {
            logger.info("Аргументы: {}", Arrays.toString(args));
        }

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            if (result != null) {
                logger.info("Результат: {}", result);
            } else {
                logger.info("Результат: void");
            }

            logger.info("Завершение метода {}.{}() за {} мс",
                    className, methodName, (endTime - startTime));

            return result;
        } catch (Exception e) {
            logger.error("Ошибка в методе {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}