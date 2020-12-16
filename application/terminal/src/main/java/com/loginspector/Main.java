package com.loginspector;

import com.loginspector.ArgumentParser.Arguments;
import com.loginspector.usecase.ProcessLogFile;
import org.apache.commons.io.FileUtils;

import java.io.*;

class Main {

    public static void main(String[] args) {
        final Arguments arguments = ArgumentParser.parse(args);
        execute(arguments);
    }

    public static void execute(ArgumentParser.Arguments arguments) {

        FileOutputStream fileOutputStream = new FileOutputStream(arguments.pathToOutputFile);

        try (InputStream in = new FileInputStream(arguments.pathToLogFile)) {
            ProcessLogFile.execute(in, fileOutputStream);
//            writeResult(result, arguments.pathToOutputFile);
        }
        catch (IOException e) {
            handleReadingError(e);
        }
    }

    private static void handleReadingError(IOException e) {
        System.err.println("ERROR: an exception occurred while reading in the input file.");
        e.printStackTrace();
        System.exit(1);
    }

    private static void writeResult(InputStream result, String pathToOutputFile) {
        File targetFile = new File(pathToOutputFile);
        try {
            FileUtils.copyInputStreamToFile(result, targetFile);
        } catch (IOException e) {
            handleWritingError(e);
        }
    }

    private static void handleWritingError(IOException e) {
        System.err.println("ERROR: an exception occurred while writing the result file.");
        e.printStackTrace();
        System.exit(1);
    }
}