package com.pls.core.service.impl.file;

import java.util.Arrays;
import java.util.List;

import com.pls.core.domain.bo.dashboard.GeographicSummaryReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Geographic summary export header information.
 * 
 * @author Dmitriy Nefedchenko
 */
public final class GeographicSummaryExportReportHeader {
    private static final ColumnDescriptor<GeographicSummaryReportBO> CUSTOMER_ID = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Organizational Id";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return item.getCustomerId();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> DESTINATION = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Destination";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return item.getDestination();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> ORIGIN = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Origin";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return item.getOrigin();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> LOAD_COUNT = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Order Count";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return item.getLoadCount();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> AVERAGE_WEIGHT = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "AVG Weight";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return item.getAverageWeight().toString() + KPIUnits.LBS;
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> LH_REVENUE = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "LH Revenue";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getLinehaulRevenue();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> FUEL_REVENUE = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Fuel Revenue";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getFuelRevenue();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> ACC_REVENUE = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "ACC Revenue";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getAccessorialRevenue();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> SUMMARY_TOTAL = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Summary Total";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getSummaryTotal();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> SHIPPER_BENCH = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getShipperBench();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> SAVINGS = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Savings";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getSavings();
        }
    };

    private static final ColumnDescriptor<GeographicSummaryReportBO> SUMMARY_TOTAL_SHIPMENT = new ColumnDescriptor<GeographicSummaryReportBO>() {

        @Override
        public String getTitle() {
            return "Summary Total Shipment";
        }

        @Override
        public Object getValue(GeographicSummaryReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getSummaryTotalShipment();
        }
    };
    private GeographicSummaryExportReportHeader() {

    }

    /**
     * Creates column header names for excel document.
     * @return - list with column headers
     */
    public static List<ColumnDescriptor<GeographicSummaryReportBO>> columnHeaders() {
        return Arrays.asList(CUSTOMER_ID, DESTINATION, ORIGIN, LOAD_COUNT, AVERAGE_WEIGHT,
                LH_REVENUE, FUEL_REVENUE, ACC_REVENUE, SUMMARY_TOTAL, SHIPPER_BENCH, SAVINGS, SUMMARY_TOTAL_SHIPMENT);
    }
}
