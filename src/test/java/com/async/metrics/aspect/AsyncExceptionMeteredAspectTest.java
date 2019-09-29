package com.async.metrics.aspect;

import com.async.metrics.annotation.AsyncExceptionMetered;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public class AsyncExceptionMeteredAspectTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private AsyncExceptionMeteredAspect aspect;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.aspect = new AsyncExceptionMeteredAspect();
        this.aspect.setMetricRegistry(metricRegistry);
    }

    @Test
    public void testAdviceProceedsNormallyWhenSystemPropertyIsPresent() throws Throwable {
        System.setProperty(Properties.ADVICE_DISABLED, "R1");
        aspect.proceed(joinPoint);
        verify(joinPoint, times(1)).proceed();
        System.clearProperty(Properties.ADVICE_DISABLED);
    }

    @AsyncExceptionMetered(name = "N1")
    public void testMethodWithAnnotationWithName() {
    }

    @AsyncExceptionMetered
    public void testMethodWithAnnotationWithNoName() {
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasNameAndFutureCompletes() throws Throwable {
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
        verifyZeroInteractions(meter);
        assertTrue(proceed.isDone());
        assertEquals("Done", proceed.get());
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasNoNameAndFutureFails() throws Throwable {
        Class declaringType = this.getClass();
        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = this.getClass().getMethod("testMethodWithAnnotationWithNoName");
        Meter meter = mock(Meter.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getDeclaringType()).thenReturn(declaringType);
        when(methodSignature.getMethod()).thenReturn(method);
        when(metricRegistry.meter(Mockito.contains("testMethodWithAnnotationWithNoName"))).thenReturn(meter);

        CompletableFuture failedFuture = new CompletableFuture();
        failedFuture.completeExceptionally(new NullPointerException("Expected error"));

        when(joinPoint.proceed()).thenReturn(failedFuture);
        CompletableFuture<String> proceed = (CompletableFuture) aspect.proceed(joinPoint);

        verify(joinPoint, times(1)).proceed();
        verify(meter, times(1)).mark();
        assertTrue(proceed.isDone());
    }
}
