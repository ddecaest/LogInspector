package com.loginspector.logging;

class LoggerLog4JImpl implements Logger {

    private final org.apache.logging.log4j.Logger delegate;

    public LoggerLog4JImpl(org.apache.logging.log4j.Logger delegate) {
        this.delegate = delegate;
    }

    public void error(String message) {
        delegate.error(message);
    }

    public void error(String message, Object... params) {
        delegate.error(message, params);
    }

    public void warn(String message) {
        delegate.warn(message);
    }

    public void warn(String message, Object... params) {
        delegate.warn(message, params);
    }
}
