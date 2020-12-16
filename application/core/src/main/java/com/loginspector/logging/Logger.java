package com.loginspector.logging;

public interface Logger {

    // TODO Clean up in interface
    void error(String message, Exception e);
    void error(String message, Object... params);
    void error(String message);

    void warn(String message, Object... params);
    void warn(String message);

    void info(String message, Object... params);
}
