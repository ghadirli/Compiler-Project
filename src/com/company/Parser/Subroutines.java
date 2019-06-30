package com.company.Parser;

import java.util.ArrayList;
import java.util.Stack;

public class Subroutines {
    private Logger logger;
    private ArrayList<Integer> semanticStack = new ArrayList<>();
    private int pbLineNumber;
    private ArrayList<String> programBlock = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> breakLines = new ArrayList<>();
    public Subroutines(Logger logger){
        this.logger = logger;
    }

    public void executeSubroutineByName(String subroutineName){
        switch (subroutineName){
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
                break0();
                break;
        }
    }

    private void popss(int numberToPop){
        for (int i = 0; i < numberToPop; i++) {
            semanticStack.remove(semanticStack.size()-1);
        }
    }

    private void pushss(int val){
        semanticStack.add(val);
    }

    private void incrementPBLine(){
        pbLineNumber++;
        programBlock.add("");
    }

    private int ssFromLast(int bias){
        return semanticStack.get(semanticStack.size()-1-bias);
    }



    //------------------------------subroutines----------------------------------------

    private void label(){
        pushss(pbLineNumber);
    }

    private void save(){
        pushss(pbLineNumber);
        incrementPBLine();
    }

    private void while0(){
        programBlock.set(ssFromLast(0), "(JPF, " + ssFromLast(1) + ", " + (pbLineNumber + 1) + ", )");
        programBlock.set(pbLineNumber, "(JP, " + semanticStack.get(semanticStack.size()-3) + ", , )");
        incrementPBLine();
        popss(3);
    }

    private void ifsjps_save(){
        programBlock.set(ssFromLast(0), "JPF, " + ssFromLast(1) + ", " + pbLineNumber+1 + ", )");
        popss(2);
        pushss(pbLineNumber+1);
        incrementPBLine();
    }

    private void ifsjp(){
        programBlock.set(ssFromLast(0), "(JP, " + pbLineNumber + ", , )");
        popss(1);
    }

    private void new_breakable(){

    }

    private void break0(){

    }
}
