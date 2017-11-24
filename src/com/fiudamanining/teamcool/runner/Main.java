package com.fiudamanining.teamcool.runner;

import com.fiudatamining.teamcool.decisiontree.IFeature;
import com.fiudatamining.teamcool.decisiontree.ISampleItem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Feature[] features
        // Tuple[] tuples
        // Tree tree
        // tree.train(features, tuples)

        // tree.printTree();

        // ISampleItem pSample
        // prediction = tree.classify(pSample);
        // print prediction
    }

    private static class AgeFeature implements IFeature {

        private List<String> attrValues;
        public AgeFeature(List<String> attrValues) {
            this.attrValues = attrValues;
        }
    }

    private static HashMap<String, IFeature> getFeatures() {
        HashMap<String, IFeature> features = new HashMap<>();

        IFeature ageFeature = new AgeFeature(Arrays.asList("young", "old", "middle_age"));
        features.put("age", ageFeature);


        IFeature curFeature = features.get("age");


        return features;
    }

    private static List<String> getData() {
        List<String> attrs = Arrays.asList("age","income","student","credit_rating","buys_computer");

        return attrs;
    }

}


