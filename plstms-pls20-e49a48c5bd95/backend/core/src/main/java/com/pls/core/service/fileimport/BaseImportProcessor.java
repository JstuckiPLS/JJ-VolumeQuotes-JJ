package com.pls.core.service.fileimport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.DocumentFactory;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;
import com.pls.core.service.fileimport.parser.core.RecordParser;
import com.pls.core.service.validation.ValidationException;

/**
 * Represents base import operations from {@link InputStream}.
 * 
 * @param <I> type of import object.
 * @param <H> type of document description which should be imported.
 * 
 * @author Artem Arapov
 *
 */
public abstract class BaseImportProcessor<I, H> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int MAX_ROWS_COUNT = 1000;

    private final Map<H, Integer> headerData = new HashMap<H, Integer>();

    private RecordParser<I, H> recordParser;

    private Document document;

    private Page currentPage;

    private Record currentRecord;

    private int succeedRecords = 0;

    private int failedRecords = 0;

    private List<String> errorMessageList;

    /**
     * Constructor.
     * 
     * @param parser
     *            Implementation of {@link RecordParser} which should be used to parse records from stream. <br>
     *            Should be not <code>null</code>.
     */
    public BaseImportProcessor(RecordParser<I, H> parser) {
        recordParser = parser;
    }

    /**
     * Get using {@link RecordParser}.
     * 
     * @return using Record Parser.
     * */
    public RecordParser<I, H> getRecordParser() {
        return recordParser;
    }

    /**
     * Start import process from {@link InputStream}.
     * 
     * @param stream
     *            not <code>null</code> instance of {@link InputStream} which should be parsed.
     * @param extension
     *            Extension of file which should be imported. <br/>
     *            Not <code>null</code> {@link FileExtensionType}
     * @return {@link ImportFileResults} results of import.
     * @throws ImportException throws in import exception situations
     */
    public ImportFileResults processImport(InputStream stream, FileExtensionType extension) throws ImportException {
        checkInputStream(stream);

        succeedRecords = 0;
        failedRecords = 0;
        errorMessageList = new ArrayList<String>();

        try {
            document = DocumentFactory.create(stream, extension);

            Iterator<Page> page = document.iterator();
            boolean headerDataExists = false;
            while (page.hasNext()) {
                currentPage = page.next();
                log.debug("Processing '{}' page...", currentPage);
                if (extension.name().equalsIgnoreCase("CSV")) {
                    removeEmptyCsvField(currentPage);
                }

                checkPageMaxSize(currentPage);

                headerData.clear();
                headerData.putAll(recordParser.initialiseHeader(currentPage.getHeader()));
                if (headerData.isEmpty()) {
                    continue;
                }

                headerDataExists = true;
                validateHeader(headerData.keySet());

                importPageRecords();
                log.debug("'{}' page was processed sucessfully.", getPageName());
            }

            if (!headerDataExists) {
                throw new ImportFileInvalidDataException("File does not contains data.");
            }
        } catch (ImportException e) {
            log.warn("Unable to parse this document due to error: " + e.getMessage(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return getResults(extension);
    }

    private void removeEmptyCsvField(Page currentPage) throws ImportFileInvalidDataException {

        Iterator<Record> it = currentPage.iterator();
        Record record;
        boolean isFieldsEmpty = false;
        while (it.hasNext()) {
            record = it.next();
            for (int i = 0; i < record.getFieldsCount(); i++) {
                if (!record.getFieldByIdx(i).getString().isEmpty()) {
                    isFieldsEmpty = false;
                    break;
                }
                isFieldsEmpty = true;
            }

            if (isFieldsEmpty) {
                it.remove();
            }

        }
    }

    /**
     * Prepare record data before validation and save operations.
     * 
     * @param record which should be prepared.
     * */
    protected abstract void prepareRecord(I record);

    /**
     * Save record.
     * 
     * @param record
     *            record which should be saved.
     */
    protected abstract void saveRecord(I record);

    /**
     * Validate record.
     * 
     * @param record
     *            record which should be validated
     * @throws ValidationException
     *             in cases when validation was failed.
     */
    protected abstract void validateRecord(I record) throws ValidationException;

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
     * Validate if parsed more than maximum allowed amount of records.
     * 
     * @param page
     *            excel file page
     * @throws ImportRecordsNumberExceededException
     *             if file contains more records than allowed
     */
    private void checkPageMaxSize(Page page) throws ImportRecordsNumberExceededException {
        int pageSize = page.size();
        if (pageSize > getMaxRowsCount()) {
            throw new ImportRecordsNumberExceededException("Expected max rows count: " + getMaxRowsCount() + ", but was recieved: " + pageSize);
        }
    }

    /**
     * Get maximum allowed rows count.
     * 
     * @return maximum allowed rows count
     */
    protected int getMaxRowsCount() {
        return MAX_ROWS_COUNT;
    }

    private void importPageRecords() {
        log.debug("Importing records from '{}' page", getPageName());

        Iterator<Record> it = currentPage.iterator();
        while (it.hasNext()) {
            currentRecord = it.next();
            if (currentRecord.isEmpty()) {
                continue;
            }

            try {
                importRecord(currentRecord);
                it.remove();
                succeedRecords++;
            } catch (ImportFileInvalidDataException e) {
                failedRecords++;
                errorMessageList.add(e.getMessage());
            }
        }
    }

    private void importRecord(Record record) throws ImportFileInvalidDataException {
        I entity = recordParser.parseRecord(record);
        if (entity != null) {
            validateAndSave(entity);
        }
    }

    private void validateAndSave(I entity) throws ImportFileInvalidDataException {
        try {
            prepareRecord(entity);
            validateRecord(entity);
            saveRecord(entity);
        } catch (Exception e) {
            log.warn("Unable to save due to error", e);
            throw new ImportFileInvalidDataException("Unable to save due to error", e);
        }
    }

    private ImportFileResults getResults(FileExtensionType extension) throws ImportException {
        ImportFileResults results = new ImportFileResults();
        results.setFaiedRowsCount(failedRecords);
        results.setSuccesRowsCount(succeedRecords);
        results.setErrorMessageList(errorMessageList);
        if (failedRecords > 0) {
            Long failedDocId = saveInvalidDocument(extension, document);
            results.setFailedDocumentId(failedDocId);
        }
        return results;
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
     * Get name of current {@link String}.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    protected String getRecordName() {
        return currentRecord.getName();
    }

    /**
     * Save invalid document and return id of document entity to which data are saved.
     *
     * @param extension file extension
     * @param document writer of document
     * @return id of document entity
     * @throws ImportException if document can not be saved
     */
    protected abstract Long saveInvalidDocument(FileExtensionType extension, Document document) throws ImportException;
}
