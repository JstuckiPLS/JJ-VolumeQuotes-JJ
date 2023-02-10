package com.pls.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;

/**
 * Business service that handle business logic for customer.
 * 
 * @author Denis Zhupinsky (TEAM International)
 */
public interface CustomerService {
    /**
     * Check if a given customer name exists.
     * 
     * @param customerName
     *            customer name to check
     * @return true if customer with given name exists.
     */
    boolean checkCustomerNameExists(String customerName);

    /**
     * Check if a given federal tax id already exists.
     * 
     * @param federalTaxId
     *            federal tax id to search
     * @return true if customers with that tax already exist
     */
    boolean checkFederalTaxIdExists(String federalTaxId);

    /**
     * Create customer.
     * 
     * @param customer
     *            business object to save
     * @return generated data that was created during save
     * @throws ApplicationException
     *             if validation checks fail or information is not sent to finance.
     */
    CustomerEntity createCustomer(CustomerEntity customer) throws ApplicationException;

    /**
     * Search customers by specified id.
     * 
     * @param customerId
     *            customer id
     * @return list of customer dto
     */
    CustomerEntity findCustomerById(Long customerId);

    /**
     * Search customers by following name.
     * 
     * @param name
     *            customer name
     * @return list of customer dto
     */
    CustomerEntity findCustomerByName(String name);

    /**
     * Get List of {@link SimpleValue} by specified filter.
     * 
     * @param filter
     *            Not <code>null</code> instance of {@link String}.
     * @return {@link List} of {@link SimpleValue}.
     */
    List<SimpleValue> getAccountExecutivesByFilter(String filter);

    /**
     * Gets the current user settings param which specify sort order of Product List drop downs.
     * 
     * @param orgId
     *            id of actual organization
     * @return {@link ProductListPrimarySort}
     */
    ProductListPrimarySort getProductListPrimarySort(Long orgId);

    /**
     * Sets the current user settings param which specify sort order of Product List drop downs.
     * 
     * @param sort
     *            {@link ProductListPrimarySort}.
     * @param orgId
     *            id of actual organization
     */
    void setProductListPrimarySort(ProductListPrimarySort sort, Long orgId);

    /**
     * Update customer.
     * 
     * @param customer
     *            business object to update
     * @return generated data that was changed during update
     * @throws ApplicationException
     *             if validation checks fail or information is not sent to finance.
     */
    CustomerEntity updateCustomer(CustomerEntity customer) throws ApplicationException;

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
     * Method returns list of {@link CustomerListItemBO} filtered by customer status and customer name for current user.
     * @param status - customer status
     * @param name - customer name pattern
     * @param businessUnitName - organizations networks
     * @return list of {@link CustomerListItemBO}
     */
    List<CustomerListItemBO> getByStatusAndName(OrganizationStatus status, String name, String businessUnitName);

    /**
     * Save bill to.
     * 
     * @param billTo
     *            'Bill To' to save
     * @throws ApplicationException
     *             if changes can't be sync'ed with finance system
     */
    void saveBillTo(BillToEntity billTo) throws ApplicationException;

    /**
     * Get bill to by id.
     *
     * @param billToId id of 'Bill To'
     * @return bill to that was found
     */
    BillToEntity getBillTo(long billToId);

    /**
     * Get min accepted margin for customer.
     * 
     * @param customerId
     *            {@link CustomerEntity#getId()}
     * @return min accepted margin. Default is 5.
     */
    BigDecimal getMarginTolerance(long customerId);

    /**
     * Retrieves true of on active customers for customerId.
     * 
     * @param customerId
     *            {@link CustomerEntity#getId()}
     * 
     * @return true if customer is active.
     */
    Boolean isActiveCustomer(Long customerId);

    /**
     * Check if {@link CustomerEntity} with given edi number already exists.
     * 
     * @param ediNumber
     *            EDI Number to check
     * @return <code>true</code> if customer with specified edi number already exists, otherwise returns <code>false</code>.
     */
    boolean checkEDINumberExists(String ediNumber);

    /**
     * Get list Networks.
     * 
     * @return list of {@link NetworkListItemBO}.
     */
    List<NetworkListItemBO> getAllNetworks();

    /**
     * Get Network Unit and Cost Center codes.
     * 
     * @param orgId
     *            id of Organization
     * @return {@link UnitAndCostCenterCodesBO}.
     */
    UnitAndCostCenterCodesBO getUnitCostCenterCodes(Long orgId);

    /**
     * Get credit limit for customer.
     * 
     * @param customerId
     *            id of customer
     * @return credit limit
     */
    Long getCreditLimit(Long customerId);

    /**
     * Check if Credit Limit Validation is required for specified Customer.
     * 
     * @param customerId - Not <code>null</code>. {@link CustomerEntity#getId()}
     * @return <code>TRUE</code> if validation is required, otherwise returns <code>FALSE</code>
     */
    boolean getCreditLimitRequited(Long customerId);

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
