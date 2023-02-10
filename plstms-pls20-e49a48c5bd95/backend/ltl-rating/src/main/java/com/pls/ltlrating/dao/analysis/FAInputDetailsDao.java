package com.pls.ltlrating.dao.analysis;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;


/**
 * DAO for {@link FAInputDetailsEntity}.
 *
 * @author Svetlana Kulish
 */
public interface FAInputDetailsDao extends AbstractDao<FAInputDetailsEntity, Long> {

    /**
     * Get by {@link FAInputDetailsEntity#getId()}.
     *
     * @param id
     *            {@link FAInputDetailsEntity#getId()}
     * @return {@link List} of {@link FAInputDetailsEntity}
     */
    List<FAInputDetailsEntity> getInputDetailsByFAId(Long id);
}

