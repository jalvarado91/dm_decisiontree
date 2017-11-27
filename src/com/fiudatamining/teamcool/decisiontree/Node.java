package com.fiudatamining.teamcool.decisiontree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Sandra Hurtado - 4157695
 * Gabriel Jose Perez Clark - 6029184
 * Juan Alvarado - 3367805
 * Uchenna Ohaeto - 5119978
 *
 * Section RVC
 */
public class Node {

    private IFeature feature;

    private String label;

    private HashMap<String, Node> children = new HashMap<>();
    private List<ISampleItem> data;


    private Node(IFeature feature) {
        this.feature = feature;
    }

    private Node(IFeature feature, String label) {
        this.feature = feature;
        this.label = label;
    }

    private Node(String label, List<ISampleItem> data) {
        this.data = data;
    }


    public static Node newNode(IFeature feature) {
        return new Node(feature);
    }

    public static Node newLeafNode(String label) {
        return new Node(null, label);
    }

    public static Node newLeafWithData(String label, List<ISampleItem> data) {
        return new Node(label, data);
    }

    public void addChild(String attr, Node child) {
        children.put(attr, child);
    }

    public HashMap<String, Node> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return label != null;
    }

    public boolean isLeafWithData() {
        return label != null && data != null;
    }

    public IFeature getFeature() {
        return feature;
    }

    public String getLabel() {
        return label;
    }

    public List<ISampleItem> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "[Node: " + "label:" + getLabel() + ", " + "feature: " +getFeature() + "]";
    }
}
