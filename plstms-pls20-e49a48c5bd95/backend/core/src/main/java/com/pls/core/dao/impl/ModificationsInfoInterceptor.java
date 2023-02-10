package com.pls.core.dao.impl;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.StaleObjectStateException;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.SpringApplicationContext;

/**
 * Transaction interceptor to manage {@link com.pls.core.domain.PlainModificationObject} data for new or updated entities.
 * 
 * @author Maxim Medvedev
 */
@Component("modificationsInfoInterceptor")
public class ModificationsInfoInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = 8641828849074487853L;

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
            Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof HasModificationInfo) {
            fillOnUpdate((HasModificationInfo) entity);
        }
        if (entity instanceof HasVersion) {
            compareVersions(entity, id, propertyNames, previousState);
        }

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity instanceof HasModificationInfo) {
            HasModificationInfo modifiableEntity = (HasModificationInfo) entity;
            fillOnCreate(modifiableEntity);
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    private void compareVersions(Object entity, Serializable id, String[] propertyNames, Object[] previousState) {
        Integer version = ((HasVersion) entity).getVersion();
        if (version != null) {
            int index = ArrayUtils.indexOf(propertyNames, "version");
            if (index >= 0 && !version.equals(previousState[index])) {
                throw new StaleObjectStateException(entity.getClass().getName(), id);
            }
        }
    }

    private Long extractCurrentUserId() {
        Long result = SecurityUtils.getCurrentPersonId();
        return result == null ? SpringApplicationContext.getAdminUserId() : result;
    }

    private void fillOnCreate(HasModificationInfo entity) {
        Date currentDate = new Date();
        Long userId = extractCurrentUserId();
        entity.getModification().setCreatedDate(currentDate);
        // Do not override createdBy field
        if (entity.getModification().getCreatedBy() == null) {
            entity.getModification().setCreatedBy(userId);
        }

        entity.getModification().setModifiedDate(currentDate);
        // Do not override modifiedBy field if it has been specified during creation
        if (entity.getModification().getModifiedBy() == null) {
            entity.getModification().setModifiedBy(userId);
        }
    }

    private void fillOnUpdate(HasModificationInfo entity) {
        Date currentDate = new Date();
        Long userId = extractCurrentUserId();
        entity.getModification().setModifiedDate(currentDate);
        entity.getModification().setModifiedBy(userId);
    }

}