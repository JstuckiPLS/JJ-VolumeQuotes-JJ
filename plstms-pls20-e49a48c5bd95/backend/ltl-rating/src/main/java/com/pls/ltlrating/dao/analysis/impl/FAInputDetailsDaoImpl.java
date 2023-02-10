package com.pls.ltlrating.dao.analysis.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.analysis.FAInputDetailsDao;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;


/**
 * Implementation of {@link FAInputDetailsDao}.
 *
 * @author Svetlana Kulish
 */
@Repository
@Transactional
public class FAInputDetailsDaoImpl extends AbstractDaoImpl<FAInputDetailsEntity, Long> implements FAInputDetailsDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<FAInputDetailsEntity> getInputDetailsByFAId(Long id) {
        return getCurrentSession().getNamedQuery(FAInputDetailsEntity.Q_GET_INPUT_DETAILS_BY_FA_ID).setParameter("id", id)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

}
