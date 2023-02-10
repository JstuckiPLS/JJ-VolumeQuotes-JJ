package com.pls.core.dao.rating;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.shared.Status;

/**
 * DAO for {@link AccessorialTypeEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface AccessorialTypeDao extends AbstractDao<AccessorialTypeEntity, String> {

    /**
     * Retrieves {@link AccessorialTypeEntity} by specified <code>status</code>.
     * 
     * @param status
     *            {@link Status}. Can be Active or Inactive.
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    List<AccessorialTypeEntity> findAccessorialTypesByStatus(Status status);

    /**
     * Change {@link AccessorialTypeEntity} status by specified arguments.
     * 
     * @param accessorialCodes
     *            {@link List} of String. Not <code>null</code>
     * @param status
     *            {@link Status}.
     * @param modifiedBy
     *            {@link Long}.
     */
    void updateStatus(List<String> accessorialCodes, Status status, Long modifiedBy);

    /**
     * Check if {@link AccessorialTypeEntity} already exists by specified code.
     * 
     * @param code
     *            - code which should be verified.
     * @return <code>true</code> if code already exists, <code>true</code> in other case.
     */
    boolean checkAccessorialCodeExists(String code);

    /**
     *  Fetches list of accessorial types.
     * 
     * @param group - certain accessorial group
     * @return accessorial type list matching group specified
     */
    List<AccessorialTypeEntity> listPickupAccessorialTypes(String group);

    /**
     *  Fetches list of accessorial types that are not PLS accessorial types.
     * 
     * @return applicable accessorial type list that are not PLS accessorial types
     */
    List<AccessorialTypeEntity> getAllApplicableAccessorialTypes();

    /**
     * Get list of all pickup and delivery accessorials that are present at passed collection.
     * 
     * @param accessorialTypes
     *            list of accessorials types that may be pickup or delivery or some other accessorials.
     * @return list of all pickup and delivery accessorials that are present at passed collection.
     */
    List<AccessorialTypeEntity> getPickupAndDeliveryAccessorials(Set<String> accessorialTypes);

    /**
     * Refresh accessorials with up-to-date state.
     * 
     * @param accessorials
     *            to refresh
     */
    void refreshAccessorials(Collection<AccessorialTypeEntity> accessorials);

    /**
     * Checks accessorial type code uniqueness.
     * 
     * @param code - particular code we are looking for
     * @return - true if code was ever used and false otherwise
     */
    boolean isAccessorialTypeUnique(String code);

    /**
     * Method returns list of ltl accessorial types belonging to a accessorial group(pickup/delivery).
     * 
     * @return - List of accessorial types.
     */
    List<AccessorialTypeEntity> getLtlAccessorialTypes();
}
