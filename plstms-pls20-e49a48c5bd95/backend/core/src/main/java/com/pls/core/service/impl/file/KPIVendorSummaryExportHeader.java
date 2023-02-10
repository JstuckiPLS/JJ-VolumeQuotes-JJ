package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.VendorSummaryReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Vendor Summary Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPIVendorSummaryExportHeader {

    public static final ColumnDescriptor<VendorSummaryReportBO> ORG_ID =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organization ID";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> ORIG_NAME =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getOrigName();
        }

        @Override
        public String getTitle() {
            return "Origin Name";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> ORIG_STATE =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getOrigState();
        }

        @Override
        public String getTitle() {
            return "Origin State";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> ORIG_CITY =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getOrigCity();
        }

        @Override
        public String getTitle() {
            return "Origin Ciry";
        }
    };


    public static final ColumnDescriptor<VendorSummaryReportBO> PERCENTLCOUNT =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getPercentLCount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "Percent Order Count";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> LCOUNT =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getlCount().toString();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> AVG_WEIGHT =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> LH_REV =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> FUEL_REV =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> ACC_REV =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> SUM_TOTAL =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> SAVINGS =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<VendorSummaryReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<VendorSummaryReportBO>() {

        @Override
        public Object getValue(VendorSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting VendorSummaryReportBO.
     * @return list of column descriptions for exporting VendorSummaryReportBO
     */
    public static List<ColumnDescriptor<VendorSummaryReportBO>> prepareColumns() {
        List<ColumnDescriptor<VendorSummaryReportBO>> result =
                new ArrayList<ColumnDescriptor<VendorSummaryReportBO>>();

        result.add(ORIG_NAME);
        result.add(ORIG_STATE);
        result.add(ORIG_CITY);
        result.add(PERCENTLCOUNT);
        result.add(LCOUNT);
        result.add(AVG_WEIGHT);
        result.add(LH_REV);
        result.add(FUEL_REV);
        result.add(ACC_REV);
        result.add(SUM_TOTAL);
        result.add(SHIPPER_BENCH);
        result.add(SAVINGS);
        result.add(BM_SAVINGS_PERCENT);
        result.add(SUM_TOTAL_SHIPM);
        return result;
    }

    private KPIVendorSummaryExportHeader() {
    }
}
