package com.loginspector.process;

import java.io.InputStream;
import java.util.function.Function;

public abstract class ProcessLogFile {

    public static InputStream execute(InputStream inputStream) {
        ProcessLogFileFlow useCaseFlow = new ProcessLogFileFlow(LogFileReader::new);
        return useCaseFlow.execute(inputStream);
    }


    private static class ProcessLogFileFlow {

        private final Function<InputStream, LogFileReader> createLogFileReader;

        ProcessLogFileFlow(Function<InputStream, LogFileReader> createLogFileReader) {
            this.createLogFileReader = createLogFileReader;
        }

        public InputStream execute(InputStream inputStream) {
            LogFileReader logFileReader = createLogFileReader.apply(inputStream);

            return null;
        }
    }
}
