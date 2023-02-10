package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
/**
 * Data Access Object for {@link LtlAccessorialsMappingEntity} data.
 *
 * @author Dmitriy Davydenko.
 */
public interface LtlAccessorialsMappingDao extends AbstractDao<LtlAccessorialsMappingEntity, Long> {

    /**
     * Gets accessorials mapping for selected Carrier.
     * 
     * @param carrierId
     *            - id of the carrier.
     * @return List<LtlAccessorialsMappingEntity> - List of {@link LtlAccessorialsMappingEntity}.
     */
    List<LtlAccessorialsMappingEntity> getAccessorialsMapping(Long carrierId);

    /**
     * Gets accessorials mapping by SCAC.
     * 
     * @param scac
     *            - scac of the carrier.
     * @return List<LtlAccessorialsMappingEntity> - List of {@link LtlAccessorialsMappingEntity}.
     */
    List<LtlAccessorialsMappingEntity> getAccessorialsMappingBySCAC(String scac);
}
