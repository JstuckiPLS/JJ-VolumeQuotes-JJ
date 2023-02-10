package com.pls.invoice.service.impl.processing;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.BillToInvoiceProcessingTimeBO;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceProcessingService;

/**
 * Test for {@link AutomaticInvoiceProcessingServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AutomaticInvoiceProcessingServiceImplTest {
    @Mock
    private InvoiceProcessingService invoiceProcessingService;

    @Mock
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Mock
    private FinancialInvoiceDao invoiceDao;

    private final long defaultPerson = (long) (Math.random() * 100);

    @InjectMocks
    private AutomaticInvoiceProcessingServiceImpl sut;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sut, "defaultPersonId", String.valueOf(defaultPerson));
    }

    /**
     * Custom date matcher that validates that provided date is of the same day as specified.
     * 
     * @author Aleksandr Leshchenko
     */
    public static class DateMatcher extends ArgumentMatcher<Date> {
        private Date date;

        /**
         * Constructor.
         *
         */
        public DateMatcher(Date date) {
            this.date = date;
        }

        @Override
        public boolean matches(Object argument) {
            return argument instanceof Date && DateUtils.isSameDay((Date) argument, date);
        }
    };

    /**
     * Matcher validates parameters provided in SendToFinance BO to be correct for Automatic invoices
     * processing.
     * 
     * @author Aleksandr Leshchenko
     */
    private class SendToFinanceMatcher extends ArgumentMatcher<SendToFinanceBO> {
        private final Long billToId;
        private final Date invoiceDate;

        SendToFinanceMatcher(Long billToId, Date invoiceDate) {
            this.billToId = billToId;
            this.invoiceDate = invoiceDate;
        }

        @Override
        public boolean matches(Object argument) {
            SendToFinanceBO bo = (SendToFinanceBO) argument;
            if (bo == null) {
                return false;
            }
            InvoiceProcessingBO invoiceBillToDetails = bo.getInvoiceProcessingDetails().iterator().next();
            return DateUtils.isSameDay(bo.getFilterLoadsDate(), invoiceDate)
                    && DateUtils.isSameDay(bo.getInvoiceDate(), new Date())
                    && ObjectUtils.equals(invoiceBillToDetails.getBillToId(),
                            billToId)
                    && CollectionUtils
                            .isEmpty(invoiceBillToDetails.getLoadIds())
                    && CollectionUtils
                            .isEmpty(invoiceBillToDetails.getAdjustmentIds());
        }
    };

    @Test
    public void shouldProcessInvoicesWeekly() throws Exception {
        List<Long> billToIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        List<BillToInvoiceProcessingTimeBO> billTos = getBillTos(billToIds);
        billTos.get(1).setFilterLoadsDate(DateUtility.addDays(new Date(), -3));
        Mockito.when(invoiceDao.getBillToIdsForAutomaticProcessing(ProcessingPeriod.WEEKLY,
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK), null)).thenReturn(billTos);

        Long invoiceId1 = (long) (Math.random() * 100);
        Mockito.when(finacialInvoiceService.processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())),
                Mockito.eq(false), Mockito.eq(defaultPerson))).thenReturn(invoiceId1);
        Long invoiceId2 = (long) (Math.random() * 100) + 101;
        Mockito.when(finacialInvoiceService.processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(1), billTos.get(1).getFilterLoadsDate())), Mockito.eq(false),
                Mockito.eq(defaultPerson))).thenReturn(invoiceId2);

        sut.processInvoicesWeekly();

        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())),
                Mockito.eq(false), Mockito.eq(defaultPerson));
        Mockito.verify(finacialInvoiceService).processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(1), billTos.get(1).getFilterLoadsDate())), Mockito.eq(false),
                Mockito.eq(defaultPerson));
        Mockito.verify(invoiceProcessingService).processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())),
                Mockito.eq(invoiceId1), Mockito.eq(defaultPerson));
        Mockito.verify(invoiceProcessingService).processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(1), billTos.get(1).getFilterLoadsDate())), Mockito.eq(invoiceId2),
                Mockito.eq(defaultPerson));
        Mockito.verifyNoMoreInteractions(invoiceProcessingService);
    }

    @Test
    public void shouldProcessInvoicesDaily() throws Exception {
        List<Long> billToIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101,
                (long) (Math.random() * 100) + 202, (long) (Math.random() * 100) + 303);
        List<BillToInvoiceProcessingTimeBO> billTos = getBillTos(billToIds);
        billTos.get(1).setProcessingTime(billTos.get(1).getProcessingTime() + 26);
        billTos.get(1).setFilterLoadsDate(DateUtility.addDays(new Date(), -2));
        billTos.get(2).setFilterLoadsDate(DateUtility.addDays(new Date(), -3));
        billTos.get(3).setProcessingTime(billTos.get(3).getProcessingTime() - 26);

        Mockito.when(invoiceDao.getBillToIdsForAutomaticProcessing(Mockito.eq(ProcessingPeriod.DAILY),
                Mockito.eq(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)),
                Mockito.argThat(new ArgumentMatcher<Integer>() {
                    @Override
                    public boolean matches(Object argument) {
                        Integer realTime = (Integer) argument;
                        ZonedDateTime nowEST = ZonedDateTime.now(ZoneId.of("US/Eastern"));
                        int minuteOfDay = nowEST.getHour() * 60 + nowEST.getMinute();
                        return realTime != null && Math.abs(minuteOfDay - realTime) <= 1;
                    }
                }))).thenReturn(billTos);

        Long invoiceId1 = (long) (Math.random() * 100);
        Mockito.when(finacialInvoiceService.processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())),
                Mockito.eq(false), Mockito.eq(defaultPerson))).thenReturn(invoiceId1);

        Long invoiceId2 = (long) (Math.random() * 100);
        Mockito.when(finacialInvoiceService.processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(1), billTos.get(1).getFilterLoadsDate())), Mockito.eq(false),
                Mockito.eq(defaultPerson))).thenReturn(invoiceId2);
        Long invoiceId3 = (long) (Math.random() * 100) + 101;
        Mockito.when(finacialInvoiceService.processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(2), billTos.get(2).getFilterLoadsDate())), Mockito.eq(false),
                Mockito.eq(defaultPerson))).thenReturn(invoiceId3);
        Long invoiceId4 = (long) (Math.random() * 100) + 101;
        Mockito.when(finacialInvoiceService.processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(3), new Date())),
                Mockito.eq(false), Mockito.eq(defaultPerson))).thenReturn(invoiceId4);

        sut.processInvoicesDaily();

        Mockito.verify(finacialInvoiceService).processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())), Mockito.eq(false), Mockito.eq(defaultPerson));
        Mockito.verify(finacialInvoiceService).processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(2), billTos.get(2).getFilterLoadsDate())), Mockito.eq(false),
                Mockito.eq(defaultPerson));
        Mockito.verify(invoiceProcessingService).processInvoices(Mockito.argThat(new SendToFinanceMatcher(billToIds.get(0), new Date())),
                Mockito.eq(invoiceId1), Mockito.eq(defaultPerson));
        Mockito.verify(invoiceProcessingService).processInvoices(
                Mockito.argThat(new SendToFinanceMatcher(billToIds.get(2), billTos.get(2).getFilterLoadsDate())), Mockito.eq(invoiceId3),
                Mockito.eq(defaultPerson));
        Mockito.verifyNoMoreInteractions(invoiceProcessingService);
    }

    private List<BillToInvoiceProcessingTimeBO> getBillTos(List<Long> billToIds) {
        List<BillToInvoiceProcessingTimeBO> billTos = new ArrayList<BillToInvoiceProcessingTimeBO>();
        for (Long billToId : billToIds) {
            BillToInvoiceProcessingTimeBO billTo = new BillToInvoiceProcessingTimeBO();
            billTo.setBillToId(billToId);
            billTo.setTimeZoneName("US/Central");
            ZonedDateTime nowForBillToZone = ZonedDateTime.now(ZoneId.of(billTo.getTimeZoneName()));
            int minuteOfDay = nowForBillToZone.getHour() * 60 + nowForBillToZone.getMinute();
            billTo.setProcessingTime(minuteOfDay);
            billTo.setRebillAdjustmentsCount(0L);
            billTos.add(billTo);
        }
        return billTos;
    }
}
