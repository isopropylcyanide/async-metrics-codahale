package com.async.metrics.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvisedMethodException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(AdvisedMethodException.class);

    public AdvisedMethodException(String methodName, Throwable throwable) {
        super(throwable);
        log.error("Exception in advised method: {} : {}", methodName, throwable.getMessage());
    }
}
