package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.AccessorialTypeEntity;

/**
 * Contains dictionary of all accessorials - LTL, FLatbed, etc.
 * @author Hima Bindu Challa
 *
 */
public interface AccessorialDictionaryService {

    /**
     * Retrieves all accessorial types.
     *
     * @return all accessorials types
     */
    List<AccessorialTypeEntity> getAllAccessorialTypes();
}
