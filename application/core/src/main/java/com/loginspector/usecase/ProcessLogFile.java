package com.loginspector.usecase;

import com.loginspector.logfile.LogFileReader;
import com.loginspector.logfile.LogFileReaderImpl;
import com.loginspector.logfile.LogLine;
import com.loginspector.logging.LoggerFactory;
import com.loginspector.statistics.write.WriteRenderStatisticsAsXmlStrategy;
import com.loginspector.statistics.write.WriteStatisticsStrategy;
import com.loginspector.statistics.RenderStatistics;
import com.loginspector.statistics.gather.GatherStatisticsStrategy;
import com.loginspector.statistics.gather.GatherRenderStatisticsStrategy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Function;

public abstract class ProcessLogFile {

    private static final ProcessLogFileFlow<RenderStatistics> USE_CASE_FLOW = new ProcessLogFileFlow<>(
            is -> new LogFileReaderImpl(is, LoggerFactory::createLogger),
            new GatherRenderStatisticsStrategy(LoggerFactory::createLogger),
            new WriteRenderStatisticsAsXmlStrategy(LoggerFactory::createLogger)
    );

    public static void execute(InputStream inputStream, OutputStream outputStream) {
        USE_CASE_FLOW.execute(inputStream, outputStream);
    }


    static class ProcessLogFileFlow<T> {

        private final Function<InputStream, LogFileReader> createLogFileReader;
        private final GatherStatisticsStrategy<T> gatherStatisticsStrategy;
        private final WriteStatisticsStrategy<T> writeStatisticsStrategy;

        ProcessLogFileFlow(Function<InputStream, LogFileReader> createLogFileReader,
                           GatherStatisticsStrategy<T> gatherStatisticsStrategy,
                           WriteStatisticsStrategy<T> writeStatisticsStrategy
        ) {
            this.gatherStatisticsStrategy = gatherStatisticsStrategy;
            this.writeStatisticsStrategy = writeStatisticsStrategy;
            this.createLogFileReader = createLogFileReader;
        }

        public void execute(InputStream inputStream, OutputStream outputStream) {
            LogFileReader logFileReader = createLogFileReader.apply(inputStream);

            Optional<LogLine> logLine = logFileReader.readLine();
            while(logLine.isPresent()) {
                gatherStatisticsStrategy.consume(logLine.get());
                logLine = logFileReader.readLine();
            }

            T result = gatherStatisticsStrategy.getResult();
            writeStatisticsStrategy.writeStatistics(outputStream, result);
        }
    }
}
