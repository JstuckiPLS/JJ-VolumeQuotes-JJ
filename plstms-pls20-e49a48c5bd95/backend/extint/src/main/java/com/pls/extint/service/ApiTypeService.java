package com.pls.extint.service;

import java.util.List;

import com.pls.extint.domain.ApiTypeEntity;

/**
 * 
 * Service for handling the business logic for Api Types.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiTypeService {

    /**
     * Get the api types matching the crietria.
     * 
     * @param carrierOrgId
     *            carrier organization
     * @param shipperOrgId
     *            shipper organization
     * @param category
     *            category of the api
     * @param apiOrgType
     *            organization type
     * @return List of api types matching the criteria.
     */
    List<ApiTypeEntity> getByCategory(Long carrierOrgId, Long shipperOrgId, String category, String apiOrgType);

    /**
     * Creates a new record if it doesn't exist else updates the record.
     * 
     * @param entity
     *            entity to persist or update.
     * @return the saved entity
     */
    ApiTypeEntity save(ApiTypeEntity entity);

}
