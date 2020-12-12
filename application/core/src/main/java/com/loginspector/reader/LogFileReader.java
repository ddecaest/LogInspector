package com.loginspector.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Optional;

public class LogFileReader {

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
        return Optional.empty();
    }


    static class LogLine {

        public final ZonedDateTime zonedDateTime;
        public final String logMessage;
        public final LogLevel loglevel;
        public final String className;
        public final String thread;

        public LogLine(ZonedDateTime zonedDateTime,
                       String logMessage,
                       LogLevel loglevel,
                       String className,
                       String thread) {
            this.zonedDateTime = zonedDateTime;
            this.logMessage = logMessage;
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
