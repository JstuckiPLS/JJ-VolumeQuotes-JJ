package com.pls.shipment.dao;

import com.pls.shipment.domain.LaneDataEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao interface for lane data.
 *
 * @author Viacheslav Vasianovych
 *
 */
public interface LaneDataDao {
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
     * Get lane data within the specified period, and specified sort directions.
     *
     * @param customerId Customer ID.
     * @param startDate
     *            - start date of specified period.
     * @param endDate
     *            - end date of specified period.
     * @param sortInfo
     *            - mapped sort direction on the column name.
     * @return lane data within the specified period.
     */
    List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate, Map<String, String> sortInfo);
}
