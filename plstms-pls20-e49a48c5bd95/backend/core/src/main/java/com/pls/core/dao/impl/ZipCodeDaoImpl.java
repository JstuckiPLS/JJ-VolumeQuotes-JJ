package com.pls.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.AbstractStreamQuery;
import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.address.ZipCodePK;

/**
 * Implementation of {@link com.pls.core.dao.ZipCodeDao}.
 *
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class ZipCodeDaoImpl extends AbstractStreamQueryImpl<ZipCodeEntity, ZipCodePK> implements ZipCodeDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<ZipCodeEntity> findZipCodesByCountryCodeAndSearchCriteria(String countryCode, String searchCriteria, Integer maxResults)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(searchCriteria)) {
            throw new IllegalArgumentException("Parameter searchCriteria should not be blank");
        }
        return getCurrentSession().getNamedQuery(ZipCodeEntity.Q_SEARCH)
                .setString("countryCode", StringUtils.trimToNull(StringUtils.upperCase(countryCode)))
                .setString("criteria", "%" + searchCriteria.toLowerCase() + "%")
                .setMaxResults(maxResults == null ? 10 : maxResults)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ZipCodeEntity> findZipCodesByCountryCodesAndSearchCriteria(String countryCodes, String searchCriteria, Integer maxResults)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(searchCriteria)) {
            throw new IllegalArgumentException("Parameter searchCriteria should not be blank");
        }
        return getCurrentSession().getNamedQuery(ZipCodeEntity.Q_SEARCH)
                .setString("countryCode", StringUtils.trimToNull(StringUtils.upperCase(countryCodes)))
                .setString("criteria", "%" + searchCriteria.toLowerCase() + "%")
                .setMaxResults(maxResults == null ? 10 : maxResults)
                .list();
    }
    
    @Override
    @SuppressWarnings("unchecked")

    public List<String>  isZipCodesExist(List<String> zipCodes) {
        List<String> parameterList = new ArrayList<>(zipCodes.size());
        zipCodes.forEach((item) -> parameterList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        return (List<String>) getCurrentSession().getNamedQuery(ZipCodeEntity.Q_IS_ZIP_CODE_EXIST)
                .setParameterList("zipCodes", parameterList)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String>  isStateCodesExist(List<String> stateCodes) {
        List<String> parameterList = new ArrayList<>(stateCodes.size());
        stateCodes.forEach((item) -> parameterList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        return (List<String>) getCurrentSession().getNamedQuery(ZipCodeEntity.Q_IS_STATE_CODE_EXIST)
                .setParameterList("stateCodes", parameterList)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String>  isCitiesExist(List<String> cities) {
        List<String> parameterList = new ArrayList<>(cities.size());
        cities.forEach((item) -> parameterList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        return (List<String>) getCurrentSession().getNamedQuery(ZipCodeEntity.Q_IS_CITY_EXIST)
                .setParameterList("cities", parameterList)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String>  isCountryCodesExist(List<String> countryCodes) {
        List<String> parameterList = new ArrayList<>(countryCodes.size());
        countryCodes.forEach((item) -> parameterList.add(StringUtils.trimToEmpty(StringUtils.upperCase(item))));
        return (List<String>) getCurrentSession().getNamedQuery(ZipCodeEntity.Q_IS_COUNTRY_EXIST)
                .setParameterList("countryCodes", parameterList)
                .list();
    }

    @Override
    public Stream<ZipCodeEntity> getZipCodesAsStream() {
        return getResultAsStream(ZipCodeEntity.Q_GET_ZIP_CODES_FOR_CACHE, AbstractStreamQuery.DEFAULT_FETCH_SIZE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ZipCodeEntity> getDefault(String countryCode, String zipCode) {
        return getCurrentSession().getNamedQuery(ZipCodeEntity.Q_GET_DEFAULT_BY_ZIP_AND_COUNTRY).setString("countryCode", countryCode)
                .setString("zip", zipCode).list();
    }
}
