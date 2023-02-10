package com.pls.core.service.fileimport.parser.core.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * Implementation of {@link Record}.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelRecord implements Record {

    private Row row;

    private static final int LAST_CELL_NUMBER = 255;

    /**
     * Constructor.
     * 
     * @param row {@link Row}.
     */
    public ExcelRecord(Row row) {
        this.row = row;
    }

    @Override
    public String getName() {
        return row.getSheet().getSheetName() + ":" + (row.getRowNum() + 1);
    }

    @Override
    public int getFieldsCount() {
        return row.getLastCellNum();
    }

    @Override
    public Field getFieldByIdx(int idx) {
        int index = idx >= 0 ? idx : LAST_CELL_NUMBER;

        return new ExcelField(row.getCell(index, MissingCellPolicy.CREATE_NULL_AS_BLANK));
    }

    @Override
    public boolean isEmpty() {
        boolean result = true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            result &= (row.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL) == null);
        }

        return result;
    }
}
