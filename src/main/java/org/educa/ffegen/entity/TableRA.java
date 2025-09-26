package org.educa.ffegen.entity;


import lombok.Data;

import java.util.List;

@Data
public class TableRA {

    private List<RAData> excelRA;
    private int numTableInDoc;

    public TableRA(List<RAData> excelRA, int numTableInDoc) {
        this.excelRA = excelRA;
        this.numTableInDoc = numTableInDoc;
    }

}
