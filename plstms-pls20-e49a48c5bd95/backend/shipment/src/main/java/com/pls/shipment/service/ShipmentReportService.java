package com.pls.shipment.service;

import java.io.IOException;
import java.text.ParseException;

import com.pls.core.domain.bo.ReportsBO;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Service for reports.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public interface ShipmentReportService {

    /**
     * Retrieves binary data for loads report. {@link ReportsBO}.
     *
     * @param reportParamsBO
     *            report parameters Object.
     * @return report data as input stream at response.
     * @throws IOException
     *             - export exception.
     * @throws ParseException
     *             - parse exception.
     */
    FileInputStreamResponseEntity exportReport(ReportParamsBO reportParamsBO) throws IOException, ParseException;

}
