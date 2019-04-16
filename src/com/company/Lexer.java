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
    private int lineNumber = 0;
    private final int ERRORSTATE = 2999;
    private final int numberOfStates = 15;
    private final int initialNumber = -1;
    private final int STARTSTATE = 0;
    private String outputFilePath;
//    private HashMap<Integer, String>


    public Lexer(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        this.input = readInputFromFile(inputFilePath) + '\n';
        System.out.println(input);
        preProcess();
    }//

    public void analyzer() {
        Token currentToken = new Token();
        Integer curser = 0;
        Integer lineNumber = 0;
        int size = input.length();
        while (curser < size) {
            Pair<Token, Integer> tokenPair = getNextToken(curser);
            System.out.println(tokenPair.getValue());
            curser = tokenPair.getValue();
            System.out.println("line number is " + countLines(input.substring(0, curser)) + " description is " +
                    tokenPair.getKey().getDescription() + " and token is " + tokenPair.getKey().getTokenType());
//            writeInFile(countLines(input.substring(0, curser)), tokenPair.getKey());
        }

    }

    private void writeInFile(Integer lineNo, Token string) {

    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    private Pair<Token, Integer> getNextToken(int startIndex) {
        Token res = new Token();
        int curState = STARTSTATE;
        int curIndex = startIndex;
        while(acceptStates.get(curState) != null) { //TODO check correct?
            curState = getNextState(curState, input.charAt(curIndex)); // TODO handle curIndex = end Of File
            curIndex++;
        }
        TokenTypes tokenType = acceptStates.get(curState);

        //handle of cases which must go back one index
        if(tokenType == TokenTypes.ID /*or keyword(because not handled by now)*/ || tokenType == TokenTypes.NUM) {
            curIndex--;
        }
        if(tokenType == TokenTypes.SYMBOL){
            if(input.charAt(curIndex-1) != '='){
                curIndex--;
            }
        }

        res.setDescription(input.substring(startIndex, curIndex));
        res.setTokenType(tokenType);

        //handle of id or keyword
        if(acceptStates.get(curState) == TokenTypes.ID) {
            for(String keyword : keywordsList){
                if(res.getDescription().equals(keyword)) {
                    res.setTokenType(TokenTypes.KEYWORD);
                }
            }
        }

        return new Pair<>(res, curIndex);
    }


    private void preProcess() {
        String tokenTypesDirectory = System.getProperty("user.dir") + "/src/com/company/InputFiles/Token Types";
        try {
            initializeAcceptedStates();
            initializeTransitionMatrix();
            addContentToList(keywordsList, tokenTypesDirectory + "/KEYWORDS.txt");
            addContentToList(symbolsList, tokenTypesDirectory + "/SYMBOLS.txt");
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
        transitionMatrix.get(0).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);

        transitionMatrix.get(1).set(CharacterTypes.ALPHABET.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.DIGIT.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.WHITESPACE.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.SYMBOL.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.STAR.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.EQUAL.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.ENTER.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.SLASH.ordinal(), ERRORSTATE);
        transitionMatrix.get(1).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);


        transitionMatrix.get(2).set(CharacterTypes.DIGIT.ordinal(), 2);
        transitionMatrix.get(2).set(CharacterTypes.ALPHABET.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.ENTER.ordinal(), 12);
        transitionMatrix.get(2).set(CharacterTypes.STAR.ordinal(), 12);
        transitionMatrix.get(2).set(CharacterTypes.SLASH.ordinal(), ERRORSTATE);
        transitionMatrix.get(2).set(CharacterTypes.EQUAL.ordinal(), 12);
        transitionMatrix.get(2).set(CharacterTypes.WHITESPACE.ordinal(), 12);
        transitionMatrix.get(2).set(CharacterTypes.SYMBOL.ordinal(), 12);
        transitionMatrix.get(2).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);

        transitionMatrix.get(3).set(CharacterTypes.EQUAL.ordinal(), 14);
        transitionMatrix.get(3).set(CharacterTypes.WHITESPACE.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.DIGIT.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.ALPHABET.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.SYMBOL.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.SLASH.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.STAR.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.ENTER.ordinal(), 15);
        transitionMatrix.get(3).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);

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
        transitionMatrix.get(6).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);

        for (int i = 0; i < CharacterTypes.values().length; i++) {
            transitionMatrix.get(7).set(i, 6);
        }
        transitionMatrix.get(7).set(CharacterTypes.SLASH.ordinal(), 16);


    }

    private int getNextState(int curState, char seenCharacter) {
        CharacterTypes charType = checkCharacterTypes(seenCharacter);
        return transitionMatrix.get(curState).get(charType.ordinal());
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

    //-------------getter and setter------------------------
    public ArrayList<String> getKeywordsList() {
        return keywordsList;
    }

    public ArrayList<String> getSymbolsList() {
        return symbolsList;
    }
}
