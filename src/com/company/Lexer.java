package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    private static ArrayList<String> keywordsList = new ArrayList<>();
    private static ArrayList<String> symbolsList = new ArrayList<>();

    static void preProcess(){
        String tokenTypesDirectory = System.getProperty("user.dir") + "\\src\\com\\company\\InputFiles\\Token Types";
        try {
            addContentToList(keywordsList, tokenTypesDirectory + "\\KEYWORDS.txt");
            addContentToList(symbolsList, tokenTypesDirectory + "\\SYMBOLS.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addContentToList(List<String> list, String directory) throws FileNotFoundException {
        FileInputStream inKeywords = new FileInputStream(directory);
        Scanner scanner = new Scanner(inKeywords);
        while(scanner.hasNext())
            keywordsList.add(scanner.next());
    }

    public static ArrayList<String> getKeywordsList() {
        return keywordsList;
    }

    public static ArrayList<String> getSymbolsList() {
        return symbolsList;
    }
}
