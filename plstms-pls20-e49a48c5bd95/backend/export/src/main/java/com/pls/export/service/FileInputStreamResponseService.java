package com.pls.export.service;

import org.springframework.stereotype.Service;

import com.pls.core.exception.ApplicationException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.export.ExcelDataBuilder;

/**
 * Class for obtaining FileInputStreamResponseEntity for export reports.
 *
 * @author Mykola Teslenko
 */
@Service
public interface FileInputStreamResponseService {

    /**
     * Method for obtaining FileInputStreamResponseEntity object for export.
     *
     * @param excelDataBuilder data builder
     * @return FileInputStreamResponseEntity {@link FileInputStreamResponseEntity} object
     * @throws ApplicationException when error with saving temp excel file
     */
    FileInputStreamResponseEntity getExcelDataAsStreamResource(ExcelDataBuilder excelDataBuilder) throws ApplicationException;
}
