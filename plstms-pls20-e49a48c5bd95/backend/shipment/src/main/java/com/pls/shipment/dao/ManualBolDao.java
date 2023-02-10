package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;

/**
 * Dao for {@link ManualBolEntity}.
 *
 * @author Alexander Nalapko
 */
public interface ManualBolDao extends AbstractDao<ManualBolEntity, Long> {
    /**
     * Find manual BOL for criteria.
     * 
     * @param search
     *            - object holding all necessary search parameters
     * @param userId
     *            id of user
     * @return list of found BOLs
     */
    List<ManualBolListItemBO> findAll(RegularSearchQueryBO search, Long userId);

    /**
     * Cancel BOL by id.
     * 
     * @param id
     *            - manual BOL id
     * @return boolean
     *            - true if manual BOL canceled
     * @throws EntityNotFoundException
     *            - if BOL with <id> not found
     */
    boolean cancel(Long id) throws EntityNotFoundException;

}
