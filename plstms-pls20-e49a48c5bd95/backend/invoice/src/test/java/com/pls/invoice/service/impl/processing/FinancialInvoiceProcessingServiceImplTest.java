package com.pls.invoice.service.impl.processing;

import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.dao.InvoiceToAXDao;
import com.pls.invoice.domain.xml.finance.FinanceInfoTable;
import com.pls.invoice.domain.xml.finance.salesorder.SalesOrder;
import com.pls.invoice.domain.xml.finance.vendinvoice.VendInvoiceInfoTable;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Test for {@link FinancialInvoiceProcessingServiceImpl}.
 *
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class FinancialInvoiceProcessingServiceImplTest {

    @Mock
    private FinancialInvoiceDao invoiceDao;

    @Mock
    private ShipmentNoteDao shipmentNoteDao;

    @Mock
    private InvoiceToAXDao invoiceAXDao;

    @Mock
    private BillingAuditReasonsDao billingAuditReasonsDao;

    @Mock
    private BillingAuditService billingAuditService;

    @InjectMocks
    private FinancialInvoiceProcessingServiceImpl sut;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Mock
    private ShipmentEventDao eventDao;

    @Test
    public void shouldSendInvoicesToAX() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);

        SalesOrder salesOrder = new SalesOrder();
        VendInvoiceInfoTable vendor = new VendInvoiceInfoTable();

        List<FinanceInfoTable> financeData = Arrays.asList((FinanceInfoTable) salesOrder, vendor);
        Mockito.when(invoiceAXDao.getDataForFinanceSystemByInvoiceID(invoiceId)).thenReturn(financeData);

        sut.sendInvoicesToAX(invoiceId, personId);

        Mockito.verify(sterlingMessageProducer)
            .publishMessage(new SterlingIntegrationMessageBO((IntegrationMessageBO) financeData.get(0), EDIMessageType.AR));
        Mockito.verify(sterlingMessageProducer)
            .publishMessage(new SterlingIntegrationMessageBO((IntegrationMessageBO) financeData.get(1), EDIMessageType.AP));
    }

    @Test(expected = ApplicationException.class)
    public void shouldHandleExceptionWhenSendInvoicesToAX() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);

        SalesOrder salesOrder = new SalesOrder();
        VendInvoiceInfoTable vendor = new VendInvoiceInfoTable();

        List<FinanceInfoTable> financeData = Arrays.asList((FinanceInfoTable) salesOrder, vendor);
        Mockito.when(invoiceAXDao.getDataForFinanceSystemByInvoiceID(invoiceId)).thenReturn(financeData);

        Mockito.doThrow(new JMSException("test")).when(sterlingMessageProducer)
                .publishMessage(new SterlingIntegrationMessageBO((IntegrationMessageBO) financeData.get(0), EDIMessageType.AR));

        sut.sendInvoicesToAX(invoiceId, personId);

        Assert.fail();
    }
}
