package com.async.metrics.aspect;

import com.async.metrics.annotation.AsyncExceptionMetered;
import com.async.metrics.constant.Properties;
import com.async.metrics.exception.AdvisedMethodException;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
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
@SuppressWarnings("unchecked")
public class AsyncExceptionMeteredAspect {

    @Inject
    private MetricRegistry metricRegistry;

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Pointcut("@annotation(com.async.metrics.annotation.AsyncExceptionMetered)")
    public void asyncExceptionMeteredAnnotatedMethods() {
    }

    @Around("asyncExceptionMeteredAnnotatedMethods()")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        if (System.getProperty(Properties.ADVICE_DISABLED) != null) {
            return joinPoint.proceed();
        }
        String metric = getMetricName(joinPoint);
        Meter exceptionMeter = metricRegistry.meter(metric);
        CompletableFuture future = (CompletableFuture) joinPoint.proceed();

        return future.handle((resp, error) -> {
            if (null != error) {
                exceptionMeter.mark();
                throw new AdvisedMethodException(metric, (Throwable) error);
            }
            return resp;
        });
    }

    private String getMetricName(JoinPoint joinPoint) {
        String declaringClassName = joinPoint.getSignature().getDeclaringType().getName();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String name = method.getAnnotation(AsyncExceptionMetered.class).name();
        String suffix = method.getAnnotation(AsyncExceptionMetered.class).suffix();

        if (StringUtils.isNotBlank(name)) {
            return MetricRegistry.name(declaringClassName, name, suffix);
        }
        return MetricRegistry.name(declaringClassName, method.getName(), suffix);
    }
}
