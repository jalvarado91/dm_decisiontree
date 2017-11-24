package com.fiudatamining.teamcool.decisiontree;

import java.util.List;

public class DecisionTree {
    private Node root;

    public DecisionTree(Node root) {
        this.root = root;
    }

    public void train(List<ISampleItem> trainingTuples, List<String> features) {
        root = makeTree(trainingTuples, features, 1);
    }

    protected Node makeTree(List<ISampleItem> trainingTuples, List<String> features, int currentLevel) {
        Node n = new Node();
        return n;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
}
