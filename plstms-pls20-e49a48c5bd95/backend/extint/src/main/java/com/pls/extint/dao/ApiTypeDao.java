package com.pls.extint.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.extint.domain.ApiTypeEntity;

/**
 * DAO class for loading/saving api types along with the api metadata.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiTypeDao extends AbstractDao<ApiTypeEntity, Long> {

    /**
     * Loads all the API Types for the organization and specific category.
     * 
     * @param carrierOrgId
     *            carrier organization for whom the API Types has to be loaded
     * @param shipperOrgId
     *            shipper organization for whom the API Types has to be loaded
     * @param category
     *            category for which the API Types has to be loaded
     * @param apiOrgType
     *            org type of the API for which API Type is loaded.
     * @return all API types applicable for that organization and category
     */
    List<ApiTypeEntity> findByCategory(Long carrierOrgId, Long shipperOrgId, String category, String apiOrgType);

    /**
     * Loads all the Document API Types of the carrier to download the load for documents required by shipper.
     * 
     * @param loadId
     *            id of Load
     * @param carrierOrgId
     *            carrier organization for whom the API Types has to be loaded
     * @param shipperOrgId
     *            shipper organization for whom the API Types has to be loaded
     * @param apiOrgType
     *            org type of the API for which API Type is loaded.
     * @return all API types applicable for that organization and category
     */
    List<ApiTypeEntity> findDocumentApiTypesForLoad(Long loadId, Long carrierOrgId, Long shipperOrgId, String apiOrgType);

    /**
     * Returns the API/PLS lookup value from API_LOOKUP_TABLE. If ApiLookup is true, the lookupValue in the method argument is a PLS Value and the
     * appropriate API value for the organization is returned. If it is false, the lookupValue in the method arguments is from API and the appropriate
     * PLS value for the lookupValue is returned.
     * 
     * @param lookupGroup
     *            The lookup group name.
     * @param orgId
     *            The organization for which API service was invoked
     * @param lookupValue
     *            API/PLS value that needs to be lookedup.
     * @param isApiLookup
     *            If true, lookupValue is a PLS value and appropriate API value is returned. If false, lookup value is API value and appropriate PLS
     *            Value is returned.
     * @return the appropriate API/PLS value.
     */
    String getLookupValue(String lookupGroup, Long orgId, String lookupValue, boolean isApiLookup);
}
