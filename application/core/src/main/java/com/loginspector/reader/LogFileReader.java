package com.loginspector.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LogFileReader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
    private static final String LOG_LINE_SEPARATOR = " ";

    private final BufferedReader reader;

    public LogFileReader(InputStream streamToLogFile) {
        reader = new BufferedReader(new InputStreamReader(streamToLogFile));
    }

    public Optional<LogLine> readLine() throws IOException {
        String line = reader.readLine();
        if(line == null) {
            return Optional.empty();
        }
        return parse(line);
    }

    private Optional<LogLine> parse(String line) {
        String[] split = line.split(LOG_LINE_SEPARATOR);
        // TODO: validate expected number of values

        LocalDateTime zonedDateTime = LocalDateTime.parse(split[0], DATE_TIME_FORMATTER);
        String thread = split[1].replace("[", "");
        LogLine.LogLevel logLevel = parseLogLevel(split[2]);
        String className = split[3].replace("[", "");

        // TODO: all the rest is the errorMessage
        String errorMessage = "";

        return Optional.of(new LogLine(zonedDateTime, errorMessage, logLevel, className, thread));
    }

    private LogLine.LogLevel parseLogLevel(String rawLogLevel) {
        try {
            return LogLine.LogLevel.valueOf(rawLogLevel);
        } catch(IllegalArgumentException e) {
            // TODO log warning
            return LogLine.LogLevel.UNKNOWN;
        }
    }


    static class LogLine {

        public final LocalDateTime zonedDateTime;
        public final String errorMessage;
        public final LogLevel loglevel;
        public final String className;
        public final String thread;

        public LogLine(LocalDateTime zonedDateTime,
                       String errorMessage,
                       LogLevel loglevel,
                       String className,
                       String thread) {
            this.zonedDateTime = zonedDateTime;
            this.errorMessage = errorMessage;
            this.loglevel = loglevel;
            this.className = className;
            this.thread = thread;
        }

        enum LogLevel {
            UNKNOWN,
            DEBUG,
            INFO,
            WARN,
            ERROR
        }
    }
}
