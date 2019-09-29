package com.async.metrics;

import com.async.metrics.module.AspectsModule;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Guice;

public class AsyncMetricsApplication {

    public static void main(String[] args) {
        MetricRegistry metricRegistry = new MetricRegistry();
        Guice.createInjector(new AspectsModule(metricRegistry));
        System.out.println("hi");
    }

}
