package com.loginspector;

import com.loginspector.ArgumentParser.Arguments;
import com.loginspector.usecase.ProcessLogFile;

import java.io.*;

class Main {

    public static void main(String[] args) {
        final Arguments arguments = ArgumentParser.parse(args);
        execute(arguments);
    }

    public static void execute(ArgumentParser.Arguments arguments) {
        try (OutputStream output = createStreamToOutputFile(arguments);
             InputStream in = new FileInputStream(arguments.pathToLogFile)
        ) {
            ProcessLogFile.execute(in, output);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private static OutputStream createStreamToOutputFile(Arguments arguments) throws IOException {
        File outputFile = new File(arguments.pathToOutputFile);
        outputFile.createNewFile();
        return new FileOutputStream(outputFile, false);
    }

    private static void handleIOException(IOException e) {
        System.err.println("ERROR: an exception occurred while setting up the input and output files.");
        e.printStackTrace();
        System.exit(1);
    }
}