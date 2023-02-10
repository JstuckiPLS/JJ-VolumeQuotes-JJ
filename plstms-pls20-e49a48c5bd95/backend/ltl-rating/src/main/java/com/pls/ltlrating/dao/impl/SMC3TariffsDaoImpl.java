package com.pls.ltlrating.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.DictionaryDaoImpl;
import com.pls.ltlrating.dao.SMC3TariffsDao;
import com.pls.ltlrating.domain.SMC3TariffsEntity;

/**
 * {@link SMC3TariffsDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Transactional
@Repository
public class SMC3TariffsDaoImpl extends DictionaryDaoImpl<SMC3TariffsEntity> implements SMC3TariffsDao {
}
