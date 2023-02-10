package com.pls.user.address.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.address.fileimport.AddressFields;
import com.pls.core.service.fileimport.BaseImportProcessor;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.DocumentFactory;
import com.pls.core.service.validation.UserAddressBookValidator;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;

/**
 * Performs import address from {@link InputStream} into database table USER_ADDRESS_BOOK.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Service
@Transactional
public class AddressImportProcessor extends BaseImportProcessor<UserAddressBookEntity, AddressFields> {

    @Autowired
    private UserAddressBookDao dao;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Resource(type = UserAddressBookValidator.class)
    private Validator<UserAddressBookEntity> validator;

    private Long customerId;

    /**
     * Constructor.
     */
    public AddressImportProcessor() {
        super(new AddressRecordParser());
    }

    /**
     * Set Customer id. <br/>
     * This value will be passed into {@link UserAddressBookEntity} in time of saving.
     * 
     * @param customerId
     *            id of customer.
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    protected void saveRecord(UserAddressBookEntity record) {
        if (record.getIsDefault() != null && record.getIsDefault()) {
            dao.resetDefaultAddressesForCustomer(record.getOrgId());
        }
        dao.persistWithFlush(record);
    }

    @Override
    protected void validateRecord(UserAddressBookEntity record) throws ValidationException {
        validator.validate(record);
    }

    @Override
    protected void validateHeader(Collection<AddressFields> headerData) throws ImportFileParseException {
        for (AddressFields descriptor : headerData) {
            if (descriptor.isRequired() && !headerData.contains(descriptor)) {
                throw new ImportFileParseException("Column '" + descriptor.getHeader() + "' was not found on '"
                        + getPageName() + "' sheet.");
            }
        }
    }

    @Override
    protected void prepareRecord(UserAddressBookEntity record) {
        record.setOrgId(customerId);
        record.setStatus(Status.ACTIVE);
    }

    @Override
    protected Long saveInvalidDocument(DocumentFactory.FileExtensionType extension, Document document) throws ImportException {
        FileOutputStream outputStream = null;
        LoadDocumentEntity loadDocument = null;
        try {
            LoadDocumentEntity loadDocumentEntity = documentService.prepareTempDocument(null, MimeTypes.getByName(extension.toString()));
            File docFile = new File(docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath()),
                    loadDocumentEntity.getDocFileName());
            outputStream = new FileOutputStream(docFile);
            document.write(outputStream);

            loadDocument = documentService.savePreparedDocument(loadDocumentEntity);

            return loadDocument.getId();
        } catch (DocumentSaveException ex) {
            IOUtils.closeQuietly(outputStream);
            documentService.deleteTempDocument(loadDocument);
            throw new ImportException(ex.getMessage(), ex);
        } catch (Exception e) {
            IOUtils.closeQuietly(outputStream);
            documentService.deleteTempDocument(loadDocument);
            throw new ImportException("Could not save document", e);
        }
    }
}
