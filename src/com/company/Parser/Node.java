package com.company.Parser;

import javafx.util.Pair;

import java.util.ArrayList;

public class Node {
    private Integer id;
    private static int numOfInstances;
    //TODO change Integer to Node
    private ArrayList<Pair<Integer, String>> neighbours;

    public Node() {
        this.id = numOfInstances;
        numOfInstances++;
    }

    public Node(ArrayList<Pair<Integer, String>> neighbours) {
        this.neighbours = neighbours;
    }

    public void addToNeighbours(Integer node_number, String edge) {
        neighbours.add(new Pair<>(node_number, edge));
    }

    public ArrayList<Pair<Integer, String>> getNeighbours() {
        return neighbours;
    }
}
