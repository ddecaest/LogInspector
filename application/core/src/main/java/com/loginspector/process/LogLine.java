package com.loginspector.process;

import java.time.LocalDateTime;

class LogLine {

    public final LocalDateTime timestamp;
    public final String errorMessage;
    public final LogLevel loglevel;
    public final String className;
    public final String thread;

    public LogLine(LocalDateTime timestamp,
                   String errorMessage,
                   LogLevel loglevel,
                   String className,
                   String thread) {
        this.timestamp = timestamp;
        this.errorMessage = errorMessage;
        this.loglevel = loglevel;
        this.className = className;
        this.thread = thread;
    }

    public static LogLine unstructuredLogLine(String errorMessage) {
        return new LogLine(null, errorMessage, null, null, null);
    }
}
