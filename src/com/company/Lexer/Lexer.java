package com.company.Lexer;

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
    private ArrayList<Character> whiteSpaceList = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> transitionMatrix = new ArrayList<>();
    private HashMap<Integer, TokenTypes> acceptStates = new HashMap<>();
    private String input;
    private String inputFilePath;
    private static int lineNumber = 1;
    private static int lastPrintedLineNumber = 0;
    private static int lastPrintedErrLineNumber = 0; // TODO remove static
    private boolean[] isCheckedEnter;
    private final int ERRORSTATE = 2999;
    private final int numberOfStates = 20;
    private final int initialNumber = -1;
    private final int STARTSTATE = 0;
    private String outputFilePath;
    private String errorFilePath;

    private int lastCursorPosition = 0;



    public Lexer(String inputFilePath, String outputFilePath, String errorFilePath) {
        this.inputFilePath = inputFilePath;
        this.input = readInputFromFile(inputFilePath) + '\n' + ' ';
        isCheckedEnter = new boolean[input.length()];
        this.outputFilePath = outputFilePath;
        this.errorFilePath = errorFilePath;
        preProcess();
    }

    public void analyzer() {
        writeInFile("", outputFilePath, false);
        writeInFile("", errorFilePath, false);
        Integer cursor = 0;
        int size = input.length();
        while (cursor < size - 1) {
            Pair<Token, Integer> tokenPair = getNextToken(cursor);
            if (tokenPair.getKey().getTokenType() == TokenTypes.EOF)
                return;
            cursor = tokenPair.getValue();
            if (tokenPair.getKey().getTokenType() != TokenTypes.COMMENT) {
                String nextLineOrEmpty = "\n";
                if(tokenPair.getKey().getTokenType() != TokenTypes.ERROR) {
                    if(lastPrintedLineNumber == 0)
                        nextLineOrEmpty = "";
                    if(lineNumber != lastPrintedLineNumber){
                        writeInFile(nextLineOrEmpty + lineNumber + ". ", outputFilePath);
                        lastPrintedLineNumber = lineNumber;
                    }
                    writeInFile(tokenPair.getKey(), outputFilePath);
                }
                else {
                    if(lastPrintedErrLineNumber == 0)
                        nextLineOrEmpty = "";
                    if(lineNumber != lastPrintedErrLineNumber){
                        writeInFile(nextLineOrEmpty + lineNumber + ". ", errorFilePath);
                        lastPrintedErrLineNumber = lineNumber;
                    }
                    writeInErrFile(tokenPair.getKey(), errorFilePath);
                }
            }
        }

    }

    private void writeInFile(Token token, String path) {
        String s = "(" + token.getTokenType() + ", " + token.getDescription() + ") ";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"))) {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInErrFile(Token token, String path) {
        String s = "(" + token.getDescription() + ", invalid input) ";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"))) {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInFile(String toBePrinted, String path){
        writeInFile(toBePrinted, path, true);
    }

    private void writeInFile(String toBePrinted, String path, boolean append){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), "utf-8"))) {
            writer.write(toBePrinted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isWhiteSpace(char ch) {
        for (char ch0 : whiteSpaceList) {
            if (ch0 == ch)
                return true;
        }
        return false;
    }

    // by the lastCursorPosition
    // TODO not checked for any potential bug
    // TODO must pass one time or lineNumber will be wrong
    public Token getNextToken(){
        int tokenStartLineNumber =lineNumber;
        Pair<Token, Integer> tokenWithCursor = getNextToken(lastCursorPosition);
        lastCursorPosition = tokenWithCursor.getValue();
        tokenWithCursor.getKey().setLineNumber(tokenStartLineNumber);
        return tokenWithCursor.getKey();
    }

    // returns the token and the endIndex (cursor position)
    public Pair<Token, Integer> getNextToken(int startIndex) {
        Token res = new Token();
        int curState = STARTSTATE;
        int curIndex = startIndex;
        boolean whiteSpaceUntilNow = true;
        while (curIndex < input.length() && acceptStates.get(curState) == null) { //check correct?
            //System.out.print(curState+" ");
            //System.out.println(input.charAt(curIndex));
            //TODO clean it up
            if(input.charAt(curIndex) == '\n' && !isCheckedEnter[curIndex]){
                lineNumber++;
                isCheckedEnter[curIndex] = true;
            }
            curState = getNextState(curState, input.charAt(curIndex)); // handle curIndex = end Of File
            if (whiteSpaceUntilNow && (isWhiteSpace(input.charAt(curIndex)) || input.charAt(curIndex) == '\n')) {
                startIndex++;
            } else {
                whiteSpaceUntilNow = false;
            }
            curIndex++;
        }
        if (curIndex >= input.length()) {
            res.setDescription(input.substring(startIndex, curIndex));
            res.setTokenType(TokenTypes.EOF);
            return new Pair<>(res, curIndex);
        }
        TokenTypes tokenType = acceptStates.get(curState);

        //handle of cases which must go back one index
        if (tokenType == TokenTypes.ID /*or keyword(because not handled by now)*/ || tokenType == TokenTypes.NUM) {
            curIndex--;
            if(input.charAt(curIndex) == '\n' && isCheckedEnter[curIndex]){
                lineNumber--;
                isCheckedEnter[curIndex] = false;
            }
        }
        if (tokenType == TokenTypes.SYMBOL) {
            if (curIndex > 1 && input.charAt(curIndex - 1) != '=' && input.charAt(curIndex - 2) == '=') {
                curIndex--;
                if(input.charAt(curIndex) == '\n' && isCheckedEnter[curIndex]){
                    lineNumber--;
                    isCheckedEnter[curIndex] = false;
                }
            }
        }

        res.setDescription(input.substring(startIndex, curIndex));
        res.setTokenType(tokenType);

        //handle of id or keyword
        if (acceptStates.get(curState) == TokenTypes.ID) {
            for (String keyword : keywordsList) {
                if (res.getDescription().equals(keyword)) {
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
            addContent(whiteSpaceList, tokenTypesDirectory + "/WHITESPACELIST.txt");
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
        transitionMatrix.get(0).set(CharacterTypes.STAR.ordinal(), 13);
        transitionMatrix.get(0).set(CharacterTypes.ENTER.ordinal(), 0);
        transitionMatrix.get(0).set(CharacterTypes.OTHER.ordinal(), ERRORSTATE);

        transitionMatrix.get(1).set(CharacterTypes.ALPHABET.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.DIGIT.ordinal(), 1);
        transitionMatrix.get(1).set(CharacterTypes.WHITESPACE.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.SYMBOL.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.STAR.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.EQUAL.ordinal(), 10);
        transitionMatrix.get(1).set(CharacterTypes.ENTER.ordinal(), 10);
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
        transitionMatrix.get(6).set(CharacterTypes.OTHER.ordinal(), 6);

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
        if (whiteSpaceList.contains(x)) {
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

    private static void addContent(List<Character> list, String fileName) throws FileNotFoundException {
        FileInputStream inKeywords = new FileInputStream(fileName);
        Scanner scanner = new Scanner(inKeywords);
        while (scanner.hasNext())
            list.add((char) Integer.parseInt(scanner.next()));
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

    public ArrayList<Character> getWhiteSpaceList() {
        return whiteSpaceList;
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
