package com.pls.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.service.AccessorialDictionaryService;

/**
 * Contains dictionary of all accessorials - LTL, FLatbed, etc.
 * @author Hima Bindu Challa
 *
 */
@Service
@Transactional(readOnly = true)
public class AccessorialDictionaryServiceImpl implements AccessorialDictionaryService {

    @Autowired
    private AccessorialTypeDao accessorialTypeDao;

    @Override
    public List<AccessorialTypeEntity> getAllAccessorialTypes() {
        return accessorialTypeDao.getAllApplicableAccessorialTypes();
    }
}
