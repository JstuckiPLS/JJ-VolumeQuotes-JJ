package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.PageQueryBO;
import com.pls.core.domain.organization.CountryEntity;

/**
 * DAO for {@link CountryEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface CountryDao extends AbstractDao<CountryEntity, String> {
    /**
     * Get all active countries {@link CountryEntity}.
     *
     * @return list of countries
     */
    List<CountryEntity> getActiveCountries();

    /**
     * Get country by name or country code.
     * 
     * @param keyword
     *            name or code of a country
     * @param page
     *            count of records which should be exist on the one page
     * @return found country
     */
    List<CountryEntity> getCountriesByCodeOrName(String keyword, PageQueryBO page);

    /**
     * Get full country code by short country code.
     * 
     * @param shortCountryCode
     *            short country code
     * @return full country code or <code>null</code>
     */
    String findFullCountryCode(String shortCountryCode);

    /**
     * Get short country code by full country code.
     * 
     * @param fullCountryCode
     *            full country code
     * @return short country code or <code>null</code>
     */
    String findShortCountryCode(String fullCountryCode);
}
