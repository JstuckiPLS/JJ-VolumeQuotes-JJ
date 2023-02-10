package com.pls.core.service.impl.file;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.ShipmentOverviewReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Shipment Overview Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPIShipmentOverviewExportHeader {

    public static final ColumnDescriptor<ShipmentOverviewReportBO> ORG_ID =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organizational ID";
        }
    };


    public static final ColumnDescriptor<ShipmentOverviewReportBO> INBOUND_OUTBOUND_FLG =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getBound();
        }

        @Override
        public String getTitle() {
            return "Inbound/Outbound ID";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> SHIP_DATE =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getShipDate();
        }

        @Override
        public String getTitle() {
            return "Ship Date";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> LCOUNT =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Ordr Count";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> AVG_WEIGHT =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "AVG Weight";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> LH_REV =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getLhRev().toString();
        }

        @Override
        public String getTitle() {
            return "LH Rev";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> FUEL_REV =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getFuelRev().toString();
        }

        @Override
        public String getTitle() {
            return "Fuel Rev";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> ACC_REV =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getAccRev().toString();
        }

        @Override
        public String getTitle() {
            return "ACC Rev";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> SUM_TOTAL =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotal().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> SHIPPER_BENCH =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getShipperBench().toString();
        }

        @Override
        public String getTitle() {
            return "Shipper Bench";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> SAVINGS =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getSavings().toString();
        }

        @Override
        public String getTitle() {
            return "Savings";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> BM_SAVINGS_PERCENT =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return item.getBmSavingsPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "BM savings percent";
        }
    };

    public static final ColumnDescriptor<ShipmentOverviewReportBO> SUM_TOTAL_SHIPM =
            new ColumnDescriptor<ShipmentOverviewReportBO>() {

        @Override
        public Object getValue(ShipmentOverviewReportBO item) {
            return KPIUnits.CURRENCY + item.getSumTotalShipm().toString();
        }

        @Override
        public String getTitle() {
            return "Sum total shipment";
        }
    };
    /**
     * Return list of column descriptions for exporting ShipmentOverviewReportBO.
     * @return list of column descriptions for exporting ShipmentOverviewReportBO
     */
    public static List<ColumnDescriptor<ShipmentOverviewReportBO>> prepareColumns() {
        List<ColumnDescriptor<ShipmentOverviewReportBO>> result =
                new ArrayList<ColumnDescriptor<ShipmentOverviewReportBO>>();

        result.add(ORG_ID);
        result.add(INBOUND_OUTBOUND_FLG);
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

    private KPIShipmentOverviewExportHeader() {
    }
}
