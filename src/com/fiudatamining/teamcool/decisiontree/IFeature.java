package com.fiudatamining.teamcool.decisiontree;

import java.util.List;

public interface IFeature {
    String getAttrName();

    List<String> getAttrValues();

    List<List<ISampleItem>> split(List<ISampleItem> data);
}
