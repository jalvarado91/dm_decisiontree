/**
 * Sandra Hurtado - 4157695
 * Gabriel Jose Perez Clark - 6029184
 * Juan Alvarado - 3367805
 * Uchenna Ohaeto - 5119978
 *
 * Section RVC
 */
package com.fiudatamining.teamcool.decisiontree;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class DecisionTree {
    private Node root;
    private String labelName;

    private int maxLevel = 4;

    public DecisionTree() {
    }

    public void train(List<ISampleItem> trainingTuples, List<IFeature> features) {
        root = makeTree(trainingTuples, features, 1);
    }

    public String classify(ISampleItem testItem) {
        Node node = root;
        while (!node.isLeaf()) {
            String relevantAttribute = node.getFeature().getAttrName();
            Collection<String> edges = node.getChildren().keySet();

            String targetEdge = null;
            for (String edge : edges) {
                if (testItem.getValue(relevantAttribute).equals(edge)) {
                    targetEdge = edge;
                    break;
                }
            }

            node = node.getChildren().get(targetEdge);
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
        for (IFeature feature : features) {
            double newGain = calculateGain(data, feature);
            if (newGain > maxGain) {
                candidateFeature = feature;
                maxGain = newGain;
            }
            gains.put(feature, newGain);
        }

        candidateFeature = gains.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();

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
        printSubTree(root, 0);
    }

    public void printSubTree(Node node, int level) {
        String spacer = "";
        for (int i = 1; i <= level; i++) spacer += "\t";
        printNode(node, spacer);
        if (!node.getChildren().isEmpty()) {
            spacer += "\t";
            Collection<Node> children = node.getChildren().values();
            Collection<String> attrs = node.getChildren().keySet();
            Iterator<Node> chIt = children.iterator();
            Iterator<String> attIt = attrs.iterator();
            while (chIt.hasNext() && attIt.hasNext()) {
                System.out.println(spacer + "-> " + attIt.next());
                System.out.print(spacer + "    ");
                printSubTree(chIt.next(), level + 1);
            }
        }
    }

    private void printNode(Node node, String spacer) {
        if (node.isLeaf()) {
            System.out.print(spacer);
            System.out.print(labelName + ": ");
            System.out.print(node.getLabel());
        }
        else {
            System.out.print(node.getFeature().getAttrName().toUpperCase());
        }
        System.out.println();
    }
}
