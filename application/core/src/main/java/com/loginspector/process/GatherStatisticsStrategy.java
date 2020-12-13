package com.loginspector.process;

import java.io.InputStream;

interface GatherStatisticsStrategy {

    void consume(LogLine logLine);

    InputStream getResultAsXml();
}
