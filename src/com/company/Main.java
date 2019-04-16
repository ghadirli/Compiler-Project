package com.company;

public class Main {

    public static void main(String[] args) {
//        String tmps = "sd\n\tk";
//        System.out.println(tmps.charAt(3));
//        System.out.println(CharacterTypes.values().length);

        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles";
        String sampleOutputDirectory = System.getProperty("user.dir") + "/src/com/company/OutputFiles";
        Lexer lexer = new Lexer(sampleInputDirectory + "/Input1", sampleOutputDirectory + "/Scanner");
        lexer.analyzer();
    }

    //public static tok
}
