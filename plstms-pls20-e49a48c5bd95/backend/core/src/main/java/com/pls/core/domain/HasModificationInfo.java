package com.pls.core.domain;

/**
 * Marker interface to manage {@link PlainModificationObject} data automatically. This means that
 * {@link PlainModificationObject#getCreatedBy()}, {@link PlainModificationObject#getModifiedBy()} and other values will
 * filled using values from security context.<br />
 * <b>Warning</b>: this interceptor handles only hibernate entities and it do not affect SQL and HQL
 * INSERT/UPDATE queries.
 * 
 * @author Maxim Medvedev
 */
public interface HasModificationInfo {
    /**
     * Get information about who and when was created this object.
     * 
     * @return Should be not <code>null</code> {@link PlainModificationObject} value.
     */
    PlainModificationObject getModification();

}
