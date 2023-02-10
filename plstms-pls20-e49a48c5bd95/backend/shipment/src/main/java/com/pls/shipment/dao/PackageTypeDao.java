package com.pls.shipment.dao;

import java.util.List;

import com.pls.shipment.domain.PackageTypeEntity;

/**
 * DAO for {@link PackageTypeEntity}.
 * 
 * @author Sergey Kirichenko
 */
public interface PackageTypeDao {

    /**
     * Get package type by id.
     * 
     * @param id
     *            package type's id
     * @return {@link PackageTypeEntity}
     */
    PackageTypeEntity getById(String id);

    /**
     * Get list entity .
     * 
     * @return List<PackageTypeEntity>
     */
    List<PackageTypeEntity> getAll();
}
