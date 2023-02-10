package com.pls.core.service.fileimport.parser.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;

/**
 * Represents base parsing operations of {@link com.pls.core.service.fileimport.parser.core.Document}. <br/>
 *
 * @param <I>
 *            Type of parsed item.
 * @param <H>
 *            Type of header identifier. This type must be immutable.
 * 
 * @author Artem Arapov
 * @author Maxim Medvedev
 *
 */
public abstract class BaseDocumentParser<I, H> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private List<I> results = new ArrayList<I>();
    private List<Record> invalidRecords = new ArrayList<Record>();
    private final Map<H, Integer> headerData = new HashMap<H, Integer>();
    private Page currentPage;
    private Record currentRecord;

    /**
     * Parse specified file and extract data.
     * 
     * @param stream
     *            MS Excel or CSV file.
     * @param extension
     *            Extension of file which should be parsed. <br/>
     *            Not <code>null</code> {@link com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType}
     * @return Not <code>null</code> {@link List}.
     * @throws ImportException
     *             Unable to parse input data.
     * @throws ImportException
     *             Incorrect data in file
     */
    public List<I> parse(InputStream stream, DocumentFactory.FileExtensionType extension) throws ImportException {
        checkInputStream(stream);
        results.clear();

        try {
            Document document = DocumentFactory.create(stream, extension);

            Iterator<Page> page = document.iterator();
            while (page.hasNext()) {
                currentPage = page.next();
                log.debug("Processing '{}' page...", currentPage);

                parseHeaderRow();
                validateHeader(headerData.keySet());

                parseDataRows();
                log.debug("'{}' page was processed sucessfully.", getPageName());
            }
        } catch (ImportException e) {
            log.warn("Unable to parse this document due to error: " + e.getMessage(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return results;
    }

    public List<Record> getInvalidRecords() {
        return Collections.unmodifiableList(invalidRecords);
    }

    /**
     * Get {@link com.pls.core.service.fileimport.parser.core.Field} for specified column and current row.
     * 
     * @param headerId
     *            Header ID.
     * @return Not <code>null</code> {@link com.pls.core.service.fileimport.parser.core.Field}.
     */
    protected final Field getColumnData(H headerId) {
        Integer fieldNumber = headerData.get(headerId);
        return currentRecord.getFieldByIdx(fieldNumber != null ? fieldNumber : -1);
    }

    /**
     * Identify columns using string from header row.
     * 
     * @param headerString
     *            Not <code>null</code> {@link String}.
     * @return Not <code>null</code> H if this is expected column. Otherwise method returns <code>null</code>.
     */
    protected abstract H parseHeaderColumn(String headerString);

    /**
     * Parse result item from data row.
     * 
     * @return Not <code>null</code> {@link RecordItem} if record parsed successfully. <code>null</code> if
     *         result item cannot be read but this is not error and parsing process should be continue.
     * 
     * @throws ImportFileInvalidDataException
     *             Error was happened. Should be used to break parsing process.
     */
    protected abstract I parseRecord() throws ImportFileInvalidDataException;

    /**
     * Validate header data. This method is called after header row has parsed and it is used to check that
     * the document has the required columns.
     * 
     * @param headerData
     *            Not <code>null</code> {@link Collection} with header items that were parsed.
     * 
     * @throws ImportFileParseException
     *             Should be thrown if required columns are absent.
     */
    protected abstract void validateHeader(Collection<H> headerData) throws ImportFileParseException;

    private void checkInputStream(InputStream stream) throws ImportFileParseException {
        if (stream == null) {
            throw new ImportFileParseException("Input data is required");
        }
    }

    /**
     * Get name of current {@link Page}.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    protected String getPageName() {
        return currentPage.getName();
    }

    /**
     * Get name of current {@link RecordItem}.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    protected String getRecordName() {
        return currentRecord.getName();
    }

    private void parseDataRows() {
        log.debug("Reading data for '{}' page", getPageName());
        Iterator<Record> rowIt = currentPage.iterator();
        while (rowIt.hasNext()) {
            currentRecord = rowIt.next();
            addRecord(currentRecord);
        }
    }

    private void addRecord(Record record) {
        if ((record != null) && (!record.isEmpty())) {
            try {
                I item = parseRecord();
                if (item != null) {
                    results.add(item);
                }
            } catch (ImportFileInvalidDataException e) {
                log.warn("Record " + record.getName() + " was skiped due to error", e);
                invalidRecords.add(record);
            }
        }
    }

    private void parseHeaderRow() throws ImportFileInvalidDataException {
        log.debug("Reading header for '{}' page", getPageName());

        headerData.clear();
        Record header = currentPage.getHeader();
        for (int colIndex = 0; colIndex < header.getFieldsCount(); colIndex++) {
            Field headerColumn = header.getFieldByIdx(colIndex);
            if (headerColumn != null && (!headerColumn.isEmpty())) {
                H headerId = parseHeaderColumn(headerColumn.getString());
                if (headerId != null) {
                    headerData.put(headerId, colIndex);
                }
            }
        }
        if (headerData.isEmpty()) {
            throw new ImportFileInvalidDataException("File does not contains data.");
        }
    }
}
