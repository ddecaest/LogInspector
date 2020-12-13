package com.loginspector.logging;

import org.apache.logging.log4j.LogManager;

public abstract class LoggerFactory {

    public static Logger createLogger(Class<Object> clazz) {
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(clazz);
        return new LoggerLog4JImpl(logger);
    }
}
