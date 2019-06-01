package com.company.Parser;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.util.ArrayList;

public class GraphNode {
    private String label;
    private Integer depth = 0;
    private ArrayList<GraphNode> children;

    public GraphNode(Integer depth) {
        this.depth = depth;
    }

    public void setChildren(ArrayList<GraphNode> children) {
        this.children = children;
    }


    public void addChildren(GraphNode graphNode) {
        this.children.add(graphNode);
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
