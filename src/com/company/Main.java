package com.company;

import com.company.Lexer.Lexer;
import com.company.Lexer.Token;
import com.company.Lexer.TokenTypes;
import com.company.Parser.Parser;

public class Main {

    public static void main(String[] args) {
//        String tmps = "sd\n\tk";
//        System.out.println(tmps.charAt(3));
//        System.out.println(CharacterTypes.values().length);

        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles";
        String sampleOutputDirectory = System.getProperty("user.dir") + "/src/com/company/OutputFiles";

//        Lexer checkLexer = new Lexer(sampleInputDirectory + "/Input1", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");
//        checkLexer.analyzer();
//        Token curToken = null;
//        do{
//            curToken = checkLexer.getNextToken();
//            System.out.println(curToken.getLineNumber() + ": " + curToken.getTokenType() + ", " + curToken.getDescription());
//        } while (curToken.getTokenType() != TokenTypes.EOF);

        Lexer lexer = new Lexer(sampleInputDirectory + "/Parser_sample2", sampleOutputDirectory + "/scanner.txt", sampleOutputDirectory + "/lexical_errors.txt");
        Parser parser = new Parser(lexer, sampleOutputDirectory + "/parser_output.txt", sampleOutputDirectory + "/parser_errors.txt");
        parser.parse();

    }

    //public static tok
}
