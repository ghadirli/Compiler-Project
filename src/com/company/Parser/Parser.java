package com.company.Parser;

import com.company.Lexer.Lexer;
import com.company.Lexer.Token;
import com.company.Lexer.TokenTypes;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.company.Parser.Logger.assertByMessage;


public class Parser {
    private Stack<String> stack;
    private String inputFilePath;
    private String outputFilePath;
    private String errorFilePath;
    private ArrayList<String> keywordsList = new ArrayList<>();
    private ArrayList<String> symbolsList = new ArrayList<>();
    private ArrayList<Character> whiteSpaceList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> firstSets; // maps the nonTerminals to their first sets
    private HashMap<String, ArrayList<String>> followSets; // maps the nonTerminals to their follow sets
    private Lexer lexer;
    private HashMap<String, TransitionTree> transitionTreesSet; // maps the nonTerminals to their transition trees
    private HashMap<String, ArrayList<String>> rules; // maps the nonTerminals to the expressions they can transform
    private String cfgBegin = "PROGRAM"; // (can be final but cleaner if not)
    private final String epsilon = "epsilon";
    private Logger logger;
    private HashMap<String, String> description = new HashMap<>();

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
        logger = new Logger(errorFilePath);
    }

    public Parser(Lexer lexer, String errorFilePath) {
        this.lexer = lexer;
        this.errorFilePath = errorFilePath;
        logger = new Logger(errorFilePath);
        keywordsList = lexer.getKeywordsList();
        symbolsList = lexer.getSymbolsList();
        whiteSpaceList = lexer.getWhiteSpaceList();
    }

    // read line by line from file and initializes rules.
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

    // check can change to string.find() ?
    public int indexOfArrow(String string) {
        for (int i = 0; i < string.length() - 3; i++) {
            if (string.substring(i, i + 3).equals(" ->"))
                return i;
        }
        return 0;
    }

    public void parse() {
        initializeTransitionTrees();
        Token currentToken;
        //int cursor = 0;
        //String currentNonTerminal = cfgBegin;
        currentToken = lexer.getNextToken();
        GraphNode root = new GraphNode(cfgBegin, 0);
        transit(cfgBegin, transitionTreesSet.get(cfgBegin).getRoot(), currentToken, root);


        /*do {
            //transit(tra);
            Node curNode = transitionTreesSet.get(currentNonTerminal).getCurrentNode();
            for (Pair<Node, String> neighbor : curNode.getNeighbours()) {
                if (isInFirst(neighbor.getValue(), currentToken)) {
                    transitionTreesSet.get(currentNonTerminal).setCurrentNode(neighbor.getKey());
                    break;
                }
            }
        } while (currentToken.getTokenType() != TokenTypes.EOF);*/
    }

    private void dfsAndPrint(GraphNode graphNode){
        for(int i=0; i<graphNode.getDepth(); i++){
            System.out.println("|   ");
        }
        System.out.println();
    }

    // TODO handle and skip whenever token type is error (and comment)
    // transits the transition diagram of the nonTerminal from the node given to the end of that diagram and
    // returns the next token that hasn't been transited
    // graphNode is the node for parse tree
    // Node is the node for transition tree
    public Token transit(String nonTerminal, Node node, Token token, GraphNode graphNode) {
        //Node curNode = transitionTree.getCurrentNode();
        if (node.isEnd())
            return token;

        for (Pair<Node, String> neighbor : node.getNeighbours()) {
            if (neighbor.getValue().equals(epsilon) && isInFollow(nonTerminal, token)) {
                GraphNode curGraphNode = new GraphNode(epsilon, graphNode.getDepth() + 1);
                graphNode.addChild(curGraphNode);
                return transit(nonTerminal, neighbor.getKey(), token, curGraphNode); // eventually does nothing because traverses
                // an epsilon edge and go to end of tree and return from there
                // but better to be here for comprehensive algorithm
            }
            if (isInFirst(neighbor.getValue(), token)) {
                if (isNonTerminal(neighbor.getValue())) {
                    GraphNode curGraphNode = new GraphNode(neighbor.getValue(), graphNode.getDepth() + 1);
                    graphNode.addChild(curGraphNode);
                    token = transit(neighbor.getValue(), transitionTreesSet.get(neighbor.getValue()).getRoot(), token, curGraphNode);
                } else {
                    GraphNode curGraphNode = new GraphNode(token.getDescription(), graphNode.getDepth() + 1);
                    graphNode.addChild(curGraphNode);
                    token = lexer.getNextToken();
                }
                return transit(nonTerminal, neighbor.getKey(), token, graphNode);
            } else if (epsilonInFirst(neighbor.getValue()) && isInFollow(neighbor.getValue(), token)) {
                GraphNode curGraphNode = new GraphNode(neighbor.getValue(), graphNode.getDepth() + 1);
                graphNode.addChild(curGraphNode);
                token = transit(neighbor.getValue(), transitionTreesSet.get(neighbor.getValue()).getRoot(), token, curGraphNode);
                return transit(nonTerminal, neighbor.getKey(), token, graphNode);
            }
        }

        // TODO input must contain error
        // and it should be in a node with out degree = 1
        Pair<Node, String> neighbor = node.getNeighbours().get(0);
        // for assertion
        assertByMessage(node.getNeighbours().size() <= 1, "Wrong assumption of neighbor size! it's more than one.");
        if (isEOF(neighbor.getValue())) {
            logger.log(token.getLineNumber() + ": Syntax Error! Malformed Input.");
            return null; // TODO check
        }
        if (!isNonTerminal(neighbor.getValue())) {
            logger.log(token.getLineNumber() + ": Syntax Error! Missing " + neighbor.getValue());
            return transit(nonTerminal, neighbor.getKey(), token, graphNode);
        }
        if (!isInFirst(neighbor.getValue(), token) && !isInFollow(neighbor.getValue(), token)) {
            logger.log(token.getLineNumber() + ": Syntax Error! Unexpected " + token.getDescription());
            return transit(nonTerminal, node, lexer.getNextToken(), graphNode);
        }
        assertByMessage(isInFollow(neighbor.getValue(), token) && !epsilonInFirst(neighbor.getValue()));
        logger.log(token.getLineNumber() + ": Syntax Error! Missing " + neighbor.getValue()); // TODO set Description for neighbor.getValue()
        return transit(nonTerminal, neighbor.getKey(), token, graphNode);
    }

    private boolean isEOF(String terminalOrNonTerminalName) {
        if(isNonTerminal(terminalOrNonTerminalName))
            return false;
        else return terminalOrNonTerminalName.equals("eof");
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


    }

    private void addProductionToTree(TransitionTree transitionTree, String production) {
        String[] stringsOfProduction = production.split("\\s+");
        Node terminal = transitionTree.getTerminal();
        Node start = transitionTree.getRoot();
        if (stringsOfProduction.length == 1)
            start.addToNeighbours(terminal, stringsOfProduction[0]);
        else {
            Node currentNode = start;
            for (int i = 0; i < stringsOfProduction.length - 1; i++) {
                Node newNode = new Node();
                transitionTree.addToNodes(newNode);
                currentNode.addToNeighbours(newNode, stringsOfProduction[i]);
                currentNode = newNode;
            }
        }
    }

    // if terminalOrNonTerminalName is terminal, we return false
    private boolean isInFollow(String terminalOrNonTerminalName, Token token) {
        if (!isNonTerminal(terminalOrNonTerminalName))
            return false;
        else {
            return checkIsInSet(token, followSets.get(terminalOrNonTerminalName));

        }
    }

    // check first, for terminals and nonTerminals
    private boolean isInFirst(String terminalOrNonTerminalName, Token token) {
        if (!isNonTerminal(terminalOrNonTerminalName)) {
            return terminalOrNonTerminalName.equals(token.getDescription());
        } else {
            return checkIsInSet(token, firstSets.get(terminalOrNonTerminalName));
        }
    }

    private boolean checkIsInSet(Token token, ArrayList arr) {
        if (token.getTokenType() == TokenTypes.ID)
            return arr.contains("id");
        else if (token.getTokenType() == TokenTypes.NUM)
            return arr.contains("num");
        else if (token.getTokenType() == TokenTypes.EOF)
            return arr.contains("eof");
        else if (token.getTokenType() == TokenTypes.KEYWORD)
            for (String s : keywordsList) {
                if (token.getDescription().equals(s))
                    return arr.contains(s);
            }
        else if (token.getTokenType() == TokenTypes.SYMBOL)
            for (String s : symbolsList) {
                if (token.getDescription().equals(s))
                    return arr.contains(s);
            }

        return false;
    }

    private boolean epsilonInFirst(String terminalOrNonTerminalName) {
        return isNonTerminal(terminalOrNonTerminalName) && firstSets.get(terminalOrNonTerminalName).contains("epsilon");
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
        followSets.put("VAR2", splitWithComma("=, *, +, -, <;, ==, ;, ), ], ,"));
        followSets.put("SIMPLEEXPRESSION", splitWithComma(""));
        followSets.put("SIMPLEEXPRESSION2", splitWithComma(";, ), ], ,"));
        followSets.put("RELOP", splitWithComma("+, -, (, id, num"));
        followSets.put("ADDITIVEEXPRESSION", splitWithComma("<;, ==, ;, ), ], ,"));
        followSets.put("ADDITIVEEXPRESSION2", splitWithComma("<;, ==, ;, ), ], ,"));
        followSets.put("ADDOP", splitWithComma("+, -, (, id, num"));
        followSets.put("TERM", splitWithComma("+, -, <;, ==, ;, ), ], ,"));
        followSets.put("TERM2", splitWithComma("+, -, <;, ==, ;, ), ], ,"));
        followSets.put("SIGNEDFACTOR", splitWithComma("*, +, -, <;, ==, ;, ), ], ,"));
        followSets.put("FACTOR", splitWithComma("*, +, -, <;, ==, ;, ), ], ,"));
        followSets.put("FACTOR2", splitWithComma("*, +, -, <;, ==, ;, ), ], ,"));
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
        firstSets.put("PARAMS2", splitWithComma("id"));
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
        firstSets.put("EXPRESSION2", splitWithComma("(, [, =, *, epsilon, +, -, &lt;, =="));
        firstSets.put("EXPRESSION3", splitWithComma("=, *, epsilon, +, -, &lt;, =="));
        firstSets.put("VAR", splitWithComma("id"));
        firstSets.put("VAR2", splitWithComma("[, epsilon"));
        firstSets.put("SIMPLEEXPRESSION", splitWithComma("+, -, (, id, num"));
        firstSets.put("SIMPLEEXPRESSION2", splitWithComma("epsilon, &lt;, =="));
        firstSets.put("RELOP", splitWithComma("&lt;, =="));
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

    public void initializeDescription() {
        description.put("EXPRESSIONSTMT", "the main program");
        description.put("CASESTMT", "on case statement");
        description.put("TYPESPECIFIER", "type of specifier");
        description.put("FACTOR2", "util for factor ");
        description.put("SIGNEDFACTOR", " signed factor");
        description.put("TERM", "term ");
        description.put("PARAM", "parameter");
        description.put("PARAMS2", "util for parameter");
        description.put("EXPRESSION3", "");
        description.put("ARGS", "");
        description.put("PARAMLIST2", "");
        description.put("PARAMS", "");
        description.put("ADDITIVEEXPRESSION2", "");
        description.put("EXPRESSION2", "");
        description.put("VAR2", "");
        description.put("ADDOP", "");
        description.put("DECLARATION", "");
        description.put("CASESTMTS", "");
        description.put("TERM2", "");
        description.put("PARAMLIST", "");
        description.put("EXPRESSION", "");
        description.put("ADDITIVEEXPRESSION", "");
        description.put("COMPOUNDSTMT", "");
        description.put("SIMPLEEXPRESSION", "");
        description.put("ITERATIONSTMT", "");
        description.put("SIMPLEEXPRESSION2", "");
        description.put("SWITCHSTMT", "");
        description.put("RETURNSTMT2", "");
        description.put("DECLARATION2", "");
        description.put("PROGRAM", "");
        description.put("STATEMENTLIST", "");
        description.put("SELECTIONSTMT", "");
        description.put("DEFAULTSTMT", "");
        description.put("FACTOR", "");
        description.put("STATEMENT", "");
        description.put("VAR", "");
        description.put("PARAM2", "");
        description.put("RELOP", "");
        description.put("ARGLIST2", "");
        description.put("RETURNSTMT", "");
        description.put("ARGLIST", "");
        description.put("DECLARATIONLIST", "");
    }
}
