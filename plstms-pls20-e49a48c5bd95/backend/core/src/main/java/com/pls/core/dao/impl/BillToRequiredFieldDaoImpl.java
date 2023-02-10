package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToRequiredFieldDao;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;

/**
 * {@link com.pls.core.dao.BillToRequiredFieldDao} implementation.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Repository
@Transactional
public class BillToRequiredFieldDaoImpl extends AbstractDaoImpl<BillToRequiredFieldEntity, Long> implements BillToRequiredFieldDao {
}
