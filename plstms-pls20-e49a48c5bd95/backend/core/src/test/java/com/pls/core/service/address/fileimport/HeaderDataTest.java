package com.pls.core.service.address.fileimport;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

/** Test cases for {@link com.pls.core.service.address.fileimport.HeaderData}.
 * 
 * @author Artem Arapov
 *
 */
public class HeaderDataTest {

    private final HeaderData headerData = new HeaderData();

    @Test(expected = ImportFileInvalidDataException.class)
    public void testHasData_EmptyHeaderRow() throws Exception {
        Row headerRow = createRow();

        headerData.readData(headerRow);
    }

    @Test
    public void testHasData_FullHeaderRow() throws Exception {
        Row headerRow = createRow();
        AddressFields[] fields = AddressFields.values();
        for (int indx = 0; indx < fields.length; indx++) {
            headerRow.createCell(indx).setCellValue(fields[indx].getHeader());
        }

        headerData.readData(headerRow);

        Assert.assertTrue(headerData.hasData());
    }

    @Test
    public void testHasData_MinimalHeaderRow() throws Exception {
        Row headerRow = createRow();
        AddressFields[] fields = AddressFields.values();
        for (int indx = 0; indx < fields.length; indx++) {
            if (fields[indx].isRequired()) {
                headerRow.createCell(indx).setCellValue(fields[indx].getHeader());
            }
        }

        headerData.readData(headerRow);

        Assert.assertTrue(headerData.hasData());
    }

    @Test
    public void testHasData_NullRow() throws Exception {
        headerData.readData(null);

        Assert.assertFalse(headerData.hasData());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testHasData_UnexpectedColumns() throws Exception {
        Row headerRow = createRow();
        headerRow.createCell(0).setCellValue("Some Column");

        headerData.readData(headerRow);
    }

    private Row createRow() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        Row headerRow = sheet.createRow(0);
        return headerRow;
    }
}
