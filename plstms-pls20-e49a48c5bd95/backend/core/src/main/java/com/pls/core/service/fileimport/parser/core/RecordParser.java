package com.pls.core.service.fileimport.parser.core;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents base parsing operations of {@link com.pls.core.service.fileimport.parser.core.Record}. <br/>
 * 
 * @param <I> type of parsed item.
 * @param <H> type of header identifier.
 * 
 * @author Artem Arapov
 *
 */
public abstract class RecordParser<I, H> {
    private final Map<H, Integer> headerData = new HashMap<H, Integer>();

    /**
     * Parse record.
     * 
     * @param record {@link com.pls.core.service.fileimport.parser.core.Record} to parse.
     * @return parsed item.
     * @throws ImportFileInvalidDataException in cases of invalid or missed mandatory fields.
     */
    public abstract I parseRecord(Record record) throws ImportFileInvalidDataException;

    /**
     * Identify columns using string from header row.
     * 
     * @param headerString
     *            Not <code>null</code> {@link String}.
     * @return Not <code>null</code> H if this is expected column. Otherwise method returns <code>null</code>.
     */
    protected abstract H parseHeaderColumn(String headerString);

    /**
     * Initialize header description of parsed record. <br/>
     * This method should be invoked before {@link RecordParser#parseRecord(Record)}.
     * 
     * @param header Record which represent header description.
     * @return header data
     * @throws ImportFileInvalidDataException throws in case if header description was empty.
     * */
    public Map<H, Integer> initialiseHeader(Record header) throws ImportFileInvalidDataException {
        headerData.clear();
        for (int colIndex = 0; colIndex < header.getFieldsCount(); colIndex++) {
            Field headerColumn = header.getFieldByIdx(colIndex);
            if (headerColumn != null && (!headerColumn.isEmpty())) {
                H headerId = parseHeaderColumn(headerColumn.getString());
                if (headerId != null) {
                    headerData.put(headerId, colIndex);
                }
            }
        }

        return headerData;
    }

    /**
     * Get {@link Field} for specified column and current row.
     * 
     * @param record
     *            from which need to get {@link Field}.
     * @param headerId
     *            Header ID.
     * @return Not <code>null</code> {@link Field}.
     */
    protected final Field getColumnData(Record record, H headerId) {
        if (headerData == null) {
            throw new IllegalArgumentException("Not initializad field 'headerData'");
        }

        Integer fieldNumber = headerData.get(headerId);
        return record.getFieldByIdx(fieldNumber != null ? fieldNumber : -1);
    }
}
