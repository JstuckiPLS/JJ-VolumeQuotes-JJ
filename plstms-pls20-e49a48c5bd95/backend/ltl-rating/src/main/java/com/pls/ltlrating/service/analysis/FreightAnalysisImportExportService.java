package com.pls.ltlrating.service.analysis;

import java.util.Set;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;


/**
 * Freight analysis service for working with excel.
 *
 * @author Svetlana Kulish
 *
 */
public interface FreightAnalysisImportExportService {

    /**
     * Import input details from file.
     *
     * @param docID
     *            id of file.
     * @return imported entities
     * @throws ImportException
     *             if import failed
     * @throws EntityNotFoundException
     *             if import failed
     * @throws DocumentReadException
     *             if import failed
     */
    Set<FAInputDetailsEntity> importInputDetailsFromFile(Long docID) throws ImportException, EntityNotFoundException, DocumentReadException;

    /**
     * Create resulting excel file for freight analysis by specified ID.
     *
     * @param analysisId
     *            Freight Analysis ID
     */
    void createOutputExcelFile(Long analysisId);
}
