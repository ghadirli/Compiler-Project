package com.company.Parser;

import com.company.Lexer.Token;
import com.company.Lexer.TokenTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


// TODO OriginalGrammarWithSubroutine mustn't have A -> #alpha x0 x1 ... | #alpha y0 y1 ...
// TODO because we assumed no left recursion in grammar and transitionTrees' middle nodes will not be of degree 1 then

public class Subroutines {
    // private Logger logger;
    private ArrayList<Integer> semanticStack = new ArrayList<>();
    private int pbLineNumber;
    private final int startOfTempMemoryAddress = 1000;
    private int lastTempMemory = startOfTempMemoryAddress;
    private ArrayList<String> programBlock = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> breakablesLines = new ArrayList<>();
    private HashMap<String, Integer> variableDeclarations = new HashMap<>(); // variables place in memory
    private HashMap<String, Integer> functionDeclaration = new HashMap<>(); // functions start line in program block
    private HashMap<String, String> typeOfVariablesAndFunctions = new HashMap<>(); // int void int[] (or int_10) ...
    private ArrayList<String> nameStack = new ArrayList<>();
    private boolean isFirstCase = false;
    private final int returnValuesAddress = startOfTempMemoryAddress - 4;
    private String currentFunction = null;

    // TODO overloading functions ( f_int_int[10] ) and global local variables (f.a)

    public Subroutines() {
        // this.logger = logger;
        programBlock.add(""); // check
    }

    public void executeSubroutineByName(String subroutineName, Token nextToken) {
        switch (subroutineName) {
            case "#label":
                label();
                break;
            case "#save":
                save();
                break;
            case "#while":
                while0();
                break;
            case "#ifsjpf_save":
                ifsjps_save();
                break;
            case "#ifsjp":
                ifsjp();
                break;
            case "#break":
                try {
                    break0();
                } catch (Exception e) {
                    // TODO inappropriate break in middle of code
                }
                break;
            case "#new_breakable":
                new_breakable();
                break;
            case "#end_of_switch":
                end_of_switch();
                break;
            case "#after_case_save":
                after_case_save(nextToken);
                break;
            case "#case_save":
                case_save();
                break;
            case "#push_name":
                push_name(nextToken);
                break;
            case "#func_addr_in_mem_save":
                save_func_addr_in_mem();
                break;
            case "#func_jump":
                func_jump();
                break;

        }
    }

    private void save_in_memory() {

    }

    private void popss(int numberToPop) {
        for (int i = 0; i < numberToPop; i++) {
            semanticStack.remove(semanticStack.size() - 1);
        }
    }

    private void pushss(int val) {
        semanticStack.add(val);
    }

    // TODO check code not contains pbLineNumber++. this method must be used instead
    private void incrementPBLine() {
        pbLineNumber++;
        programBlock.add("");
    }

    private int ssFromLast(int bias) {
        return semanticStack.get(semanticStack.size() - 1 - bias);
    }

    int getTempMemory(){
        lastTempMemory += 4;
        return lastTempMemory;
    }


    //------------------------------subroutines----------------------------------------

    private void label() {
        pushss(pbLineNumber);
    }

    private void save() {
        pushss(pbLineNumber);
        incrementPBLine();
    }

    private void while0() {
        programBlock.set(ssFromLast(0), "(JPF, " + ssFromLast(1) + ", " + (pbLineNumber + 1) + ", )");
        programBlock.set(pbLineNumber, "(JP, " + ssFromLast(2) + ", , )");
        incrementPBLine();
        popss(3);

        // for breaks
        for (int line : breakablesLines.get(breakablesLines.size() - 1)) {
            programBlock.set(line, "(JP, " + (pbLineNumber - 1) + ", , )");
        }
        breakablesLines.remove(breakablesLines.size() - 1);
    }

    private void ifsjps_save() {
        programBlock.set(ssFromLast(0), "JPF, " + ssFromLast(1) + ", " + (pbLineNumber + 1) + ", )");
        popss(2);
        pushss(pbLineNumber + 1);
        incrementPBLine();
    }

    private void ifsjp() {
        programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
        popss(1);
    }

    private void new_breakable() {
        breakablesLines.add(new ArrayList<>());
    }

    private void break0() {
        breakablesLines.get(breakablesLines.size() - 1).add(pbLineNumber);
        incrementPBLine();
    }

    private void end_of_switch() {
        // for breaks
        for (int line : breakablesLines.get(breakablesLines.size() - 1)) {
            programBlock.set(line, "(JP, " + (pbLineNumber - 1) + ", , )");
        }
        breakablesLines.remove(breakablesLines.size() - 1);
    }

    // TODO must handle isFirstCase
    private void after_case_save(Token nextToken) {
        if (!nextToken.getTokenType().equals(TokenTypes.NUM))
            System.err.println("switch case hasn't numbers as values :)");
        else {
            int tempMem = getTempMemory();
            if (isFirstCase) {
                programBlock.set(pbLineNumber, "(EQ, " + ssFromLast(0) + ", #" + nextToken.getDescription() + ", " + tempMem + ")");
                pushss(tempMem);
                incrementPBLine();
                save();
            } else {
                programBlock.set(pbLineNumber, "(EQ, " + ssFromLast(1) + ", #" + nextToken.getDescription() + ", " + tempMem + ")");
                incrementPBLine();
                programBlock.set(ssFromLast(0), "JP, " + pbLineNumber + ", , )");
                //incrementPBLine();
                popss(1);
                pushss(tempMem);
                save(); // TODO check
            }
        }
    }

    private void case_save(){
        programBlock.set(ssFromLast(0), "JPF, " + ssFromLast(1) + ", " + pbLineNumber + ", )");
        popss(2);
        save();
    }

    // TODO check
    private void default_epsilon(){
        popss(2);
    }

    private void default0(){
        // TODO
    }

    private void push_name(Token nextToken){
        nameStack.add(nextToken.getDescription());
    }

    private void save_func_addr_in_mem(){
        if(!nameStack.get(nameStack.size()-1).equals("main")){
            save();
        }
        currentFunction = nameStack.get(nameStack.size()-1);
        functionDeclaration.put(nameStack.get(nameStack.size()-1), pbLineNumber);
        nameStack.remove(nameStack.size()-1);
    }

    private void func_jump(){
        if(!currentFunction.equals("main")){
            programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
            popss(1);
        }
        currentFunction = null;
    }
}
