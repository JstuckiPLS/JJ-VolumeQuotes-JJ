package com.pls.ltlrating.dao.analysis.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.analysis.FAOutputDetailsDao;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;

/**
 * Implementation of {@link FAOutputDetailsDao}.
 *
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FAOutputDetailsDaoImpl extends AbstractDaoImpl<FAOutputDetailsEntity, Long> implements FAOutputDetailsDao {
}
