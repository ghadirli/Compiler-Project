package com.company.Parser;

import java.io.*;

public class Logger {
    private String path;
//    private boolean unexpectedOutputHasBeenLogged = false;

    public Logger(String path) {
        this.path = path;
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "utf-8"))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void logUnexpectedEndOfFile(int lineNumber){
//        if(!unexpectedOutputHasBeenLogged){
//            unexpectedOutputHasBeenLogged = true;
//            log(lineNumber + ": Syntax Error! Unexpected EndOfFile");
//        }
//    }

    public static void assertByMessage(boolean exp){
        assertByMessage(exp, "Assertion Failed!");
    }

    public static void assertByMessage(boolean exp, String message){
        if(!exp)
            throw new RuntimeException(message);
    }
}
