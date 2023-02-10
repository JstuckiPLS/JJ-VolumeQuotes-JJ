package com.pls.shipment.dao.impl;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LoadMaterialDao;
import com.pls.shipment.domain.LoadMaterialEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * {@link LoadMaterialDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 * 
 */
@Repository
@Transactional
public class LoadMaterialDaoImpl extends AbstractDaoImpl<LoadMaterialEntity, Long> implements LoadMaterialDao {
}
