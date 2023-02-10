package com.pls.documentmanagement.service;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Prepare file names and folder path for documents.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class DocFileNamesResolver {
    private static final String DEFAULT_FILE_EXTENSION = "pdf";

    protected static final String FILE_PATH_SEPARATOR = File.separator;

    protected static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss_SSS";

    private static final String LOAD_DOCUMENTS_FOLDER = "loadDocument";

    private static final String MANUAL_BOL_DOCUMENT_FOLDER = "manualBolDocument";

    private static final String TEMP_DOCUMENTS_FOLDER = "tempDocument";

    @Value("${documents.path}")
    private String documentsPath;

    /**
     * Sets the path in the file system for load document. These are auto generated based on load id and document type.
     *
     * @param document
     *            document whose properties are to be set.
     * @return file path to document
     */
    public String buildDocumentPath(LoadDocumentEntity document) {
        StringBuilder builder = new StringBuilder(LOAD_DOCUMENTS_FOLDER);
        Long loadId = document.getLoadId();
        // TODO load ID?
        builder.append(FILE_PATH_SEPARATOR).append(loadId).append(FILE_PATH_SEPARATOR).append(document.getDocumentType());

        return builder.toString();
    }

    /**
     * Sets the path in the file system for ManualBol document. These are auto generated based on Manual Bol id and document type.
     * 
     * @param document
     *            document whose properties are to be set.
     * @return file path to document
     */
    public String buildManualBolPath(LoadDocumentEntity document) {
        StringBuilder builder = new StringBuilder(MANUAL_BOL_DOCUMENT_FOLDER);
        Long loadId = document.getManualBolId();
        builder.append(FILE_PATH_SEPARATOR).append(loadId).append(FILE_PATH_SEPARATOR).append(document.getDocumentType());

        return builder.toString();
    }

    /**
     * Sets the file name for load document. These are auto generated based on load id and document type.
     *
     * @param document
     *            document whose properties are to be set.
     * @return file name of document
     */
    public String buildDocumentFileName(LoadDocumentEntity document) {
        String extension = ObjectUtils.toString(document.getFileType(), DEFAULT_FILE_EXTENSION).toLowerCase();
        Long loadId = document.getLoadId();

        return buildDocumentFileName(loadId, extension, document.getDocumentType());
    }

    /**
     * Sets the file name for Manual Bol document. These are auto generated based on Manual Bol Id and document type.
     *
     * @param document
     *            document whose properties are to be set.
     * @return file name of document
     */
    public String buildManualBolDocumentFileName(LoadDocumentEntity document) {
        String extension = ObjectUtils.toString(document.getFileType(), DEFAULT_FILE_EXTENSION).toLowerCase();
        Long loadId = document.getManualBolId();

        return buildDocumentFileName(loadId, extension, document.getDocumentType());
    }

    /**
     * Prepare filename based on prefix and mime type.
     *
     * @param prefix filename prefix
     * @param mimeType mime type of document
     * @return file name of document
     */
    public String buildDocumentFileName(String prefix, MimeTypes mimeType) {
        String extension = ObjectUtils.toString(mimeType, DEFAULT_FILE_EXTENSION).toLowerCase();

        StringBuilder builder = new StringBuilder();

        builder.append(prefix)
                .append('-')
                .append(getFileVersion())
                .append('.')
                .append(extension);

        return builder.toString();
    }

    /**
     * Get version of file.
     *
     * @return file version
     */
    public String getFileVersion() {
        return DateFormatUtils.format(new Date(), DATE_FORMAT, Locale.US);
    }

    /**
     * Prepare path for temp document accordingly to document type.
     *
     * @param docType document type
     * @return path to temp document folder
     */
    public String buildTempDocumentPath(String docType) {
        StringBuilder builder = new StringBuilder(TEMP_DOCUMENTS_FOLDER);
        builder.append(FILE_PATH_SEPARATOR);
        if (StringUtils.isBlank(docType)) {
            builder.append("UNKNOWN-").append(UUID.randomUUID());
        } else {
            builder.append(docType);
            builder.append('-');
            builder.append(getFileVersion());
        }

        return builder.toString();
    }

    /**
     * Prepare file name of temp document accordingly to document type and mime type.
     *
     * @param mimeType mime type
     * @param docType document type
     * @return file name of temp document
     */
    public String buildTempFileName(String docType, MimeTypes mimeType) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(docType)) {
            builder.append(UUID.randomUUID());
        } else {
            builder.append(docType);
        }

        builder.append('-');
        builder.append(getFileVersion());
        builder.append('.');
        builder.append(mimeType.name().toLowerCase());

        return builder.toString();
    }

    /**
     * Prepare absolute path to specified relative path considering folder where that path should be.
     *
     * @param folder folder where relative path should be
     * @param relativePath relative path
     * @return absolute path
     */
    public String buildPathToSpecificFolder(String folder, String relativePath) {
        StringBuilder builder = new StringBuilder(documentsPath);

        if (StringUtils.isNotBlank(folder)) {
            builder.append(FILE_PATH_SEPARATOR);
            builder.append(folder);
        }

        builder.append(FILE_PATH_SEPARATOR);
        builder.append(relativePath);

        return builder.toString();
    }

    /**
     * Prepare direct path based on relative path.
     *
     * @param relativePath relative path
     * @return direct path
     */
    public String buildDirectPath(String relativePath) {
        return buildPathToSpecificFolder(null, relativePath);
    }

    protected void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }

    private String buildDocumentFileName(Long loadId, String extension, String documentType) {
        StringBuilder builder = new StringBuilder();
        if (loadId != null) {
            builder.append(loadId);
        } else {
            builder.append(UUID.randomUUID());
        }

        builder.append('-')
                .append(documentType)
                .append('-')
                .append(getFileVersion())
                .append('.')
                .append(extension);

        return builder.toString();
    }
}
