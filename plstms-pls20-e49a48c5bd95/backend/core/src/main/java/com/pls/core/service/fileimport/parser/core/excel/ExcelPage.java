package com.pls.core.service.fileimport.parser.core.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * Implementation of {@link Page} for Excel document page.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelPage implements Page {

    private static final class RecordIterator implements Iterator<Record> {
        private final Iterator<Row> iterator;
        private Row currentRow;

        protected RecordIterator(Iterator<Row> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Record next() {
            currentRow = iterator.next();
            return new ExcelRecord(currentRow);
        }

        @Override
        public void remove() {
            if (currentRow != null) {
                Sheet page = currentRow.getSheet();
                page.removeRow(currentRow);
                currentRow = null;
            }
        }
    }


    private Sheet page;

    private Record header;

    /**
     * Constructor.
     * 
     * @param sheet not <code>null</code> {@link Sheet}.
     * @throws ImportFileParseException exception
     */
    public ExcelPage(Sheet sheet) throws ImportFileParseException {
        if (sheet == null) {
            throw new ImportFileParseException("Excel sheet can't be null");
        }

        this.page = sheet;

        Iterator<Row> iterator = page.iterator();
        if (iterator.hasNext()) {
            header = new ExcelRecord(iterator.next());
        } else {
            header = new ExcelRecord(page.createRow(0));
        }
    }

    @Override
    public Iterator<Record> iterator() {
        final Iterator<Row> iterator = copyRows(page).iterator();
        iterator.next();

        return new RecordIterator(iterator);
    }

    private List<Row> copyRows(Sheet sheet) {
        List<Row> results = new ArrayList<Row>();
        for (Row row : sheet) {
            results.add(row);
        }
        return results;
    }

    @Override
    public String getName() {
        return page.getSheetName();
    }

    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    @Override
    public Record getHeader() {
        return header;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int size() {
        int count = 0;
        for (Row row : page) {
            for (Cell cell : row) {
                if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                    count++;
                    break;
                }
            }
        }
        return count > 0 ? count - 1 : count;
    }

    @Override
    public Object getSource() {
        return page;
    }

}
