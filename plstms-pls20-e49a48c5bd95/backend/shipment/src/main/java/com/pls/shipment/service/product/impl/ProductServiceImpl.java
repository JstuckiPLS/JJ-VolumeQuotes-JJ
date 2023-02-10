package com.pls.shipment.service.product.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.shipment.dao.LoadMaterialDao;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.service.ProductService;
import com.pls.shipment.service.product.impl.fileimport.ProductImportProcessor;
import com.pls.shipment.service.product.impl.validation.ProductValidator;
import com.pls.shipment.service.xls.ProductsReportExcelBuilder;

/**
 * Product service.
 *
 * @author Gleb Zgonikov
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final int DEFAULT_RECENT_PRODUCTS_COUNT = 15;

    @Autowired
    private LtlProductDao dao;

    @Autowired
    private LoadMaterialDao loadMaterialDao;

    @Resource(type = ProductValidator.class)
    private Validator<LtlProductEntity> validator;

    @Autowired
    ProductImportProcessor importProcessor;

    @Autowired
    private DocumentService documentService;

    @Value("/templates/Product_Export_Template.xlsx")
    private ClassPathResource reportResource;

    @Override
    public List<LtlProductEntity> findRecentProducts(Long orgId, Long userId, String filter, CommodityClass commodityClass, boolean hazmat) {
        if (StringUtils.isEmpty(filter) && commodityClass == null) {
            return dao.findRecentProducts(orgId, userId, DEFAULT_RECENT_PRODUCTS_COUNT, commodityClass, hazmat);
        } else {
            return dao.findProducts(orgId, userId, filter, commodityClass, hazmat, DEFAULT_RECENT_PRODUCTS_COUNT);
        }
    }

    @Override
    public List<LtlProductEntity> findAllProducts(Long orgId, Long userId) {
        return dao.getProductList(orgId, userId);
    }

    @Override
    public LtlProductEntity findById(Long productId) {
        return dao.find(productId);
    }

    @Override
    public LoadMaterialEntity findLoadMaterialById(Long materialId) {
        return loadMaterialDao.find(materialId);
    }

    @Override
    public void archiveProduct(Long userId, Long productId) throws EntityNotFoundException {
        dao.markAsInactive(productId, userId, new Date());
    }

    @Override
    public void saveOrUpdateProduct(LtlProductEntity product, Long customerId, Long userId, boolean isShared) throws EntityNotFoundException,
            ValidationException {
        if (product.getCustomerId() == null) {
            product.setCustomerId(customerId);
        }

        if (product.getId() == null) {
            product.getModification().setCreatedBy(userId);
        }

        product.setPersonId(null);
        if (!isShared) {
            product.setPersonId(userId);
        }

        validator.validate(product);

        dao.saveOrUpdate(product);
    }

    @Override
    public ImportFileResults importProductsFromFile(InputStream stream, String extension, Long customerId, Long userId)
            throws ImportException {
        FileExtensionType extType = FileExtensionType.getByValue(extension);
        if (extType == null) {
            throw new IllegalArgumentException("Unsupported extension of file. Please, check your file");
        }

        importProcessor.setCustomerId(customerId);
        importProcessor.setUserId(userId);
        return importProcessor.processImport(stream, extType);
    }

    @Override
    public LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException {
        return documentService.getDocumentWithStream(id);
    }

    @Override
    public void removeImportFailedDocument(Long id) throws EntityNotFoundException {
        documentService.deleteTempDocument(id);
    }

    @Override
    public boolean isProductUnique(Long orgId, Long productId, Long personId, String description, CommodityClass commodityClass) {
        return dao.isProductUnique(orgId, productId, personId, description, commodityClass);
    }

    @Override
    public LtlProductEntity findProductByCodeAndDescription(String productCode, String productDesc) {
        return dao.findProductByDescAndName(productCode, productDesc);
    }

    @Override
    public LtlProductEntity findProductByInfo(String productDesc, Long orgId, boolean hazmat, CommodityClass commodityClass) {
        return dao.findProductByInfo(productDesc, orgId, hazmat, commodityClass);
    }

    @Override
    public LtlProductEntity findUniqueProductByInfo(Long orgId, CommodityClass commodityClass, String productCode) {
        return dao.findUniqueProductByInfo(orgId, commodityClass, productCode);
    }

    @Override
    public FileInputStreamResponseEntity export(Long orgId, Long userId) throws IOException {
        return new ProductsReportExcelBuilder(reportResource).generateReport(dao.getProductList(orgId, userId));
    }
}
