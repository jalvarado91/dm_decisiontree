package com.fiudatamining.teamcool.decisiontree;

import java.util.*;
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
        List<List<ISampleItem>> splitData = bestSplit.split(trainingTuples);

        List<IFeature> cleanedFeatures = features.stream()
                .filter(f -> !f.equals(bestSplit))
                .collect(Collectors.toList());

        Node n = Node.newNode(bestSplit);
        for (List<ISampleItem> trainingSubset : splitData) {
            if (trainingSubset.isEmpty()) {
                n.addChild(Node.newLeafNode(getLabelFromMajorityVoting(trainingTuples)));
            }
            else {
                n.addChild(makeTree(trainingSubset, cleanedFeatures, currLevel + 1));
            }
        }
        return n;
    }

    public static double calculateProbRatio(double yes_count, double no_count, double setSize) {
        double probability = yes_count / setSize != 0 ? yes_count / setSize : 1;
        double probability2 = no_count / setSize != 0 ? no_count / setSize : 1;
        return -(probability * (Math.log(probability) / Math.log(2))) - (probability2 * (Math.log(probability2) / Math.log(2)));
    }

    public static double calculateGain(List<ISampleItem> D, IFeature feature) {
        double yes_count = 0, no_count = 0, setSize;
        Map<String, LinkedHashSet<ISampleItem>> split = new HashMap<>();
        for (ISampleItem t : D) {
            if (t.getValue("buys_computer").equals("yes"))
                yes_count++;
            else
                no_count++;
            String curValue = t.getValue(feature.getAttrName()).toString();
            if (split.containsKey(curValue)) {
                split.get(curValue).add(t);
            } else {
                LinkedHashSet<ISampleItem> ls = new LinkedHashSet<>();
                ls.add(t);
                split.put(curValue, ls);
            }
        }

        setSize = D.size(); //Size of data set
        double infoD = calculateProbRatio(yes_count, no_count, setSize);


        double info_attr_D = 0;
        double sub_yes_count = 0;
        double sub_no_count = 0;
        double subList_size = 0;
        double prob = 0;

        for (HashSet<ISampleItem> hs : split.values()) {
            sub_yes_count = 0;
            sub_no_count = 0;
            for (ISampleItem t : hs) {
                if (t.getValue("buys_computer").equals("yes"))
                    sub_yes_count++;
                else
                    sub_no_count++;
            }
            subList_size = hs.size();
            prob = subList_size / setSize;
            info_attr_D += prob * calculateProbRatio(sub_yes_count, sub_no_count, subList_size);
        }
        return (infoD - info_attr_D);
    }

    protected IFeature getBestSplitAttr(List<ISampleItem> data, List<IFeature> features) {
        double informationGain = 0;
        IFeature candidateFeature = null;

        Map<IFeature, Double> gains = new HashMap<>();
        double maxGain = 0;
        System.out.println("Gains:");
        // TODO: Do information gain calculations
        for (IFeature feature : features) {
            double newGain = calculateGain(data, feature);
            if (newGain > maxGain) {
                candidateFeature = feature;
                maxGain = newGain;
            }
            gains.put(feature, newGain);
            System.out.println(feature.getAttrName() + ": " + gains.get(feature));
        }

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
            Iterator<Node> childIt = node.getChildren().iterator();
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
