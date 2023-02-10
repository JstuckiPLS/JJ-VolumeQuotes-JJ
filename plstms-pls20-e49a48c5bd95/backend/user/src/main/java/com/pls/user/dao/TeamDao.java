package com.pls.user.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.user.domain.TeamEntity;

/**
 * DAO for {@link TeamEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface TeamDao extends AbstractDao<TeamEntity, Long> {
    /**
     * Get list of teams names.
     * 
     * @return teams names
     */
    List<SimpleValue> getNames();
}
