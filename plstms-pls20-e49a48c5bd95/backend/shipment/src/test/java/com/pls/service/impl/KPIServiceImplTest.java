package com.pls.service.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.KPIDao;
import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;
import com.pls.core.service.impl.KPIServiceImpl;

/**
 * KPI services test.
 *
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class KPIServiceImplTest {
    private static final Long ORG_ID = 1L;

    @InjectMocks
    private KPIServiceImpl sut;

    @Mock
    private KPIDao dao;

    @Test
    public void testDailyActivityReport() {
        sut.getDailyLoadActivityReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getDailyLoadActivityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testDestinationReport() {
        sut.getDestinationReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getDestinationReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testFilterValues() {
        sut.getFilterValues(ORG_ID);

        verify(dao).getFilterValues(ORG_ID);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testFreightSpendAnalysisReports() {
        sut.getFreightSpendAnalysisReports(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getFreightSpendAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testWeightAnalysisReports() {
        sut.getWeightAnalysisReports(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getWeightAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportDailyLoadActivityReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportDailyLoadActivityReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getDailyLoadActivityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportFreightSpendAnalysisReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportFreightSpendAnalysisReports(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getFreightSpendAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportWeightAnalyisReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportWeightAnalysisReports(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getWeightAnalysisReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testGeographicalSummaryReports() {
        sut.getGeographicSummaryReports(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getGeographicSummaryReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportGeographicalSummaryReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportGeographicSummaryReports(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getGeographicSummaryReports(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportCarrierSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportCarrierSummaryReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getCarrierSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportClassSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportClassSummaryReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getClassSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportCustomerSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportCustomerSummaryReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getCustomerSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportSeasonalityReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportSeasonalityReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getSeasonalityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportShipmentOverviewReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportShipmentOverviewReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getShipmentOverviewReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testExportVendorSummaryReport() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sut.exportVendorSummaryReport(ORG_ID, new KPIReportFiltersBO(), outputStream);

        verify(dao).getVendorSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testCarrierSummaryReport() throws IOException {
        sut.getCarrierSummaryReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getCarrierSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testClassSummaryReport() throws IOException {
        sut.getClassSummaryReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getClassSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testCustomerSummaryReport() throws IOException {
        sut.getCustomerSummaryReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getCustomerSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testSeasonalityReport() throws IOException {
        sut.getSeasonalityReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getSeasonalityReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testShipmentOverviewReport() throws IOException {
        sut.getShipmentOverviewReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getShipmentOverviewReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testVendorSummaryReport() throws IOException {
        sut.getVendorSummaryReport(ORG_ID, new KPIReportFiltersBO());

        verify(dao).getVendorSummaryReport(eq(ORG_ID), notNull(KPIReportFiltersBO.class));
        verifyNoMoreInteractions(dao);
    }
}
