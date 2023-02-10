package com.pls.shipment.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.enums.ExcelReportName;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.util.TestUtils;
import com.pls.shipment.dao.ShipmentReportDao;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;
import com.pls.shipment.domain.enums.DateTypeOption;
import com.pls.shipment.service.impl.ShipmentReportServiceImpl;

/**
 * 
 * Test cases for {@link ShipmentReportServiceImpl} class.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentReportServiceImplTest {
    DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.US);
    private static final Long ORG_ID = 1L;

    @InjectMocks
    private ShipmentReportServiceImpl shipmentReportServiceImpl;

    @Mock
    private ShipmentReportDao shipmentReportDao;

    //Mocked Data
    private Date endDate;
    private Date startDate;

    @Before
    public void init() throws ParseException {
        endDate = format.parse("08/12/2015");
        startDate = format.parse("08/12/2012");
        TestUtils.instantiateClassPathResource("activityReportResource", shipmentReportServiceImpl);
        TestUtils.instantiateClassPathResource("revenueReport", shipmentReportServiceImpl);
        TestUtils.instantiateClassPathResource("shipmentCreationReport", shipmentReportServiceImpl);
    }

    @Test
    public void testGetReportDataForUnbilledReport() throws IOException, ParseException {
        Calendar calendar = DateUtility.getCalenderInstance();
        calendar.set(2008, 4, 9);
        FileInputStreamResponseEntity reportsBO = shipmentReportServiceImpl.exportReport(getReportParamsBo(
                ExcelReportName.UNBILLED.getName(), ORG_ID, calendar.getTime(), calendar.getTime(), null));
        Assert.assertNotNull(reportsBO);
    }

    @Test
    public void testGetReportDataForActivityReport() throws IOException, ParseException {
        List<FreightAnalysisReportBO> data = new ArrayList<FreightAnalysisReportBO>();
        Mockito.when(shipmentReportDao.getActivityReport(1L, null, startDate, endDate, DateTypeOption.GL)).thenReturn(data);
        FileInputStreamResponseEntity reportsBO = shipmentReportServiceImpl.exportReport(
                getReportParamsBo(ExcelReportName.ACTIVITY.getName(), ORG_ID, startDate, endDate, "Customer Vasja"));
        Assert.assertNotNull(reportsBO);
    }

    @Test
    public void testGetCreationReport() throws IOException, ParseException {
        List<CreationReportBO> data = new ArrayList<CreationReportBO>();
        Mockito.when(shipmentReportDao.getCreationReport(1L, null, startDate, endDate, true)).thenReturn(data);
        FileInputStreamResponseEntity reportsBO = shipmentReportServiceImpl.exportReport(
                getReportParamsBo(ExcelReportName.SHIPMENT_CREATION.getName(), ORG_ID, startDate, endDate, "Customer Name"));
        Assert.assertNotNull(reportsBO);
    }

    private ReportParamsBO getReportParamsBo(String reportName, Long custId, Date startDt, Date endDt,
            String custName) {
        ReportParamsBO reportParamsBo = new ReportParamsBO();
        reportParamsBo.setReportName(reportName);
        reportParamsBo.setCustomerId(custId);
        reportParamsBo.setStartDate(startDt);
        reportParamsBo.setEndDate(endDt);
        reportParamsBo.setCustomerName(custName);
        return reportParamsBo;
    }

}
