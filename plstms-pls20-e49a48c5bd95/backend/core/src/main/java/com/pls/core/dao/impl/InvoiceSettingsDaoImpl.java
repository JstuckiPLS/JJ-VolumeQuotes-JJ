package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.InvoiceSettingsDao;
import com.pls.core.domain.organization.InvoiceSettingsEntity;

/**
 * Implementation of {@link InvoiceSettingsDao}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Transactional
@Repository
public class InvoiceSettingsDaoImpl extends AbstractDaoImpl<InvoiceSettingsEntity, Long> implements InvoiceSettingsDao {
    @Override
    public InvoiceSettingsEntity getByBillToId(long billToId) {
        return (InvoiceSettingsEntity) getCurrentSession()
                .createQuery("from InvoiceSettingsEntity where billTo.id=:billTo").setParameter("billTo", billToId).uniqueResult();
    }
}
