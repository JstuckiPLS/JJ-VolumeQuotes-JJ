package com.pls.core.service.impl.file;

import java.util.Arrays;
import java.util.List;

import com.pls.core.domain.bo.dashboard.FreightSpendAnalysisReportBO;
import com.pls.core.service.file.ColumnDescriptor;
import com.pls.core.service.impl.file.enums.KPIUnits;

/**
 * Freight spend analysis export header information.
 * 
 * @author Dmitriy Nefedchenko
 */
public final class FreightSpendAnalysisExportReportHeader {
    public static final ColumnDescriptor<FreightSpendAnalysisReportBO> COMODITY_CLASS = new ColumnDescriptor<FreightSpendAnalysisReportBO>() {

        @Override
        public Object getValue(FreightSpendAnalysisReportBO item) {
            return item.getClassCode();
        }

        @Override
        public String getTitle() {
            return "Class";
        }
    };

    public static final ColumnDescriptor<FreightSpendAnalysisReportBO> LOAD_COUNT = new ColumnDescriptor<FreightSpendAnalysisReportBO>() {

        @Override
        public Object getValue(FreightSpendAnalysisReportBO item) {
            return item.getLoadCount();
        }

        @Override
        public String getTitle() {
            return "Order Count";
        }
    };

    public static final ColumnDescriptor<FreightSpendAnalysisReportBO> SUMMARY_COST = new ColumnDescriptor<FreightSpendAnalysisReportBO>() {

        @Override
        public Object getValue(FreightSpendAnalysisReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getSummaryCost();
        }

        @Override
        public String getTitle() {
            return "Summary cost";
        }
    };

    public static final ColumnDescriptor<FreightSpendAnalysisReportBO> AVARAGE_COST = new ColumnDescriptor<FreightSpendAnalysisReportBO>() {

        @Override
        public Object getValue(FreightSpendAnalysisReportBO item) {
            return KPIUnits.CURRENCY.toString() + item.getAvarageCost();
        }

        @Override
        public String getTitle() {
            return "Avarage cost";
        }
    };

    /**
     * Creates list of header descriptions.
     * @return - list of header column descriptions for excel export
     */
    public static List<ColumnDescriptor<FreightSpendAnalysisReportBO>> columnHeaders() {
        return Arrays.asList(COMODITY_CLASS, LOAD_COUNT, SUMMARY_COST, AVARAGE_COST);
    }

    private FreightSpendAnalysisExportReportHeader() {

    }
}
