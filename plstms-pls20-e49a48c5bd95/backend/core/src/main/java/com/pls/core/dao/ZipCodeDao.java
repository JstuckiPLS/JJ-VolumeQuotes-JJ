package com.pls.core.dao;

import java.util.List;
import java.util.stream.Stream;

import com.pls.core.domain.address.ZipCodeEntity;

/**
 * DAO for ZipCodeEntity.
 *
 * @author Sergey Kirichenko
 */
public interface ZipCodeDao {

    /**
     * Method returns the list of the ZipCodeEntity by country code and search criteria that can be value of
     * the city, state or zip.
     *
     * @param countryCode
     *            is a countryCode, if null then it is omitted from search query
     * @param searchCriteria
     *            is a search criteria
     * @param maxResults
     *            maximum number of entities to be returned.
     * @return the list of the {@link ZipCodeEntity}
     * @throws IllegalArgumentException
     *             if searchCriteria is blank
     */
    List<ZipCodeEntity> findZipCodesByCountryCodeAndSearchCriteria(String countryCode, String searchCriteria, Integer maxResults)
            throws IllegalArgumentException;
    
    /**
     * Method returns the list of the ZipCodeEntity by country code(s) and search criteria that can be value of
     * the city, state or zip.
     *
     * @param countryCode
     *            is a comma separated list of countryCode, if null then it is omitted from search query
     * @param searchCriteria
     *            is a search criteria
     * @param maxResults
     *            maximum number of entities to be returned.
     * @return the list of the {@link ZipCodeEntity}
     * @throws IllegalArgumentException
     *             if searchCriteria is blank
     */
    List<ZipCodeEntity> findZipCodesByCountryCodesAndSearchCriteria(String countryCodes, String searchCriteria, Integer maxResults)
            throws IllegalArgumentException;

    /**
     * Checks if is zip codes exist.
     *
     * @param zipCodes the zip codes
     * @return the list of absent zip codes
     */
    List<String> isZipCodesExist(List<String> zipCodes);


    /**
     * Checks if is state codes exist.
     *
     * @param stateCodes the state codes
     * @return the list of absent zip codes
     */
    List<String> isStateCodesExist(List<String> stateCodes);


    /**
     * Checks if is cities exist.
     *
     * @param cities the cities
     * @return the list of absent cities
     */
    List<String> isCitiesExist(List<String> cities);


    /**
     * Checks if is country codes exist.
     *
     * @param countryCodes the country codes
     * @return the list of absent countries
     */
    List<String> isCountryCodesExist(List<String> countryCodes);

    /**
     * Returns all {@link ZipCodeEntity} as {@link Stream}.
     * 
     * @return Stream of {@link ZipCodeEntity}.
     */
    Stream<ZipCodeEntity> getZipCodesAsStream();

    /**
     * Get list of default {@link ZipCodeEntity} items by specified country and zip.
     * 
     * @param countryCode
     *            country
     * @param zipCode
     *            zip
     * @return list of default zip codes
     */
    List<ZipCodeEntity> getDefault(String countryCode, String zipCode);
}
