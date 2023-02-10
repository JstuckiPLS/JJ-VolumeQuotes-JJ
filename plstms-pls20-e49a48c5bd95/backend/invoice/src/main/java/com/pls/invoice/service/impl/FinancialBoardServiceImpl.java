package com.pls.invoice.service.impl;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.invoice.dao.FinancialBoardDao;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.FinancialBoardService;
import com.pls.invoice.service.InvoiceErrorService;
import com.pls.invoice.service.impl.jms.InvoiceJMSMessage;
import com.pls.invoice.service.jms.InvoiceMessageProducer;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.xsl.CBIProcessedLoadsExcelReportBuilder;
import com.pls.invoice.service.xsl.TransactionalProcessedLoadsExcelReportBuilder;

/**
 * {@link FinancialBoardService} implementation.
 *
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class FinancialBoardServiceImpl implements FinancialBoardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinancialBoardServiceImpl.class);

    @Autowired
    private FinancialBoardDao finBoardDao;

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Autowired
    private InvoiceErrorService invoiceErrorService;

    @Autowired
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Autowired
    private InvoiceMessageProducer invoiceMessageProducer;

    @Value("/templates/Export_Processed_Loads_Summary_for_Transactional.xlsx")
    private ClassPathResource transactionalProcessedReport;

    @Value("/templates/Export_Processed_Loads_Summary_for_CBI.xlsx")
    private ClassPathResource cbiProcessedReport;

    @Override
    public List<InvoiceBO> getTransactionalInvoices(Long personId) {
        return finBoardDao.getTransactionalInvoices(personId);
    }

    @Override
    public FileInputStreamResponseEntity getTransactionalProcessedReport(ProcessedLoadsReportBO bo) throws IOException {
        TransactionalProcessedLoadsExcelReportBuilder transactionalBuilder = new TransactionalProcessedLoadsExcelReportBuilder(
                transactionalProcessedReport);
        return transactionalBuilder.generateReport(bo);
    }

    @Override
    public FileInputStreamResponseEntity getCBIProcessedReport(ProcessedLoadsReportBO bo) throws IOException {
        CBIProcessedLoadsExcelReportBuilder cbiBuilder = new CBIProcessedLoadsExcelReportBuilder(cbiProcessedReport);
        return cbiBuilder.generateReport(bo);
    }

    @Override
    public List<ConsolidatedInvoiceBO> getConsolidatedInvoiceData(Long userId) {
        return finBoardDao.getConsolidatedInvoiceData(userId);
    }

    @Override
    public List<InvoiceBO> getConsolidatedLoads(Long userId, Long billToId) {
        return finBoardDao.getConsolidatedLoads(userId, billToId);
    }

    @Override
    public List<InvoiceBO> getConsolidatedLoads(Long userId, List<Long> billToId) {
        return finBoardDao.getConsolidatedLoads(userId, billToId);
    }

    @Override
    public List<InvoiceBO> getRebillAdjustments(Long userId, List<Long> ids) {
        return finBoardDao.getRebillAdjustments(userId, ids);
    }

    @Override
    public List<InvoiceResultBO> processInvoices(SendToFinanceBO bo, Long userId) throws ApplicationException {
        Long invoiceId = finacialInvoiceService.processInvoices(bo, true, userId);
        List<InvoiceResultBO> processedInvoices = invoiceDao.getInvoiceResults(invoiceId);
        if (processedInvoices.stream().noneMatch(invoiceResultBO -> invoiceResultBO.getErrorMessage() != null
                || invoiceResultBO.getFinalizationStatus() != ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE)) {
            try {
                InvoiceJMSMessage message = new InvoiceJMSMessage();
                message.setFinanceBO(bo);
                message.setInvoiceId(invoiceId);
                message.setUserId(userId);
                invoiceMessageProducer.sendMessage(message);
            } catch (JMSException e) {
                String msg = "Error during sending invoices to AX. " + e.getMessage();
                invoiceErrorService.saveInvoiceError(invoiceId, msg, e, false, false, false, false, userId);
                LOGGER.error(msg, e);
            }
        }
        return processedInvoices;
    }

}
