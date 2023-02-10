package com.pls.shipment.service.impl.email;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.util.TestUtils;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

import freemarker.template.TemplateException;

/**
 * IT test for sending EDI emails.
 *
 * @author Sergii Belodon
 */

@RunWith(MockitoJUnitRunner.class)
public class EDIEmailSenderTest {

    private static final String CLIENT_URL = "http://www.example.com";
    private static final String RECIPIENTS = "test@example.com";
    private static final String LTL_RECIPIENTS = "ltl_test@example.com";

    @Before
    public void init() {
        TestUtils.instantiateField("recipients", mailSender, RECIPIENTS);
        TestUtils.instantiateField("ltlRecipients", mailSender, LTL_RECIPIENTS);
        TestUtils.instantiateField("clientUrl", mailSender, CLIENT_URL);
    }

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @Mock
    private CarrierDao carrierDao;

    @Mock
    private EmailTemplateRenderer emailTemplateRenderer;

    @Mock
    private EmailMessageProducer emailMessageProducer;

    @InjectMocks
    private EDIEmailSender mailSender;

    @SuppressWarnings("unchecked")
    @Test
    public void loadTenderFailedEmailTest() throws IOException, TemplateException {
        LoadEntity load = createLoad();

        Mockito.when(ltlShipmentDao.find(Mockito.eq(1L))).thenReturn(load);
        Mockito.when(ltlShipmentDao.find(Mockito.eq(123454L))).thenReturn(null);

        CarrierEntity carrier = createCarrier();
        Mockito.when(carrierDao.find(Mockito.anyLong())).thenReturn(carrier);
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.anyString(), Mockito.anyMap())).thenCallRealMethod();

        // existing load
        mailSender.loadTenderFailed(1L, Collections.singletonList(1L));
        String emailContent = "Load Tender data has not been sent to carrier <b>\"Carr Name\"</b> (SCAC: <b>\"scac\"</b>).";
        Mockito.verify(emailMessageProducer).sendEmail(Mockito.eq(Collections.singletonList(RECIPIENTS)),
                Mockito.eq("Unable to send EDI 204 to carrier."), Mockito.contains(emailContent), Mockito.eq(EmailType.EDI),
                Mockito.isNull(NotificationTypeEnum.class), Mockito.isNull(Long.class), Mockito.eq(Collections.singletonList(1L)));

        // absent load
        mailSender.loadTenderFailed(1L, Collections.singletonList(123454L));
        Mockito.verifyNoMoreInteractions(emailMessageProducer);
    }

    @Test
    public void loadTrackingFailedEmailTest() throws IOException, TemplateException {
        mailSender.loadTrackingFailed("file.edi", "error", "204", Collections.singletonList(1L));
        Mockito.verify(emailMessageProducer).sendEmail(Mockito.eq(Collections.singletonList(RECIPIENTS)),
                Mockito.eq("Unable to process EDI 214."), Mockito.isNull(String.class), Mockito.eq(EmailType.EDI),
                Mockito.isNull(NotificationTypeEnum.class), Mockito.isNull(Long.class), Mockito.eq(Collections.singletonList(1L)));
    }

    @Test
    public void vendorBillFailedEmailTest() throws IOException, TemplateException {
        mailSender.vendorBillFailed("file.edi");
        Mockito.verify(emailMessageProducer).sendEmail(Mockito.eq(Collections.singletonList(RECIPIENTS)),
                Mockito.eq("Unable to process EDI 214."), Mockito.isNull(String.class), Mockito.eq(EmailType.EDI),
                Mockito.isNull(NotificationTypeEnum.class), Mockito.isNull(Long.class), Mockito.eq(Collections.<Long>emptyList()));
    }

    @Test
    public void forLTLDistributionListEmailTest() throws IOException, TemplateException {
        mailSender.forLTLDistributionList(createLoad(), "status");
        Mockito.verify(emailMessageProducer).sendEmail(Mockito.eq(Collections.singletonList(LTL_RECIPIENTS)),
                Mockito.eq("Notification of Carrier Response to EDI 204, PLS PRO BOL null, status "), Mockito.isNull(String.class),
                Mockito.eq(EmailType.EDI), Mockito.isNull(NotificationTypeEnum.class), Mockito.isNull(Long.class),
                Mockito.eq(Collections.singletonList(1L)));
    }

    private CarrierEntity createCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("Carr Name");
        carrier.setScac("scac");
        return carrier;
    }

    private LoadEntity createLoad() {
        LoadEntity load = new LoadEntity();
        load.addLoadDetails(new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN));
        load.addLoadDetails(new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION));
        load.getOrigin().setAddress(new AddressEntity());
        load.getOrigin().setContactName("cn");
        load.getOrigin().getAddress().setAddress1("address1");
        load.getOrigin().getAddress().setCity("city");
        load.getOrigin().getAddress().setStateCode("ST");
        load.getOrigin().setEarlyScheduledArrival(Calendar.getInstance().getTime());
        load.getDestination().setAddress(new AddressEntity());
        load.getDestination().setContactName("cn");
        load.getDestination().getAddress().setAddress1("address1");
        load.getDestination().getAddress().setCity("city");
        load.getDestination().getAddress().setStateCode("ST");
        load.getDestination().setScheduledArrival(Calendar.getInstance().getTime());
        load.getNumbers().setRefNumber("ref");
        load.setId(1L);
        return load;
    }

}
