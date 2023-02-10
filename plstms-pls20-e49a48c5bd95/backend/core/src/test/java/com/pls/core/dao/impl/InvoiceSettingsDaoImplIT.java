package com.pls.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.InvoiceSettingsDao;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.InvoiceSettingsEntity;

/**
 * Test cases for {@link InvoiceSettingsDaoImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class InvoiceSettingsDaoImplIT extends AbstractDaoTest {

    private static final Long BILL_TO_ID = 1L;

    @Autowired
    @Qualifier("invoiceSettingsDaoImpl")
    private InvoiceSettingsDao sut;

    @Test
    public void testGetByCustomerId() {
        InvoiceSettingsEntity invoiceSettingsEntity = sut.getByBillToId(BILL_TO_ID);
        Assert.assertNotNull(invoiceSettingsEntity);
        Assert.assertEquals(BILL_TO_ID, invoiceSettingsEntity.getBillTo().getId());
    }

    @Test
    public void testInvoiceDocuments() {
        List<InvoiceDocument> expectedDocuments = Arrays.asList(InvoiceDocument.PDF, InvoiceDocument.STANDARD_EXCEL);
        InvoiceSettingsEntity entity = sut.getByBillToId(BILL_TO_ID);
        entity.setDocuments(expectedDocuments);
        sut.saveOrUpdate(entity);
        flushAndClearSession();
        InvoiceSettingsEntity actualEntity = sut.getByBillToId(BILL_TO_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(expectedDocuments.size(), actualEntity.getDocuments().size());
    }

    @Test
    public void testNullInvoiceDocuments() {
        List<InvoiceDocument> expectedDocuments = null;
        InvoiceSettingsEntity entity = sut.getByBillToId(BILL_TO_ID);
        entity.setDocuments(expectedDocuments);
        sut.saveOrUpdate(entity);
        flushAndClearSession();
        InvoiceSettingsEntity actualEntity = sut.getByBillToId(BILL_TO_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertNull(actualEntity.getDocuments());
    }
}
