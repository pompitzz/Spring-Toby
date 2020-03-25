package ch5;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/25
 */
@Component
@Aspect
public class SimpleMonitoringAspect {

    @Pointcut("execution(* hello(..))")
    private void all(){ }

    @Around("all()")
    public Object printParametersAndReturnVal(ProceedingJoinPoint pjp) throws Throwable {
        Object ret = pjp.proceed();
        return ret;
    }
}
