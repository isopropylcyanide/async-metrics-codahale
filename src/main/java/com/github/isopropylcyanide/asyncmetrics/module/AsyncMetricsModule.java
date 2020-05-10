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
package com.github.isopropylcyanide.asyncmetrics.module;

import com.codahale.metrics.MetricRegistry;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncExceptionMetered;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncMetered;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncTimed;
import com.github.isopropylcyanide.asyncmetrics.interceptor.AsyncExceptionMeteredInterceptor;
import com.github.isopropylcyanide.asyncmetrics.interceptor.AsyncMeteredInterceptor;
import com.github.isopropylcyanide.asyncmetrics.interceptor.AsyncTimedInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

@SuppressWarnings("WeakerAccess")
public class AsyncMetricsModule extends AbstractModule {

    private final MetricRegistry metricRegistry;

    public AsyncMetricsModule(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(AsyncTimed.class), new AsyncTimedInterceptor(metricRegistry));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(AsyncMetered.class), new AsyncMeteredInterceptor(metricRegistry));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(AsyncExceptionMetered.class), new AsyncExceptionMeteredInterceptor(metricRegistry));
    }
}
