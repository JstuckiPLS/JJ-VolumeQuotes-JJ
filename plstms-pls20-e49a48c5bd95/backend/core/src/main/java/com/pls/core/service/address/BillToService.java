package com.pls.core.service.address;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;

/**
 * Bill To service.
 * 
 * @author Andrey Kachur
 * 
 */
public interface BillToService {

    /**
     * Get a instance of {@link BillToEntity} by specified id.
     * 
     * @param id
     *          unique identifier of {@link BillToEntity}.
     * @return {@link BillToEntity}
     * */
    BillToEntity getBillTo(Long id);

    /**
     * Validate whether address with the same name exists or not.
     * 
     * @param name
     *            - address name to be validated.
     * @param orgId
     *            Organization Id.
     * @return true if address with specified name already exists and false otherwise
     */
    boolean validateDuplicateName(String name, Long orgId);

    /**
     * Returns List of Bill To {@link KeyValueBO} by specified organization id.
     * 
     * @param orgId Organization Id.
     * @return List of {@link KeyValueBO}.
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
