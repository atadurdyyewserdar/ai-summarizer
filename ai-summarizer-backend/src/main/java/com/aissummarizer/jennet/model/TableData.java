package com.aissummarizer.jennet.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for a single table
 */
@Data
public class TableData {
    private int tableNumber;
    private List<List<String>> rows = new ArrayList<>();

    public TableData(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public int getRowCount() {
        return rows.size();
    }

    /**
     * Convert table to text format
     */
    public String toText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            text.append("  | ");
            for (String cell : row) {
                text.append(cell).append(" | ");
            }
            text.append("\n");

            // Add separator after first row (header)
            if (i == 0 && rows.size() > 1) {
                text.append("  ");
                for (int j = 0; j < row.size(); j++) {
                    text.append("--------");
                }
                text.append("\n");
            }
        }
        return text.toString();
    }
}