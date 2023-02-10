package com.pls.core.service.address;

import java.io.IOException;
import java.util.List;

import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ContactInfoSetBO;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.AddressVO;

/**
 * Business service that handles business logic for addresses.
 *
 * @author Gleb Zgonikov
 */
public interface AddressService {

    /**
     * Get instance of {@link ContactInfoSetBO} by specified <code>userId</code>.
     *
     * @param userId
     *            id of user
     * @return {@link ContactInfoSetBO}
     * */
    ContactInfoSetBO findContactInfoSet(Long userId);

    /**
     * Lists all Bill To addresses of specified organization.
     *
     * @param organizationId
     *            id of organization
     * @param currency currency code
     * @return list of user's address book or empty list
     */
    List<BillToEntity> getOrgBillToAddresses(Long organizationId, Currency currency);

    /**
     * Deletes address. (Moved to archived status).
     *
     * @param addrId
     *            - id of address that will be deleted
     * @param userId
     *            id of modified user
     * @return true if successful
     */
    boolean deleteAddressBookEntry(Long addrId, Long userId);

    /**
     * Save or update {@link com.pls.core.domain.address.UserAddressBookEntity}.
     *
     * @param address
     *            entity to save or update.
     * @param organizationId
     *            id of organization
     * @param userId
     *            id of user to be set as created user (in case of create operation)
     * @param isSelf
     *            permission for specified user to share or not this address with other users.
     * @throws ValidationException
     *             exception
     */
    void saveOrUpdate(UserAddressBookEntity address, Long organizationId, Long userId, boolean isSelf)
            throws ValidationException;

    /**
     * Save or update {@link com.pls.core.domain.address.UserAddressBookEntity}.
     *
     * @param address
     *            entity to save or update.
     * @param organizationId
     *            id of organization
     * @param userId
     *            id of user to be set as created user (in case of create operation)
     * @param copyAddress
     *            define should we or not create new entry to address table
     * @param isSelf
     *            permission for specified user to share or not this address with other users.
     * @throws ValidationException
     *             exception
     */
    void saveOrUpdate(UserAddressBookEntity address, Long organizationId, Long userId, boolean copyAddress,
            boolean isSelf) throws ValidationException;

    /**
     * Get Address entity by id and organization.
     *
     * @param id
     *            address identifier
     * @return object or null
     */
    UserAddressBookEntity getCustomerAddressById(Long id);

    /**
     * Get Address entity by address name and address code.
     *
     * @param addressName
     *            address name
     * @param addressCode
     *            address Code
     * @param customerId
     *            id of Customer
     * @return object or null
     */
    UserAddressBookEntity getCustomerAddressByNameAndCode(String addressName, String addressCode, Long customerId);

    /**
     * Method returns a filtered and/or sorted list of
     * {@link com.pls.core.domain.address.UserAddressBookEntity} for provided organization and user.
     *
     * @param customerId
     *            id of customer
     * @param userId
     *            id of customer user to select addresses. Should be <code>null</code> if need to select all
     *            addresses for customer
     * @param filterWarnings
     *            when <code>true</code> then no addresses with PO Box ZIP codes will be returned.
     * @param types
     *            array of address types {@link AddressType}}
     * @return list of {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    List<UserAddressBookEntity> getCustomerAddressBookForUser(Long customerId, Long userId, boolean filterWarnings, AddressType[] types);

    /**
     * Method returns the list of the {@link com.pls.core.domain.address.UserAddressBookEntity} for provided
     * organization and user by country code and zip.
     *
     * @param countryCode
     *            country code
     * @param zipCode
     *            zip code
     * @param city
     *            city
     * @param customerId
     *            id of customer
     * @param userId
     *            id of customer user to select addresses. Should be <code>null</code> if need to select all addresses for customer
     * @param types
     *            array of address types {@link AddressType}}
     * @return list of {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    List<UserAddressBookEntity> getCustomerAddressBookByCountryAndZip(String countryCode, String zipCode, String city,
            Long customerId, Long userId, AddressType[] types);

    /**
     * Checks if address with same name already exist in database.
     *
     * @param addressName
     *            address name
     * @param customerId
     *            id of customer
     * @return true if exists
     */
    boolean isAddressNameExists(String addressName, Long customerId);

    /**
     * Checks address code already exist in database.
     *
     * @param addressCode
     *            address code
     * @param customerId
     *            id of customer
     * @return true if exists
     */
    boolean isAddressCodeExists(String addressCode, Long customerId);

    /**
     * Checks whether combination of address code and address name given customer is unique.
     *
     * @param addressName
     *            address name
     * @param addressCode
     *            address code
     * @param customerId
     *            id of customer
     * @return true if uniquie
     */
    boolean isAddressUnique(String addressName, String addressCode, Long customerId);

    /**
     * Method gets address id.
     * @param address - address
     * @return address ID
     */
    Long getAddressId(AddressVO address);

    /**
     * Method gets the address record.
     *
     * @param address
     *            - address
     * @return address record
     */
    AddressEntity getNewOrExistingAddress(AddressVO address);

    /**
     * Method returns address VO by id.
     * @param id - id
     * @return address VO
     */
    AddressVO getAddressVOById(Long id);

    /**
     * Find time zone by country and zip code.
     *
     * @param countryCode country code
     * @param zipCode zip/postal code
     * @return {@link TimeZoneEntity} or null
     */
    TimeZoneEntity findTimeZoneByCountryZip(String countryCode, String zipCode);

    /**
     * Method returns address entity by id.
     * @param id - id
     * @return address entity
     */
    AddressEntity getAddressEntityById(Long id);

    /**
     *  Intended to find all addresses matching query.
     *
     * @param customerId - customer identifier to perform address search for
     * @param query - address chunk to look for
     * @return - list of matching addresses
     */
    List<UserAddressBookEntity> listSuggestions(Long customerId, String query);

    /**
     *  Intended to find all addresses matching query.
     *
     * @param customerId - customer identifier to perform address search for
     * @param query - address chunk to look for
     * @param strict - if set to true perform "EXACT" matching and "ANYWHERE" otherwise
     * @return list of matching addresses
     */
    List<UserAddressBookEntity> listSuggestions(Long customerId, String query, boolean strict);


    /**
     * Finds the address using the address code and customer Id.
     *
     * @param code address code
     * @param orgId id of customer
     * @return address entity
     */
    UserAddressBookEntity getCustomerAddressByCode(Long orgId, String code);

    /**
     * Export list of addresses for specified customer.
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
     * Returns default Freight Bill Pay To Address assigned to specified Customer.
     * 
     * @param customerId - Id of Customer. Not <code>null</code>.
     * @return {@link UserAddressBookEntity} if it found, otherwise returns <code>null</code>.
     */
    UserAddressBookEntity getDefaultFreightBillPayToAddresses(Long customerId);

    /**
     * Get user address books for Freight Bill Pay To.
     * 
     * @param customerId
     *            id of customer
     * @param userId
     *            id of customer user to select addresses. Should be <code>null</code> if need to select all
     *            addresses for customer
     * @param filter
     *            text to filter results.
     * @return list of address books
     */
    List<UserAddressBookEntity> getAddressBooksForFreightBill(Long customerId, Long userId, String filter);
}
