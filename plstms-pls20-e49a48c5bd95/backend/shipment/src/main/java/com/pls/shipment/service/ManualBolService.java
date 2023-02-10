package com.pls.shipment.service;

import java.util.List;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.validation.ValidationException;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;

/**
 * Manual BOL Service.
 * 
 * @author Alexander Nalapko
 *
 */
public interface ManualBolService {

    /**
     * save BOL.
     * 
     * @param entity
     *            - ManualBolEntity
     * @return ManualBolEntity
     * @throws ValidationException in cases when validation failed.
     */
    ManualBolEntity saveOrUpdate(ManualBolEntity entity) throws ValidationException;

    /**
     * find manual BOL for id.
     * 
     * @param id
     *            - manual BOL id
     * @return ManualBolEntity
     */
    ManualBolEntity find(Long id);

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
     * Find manual BOL like Shipment for criteria.
     * 
     * @param search
     *            - object holding all necessary search parameters
     * @param userId
     *            id of user
     * @return list of found BOLs
     */
    List<ShipmentListItemBO> findAllShipment(RegularSearchQueryBO search, Long userId);

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
