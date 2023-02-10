/**
 * 
 */
package com.pls.shipment.service.xls;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.shipment.domain.bo.AccessorialReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.bo.ProductReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Test cases for {@link SavingsReportExcelBuilder} class.
 * 
 * @author Alexander Nalapko
 *
 */
public class SavingsReportExcelBuilderTest {

    DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.US);

    private static  final int LINE_COUNT = 10;

    private ClassPathResource savingsReportResource = new ClassPathResource("/templates/SavingsRpt.xlsx");

    SavingsReportExcelBuilder builder = null;

    @Test
    public void exportSavingsReport() throws ParseException, IOException {

        InputStream templateEmpty = savingsReportResource.getInputStream();
        Workbook workbookEmpty = new XSSFWorkbook(templateEmpty);
        Sheet mainSheetEmpty = workbookEmpty.getSheetAt(0);
        mainSheetEmpty.setDisplayGridlines(false);

        SavingsReportExcelBuilder builder = new SavingsReportExcelBuilder(savingsReportResource);
        Date endDate = format.parse("01/01/2015");
        Date startDate = format.parse("01/01/2012");
        List<FreightAnalysisReportBO> reports = new ArrayList<FreightAnalysisReportBO>();

        for (int i = 0; i < LINE_COUNT; i++) {
            reports.add(getFreightAnalysisReportBO());
        }

        ReportParamsBO reportParams = new ReportParamsBO();
        reportParams.setCustomerId(1L);
        reportParams.setCustomerName("PLS");
        reportParams.setStartDate(startDate);
        reportParams.setEndDate(endDate);
        FileInputStreamResponseEntity entity = builder.generateReport(reports, reportParams);
        System.out.println(entity);

        InputStream template = entity.getBody().getInputStream();
        Workbook workbook = new XSSFWorkbook(template);
        Sheet mainSheet = workbook.getSheetAt(0);
        mainSheet.setDisplayGridlines(false);

        Assert.assertEquals(mainSheetEmpty.getLastRowNum() + LINE_COUNT, mainSheet.getLastRowNum());
        Assert.assertEquals(
                "PLS Logistics Customer Savings Report for  " + format.format(startDate) + " to "
                        + format.format(endDate), mainSheet.getRow(0).getCell(0).getStringCellValue());
        Assert.assertNotEquals(mainSheetEmpty.getRow(0).getCell(0).getStringCellValue(), mainSheet.getRow(0).getCell(0)
                .getStringCellValue());
    }

    private FreightAnalysisReportBO getFreightAnalysisReportBO() {
        FreightAnalysisReportBO bo = new FreightAnalysisReportBO();
        bo.setAccessorials((List) new ArrayList<AccessorialReportBO>());
        bo.setBenchmarkAmount(BigDecimal.ZERO);
        bo.setBol("bol");
        bo.setCarrierName("PLS carrier");
        bo.setCustFsCost(BigDecimal.ZERO);
        bo.setCustLhCost(BigDecimal.ZERO);
        bo.setCustomerName("PLS");
        bo.setDeparture(new Date());
        bo.setDestinationAddress("destinationAddress");
        bo.setDestinationCity("DestinationCity");
        bo.setDestinationContact("destinationContact");
        bo.setDestinationStateCode("destinationStateCode");
        bo.setDestinationZip("destinationZip");
        bo.setGlNumber("glNumber");
        bo.setLoadId(1L);
        bo.setOriginAddress("originAddress");
        bo.setOriginCity("originCity");
        bo.setOriginContact("originContact");
        bo.setOriginStateCode("originStateCode");
        bo.setOriginZip("originZip");
        bo.setPoNumber("poNumber");
        bo.setProducts((List) new ArrayList<ProductReportBO>());
        bo.setProNumber("proNumber");
        bo.setScacCode("scacCode");
        bo.setShipmentDirection(ShipmentDirection.INBOUND);
        bo.setShipperRef("shipperRef");
        bo.setTotalRevenue(BigDecimal.ZERO);
        return bo;
    }
}
