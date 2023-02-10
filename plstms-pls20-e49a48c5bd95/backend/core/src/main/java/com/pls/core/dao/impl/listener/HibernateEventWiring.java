package com.pls.core.dao.impl.listener;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pls.core.dao.impl.hibernate.AccountExecutiveEventListeners;
import com.pls.core.dao.impl.hibernate.BillingInvoiceNodeEventListeners;

/**
 * Spring component that register hibernate event listeners at the system.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
@Lazy(false)
public class HibernateEventWiring {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserAddressBookListener listener;

    /**
     * Registers hibernate listeners.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(listener);
        registry.prependListeners(EventType.PRE_INSERT, AccountExecutiveEventListeners.PreInsert.class);
        registry.prependListeners(EventType.PRE_UPDATE, AccountExecutiveEventListeners.PreUpdate.class);
        registry.prependListeners(EventType.PRE_INSERT, BillingInvoiceNodeEventListeners.PreInsert.class);
    }
}
