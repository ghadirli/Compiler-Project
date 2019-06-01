package com.company.Parser;

import java.util.ArrayList;

public class GraphNode {
    private String label;
    private Integer depth = 0;
    private ArrayList<GraphNode> children;

    public GraphNode(String label, Integer depth) {
        this.label = label;
        this.depth = depth;
    }

//    GraphNode(String label){
//        this(label)
//    }

    public ArrayList<GraphNode> getChildren() {
        return children;
    }

    public String getLabel() {
        return label;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setChildren(ArrayList<GraphNode> children) {
        this.children = children;
    }


    public void addChild(GraphNode graphNode) {
        this.children.add(graphNode);
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
