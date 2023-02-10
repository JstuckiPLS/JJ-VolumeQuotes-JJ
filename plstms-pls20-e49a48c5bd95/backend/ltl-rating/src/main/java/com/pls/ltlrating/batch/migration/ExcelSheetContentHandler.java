package com.pls.ltlrating.batch.migration;

import static org.hibernate.jpa.internal.QueryImpl.LOG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.batch.item.excel.support.rowset.RowSetMetaData;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;

/**
 * Class to process excel file content during parsing.
 *
 * @author Aleksandr Leshchenko
 */
public class ExcelSheetContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
    private static String[] columnNames;
    private static Map<String, String> templateRowMap;

    public static final int AMOUNT = 48;

    static {
        templateRowMap = new HashMap<String, String>();
        List<String> columns = new ArrayList<String>();
        for (String columnRef : new AlphaSequence(AMOUNT)) {
            columns.add(columnRef);
            templateRowMap.put(columnRef, null);
        }
        columnNames = columns.toArray(new String[columns.size()]);
    }
    private final int startInd;
    private final int endInd;
    private final List<LtlPricingItem> pricingItems;
    private final RowMapper<LtlPricingItem> rowMapper;

    private boolean process = false;
    private Map<String, String> rowMap;

    /**
     * Constructor.
     *
     * @param pricingItems
     *            pricing Items
     * @param rowMapper
     *            row Mapper
     * @param startInd
     *            start Index
     * @param endInd
     *            end Index
     */
    public ExcelSheetContentHandler(final List<LtlPricingItem> pricingItems, final RowMapper<LtlPricingItem> rowMapper, final int startInd,
                                    final int endInd) {
        this.pricingItems = pricingItems;
        this.rowMapper = rowMapper;
        this.startInd = startInd;
        this.endInd = endInd;
    }

    @Override
    public void startRow(final int rowNum) {
        checkProcFlag(rowNum, true);
        if (process) {
            rowMap = new HashMap<>(templateRowMap);
        }
    }

    @Override
    public void endRow(final int rowNum) {
        checkProcFlag(rowNum, false);
        if (process) {
            List<String> rowData = Arrays.stream(columnNames).map(columnRef -> rowMap.get(columnRef)).collect(Collectors.toList());
            try {
                pricingItems.add(rowMapper.mapRow(new PlainRowSet(rowNum, rowData.toArray(new String[rowData.size()]))));
            } catch (Exception e) {
                LOG.warn("PRICE IMPORT: Error on converting pricing item", e);
            }
        }
    }

    @Override
    public void cell(final String cellReference, final String formattedValue, final XSSFComment comment) {
        if (process) {
            String cellName = cellReference.replaceAll("\\d", "");
            rowMap.put(cellName, formattedValue);
        }
    }

    @Override
    public void headerFooter(final String text, final boolean isHeader, final String tagName) {
        //Do nothing.
    }

    private void checkProcFlag(final int rowNum, final boolean isStartRow) {
        if (!process && isStartRow && rowNum == startInd) {
            process = true;
        } else if (process && !isStartRow && rowNum == endInd) {
            process = false;
        }
    }


    private static class AlphaSequence implements Iterable<String>, Iterator<String> {

        private int now;
        private static char[] vs;

        static {
            vs = new char['Z' - 'A' + 1];
            for (char i = 'A'; i <= 'Z'; i++) {
                vs[i - 'A'] = i;
            }
        }

        private int amount;

        AlphaSequence(final int amount) {
            this.amount = amount;
        }

        private StringBuilder alpha(final int index) {
            assert index > 0;
            char r = vs[(index - 1) % vs.length];
            int n = (index - 1) / vs.length;
            return n == 0 ? new StringBuilder().append(r) : alpha(n).append(r);
        }

        public boolean hasNext() {
            return now < amount;
        }

        public String next() {
            return alpha(++now).toString();
        }

        public Iterator<String> iterator() {
            return this;
        }
    }

    private static class PlainRowSet implements RowSet {

        private final int rowIndex;
        private final String[] rowData;

        PlainRowSet(final int rowIndex, final String[] rowData) {
            this.rowIndex = rowIndex;
            this.rowData = Arrays.copyOf(rowData, rowData.length);
        }

        @Override
        public RowSetMetaData getMetaData() {
            return null;
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public int getCurrentRowIndex() {
            return rowIndex;
        }

        @Override
        public String[] getCurrentRow() {
            return rowData;
        }

        @Override
        public String getColumnValue(final int idx) {
            return rowData[idx];
        }

        @Override
        public Properties getProperties() {
            return null;
        }
    }
}
