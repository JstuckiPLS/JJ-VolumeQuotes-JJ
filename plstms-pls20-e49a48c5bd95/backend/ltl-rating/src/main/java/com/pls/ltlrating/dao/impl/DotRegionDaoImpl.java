package com.pls.ltlrating.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.DotRegionDao;
import com.pls.ltlrating.domain.DotRegionEntity;

/**
 * Implementation of {@link DotRegionDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class DotRegionDaoImpl extends AbstractDaoImpl<DotRegionEntity, Long> implements DotRegionDao {

}
