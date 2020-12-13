package com.loginspector.process;

import com.loginspector.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogFileReaderImpl implements LogFileReader {

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
    private final Logger logger;
    private int currentLine;

    public LogFileReaderImpl(InputStream streamToLogFile, Function<Class, Logger> createLogger) {
        this.reader = new BufferedReader(new InputStreamReader(streamToLogFile));
        this.logger = createLogger.apply(this.getClass());
        this.currentLine = 0;
    }

    /**
     * Reads the next available line and returns the result as a parsed LogLine. <br>
     * If no more lines are available or a unrecoverable error occurs, return Optional.empty() <br>
     * If a line is available but it is malformed, a LogLine is returned that only contains an errorMessage.
     */
    public Optional<LogLine> readLine() {
        currentLine++;

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            logger.warn("An error occurred while reading in a log line.", e);
            return Optional.empty();
        }
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
        LocalDateTime localDateTime = parseLocalDateTime(matcher.group(1));
        String thread = matcher.group(2);
        LogLevel logLevel = parseLogLevel(matcher.group(3));
        String className = matcher.group(4);
        String errorMessage = matcher.group(5);

        return Optional.of(new LogLine(localDateTime, errorMessage, logLevel, className, thread));
    }

    private LocalDateTime parseLocalDateTime(String rawLocalDateTime) {
        try {
            return LocalDateTime.parse(rawLocalDateTime, DATE_TIME_FORMATTER);
        } catch(DateTimeParseException e) {
            logger.warn("Could not parse the date on line %s, %s is not a valid date!", String.valueOf(currentLine), rawLocalDateTime);
            return null;
        }
    }

    private LogLevel parseLogLevel(String rawLogLevel) {
        try {
            return LogLevel.valueOf(rawLogLevel);
        } catch(IllegalArgumentException e) {
            logger.warn("Could not parse the log level on line %s, %s is not a valid log level!", String.valueOf(currentLine), rawLogLevel);
            return LogLevel.UNKNOWN;
        }
    }
}
