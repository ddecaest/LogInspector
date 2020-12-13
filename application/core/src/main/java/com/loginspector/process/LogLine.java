package com.loginspector.process;

import java.time.LocalDateTime;

class LogLine {

    private enum Type {
        STRUCTURED,
        UNSTRUCTURED
    }

    public final LocalDateTime timestamp;
    public final String message;
    public final LogLevel loglevel;
    public final String className;
    public final String thread;
    private final Type type;

    private LogLine(LocalDateTime timestamp,
                   String message,
                   LogLevel loglevel,
                   String className,
                   String thread,
                   Type type
    ) {
        this.timestamp = timestamp;
        this.message = message;
        this.loglevel = loglevel;
        this.className = className;
        this.thread = thread;
        this.type = type;
    }

    public static LogLine structuredLogLine(LocalDateTime timestamp,
                                            String message,
                                            LogLevel loglevel,
                                            String className,
                                            String thread
    ) {
        return new LogLine(timestamp, message, loglevel, className, thread, Type.STRUCTURED);
    }

    public static LogLine unstructuredLogLine(String message) {
        return new LogLine(null, message, null, null, null, Type.UNSTRUCTURED);
    }

    public boolean isStructuredLogLine() {
        return this.type == Type.STRUCTURED;
    }
}
