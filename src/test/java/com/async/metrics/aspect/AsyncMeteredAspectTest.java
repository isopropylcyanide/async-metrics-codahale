package com.async.metrics.aspect;

import com.async.metrics.annotation.AsyncMetered;
import com.async.metrics.constant.Properties;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public class AsyncMeteredAspectTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private AsyncMeteredAspect aspect;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.aspect = new AsyncMeteredAspect();
        this.aspect.setMetricRegistry(metricRegistry);
    }

    @Test
    public void testAdviceProceedsNormallyWhenSystemPropertyIsPresent() throws Throwable {
        System.setProperty(Properties.ADVICE_DISABLED, "random");
        aspect.proceed(joinPoint);
        verify(joinPoint, times(1)).proceed();
        System.clearProperty(Properties.ADVICE_DISABLED);
    }

    @AsyncMetered(name = "N1")
    public void testMethodWithAnnotationWithName() {
    }

    @AsyncMetered
    public void testMethodWithAnnotationWithNoName() {
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasName() throws Throwable {
        Class declaringType = this.getClass();
        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = this.getClass().getMethod("testMethodWithAnnotationWithName");
        Meter meter = mock(Meter.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getDeclaringType()).thenReturn(declaringType);
        when(methodSignature.getMethod()).thenReturn(method);
        when(metricRegistry.meter(Mockito.contains("N1"))).thenReturn(meter);

        when(joinPoint.proceed()).thenReturn(CompletableFuture.completedFuture("Done"));
        CompletableFuture<String> proceed = (CompletableFuture) aspect.proceed(joinPoint);

        verify(joinPoint, times(1)).proceed();
        verify(meter, times(1)).mark();
        assertTrue(proceed.isDone());
        assertEquals("Done", proceed.get());
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasNoName() throws Throwable {
        Class declaringType = this.getClass();
        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = this.getClass().getMethod("testMethodWithAnnotationWithNoName");
        Meter meter = mock(Meter.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getDeclaringType()).thenReturn(declaringType);
        when(methodSignature.getMethod()).thenReturn(method);
        when(metricRegistry.meter(Mockito.contains("testMethodWithAnnotationWithNoName"))).thenReturn(meter);

        when(joinPoint.proceed()).thenReturn(CompletableFuture.completedFuture("Done"));
        CompletableFuture<String> proceed = (CompletableFuture) aspect.proceed(joinPoint);

        verify(joinPoint, times(1)).proceed();
        verify(meter, times(1)).mark();
        assertTrue(proceed.isDone());
        assertEquals("Done", proceed.get());
    }
}
