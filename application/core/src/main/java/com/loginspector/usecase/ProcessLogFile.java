package com.loginspector.usecase;

import com.loginspector.logfile.LogFileReader;
import com.loginspector.logfile.LogFileReaderImpl;
import com.loginspector.logfile.LogLine;
import com.loginspector.logging.LoggerFactory;
import com.loginspector.statistics.write.WriteStastisticsStrategyRenderingStatisticsAsXml;
import com.loginspector.statistics.write.WriteStatisticsStrategy;
import com.loginspector.statistics.RenderingStatistics;
import com.loginspector.statistics.gather.GatherStatisticsStrategy;
import com.loginspector.statistics.gather.GatherStatisticsStrategyImpl;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public abstract class ProcessLogFile {

    private static final ProcessLogFileFlow<RenderingStatistics> USE_CASE_FLOW = new ProcessLogFileFlow<>(
            is -> new LogFileReaderImpl(is, LoggerFactory::createLogger),
            new GatherStatisticsStrategyImpl(),
            new WriteStastisticsStrategyRenderingStatisticsAsXml()
    );

    public static InputStream execute(InputStream inputStream) {
        return USE_CASE_FLOW.execute(inputStream);
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

        public InputStream execute(InputStream inputStream) {
            LogFileReader logFileReader = createLogFileReader.apply(inputStream);

            Optional<LogLine> logLine = logFileReader.readLine();
            while(logLine.isPresent()) {
                gatherStatisticsStrategy.consume(logLine.get());
                logLine = logFileReader.readLine();
            }

            T result = gatherStatisticsStrategy.getResult();
            return writeStatisticsStrategy.writeStatistics(result);
        }
    }
}
