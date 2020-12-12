package com.loginspector;

class TerminalMain {

    public static void main(String[] args) {

        if(args.length != 1) {
            System.err.println("ERROR: expected exactly one argument: the path to the log file to inspect");
            System.exit(1);
        }

        CoreMain.main();
    }
}