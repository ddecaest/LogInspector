package com.loginspector.statistics.write;

import java.io.OutputStream;

public interface WriteStatisticsStrategy<T> {

    void writeStatistics(OutputStream outputStream, T statistics);
}
