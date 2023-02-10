package com.pls.core.aspect;

import java.util.concurrent.atomic.AtomicLong;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for logging restful and dao methods execution time.
 * 
 * @author Aleksandr Leshchenko
 */
@Aspect
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    private static volatile AtomicLong id = new AtomicLong(1);
    private static final ThreadLocal<Long> LOCAL_ID = new ThreadLocal<Long>();

    /**
     * Logs restful and dao methods execution time.
     * 
     * @param pjp
     *            method execution.
     * @return value that pjp is returning.
     * @throws Throwable
     *             that pjp is throwing.
     */
    @Around("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.stereotype.Repository *) "
            + "|| within(@org.springframework.stereotype.Service *) || within(com.pls..dao..*)")
    public Object logResourceExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getSignature().getDeclaringTypeName().contains(".restful.")) {
            LOCAL_ID.set(id.getAndIncrement());
        } else if (LOCAL_ID.get() == null || LOCAL_ID.get() == 0L) {
            LOCAL_ID.set(id.getAndIncrement());
        }
        long executionStartTime = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        long executionEndTime = System.currentTimeMillis();
        LOGGER.info("LOCAL_ID='{}' Method {} has been executing {} milliseconds", LOCAL_ID.get(), pjp.getSignature(),
                executionEndTime - executionStartTime);
        return retVal;
    }
}