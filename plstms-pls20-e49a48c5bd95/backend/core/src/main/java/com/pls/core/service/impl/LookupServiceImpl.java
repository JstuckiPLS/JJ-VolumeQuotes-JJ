package com.pls.core.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.LookupValueDao;
import com.pls.core.dao.LtlLookupValueDao;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.LtlLookupValueEntity;
import com.pls.core.domain.enums.LookupGroup;
import com.pls.core.service.LookupService;

/**
 * Class to get all Lookup values - either LTL Lookup values or flatbed Lookup values.
 * 
 * @author Hima Bindu Challa
 */
@Service
@Transactional(readOnly = true)
public class LookupServiceImpl implements LookupService {

    @Autowired
    private LtlLookupValueDao ltlLookupDao;

    @Autowired
    private LookupValueDao lookupDao;

    /**
     * Get LTL lookup values by group.
     * 
     * @param lookupGroup
     *            Look up Group
     * 
     * @return list of LtlLookupValueEntities.
     */
    public List<LtlLookupValueEntity> getLtlLookupValuesByGroup(String lookupGroup) {
        return ltlLookupDao.findLookupValuesbyGroup(lookupGroup);
    }

    @Override
    public List<LookupValueEntity> getGLNumberComponents() {
        return lookupDao.findLookupValuesByGroup(
                Arrays.asList(LookupGroup.CMP_NUM, LookupGroup.BRN_NUM, LookupGroup.SBR_NUM, LookupGroup.DIV_NUM));
    }

    @Override
    public List<LookupValueEntity> getLookupValuesForPayMethod() {
        return lookupDao.findLookupValuesForPayMethod();
    }

    @Override
    public List<LookupValueEntity> getGLValuesForFreightCharge() {
        return lookupDao.findLookupValuesByGroup(Arrays.asList(LookupGroup.FRT_BILL, LookupGroup.FRT_TYPE));
    }

    @Override
    public List<LookupValueEntity> getBrandNumberComponents() {
        return lookupDao.findLookupValuesByGroup(Arrays.asList(LookupGroup.BRN_NUM_BRAND, LookupGroup.GL_NUM_BRAND));
    }

    @Override
    public List<LookupValueEntity> getAlumaNumberComponents() {
        return lookupDao.findLookupValuesByGroup(Arrays.asList(LookupGroup.BRN_NUM_ALUMA, LookupGroup.GL_NUM_ALUMA));
    }
}
