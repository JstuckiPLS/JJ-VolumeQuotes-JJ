package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.ClassSummaryReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Class Summary Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPIClassSummaryExportHeader {

    public static final ColumnDescriptor<ClassSummaryReportBO> ORG_ID =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organizational ID";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> CLASS =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getClassCode();
        }

        @Override
        public String getTitle() {
            return "Class";
        }
    };


    public static final ColumnDescriptor<ClassSummaryReportBO> SHIP_DATE =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getShipDate();
        }

        @Override
        public String getTitle() {
            return "Ship Date";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> LCOUNT =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> PLCOUNT =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getPercentLCount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "Percent Order Count";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> AVG_WEIGHT =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> LH_REV =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> FUEL_REV =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> ACC_REV =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> SUM_TOTAL =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> SAVINGS =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<ClassSummaryReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<ClassSummaryReportBO>() {

        @Override
        public Object getValue(ClassSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting ClassSummaryReportBO.
     * @return list of column descriptions for exporting ClassSummaryReportBO
     */
    public static List<ColumnDescriptor<ClassSummaryReportBO>> prepareColumns() {
        List<ColumnDescriptor<ClassSummaryReportBO>> result =
                new ArrayList<ColumnDescriptor<ClassSummaryReportBO>>();

        result.add(CLASS);
        result.add(SHIP_DATE);
        result.add(LCOUNT);
        result.add(PLCOUNT);
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

    private KPIClassSummaryExportHeader() {
    }
}
