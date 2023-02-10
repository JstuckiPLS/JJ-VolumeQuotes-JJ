package com.pls.invoice.service.impl.processing;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * Test for {@link EmailInvoiceProcessingServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@RunWith(MockitoJUnitRunner.class)
public class EmailInvoiceProcessingServiceImplTest {

    @Mock
    private DocumentService documentService;

    @Mock
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Mock
    private LoadDocumentDao documentDao;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @Mock
    private EmailTemplateRenderer emailTemplateRenderer;

    @Mock
    private ShipmentEmailSender emailSender;

    @Mock
    private FinancialInvoiceDao invoiceDao;

    private final String clientUrl = "clientUrl" + Math.random();

    @InjectMocks
    private EmailInvoiceProcessingServiceImpl sut;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(sut, "clientUrl", clientUrl);
    }

    @Test
    public void shouldSendTransactionalInvoicesViaEmail() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList("T1-" + Math.random(), "T2-" + Math.random());
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);

        String subject = "PLS-Invoice-" + StringUtils.join(invoiceNumbers, ",");
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        ArgumentCaptor<List> invoiceDocumentsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(emailSender).sendInvoice(Mockito.eq(emails), Mockito.eq(subject), Mockito.eq(emailContent),
                invoiceDocumentsCaptor.capture(), Mockito.eq(invoiceSettings.isNotSplitRecipients()), Mockito.eq(loadIds), Mockito.eq(personId));
        Mockito.verifyNoMoreInteractions(emailSender);

        List<EmailAttachmentDTO> attachments = invoiceDocumentsCaptor.getValue();
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(invoiceDocuments.get(InvoiceDocument.PDF).getId(), attachments.get(0).getImageMetadataId());
        Assert.assertEquals("PLS Invoices, " + DateFormatUtils.format(new Date(), "MM/dd/yyyy") + ".pdf", attachments.get(0).getAttachmentFileName());
    }

    @Test
    public void shouldSendOneTransactionalInvoiceViaEmail() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList("T1-" + Math.random());
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String subject = "PLS-Invoice-" + invoiceNumbers.get(0);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, null, null, comments, billTo, personId, invoiceDocuments);

        ArgumentCaptor<List> invoiceDocumentsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(emailSender).sendInvoice(Mockito.eq(emails), Mockito.eq(subject), Mockito.eq(emailContent),
                invoiceDocumentsCaptor.capture(), Mockito.eq(invoiceSettings.isNotSplitRecipients()), Mockito.eq(loadIds), Mockito.eq(personId));
        Mockito.verifyNoMoreInteractions(emailSender);

        List<EmailAttachmentDTO> attachments = invoiceDocumentsCaptor.getValue();
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(invoiceDocuments.get(InvoiceDocument.PDF).getId(), attachments.get(0).getImageMetadataId());
        Assert.assertEquals("PLS Invoice " + invoiceNumbers.get(0) + ".pdf", attachments.get(0).getAttachmentFileName());
    }

    @Test
    public void shouldSendCBIViaEmail() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        invoiceSettings.setDocuments(Arrays.asList(InvoiceDocument.PDF));
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        invoiceSettings.setCbiInvoiceType(CbiInvoiceType.PLS);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList(StringUtils.left("C-" + Math.random(), 20));
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String subject = "test subject " + Math.random();
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        invoiceDocuments.put(InvoiceDocument.STANDARD_EXCEL, new LoadDocumentEntity((long) (Math.random() * 100 + 101)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, subject, comments, billTo, personId, invoiceDocuments);

        ArgumentCaptor<List> invoiceDocumentsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(emailSender).sendInvoice(Mockito.eq(emails), Mockito.eq(subject), Mockito.eq(emailContent),
                invoiceDocumentsCaptor.capture(), Mockito.eq(invoiceSettings.isNotSplitRecipients()), Mockito.eq(loadIds), Mockito.eq(personId));
        Mockito.verifyNoMoreInteractions(emailSender);

        List<EmailAttachmentDTO> attachments = invoiceDocumentsCaptor.getValue();
        Assert.assertEquals(2, attachments.size());
        Assert.assertEquals(invoiceDocuments.get(InvoiceDocument.PDF).getId(), attachments.get(0).getImageMetadataId());
        Assert.assertEquals(invoiceNumbers.get(0) + ".pdf", attachments.get(0).getAttachmentFileName());
        Assert.assertEquals(invoiceDocuments.get(InvoiceDocument.STANDARD_EXCEL).getId(), attachments.get(1).getImageMetadataId());
        Assert.assertEquals(invoiceNumbers.get(0) + ".xlsx", attachments.get(1).getAttachmentFileName());
    }

    @Test
    public void shouldSendCBIViaEmailWithExcelGroupedByGLNumber() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        invoiceSettings.setDocuments(Arrays.asList(InvoiceDocument.PDF));
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        invoiceSettings.setCbiInvoiceType(CbiInvoiceType.PLS);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList(StringUtils.left("C-" + Math.random(), 20));
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String subject = "PLS-Invoice-" + invoiceNumbers.get(0);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.GROUPED_BY_GL_NUMBER_EXCEL, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, null, null, comments, billTo, personId, invoiceDocuments);

        ArgumentCaptor<List> invoiceDocumentsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(emailSender).sendInvoice(Mockito.eq(emails), Mockito.eq(subject), Mockito.eq(emailContent),
                invoiceDocumentsCaptor.capture(), Mockito.eq(invoiceSettings.isNotSplitRecipients()), Mockito.eq(loadIds), Mockito.eq(personId));
        Mockito.verifyNoMoreInteractions(emailSender);

        List<EmailAttachmentDTO> attachments = invoiceDocumentsCaptor.getValue();
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(invoiceDocuments.get(InvoiceDocument.GROUPED_BY_GL_NUMBER_EXCEL).getId(), attachments.get(0).getImageMetadataId());
        Assert.assertEquals(invoiceNumbers.get(0) + ".xlsx", attachments.get(0).getAttachmentFileName());
    }

    @Test
    public void shouldNotSendInvoicesViaEmailWhenNoLoadsOrAdjustmentsToSend() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<String> invoiceNumbers = Arrays.asList("T1-" + Math.random(), "T2-" + Math.random());
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String comments = "comments" + Math.random();

        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        Mockito.verify(invoiceDao).getAllLoadsIds(invoiceId, billTo.getId());
        Mockito.verifyNoMoreInteractions(invoiceDao);
        Mockito.verifyNoMoreInteractions(emailSender);
        Mockito.verifyNoMoreInteractions(documentService);
        Mockito.verifyNoMoreInteractions(documentDao);
        Mockito.verifyNoMoreInteractions(emailTemplateRenderer);
        Mockito.verifyNoMoreInteractions(documentGeneratorService);
    }

    @Test
    public void shouldNotSendCBIViaEmailWhenNoDocuments() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        invoiceSettings.setCbiInvoiceType(CbiInvoiceType.PLS);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList(StringUtils.left("C-" + Math.random(), 20));
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        invoiceDocuments.put(InvoiceDocument.STANDARD_EXCEL, new LoadDocumentEntity((long) (Math.random() * 100 + 101)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        Mockito.verifyNoMoreInteractions(invoiceDao);
        Mockito.verifyNoMoreInteractions(emailSender);
        Mockito.verifyNoMoreInteractions(documentService);
        Mockito.verifyNoMoreInteractions(documentDao);
        Mockito.verifyNoMoreInteractions(emailTemplateRenderer);
        Mockito.verifyNoMoreInteractions(documentGeneratorService);
    }

    @Test
    public void shouldNotSendCBIViaEmailWhenInvoiceInFinancials() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        invoiceSettings.setDocuments(Arrays.asList(InvoiceDocument.PDF));
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        invoiceSettings.setCbiInvoiceType(CbiInvoiceType.FIN);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList(StringUtils.left("C-" + Math.random(), 20));
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        invoiceDocuments.put(InvoiceDocument.STANDARD_EXCEL, new LoadDocumentEntity((long) (Math.random() * 100 + 101)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        Mockito.verifyNoMoreInteractions(invoiceDao);
        Mockito.verifyNoMoreInteractions(emailSender);
        Mockito.verifyNoMoreInteractions(documentService);
        Mockito.verifyNoMoreInteractions(documentDao);
        Mockito.verifyNoMoreInteractions(emailTemplateRenderer);
        Mockito.verifyNoMoreInteractions(documentGeneratorService);
    }

    @Test(expected = ApplicationException.class)
    public void shouldHandleExceptionWhileGeneratingEmailContent() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList("T1-" + Math.random(), "T2-" + Math.random());
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenReturn(document3);
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenThrow(new IOException("test"));

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        Assert.fail();
    }

    @Test(expected = ApplicationException.class)
    public void shouldHandleExceptionWhileGeneratingPaperwork() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        invoiceSettings.setNotSplitRecipients(Math.random() > 0.5);
        billTo.setInvoiceSettings(invoiceSettings);
        BillingInvoiceNodeEntity invoiceNode = new BillingInvoiceNodeEntity();
        invoiceNode.setEmail(emails + 1);
        billTo.setBillingInvoiceNode(invoiceNode);

        List<Long> loadIds = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100 + 101));
        Mockito.when(invoiceDao.getAllLoadsIds(invoiceId, billTo.getId())).thenReturn(loadIds);

        List<String> invoiceNumbers = Arrays.asList("T1-" + Math.random(), "T2-" + Math.random());
        Mockito.when(invoiceDao.getGroupInvoiceNumbers(invoiceId, billTo.getId())).thenReturn(invoiceNumbers);
        String comments = "comments" + Math.random();

        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(0))).thenReturn(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2)));
        Mockito.when(documentDao.findRequiredAndAvailableDocumentsByLoadId(loadIds.get(1))).thenReturn(
                Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4)));
        final LoadDocumentEntity document1 = getDocument(1L);
        final LoadDocumentEntity document2 = getDocument(2L);
        final LoadDocumentEntity document3 = getDocument(3L);
        final LoadDocumentEntity document4 = getDocument(4L);
        Mockito.when(documentService.getDocumentWithStream(1L)).thenReturn(document1);
        Mockito.when(documentService.getDocumentWithStream(2L)).thenReturn(document2);
        Mockito.when(documentService.getDocumentWithStream(3L)).thenThrow(new DocumentReadException("test"));
        Mockito.when(documentService.getDocumentWithStream(4L)).thenReturn(document4);

        final LoadDocumentEntity paperwork = new LoadDocumentEntity((long) (Math.random() * 100 + 10));
        paperwork.setDownloadToken("downloadToken" + Math.random());
        List<InputStream> streams = Arrays.asList(document1.getStreamContent(), document2.getStreamContent(), document3.getStreamContent(),
                document4.getStreamContent());
        Mockito.when(documentService.concatenateAndSaveDocument(Mockito.eq(streams), Mockito.eq(DocumentTypes.MISCELLANEOUS))).thenReturn(
                paperwork);

        String emailContent = "emailContent" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.eq("InvoiceTemplate.ftl"),
                        Mockito.argThat(new EmailRenderParamsMatcher(paperwork, comments)))).thenReturn(emailContent);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new TreeMap<InvoiceDocument, LoadDocumentEntity>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
        invoiceDocuments.put(InvoiceDocument.PDF, new LoadDocumentEntity((long) (Math.random() * 100)));
        Mockito.when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        sut.sendInvoicesViaEmail(invoiceId, emails, null, comments, billTo, personId, invoiceDocuments);

        Assert.fail();
    }

    private LoadDocumentEntity getDocument(long docId) {
        LoadDocumentEntity document4 = new LoadDocumentEntity(docId);
        document4.setStreamContent(Mockito.mock(InputStream.class));
        return document4;
    }

    private final class EmailRenderParamsMatcher extends ArgumentMatcher<Map<String, Object>> {
        private final LoadDocumentEntity paperwork;
        private String comments;

        private EmailRenderParamsMatcher(LoadDocumentEntity paperwork, String comments) {
            this.paperwork = paperwork;
            this.comments = comments;
        }

        @Override
        public boolean matches(Object argument) {
            Map<String, Object> map = (Map<String, Object>) argument;
            return map.size() == 5 && map.get("documentId") == paperwork.getId() && StringUtils.equals(comments, (String) map.get("comments"));
        }
    }
}
