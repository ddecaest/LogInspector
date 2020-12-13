package com.loginspector.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogFileReader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
    private static final Pattern LOG_LINE_PATTERN;

    static {
        final String TIMESTAMP = "(....-..-..\s..:..:..,...)";
        final String THREAD = "\\[(.*)?]";
        final String LOG_LEVEL = "(.*?)";
        final String CLASS_NAME = "\\[(.*)?]";
        final String ERROR_MESSAGE = "(.*)";

        LOG_LINE_PATTERN = Pattern.compile(
                TIMESTAMP + "\s" + THREAD + "\s" + LOG_LEVEL + "\s" + CLASS_NAME + ":\s" + ERROR_MESSAGE
        );
    }

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
        Matcher matcher = LOG_LINE_PATTERN.matcher(line);
        if (matcher.find( )) {
            return handleStandardLogLine(matcher);
        } else {
            return handleUnstructuredLogLine(line);
        }
    }

    private Optional<LogLine> handleUnstructuredLogLine(String line) {
        return Optional.of(LogLine.unstructuredLogLine(line));
    }

    private Optional<LogLine> handleStandardLogLine(Matcher matcher) {
        LocalDateTime zonedDateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);
        String thread = matcher.group(2);
        LogLevel logLevel = parseLogLevel(matcher.group(3));
        String className = matcher.group(4);
        String errorMessage = matcher.group(5);

        return Optional.of(new LogLine(zonedDateTime, errorMessage, logLevel, className, thread));
    }

    private LogLevel parseLogLevel(String rawLogLevel) {
        try {
            return LogLevel.valueOf(rawLogLevel);
        } catch(IllegalArgumentException e) {
            // TODO log warning
            return LogLevel.UNKNOWN;
        }
    }
}
