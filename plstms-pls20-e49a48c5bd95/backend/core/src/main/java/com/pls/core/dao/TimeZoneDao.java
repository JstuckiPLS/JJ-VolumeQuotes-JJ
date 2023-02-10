package com.pls.core.dao;

import com.pls.core.domain.TimeZoneEntity;

/**
 * DAO for {@link TimeZoneEntity}.
 *
 * @author Sergey Kirichenko
 */
public interface TimeZoneDao extends DictionaryDao<TimeZoneEntity> {

    /**
     * Find time zone by country and zip code.
     *
     * @param countryCode country code
     * @param zipCode zip/postal code
     * @return {@link TimeZoneEntity} or null
     */
    TimeZoneEntity findByCountryZip(String countryCode, String zipCode);
}
