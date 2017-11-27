/**
 * Sandra Hurtado - 4157695
 * Gabriel Jose Perez Clark - 6029184
 * Juan Alvarado - 3367805
 * Uchenna Ohaeto - 5119978
 *
 * Section RVC
 */
package com.fiudatamining.teamcool.decisiontree;

import java.util.HashMap;

public class SimpleSampleItem implements ISampleItem {

    private HashMap<String, Object> values = new HashMap<>();

    private String labelName;

    private SimpleSampleItem(String labelName, String[] columnHeaders, Object... values) {
        super();
        this.labelName = labelName;
        for (int i = 0; i < columnHeaders.length; i++) {
            this.values.put(columnHeaders[i], values[i]);
        }
    }

    @Override
    public Object getValue(String column) {
        return this.values.get(column);
    }

    public String getLabelName() {
        return labelName;
    }

    public String getLabel() {
        return (String)values.get(labelName);
    }

    public static SimpleSampleItem newSimpleSampleItem(String labelColumn, String[] columnHeaders, Object... values) {
        return new SimpleSampleItem(labelColumn, columnHeaders, values);
    }

    @Override
    public String toString() {
        return "[" + this.values.toString() + "]";
    }
}
