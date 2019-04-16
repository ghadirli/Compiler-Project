package com.company;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import com.company.TokenTypes;



public class Lexer {
    private ArrayList<String> keywordsList = new ArrayList<>();
    private ArrayList<String> symbolsList = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> transitionMatrix = new ArrayList<>();
    private HashMap<Integer, TokenTypes> acceptStates = new HashMap<>();
    private String input;
    private String inputFilePath;
    private int lineNumber;


    public Lexer(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        this.input = readInputFromFile(inputFilePath);
        preProcess();
    }//

    private void getToNextToken() {
        //TODO
    }

    private void initializeAcceptedStates(){
        acceptStates.put(8, TokenTypes.ERROR);
        acceptStates.put(9, TokenTypes.ERROR);
        acceptStates.put(10, TokenTypes.ID); //TODO must check for symbols
        acceptStates.put(11, TokenTypes.ERROR);
        acceptStates.put(12, TokenTypes.NUM);
        acceptStates.put(13, TokenTypes.SYMBOL);
        acceptStates.put(14, TokenTypes.SYMBOL);
        acceptStates.put(15, TokenTypes.SYMBOL);
        acceptStates.put(16, TokenTypes.COMMENT);
        acceptStates.put(17, TokenTypes.COMMENT);
        acceptStates.put(18, TokenTypes.ERROR);
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
        final int numberOfStates = 10;
        for(int i=0; i<numberOfStates; i++) {
            transitionMatrix.add(new ArrayList<>());
        }
        transitionMatrix.get(0).set(CharacterTypes.WHITESPACE.ordinal(), 0);

    }

    private int getNextState(int curState, char seenCharacter){
        //TODO
    }

    private CharacterTypes checkCharacterTypes(char x) {
        if (x == ' ') {
            return CharacterTypes.WHITESPACE;
        } else if (x == '\n') {
            return CharacterTypes.ENTER;
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
