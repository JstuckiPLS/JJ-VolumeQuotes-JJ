package com.pls.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;

/**
 * DAO for {@link com.pls.core.domain.address.UserAddressBookEntity} data.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface UserAddressBookDao extends AbstractDao<UserAddressBookEntity, Long> {

    /**
     * Method returns a list of {@link com.pls.core.domain.address.UserAddressBookEntity} for provided
     * organization and user. If provided user id is <code>null</code>, the result will contain all addresses
     * for given organization.
     *
     * @param orgId
     *            organization Id
     * @param userId
     *            user Id. Should be <code>null</code> in case all organization's addresses need to be
     *            retrieved
     * @param filterWarnings
     *            when <code>true</code> then no addresses with PO Box ZIP codes will be returned.
     * @param types
     *            list of {@link AddressType}
     * @return list of {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    List<UserAddressBookEntity> getCustomerAddressBookForUser(Long orgId, Long userId, boolean filterWarnings, List<AddressType> types);

    /**
     * Get user address books for Freight Bill Pay To.
     * 
     * @param customerId
     *            organization Id
     * @param userId
     *            user Id. Should be <code>null</code> in case all organization's addresses need to be
     *            retrieved
     * @param filter
     *            text to filter results.
     * @return list of address books
     */
    List<UserAddressBookEntity> getAddressBooksForFreightBill(Long customerId, Long userId, String filter);

    /**
     * Method sets the status of the UserAddressBookEntity into Archived and update value of the modifiedBy
     * and the modifiedDate fields.
     *
     * @param addressId
     *            ltl address id
     * @param userId
     *            user id
     * @return true if successful
     */
    boolean deleteUserAddressBookEntry(Long addressId, Long userId);

    /**
     * Find next value at sequence of location codes.
     *
     * @return address code
     */
    BigDecimal getNextGeneratedAddressCode();

    /**
     * Find next value at sequence of address names numbers.
     *
     * @return address name number
     */
    BigDecimal getNextGeneratedAddressNameNumber();

    /**
     * Check if address code for specified organization user already exists. Will return true if exists.
     *
     * @param orgUsrId        organization user id
     * @param addressCode address code to check
     * @return <code>true</code> if address code already exists
     */
    boolean checkAddressCodeExists(Long orgUsrId, String addressCode);


    /**
     * Check if address code for specified organization user already exists. Will return true if exists.
     *
     * @param orgId
     *            organization user id
     * @param addressCode
     *            address code to check
     * @param addressName
     *            address name to check
     * @return <code>true</code> if address code already exists
     */
    boolean isAddressUnique(Long orgId, String addressName, String addressCode);

    /**
     * Check if address name for specified customer already exists. Will return true if exists.
     *
     * @param orgUsrId       organization user id
     * @param addressName address name to check
     * @return <code>true</code> if address name already exists
     */
    boolean checkAddressNameExists(Long orgUsrId, String addressName);

    /**
     * Method returns customer addresses by country code and zip code.
     * If provided user id is <code>null</code>, the result will contain all addresses for given organization filtered by country and zip.
     *
     * @param orgId       organization id
     * @param userId      user id. Should be <code>null</code> in case all organization's addresses need to be retrieved
     * @param countryCode country code
     * @param zip         zip code
     * @param city        city
     * @param types       list of {@link AddressType}
     * @return list of {@link com.pls.core.domain.address.UserAddressBookEntity}
     * @throws IllegalArgumentException if one of the parameters is null or blank
     */
    List<UserAddressBookEntity> findCustomerAddressByCountryAndZip(Long orgId, Long userId, String countryCode,
            String zip, String city, List<AddressType> types)
            throws IllegalArgumentException;

    /**
     * Method returns an address by id.
     *
     * @param addressId
     *            address id
     * @return {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    UserAddressBookEntity getUserAddressBookEntryById(Long addressId);

    /**
     * Method returns an address by address name and address code.
     *
     * @param addressName
     *            address name
     * @param addressCode
     *            address Code
     * @param customerId
     *            id of Customer
     * @return {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    UserAddressBookEntity getUserAddressBookEntryByNameAndCode(String addressName, String addressCode, Long customerId);


    /**
     * Returns found addresses by parameters.
     *
     * @param address1 first address name
     * @param address2 second address name
     * @param city the name of city
     * @param zip - zip of country
     * @param countryCode the code of country
     * @param stateCode the code of state
     * @param customerId id of customer
     * @return {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    List<UserAddressBookEntity> searchUserAddressBookEntries(String address1,
            String address2, String city, String zip, String countryCode,
            String stateCode, Long customerId);

    /**
     * Returns address book after search by word on "address1, address2, orgName, contactFirstName, contactLastName".
     * If provided user id is <code>null</code>, the result will contain all addresses for given organization filtered by search word.
     *
     * @param orgId      is organization's identifier
     * @param userId     is user's identifier. Should be <code>null</code> in case all organization's addresses need to be retrieved
     * @param filterWord is a search criteria
     * @return list of address book entities filtered by word or empty list
     */
    List<UserAddressBookEntity> listFilteredByWord(Long orgId, Long userId, String filterWord);

    /**
     * Current method will immediately flush persisted data to DB.
     *
     * @param entity entity to persist
     */
    void persistWithFlush(UserAddressBookEntity entity);

    /**
     * Returns list of {@link com.pls.core.domain.address.UserAddressBookEntity} for given organization and user, filtered by Address Name.
     * If provided user id is <code>null</code>, the result will contain all addresses filtered by Address Name for given organization.
     *
     *
     * @param orgId       organization id
     * @param userId      user id. Should be <code>null</code> in case all organization's addresses need to be retrieved
     * @param addressName Address Name
     * @param strictSearch if true will search by exact address name
     * @return list of {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    List<UserAddressBookEntity> getAddressesByName(Long orgId, Long userId, String addressName, boolean strictSearch);

    /**
     * Re-read the state of the address from the database and make the address archived.
     *
     * @param address to reset
     * @param userId user id
     */
    void resetAndArchived(UserAddressBookEntity address, Long userId);

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
     * @param strictSearch - if set to true perform "EXACT" matching and "ANYWHERE" otherwise
     * @return list of matching addresses
     */
    List<UserAddressBookEntity> listSuggestions(Long customerId, String query, boolean strictSearch);

    /**
     * Finds the address entity with the matching customer and code.
     *
     * @param orgId organization user id
     * @param code address code to check
     * @return {@link com.pls.core.domain.address.UserAddressBookEntity}
     */
    UserAddressBookEntity getCustomerAddressBookEntryByCode(Long orgId, String code);

    /**
     * Resets default Addresses for customer.
     *
     * @param orgId organization user id
     */
    void resetDefaultAddressesForCustomer(Long orgId);

    /**
     * Gets the default freight bill pay to.
     *
     * @param customerId the customer id
     * @return the default freight bill pay to
     */
    UserAddressBookEntity getDefaultFreightBillPayTo(Long customerId);
}
