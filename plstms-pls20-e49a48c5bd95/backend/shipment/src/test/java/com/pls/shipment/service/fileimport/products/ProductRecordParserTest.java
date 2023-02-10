package com.pls.shipment.service.fileimport.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.fileimport.parser.core.Record;
import com.pls.core.service.fileimport.parser.core.excel.ExcelRecord;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.service.product.impl.fileimport.ProductFieldsDescription;
import com.pls.shipment.service.product.impl.fileimport.ProductRecordParser;

/**
 * Test cases of using {@link com.pls.shipment.service.product.impl.fileimport.ProductRecordParser}.
 * 
 * @author Artem Arapov
 *
 */
public class ProductRecordParserTest {

    private static final String TEST_SHEET_NAME = "Test sheet";

    @Test
    public void testValidProductRecord() throws Exception {
        ProductRecordParser sut = new ProductRecordParser();

        Record header = new ExcelRecord(prepareHeader());
        Record record = new ExcelRecord(prepareRow("TestProd1", "nmfc1", "50", "sku1", "yes", "UN1", "PG1", "5"));
        assertNotNull(sut.initialiseHeader(header));
        LtlProductEntity entity = sut.parseRecord(record);

        checkProduct(entity);
    }

    private void checkProduct(LtlProductEntity entity) {
        assertNotNull(entity);
        assertEquals("TestProd1", entity.getDescription());
        assertEquals("nmfc1", entity.getNmfcNum());
        assertEquals(CommodityClass.CLASS_50, entity.getCommodityClass());
        assertEquals("sku1", entity.getProductCode());

        LtlProductHazmatInfo info = entity.getHazmatInfo();
        assertNotNull(info);
        assertEquals("UN1", info.getUnNumber());
        assertEquals("PG1", info.getPackingGroup());
        assertEquals("5", info.getHazmatClass());
    }

    private Row prepareRow(String description, String nmfc, String commodityClass, String productCode, String hazmat, String un,
            String packagingGroup, String hazmatClass) {
        Workbook workbook = new SXSSFWorkbook();
        Row result = workbook.createSheet(TEST_SHEET_NAME).createRow(0);

        Cell cell = result.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(description);

        result.createCell(1).setCellValue(nmfc);
        result.createCell(2).setCellValue(commodityClass);
        result.createCell(3).setCellValue(productCode);
        result.createCell(4).setCellValue(hazmat);
        result.createCell(5).setCellValue(un);
        result.createCell(6).setCellValue(packagingGroup);
        result.createCell(7).setCellValue(hazmatClass);

        return result;
    }

    private Row prepareHeader() {
        Workbook workbook = new SXSSFWorkbook();
        Row result = workbook.createSheet(TEST_SHEET_NAME).createRow(0);

        result.createCell(0).setCellValue(ProductFieldsDescription.DESCRIPTION.getHeader());
        result.createCell(1).setCellValue(ProductFieldsDescription.NMFC_NUM.getHeader());
        result.createCell(2).setCellValue(ProductFieldsDescription.COMMODITY_CLASS.getHeader());
        result.createCell(3).setCellValue(ProductFieldsDescription.SKU.getHeader());
        result.createCell(4).setCellValue(ProductFieldsDescription.HAZMAT_FLAG.getHeader());
        result.createCell(5).setCellValue(ProductFieldsDescription.HAZMAT_UN.getHeader());
        result.createCell(6).setCellValue(ProductFieldsDescription.HAZMAT_PACKING_CODE.getHeader());
        result.createCell(7).setCellValue(ProductFieldsDescription.HAZMAT_CLASS.getHeader());

        return result;
    }
}
