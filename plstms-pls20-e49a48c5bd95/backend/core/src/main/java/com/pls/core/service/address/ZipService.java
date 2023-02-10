package com.pls.core.service.address;

import java.util.List;

import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.bo.AddressBO;
import com.pls.core.service.validation.ValidationException;

/**
 * Business service that handle business logic for zip.
 *
 * @author Gleb Zgonikov
 */
public interface ZipService {

    /**
     * Method returns the list of the ZipCodeEntity by country code and search criteria tha can be value of the city,
     * state or zip.
     *
     * @param countryCode is a countryCode, if null then it is omitted from search query
     * @param searchCriteria is a search criteria
     * @param count is an amount of zip to return
     * @return the list of the {@link ZipCodeEntity}
     * @throws ValidationException exception
     */
    List<ZipCodeEntity> findZips(String countryCode, String searchCriteria, Integer count) throws ValidationException;
    

    /**
     * Method returns the list of the ZipCodeEntity by country code and search criteria tha can be value of the city,
     * state or zip.
     *
     * @param countryCode is a countryCode, if null then it is omitted from search query
     * @param searchCriteria is a search criteria
     * @param count is an amount of zip to return
     * @return the list of the {@link ZipCodeEntity}
     * @throws ValidationException exception
     */
    List<ZipCodeEntity> findZipForMultipleCountries(String countryCodes, String searchCriteria, Integer count) throws ValidationException;

    
    /**
     * Validate address.
     *
     * @param dto the dto
     * @return the address dto
     */
    AddressBO validateAddress(AddressBO dto);
}
