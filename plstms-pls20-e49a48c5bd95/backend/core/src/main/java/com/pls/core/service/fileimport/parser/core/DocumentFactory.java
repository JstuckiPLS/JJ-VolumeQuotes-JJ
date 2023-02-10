package com.pls.core.service.fileimport.parser.core;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.csv.CsvDocument;
import com.pls.core.service.fileimport.parser.core.excel.ExcelDocument;

/**
 * Build {@link com.pls.core.service.fileimport.parser.core.Document} from {@link InputStream}.
 * 
 * @author Artem Arapov
 */
public final class DocumentFactory {

    private DocumentFactory() {
    }

    /**
     * Create instance of {@link com.pls.core.service.fileimport.parser.core.Document} from not <code>null</code> {@link InputStream}.
     * 
     * @param stream
     *            Not <code>null</code> {@link InputStream}.
     * @param extension
     *            Not <code>null</code> {@link FileExtensionType}
     * @return {@link com.pls.core.service.fileimport.parser.core.Document}
     * @throws ImportException
     *             exception
     */
    public static Document create(InputStream stream, FileExtensionType extension) throws ImportException {
        if (extension == null) {
            throw new IllegalArgumentException("Argument 'extension' can't be null");
        }

        Document document = null;

        try {
            switch (extension) {
            case XLS:
            case XLSX:
                document = new ExcelDocument(stream);
                break;
            case CSV:
                document = new CsvDocument(stream);
                break;
            default:
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return document;
    }

    /**
     * File extension constants which supports by {@link DocumentFactory}.
     * */
    public enum FileExtensionType {
        /**
         * Excel file extension '*.xlsx'.
         */
        XLSX("xlsx"),
        /**
         * Excel file extension '*.xls'.
         */
        XLS("xls"),
        /**
         * CSV file extension '*.csv'.
         */
        CSV("csv");

        private String value;

        FileExtensionType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

        /**
         * Get appropriated {@link FileExtensionType} which relate to <code>value</code>.
         * 
         * @param value Not <code>null</code> and not empty <code>String</code> value
         * @return {@link FileExtensionType} if this value was recognized, otherwise return null.
         * */
        public static FileExtensionType getByValue(String value) {
            FileExtensionType result = null;

            for (FileExtensionType type : FileExtensionType.values()) {
                if (StringUtils.equalsIgnoreCase(type.value, value)) {
                    result = type;
                    break;
                }
            }

            return result;
        }


        @Override
        public String toString() {
            return value;
        }
    }
}
