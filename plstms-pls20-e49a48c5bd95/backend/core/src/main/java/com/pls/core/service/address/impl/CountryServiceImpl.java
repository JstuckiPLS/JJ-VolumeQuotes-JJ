package com.pls.core.service.address.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CountryDao;
import com.pls.core.domain.bo.PageQueryBO;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.address.CountryService;

/**
 * {@link com.pls.core.service.address.CountryService} implementation .
 * 
 * @author Artem Arapov
 *
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryDao countryDAO;

    @Override
    public List<CountryEntity> getAll() {
        return countryDAO.getAll();
    }

    @Override
    public List<CountryEntity> findCountries(String countryName, Integer count) {
        return countryDAO.getCountriesByCodeOrName(countryName, new PageQueryBO(0, count));
    }

}
