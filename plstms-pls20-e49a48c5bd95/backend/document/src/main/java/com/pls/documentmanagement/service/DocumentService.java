package com.pls.documentmanagement.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.shared.DocumentSplitCO;

/**
 * Service processes documents basic operation like get, save etc.
 *
 * @author Pavani Challa
 * @author Denis Zhupinsky (Team International)
 */
public interface DocumentService {
    /**
     * Load the document from the file system by load document id.
     *
     * @param id
     *            Load document to be loaded
     * @return document read from the file system
     * @throws DocumentReadException
     *             if file could not be read
     * @throws EntityNotFoundException if document wasn't found
     */
    LoadDocumentEntity loadDocument(Long id) throws DocumentReadException, EntityNotFoundException;

    /**
     * Load the document from the file system by load document id without its content.
     *
     * @param id
     *            Load document to be loaded
     * @return document read from the file system
     * @throws EntityNotFoundException if document wasn't found
     */
    LoadDocumentEntity loadDocumentWithoutContent(Long id) throws EntityNotFoundException;

    /**
     * Reads the document on the file system that stored as {@link LoadDocumentEntity} and returns the actual bytes of the document.
     *
     * @param document {@link LoadDocumentEntity} that describes current document
     *            location of the document on file system
     * @return the actual bytes of the document.
     * @throws DocumentReadException for error when reading the document
     */
    byte[] readDocument(LoadDocumentEntity document) throws DocumentReadException;

    /**
     * Get document as {@link File} reference.
     *
     * @param document
     *            document to get a file
     * @return document as {@link File}
     * @throws IOException
     *             if {@link File} cannot be created
     */
    File getDocumentFile(LoadDocumentEntity document) throws IOException;

    /**
     * Get document input stream without reading from database.
     *
     * @param document
     *            document to get stream
     * @return document input stream
     * @throws IOException
     *             if input stream cannot be returned
     */
    InputStream getDocumentInputStream(LoadDocumentEntity document) throws IOException;

    /**
     * Reads the document on the file system and returns the actual bytes of the document.
     *
     * @param location
     *            location of the document on file system
     * @param fileName
     *            name of the document on file system
     * @return the actual bytes of the document.
     * @throws DocumentReadException
     *             for error when reading the document
     */
    byte[] readDocument(String location, String fileName) throws DocumentReadException;

    /**
     * Saves temp document on FS at folder that are used for files of specific document type.
     *
     * @param content file content
     * @param docType document type
     * @param mimeType mime type that corresponds to content that need to be saved, considered during file name building
     * @return pair with path and name of saved document
     * @throws DocumentSaveException thrown if file saving fails
     */
    LoadDocumentEntity saveTempDocument(byte[] content, String docType, MimeTypes mimeType) throws DocumentSaveException;

    /**
     * Prepare and fill temp document for specified doc type and mime type with structure that needs for that
     * file.<br>
     * <br>
     * <b>!!! Please make sure that entity is saved in database or removed with help of
     * {@link #deleteTempDocument(LoadDocumentEntity)} method.</b>
     *
     * @param docType
     *            document type
     * @param mimeType
     *            mime type
     * @return prepared document
     * @throws DocumentSaveException
     *             if needed structure for file cannot be created
     */
    LoadDocumentEntity prepareTempDocument(String docType, MimeTypes mimeType) throws DocumentSaveException;

    /**
     * Save document that have been prepared previously.
     *
     * @param loadDocument document to save
     * @return saved document
     */
    LoadDocumentEntity savePreparedDocument(LoadDocumentEntity loadDocument);

    /**
     *  Prepare and fill document for specified doc type and mime type with structure that needs for that file.
     *
     * @param document document to fill
     * @throws DocumentSaveException if needed structure for file cannot be created
     */
    void prepareDocument(LoadDocumentEntity document) throws DocumentSaveException;

    /**
     * Merges the documents and saves the merged document in the path passed with the file name provided.
     *
     * @param loadDocuments
     *            Documents to be merged
     * @param path
     *            Path to which the merged document has to be saved.
     * @param fileName
     *            File name with which the merged document has to be saved.
     * @throws com.pls.core.exception.ApplicationException
     *             thrown if any error in reading the available documents or creating a new document
     */
    void mergeDocuments(List<Long> loadDocuments, String path, String fileName) throws ApplicationException;

