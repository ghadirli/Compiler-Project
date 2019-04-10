package com.company;

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
    private static ArrayList<String> whiteSpaceList = new ArrayList<>();
    private String inputString;
    private String inputFilePath;

    public Lexer(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        this.inputString = readInputFromFile(inputFilePath);
        System.out.println(inputString);
        preProcess();
    }//

    void analyze(String context){
        Scanner scanner = new Scanner(context);
        InputStream
        while(scanner.hasNext())
            System.out.println(scanner.next());
    }

    Token getNextToken(int index){
        while(true) {
            boolean isSpace = false;
            for(String whiteSpace : whiteSpaceList){
                if(inputString.charAt(index) == (char)Integer.parseInt(whiteSpace)){
                    index++;
                    isSpace = true;
                    break;
                }
            }
            if(!isSpace)
                break;
        }

    }

    private void preProcess(){
        String tokenTypesDirectory = System.getProperty("user.dir") + "\\src\\com\\company\\InputFiles\\Token Types";
        try {
            addContentToList(keywordsList, tokenTypesDirectory + "\\KEYWORDS.txt");
            addContentToList(symbolsList, tokenTypesDirectory + "\\SYMBOLS.txt");
            addContentToList(whiteSpaceList, tokenTypesDirectory + "\\WHITESPACELIST.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addContentToList(List<String> list, String fileName) throws FileNotFoundException {
        FileInputStream inKeywords = new FileInputStream(fileName);
        Scanner scanner = new Scanner(inKeywords);
        while(scanner.hasNext())
            keywordsList.add(scanner.next());
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
        String fileAsString = sb.toString();

        return fileAsString;

    }

    //-------------------getter setter-----------------------------
    public static ArrayList<String> getKeywordsList() {
        return keywordsList;
    }

    public static ArrayList<String> getSymbolsList() {
        return symbolsList;
    }
}
