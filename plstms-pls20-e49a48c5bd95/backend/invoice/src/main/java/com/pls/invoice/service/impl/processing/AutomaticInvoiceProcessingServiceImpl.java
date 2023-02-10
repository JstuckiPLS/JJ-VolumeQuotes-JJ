package com.pls.invoice.service.impl.processing;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.exception.ApplicationException;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.BillToInvoiceProcessingTimeBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.processing.AutomaticInvoiceProcessingService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceProcessingService;

/**
 * Implementation of {@link AutomaticInvoiceProcessingService}.
 *
 * @author Sergey Kirichenko
 */
@Service
@Transactional
public class AutomaticInvoiceProcessingServiceImpl implements AutomaticInvoiceProcessingService {
    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    @Autowired
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Value("${backgroundJob.personId}")
    private String defaultPersonId;

    @Override
    public void processInvoicesWeekly() throws ApplicationException {
        List<BillToInvoiceProcessingTimeBO> billTos = invoiceDao.getBillToIdsForAutomaticProcessing(ProcessingPeriod.WEEKLY,
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK), null);
        processAutomaticInvoicesByBillTo(billTos);
    }

    @Override
    public void processInvoicesDaily() throws ApplicationException {
        ZonedDateTime nowEST = ZonedDateTime.now(ZoneId.of("US/Eastern"));
        List<BillToInvoiceProcessingTimeBO> billTos = invoiceDao.getBillToIdsForAutomaticProcessing(ProcessingPeriod.DAILY,
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK), getMinuteOfDay(nowEST));

        List<BillToInvoiceProcessingTimeBO> matchingBillTos = new ArrayList<BillToInvoiceProcessingTimeBO>();
        for (BillToInvoiceProcessingTimeBO billTo : billTos) {
            ZonedDateTime billToTime = ZonedDateTime.now(ZoneId.of(billTo.getTimeZoneName()));
            if (Math.abs(getMinuteOfDay(billToTime) - billTo.getProcessingTime()) < 25) {
                matchingBillTos.add(billTo);
            }
        }

        processAutomaticInvoicesByBillTo(matchingBillTos);
    }

    private int getMinuteOfDay(ZonedDateTime date) {
        return date.getHour() * 60 + date.getMinute();
    }

    private void processAutomaticInvoicesByBillTo(List<BillToInvoiceProcessingTimeBO> billTos) throws ApplicationException {
        Long userId = Long.valueOf(defaultPersonId);
        for (BillToInvoiceProcessingTimeBO billTo : billTos) {
            SendToFinanceBO bo = getSendToFinanceBO(billTo);
            Long invoiceId = finacialInvoiceService.processInvoices(bo, false, userId);
            invoiceProcessingService.processInvoices(bo, invoiceId, userId);
        }
    }

    private SendToFinanceBO getSendToFinanceBO(BillToInvoiceProcessingTimeBO billTo) {
        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billTo.getBillToId());
        bo.setInvoiceProcessingDetails(new ArrayList<InvoiceProcessingBO>());
        bo.getInvoiceProcessingDetails().add(invoiceBillToDetails);
        if (billTo.getRebillAdjustmentsCount() > 0) {
            List<InvoiceBO> adjustments = invoiceDao.getRebillAdjustmentsForAutomaticProcessing(billTo.getBillToId());
            adjustments.stream().collect(Collectors.groupingBy(InvoiceBO::getBillToId))
                    .forEach((billToId, adjustmentsForBillTo) -> addInvoiceProcessingDetails(bo, billToId, adjustmentsForBillTo));
        }
        bo.setFilterLoadsDate(ObjectUtils.defaultIfNull(billTo.getFilterLoadsDate(), new Date()));
        bo.setInvoiceDate(new Date()); // invoice date should always be current date per Nick's request
        return bo;
    }

    private void addInvoiceProcessingDetails(SendToFinanceBO bo, Long billToId, List<InvoiceBO> adjustments) {
        if (!adjustments.isEmpty()) {
            InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
            invoiceBillToDetails.setBillToId(billToId);
            invoiceBillToDetails.setAdjustmentIds(adjustments.stream().map(InvoiceBO::getAdjustmentId).collect(Collectors.toList()));
            bo.getInvoiceProcessingDetails().add(invoiceBillToDetails);
        }
    }
}
