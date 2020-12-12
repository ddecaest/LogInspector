package com.loginspector;

import java.util.Objects;

public abstract class ArgumentParser {

    public static Arguments parse(String[] args) {
        if(args == null || args.length != 2) {
            invalidArgsError();
        }
        final String pathToLogFile = Objects.toString(args[0], "").trim();
        final String pathToOutputFile = Objects.toString(args[1], "").trim();

        if(pathToLogFile.isEmpty()) {
            invalidArgsError();
        }
        if(pathToOutputFile.isEmpty()) {
            invalidArgsError();
        }

        return new Arguments(pathToOutputFile, pathToLogFile);
    }

    private static void invalidArgsError() {
        throw new IllegalArgumentException("Expected exactly two arguments: the path to the log file to inspect and the path to the output file!");
    }


    static class Arguments {

        public final String pathToOutputFile;
        public final String pathToLogFile;

        public Arguments(String pathToOutputFile, String pathToLogFile) {
            this.pathToOutputFile = pathToOutputFile;
            this.pathToLogFile = pathToLogFile;
        }
    }
}
