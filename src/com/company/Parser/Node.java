package com.company.Parser;

import javafx.util.Pair;

import java.util.ArrayList;

public class Node {
    private Integer id;
    private static int numOfInstances;
    //TODO change Integer to Node
    private ArrayList<Pair<Node, String>> neighbours;
    private boolean isEnd = false;

    public Node() {
        neighbours = new ArrayList<>();
        this.id = numOfInstances;
        numOfInstances++;
    }

    public Integer getId() {
        return id;
    }

    public Node(ArrayList<Pair<Node, String>> neighbours) {
        this.neighbours = neighbours;
    }

    public void addToNeighbours(Node node, String edge) {
        neighbours.add(new Pair<>(node, edge));
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public ArrayList<Pair<Node, String>> getNeighbours() {
        return neighbours;
    }

    public boolean isEnd() {
        return isEnd;
    }
}
