package com.loginspector.process;

import java.util.Optional;

interface LogFileReader {

    Optional<LogLine> readLine();
}
