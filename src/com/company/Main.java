package com.company;

public class Main {

    public static void main(String[] args) {
        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles";
        Lexer lexer = new Lexer(sampleInputDirectory + "/Input1");
        lexer.analyzer();
    }

    //public static tok
}
