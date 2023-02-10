package com.pls.ltlrating.service.analysis.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.ltlrating.dao.analysis.FAFinancialAnalysisDao;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.service.analysis.FreightAnalysisImportExportService;
import com.pls.ltlrating.service.analysis.fileimport.AnalysisExcelBuilder;
import com.pls.ltlrating.service.analysis.fileimport.AnalysisImportProcessor;


/**
 *  Implementation of {@link FreightAnalysisImportExportService}.
 *
 * @author Svetlana Kulish
 *
 */
@Service
@Transactional
public class FreightAnalysisImportExportServiceImpl implements FreightAnalysisImportExportService {
    private static final Logger LOG = LoggerFactory.getLogger(FreightAnalysisImportExportServiceImpl.class);

    @Autowired
    private FAFinancialAnalysisDao dao;

    @Autowired
    private DocumentService documentService;

    @Override
    public Set<FAInputDetailsEntity> importInputDetailsFromFile(Long docId) throws ImportException, EntityNotFoundException, DocumentReadException {
        AnalysisImportProcessor importProcessor = new AnalysisImportProcessor();
        try {
            LoadDocumentEntity document = documentService.getDocumentWithStream(docId);
            InputStream stream = documentService.getDocumentInputStream(document);
            importProcessor.processImport(stream, FileExtensionType.XLSX);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return importProcessor.getRowsSet();
    }

    @Override
    public void createOutputExcelFile(Long analysisId) {
        LOG.info("Start generating result file for Analysis ID: {}", analysisId);
        LoadDocumentEntity document = null;
        try {
            document = documentService.prepareTempDocument(DocumentTypes.TEMP.getDbValue(), MimeTypes.XLSX);
            documentService.savePreparedDocument(document);
            File file = documentService.getDocumentFile(document);
            FAFinancialAnalysisEntity analysisEntity = dao.getWithDependencies(analysisId);
            new AnalysisExcelBuilder().generateFile(analysisEntity, file);
            documentService.moveAndSaveTempDocPermanently(document.getId(), null, DocumentTypes.UNKNOWN.getDbValue());
            dao.updateAnalysisWithResultFile(analysisId, document.getId());
        } catch (Exception e) {
            LOG.error("Error generating output file for Freight Analysis. " + e.getMessage(), e);
            documentService.deleteTempDocument(document);
        } finally {
            LOG.info("Completed generating result file for Analysis ID: {}", analysisId);
        }
    }
}
