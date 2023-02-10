package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.CustomerSummaryReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Carrier Summary Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPICustomerSummaryExportHeader {

    public static final ColumnDescriptor<CustomerSummaryReportBO> ORG_ID =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organizational ID";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> CUSTOMER =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getCustomer();
        }

        @Override
        public String getTitle() {
            return "Customer";
        }
    };


    public static final ColumnDescriptor<CustomerSummaryReportBO> DEST_STATE =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getDestState();
        }

        @Override
        public String getTitle() {
            return "Dest State";
        }
    };


    public static final ColumnDescriptor<CustomerSummaryReportBO> PERCENTLCOUNT =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getPercentLCount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "Percent Order Count";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> LCOUNT =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> AVG_WEIGHT =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> LH_REV =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> FUEL_REV =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> ACC_REV =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> SUM_TOTAL =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> SAVINGS =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<CustomerSummaryReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<CustomerSummaryReportBO>() {

        @Override
        public Object getValue(CustomerSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting CustomerSummaryReportBO.
     * @return list of column descriptions for exporting CustomerSummaryReportBO
     */
    public static List<ColumnDescriptor<CustomerSummaryReportBO>> prepareColumns() {
        List<ColumnDescriptor<CustomerSummaryReportBO>> result =
                new ArrayList<ColumnDescriptor<CustomerSummaryReportBO>>();

        result.add(CUSTOMER);
        result.add(DEST_STATE);
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

    private KPICustomerSummaryExportHeader() {
    }
}
