package com.pls.core.dao.impl;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.KPIDao;
import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;

/**
 * Test cases for {@link com.pls.core.dao.impl.KPIDaoImpl}.
 * 
 * @author Nalapko Alexander, Dmitriy Nefedchenko
 */
@Ignore // FIXME Ignore this test file unless define if we still need KPI or not
public class KPIDaoImplIT extends AbstractDaoTest {
    private static final Long ORG_ID = 1L;

    @Autowired
    private KPIDao sut;

    @Test
    public void testDailyLoadActivity() {
        assertNotNull(sut.getDailyLoadActivityReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testKPIReportFilters() {
        assertNotNull(sut.getFilterValues(ORG_ID));
    }

    @Test
    public void testDestinationReports() {
        assertNotNull(sut.getDestinationReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testFreightSpendAnalysisReports() {
        assertNotNull(sut.getFreightSpendAnalysisReports(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testWeightAnalysisReport() {
        assertNotNull(sut.getWeightAnalysisReports(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testGeographicalSummaryReports() {
        assertNotNull(sut.getGeographicSummaryReports(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testCarrierSummaryReports() {
        assertNotNull(sut.getCarrierSummaryReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testClassSummaryReports() {
        assertNotNull(sut.getClassSummaryReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testCustomerSummaryReports() {
        assertNotNull(sut.getCustomerSummaryReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testSeasonalitySummaryReports() {
        assertNotNull(sut.getSeasonalityReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testShipmentSummaryReports() {
        assertNotNull(sut.getShipmentOverviewReport(ORG_ID, new KPIReportFiltersBO()));
    }

    @Test
    public void testVendorSummaryReports() {
        assertNotNull(sut.getVendorSummaryReport(ORG_ID, new KPIReportFiltersBO()));
    }
}
