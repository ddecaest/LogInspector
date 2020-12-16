package com.loginspector.logfile;

import java.time.LocalDateTime;

public class LogLine {

    private enum Type {
        STRUCTURED,
        UNSTRUCTURED
    }

    public final LocalDateTime timestamp;
    public final LogLevel loglevel;
    public final String className;
    public final String message;
    public final String thread;
    public final int lineNumber;
    private final Type type;

    private LogLine(LocalDateTime timestamp,
                    String message,
                    LogLevel loglevel,
                    String className,
                    String thread,
                    Type type,
                    int lineNumber
    ) {
        this.timestamp = timestamp;
        this.message = message;
        this.loglevel = loglevel;
        this.className = className;
        this.thread = thread;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public static LogLine structuredLogLine(LocalDateTime timestamp,
                                            String message,
                                            LogLevel loglevel,
                                            String className,
                                            String thread,
                                            int lineNumber
    ) {
        return new LogLine(timestamp, message, loglevel, className, thread, Type.STRUCTURED, lineNumber);
    }

    public static LogLine unstructuredLogLine(String message, int lineNumber) {
        return new LogLine(null, message, null, null, null, Type.UNSTRUCTURED, lineNumber);
    }

    public boolean isStructuredLogLine() {
        return this.type == Type.STRUCTURED;
    }
}
