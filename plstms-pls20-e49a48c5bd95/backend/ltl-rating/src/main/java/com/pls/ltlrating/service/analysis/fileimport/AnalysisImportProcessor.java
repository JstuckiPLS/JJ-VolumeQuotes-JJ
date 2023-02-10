package com.pls.ltlrating.service.analysis.fileimport;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.BaseImportProcessor;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;


/**
 * Performs import input details from {@link InputStream} into database table FA_INPUT_DETAILS.
 *
 * @author Svetlana Kulish
 */

public class AnalysisImportProcessor extends BaseImportProcessor<FAInputDetailsEntity, AnalysisFieldsDescription> {
    private static final int MAX_ROWS_COUNT = 200000;

    private Set<FAInputDetailsEntity> rowsSet = new HashSet<>();

    /**
     * Constructor.
     */
    public AnalysisImportProcessor() {
        super(new AnalysisRecordParser());
    }

    @Override
    protected void prepareRecord(FAInputDetailsEntity record) {
        record.setSeq((long) getRowsSet().size());
    }

    @Override
    protected void saveRecord(FAInputDetailsEntity record) {
        rowsSet.add(record);
    }

    @Override
    protected void validateRecord(FAInputDetailsEntity record) throws ValidationException {
    //It should be empty
    }

    @Override
    protected int getMaxRowsCount() {
        return MAX_ROWS_COUNT;
    }

    @Override
    protected Long saveInvalidDocument(FileExtensionType extension, Document document) {
        // TODO Auto-generated method stub
        // throw new ApplicationException("Error in parser");
        return null;
    }

    /**
     * Validate headers.
     * 
     * @param headerData
     *            header to validate
     * @throws ImportFileParseException
     *             if header is incorrect
     */
    protected void validateHeader(Collection<AnalysisFieldsDescription> headerData) throws ImportFileParseException {
        for (AnalysisFieldsDescription descriptor : headerData) {
            if (descriptor.isRequired() && !headerData.contains(descriptor)) {
                throw new ImportFileParseException("Column '" + descriptor.getHeader() + "' was not found on '" + getPageName() + "' sheet.");
            }
        }
    }

    public Set<FAInputDetailsEntity> getRowsSet() {
        return rowsSet;
    }
}
