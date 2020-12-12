package com.loginspector;

public abstract class TerminalArgumentParser {

    public static Arguments parse(String[] args) {
        if(args == null || args.length != 2) {
            throw new IllegalArgumentException("Expected exactly two arguments: the path to the log file to inspect and the path to the output file!");
        }
        final String pathToLogFile = args[0];
        final String pathToOutputFile = args[1];
        return new Arguments(pathToOutputFile, pathToLogFile);
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
