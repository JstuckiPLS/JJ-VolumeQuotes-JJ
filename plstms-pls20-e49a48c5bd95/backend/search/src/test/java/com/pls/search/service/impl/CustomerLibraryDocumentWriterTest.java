package com.pls.search.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.common.utils.DateUtility;
import com.pls.search.domain.vo.CustomerLibraryVO;
import com.pls.search.service.impl.file.CustomerLibraryDocumentWriter;

/**
 * Test cases for {@link CustomerLibraryDocumentWriter}.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryDocumentWriterTest {

    private CustomerLibraryDocumentWriter writer = new CustomerLibraryDocumentWriter();

    private static final String[] HEADER = new String[] { "Customer", "Account Exec", "Added", "Last Load" };

    private static final int FIRST_CELL = 0;
    private static final int SECCOND_CELL = 1;
    private static final int THIRD_CELL = 2;
    private static final int FOURTH_CELL = 3;

    @Test
    public void testCreateFileBody() throws Exception {
        List<CustomerLibraryVO> list = new ArrayList<CustomerLibraryVO>();
        list.add(createTestObject("Customer1", "Person1", new Date(), new Date()));
        list.add(createTestObject("Customer2", "Person2", new Date(), new Date()));

        byte[] documentBytes = writer.createFileBody(list);

        Assert.assertNotNull(documentBytes);
        Assert.assertFalse(documentBytes.length == 0);

        ByteArrayInputStream inStream = new ByteArrayInputStream(documentBytes);

        assertInputStream(list, inStream);
    }

    private CustomerLibraryVO createTestObject(String customerName, String accExecName, Date createdDate,
            Date lastLoadDate) {
        CustomerLibraryVO vo = new CustomerLibraryVO();

        vo.setCustomerName(customerName);
        vo.setAccountExecName(accExecName);
        vo.setCreatedDate(createdDate);
        vo.setLastLoadDate(lastLoadDate);

        return vo;
    }

    private void assertInputStream(List<CustomerLibraryVO> list, InputStream inStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inStream);
        Sheet sheet = workbook.getSheetAt(0);

        assertSheet(list, sheet);
    }

    private void assertSheet(List<CustomerLibraryVO> list, Sheet sheet) throws Exception {
        assertHeader(HEADER, sheet.getRow(0));
        assertRow(list.get(0), sheet.getRow(1));
        assertRow(list.get(1), sheet.getRow(2));
    }

    private void assertHeader(String[] expectedHeader, Row rowHeader) {
        Cell cell = rowHeader.getCell(FIRST_CELL);
        Assert.assertEquals(expectedHeader[FIRST_CELL], cell.getStringCellValue());
        cell = rowHeader.getCell(SECCOND_CELL);
        Assert.assertEquals(expectedHeader[SECCOND_CELL], cell.getStringCellValue());
        cell = rowHeader.getCell(THIRD_CELL);
        Assert.assertEquals(expectedHeader[THIRD_CELL], cell.getStringCellValue());
        cell = rowHeader.getCell(FOURTH_CELL);
        Assert.assertEquals(expectedHeader[FOURTH_CELL], cell.getStringCellValue());
    }

    private void assertRow(CustomerLibraryVO entity, Row actualRow) throws Exception {
        Cell cell = actualRow.getCell(FIRST_CELL);
        Assert.assertEquals(entity.getCustomerName(), cell.getStringCellValue());
        cell = actualRow.getCell(SECCOND_CELL);
        Assert.assertEquals(entity.getAccountExecName(), cell.getStringCellValue());

        cell = actualRow.getCell(THIRD_CELL);
        String expectedCreatedDate = DateUtility.dateToString(entity.getCreatedDate(), DateUtility.POSITIONAL_DATE);
        Assert.assertEquals(expectedCreatedDate, cell.getStringCellValue());

        cell = actualRow.getCell(FOURTH_CELL);
        String expectedLastLoadDate = DateUtility.dateToString(entity.getCreatedDate(), DateUtility.POSITIONAL_DATE);
        Assert.assertEquals(expectedLastLoadDate, cell.getStringCellValue());
    }
}
