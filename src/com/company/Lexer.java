package com.company;

import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.*;


public class Lexer {
    private ArrayList<String> keywordsList = new ArrayList<>();
    private ArrayList<String> symbolsList = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> transitionMatrix = new ArrayList<>();
    private HashMap<Integer, TokenTypes> acceptStates = new HashMap<>();
    private String input;
    private String inputFilePath;
    private int lineNumber;
    private final int ERRORSTATE = 2999;
    private final int numberOfStates = 15;
    private final int initialNumber = -1;
    private final int STARTSTATE = 0;


    public Lexer(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        this.input = readInputFromFile(inputFilePath);
        preProcess();
    }//

    private Pair<Token, Integer> getNextToken(int startIndex) {
        Token res = new Token();
        int curState = STARTSTATE;
        int curIndex = startIndex;
        while(acceptStates.get(curState) != null) { //TODO check correct?
            curState = getNextState(curState, input.charAt(curIndex));
            curIndex++;
        }
        res.setDescription(input.substring(startIndex, curIndex)); // TODO handle cases which must go back one index
        res.setTokenType(acceptStates.get(curState));
        return new Pair<>(res, curIndex);
        //TODO
    }


    private void preProcess() {
        String tokenTypesDirectory = System.getProperty("user.dir") + "\\src\\com\\company\\InputFiles\\Token Types";
        try {
            initializeAcceptedStates();
            initializeTransitionMatrix();
            addContentToList(keywordsList, tokenTypesDirectory + "\\KEYWORDS.txt");
            addContentToList(symbolsList, tokenTypesDirectory + "\\SYMBOLS.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        constructTransitionMatrix();
    }
    private void initializeTransitionMatrix() {
        for (int i = 0; i < numberOfStates; i++) {
            transitionMatrix.add(new ArrayList<>());
            for (int j = 0; j < CharacterTypes.values().length; j++) {
                transitionMatrix.get(i).add(initialNumber);
            }
        }
    }
    private void constructTransitionMatrix() {

        transitionMatrix.get(0).set(CharacterTypes.WHITESPACE.ordinal(), 0);
        transitionMatrix.get(0).set(CharacterTypes.DIGIT.ordinal(), 2);
        transitionMatrix.get(0).set(CharacterTypes.ALPHABET.ordinal(), 1);
        transitionMatrix.get(0).set(CharacterTypes.SYMBOL.ordinal(), 13);
        transitionMatrix.get(0).set(CharacterTypes.EQUAL.ordinal(), 3);
        transitionMatrix.get(0).set(CharacterTypes.SLASH.ordinal(), 4);
        transitionMatrix.get(0).set(CharacterTypes.STAR.ordinal(), ERRORSTATE);
        transitionMatrix.get(0).set(CharacterTypes.ENTER.ordinal(), ERRORSTATE);

        transitionMatrix.get(1).set(CharacterTypes.ALPHABET.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.DIGIT.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.WHITESPACE.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.SYMBOL.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.STAR.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.EQUAL.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.ENTER.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.SLASH.ordinal(), ERRORSTATE);


        transitionMatrix.get(2).set(CharacterTypes.DIGIT.ordinal(), 2);
        transitionMatrix.get(2).set(CharacterTypes.ALPHABET.ordinal(), 11);
        transitionMatrix.get(2).set(CharacterTypes.ENTER.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.STAR.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.SLASH.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.EQUAL.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.WHITESPACE.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.SYMBOL.ordinal(), ERRORSTATE);

        transitionMatrix.get(3).set(CharacterTypes.EQUAL.ordinal(), 14);
        transitionMatrix.get(3).set(CharacterTypes.WHITESPACE.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.DIGIT.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.ALPHABET.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.SYMBOL.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.SLASH.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.STAR.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.ENTER.ordinal(), 15);

        for (int i = 0; i < CharacterTypes.values().length; i++) {
            transitionMatrix.get(4).set(i, ERRORSTATE);
        }
        transitionMatrix.get(4).set(CharacterTypes.SLASH.ordinal(), 5);
        transitionMatrix.get(4).set(CharacterTypes.STAR.ordinal(), 6);

        for (int i = 0; i < CharacterTypes.values().length; i++) {
            transitionMatrix.get(5).set(i, 5);
        }
        transitionMatrix.get(5).set(CharacterTypes.ENTER.ordinal(), 16);

        transitionMatrix.get(6).set(CharacterTypes.STAR.ordinal(), 7);
        transitionMatrix.get(6).set(CharacterTypes.EQUAL.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.ALPHABET.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.DIGIT.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.SYMBOL.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.ENTER.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.SLASH.ordinal(), 6);
        transitionMatrix.get(6).set(CharacterTypes.WHITESPACE.ordinal(), 6);

        for (int i = 0; i < CharacterTypes.values().length; i++) {
            transitionMatrix.get(7).set(i, 6);
        }
        transitionMatrix.get(7).set(CharacterTypes.SLASH.ordinal(), 16);


    }

    private int getNextState(int curState, char seenCharacter) {
        //TODO

        return 0;
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
        } else if (symbolsList.contains("" + x)) {
            return CharacterTypes.SYMBOL;
        } else return CharacterTypes.OTHER;

    }

    private static void addContentToList(List<String> list, String fileName) throws FileNotFoundException {
        FileInputStream inKeywords = new FileInputStream(fileName);
        Scanner scanner = new Scanner(inKeywords);
        while (scanner.hasNext())
            list.add(scanner.next());
    }

    public ArrayList<String> getKeywordsList() {
        return keywordsList;
    }

    public ArrayList<String> getSymbolsList() {
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

    private void initializeAcceptedStates() {
        acceptStates.put(ERRORSTATE, TokenTypes.ERROR);
        acceptStates.put(10, TokenTypes.ID); //TODO must check for symbols
        acceptStates.put(12, TokenTypes.NUM);
        acceptStates.put(13, TokenTypes.SYMBOL);
        acceptStates.put(15, TokenTypes.SYMBOL);
        acceptStates.put(14, TokenTypes.SYMBOL);
        acceptStates.put(16, TokenTypes.COMMENT);
    }

}
