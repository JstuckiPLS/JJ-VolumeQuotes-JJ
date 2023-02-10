package com.pls.core.service.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.exception.file.ExportException;

/**
 * Base class for exporting entities to document.
 * 
 * @author Stas Norochevskiy
 *
 *
 * @param <EntityType> entity type to be exported
 */
public abstract class ExcelDocumentWriter<EntityType> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Writes list of Entities to excel.
     * @param entities entities to be save
     * @return body of excel document
     * @throws ExportException exception
     */
    public byte[] createFileBody(List<EntityType> entities) throws ExportException {

        if (entities == null || entities.isEmpty()) {
            logger.info("entities is null or empty");
            throw new ExportException("No data to export");
        }

        ByteArrayOutputStream fileBody = new ByteArrayOutputStream();
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exported entities");

        // Create Header
        Row header = sheet.createRow(0);
        String[] headerColumns = getHeaders();
        for (int i = 0; i < headerColumns.length; i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(headerColumns[i]);
        }

        // Create rows with data
        for (int i = 0; i < entities.size(); i++) {
            Row row = sheet.createRow(i + 1); // 1-st row is header
            String[] rowData = getRowFromEntity(entities.get(i));
            for (int j = 0; j < rowData.length; j++) {
                Cell dataCell = row.createCell(j);
                dataCell.setCellValue(rowData[j]);
            }
        }

        try {
            workbook.write(fileBody);
        } catch (IOException e) {
            throw new ExportException("Cannot write excel data to buffer", e);
        }

        return fileBody.toByteArray();

    }

    /**
     * Get array of column names for document.
     * @return array of column names for document
     */
    public abstract String[] getHeaders();

    /**
     * Transform Entity to a an array of values (in string representation).
     * @param entity entity
     * @return array of values to be writed as row
     */
    public abstract String[] getRowFromEntity(EntityType entity);
}