    /**
     * Loads the document from the file system and creates multiple pdf documents based on the criteria. Saves the details like document path and file
     * name to the IMAGE_METADATA table.
     *
     * @param documentToSplit
     *            Full path of the document in the file system including the file name.
     * @param criteria
     *            Criteria for splitting the document
     * @throws ApplicationException
     *             thrown if any error in reading the available documents or creating a new document
     */

    void splitDocument(String documentToSplit, List<DocumentSplitCO> criteria) throws ApplicationException;

    /**
     * Creates multiple pdf documents based on the criteria from the document provided. Saves the details like document path and file name to the
     * IMAGE_METADATA table.
     *
     * @param documentToSplit
     *            Actual document bytes for creating new documents.
     * @param criteria
     *            Criteria for splitting the document
     * @throws ApplicationException
     *             thrown if any error in reading the available documents or creating a new document
     */
    void splitDocument(byte[] documentToSplit, List<DocumentSplitCO> criteria) throws ApplicationException;

    /**
     * Saves the document to the file system and updates the database.
     *
     * @param document
     *            document to be saved
     * @throws DocumentSaveException
     *             thrown if any error in saving the document
     */
    void saveDocument(LoadDocumentEntity document) throws DocumentSaveException;

    /**
     * Save document from input stream. Will use prefix as part of file name and path if specified.
     *
     * @param document document to save
     * @param stream document input stream
     * @param prefix prefix for part of file name and path. Can be null
     * @throws DocumentSaveException if document cannot be saved
     */
    void saveDocument(LoadDocumentEntity document, InputStream stream, String prefix) throws DocumentSaveException;

    /**
     * Delete temp document entity with related data on file system.
     *
     * @param tempDocId id of temporary saved document
     * @throws EntityNotFoundException if temp document with specified id doesn't exist
     */
    void deleteTempDocument(long tempDocId) throws EntityNotFoundException;

    /**
     * Delete temp document entity with related data on file system.
     *
     * @param loadDocumentEntity
     *            document to be removed. Can be <code>null</code>
     */
    void deleteTempDocument(LoadDocumentEntity loadDocumentEntity);

    /**
     * Delete document by id.
     *
     * @param docIds
     *            ids of document to delete
     */
    void deleteDocuments(List<Long> docIds);

    /**
     * Delete all temp documents that were created more than specified number of days.
     *
     * @param passedDays count of days that have passed after documents creation
     */
    void deleteStaleTempDocuments(int passedDays);

    /**
     * Change temporary stored document to be saved permanently.
     *
     * @param tempDocId temporary document id
     * @param loadId id of load for document to belong
     * @param docType document type
     * @throws DocumentSaveException if document cannot be moved to permanent location
     * @throws EntityNotFoundException if temporary document with specified id doesn't exist
     */
    void moveAndSaveTempDocPermanently(Long tempDocId, Long loadId, String docType) throws EntityNotFoundException, DocumentSaveException;

    /**
     * Returns document entity with a stream.
     *
     * @param documentId document's id
     * @return {@link LoadDocumentEntity}
     * @throws EntityNotFoundException if document with specified id doesn't exist
     * @throws DocumentReadException for error when reading the document
     */
    LoadDocumentEntity getDocumentWithStream(Long documentId) throws EntityNotFoundException, DocumentReadException;

    /**
     * Returns document entity with a stream.
     *
     * @param documentId document's id
     * @param downloadToken
     *            token for download file without authorization
     * @return {@link LoadDocumentEntity}
     * @throws EntityNotFoundException if document with specified id doesn't exist
     * @throws DocumentReadException for error when reading the document
     */
    LoadDocumentEntity getDocumentWithStream(Long documentId, String downloadToken) throws EntityNotFoundException, DocumentReadException;

    /**
     * Concatenates list of files in one and saves it.
     * 
     * @param documents
     *            list of documents to concatenate
     * @param documentType
     *            type of document
     * @return concatenated document entity
     * @throws ApplicationException
     *             if concatenation failed
     */
    LoadDocumentEntity concatenateAndSaveDocument(List<InputStream> documents, DocumentTypes documentType) throws ApplicationException;

    /**
     * Initialize input stream for document.
     * 
     * @param document
     *            saved document
     * @throws DocumentReadException
     *             if initialization failed
     */
    void initDocumentStream(LoadDocumentEntity document) throws DocumentReadException;
}
