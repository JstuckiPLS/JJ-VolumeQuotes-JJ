package com.pls.core.service.fileimport.parser.core.csv;

import java.util.ArrayList;
import java.util.List;

import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * {@link Record} for CSV file.
 * 
 * @author Artem Arapov
 *
 */
public class CsvRecord implements Record {

    private final List<Field> fields = new ArrayList<Field>();

    private final String name;

    /**
     * Constructor.
     * 
     * @param content
     *            Fields for this row.
     * @param rowNumber
     *            Number of row in CSV file.
     */
    CsvRecord(String[] content, int rowNumber) {
        name = "ROW#" + rowNumber;

        if (content != null) {
            for (int colIndx = 0; colIndx < content.length; colIndx++) {
                fields.add(new CsvField(content[colIndx], name, colIndx));
            }
        }
    }

    @Override
    public Field getFieldByIdx(int fieldNumber) {
        Field result;
        if (fieldNumber >= 0 && fieldNumber < fields.size()) {
            result = fields.get(fieldNumber);
        } else {
            result = new CsvField(null, name, fieldNumber);
        }
        return result;
    }

    @Override
    public int getFieldsCount() {
        return fields.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return fields.isEmpty();
    }
}
