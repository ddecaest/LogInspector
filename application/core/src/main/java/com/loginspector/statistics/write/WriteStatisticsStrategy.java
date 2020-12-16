package com.loginspector.statistics.write;

import java.io.InputStream;

public interface WriteStatisticsStrategy<T> {

    InputStream writeStatistics(T statistics);
}
