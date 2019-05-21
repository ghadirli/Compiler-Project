package com.company;

public class Main {

    public static void main(String[] args) {
//        String tmps = "sd\n\tk";
//        System.out.println(tmps.charAt(3));
//        System.out.println(CharacterTypes.values().length);

        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles";
        String sampleOutputDirectory = System.getProperty("user.dir") + "/src/com/company/OutputFiles";
        Lexer lexer = new Lexer(sampleInputDirectory + "/Input1", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");
//        lexer.analyzer();
        Parser parser = new Parser(sampleInputDirectory + "/Input1", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");

    }

    //public static tok
}
