package com.pls.core.service.address;

import java.util.List;

import com.pls.core.domain.organization.CountryEntity;

/**
 * Business service that handles business logic for CountryEntity.
 * 
 * @author Artem Arapov
 *
 */
public interface CountryService {

    /**
     * Retrieves a list of all countries.
     * 
     * @return list of countries
     * */
    List<CountryEntity> getAll();

    /**
     * Returns list of {@link com.pls.core.domain.organization.CustomerEntity} filled with search results.
     * 
     * @param countryName
     *            keyword to search a list of countries
     * @param count
     *            count of records in result list
     * @return list or empty list
     */
    List<CountryEntity> findCountries(String countryName, Integer count);
}
