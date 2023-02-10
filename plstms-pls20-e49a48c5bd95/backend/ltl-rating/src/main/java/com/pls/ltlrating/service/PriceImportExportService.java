package com.pls.ltlrating.service;

import java.io.IOException;
import java.io.InputStream;

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.file.ExportException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;

/**
 * Service that perform import of prices.
 *
 * @author Alex Kyrychenko
 */
public interface PriceImportExportService {

    /**
     * Import prices from excel file and save it to database. Performed asynchronously by returning import job UUID.
     * @param inputStream {@link java.io.InputStream} excel file data.
     * @param extension extension of import file
     * @return Import job UUID.
     * @throws  ImportException
     *             when imported file contains invalid data.
     * @throws IOException when some of the file operation has been failed.
     */
    String importPricesAsync(InputStream inputStream, String extension) throws ImportException, IOException;

    /**
     * Method that allows to check whether import prices job with specified UUID has been finished or not.
     * @param jobUUID - UUID of the import prices job
     *
     * @return true if export prices job with specified UUID has been finished.
     * @throws ExportException if export prices job with specified UUID doesn't exist.
     */
    boolean isImportPricesFinished(String jobUUID) throws ExportException;

    /**
     * Method that returns import prices result {@link ImportFileResults} for import prices job with specified UUID.
     * @param jobUUID - UUID of the import prices job
     *
     * @return {@link ImportFileResults}.
     * @throws ExportException if export prices job with specified UUID doesn't exist.
     * @throws IOException when some of the file operation has been failed.
     */
    ImportFileResults getImportPricesResult(String jobUUID) throws ExportException, IOException;

    /**
     * Import prices from excel file and save it to database.
     * @param inputStream {@link java.io.InputStream} excel file data.
     * @param extension extension of import file
     * @return {@link com.pls.core.domain.bo.ImportFileResults}.
     * @throws  ImportException
     *             when imported file contains invalid data.
     * @throws IOException when some of the file operation has been failed.
     */
    ImportFileResults importPrices(InputStream inputStream, String extension) throws ImportException, IOException;

    /**
     * Returns document with data about prices records which were failed to import during importPrices()
     * method processing.
     *
     * @param id
     *            id of document.
     * @return {@link LoadDocumentEntity}.
     * @throws EntityNotFoundException when document by given id was not found.
     * @throws DocumentReadException if imported document cannot be read
     */
    LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException;

    /**
     * Remove document with specified id.
     *
     * @param id id of document.
     * @throws EntityNotFoundException when document by given id was not found.
     */
    void removeImportFailedDocument(Long id) throws EntityNotFoundException;

    /**
     * Method that starts export prices to excel file async job.
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job wasn't be able to start.
     */
    String exportPrices() throws ExportException;

    /**
     * Method that allows to check whether export prices job with specified UUID has been finished or not.
     * @param jobUUID - UUID of the export prices job
     *
     * @return true if export prices job with specified UUID has been finished.
     * @throws ExportException if export prices job with specified UUID doesn't exist.
     */
    boolean isExportPricesFinished(String jobUUID) throws ExportException;

    /**
     * Method that returns export prices excel file for export prices job with specified UUID.
     * @param jobUUID - UUID of the eposrt prices job
     *
     * @return {@link FileInputStreamResponseEntity} with export prices file content for the specified export job.
     * @throws ExportException if export prices job with specified UUID doesn't exist or hasn't been finished yet.
     */
    FileInputStreamResponseEntity getExportPricesFile(String jobUUID) throws ExportException;
}
