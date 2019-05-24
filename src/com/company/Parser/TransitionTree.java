package com.company.Parser;

import java.util.ArrayList;

public class TransitionTree {
    private Node root;
    private ArrayList<Node> nodes;
    private Node currentNode;

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }
}
