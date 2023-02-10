package com.pls.shipment.service;

import org.junit.Assert;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test for ExcelFileBuilder, that creates excel file for downloading.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
public class ExcelFileBuilderTest {

    @Test
    public void buildExcelFileHeadersTest() throws Exception {
        ExcelFileBuilder builder = new ExcelFileBuilder();
        builder.addHeader("foo");
        builder.addHeader("bar");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildExcelFile(out);
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Workbook w = WorkbookFactory.create(in);
        Sheet sheet = w.getSheetAt(0);
        String columnFooTitle = sheet.getRow(0).getCell(0).getStringCellValue();
        Assert.assertEquals(columnFooTitle, "foo");
        String columnBarTitle = sheet.getRow(0).getCell(1).getStringCellValue();
        Assert.assertEquals(columnBarTitle, "bar");
    }

    @Test
    public void buildExcelFileMarginsTest() throws Exception {
        ExcelFileBuilder builder = new ExcelFileBuilder();
        builder.setLeftMargin(2);
        builder.setTopMargin(2);
        builder.addHeader("foo");
        builder.addHeader("bar");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildExcelFile(out);
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Workbook w = WorkbookFactory.create(in);
        Sheet sheet = w.getSheetAt(0);
        String columnFooTitle = sheet.getRow(2).getCell(2).getStringCellValue();
        Assert.assertEquals(columnFooTitle, "foo");
        String columnBarTitle = sheet.getRow(2).getCell(3).getStringCellValue();
        Assert.assertEquals(columnBarTitle, "bar");
    }

    @Test
    public void buildExcelFileRowsTest() throws Exception {
        ExcelFileBuilder builder = new ExcelFileBuilder();
        builder.addHeader("foo");
        builder.addHeader("bar");
        builder.addHeader("bar");
        builder.addRow("abc", "def", "jf");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildExcelFile(out);
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Workbook w = WorkbookFactory.create(in);
        Sheet sheet = w.getSheetAt(0);
        String columnFooTitle = sheet.getRow(0).getCell(0).getStringCellValue();
        Assert.assertEquals(columnFooTitle, "foo");
        String columnBarTitle = sheet.getRow(0).getCell(1).getStringCellValue();
        Assert.assertEquals(columnBarTitle, "bar");

        String rowAbcCellValue = sheet.getRow(1).getCell(0).getStringCellValue();
        Assert.assertEquals(rowAbcCellValue, "abc");
        String rowDefCellValue = sheet.getRow(1).getCell(1).getStringCellValue();
        Assert.assertEquals(rowDefCellValue, "def");
    }
}
