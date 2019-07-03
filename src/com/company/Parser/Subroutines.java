package com.company.Parser;

import com.company.Lexer.Token;
import com.company.Lexer.TokenTypes;

import java.util.ArrayList;
import java.util.HashMap;


// TODO OriginalGrammarWithSubroutine mustn't have A -> #alpha x0 x1 ... | #alpha y0 y1 ...
// TODO because we assumed no left recursion in grammar and transitionTrees' middle nodes will not be of degree 1 then

public class Subroutines {
    // private Logger logger;
    private ArrayList<Integer> semanticStack = new ArrayList<>();
    private int pbLineNumber;
    private final int startOfTempMemoryAddress = 1000;
    private final int dataSectionStart = 500;
    private final int MAXARRAYSIZE = (startOfTempMemoryAddress - dataSectionStart) / 10;
    private int lastDataPointer = dataSectionStart;
    private int lastTempMemory = startOfTempMemoryAddress;
    private ArrayList<String> programBlock = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> breakablesLines = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> continuableLines = new ArrayList<>();
    private HashMap<String, Integer> variableDeclarations = new HashMap<>(); // variables place in memory
    private HashMap<String, Integer> functionDeclaration = new HashMap<>(); // functions start line in program block
    private HashMap<String, String> typeOfVariablesAndFunctions = new HashMap<>(); // int void int[] (or int_10) ... function -> int.int_int_int[]
    private HashMap<String, ArrayList<String>> argumentsOfFunction = new HashMap<>();
    private ArrayList<Integer> tempForParams = new ArrayList<>();
    private ArrayList<String> nameStack = new ArrayList<>();
    private boolean isFirstCase = true;
    private final int dollar_ra = startOfTempMemoryAddress - 4;
    private String currentFunction = "";
    private ArrayList<Integer> returnAdresses = new ArrayList<>();

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
                    System.out.println("no while or switch found for break.");
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
                func_addr_in_mem_save();
                break;
            case "#func_jump":
                func_jump();
                break;
            case "#variable_declare_addr":
                variable_declare_addr();
                break;
            case "#array_declare_addr":
                array_declare_addr(nextToken);
                break;
            case "#pid":
                pid(nextToken);
                break;
            case "#bias_to_memory":
                bias_to_memory();
                break;
            case "#assign":
                assign();
                break;
            case "#pop1":
                pop1();
                break;
            case "#push_to_stack_num":
                push_to_stack_num(nextToken);
                break;
            case "#mult":
                mult();
                break;
            case "#push0":
                push0();
                break;
            case "#new_continuable":
                new_continuable();
                break;
            case "#push1":
                push1();
                break;
            case "#sum_or_minus":
                sum_or_minus();
                break;
            case "#lt_or_equal":
                lt_or_equal();
                break;
            case "#return_value":
                return_value();
                break;
            case "#jpf_save_case_and_push_num":
                jpf_save_case_and_push_num(nextToken);
                break;
            case "#continue":
                try {
                    continue0();
                } catch (Exception e){
                    System.out.println("no while found for continue.");
                }
                break;
            case "#default":
                default0();
                break;
            case "#void_error":
                void_error();
                break;
            case "#variable_declare_addr_add_to_hashmap":
                variable_declare_addr_add_to_hashmap();
                break;
            case "#array_declare_addr_without_num":
                array_declare_addr_without_num();
                break;
            case "#final_assign_param":
                final_assign_param();
                break;
            case "#assign_param":
                assign_param();
                break;
            case "#fix_dollar_ra":
                fix_dollar_ra();
                break;
            case "#return":
                return0();
                break;
            default:
                System.err.println(subroutineName);
                System.err.println("Tu executeSubroutineByName Ridi");
        }
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

    int getTempMemory() {
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
        for (int line : continuableLines.get(continuableLines.size() - 1)) {
            programBlock.set(line, "(JP, " + ssFromLast(2) + ", , )");
        }
        continuableLines.remove(continuableLines.size() - 1);

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
        programBlock.set(ssFromLast(0), "(JPF, " + ssFromLast(1) + ", " + (pbLineNumber + 1) + ", )");
        popss(2);
        pushss(pbLineNumber);
        incrementPBLine();
    }

    private void ifsjp() {
        programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
        popss(1);
    }

    private void new_breakable() {
        breakablesLines.add(new ArrayList<>());
    }

    private void new_continuable() {
        continuableLines.add(new ArrayList<>());
    }

    private void break0() {
        breakablesLines.get(breakablesLines.size() - 1).add(pbLineNumber);
        incrementPBLine();
    }

    private void continue0() {
        continuableLines.get(continuableLines.size() - 1).add(pbLineNumber);
        incrementPBLine();
    }

    private void end_of_switch() {
        // for breaks
        for (int line : breakablesLines.get(breakablesLines.size() - 1)) {
            programBlock.set(line, "(JP, " + pbLineNumber + ", , )");
        }
        breakablesLines.remove(breakablesLines.size() - 1);
        isFirstCase = true;
        popss(1); // check
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
                isFirstCase = false;
            } else {
                programBlock.set(pbLineNumber, "(EQ, " + ssFromLast(1) + ", #" + nextToken.getDescription() + ", " + tempMem + ")");
                incrementPBLine();
                programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
                //incrementPBLine();
                popss(1);
                pushss(tempMem);
                save(); // TODO check
            }
        }
    }

    private void case_save() {
        programBlock.set(ssFromLast(0), "(JPF, " + ssFromLast(1) + ", " + pbLineNumber + ", )");
        popss(2);
        save();
    }

    // TODO check
    private void default_epsilon() {
        popss(2);
    }

    private void default0() {
        // TODO
        int t = getTempMemory();
        programBlock.set(ssFromLast(1), "(EQ, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
        programBlock.set(ssFromLast(1) + 1, "(JPF, " + t + ", " + pbLineNumber + ", )");
        popss(2);
    }

    private void push_name(Token nextToken) {
        nameStack.add(nextToken.getDescription());
    }

    // TODO overloading
    private void func_addr_in_mem_save() {
        argumentsOfFunction.put(nameStack.get(nameStack.size()-1), new ArrayList<>());
        if (!nameStack.get(nameStack.size() - 1).equals("main")) {
            save();
        }
//        if (!currentFunction.equals("")) {
//            System.out.println("this inner function wasn't meant to be here!");
//        }
        currentFunction = currentFunction + "." + nameStack.get(nameStack.size() - 1);
        functionDeclaration.put(currentFunction, pbLineNumber);
        nameStack.remove(nameStack.size() - 1);
    }

    private void func_jump() {
        if (!currentFunction.equals(".main")) {
            System.out.println(semanticStack.size());
            for(int val : semanticStack){
                System.out.println(val);
            }
            programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
            popss(1);
        }
        currentFunction = currentFunction.substring(0, currentFunction.lastIndexOf("."));
    }

    private void variable_declare_addr() {
        variableDeclarations.put(currentFunction + "." + nameStack.get(nameStack.size() - 1), lastDataPointer);
        lastDataPointer += 4;
        nameStack.remove(nameStack.size() - 1);
    }

    private void variable_declare_addr_add_to_hashmap() {
        variableDeclarations.put(currentFunction + "." + nameStack.get(nameStack.size() - 1), lastDataPointer);
        if(argumentsOfFunction.get(currentFunction) == null){
            argumentsOfFunction.put(currentFunction, new ArrayList<>());
        }
        argumentsOfFunction.get(currentFunction).add(nameStack.get(nameStack.size()-1));
        lastDataPointer += 4;
        nameStack.remove(nameStack.size() - 1);
    }

    private void array_declare_addr(Token nextToken) {
        variableDeclarations.put(currentFunction + "." + nameStack.get(nameStack.size() - 1) + "[" + nextToken.getDescription() + "]", lastDataPointer);
        lastDataPointer += 4 * Integer.parseInt(nextToken.getDescription());
        nameStack.remove(nameStack.size() - 1);
    }

    private void array_declare_addr_without_num() {
        variableDeclarations.put(currentFunction + "." + nameStack.get(nameStack.size() - 1) + "[" + MAXARRAYSIZE + "]", lastDataPointer);
        argumentsOfFunction.get(currentFunction).add(nameStack.get(nameStack.size()-1) + "[" + MAXARRAYSIZE + "]");
        lastDataPointer += 4 * MAXARRAYSIZE;
        nameStack.remove(nameStack.size() - 1);
    }

    private String checkArrayInHashMap(String name, HashMap<String, Integer> hashMap) {
        for (String s : hashMap.keySet()) {
            if (s.startsWith(name) && s.charAt(name.length()) == '[') {
                return s;
            }
        }
        return null;
    }

    private void pid(Token nextToken) {
        // TODO check uniqeness #pid a[3] vali int a dashtim (ya barax)
        // TODO halat in ke ye esme tabe va moteghayer yeki bashan handle nemishe
        if (variableDeclarations.containsKey(currentFunction + "." + nextToken.getDescription())) {
            pushss(variableDeclarations.get(currentFunction + "." + nextToken.getDescription()));
        } else {
            String arrayName = checkArrayInHashMap(currentFunction + "." + nextToken.getDescription(), variableDeclarations);
            if (arrayName != null) {
                pushss(variableDeclarations.get(arrayName));
            } else if (variableDeclarations.containsKey("." + nextToken.getDescription())) {
                pushss(variableDeclarations.get("." + nextToken.getDescription()));
            } else {
                String globalArrayName = checkArrayInHashMap("." + nextToken.getDescription(), variableDeclarations);
                if (globalArrayName != null) {
                    pushss(variableDeclarations.get(globalArrayName));
                } else if (functionDeclaration.containsKey("." + nextToken.getDescription())) {
                    pushss(functionDeclaration.get("." + nextToken.getDescription()));
                } else {
                    System.out.println(nextToken.getDescription() + " is not defined.");
                }
            }
        }
    }

    private void bias_to_memory() {
        semanticStack.set(semanticStack.size() - 2, semanticStack.get(semanticStack.size() - 2) + 4 * semanticStack.get(semanticStack.size() - 1));
        popss(1);
    }

    private void assign() {
        programBlock.set(pbLineNumber, "(ASSIGN, " + ssFromLast(0) + ", " + ssFromLast(1) + ", )");
        incrementPBLine();
        popss(1);
    }

    private void pop1() {
        popss(1);
    }

    private void push_to_stack_num(Token nextToken) {
        int t = getTempMemory();
        programBlock.set(pbLineNumber, "(ASSIGN, " + "#" + nextToken.getDescription() + ", " + t + ", )");
        pushss(t);
        incrementPBLine();
    }

    private void mult() {
        int t = getTempMemory();
        programBlock.set(pbLineNumber, "(MULT, " + ssFromLast(0) + ", " + ssFromLast(1) + ", " + t + ")");
        incrementPBLine();
        popss(2);
        pushss(t);
    }

    private void push0() {
        pushss(0);
    }

    private void push1() {
        pushss(1);
    }

    private void sum_or_minus() {
        int t = getTempMemory();
        if (ssFromLast(1) == 0) {
            programBlock.set(pbLineNumber, "(ADD, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
        } else {
            programBlock.set(pbLineNumber, "(SUB, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
        }
        popss(3);
        pushss(t);
        incrementPBLine();
    }

    private void lt_or_equal() {
        int t = getTempMemory();
        if (ssFromLast(1) == 0)
            programBlock.set(pbLineNumber, "(LT, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
        else
            programBlock.set(pbLineNumber, "(EQ, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
        popss(3);
        pushss(t);
        incrementPBLine();

    }

    // TODO jump after return
    private void return_value() {
//        programBlock.set(pbLineNumber, "(ASSIGN, " + returnAdresses.get(returnAdresses.size()-1) + ", " + dollar_ra + ", )");
//        popss(1);
//        incrementPBLine();

    }

    private void fix_dollar_ra(){
        returnAdresses.add(dollar_ra);
        programBlock.set(pbLineNumber, "(ASSIGN, " + (pbLineNumber+2) + ", " + dollar_ra + ", )");
        incrementPBLine();
        programBlock.set(pbLineNumber, "(JP, " + ssFromLast(0) + ", , )");
        incrementPBLine();
        programBlock.set(pbLineNumber, "(ASSIGN, " + returnAdresses.get(returnAdresses.size()-1) + ", " + dollar_ra + ", )");
        incrementPBLine();
        returnAdresses.remove(returnAdresses.size()-1);
        popss(1);
    }

    private void return0(){
        programBlock.set(pbLineNumber, "(JP, @" + dollar_ra + ", , )");
        incrementPBLine();
        // popss(1);
    }

    // TODO first and last one
    private void jpf_save_case_and_push_num(Token nextToken) {
        int t1 = getTempMemory();

        if (!isFirstCase) {
            int t = getTempMemory();
            programBlock.set(pbLineNumber, "(JP, " + (pbLineNumber + 4) + ", , )");
            incrementPBLine();
            programBlock.set(pbLineNumber, "(ASSIGN, " + "#" + nextToken.getDescription() + ", " + t1 + ", )");
            incrementPBLine();
            programBlock.set(ssFromLast(1), "(EQ, " + ssFromLast(2) + ", " + ssFromLast(0) + ", " + t + ")");
            programBlock.set(ssFromLast(1) + 1, "(JPF, " + t + ", " + (pbLineNumber - 1) + ", )");
            popss(2);
            save();
            incrementPBLine();
        } else {
            programBlock.set(pbLineNumber, "(ASSIGN, " + "#" + nextToken.getDescription() + ", " + t1 + ", )");
            incrementPBLine();
            save();
            incrementPBLine();
            isFirstCase = false;
        }

        pushss(t1);
    }

    private void void_error(){
        System.out.println("Illegal type of void.");
    }

    private void assign_param(){
        tempForParams.add(ssFromLast(0));
        //variableDeclarations.get(argumentsOfFunction.get(funcName))
        popss(1);
    }

    private void final_assign_param(){
        String funcName = findFunctionNameByAddress(ssFromLast(0));
        int ind = 0;
        if(tempForParams.size() != argumentsOfFunction.get(funcName).size()){
            System.out.println("Mismatch in number of arguments of " + funcName);
        }
        for(String variableName : argumentsOfFunction.get(funcName)){
            programBlock.set(pbLineNumber, "(ASSIGN, " + tempForParams.get(ind) + ", " + variableDeclarations.get(variableName) + ", )");
            incrementPBLine();
            ind++;
        }
        tempForParams = new ArrayList<>();
        popss(1);
    }

    //------------------------other functions---------------------------

    private String findFunctionNameByAddress(int addr){
        for(String name : functionDeclaration.keySet()){
            if(functionDeclaration.get(name) == addr){
                return name;
            }
        }
        return null;
    }

    //------------------------getter setter------------------------------

    public ArrayList<String> getProgramBlock() {
        return programBlock;
    }
}
