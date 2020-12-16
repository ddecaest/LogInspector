package com.loginspector.statistics.gather;

import com.loginspector.logfile.LogLine;

public interface GatherStatisticsStrategy<T> {

    void consume(LogLine logLine);

    T getResult();
}
