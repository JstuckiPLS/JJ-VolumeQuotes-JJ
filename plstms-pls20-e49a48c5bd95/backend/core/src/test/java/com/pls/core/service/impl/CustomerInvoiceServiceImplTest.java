package com.pls.core.service.impl;

import com.pls.core.dao.InvoiceSettingsDao;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for {@link BillToInvoiceServiceImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerInvoiceServiceImplTest {

    private static final long ORG_ID = 1L;

    private static final long PERSON_ID = 1L;

    private static final long TEST_INVOICE_SETTINGS_ID = 1L;

    private static final Long TEST_BILL_TO_ID = 2L;

    @InjectMocks
    private BillToInvoiceServiceImpl sut;

    @Mock
    private InvoiceSettingsDao invoiceSettingsDao;

    @Before
    public void setUp() {
        SecurityTestUtils.login("ADMIN", PERSON_ID, ORG_ID);
    }

    @Test
    public void testGetRequiredDocument() {
        InvoiceSettingsEntity expected = new InvoiceSettingsEntity();
        Mockito.when(invoiceSettingsDao.find(TEST_INVOICE_SETTINGS_ID)).thenReturn(expected);

        InvoiceSettingsEntity actual = sut.getInvoiceSettings(TEST_INVOICE_SETTINGS_ID);
        Mockito.verify(invoiceSettingsDao).find(Matchers.eq(TEST_INVOICE_SETTINGS_ID));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetInvoiceSettingsForCustomer() {
        InvoiceSettingsEntity expected = new InvoiceSettingsEntity();
        Mockito.when(invoiceSettingsDao.getByBillToId(TEST_BILL_TO_ID)).thenReturn(expected);

        InvoiceSettingsEntity actual = sut.getInvoiceSettingsForBillTo(TEST_BILL_TO_ID);
        Mockito.verify(invoiceSettingsDao).getByBillToId(Matchers.eq(TEST_BILL_TO_ID));

        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testSaveInvoiceSettings() {
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        sut.saveInvoiceSettings(invoiceSettings, TEST_BILL_TO_ID);

        Assert.assertNotNull(invoiceSettings.getBillTo());
        Assert.assertEquals(invoiceSettings.getBillTo().getId(), TEST_BILL_TO_ID);
        Assert.assertNotNull(invoiceSettings.getModification().getCreatedBy());
        Assert.assertNotNull(invoiceSettings.getModification().getModifiedBy());

        Mockito.verify(invoiceSettingsDao).saveOrUpdate(Matchers.eq(invoiceSettings));
    }
}

