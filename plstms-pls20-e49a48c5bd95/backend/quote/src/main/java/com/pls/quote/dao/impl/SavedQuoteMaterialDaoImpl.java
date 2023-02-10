package com.pls.quote.dao.impl;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.quote.dao.SavedQuoteMaterialDao;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link SavedQuoteMaterialDao} implementation.
 *
 * @author Mikhail Boldinov, 20/03/13
 */
@Repository
@Transactional
public class SavedQuoteMaterialDaoImpl extends AbstractDaoImpl<SavedQuoteMaterialEntity, Long> implements SavedQuoteMaterialDao {
}
