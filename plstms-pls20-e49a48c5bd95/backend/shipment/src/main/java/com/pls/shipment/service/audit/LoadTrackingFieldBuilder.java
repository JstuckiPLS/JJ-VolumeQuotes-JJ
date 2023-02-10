
package com.pls.shipment.service.audit;

import java.util.List;

import org.hibernate.Session;

import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;

/**
 * Functional Interface for building List of {@link LoadEventEntity} for different {@link LoadEntity} fields.
 * @author Dmitriy Davydenko
 *
 */
@FunctionalInterface
public interface LoadTrackingFieldBuilder {

    /**
     * Produces the List of {@link LoadEventEntity} based on the field passed.
     * @param entity - Load entity.
     * @param session - Hibernate session.
     * @param fieldName - field name of {@link LoadEntity} for which need to create load event.
     * @param oldValue - old value of tracking field.
     * @param newValue - new value of tracking field.
     * @return {@link LoadEventEntity} or <code>null</code> if it was provided by logic.
     * @return
     */
    List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue);

}
