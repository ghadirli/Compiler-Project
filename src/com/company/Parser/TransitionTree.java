package com.company.Parser;

import java.util.ArrayList;

public class TransitionTree {
    private Node root;
    private Node terminal;
    private ArrayList<Node> nodes;
    private Node currentNode;

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void addToNodes(Node node){
        nodes.add(node);
    }
    public TransitionTree() {
        root = new Node();
        nodes = new ArrayList<>();
        terminal = new Node();
        terminal.setEnd(true);
    }

    public Node getTerminal() {
        return terminal;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public Node getRoot() {
        return root;
    }
}
