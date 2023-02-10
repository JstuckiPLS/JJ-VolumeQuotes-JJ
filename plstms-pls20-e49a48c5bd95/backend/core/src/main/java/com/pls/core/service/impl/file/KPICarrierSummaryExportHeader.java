package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.CarrierSummaryReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Carrier Summary Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPICarrierSummaryExportHeader {


    public static final ColumnDescriptor<CarrierSummaryReportBO> ORG_ID =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organizational ID";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> SCAC =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getScac();
        }

        @Override
        public String getTitle() {
            return "SCAC";
        }
    };


    public static final ColumnDescriptor<CarrierSummaryReportBO> SHIP_DATE =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getShipDate();
        }

        @Override
        public String getTitle() {
            return "Ship Date";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> LCOUNT =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> AVG_WEIGHT =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> LH_REV =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> FUEL_REV =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> ACC_REV =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> SUM_TOTAL =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> SAVINGS =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<CarrierSummaryReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<CarrierSummaryReportBO>() {

        @Override
        public Object getValue(CarrierSummaryReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting CarrierSummaryReportBO.
     * @return list of column descriptions for exporting CarrierSummaryReportBO
     */
    public static List<ColumnDescriptor<CarrierSummaryReportBO>> prepareColumns() {
        List<ColumnDescriptor<CarrierSummaryReportBO>> result =
                new ArrayList<ColumnDescriptor<CarrierSummaryReportBO>>();

        result.add(SCAC);
        result.add(SHIP_DATE);
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

    private KPICarrierSummaryExportHeader() {
    }
}
