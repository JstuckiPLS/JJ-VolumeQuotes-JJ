package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToThresholdSettingsDAO;
import com.pls.core.dao.InvoiceSettingsDao;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;

/**
 * Implementation of {@link InvoiceSettingsDao}.
 *
 * @author Brichak Aleksandr
 */
@Transactional
@Repository
public class BillToThresholdSettingsDAOImpl extends AbstractDaoImpl<BillToThresholdSettingsEntity, Long>
        implements BillToThresholdSettingsDAO {
}
