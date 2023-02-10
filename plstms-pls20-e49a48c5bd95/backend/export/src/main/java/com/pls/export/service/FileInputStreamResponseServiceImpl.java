package com.pls.export.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.export.ExcelDataBuilder;

/**
 * Implementation of FileInputStreamResponseService.
 *
 * @author Mykola Teslenko
 */
@Service
public class FileInputStreamResponseServiceImpl implements FileInputStreamResponseService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Override
    public FileInputStreamResponseEntity getExcelDataAsStreamResource(ExcelDataBuilder excelDataBuilder) throws ApplicationException {
        LoadDocumentEntity tempDocument = documentService.prepareTempDocument(DocumentTypes.TEMP.getDbValue(), MimeTypes.XLSX);
        try {
            File docFile = new File(docFileNamesResolver.buildDirectPath(tempDocument.getDocumentPath()), tempDocument.getDocFileName());
            FileOutputStream outputStream = new FileOutputStream(docFile);
            excelDataBuilder.buildExcelData(outputStream);
            documentService.savePreparedDocument(tempDocument); // save the document so that it gets deleted accurately by scheduler
            InputStream documentInputStream = documentService.getDocumentInputStream(tempDocument);
            return new FileInputStreamResponseEntity(documentInputStream, documentInputStream.available(), excelDataBuilder.buildFileName());
        } catch (Exception e) {
            documentService.deleteTempDocument(tempDocument);
            throw new ApplicationException("Preparing document failed.", e);
        }
    }
}
