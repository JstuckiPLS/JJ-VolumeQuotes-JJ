package com.pls.core.service.rating;

import java.util.List;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.shared.Status;

/**
 * Service that handle business logic for Accessorial Types.
 * 
 * @author Artem Arapov
 * 
 */
public interface RatingAccessorialTypeService {

    /**
     * Get all {@link AccessorialTypeEntity}.
     * 
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    List<AccessorialTypeEntity> getAllAccessorialType();

    /**
     * Get {@link AccessorialTypeEntity} by specified <code>status</code>.
     * 
     * @param status
     *            {@link Status}. Can be Active or Inactive.
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    List<AccessorialTypeEntity> getAllAccessorialTypeByStatus(Status status);

    /**
     * Get {@link AccessorialTypeEntity} by specified <code>accessorialCode</code>.
     * 
     * @param accessorialCode
     *            code of {@link AccessorialTypeEntity} which should be finded.
     * @return {@link AccessorialTypeEntity.
     */
    AccessorialTypeEntity getByCode(String accessorialCode);

    /**
     * Save corresponded entity.
     * 
     * @param entity
     *            {@link AccessorialTypeEntity} entity to save.
     */
    void saveAccessorialType(AccessorialTypeEntity entity);

    /**
     * Change {@link AccessorialTypeEntity} status by specified arguments.
     * 
     * @param accessorialCodes
     *            {@link List} of String. Not <code>null</code>
     * @param status
     *            {@link Status}.
     */
    void updateStatus(List<String> accessorialCodes, Status status);

    /**
     * Check if {@link AccessorialTypeEntity} already exists by specified code.
     * 
     * @param code
     *            - code which should be verified.
     * @return <code>true</code> if code already exists, <code>true</code> in other case.
     */
    boolean checkAccessorialCodeExists(String code);

    /**
     *  Fetches all accessorial types matching group specified.
     * 
     *  @param group - certain accessorial group
     * @return list of accessorial types
     */
    List<AccessorialTypeEntity> listAccessorialTypesByGroup(String group);

    /**
     *  Checks whether or not accessorial type code unique(is not met neither in saved quotes nor in shipments).
     * @param code - accessorial type code is being looking for
     * @return true if accessorial type is already used and false otherwise
     */
    boolean isAccessorialTypeUnique(String code);
}
