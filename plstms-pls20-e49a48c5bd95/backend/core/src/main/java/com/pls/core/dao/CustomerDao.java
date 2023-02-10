package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.CustomerEntity;

/**
 * DAO for customers.
 * 
 * @author Denis Zhupinsky
 */
public interface CustomerDao extends AbstractDao<CustomerEntity, Long> {
    /**
     * Find customer by name.
     * 
     * @param name
     *            customer name to find.
     * @return existing customer with specified name.
     */
    CustomerEntity findCustomerByName(String name);

    /**
     * Check if customer with given name already exist.
     * 
     * @param name
     *            name to check
     * @return true if customer already exist
     */
    boolean checkCustomerNameExists(String name);

    /**
     * Check if another customer with {@link com.pls.core.domain.organization.OrganizationEntity#getId()}
     * <code>!=</code> <b>customerId</b> and given name already exists.
     * 
     * @param customerId
     *            id of current customer
     * @param networkId
     *            id of customer network
     * @param name
     *            name to check
     * @return true if customer already exist
     */
    boolean checkCustomerNameExists(Long customerId, Long networkId, String name);

    /**
     * Find customers with given federal tax id.
     * 
     * @param federalTaxId
     *            customer federal tax id to find.
     * @return list of customers with specified federal tax id.
     */
    List<CustomerEntity> findCustomersByFederalTaxId(String federalTaxId);

    /**
     * Check if customers with given federal tax id already exist.
     * 
     * @param federalTaxId
     *            customer federal tax id.
     * @return true if customers with specified federal tax id already exist
     */
    boolean checkCustomerFederalTaxIdExists(String federalTaxId);

    /**
     * Check if {@link CustomerEntity} with given ID already exist.
     * 
     * @param customerId
     *            Customer ID.
     * @return <code>true</code> if customer with specified ID exists. Otherwise returns <code>false</code>.
     */
    boolean checkCustomerExists(Long customerId);

    /**
     * Find customer with specified ediNumber.
     * 
     * @param ediNumber
     *            EDI Number to find
     * @return existing customer with specified EDI Number
     */
    CustomerEntity findCustomerByEDINumber(String ediNumber);

    /**
     * Check if {@link CustomerEntity} with given edi number already exists.
     * 
     * @param ediNumber
     *            EDI Number to check
     * @return <code>true</code> if customer with specified edi number already exists, otherwise returns <code>false</code>.
     */
    boolean checkEDINumberExists(String ediNumber);

    /**
     * Update customer by customer.
     * 
     * @param customer
     *            id of customer
     * @return customers
     */
    CustomerEntity update(CustomerEntity customer);

    /**
     * Retrieves the primary sorting of Product list for given Customer.
     * 
     * @param customerId
     *            id of customer
     * @return the {@link ProductListPrimarySort}
     */
    ProductListPrimarySort getProductListPrimarySort(Long customerId);

    /**
     * Updates the primary sorting of Product list for given Customer.
     * 
     * @param customerId
     *            id of customer
     * @param productListPrimarySort
     *            the {@link ProductListPrimarySort}
     */
    void updateProductListPrimarySort(Long customerId, ProductListPrimarySort productListPrimarySort);

    /**
     * Find a list of {@link SimpleValue} which related to customer organizations and with not expired
     * effective date and with Active status.
     * 
     * @param filter
     *            Not <code>null</code> instance of {@link String} which correspond filter chars.
     * @param count
     *            count of records in result.
     * 
     * @return {@link List} of {@link SimpleValue}.
     */
    List<SimpleValue> findAccountExecutiveList(String filter, int count);

    /**
     * Find {@link CustomerEntity} which may handle specified user.
     *
     * @param personId
     *            Normally not <code>null</code> personId.
     * @param name
     *            Name pattern.
     * @param limit
     *            Max number of result entities.
     * @return Not <code>null</code> {@link List}.
     */
    List<ParentOrganizationBO> findCustomersForUserByName(Long personId, String name, int limit);

    /**
     * Get Customer Id, Name for LookupValue field.
     * 
     * If the parameter 'status' not pass, we get only active customer, if you pass a parameter 'status' we
     * get users without status
     * 
     * @param name
     *            Name pattern.
     * @param limit
     *            Max number of result entities.
     * @param offset
     *            Number of the first entity.
     * @param status
     *            Status customera.
     * @param personId
     *            User ID that will be used to obtain customers
     * @return Not <code>null</code> {@link List}.
     */
    List<SimpleValue> getCustomerIdNameTuplesByName(String name, int limit, int offset, Boolean status, Long personId);

    /**
     * Retrieves a list of all available Account Executives.
     *
     * @return {@link List} of {@link SimpleValue}.
     */
    List<SimpleValue> getAccountExecutives();

    /**
     * Gets customer credit info.
     *
     * @param customerId customer id
     * @return {@link CustomerCreditInfoBO}
     */
    CustomerCreditInfoBO getCustomerCreditInfo(Long customerId);

    /**
     * Method returns list of {@link CustomerListItemBO} filtered by customer status and customer name for specified user.
     * @param personId - id of user
     * @param status - customer status
     * @param name - customer name pattern
     * @param businessUnitName - organizations networks
     * @return list of {@link CustomerListItemBO}
     */
    List<CustomerListItemBO> getByStatusAndName(Long personId, OrganizationStatus status, String name, String businessUnitName);

    /**
     * Check if any of fields which are sent to finance system is changed.
     * 
     * @param customer
     *            to check
     * 
     * @return <code>true</code> if at least one field is changed. <code>false</code> otherwise.
     */
    boolean isCustomerChangedForFinance(CustomerEntity customer);

    /**
     * Check customer by status.
     * 
     * @param customerId
     *            customer id
     * @return true if customer is active.
     */
    Boolean isActiveCustomer(Long customerId);

    /**
     * Get credit limit for customer.
     * 
     * @param customerId
     *            id of customer
     * @return credit limit
     */
    Long getCreditLimit(Long customerId);

    /**
     * Get list of locations for specified customer with information for specified user that current user has access to.
     * 
     * @param customerId
     *            id of customer.
     * @param currentUserId
     *            id of currently logged in user
     * @param userId
     *            id of user to get modification information
     * @return list of locations for specified customer that current user has access to.
     */
    List<AssociatedCustomerLocationBO> getAssociatedCustomerLocations(Long customerId, Long currentUserId, Long userId);

    /**
     * Returns <code>TRUE</code> if credit limit checking is required for specified customer. Otherwise returns <code>FALSE</code>
     * 
     * @param customerId - Not <code>null</code> {@link CustomerEntity#getId()}.
     * @return <code>TRUE</code> if credit limit checking is required for specified customer. Otherwise returns <code>FALSE</code>
     */
    Boolean getCreditLimitRequired(Long customerId);

    /**
     * Get Internal Note for customer.
     * 
     * @param customerId
     *            id of customer
     * @return Internal Note
     */
    String getInternalNote(Long customerId);

    /**
     * Check should we add a barcode to the BOL document.
     * 
     * @param customerId
     *            id of customer
     * @return true in case when barcode should be added to the BOL document
     */
    Boolean isPrintBarcode(Long customerId);
}
