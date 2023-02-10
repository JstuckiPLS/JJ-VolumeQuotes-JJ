package com.pls.core.service.address.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.bo.AddressBO;
import com.pls.core.service.address.ZipService;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Countries;

/**
 * {@link com.pls.core.service.address.ZipService} implementation.
 *
 * @author Gleb Zgonikov
 */
@Service
@Transactional
public class ZipServiceImpl implements ZipService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipServiceImpl.class);

    private static final String SPLITTER_REGEXP = ",";
    
    @Autowired
    private ZipCodeDao zipCodeDao;
    
    /** Cross map the code to the Name. Not sure of all of what would break if I "fixed" the Countries enum class.**/
    private String getCountryCodeEnumName(String countryCode) {
    	
    	switch (countryCode) {
    		case "USA" : return "USA";
    		case "CAN" : return "CANADA";
    		case "MEX" : return "MEXICO";
    		default: return "";
    	}
    }
    
    @Override
    public List<ZipCodeEntity> findZips(String countryCode, String searchCriteria, Integer count) throws ValidationException {
        
    	 LOGGER.debug(" Looking up zip code matches for Country: " + countryCode + " and search Criteria: " + searchCriteria);
    	
    	 if (StringUtils.isBlank(countryCode)) {
             HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
             errors.put("countryCode", ValidationError.IS_EMPTY);
             LOGGER.error("No Country was specified - unable to look up matching addresses.");
             throw new ValidationException(errors);
         }
    	 
    	 
    	 String countryName = getCountryCodeEnumName(countryCode);
    	 
    	 if (EnumUtils.isValidEnum(Countries.class, countryName) == false) {
    		 HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
             errors.put("countryCode", ValidationError.IS_NOT_VALID);
             LOGGER.error("Country " + countryCode + " is not a valid Country - unable to look up matching addresses");
             throw new ValidationException(errors);
    	 }
    	
    	if (StringUtils.isBlank(searchCriteria)) {
            HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
            errors.put("searchCriteria", ValidationError.IS_EMPTY);
            LOGGER.error("No search Criteria was specified - unable to look up matching addresses");
            throw new ValidationException(errors);
        }

        return zipCodeDao.findZipCodesByCountryCodeAndSearchCriteria(countryCode, searchCriteria, count);
    }
    
    @Override
    public List<ZipCodeEntity> findZipForMultipleCountries(String countryCodes, String searchCriteria, Integer count) throws ValidationException {
        if (StringUtils.isBlank(searchCriteria)) {
            HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
            errors.put("searchCriteria", ValidationError.IS_EMPTY);
            throw new ValidationException(errors);
        }

        return zipCodeDao.findZipCodesByCountryCodesAndSearchCriteria(countryCodes, searchCriteria, count);
    }
   
    public void setZipCodeDao(ZipCodeDao zipCodeDao) {
        this.zipCodeDao = zipCodeDao;
    }

    @Override
    public AddressBO validateAddress(AddressBO bo) {
        AddressBO result = new AddressBO();
        if (bo == null) {
            return result;
        }
        if (bo.getZip() != null) {
            validateZips(bo, result);
        }
        if (bo.getCity() != null) {
            validateCities(bo, result);
        }
        if (bo.getState() != null) {
            validateStates(bo, result);
        }
        if (bo.getCountry() != null) {
            validateCountries(bo, result);
        }
        return result;
    }

    private void validateCountries(AddressBO dto, AddressBO result) {
        List<String> countries = Arrays.asList(dto.getCountry().split(SPLITTER_REGEXP));
        List<String> countriesList = new ArrayList<>(countries.size());
        countries.forEach((item) -> countriesList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        List<String> existingCountries = checkIfCountriesExists(countriesList);
        countriesList.removeAll(existingCountries);
        if (!countriesList.isEmpty()) {
            result.setCountry(StringUtils.join(countriesList, SPLITTER_REGEXP));
        }
    }

    private void validateStates(AddressBO dto, AddressBO result) {
        List<String> states = Arrays.asList(dto.getState().split(SPLITTER_REGEXP));
        List<String> statesList = new ArrayList<>(states.size());
        states.forEach((item) -> statesList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        List<String> existingStates = checkIfStatesExists(statesList);
        statesList.removeAll(existingStates);
        if (!statesList.isEmpty()) {
            result.setState(StringUtils.join(statesList, SPLITTER_REGEXP));
        }
    }

    private void validateCities(AddressBO dto, AddressBO result) {
        List<String> cities = Arrays.asList(dto.getCity().split(SPLITTER_REGEXP));
        List<String> citiesList = new ArrayList<>(cities.size());
        cities.forEach((item) -> citiesList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        List<String> existingCities = checkIfCitiesExists(citiesList);
        citiesList.removeAll(existingCities);
        if (!citiesList.isEmpty()) {
            result.setCity(StringUtils.join(citiesList, SPLITTER_REGEXP));
        }
    }

    private void validateZips(AddressBO dto, AddressBO result) {
        List<String> zips = Arrays.asList(dto.getZip().split(SPLITTER_REGEXP));
        List<String> zipList = new ArrayList<>(zips.size());
        zips.forEach((item) -> zipList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        List<String> existingZips = checkIfZipExists(zipList);
        zipList.removeAll(existingZips);
        if (!zipList.isEmpty()) {
            result.setZip(StringUtils.join(zipList, SPLITTER_REGEXP));
        }
    }

    private List<String> checkIfCountriesExists(List<String> countries) {
        return zipCodeDao.isCountryCodesExist(countries);
    }

    private List<String> checkIfStatesExists(List<String> states) {
        return zipCodeDao.isStateCodesExist(states);
    }

    private List<String> checkIfCitiesExists(List<String> cities) {
        return zipCodeDao.isCitiesExist(cities);
    }

    private List<String> checkIfZipExists(List<String> zips) {
        return zipCodeDao.isZipCodesExist(zips);
    }
}
