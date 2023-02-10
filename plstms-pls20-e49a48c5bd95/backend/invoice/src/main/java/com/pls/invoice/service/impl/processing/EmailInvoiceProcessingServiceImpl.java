package com.pls.invoice.service.impl.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.documentmanagement.util.PdfUtil;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.bo.CustomerInvoiceProcessingBO;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

import freemarker.template.TemplateException;

/**
 * Implementation of {@link EmailInvoiceProcessingService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class EmailInvoiceProcessingServiceImpl implements EmailInvoiceProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailInvoiceProcessingServiceImpl.class);

    @Autowired
    private DocumentService documentService;

    @Autowired
    private LoadDocumentDao documentDao;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    @Autowired
    private ShipmentEmailSender emailSender;

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Value("${pls.client.url}")
    private String clientUrl;

    @Value("${email.from.accountsReceivable.email}")
    private String accountsReceivableEmail;

    @Value("${email.from.accountsReceivable.name}")
    private String accountsReceivableName;

    @Override
    public void sendInvoicesViaEmail(Long invoiceId, String emails, String subject, String comments, BillToEntity billTo, Long personId,
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) throws ApplicationException {
        LOGGER.info("Sending Invoice Email to Customer for invoice ID: {}", invoiceId);
        InvoiceSettingsEntity invoiceSettings = billTo.getInvoiceSettings();
        if (invoiceSettings.isNoInvoiceDocument() || (invoiceSettings.getInvoiceType() == InvoiceType.CBI
                && (CollectionUtils.isEmpty(invoiceSettings.getDocuments()) || invoiceSettings.getCbiInvoiceType() == CbiInvoiceType.FIN))) {
            LOGGER.info("No Invoice Email will be sent to Customer for invoice ID: {}", invoiceId);
            return;
        }

        Collection<Long> loadIds = invoiceDao.getAllLoadsIds(invoiceId, billTo.getId());

        if (!loadIds.isEmpty()) {
            Collection<String> invoiceNumbers = invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId());

            String recipients = StringUtils.isEmpty(emails) ? billTo.getBillingInvoiceNode().getEmail() : emails;
            String emailSubject = StringUtils.isNotBlank(subject) ? subject : getEmailSubject(invoiceSettings.getInvoiceType(), invoiceNumbers);
            String emailContent = getEmailContent(loadIds, comments);
            List<EmailAttachmentDTO> invoiceAttachments = getInvoiceAttachments(invoiceId, billTo, invoiceNumbers, invoiceDocuments);

            emailSender.sendInvoice(recipients, emailSubject, emailContent, invoiceAttachments, invoiceSettings.isNotSplitRecipients(),
                    loadIds, personId);
        }
        LOGGER.info("Finished Sending Invoice Email to Customer for invoice ID: {}", invoiceId);
    }

    @Override
    public void sendInvoicesViaEmail(Long invoiceId, Long personId, CustomerInvoiceProcessingBO bo) throws ApplicationException {
        sendInvoicesViaEmail(invoiceId, bo.getEmail(), bo.getSubject(), bo.getComments(), bo.getBillTo(), personId, bo.getInvoiceDocuments());
    }

    private List<EmailAttachmentDTO> getInvoiceAttachments(Long invoiceId, BillToEntity billTo, Collection<String> invoiceNumbers,
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) {
        LOGGER.info("Preparing Invoice Document for Customer for invoice ID: {}", invoiceId);

        List<EmailAttachmentDTO> invoiceAttachments = new ArrayList<EmailAttachmentDTO>();

        for (Entry<InvoiceDocument, LoadDocumentEntity> invoiceDocument : invoiceDocuments.entrySet()) {
            String fileName = getAttachmentFileName(billTo.getInvoiceSettings().getInvoiceType(), invoiceDocument.getKey(), invoiceNumbers);
            invoiceAttachments.add(new EmailAttachmentDTO(invoiceDocument.getValue().getId(), fileName));
        }

        LOGGER.info("Finished Preparing Invoice Document for Customer for invoice ID: {}", invoiceId);
        return invoiceAttachments;
    }

    private String getEmailContent(Collection<Long> loadIds, String comments) throws ApplicationException {
        LoadDocumentEntity paperwork = generateDocumentsForEmail(loadIds);
        try {
            return generateContentForEmail(paperwork, comments);
        } catch (Exception e) {
            throw new ApplicationException("Exception during rendering email body template for invoice. "
                    + e.getMessage(), e);
        }
    }

    private String generateContentForEmail(LoadDocumentEntity paperwork, String comments) throws IOException, TemplateException {
        String content;
        Map<String, Object> data = new HashMap<String, Object>();

        UserAdditionalContactInfoBO contact = new UserAdditionalContactInfoBO();
        contact.setEmail(accountsReceivableEmail);
        contact.setContactName(accountsReceivableName);
        data.put("contact", contact);

        data.put("documentId", paperwork.getId());
        String link = String.format("%s/restful/customerdocs?id=%s&token=%s", clientUrl, paperwork.getId(), paperwork.getDownloadToken());
        data.put("paperworkLink", link);
        data.put("clientUrl", "clientIndexUrl");
        data.put("comments", prepareComments(comments));
        content = emailTemplateRenderer.renderEmailTemplate("InvoiceTemplate.ftl", data);
        return content;
    }

    private String prepareComments(String comments) {
        String preparedComments = StringUtils.trimToNull(comments);
        if (preparedComments != null) {
            preparedComments = preparedComments.replaceAll("\\n", "<br/>");
        }
        return preparedComments;
    }

    private LoadDocumentEntity generateDocumentsForEmail(Collection<Long> loadIds) throws ApplicationException {
        List<InputStream> paperworks = getPaperworkDocumentsFileContent(loadIds);
        return documentService.concatenateAndSaveDocument(paperworks, DocumentTypes.MISCELLANEOUS);
    }

    private List<InputStream> getPaperworkDocumentsFileContent(Collection<Long> loadsIds) throws ApplicationException {
        List<InputStream> pdfFilesContent = new ArrayList<InputStream>();
        try {
            for (Long loadId : loadsIds) {
                // TODO get docuemtns ids for all loads at a time
                List<BigInteger> documentIds = documentDao.findRequiredAndAvailableDocumentsByLoadId(loadId);
                for (BigInteger documentId : documentIds) {
                    LoadDocumentEntity loadDocumentEntity = documentService.getDocumentWithStream(documentId.longValue());
                    if (PdfUtil.isImage(loadDocumentEntity.getFileType())) {
                        File tempFile = File.createTempFile("paperworkImgPdf", "tmp");
                        String fileName = new File(docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath()),
                                loadDocumentEntity.getDocFileName()).getAbsolutePath();
                        PdfUtil.convertImageToPdf(fileName, new FileOutputStream(tempFile));
                        pdfFilesContent.add(new FileInputStream(tempFile));
                    } else {
                        pdfFilesContent.add(loadDocumentEntity.getStreamContent());
                    }
                }
            }
        } catch (Exception e) {
            String message = String.format("Error getting paperwork documents for loads(%s). ", StringUtils.join(loadsIds, ',')) + e.getMessage();
            throw new ApplicationException(message, e);
        }
        return pdfFilesContent;
    }

    private String getAttachmentFileName(InvoiceType invoiceType, InvoiceDocument invoiceDocument, Collection<String> invoiceNumbers) {
        if (invoiceType == InvoiceType.TRANSACTIONAL) {
            if (invoiceNumbers.size() > 1) {
                return String.format("PLS Invoices, %s.pdf", DateFormatUtils.format(new Date(), "MM/dd/yyyy"));
            } else {
                return String.format("PLS Invoice %s.pdf", invoiceNumbers.iterator().next());
            }
        } else {
            String invoiceNumber = invoiceNumbers.iterator().next();
            int cbiItemIndex = invoiceNumber.indexOf('-', 2);
            invoiceNumber = invoiceNumber.substring(0, cbiItemIndex == -1 ? invoiceNumber.length() : cbiItemIndex);
            if (invoiceDocument == InvoiceDocument.PDF) {
                return invoiceNumber + ".pdf";
            } else {
                return invoiceNumber + ".xlsx";
            }
        }
    }

    @Override
    public String getEmailSubject(InvoiceType invoiceType, Collection<String> invoiceNumbers) {
        if (invoiceType == InvoiceType.TRANSACTIONAL) {
            return "PLS-Invoice-" + StringUtils.join(invoiceNumbers, ",");
        } else {
            String invoiceNumber = invoiceNumbers.iterator().next();
            int cbiItemIndex = invoiceNumber.indexOf('-', 2);
            invoiceNumber = invoiceNumber.substring(0, cbiItemIndex == -1 ? invoiceNumber.length() : cbiItemIndex);
            return "PLS-Invoice-" + invoiceNumber;
        }
    }
}
