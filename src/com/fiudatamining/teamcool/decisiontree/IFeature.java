package com.fiudatamining.teamcool.decisiontree;

import java.util.List;

public interface IFeature {
    String getAttrName();

    List<String> getAttrValues();

    boolean belongsTo(ISampleItem sampleItem);

    List<List<ISampleItem>> split(List<ISampleItem> data);
}
