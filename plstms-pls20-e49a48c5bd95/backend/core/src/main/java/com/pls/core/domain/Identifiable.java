package com.pls.core.domain;

import java.io.Serializable;

/**
 * Interface represents contract for entity to have an ID.
 * 
 * @param <IdType>
 *            is used to present type of entity ID.
 * 
 * @author Gleb Zgonikov
 */
public interface Identifiable<IdType> extends Serializable {
    /**
     * Return the id of this Identifiable object.
     * 
     * @return the id of this Identifiable object.
     */
    IdType getId();

    //TODO normally we do not need setter for primary key
    /**
     * Set the id of this Identifiable object.
     * 
     * @param id
     *            the id of this Identifiable object.
     */
    void setId(IdType id);
}
