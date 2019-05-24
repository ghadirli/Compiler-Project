package com.company.Parser;

import Utils.Constants;
import com.company.Lexer.Lexer;
import com.company.Lexer.Token;
import com.company.Lexer.TokenTypes;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


public class Parser {
    private Stack<String> stack;
    private String inputFilePath;
    private String outputFilePath;
    private String errorFilePath;
    private HashMap<String, ArrayList<String>> firstSets;
    private HashMap<String, ArrayList<String>> followSets;
    private Lexer lexer;
    private HashMap<String, TransitionTree> transitionTreesSet;
    private HashMap<String, ArrayList<String>> rules;
    private String cfgBegin = "PROGRAM";

    public Parser(String inputFilePath, String outputFilePath, String errorFilePath) {
        stack = new Stack<>();
        firstSets = new HashMap<>();
        followSets = new HashMap<>();
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.errorFilePath = errorFilePath;
        initializeFirstSets();
        initializeFollowSets();
        initializeRules();
        initializeParseTable();
    }

    private void initializeRules() {
        BufferedReader reader;
        String sampleInputDirectory = System.getProperty("user.dir") + "/src/com/company";
        String grammar = sampleInputDirectory + "/Utils/CFG_grammar_one_per_line";
        try {
            reader = new BufferedReader(new FileReader(grammar));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                int arrowIndex = indexOfArrow(line);
                String left = line.substring(0, arrowIndex);
                String right = line.substring(arrowIndex + 4);
                if (rules.containsKey(left))
                    rules.get(left).add(right);
                else {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(right);
                    rules.put(left, arr);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int indexOfArrow(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.substring(i, i + 3).equals(" ->"))
                return i;
        }
        return 0;
    }

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void parse() {
        initializeTransitionTrees();
        Token currentToken;
        int cursor = 0;
        String currentNonTerminal = cfgBegin;
        do {
            currentToken = lexer.getNextToken();
            Node curNode = transitionTreesSet.get(currentNonTerminal).getCurrentNode();
            for (Pair<Node, String> neighbor : curNode.getNeighbours()) {
                if (isInFirst(neighbor.getValue(), currentToken)) {
                    //TODO complete Soroush
                    transitionTreesSet.get(currentNonTerminal).setCurrentNode(neighbor.getKey());
                    break;
                }
            }
        } while (currentToken.getTokenType() != TokenTypes.EOF);
    }

    // for transition trees
    private void initializeTransitionTrees() {
        for (Map.Entry<String, ArrayList<String>> entry : rules.entrySet()) {
            TransitionTree transitionTree = new TransitionTree();
            for (int i = 0; i < entry.getValue().size(); i++) {
                addProductionToTree(transitionTree, entry.getValue().get(i));
            }
            transitionTreesSet.put(entry.getKey(), transitionTree);
        }
        //TODO
    }

    private void addProductionToTree(TransitionTree transitionTree, String production) {
        //TODO
    }

    // check first, for terminals and nonTerminals
    private boolean isInFirst(String terminalOrNonTerminalName, Token token) {
        return true;
        //TODO
    }

    private void initializeFollowSets() {
        followSets.put("PROGRAM", splitWithComma("-|"));
        followSets.put("DECLARATIONLIST", splitWithComma("eof, {, continue, break, ;, if, while, return, switch, id, +, -, (, num, }"));
        followSets.put("DECLARATION", splitWithComma("int, void, eof, {, continue, break, ;, if, while, return, switch, id, +, -, (, num, }"));
        followSets.put("DECLARATION2", splitWithComma("int, void, eof, {, continue, break, ;, if, while, return, switch, id, +, -, (, num, }"));
        followSets.put("TYPESPECIFIER", splitWithComma("id"));
        followSets.put("PARAMS", splitWithComma(")"));
        followSets.put("PARAMS2", splitWithComma(")"));
        followSets.put("PARAMLIST", splitWithComma(""));
        followSets.put("PARAMLIST2", splitWithComma(")"));
        followSets.put("PARAM", splitWithComma(",, )"));
        followSets.put("PARAM2", splitWithComma(",, )"));
        followSets.put("COMPOUNDSTMT", splitWithComma("int, void, eof, {, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("STATEMENTLIST", splitWithComma("}, case, default"));
        followSets.put("STATEMENT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("EXPRESSIONSTMT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("SELECTIONSTMT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("ITERATIONSTMT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("RETURNSTMT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("RETURNSTMT2", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("SWITCHSTMT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num, }, else, case, default"));
        followSets.put("CASESTMTS", splitWithComma("default, }"));
        followSets.put("CASESTMT", splitWithComma("case, default, }"));
        followSets.put("DEFAULTSTMT", splitWithComma("}"));
        followSets.put("EXPRESSION", splitWithComma(";, ), ], ,"));
        followSets.put("EXPRESSION2", splitWithComma(";, ), ], ,"));
        followSets.put("EXPRESSION3", splitWithComma(";, ), ], ,"));
        followSets.put("VAR", splitWithComma(""));
        followSets.put("VAR2", splitWithComma("=, *, +, -, less, ==, ;, ), |, ], ,"));
        followSets.put("SIMPLEEXPRESSION", splitWithComma(""));
        followSets.put("SIMPLEEXPRESSION2", splitWithComma(";, ), |, ], ,"));
        followSets.put("RELOP", splitWithComma("+, -, (, id, num"));
        followSets.put("ADDITIVEEXPRESSION", splitWithComma("less, ==, ;, ), |, ], ,"));
        followSets.put("ADDITIVEEXPRESSION2", splitWithComma("less, ==, ;, ), |, ], ,"));
        followSets.put("ADDOP", splitWithComma("+, -, (, id, num"));
        followSets.put("TERM", splitWithComma("+, -, less, ==, ;, ), |, ], ,"));
        followSets.put("TERM2", splitWithComma("+, -, less, ==, ;, ), |, ], ,"));
        followSets.put("SIGNEDFACTOR", splitWithComma("*, +, -, less, ==, ;, ), |, ], ,"));
        followSets.put("FACTOR", splitWithComma("*, +, -, less, ==, ;, ), |, ], ,"));
        followSets.put("FACTOR2", splitWithComma("*, +, -, less, ==, ;, ), |, ], ,"));
        followSets.put("ARGS", splitWithComma(")"));
        followSets.put("ARGLIST", splitWithComma(")"));
        followSets.put("ARGLIST2", splitWithComma(")"));
    }

    // automatically generated from web with python, thus this may have no mistakes :)
    private void initializeFirstSets() {
        firstSets.put("PROGRAM", splitWithComma("eof, int, void"));
        firstSets.put("DECLARATIONLIST", splitWithComma("epsilon, int, void"));
        firstSets.put("DECLARATION", splitWithComma("int, void"));
        firstSets.put("DECLARATION2", splitWithComma(";, [, ("));
        firstSets.put("TYPESPECIFIER", splitWithComma("int, void"));
        firstSets.put("PARAMS", splitWithComma("int, void"));
        firstSets.put("KPARAMS2", splitWithComma("id"));
        firstSets.put("PARAMLIST", splitWithComma("int, void"));
        firstSets.put("PARAMLIST2", splitWithComma(",, epsilon"));
        firstSets.put("PARAM", splitWithComma("int, void"));
        firstSets.put("PARAM2", splitWithComma("[, epsilon"));
        firstSets.put("COMPOUNDSTMT", splitWithComma("{"));
        firstSets.put("STATEMENTLIST", splitWithComma("epsilon, {, continue, break, ;, if, while, return, switch, id, +, -, (, num"));
        firstSets.put("STATEMENT", splitWithComma("{, continue, break, ;, if, while, return, switch, id, +, -, (, num"));
        firstSets.put("EXPRESSIONSTMT", splitWithComma("continue, break, ;, id, +, -, (, num"));
        firstSets.put("SELECTIONSTMT", splitWithComma("if"));
        firstSets.put("ITERATIONSTMT", splitWithComma("while"));
        firstSets.put("RETURNSTMT", splitWithComma("return"));
        firstSets.put("RETURNSTMT2", splitWithComma(";, id, +, -, (, num"));
        firstSets.put("SWITCHSTMT", splitWithComma("switch"));
        firstSets.put("CASESTMTS", splitWithComma("epsilon, case"));
        firstSets.put("CASESTMT", splitWithComma("case"));
        firstSets.put("DEFAULTSTMT", splitWithComma("default, epsilon"));
        firstSets.put("EXPRESSION", splitWithComma("id, +, -, (, num"));
        firstSets.put("EXPRESSION2", splitWithComma("("));
        firstSets.put("EXPRESSION3", splitWithComma("=, *, epsilon, +, -, less, =="));
        firstSets.put("VAR", splitWithComma("id"));
        firstSets.put("VAR2", splitWithComma("[, epsilon"));
        firstSets.put("SIMPLEEXPRESSION", splitWithComma("+, -, (, id, num"));
        firstSets.put("SIMPLEEXPRESSION2", splitWithComma("epsilon, less, =="));
        firstSets.put("RELOP", splitWithComma("less, =="));
        firstSets.put("ADDITIVEEXPRESSION", splitWithComma("+, -, (, id, num"));
        firstSets.put("ADDITIVEEXPRESSION2", splitWithComma("epsilon, +, -"));
        firstSets.put("ADDOP", splitWithComma("+, -"));
        firstSets.put("TERM", splitWithComma("+, -, (, id, num"));
        firstSets.put("TERM2", splitWithComma("*, epsilon"));
        firstSets.put("SIGNEDFACTOR", splitWithComma("+, -, (, id, num"));
        firstSets.put("FACTOR", splitWithComma("(, id, num"));
        firstSets.put("FACTOR2", splitWithComma("(, [, epsilon"));
        firstSets.put("ARGS", splitWithComma("epsilon, id, +, -, (, num"));
        firstSets.put("ARGLIST", splitWithComma("id, +, -, (, num"));
        firstSets.put("ARGLIST2", splitWithComma(",, epsilon"));
    }

    private Boolean isNonTerminal(String production) {
        return production.charAt(0) <= 90 && production.charAt(0) >= 65;

    }

    private ArrayList<String> splitWithComma(String str) {
        return new ArrayList<String>(Arrays.asList(str.split(", ")));
    }

    public void initializeParseTable() {

    }
}
