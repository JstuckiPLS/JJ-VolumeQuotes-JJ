package com.pls.core.service.impl.file;

import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.dashboard.DestinationReportBO;
import com.pls.core.service.file.ColumnDescriptor;

/**
 * Information about header row for rule exporting KPI Destination Report.
 * 
 * @author Alexander Nalapko
 *
 */
public final class KPIDestinationReportExportHeader {

    public static final ColumnDescriptor<DestinationReportBO> ORG_ID =
            new ColumnDescriptor<DestinationReportBO>() {

        @Override
        public Object getValue(DestinationReportBO item) {
            return item.getOrgID();
        }

        @Override
        public String getTitle() {
            return "Organizational ID";
        }
    };

    public static final ColumnDescriptor<DestinationReportBO> DEST_STATE =
            new ColumnDescriptor<DestinationReportBO>() {

        @Override
        public Object getValue(DestinationReportBO item) {
            return item.getDestState();
        }

        @Override
        public String getTitle() {
            return "Destination State";
        }
    };

    public static final ColumnDescriptor<DestinationReportBO> ORIG_STATE =
            new ColumnDescriptor<DestinationReportBO>() {

        @Override
        public Object getValue(DestinationReportBO item) {
            return item.getOrigState();
        }

        @Override
        public String getTitle() {
            return "Origin State";
        }
    };

    public static final ColumnDescriptor<DestinationReportBO> L_COUNT =
            new ColumnDescriptor<DestinationReportBO>() {

        @Override
        public Object getValue(DestinationReportBO item) {
            return item.getlCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<DestinationReportBO> ORG_NAME =
            new ColumnDescriptor<DestinationReportBO>() {

        @Override
        public Object getValue(DestinationReportBO item) {
            return item.getOrgName();
        }

        @Override
        public String getTitle() {
            return "Customer Name";
        }
    };



    /**
     * Return list of column descriptions for exporting DestinationReportBO.
     * @return list of column descriptions for exporting DestinationReportBO
     */
    public static List<ColumnDescriptor<DestinationReportBO>> prepareColumns() {
        List<ColumnDescriptor<DestinationReportBO>> result =
                new ArrayList<ColumnDescriptor<DestinationReportBO>>();

        result.add(ORG_ID);
        result.add(DEST_STATE);
        result.add(ORIG_STATE);
        result.add(L_COUNT);
        result.add(ORG_NAME);
        return result;
    }

    private KPIDestinationReportExportHeader() {
    }
}
