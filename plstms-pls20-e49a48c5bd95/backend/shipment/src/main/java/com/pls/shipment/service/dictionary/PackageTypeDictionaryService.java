package com.pls.shipment.service.dictionary;

import com.pls.shipment.domain.PackageTypeEntity;

import java.util.List;

/**
 * Service for {@link PackageTypeEntity}.
 *
 * @author Sergey Kirichenko
 */
public interface PackageTypeDictionaryService {

    /**
     * Get all package types.
     *
     * @return list of {@link PackageTypeEntity}
     */
    List<PackageTypeEntity> getAllPackageTypes();

    /**
     * Get package type by id.
     *
     * @param id package type's id
     * @return {@link PackageTypeEntity}
     */
    PackageTypeEntity getById(String id);
}
