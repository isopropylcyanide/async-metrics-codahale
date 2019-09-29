/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.async.metrics.interceptor;

import com.async.metrics.annotation.AsyncTimed;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aopalliance.intercept.MethodInvocation;
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
public class AsyncTimedAspectTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private MethodInvocation invocation;

    private AsyncTimedInterceptor interceptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.interceptor = new AsyncTimedInterceptor(metricRegistry);
    }

    @AsyncTimed(name = "N1")
    public void testMethodWithAnnotationWithName() {
    }

    @AsyncTimed
    public void testMethodWithAnnotationWithNoName() {
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasName() throws Throwable {
        Method method = this.getClass().getMethod("testMethodWithAnnotationWithName");
        Timer timer = mock(Timer.class);
        Timer.Context context = mock(Timer.Context.class);

        when(invocation.getMethod()).thenReturn(method);
        when(metricRegistry.timer(Mockito.contains("N1"))).thenReturn(timer);
        when(timer.time()).thenReturn(context);

        when(invocation.proceed()).thenReturn(CompletableFuture.completedFuture("Done"));
        CompletableFuture<String> proceed = (CompletableFuture) interceptor.invoke(invocation);

        verify(invocation, times(1)).proceed();
        verify(context, times(1)).stop();
        assertTrue(proceed.isDone());
        assertEquals("Done", proceed.get());
    }

    @Test
    public void testAdviceMarksMetricsWhenSystemPropertyIsNotPresentAndAnnotationHasNoName() throws Throwable {
        Method method = this.getClass().getMethod("testMethodWithAnnotationWithNoName");
        Timer timer = mock(Timer.class);
        Timer.Context context = mock(Timer.Context.class);

        when(invocation.getMethod()).thenReturn(method);
        when(metricRegistry.timer(Mockito.contains("testMethodWithAnnotationWithNoName"))).thenReturn(timer);
        when(timer.time()).thenReturn(context);

        when(invocation.proceed()).thenReturn(CompletableFuture.completedFuture("Done"));
        CompletableFuture<String> proceed = (CompletableFuture) interceptor.invoke(invocation);

        verify(invocation, times(1)).proceed();
        verify(context, times(1)).stop();
        assertTrue(proceed.isDone());
        assertEquals("Done", proceed.get());
    }
}
