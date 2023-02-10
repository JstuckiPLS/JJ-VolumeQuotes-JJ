/**
 * 
 */
package com.pls.shipment.service.xls;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;

/**
 * Test cases for {@link ProductsReportExcelBuilder} class.
 * 
 * @author Alexander Nalapko
 *
 */
public class ProductsReportExcelBuilderTest {

    private static final int LINE_COUNT = 10;

    private ClassPathResource savingsReportResource = new ClassPathResource("/templates/Product_Export_Template.xlsx");

    ProductsReportExcelBuilder builder = null;

    @Test
    public void exportSavingsReport() throws ParseException, IOException {

        InputStream templateEmpty = savingsReportResource.getInputStream();
        Workbook workbookEmpty = new XSSFWorkbook(templateEmpty);
        Sheet mainSheetEmpty = workbookEmpty.getSheetAt(0);
        mainSheetEmpty.setDisplayGridlines(false);

        ProductsReportExcelBuilder builder = new ProductsReportExcelBuilder(savingsReportResource);
        List<LtlProductEntity> reports = new ArrayList<LtlProductEntity>();

        for (int i = 0; i < LINE_COUNT; i++) {
            reports.add(getLtlProductEntity());
        }

        FileInputStreamResponseEntity entity = builder.generateReport(reports);
        System.out.println(entity);

        InputStream template = entity.getBody().getInputStream();
        Workbook workbook = new XSSFWorkbook(template);
        Sheet mainSheet = workbook.getSheetAt(0);
        mainSheet.setDisplayGridlines(false);

        int descriptionColumnSize = mainSheet.getColumnWidth(0);
        int instructionColumnSize = mainSheet.getColumnWidth(13);
        int descriptionValueLength = mainSheet.getRow(1).getCell(0).getStringCellValue().length();
        int instructionValueLength = mainSheet.getRow(1).getCell(13).getStringCellValue().length();
        Assert.assertTrue(descriptionValueLength > instructionValueLength && descriptionColumnSize < instructionColumnSize);

        Assert.assertEquals(mainSheetEmpty.getLastRowNum() + LINE_COUNT - 1, mainSheet.getLastRowNum());
    }

    private LtlProductEntity getLtlProductEntity() {
        LtlProductEntity bo = new LtlProductEntity();
        bo.setDescription("Description Description");
        bo.setNmfcNum("NmfcNum");
        bo.setCommodityClass(CommodityClass.CLASS_100);
        bo.setProductCode("ProductCode");
        bo.setShared(true);
        LtlProductHazmatInfo info = new LtlProductHazmatInfo();
        info.setUnNumber("UnNumber");
        info.setPackingGroup("PackingGroup");
        info.setHazmatClass("HazmatClass");
        info.setEmergencyCompany("EmergencyCompany");
        info.setEmergencyPhone(new PhoneEmbeddableObject());
        info.setEmergencyPhone(new PhoneEmbeddableObject());
        info.setEmergencyContract("EmergencyContract");
        info.setInstructions("Instructions");
        bo.setHazmatInfo(info);
        return bo;
    }
}
