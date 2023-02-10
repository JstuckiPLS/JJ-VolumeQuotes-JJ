package com.pls.core.service.impl.file;

import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.DailyLoadActivityBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Information about header row for rule exporting KPI Daily Load Activity Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPIDailyLoadActivityReportExportHeader {

    public static final ColumnDescriptor<DailyLoadActivityBO> PICKUP_DATE =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getPickup();
        }

        @Override
        public String getTitle() {
            return "Pickup Date";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> BOUND =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getBound();
        }

        @Override
        public String getTitle() {
            return "Bound";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> DEST_STATE =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getDestState();
        }

        @Override
        public String getTitle() {
            return "Destination State";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> ORIG_STATE =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getOrigState();
        }

        @Override
        public String getTitle() {
            return "Origin State";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> CLASS_CODE =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getClassCode();
        }

        @Override
        public String getTitle() {
            return "Class Code";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> PICK_UP =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getPickup();
        }

        @Override
        public String getTitle() {
            return "Pickup";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> SCAC =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getScac();
        }

        @Override
        public String getTitle() {
            return "SCAC";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> CUSTOMER =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getCustomer();
        }

        @Override
        public String getTitle() {
            return "Customer";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> WEEKDAY =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getWeekday();
        }

        @Override
        public String getTitle() {
            return "Weekday";
        }
    };

    public static final ColumnDescriptor<DailyLoadActivityBO> TOTAL =
            new ColumnDescriptor<DailyLoadActivityBO>() {

        @Override
        public Object getValue(DailyLoadActivityBO item) {
            return item.getTotal().toString() + KPIUnits.PERCENT;
        }

        @Override
        public String getTitle() {
            return "Total";
        }
    };

    /**
     * Return list of column descriptions for exporting DailyLoadActivityBO.
     * @return list of column descriptions for exporting DailyLoadActivityBO
     */
    public static List<ColumnDescriptor<DailyLoadActivityBO>> prepareColumns() {
        List<ColumnDescriptor<DailyLoadActivityBO>> result =
                new ArrayList<ColumnDescriptor<DailyLoadActivityBO>>();
        result.add(BOUND);
        result.add(DEST_STATE);
        result.add(ORIG_STATE);
        result.add(CLASS_CODE);
        result.add(CUSTOMER);
        result.add(PICKUP_DATE);
        result.add(SCAC);
        result.add(WEEKDAY);
        result.add(TOTAL);
        return result;
    }

    private KPIDailyLoadActivityReportExportHeader() {
    }
}
