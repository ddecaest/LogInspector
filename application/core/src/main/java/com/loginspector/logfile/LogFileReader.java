package com.loginspector.logfile;

import java.util.Optional;

public interface LogFileReader {

    Optional<LogLine> readLine();
}
