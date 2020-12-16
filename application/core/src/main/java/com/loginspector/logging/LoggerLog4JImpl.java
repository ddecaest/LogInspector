package com.loginspector.logging;

class LoggerLog4JImpl implements Logger {

    private final org.apache.logging.log4j.Logger delegate;

    public LoggerLog4JImpl(org.apache.logging.log4j.Logger delegate) {
        this.delegate = delegate;
    }

    public void error(String message) {
        delegate.error(message);
    }

    @Override
    public void error(String message, Exception e) {
        delegate.error(message, e);
    }

    public void error(String message, Object... params) {
        delegate.error(message, params);
    }

    public void warn(String message) {
        delegate.warn(message);
    }

    @Override
    public void info(String message, Object... params) {
        delegate.info(message, params);
    }

    public void warn(String message, Object... params) {
        delegate.warn(message, params);
    }
}
