package com.pls.restful.kpi;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;
import com.pls.core.domain.bo.dashboard.ScacBO;
import com.pls.core.service.KPIService;
import com.pls.restful.KPIResource;

/**
 * Test cases for {@link KPIResource} class.
 * 
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class KPIResourceTest {
    private static final Long ORG_ID = 1L;

    private List<String> stubList;
    private List<ScacBO> scacBOs;

    @InjectMocks private KPIResource sut;

    @Mock private KPIService service;

    @Before
    public void setUp() {
        stubList = Collections.emptyList();
    }

    @Test
    public void testDailyLoadActivityReports() {
        List<String> stubList = Collections.emptyList();

        sut.getDailyLoadActivityReport(ORG_ID, stubList, scacBOs, stubList, stubList, stubList, stubList, stubList);

        verify(service, times(1)).getDailyLoadActivityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testDestinationReport() {
        sut.getDestinationReport(ORG_ID, stubList, scacBOs, stubList, stubList, stubList);

        verify(service, times(1)).getDestinationReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
    }

    @Test
    public void testFilterValues() {
        sut.getFilterValues(ORG_ID);

        verify(service, times(1)).getFilterValues(ORG_ID);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testGetFreightAnalysisReports() {
        sut.getFreightSpendReports(ORG_ID, stubList, stubList);

        verify(service, times(1)).getFreightSpendAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testWeightAnalysisReport() {
        sut.getWeightAnalysisReports(ORG_ID, stubList, stubList);

        verify(service, times(1)).getWeightAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportDestinationReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportDestinationReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportDestinationReport(ORG_ID, stubList, scacBOs, stubList, stubList, stubList);

        verify(service, times(1)).exportDestinationReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportFreightSpendAnalysisReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportFreightSpendAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportFreightSpendReports(ORG_ID, stubList, stubList);

        verify(service).exportFreightSpendAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportWeightAnalysisReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportWeightAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportWeightAnalysisReports(ORG_ID, stubList, stubList);

        verify(service).exportWeightAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testGeographicalSummaryReport() {
        sut.getGeographicSummaryReports(ORG_ID, stubList, stubList, stubList, stubList, stubList);

        verify(service).getGeographicSummaryReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportCarrierSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportCarrierSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportCarrierSummaryReport(ORG_ID, stubList, stubList, stubList, stubList, stubList);

        verify(service).exportCarrierSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testCarrierSummaryReport() throws IOException {
        sut.getCarrierSummaryReport(ORG_ID, stubList, stubList, stubList, stubList, stubList, stubList);

        verify(service, times(1)).getCarrierSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportClassSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportClassSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportClassSummaryReport(ORG_ID, stubList, scacBOs, stubList);

        verify(service).exportClassSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testClassSummaryReport() throws IOException {
        sut.getClassSummaryReport(ORG_ID, stubList, scacBOs, stubList);

        verify(service, times(1)).getClassSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportCustomerSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportCustomerSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportCustomerSummaryReport(ORG_ID, stubList, stubList, scacBOs, stubList, stubList);

        verify(service).exportCustomerSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testCustomerSummaryReport() throws IOException {
        sut.getCustomerSummaryReport(ORG_ID, stubList, scacBOs, stubList, stubList, stubList);

        verify(service, times(1)).getCustomerSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportSeasonalityReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportSeasonalityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportSeasonalityReport(ORG_ID, stubList, stubList);

        verify(service).exportSeasonalityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testSeasonalityReport() throws IOException {
        sut.getSeasonalityReport(ORG_ID, stubList, stubList);

        verify(service, times(1)).getSeasonalityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportShipmentOverviewReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportShipmentOverviewReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportShipmentOverviewReport(ORG_ID, stubList, scacBOs, stubList, stubList, stubList);

        verify(service).exportShipmentOverviewReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testShipmentOverviewReport() throws IOException {
        sut.getShipmentOverviewReport(ORG_ID, stubList, stubList, scacBOs, stubList, stubList);

        verify(service, times(1)).getShipmentOverviewReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testExportVendorSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doNothing().when(service).exportVendorSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), eq(outputStream));

        sut.exportVendorSummaryReport(ORG_ID, stubList, stubList, scacBOs, stubList);

        verify(service).exportVendorSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class), any(OutputStream.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testVendorSummaryReport() throws IOException {
        sut.getVendorSummaryReport(ORG_ID, stubList, scacBOs, stubList, stubList);

        verify(service, times(1)).getVendorSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(service);
    }
}
