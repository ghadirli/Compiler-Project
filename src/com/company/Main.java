package com.company;

import com.company.Lexer.Lexer;
import com.company.Parser.Parser;

public class Main {

    public static void main(String[] args) {
//        String tmps = "sd\n\tk";
//        System.out.println(tmps.charAt(3));
//        System.out.println(CharacterTypes.values().length);

        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles";
        String sampleOutputDirectory = System.getProperty("user.dir") + "/src/com/company/OutputFiles";
        Lexer lexer = new Lexer(sampleInputDirectory + "/Parser_sample_input", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");
//        lexer.analyzer();
        //Parser parser = new Parser(sampleInputDirectory + "/Input1", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");
        Parser parser = new Parser(lexer, sampleOutputDirectory + "/parser_output.txt", sampleOutputDirectory + "/parser_errors.txt");
        parser.parse();

    }

    //public static tok
}
