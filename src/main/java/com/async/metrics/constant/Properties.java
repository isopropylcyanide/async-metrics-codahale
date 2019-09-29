package com.async.metrics.constant;

public class Properties {

    private Properties() {
        throw new IllegalStateException("Cannot initialise Utility class " + getClass().getName());
    }

    /**
     * This is a constant string whose value is used to identify a test environment. Ideally, while running
     * unit tests on methods advised by advices, we do not want to test the woven code. The aspect will only
     * execute if the property is not set
     */
    public static final String ADVICE_DISABLED = "advice_disabled";
}

