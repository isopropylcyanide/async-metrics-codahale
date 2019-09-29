package com.async.metrics.module;

import com.async.metrics.aspect.AsyncExceptionMeteredAspect;
import com.async.metrics.aspect.AsyncTimedAspect;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.aspectj.lang.Aspects;

public class AspectsModule extends AbstractModule {

    private final MetricRegistry metricRegistry;

    public AspectsModule(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    protected void configure() {
        initializeAspects();
    }

    @Provides
    @Singleton
    private MetricRegistry getMetricRegistry() {
        return this.metricRegistry;
    }

    private void initializeAspects() {
        requestInjection(Aspects.aspectOf(AsyncTimedAspect.class));
        requestInjection(Aspects.aspectOf(AsyncExceptionMeteredAspect.class));
    }
}
