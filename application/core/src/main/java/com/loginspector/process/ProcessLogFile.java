package com.loginspector.process;

import com.loginspector.logging.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public abstract class ProcessLogFile {

    public static final ProcessLogFileFlow USE_CASE_FLOW = new ProcessLogFileFlow(
            is -> new LogFileReader(is, LoggerFactory::createLogger),
            new GatherStatisticsStrategyImpl()
    );

    public static InputStream execute(InputStream inputStream) {
        return USE_CASE_FLOW.execute(inputStream);
    }


    private static class ProcessLogFileFlow {

        private final Function<InputStream, LogFileReader> createLogFileReader;
        private final GatherStatisticsStrategy gatherStatisticsStrategy;

        ProcessLogFileFlow(Function<InputStream, LogFileReader> createLogFileReader,
                           GatherStatisticsStrategy gatherStatisticsStrategy
        ) {
            this.createLogFileReader = createLogFileReader;
            this.gatherStatisticsStrategy = gatherStatisticsStrategy;
        }

        public InputStream execute(InputStream inputStream) {
            LogFileReader logFileReader = createLogFileReader.apply(inputStream);

            Optional<LogLine> logLine = logFileReader.readLine();
            while(logLine.isPresent()) {
                gatherStatisticsStrategy.consume(logLine.get());
                logLine = logFileReader.readLine();
            }

            return gatherStatisticsStrategy.getResultAsXml();
        }
    }
}
