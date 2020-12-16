package com.loginspector.logging;

public interface Logger {

    void error(String message, Exception e);
    void warn(String message, Object... params);
    void info(String message, Object... params);
}
