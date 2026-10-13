package com.example.informixjava.informixdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record InformixQueryResult(
        List<String> columnNames,
        List<List<Object>> rows,
        int updateCount) {

    public InformixQueryResult {
        columnNames = List.copyOf(columnNames);
        rows = rows.stream()
                .map(row -> Collections.unmodifiableList(new ArrayList<>(row)))
                .toList();
    }

    public boolean hasRows() {
        return !columnNames.isEmpty();
    }
}
