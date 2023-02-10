package com.pls.user.address.service.impl;

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.validation.UserAddressBookValidator;
import com.pls.core.service.validation.support.Validator;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.user.address.service.AddressImportService;

/**
 * Implementation of {@link AddressImportService}.
 * 
 * @author Denis Zhupinsky (Team International)
 */
@Service
public class AddressImportServiceImpl implements AddressImportService, InitializingBean {

    @Autowired
    private DocumentService documentService;

    @Autowired
    AddressImportProcessor importProcessor;

    @Resource(type = UserAddressBookValidator.class)
    private Validator<UserAddressBookEntity> userAddressBookImportValidator;

    @Override
    public void afterPropertiesSet() throws Exception {
        userAddressBookImportValidator.setImportValidator(true);
    }

    @Override
    public ImportFileResults importAddresses(Long customerId, Long personId, InputStream inputStream, String extension)
            throws ImportException {
        FileExtensionType extType = FileExtensionType.getByValue(extension);
        if (extType == null) {
            throw new IllegalArgumentException("Unsupported extension of file. Please, check your file");
        }

        importProcessor.setCustomerId(customerId);
        return importProcessor.processImport(inputStream, extType);
    };

    @Override
    public LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException {
        return documentService.getDocumentWithStream(id);
    }

    @Override
    public void removeImportFailedDocument(Long id) throws EntityNotFoundException {
        documentService.deleteTempDocument(id);
    }
}
