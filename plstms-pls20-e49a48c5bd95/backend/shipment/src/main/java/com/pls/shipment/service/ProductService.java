package com.pls.shipment.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.validation.ValidationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlProductEntity;

/**
 * Business service that handle business logic for products.
 *
 * @author Gleb Zgonikov
 */
public interface ProductService {

    /**
     * Search for recent products.
     *
     * @param userId - user id
     * @param orgId  - id of actual organization
     * @param filter value for SKU code or description. Will return all data that match given criterion
     * @param commodityClass commodity class of products to find
     * @param hazmat 'hazmatOnly' filter flag
     * @return list of {@link LtlProductEntity}
     */
    List<LtlProductEntity> findRecentProducts(Long orgId, Long userId, String filter, CommodityClass commodityClass, boolean hazmat);

    /**
     * Search for all products.
     *
     * @param orgId id of actual organization
     * @param userId - user id
     * @return list of products
     */
    List<LtlProductEntity> findAllProducts(Long orgId, Long userId);

    /**
     * Search for product with given id.
     *
     *
     * @param productId
     *            id of actual product
     * @return product or null
     */
    LtlProductEntity findById(Long productId);

    /**
     * Search for {@link LoadMaterialEntity} with given id.
     *
     * @param materialId
     *            id of actual load material
     * @return entity or null
     */
    LoadMaterialEntity findLoadMaterialById(Long materialId);

    /**
     * Import Products from Excel or CSV file.
     *
     * @param stream
     *            file data.
     * @param extension
     *            Extension of file which will be imported. <br/>
     *            Not <code>null</code> {@link String} value.
     * @param customerId
     *            id of customer for product binding.
     * @param userId
     *            id of customer for product binding.
     * @return results of import operation.
     * @throws ImportException
     *             when imported file contains invalid data.
     * */
    ImportFileResults importProductsFromFile(InputStream stream, String extension, Long customerId,
                                            Long userId) throws ImportException;

    /**
     * Returns document with data about addresses records which was failed to import during
     * importProductsFromFile() method processing.
     *
     * @param id
     *            id of document.
     * @return temp document as {@link LoadDocumentEntity}.
     * @throws EntityNotFoundException
     *             when document by given id was not found
     * @throws DocumentReadException
     *             when document cannot be received from file system
     */
    LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException;

    /**
     * Remove document with specified id.
     *
     * @param id id of document.
     * @throws EntityNotFoundException if stored document with specified id doesn't exist
     */
    void removeImportFailedDocument(Long id) throws EntityNotFoundException;

    /**
     * Change status of specified {@link LtlProductEntity}. {@link LtlProductEntity#getModification()} data
     * will be changed (only "modify" fields).
     *
     * @param userId - user id
     * @param productId
     *            Not <code>null</code> product ID (value of {@link LtlProductEntity#getId()}).
     *
     * @throws EntityNotFoundException
     *             when invalid customer ID or required product was not found.
     */
    void archiveProduct(Long userId, Long productId) throws EntityNotFoundException;

    /**
     * Create new {@link LtlProductEntity} or update existing one.
     *
     * @param product
     *            Not <code>null</code> {@link LtlProductEntity} that should be saved.
     * @param customerId
     *            id of customer
     * @param userId
     *            id of user who will be set as createdBy
     * @param isShared
     *            permission for specified user to share or not this product with other users.
     *
     * @throws EntityNotFoundException
     *             when invalid customer ID or required product was not found.
     * @throws ValidationException
     *             when product validation checks fail.
     */
    void saveOrUpdateProduct(LtlProductEntity product, Long customerId, Long userId, boolean isShared) throws EntityNotFoundException,
            ValidationException;

    /**
     * Checks whether the product's description and class are unique for given organization and user or not.
     *
     * @param orgId          organization id
     * @param productId      product id or <code>null</code> for nonexistent product
     * @param personId       id of user which owns the product or <code>null</code> for shared product
     * @param description    product description
     * @param commodityClass product commodity class
     * @return <code>true</code> if the product is unique, otherwise <code>false</code>
     */
    boolean isProductUnique(Long orgId, Long productId, Long personId, String description, CommodityClass commodityClass);

    /**
     * Checks to see whether a product with the matching code and description exists.
     *
     * @param productCode the code of the product used for searching.
     * @param productDesc the description of the product used for searching.
     * @return the resulted product.
     */
    LtlProductEntity findProductByCodeAndDescription(String productCode, String productDesc);

    /**
     * Checks to see whether a product with the matching code and description exists for a given customer.
     *
     * @param productDesc the description of the product used for searching.
     * @param orgId id of the customer organization.
     * @param hazmat flag of the product to be searched with
     * @param commodityClass class of the product to be searched with
     * @return the resulted product.
     */
    LtlProductEntity findProductByInfo(String productDesc, Long orgId, boolean hazmat, CommodityClass commodityClass);

    /**
     * Export list of products for specified customer.
     *
     * @param orgId
     *            organization Id
     * @param userId
     *            user Id. Should be <code>null</code> in case all organization's products need to be
     *            retrieved
     * @return FileInputStreamResponseEntity inputStream
     * @throws IOException
     *             i/o Exception
     */
    FileInputStreamResponseEntity export(Long orgId, Long userId) throws IOException;

    /**
     * Finds the products with the matching productCode and commodityClass for a specific customer.
     * @param productCode product code of the product
     * @param commodityClass commodity class of the product we are looking for
     * @param orgId unique identification for the customer
     * @return the matching LTL product.
     */
    LtlProductEntity findUniqueProductByInfo(Long orgId, CommodityClass commodityClass, String productCode);
}
