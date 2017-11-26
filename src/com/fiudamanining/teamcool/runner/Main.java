package com.fiudamanining.teamcool.runner;

import com.fiudatamining.teamcool.decisiontree.DecisionTree;
import com.fiudatamining.teamcool.decisiontree.IFeature;
import com.fiudatamining.teamcool.decisiontree.ISampleItem;
import com.fiudatamining.teamcool.decisiontree.SimpleSampleItem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Main {
    public static void main(String[] args) {

        List<IFeature> features = getFeatures();
        List<ISampleItem> data = getData();

        DecisionTree tree = new DecisionTree();
        tree.train(data, features);

        tree.printTree();

        ISampleItem pSample = SimpleSampleItem.newSimpleSampleItem(
                "buys_computer",
                new String[] { "age","income","student","credit_rating" },
                new String[] { "senior", "high", "yes", "excellent" }
        );

        String prediction = tree.classify(pSample);
        System.out.println("Sample: " + pSample + ", Prediction: " + pSample.getLabelName() + ": " + prediction);
    }

    private static List<IFeature> getFeatures() {
        IFeature ageFeature = new AgeFeature("age", Arrays.asList("youth", "middle_aged", "senior"));
        IFeature incomeFeature = new IncomeFeature("income", Arrays.asList("low", "medium", "high"));
        IFeature studentFeature = new StudentFeature("student", Arrays.asList("yes", "no"));
        IFeature creditFeature = new CreditFeature("credit_rating", Arrays.asList("fair", "excellent"));

        List<IFeature> features = Arrays.asList(
                ageFeature,
                incomeFeature,
                studentFeature,
                creditFeature);
        return features;
    }

    private static List<ISampleItem> getData() {
        List<String> attrs = Arrays.asList("age","income","student","credit_rating","buys_computer");
        List<List<String>> data = rawData();
        List<ISampleItem> sampleData = data.stream()
                .map(row -> SimpleSampleItem.newSimpleSampleItem("buys_computer", (String[]) attrs.toArray(), row.toArray()))
                .collect(Collectors.toList());
        return sampleData;
    }

    private static List<List<String>> rawData() {
        return Arrays.asList(
                Arrays.asList("youth", "high", "no", "fair", "no"),
                Arrays.asList("youth", "high", "no", "excellent", "no"),
                Arrays.asList("middle_aged", "high", "no", "fair", "yes"),
                Arrays.asList("senior", "medium", "no", "fair", "yes"),
                Arrays.asList("senior", "low", "yes", "fair", "yes"),
                Arrays.asList("senior", "low", "yes", "excellent", "no"),
                Arrays.asList("middle_aged", "low", "yes", "excellent", "yes"),
                Arrays.asList("youth", "medium", "no", "fair", "no"),
                Arrays.asList("youth", "low", "yes", "fair", "yes"),
                Arrays.asList("senior", "medium", "yes", "fair", "yes"),
                Arrays.asList("youth", "medium", "yes", "excellent", "yes"),
                Arrays.asList("middle_aged", "medium", "no", "excellent", "yes"),
                Arrays.asList("senior", "medium", "no", "excellent", "no"),
                Arrays.asList("middle_aged", "high", "yes", "fair", "yes")
        );
    }
}

class AgeFeature extends BaseFeature {
    public AgeFeature(String attrName, List<String> attrValues) {
        super(attrName, attrValues);
    }
}

class IncomeFeature extends BaseFeature {
    public IncomeFeature(String attrName, List<String> attrValues) {
        super(attrName, attrValues);
    }
}

class StudentFeature extends BaseFeature {
    public StudentFeature(String attrName, List<String> attrValues) {
        super(attrName, attrValues);
    }
}

class CreditFeature extends BaseFeature {
    public CreditFeature(String attrName, List<String> attrValues) {
        super(attrName, attrValues);
    }
}

class BuysPCFeature extends BaseFeature {
    public BuysPCFeature(String attrName, List<String> attrValues) {
        super(attrName, attrValues);
    }

}

abstract class BaseFeature implements IFeature {

    private String attrName;
    private List<String> attrValues;

    public BaseFeature(String attrName, List<String> attrValues) {
        this.attrName = attrName;
        this.attrValues = attrValues;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public List<String> getAttrValues() {
        return attrValues;
    }

    public void setAttrValues(List<String> attrValues) {
        this.attrValues = attrValues;
    }

    @Override
    public List<List<ISampleItem>> split(List<ISampleItem> data) {
        List<List<ISampleItem>> result = new ArrayList<>();

        for (String currAttr : attrValues) {
            List<ISampleItem> byAttr = data.stream()
                    .filter(sample -> sample.getValue(attrName).equals(currAttr))
                    .collect(Collectors.toList());

            if (!byAttr.isEmpty()) {
                result.add(byAttr);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getAttrName() + ", " + getAttrValues() +")";
    }
}

