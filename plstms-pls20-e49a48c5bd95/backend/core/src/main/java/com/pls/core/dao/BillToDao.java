package com.pls.core.dao;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;

/**
 * DAO that works with BillToEntity.
 * 
 * @author Andrey Kachur
 */
public interface BillToDao extends AbstractDao<BillToEntity, Long> {

    /**
     * Finds all Bill To entities for provided organization.
     * @param orgId Organization ID.
     * @param currency currency code.
     * @return list of BillToEntity or empty list
     */
    List<BillToEntity> findBillTo(Long orgId, Currency currency);

    /**
     * Checks whether specified address exists or not.
     * 
     * @param nameToBeValidated
     *            - address name to be validated
     * @param orgId
     *            Organization ID
     * @return - true is specified address already exists and false otherwise
     */
    boolean validateDuplicateName(String nameToBeValidated, Long orgId);

    /**
     * Returns List of {@link KeyValueBO} by specified organization id.
     * 
     * @param orgId Organization ID.
     * @return - List of {@link KeyValueBO}.
     */
    List<KeyValueBO> getIdAndNameByOrgId(Long orgId);

    /**
     * Returns email list for bill to if present in invoice preferences.
     * 
     * @param billToIds
     *            list of Bill To id.
     * @return List of {@link KeyValueBO} where key > 0 if email should be sent for Bill To, value is email list.
     */
    List<KeyValueBO> getBillToEmails(List<Long> billToIds);

    /**
     * Returns the bill to Entity.
     * @param billToName name of the Bill To address.
     * @param customerOrgId Id of customer organization.
     * @return {@link BillToEntity} corresponding
     */
    BillToEntity findByCustomerAndBillToName(String billToName, Long customerOrgId);

    /**
     * Returns list of bill to addresses for customer.
     * 
     * @param customerId
     *            id of customer
     * @return list of bill to addresses
     */
    Collection<BillToEntity> getBillToAddresses(Long customerId);

    /**
     * Gets the default entity by id.
     *
     * @param defaultEntityId the default entity id
     * @return the default entity by id
     */
    BillToDefaultValuesEntity getDefaultEntityById(Long defaultEntityId);
}
