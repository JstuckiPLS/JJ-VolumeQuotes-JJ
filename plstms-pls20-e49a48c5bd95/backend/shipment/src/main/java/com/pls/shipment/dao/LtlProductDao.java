package com.pls.shipment.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.shipment.domain.LtlProductEntity;

/**
 * Data Access Object for {@link LtlProductEntity} data.
 *
 * @author Maxim Medvedev
 */
public interface LtlProductDao extends AbstractDao<LtlProductEntity, Long> {

    /**
     * Mark specified {@link LtlProductEntity} as inactive and save changes to DB.
     *
     * @param productId
     *            Not <code>null</code> ID for {@link LtlProductEntity} that should be archived.
     * @param modifiedBy
     *            User ID that performs this operation.
     * @param modifiedDate
     *            Current date.
     */
    void markAsInactive(Long productId, Long modifiedBy, Date modifiedDate);

    /**
     * Create new or update an existing {@link LtlProductEntity}.
     *
     * @param product
     *            Not <code>null</code> {@link LtlProductEntity} with <code>null</code>
     *            {@link LtlProductEntity#getId()} to create new DB record or with not <code>null</code>
     *            {@link LtlProductEntity#getId()} to update an existing.
     * @return Not <code>null</code> attached {@link LtlProductEntity}.
     */
    LtlProductEntity save(LtlProductEntity product);

    /**
     * Check if {@link LtlProductEntity} with given ID already exist and linked with specified customer.
     *
     * @param customerId
     *            Customer ID.
     * @param productId
     *            Product ID.
     *
     * @return <code>true</code> if specified {@link LtlProductEntity} was found and it is linked with
     *         specified customer.
     */
    boolean checkProductExists(Long customerId, Long productId);

    /**
     * Search for recent products or products with SKU or description. List of products will be sorted accordingly to sort field
     * that customer already specified at settings.
     *
     * @param orgId          id of organization to which products must belong to
     * @param userId         if <code>userId</code> is not specified than returns all customer products for specified filter criteria
     *                       otherwise returns only products created by user or his/her subordinators.
     * @param count          count of recent products that will be returned if <code>filter</code> is not specified.
     *                       Otherwise this value will not be considered
     * @param commodityClass commodity class of products to find
     * @param hazmat         'hazmatOnly' filter flag
     * @return list of products that match to specified criteria
     */
    List<LtlProductEntity> findRecentProducts(Long orgId, Long userId, Integer count, CommodityClass commodityClass, boolean hazmat);

    /**
     * Search for products that match to filter keyword. List of products will be sorted accordingly to sort field
     * that customer already specified at settings.
     *
     * @param orgId          id of organization to which products must belong to
     * @param userId         if <code>userId</code> is not specified than returns all customer products for specified filter criteria
     *                       otherwise returns only products created by user or his/her subordinators.
     * @param filter         value for SKU code or description. Will return all data that match given criterion
     * @param commodityClass commodity class of products to find
     * @param hazmat         'hazmatOnly' filter flag
     * @param count - number of products to be fetched
     * @return list of products that match to specified criteria
     */
    List<LtlProductEntity> findProducts(Long orgId, Long userId, String filter, CommodityClass commodityClass, boolean hazmat, Integer count);

    /**
     * Method returns a filtered and/or sorted list of {@link LtlProductEntity} for provided organization and user.
     * If provided user id is <code>null</code>, the result will contain all filtered and sorted products for given organization.
     *
     * @param orgId organization Id
     * @param userId user Id. Should be <code>null</code> in case all organization's products need to be retrieved
     * @return list of {@link LtlProductEntity}
     */
    List<LtlProductEntity> getProductList(Long orgId, Long userId);

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
     * Finds the products with the matching Name and Description.
     * @param productDesc the description of the product to be searched by.
     * @param productCode the code of the product to be searched by.
     * @return the matching LTL product.
     */
    LtlProductEntity findProductByDescAndName(String productCode, String productDesc);

    /**
     * Finds the products with the matching Name and Description for a specific customer.
     * @param description the description of the product to be searched by.
     * @param hazmat hazmat flag for product search
     * @param commodityClass commodity class of the product we are looking for
     * @param orgId unique identification for the customer
     * @return the matching LTL product.
     */
    LtlProductEntity findProductByInfo(String description, Long orgId, boolean hazmat, CommodityClass commodityClass);

    /**
     * Finds the products with the matching productCode and commodityClass for a specific customer.
     * @param productCode product code of the product
     * @param commodityClass commodity class of the product we are looking for
     * @param orgId unique identification for the customer
     * @return the matching LTL product.
     */
    LtlProductEntity findUniqueProductByInfo(Long orgId, CommodityClass commodityClass, String productCode);
}
