package com.pingguard.exception;

public class MonitorLimitExceededException extends RuntimeException {
    public MonitorLimitExceededException(String message) {
        super(message);
    }
}
