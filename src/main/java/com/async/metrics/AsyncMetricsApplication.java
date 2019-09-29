package com.async.metrics;

import com.async.metrics.annotation.AsyncExceptionMetered;
import com.async.metrics.annotation.AsyncTimed;
import com.async.metrics.module.AspectsModule;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Guice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AsyncMetricsApplication {

    private static final Logger log = LoggerFactory.getLogger(AsyncMetricsApplication.class);

    @AsyncTimed
    @AsyncExceptionMetered
    CompletableFuture<Integer> asyncMethodThatCompletesNormally() {
        return CompletableFuture.supplyAsync(() -> StringUtils.split(getClass().getName(), "."))
                .thenApply(s -> s.length);
    }

    @AsyncTimed
    @AsyncExceptionMetered
    CompletableFuture<Integer> asyncMethodThatCompletesExceptionally() {
        CompletableFuture<Integer> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new Exception());
        return failedFuture;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        Guice.createInjector(new AspectsModule(metricRegistry));

        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start();
//        reporter.start(5, TimeUnit.SECONDS);

        AsyncMetricsApplication application = new AsyncMetricsApplication();

        application.asyncMethodThatCompletesNormally().thenAccept(val -> log.info("Normal Output {}", val)).get();
        application.asyncMethodThatCompletesExceptionally()
                .handle((res, ex) -> {
                    if (ex != null) {
                        log.info("Erroneous output encountered ", ex);
                        return 0;
                    }
                    return res;
                }).thenAccept(val -> log.info("Normal Output {}", val)).get();

        while (true) {
            Thread.sleep(15000);
        }
    }

}
