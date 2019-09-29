package com.async.metrics.util;

import com.async.metrics.constant.Properties;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestEnvironmentStarter extends TestWatcher {

    @Override
    protected void starting(Description description) {
        System.setProperty(Properties.ADVICE_DISABLED, "true");
    }

    @Override
    protected void finished(Description description) {
        System.clearProperty(Properties.ADVICE_DISABLED);
    }
}
