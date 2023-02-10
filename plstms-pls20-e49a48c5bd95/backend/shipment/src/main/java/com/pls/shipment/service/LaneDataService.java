package com.pls.shipment.service;

import com.pls.shipment.domain.LaneDataEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Server-side service interface.
 * 
 * @author Viacheslav Vasianovych
 */
public interface LaneDataService {
    /**
     * Get lane data within the specified period.
     * 
     * @param customerId Customer ID.
     * @param startDate
     *            - start date of specified period.
     * @param endDate
     *            - end date of specified period.
     * @return lane data within the specified period.
     */
    List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate);

    /**
     * Get lane data within the specified period and generate MS Excel file.
     * 
     * @param customerId Customer ID.
     * @param startDate
     *            - start date of specified period.
     * @param endDate
     *            - end date of specified period.
     * @param sortInfo
     *            - mapped sort direction on the column name.
     * @param out lane data within the specified period.
     * @throws IOException Unable to prepare Excel file.
     */
    void generateExcelFile(Long customerId, OutputStream out, Date startDate, Date endDate,
            Map<String, String> sortInfo) throws IOException;
}
