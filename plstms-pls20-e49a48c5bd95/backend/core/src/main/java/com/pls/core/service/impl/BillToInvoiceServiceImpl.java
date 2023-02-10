package com.pls.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.dao.InvoiceSettingsDao;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.service.BillToInvoiceService;

/**
 * Implementation of {@link com.pls.core.service.BillToInvoiceService}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
public class BillToInvoiceServiceImpl implements BillToInvoiceService {
    @Autowired
    private InvoiceSettingsDao invoiceSettingsDao;

    @Override
    public void saveInvoiceSettings(InvoiceSettingsEntity invoiceSettings, Long billToId) {
        BillToEntity billTo = new BillToEntity();
        billTo.setId(billToId);

        invoiceSettings.setBillTo(billTo);
        invoiceSettingsDao.saveOrUpdate(invoiceSettings);
    }

    @Override
    public InvoiceSettingsEntity getInvoiceSettings(Long id) {
        return invoiceSettingsDao.find(id);
    }

    @Override
    public InvoiceSettingsEntity getInvoiceSettingsForBillTo(Long billToId) {
        return invoiceSettingsDao.getByBillToId(billToId);
    }
}
