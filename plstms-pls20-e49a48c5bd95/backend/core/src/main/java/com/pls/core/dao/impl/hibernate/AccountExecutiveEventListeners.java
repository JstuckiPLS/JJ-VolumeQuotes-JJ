package com.pls.core.dao.impl.hibernate;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import com.google.common.base.Optional;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.shared.Status;

/**
 * Hibernate listeners tracking changes in AccountExecutiveEntity and maintaining history of changes.
 * <p>
 * Any table having <<XX>_TRACKING_ID is used for maintaining history. In this case, whenever we make a change to a record,
 * we inactivate existing one and create new one with updated information. To identify which was the original record which we have been updating,
 * we use “<<XX>_TRACKING_ID” and this gives us the very first record that was created for this scenario.
 * Account Executives is just this case.<br/>
 * Changes of STATUS and Modification Information is not tracked and not lead to creation of new records in database.
 * </p>
 * <p>
 *  Scenarios:<br/>
 *  1. Create Account Executive for a location => INSERT a record in USER_CUSTOMER table. Use PK as tracking Id.<br/>
 *  2. Update eff_date/exp_date for same Account Executive on same location => UPDATE existing record (created above) by inactivating it and
 * INSERT new record with updated values and with old record PK as tracking Id.<br/>
 *  3. Change Account Executive (different user) for same location  => UPDATE existing record (created above) by inactivating it and INSERT new
 *  record with expiry date = eff_date of new record (new user). INSERT another record similar to point 1 (INSERT a record in USER_CUSTOMER table
 *  by using PK as tracking Id ) with person_id = new user person_id. And the next steps will be same as 2,
 *  4 until there is change in User for the same location. <br/>
 *  4. Remove account executive for same location (emptying the Account Executive for a location) => Just UPDATE existing record by setting
 * current date as exp_date. Don’t inactivate the record.
 * </p>
 *
 * @author Viacheslav Krot
 */
public class AccountExecutiveEventListeners {

    /**
     * Creates new table records with updated information.
     */
    public static class PreUpdate implements PreUpdateEventListener {
        private static final long serialVersionUID = -2690489924941035347L;
        private static final List<String> TRACKED_FIELDS = Arrays.asList("effectiveDate", "expirationDate", "user", "locationId", "organization");

        @Override
        public boolean onPreUpdate(PreUpdateEvent event) {
            if (event.getEntity() instanceof AccountExecutiveEntity) {
                // if change was made to audit information or STATUS - do not create separate record
                if (!isDirty(event.getState(), event.getOldState(), event.getPersister())) {
                    return false;
                }

                AccountExecutiveEntity entity = (AccountExecutiveEntity) event.getEntity();

                // prohibit changes of any fields when status is inactive
                if (entity.getStatus() == Status.INACTIVE) {
                    return true;
                }

                // clone existing record and add to customer account executives collection
                AccountExecutiveEntity clone = new AccountExecutiveEntity(entity);
                entity.getLocation().getAccountExecutives().add(clone); // save clone
                event.getSession().save(clone);
                event.getSession().getActionQueue().executeInserts();

                // rollback changes in entity and make it inactive
                entity.setStatus(Status.INACTIVE);

                String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
                for (int i = 0; i < propertyNames.length; i++) {
                    if (TRACKED_FIELDS.contains(propertyNames[i])) {
                        event.getState()[i] = event.getOldState()[i];
                    }
                }
                setValue(event.getPersister(), event.getState(), "status", Status.INACTIVE);
            }
            return false;
        }

        private boolean isDirty(Object[] state, Object[] oldState, EntityPersister persister) {
            String[] propertyNames = persister.getEntityMetamodel().getPropertyNames();
            for (int i = 0; i < propertyNames.length; i++) {
                if (TRACKED_FIELDS.contains(propertyNames[i])
                        && !Optional.fromNullable(state[i]).equals(Optional.fromNullable(oldState[i]))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Sets initial tracking id to value equal to ID.
     */
    public static class PreInsert implements PreInsertEventListener {
        private static final long serialVersionUID = 8665169630134032481L;

        @Override
        public boolean onPreInsert(PreInsertEvent event) {
            if (event.getEntity() instanceof AccountExecutiveEntity) {
                AccountExecutiveEntity acc = (AccountExecutiveEntity) event.getEntity();
                if (acc.getTrackingId() == null) {
                    acc.setTrackingId(acc.getId());
                    setValue(event.getPersister(), event.getState(), "trackingId", acc.getTrackingId());
                }
            }
            return false;
        }
    }

    private static void setValue(EntityPersister persister, Object[] state, String fieldName, Object value) {
        String[] propertyNames = persister.getEntityMetamodel().getPropertyNames();
        int index = ArrayUtils.indexOf(propertyNames, fieldName);
        if (index >= 0) {
            state[index] = value;
        } else {
            throw new IllegalArgumentException(String.format("Fields %s not found among property names", fieldName));
        }
    }

}
