package com.pls.shipment.service.product.impl.fileimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.BaseImportProcessor;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.DocumentFactory;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.service.product.impl.validation.ProductValidator;

/**
 * Performs import products from {@link InputStream} into database table LTL_PRODUCT.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional
public class ProductImportProcessor extends BaseImportProcessor<LtlProductEntity, ProductFieldsDescription> {

    @Autowired
    private LtlProductDao dao;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Resource(type = ProductValidator.class)
    private Validator<LtlProductEntity> validator;

    private Long customerId;

    private Long userId;


    /**
     * Constructor.
     */
    public ProductImportProcessor() {
        super(new ProductRecordParser());
    }

    /**
     * Set Customer id. <br/>
     * This value will be passed into {@link LtlProductEntity} in time of saving.
     *
     * @param customerId
     *            id of customer.
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductDao(LtlProductDao productDao) {
        this.dao = productDao;
    }

    public void setValidator(Validator<LtlProductEntity> productValidator) {
        this.validator = productValidator;
    }

    @Override
    protected void saveRecord(LtlProductEntity record) {
        dao.save(record);
    }

    @Override
    protected void validateRecord(LtlProductEntity record) throws ValidationException {
        checkProductUniqueness(record);
        validator.validate(record);
    }

    @Override
    protected void validateHeader(Collection<ProductFieldsDescription> headerData) throws ImportFileParseException {
        for (ProductFieldsDescription descriptor : headerData) {
            if (descriptor.isRequired() && !headerData.contains(descriptor)) {
                throw new ImportFileParseException("Column '" + descriptor.getHeader() + "' was not found on '" + getPageName() + "' sheet.");
            }
        }
    }

    @Override
    protected void prepareRecord(LtlProductEntity record) {
        record.setCustomerId(customerId);
        /* Products belongs to customer users. Unfortunately this is the only way to do it now*/
        record.getModification().setCreatedBy(userId);
        if (!record.isShared()) {
            record.setPersonId(userId);
        }
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

    private void checkProductUniqueness(LtlProductEntity product) throws ValidationException {
        if (!dao.isProductUnique(customerId, product.getId(), product.isShared() ? null : userId, product.getDescription(),
                product.getCommodityClass())) {
            HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
            errors.put("productDescription, productClass", ValidationError.UNIQUE);
            throw new ValidationException(errors);
        }
    }

}
