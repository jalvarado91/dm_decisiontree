package com.fiudatamining.teamcool.decisiontree;

import java.util.HashMap;

public class SimpleSampleItem implements ISampleItem {

    private HashMap<String, Object> values = new HashMap<>();

    private String labelColumn;

    private SimpleSampleItem(String labelColumn, String[] columnHeaders, Object... values) {
        super();
        this.labelColumn = labelColumn;
        for (int i = 0; i < columnHeaders.length; i++) {
            this.values.put(columnHeaders[i], values[i]);
        }
    }

    @Override
    public Object getValue(String column) {
        return this.values.get(column);
    }

    public static SimpleSampleItem newSimpleSampleItem(String labelColumn, String[] columnHeaders, Object... values) {
        return new SimpleSampleItem(labelColumn, columnHeaders, values);
    }
}
