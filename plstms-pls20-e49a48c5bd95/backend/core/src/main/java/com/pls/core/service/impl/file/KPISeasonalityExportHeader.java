package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.SeasonalityReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Seasonality Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPISeasonalityExportHeader {

    public static final ColumnDescriptor<SeasonalityReportBO> ORG_ID =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organization ID";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> SHIP_DATE_MONTH =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getShipDateMonth();
        }

        @Override
        public String getTitle() {
            return "Month";
        }
    };


    public static final ColumnDescriptor<SeasonalityReportBO> DEST_STATE =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getDestState();
        }

        @Override
        public String getTitle() {
            return "Destination State";
        }
    };


    public static final ColumnDescriptor<SeasonalityReportBO> PERCENTLCOUNT =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getPercentLCount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "Percent Order Count";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> LCOUNT =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> AVG_WEIGHT =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> LH_REV =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> FUEL_REV =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> ACC_REV =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> SUM_TOTAL =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> SAVINGS =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<SeasonalityReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<SeasonalityReportBO>() {

        @Override
        public Object getValue(SeasonalityReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting SeasonalityReportBO.
     * @return list of column descriptions for exporting SeasonalityReportBO
     */
    public static List<ColumnDescriptor<SeasonalityReportBO>> prepareColumns() {
        List<ColumnDescriptor<SeasonalityReportBO>> result =
                new ArrayList<ColumnDescriptor<SeasonalityReportBO>>();

        result.add(SHIP_DATE_MONTH);
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

    private KPISeasonalityExportHeader() {
    }
}
