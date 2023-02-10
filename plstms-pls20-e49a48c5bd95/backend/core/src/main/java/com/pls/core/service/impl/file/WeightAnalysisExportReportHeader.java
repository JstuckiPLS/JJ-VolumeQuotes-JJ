package com.pls.core.service.impl.file;

import java.util.Arrays;
import java.util.List;

import com.pls.core.domain.bo.dashboard.WeightAnalysisReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Weight analysis export header information.
 * 
 * @author Dmitriy Nefedchenko
 */
public final class WeightAnalysisExportReportHeader {
    private static final ColumnDescriptor<WeightAnalysisReportBO> LOAD_ID = new ColumnDescriptor<WeightAnalysisReportBO>() {

        @Override
        public String getTitle() {
            return "Order Id";
        }

        @Override
        public Object getValue(WeightAnalysisReportBO item) {
            return item.getLoadId();
        }
    };

    private static final ColumnDescriptor<WeightAnalysisReportBO> WEIGHT = new ColumnDescriptor<WeightAnalysisReportBO>() {

        @Override
        public Object getValue(WeightAnalysisReportBO item) {
            return item.getWeight().toString() + KPIUnits.LBS;
        }

        @Override
        public String getTitle() {
            return "Weight";
        }
    };

    /**
     * Creates list of header descriptions.
     * @return - list of header column descriptions for excel export
     */
    public static List<ColumnDescriptor<WeightAnalysisReportBO>> columnHeaders() {
        return Arrays.asList(LOAD_ID, WEIGHT);
    }

    private WeightAnalysisExportReportHeader() {

    }
}
