package com.company.Parser;

import java.util.ArrayList;
import java.util.Stack;

public class Subroutines {
    private Logger logger;
    private ArrayList<Integer> semanticStack = new ArrayList<>();
    private int pbLineNumber;
    ArrayList<String> programBlock = new ArrayList<>();
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
        }
    }

    private void incrementPBLine(){
        pbLineNumber++;
        programBlock.add("");
    }

    private void label(){
        semanticStack.add(pbLineNumber);
    }

    private void save(){
        semanticStack.add(pbLineNumber);
        incrementPBLine();
    }

    private void while0(){
        programBlock.set(semanticStack.get(semanticStack.size()-1), "(JPF, " + semanticStack.get(semanticStack.size()-2) + ", " + (pbLineNumber + 1) + ", )");
        programBlock.set(pbLineNumber, "(JP, " + semanticStack.get(semanticStack.size()-3) + ", , )");
        incrementPBLine();
        for (int i = 0; i < 3; i++) {
            semanticStack.remove(semanticStack.size()-1);
        }
    }
}
