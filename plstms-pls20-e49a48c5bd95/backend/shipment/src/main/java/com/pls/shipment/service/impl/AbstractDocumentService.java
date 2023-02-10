package com.pls.shipment.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ManualBolEntity;

/**
 * Base functionality for creating Pdf documents.
 * 
 * @author Artem Arapov
 *
 */
public abstract class AbstractDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDocumentService.class);

    @Autowired
    protected LoadDocumentDao documentDao;

    @Autowired
    protected DocumentService documentService;

    @Autowired
    protected DocFileNamesResolver docFileNamesResolver;

    @Autowired
    private UserInfoDao userInfoDao;

    /**
     * Prepare {@link LoadDocumentEntity}.
     * Should be implemented to populate all necessary fields of {@link LoadDocumentEntity}.
     * 
     * @param shipmentId id of shipment (could be either {@link LoadEntity#getId()} or {@link ManualBolEntity#getId()} ).
     * @param documentType Type of Document.
     * @return prepared {@link LoadDocumentEntity}.
     */
    protected abstract LoadDocumentEntity prepareLoadDocument(Long shipmentId, DocumentTypes documentType);

    /**
     * Create Pdf Document.
     * 
     * <p>
     * <ul>
     * <li>Prepare {@link LoadDocumentEntity} (find existing or create new one).
     * <li>Create Pdf document according to corresponded <code>documentType</code> and <code>writer</code>
     * <li>Save created document into the file system
     * <li>Save {@link LoadDocumentEntity} into the database.
     * </ul>
     * 
     * @param shipmentId id of shipment (could be either {@link LoadEntity#getId()} or {@link ManualBolEntity#getId()} ).
     * @param documentType Type of Document.
     * @param writer document writer that handles pdf document generation.
     * @param parameter pdf document generation arguments.
     * @param <T> type of parameter for document writing
     * @return document id
     * @throws PDFGenerationException will be thrown if document generation fails
     */
    protected <T extends PdfDocumentParameter> Long createPdfDocument(Long shipmentId, DocumentTypes documentType,
            PdfDocumentWriter<T> writer, T parameter) throws PDFGenerationException {
        LoadDocumentEntity document = prepareLoadDocument(shipmentId, documentType);
        document.setStatus(Status.ACTIVE);
        document.setFileType(MimeTypes.PDF);

        createDocumentInFileSystem(document, documentType, writer, parameter);
        LoadDocumentEntity savedDoc = documentService.savePreparedDocument(document);
        if (savedDoc != null) {
            return savedDoc.getId();
        }
        return null;
    }

    /**
     * Returns {@link UserEntity} for current user session.
     * 
     * @return {@link UserEntity}
     */
    protected UserEntity getCurrentUser() {
        return userInfoDao.getUserEntityById(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Returns {@link UserEntity} for current user session.
     * 
     * @param userId user id
     * 
     * @return {@link UserEntity}
     */
    protected UserEntity getUserById(Long userId) {
        return userInfoDao.getUserEntityById(userId);
    }

    /**
     * Returns customer logo by specified <code>customerId</code>.
     * 
     * @param customerId {@link CustomerEntity#getId()}.
     * @return {@link InputStream}
     */
    protected InputStream getCustomerLogoForBol(Long customerId) {
        try {
            LoadDocumentEntity document = documentDao.findCustomerLogoForBOL(customerId);
            if (document != null) {
                return documentService.getDocumentInputStream(document);
            }
        } catch (IOException e) {
            LOG.error("Cannot get Customer Logo", e);
        }
        return null;
    }

    /**
     * Returns customer logo by specified <code>customerId</code>.
     * 
     * @param customerId {@link CustomerEntity#getId()}.
     * @return {@link InputStream}
     */
    protected InputStream getCustomerLogoForShipLabel(Long customerId) {
        try {
            LoadDocumentEntity document = documentDao.findCustomerLogoForShipLabel(customerId);
            if (document != null) {
                return documentService.getDocumentInputStream(document);
            }
        } catch (IOException e) {
            LOG.error("Cannot get Customer Logo", e);
        }
        return null;
    }

    private <T extends PdfDocumentParameter> void createDocumentInFileSystem(LoadDocumentEntity document, DocumentTypes documentType,
            PdfDocumentWriter<T> writer, T parameter) throws PDFGenerationException {
        try {
            FileUtils.forceMkdir(new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath())));
            File docFile = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());

            FileOutputStream docOutputStream = new FileOutputStream(docFile);
            writer.write(parameter, docOutputStream);
        } catch (Exception ex) {
            documentService.deleteTempDocument(document);
            throw new PDFGenerationException(String.format("Cannot generate document for type: %s", documentType), ex);
        }
    }
}
