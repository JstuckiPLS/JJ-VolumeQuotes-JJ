package com.pls.shipment.dao.impl.hibernate;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pls.shipment.dao.impl.listener.LoadEventListeners;
import com.pls.shipment.dao.impl.listener.LoadStatusListener;


/**
 * Hibernate Events.
 *
 * @author Artem Arapov
 *
 */
@Component
@Lazy(false)
public class HibernateEvents {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private LoadStatusListener loadStatusListener;

    /**
     * Registering events.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        registry.appendListeners(EventType.POST_INSERT, LoadEventListeners.PostInsertListener.class);
        registry.appendListeners(EventType.POST_UPDATE, LoadEventListeners.PostUpdateListener.class);
        registry.appendListeners(EventType.POST_INSERT, loadStatusListener);
        registry.appendListeners(EventType.POST_UPDATE, loadStatusListener);
    }
}
