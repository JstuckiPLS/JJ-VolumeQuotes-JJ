/**
 * 
 */
package com.pls.ltlrating.service.analysis.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.ltlrating.service.analysis.FreightAnalysisValidationService;
import com.pls.ltlrating.service.analysis.fileimport.AnalysisReportSheetValidator;

/**
 * An implementation of {@link FreightAnalysisValidationService} for validating of users uploaded file.
 * 
 * @author Dmitriy Davydenko
 *
 */
@Service
@Transactional(readOnly = true)
public class FreightAnalysisValidationServiceImpl implements FreightAnalysisValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(FreightAnalysisValidationServiceImpl.class);

    @Autowired
    private DocumentService documentService;

    @Override
    public long validateFileForFreightAnalysis(Long docId) throws Exception {
        Workbook workbook = null;
        long result = -2L;

        LoadDocumentEntity docToValidate = documentService.loadDocumentWithoutContent(docId);
        File fileToValidate = documentService.getDocumentFile(docToValidate);
        try {
            workbook = WorkbookFactory.create(fileToValidate);
            Sheet sheet = workbook.getSheetAt(0);
            result = validateSheet(sheet);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return -1;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return result;
    }

    private long validateSheet(Sheet sheet) throws IOException, DocumentSaveException {

        CellStyle style = createErrorDescriptionCellStyle(sheet);
        AnalysisReportSheetValidator validator = new AnalysisReportSheetValidator(style, sheet);
        createErrorDescriptionHeaderCell(sheet, validator.getErrorCellNumber());
        try {
            validator.validateHeader();
            validator.checkForFileEmptiness();
        } catch (Exception ex) {
            return -1;
        }
        boolean errorsAppeared = validator.validateSheet();

        if (errorsAppeared) {
            LoadDocumentEntity document = documentService.prepareTempDocument(DocumentTypes.TEMP.getDbValue(), MimeTypes.XLSX);
            documentService.savePreparedDocument(document);
            File file = documentService.getDocumentFile(document);
            FileOutputStream outputStream = new FileOutputStream(file);
            sheet.getWorkbook().write(outputStream);
            outputStream.close();
            sheet.getWorkbook().close();
            return document.getId();
        }
        return -2;
    }

    private CellStyle createErrorDescriptionCellStyle(Sheet sheet) {
        CellStyle errorDescriptionCellStyle = sheet.getWorkbook().createCellStyle();
        errorDescriptionCellStyle.setBorderBottom(BorderStyle.THIN);
        errorDescriptionCellStyle.setBorderRight(BorderStyle.THIN);
        return errorDescriptionCellStyle;
    }

    private void createErrorDescriptionHeaderCell(Sheet sheet, int colNumber) {
        Row headerRow = sheet.getRow(0);
        Cell errorHeaderCell = headerRow.createCell(colNumber);
        errorHeaderCell.setCellStyle(createErrorDescriptionHeaderCellStyle(sheet));
        errorHeaderCell.setCellValue("Error Description");
        errorHeaderCell.setCellType(CellType.STRING);
        sheet.autoSizeColumn(colNumber);
    }

    private CellStyle createErrorDescriptionHeaderCellStyle(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints(new Integer(10).shortValue());
        font.setBold(true);

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.cloneStyleFrom(sheet.getRow(0).getCell(0).getCellStyle());
        style.setBorderBottom(BorderStyle.THIN);
        style.setFont(font);
        return style;
    }
}
