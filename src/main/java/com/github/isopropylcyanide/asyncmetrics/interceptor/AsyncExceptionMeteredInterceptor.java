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
package com.github.isopropylcyanide.asyncmetrics.interceptor;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncExceptionMetered;
import com.github.isopropylcyanide.asyncmetrics.exception.AdvisedMethodException;
import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Singleton
@SuppressWarnings("unchecked")
public final class AsyncExceptionMeteredInterceptor implements MethodInterceptor {

    private final MetricRegistry metricRegistry;

    public AsyncExceptionMeteredInterceptor(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String metric = getMetricName(invocation);
        Meter exceptionMeter = metricRegistry.meter(metric);
        CompletableFuture future = (CompletableFuture) invocation.proceed();

        return future.handle((resp, error) -> {
            if (null != error) {
                exceptionMeter.mark();
                throw new AdvisedMethodException(metric, (Throwable) error);
            }
            return resp;
        });
    }

    private String getMetricName(MethodInvocation invocation) {
        String declaringClassName = invocation.getMethod().getDeclaringClass().getName();
        Method method = invocation.getMethod();
        String name = method.getAnnotation(AsyncExceptionMetered.class).name();
        String suffix = method.getAnnotation(AsyncExceptionMetered.class).suffix();

        if (StringUtils.isNotBlank(name)) {
            return MetricRegistry.name(declaringClassName, name, suffix);
        }
        return MetricRegistry.name(declaringClassName, method.getName(), suffix);
    }
}
