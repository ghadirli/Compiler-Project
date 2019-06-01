package com.company.Parser;

import java.io.*;

public class ErrorLogger {
    String path;

    public ErrorLogger(String path) {
        this.path = path;
    }

    public void logParseError(String message) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void assertByMessage(boolean exp){
        assertByMessage(exp, "Assertion Failed!");
    }

    public static void assertByMessage(boolean exp, String message){
        if(!exp)
            throw new RuntimeException(message);
    }
}
