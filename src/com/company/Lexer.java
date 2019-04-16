package com.company;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class Lexer {
    private static ArrayList<String> keywordsList = new ArrayList<>();
    private static ArrayList<String> symbolsList = new ArrayList<>();
    private String input;
    private String inputFilePath;
    private int lineNumber;


    public Lexer(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        this.input = readInputFromFile(inputFilePath);
        preProcess();
    }//

    private void getToNextToken() {

    }

    private void preProcess() {
        String tokenTypesDirectory = System.getProperty("user.dir") + "\\src\\com\\company\\InputFiles\\Token Types";
        try {
            constructTransitionMatrix();
            addContentToList(keywordsList, tokenTypesDirectory + "\\KEYWORDS.txt");
            addContentToList(symbolsList, tokenTypesDirectory + "\\SYMBOLS.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void constructTransitionMatrix() {

    }

    private CharacterTypes checkCharacterTypes(char x) {
        if (x == ' ') {
            return CharacterTypes.WHITESPACE;
        } else if (x == '=') {
            return CharacterTypes.EQUAL;
        } else if (Character.isDigit(x)) {
            return CharacterTypes.DIGIT;
        } else if (Character.isLetter(x)) {
            return CharacterTypes.ALPHABET;
        } else if (x == '*') {
            return CharacterTypes.STAR;
        } else if (x == '/') {
            return CharacterTypes.SLASH;
        } else if (symbolsList.contains(x)) {
            return CharacterTypes.SYMBOL;
        } else return CharacterTypes.OTHER;

    }

    private static void addContentToList(List<String> list, String fileName) throws FileNotFoundException {
        FileInputStream inKeywords = new FileInputStream(fileName);
        Scanner scanner = new Scanner(inKeywords);
        while (scanner.hasNext())
            keywordsList.add(scanner.next());
    }

    public static ArrayList<String> getKeywordsList() {
        return keywordsList;
    }

    public static ArrayList<String> getSymbolsList() {
        return symbolsList;
    }


    public String readInputFromFile(String fileName) {
        InputStream is = null;
        try {

            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

}
