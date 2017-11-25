package com.fiudatamining.teamcool.decisiontree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class DecisionTree {
    private Node root;
    private String labelName;

    private int maxLevel = 2;

    public DecisionTree() {
    }

    public void train(List<ISampleItem> trainingTuples, List<IFeature> features) {
        root = makeTree(trainingTuples, features, 1);
    }

    public String classify(ISampleItem testItem) {
        Node node = root;
        while (!node.isLeaf()) {
            String currAttr = node.getFeature().getAttrName();
            List<String> currAttributes = node.getFeature().getAttrValues();
        }

        return node.getLabel();
    }

    protected Node makeTree(List<ISampleItem> trainingTuples, List<IFeature> features, int currLevel) {
        labelName = trainingTuples.get(0).getLabelName();
        String currentNodeLabel = null;

        String potentialLabel = getLabelIfSameClass(trainingTuples);
        if (potentialLabel != null) {
            currentNodeLabel = potentialLabel;
            return Node.newLeafNode(currentNodeLabel);
        }

        if (features.isEmpty() || currLevel >= maxLevel) {
            currentNodeLabel = getLabelFromMajorityVoting(trainingTuples);
            return Node.newLeafNode(currentNodeLabel);
        }

        IFeature bestSplit = getBestSplitAttr(trainingTuples, features);
        List<String> splitAttrs = bestSplit.getAttrValues();
        List<List<ISampleItem>> splitData = bestSplit.split(trainingTuples);

        List<IFeature> cleanedFeatures = features.stream()
                .filter(f -> !f.equals(bestSplit))
                .collect(Collectors.toList());

        Node n = Node.newNode(bestSplit);
        Iterator<String> attrsIt = splitAttrs.iterator();
        Iterator<List<ISampleItem>> splitDataIt = splitData.iterator();

        while (attrsIt.hasNext() && splitDataIt.hasNext()) {
            List<ISampleItem> trainingSubset = splitDataIt.next();
            String attributeVal = attrsIt.next();
            if (trainingSubset.isEmpty()) {
                n.addChild(attributeVal, Node.newLeafNode(getLabelFromMajorityVoting(trainingTuples)));
            }
            else {
                n.addChild(attributeVal, makeTree(trainingSubset, cleanedFeatures, currLevel + 1));
            }
        }
        
        return n;
    }

    protected IFeature getBestSplitAttr(List<ISampleItem> data, List<IFeature> features) {
        double informationGain = 0;
        IFeature candidateFeature = null;

        // TODO: Do information gain calculations
//        for (IFeature feature : features) {
//            List<List<ISampleItem>> splitData = feature.split(data);
//
//        }

        candidateFeature = (IFeature)features.stream()
                .filter(f -> f.getAttrName().equals("age"))
                .collect(Collectors.toList())
                .toArray()[0];

        return candidateFeature;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    protected String getLabelIfSameClass(List<ISampleItem> data) {
        Map<String, Long> labelCount = data.stream()
                .collect(groupingBy(ISampleItem::getLabel, counting()));

        return labelCount.keySet().size() == 1
            ? (String)labelCount.keySet().toArray()[0]
            : null;
    }

    protected String getLabelFromMajorityVoting(List<ISampleItem> data) {
        // Make a map of <Label, count> and return the one one with the most counts
        return data.stream()
                .collect(groupingBy(ISampleItem::getLabel, counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    public void printTree() {
        printSubTree(root);
    }

    public void printSubTree(Node node) {
        printNode(node);
        if (!node.getChildren().isEmpty()) {
            List<String> attrValues = node.getFeature().getAttrValues();
            Iterator<String> attrIt = attrValues.iterator();
            Iterator<Node> childIt = node.getChildren().values().iterator();
            while (attrIt.hasNext() && childIt.hasNext()) {
                String spacer = "\t" + attrIt.next() + "-> ";
                printTree(childIt.next(), spacer);
            }
        }
    }

    private void printNode(Node node) {
        if (node.isLeaf()) {
            System.out.print(labelName + ": ");
            System.out.print(node.getLabel());
        }
        else {
            System.out.print(node.getFeature().getAttrName());
        }
        System.out.println();
    }

    public void printTree(Node node, String spacer) {
        System.out.print(spacer);
        printSubTree(node);
    }
}
