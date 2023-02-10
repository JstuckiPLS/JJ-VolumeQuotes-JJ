package com.pls.shipment.dao.impl.listener;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.event.LoadStatusEvent;
import com.pls.shipment.service.audit.LoadTrackingFields;

/**
 * This Listener listens to load status field changes and calls the LoadTrackingService to send 214.
 *
 * @author Yasaman Honarvar
 *
 */

@Component
public class LoadStatusListener implements PostInsertEventListener, PostUpdateEventListener {

    @Autowired
    private ApplicationEventPublisher publisher;

    private static final long serialVersionUID = 718669358740222628L;

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof LoadEntity) {
            statusInsert(event);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof LoadEntity) {
            statusUpdate(event);
        }
    }

    private void statusUpdate(PostUpdateEvent event) {
        EntityPersister persister = event.getPersister();
        String[] properties = persister.getPropertyNames();
        Object[] oldStates = event.getOldState();
        Object[] newStates = event.getState();

        for (int i = 0; i < properties.length; i++) {
            if (LoadTrackingFields.STATUS.toString().equalsIgnoreCase(properties[i]) && ObjectUtils.notEqual(oldStates[i], newStates[i])
                    && newStates[i].toString().compareTo("OPEN") != 0) {
                LoadStatusEvent ce = new LoadStatusEvent(this, (LoadEntity) event.getEntity());
                publisher.publishEvent(ce);
            }
        }
    }

    private void statusInsert(PostInsertEvent event) {
        EntityPersister persister = event.getPersister();
        String[] properties = persister.getPropertyNames();
        Object[] states = event.getState();

        for (int i = 0; i < properties.length; i++) {
            if (LoadTrackingFields.STATUS.toString().equalsIgnoreCase(properties[i]) && states[i] != null) {
                event.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcessImpl((LoadEntity) event.getEntity()));
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }

    private class AfterTransactionCompletionProcessImpl implements AfterTransactionCompletionProcess {

        private final LoadEntity load;

        AfterTransactionCompletionProcessImpl(LoadEntity load) {
            this.load = load;
        }

        @Override
        public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
            if (success) {
                LoadStatusEvent ce = new LoadStatusEvent(this, load);
                publisher.publishEvent(ce);
            }
        }
    }
}
