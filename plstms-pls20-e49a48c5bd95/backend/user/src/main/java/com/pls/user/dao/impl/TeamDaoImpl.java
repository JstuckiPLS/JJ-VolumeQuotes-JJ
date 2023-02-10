package com.pls.user.dao.impl;

import java.util.List;

import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.user.dao.TeamDao;
import com.pls.user.domain.TeamEntity;

/**
 * Implementation of {@link TeamDao}.
 * 
 * @author Aleksandr Leshchenko
 */
@Transactional
@Repository
public class TeamDaoImpl extends AbstractDaoImpl<TeamEntity, Long> implements TeamDao {
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleValue> getNames() {
        return getCurrentSession().getNamedQuery(TeamEntity.Q_GET_NAMES).setResultTransformer(Transformers.aliasToBean(SimpleValue.class))
                .list();
    }

}
