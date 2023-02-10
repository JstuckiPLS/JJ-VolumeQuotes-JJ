package com.pls.export;

import java.io.Serializable;
import java.util.List;

/**
 * ExcelRow - representation of row.
 * @author Sergey Belodon
 */

public class ExcelRow implements Serializable {

    private static final long serialVersionUID = -3182031211448456950L;

    private List<String> rowData;

    private boolean marked;

    public List<String> getRowData() {
        return rowData;
    }

    public void setRowData(List<String> rowData) {
        this.rowData = rowData;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean isMarked) {
        marked = isMarked;
    }

}
