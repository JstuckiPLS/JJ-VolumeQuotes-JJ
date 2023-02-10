package com.pls.core.dao.impl.listener;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;


/**
 * Listener for {@link com.pls.core.domain.address.UserAddressBookEntity} that will be invoked before inserting new entity.
 * Purpose of this listener is to check whether addressCode and addressName are empty and if yes it will generate values for that columns.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class UserAddressBookListener implements PreInsertEventListener {

    private static final long serialVersionUID = -5128476135494011852L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAddressBookListener.class);

    private static final String LTL_LOCATION_CODE_PREFIX = "LT";

    private static final String GENERATED_LOCATION_CODE_FORMAT = "%s%s%s%s%07d";

    private static final String ADDR_NAME_PREFIX = "Imported_Address_";

    private static final String GENERATED_ADDR_NAME_FORMAT = "%s%06d";

    private static final String SEPARATOR = "-";

    @Autowired
    private UserAddressBookDao userAddressBookDao;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {

        if (event.getEntity() instanceof UserAddressBookEntity) {
            UserAddressBookEntity entity = (UserAddressBookEntity) event.getEntity();

            if (StringUtils.isBlank(entity.getAddressCode())) {
                BigDecimal maxAddressCode = userAddressBookDao.getNextGeneratedAddressCode();

                Long personId = getPersonId(entity);

                String newValue = String.format(GENERATED_LOCATION_CODE_FORMAT, LTL_LOCATION_CODE_PREFIX, SEPARATOR,
                        personId, SEPARATOR, maxAddressCode.longValue());

                entity.setAddressCode(newValue);
                changeState(event, "addressCode", entity.getAddressCode(), entity);
            }
            if (StringUtils.isBlank(entity.getAddressName())) {
                BigDecimal maxAddressCode = userAddressBookDao.getNextGeneratedAddressNameNumber();

                String newValue = String.format(GENERATED_ADDR_NAME_FORMAT, ADDR_NAME_PREFIX, maxAddressCode.longValue());

                entity.setAddressName(newValue);
                changeState(event, "addressName", entity.getAddressName(), entity);
            }
        }

        return false;
    }

    private Long getPersonId(UserAddressBookEntity entity) {
        Long personId = SecurityUtils.getCurrentPersonId();
        if (personId == null) {
            personId = entity.getModification().getCreatedBy();
        }
        return personId;
    }

    private void changeState(PreInsertEvent event, String fieldName, Object value, Object entity) {
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        Object[] state = event.getState();

        setValue(state, propertyNames, fieldName, value, entity);
    }

    private void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value, Object entity) {
        int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
        if (index >= 0) {
            currentState[index] = value;
        } else {
            LOGGER.error("Field '" + propertyToSet + "' not found on entity '" + entity.getClass().getName() + "'.");
        }
    }

}
