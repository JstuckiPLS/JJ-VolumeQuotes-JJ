package com.pls.core.domain;

/**
 * Marker interface to manage version automatically. <br />
 * <b>Warning</b>: this interceptor handles only hibernate entities and it do not affect SQL and HQL
 * INSERT/UPDATE queries.
 * 
 * @author Maxim Medvedev
 */
public interface HasVersion {
    /**
     * Get information about this object's version.
     * 
     * @return version number.
     */
    Integer getVersion();

}
