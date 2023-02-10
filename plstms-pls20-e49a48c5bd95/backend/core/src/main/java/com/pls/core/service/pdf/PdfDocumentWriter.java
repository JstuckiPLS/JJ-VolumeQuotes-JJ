package com.pls.core.service.pdf;

import java.io.OutputStream;

import com.pls.core.service.pdf.exception.PDFGenerationException;

/**
 * PDF document writer that handles pdf document generation.
 *
 * @param <T> type of parameter for document writing
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface PdfDocumentWriter<T extends PdfDocumentParameter> {
    /**
     * Write pdf document from {@link PdfDocumentParameter} directly to output stream.
     *
     * @param parameter {@link PdfDocumentParameter} instance with parameters that needs for specific implementation
     * @param outputStream output stream of  pdf content
     * @throws PDFGenerationException will be thrown if document generation fails
     */
    void write(T parameter, OutputStream outputStream) throws PDFGenerationException;
}
