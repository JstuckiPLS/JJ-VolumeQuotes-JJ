package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CompanyCodeDao;
import com.pls.core.domain.organization.CompanyCodeEntity;

/**
 * Company codes DAO impl.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Transactional
@Repository
public class CompanyCodeDaoImpl extends AbstractDaoImpl<CompanyCodeEntity, String> implements CompanyCodeDao {
}
