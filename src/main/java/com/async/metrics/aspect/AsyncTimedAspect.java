package com.async.metrics.aspect;

import com.async.metrics.annotation.AsyncTimed;
import com.async.metrics.constant.Properties;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Aspect
@Singleton
public class AsyncTimedAspect {

    @Inject
    private MetricRegistry metricRegistry;

    @Pointcut("@annotation(com.async.metrics.annotation.AsyncTimed)")
    public void asyncTimedAnnotatedMethods() {
    }

    @Around("asyncTimedAnnotatedMethods()")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        if (System.getProperty(Properties.ADVICE_DISABLED) != null) {
            return joinPoint.proceed();
        }
        String metric = getMetricName(joinPoint);
        Timer timer = metricRegistry.timer(metric);
        Timer.Context context = timer.time();

        CompletableFuture future = (CompletableFuture) joinPoint.proceed();
        future.thenRun(context::stop);
        return future;
    }

    private String getMetricName(JoinPoint joinPoint) {
        String declaringClassName = joinPoint.getSignature().getDeclaringType().getName();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String name = method.getAnnotation(AsyncTimed.class).name();
        String suffix = method.getAnnotation(AsyncTimed.class).suffix();

        if (StringUtils.isNotBlank(name)) {
            return MetricRegistry.name(declaringClassName, name, suffix);
        }
        return MetricRegistry.name(declaringClassName, method.getName(), suffix);
    }
}
