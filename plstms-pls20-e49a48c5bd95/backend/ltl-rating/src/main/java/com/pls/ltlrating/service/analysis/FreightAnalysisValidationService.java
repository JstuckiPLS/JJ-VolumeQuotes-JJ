/**
 * 
 */
package com.pls.ltlrating.service.analysis;

/**
 * Freight Analysis service for validation of user's uploaded file.
 * 
 * @author Dmitriy Davydenko
 *
 */
public interface FreightAnalysisValidationService {

    /**
     *     This method validates each cell of uploaded file. The result of validation is a number,
     * based on validation results. In case if the errors will appear in file, new document
     * is being generated with additional column. This column will contain all errors found in appropriate row.
     * 
     * @param docId id of already stored document.
     * @return '-1' - if uploaded document is of wrong format.
     *         '-2' - if no validation errors were found in specified file.
     *         docId - if validation errors were found. In this case new documents is being generated with additional column
     * @throws Exception
     *              if errors occured during file validation or creation of output file. Doesn't include files format exceptions.
     */
    long validateFileForFreightAnalysis(Long docId) throws Exception;
}
