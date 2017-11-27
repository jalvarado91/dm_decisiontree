/**
 * Sandra Hurtado - 4157695
 * Gabriel Jose Perez Clark - 6029184
 * Juan Alvarado - 3367805
 * Uchenna Ohaeto - 5119978
 *
 * Section RVC
 */
package com.fiudatamining.teamcool.decisiontree;

import java.util.List;

public interface IFeature {
    String getAttrName();

    List<String> getAttrValues();

    List<List<ISampleItem>> split(List<ISampleItem> data);
}
