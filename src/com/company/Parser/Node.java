package com.company.Parser;

import javafx.util.Pair;

import java.util.ArrayList;

public class Node {
    private Integer id;
    private static int numOfInstances;
    //TODO change Integer to Node
    private ArrayList<Pair<Node, String>> neighbours;

    public Node() {
        this.id = numOfInstances;
        numOfInstances++;
    }

    public Node(ArrayList<Pair<Node, String>> neighbours) {
        this.neighbours = neighbours;
    }

    public void addToNeighbours(Node node, String edge) {
        neighbours.add(new Pair<>(node, edge));
    }

    public ArrayList<Pair<Node, String>> getNeighbours() {
        return neighbours;
    }
}
