package com.pls.export;

import java.io.Serializable;
import java.util.List;

/**
 * Class which contains data for export.
 *
 * @author Mykola Teslenko
 */
public class ExportData implements Serializable {

    private static final long serialVersionUID = -3182031210440456958L;

    private String fileName;
    private String sheetName;
    private List<String> columnNames;
    private List<ExcelRow> data;
    private List<List<String>> footerData;
    private List<List<String>> headerData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<ExcelRow> getData() {
        return data;
    }

    public void setData(List<ExcelRow> data) {
        this.data = data;
    }

    public List<List<String>> getFooterData() {
        return footerData;
    }

    public void setFooterData(List<List<String>> footerData) {
        this.footerData = footerData;
    }

    public List<List<String>> getHeaderData() {
        return headerData;
    }

    public void setHeaderData(List<List<String>> headerData) {
        this.headerData = headerData;
    }

}
