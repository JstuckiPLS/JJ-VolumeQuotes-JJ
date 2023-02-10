package com.pls.ltlrating.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.DotRegionFuelEntity;

/**
 * DAO for working with {@link DotRegionFuelEntity}.
 *
 * @author Stas Norochevskiy
 *
 */
public interface DotRegionFuelDao extends AbstractDao<DotRegionFuelEntity, Long> {

    /**
     * Save list of entities.
     * @param entities to save
     * @return persisted entities
     */
    List<DotRegionFuelEntity> saveAll(List<DotRegionFuelEntity> entities);

    /**
     * Get list of {@link DotRegionFuelEntity}.
     * @param fromDate start date for DotRegionFuelEntity
     * @param toDate end date for DotRegionFuelEntity
     * @param ids list of IDs to compare. Can be null.
     * @return list of retrieved {@link DotRegionFuelEntity}
     */
    List<DotRegionFuelEntity> getByDateRangeAndIds(Date fromDate, Date toDate, List<Long> ids);

    /**
     * Get list of {@link DotRegionFuelEntity} with active status and valid effective and expiration date.
     *
     * @return list of {@link DotRegionFuelEntity}
     */
    List<DotRegionFuelEntity> getActiveRegionRates();

    /**
     * Expirate all existing records.
     */
    void expirateRates();
}
