package com.pls.core.dao.impl.hibernate;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

import com.pls.core.domain.organization.BillingInvoiceNodeEntity;

/**
 * Hibernate Listener to fill required fields for {@link BillingInvoiceNodeEntity}.
 *
 * @author Aleksandr Leshchenko
 */
public class BillingInvoiceNodeEventListeners {
    /**
     * Sets initial Customer ID and Customer Number fields values.
     */
    public static class PreInsert implements PreInsertEventListener {
        private static final long serialVersionUID = -5633687912528421659L;

        @Override
        public boolean onPreInsert(PreInsertEvent event) {
            if (event.getEntity() instanceof BillingInvoiceNodeEntity) {
                BillingInvoiceNodeEntity node = (BillingInvoiceNodeEntity) event.getEntity();

                // Once the billing invoice  node is created, fields customerId and CustomerNumber should not be updated.
                // The customer id in billing invoice node should be “Bill To Id + Network Id”
                node.setCustomerId(node.getBillTo().getId().toString() + node.getNetworkId());
                setValue(event.getPersister(), event.getState(), "customerId", node.getCustomerId());
                // Customer number should be “Org Id”
                node.setCustomerNumber(String.valueOf(node.getBillTo().getOrganization().getId()));
                setValue(event.getPersister(), event.getState(), "customerNumber", node.getCustomerNumber());
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
