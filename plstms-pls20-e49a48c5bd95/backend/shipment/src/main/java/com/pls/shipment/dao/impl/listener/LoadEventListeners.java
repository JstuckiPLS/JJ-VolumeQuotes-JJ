package com.pls.shipment.dao.impl.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import com.google.common.base.Optional;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.service.audit.LoadDetailsTrackingFields;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.audit.LoadTrackingFields;

/**
 * Event listeners to track load events.
 * 
 * @author Artem Arapov
 *
 */
public class LoadEventListeners {

    /**
     * Post Update Listener.
     * 
     * @author Artem Arapov
     *
     */
    public static class PostUpdateListener implements PostUpdateEventListener {

        private static final long serialVersionUID = 280675561800992612L;

        @Override
        public void onPostUpdate(PostUpdateEvent event) {
            if (event.getEntity() instanceof LoadEntity) {
                loadEntityPostUpdate(event);
            } else if (event.getEntity() instanceof LoadDetailsEntity) {
                loadDetailsEntityPostUpdate(event);
            }
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }

        private void loadEntityPostUpdate(PostUpdateEvent event) {
            List<LoadEventEntity> eventsList = buildLoadEntityPostUpdateEvents(event);
            if (!eventsList.isEmpty()) {
                event.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcessImpl(eventsList));
            }
        }

        private void loadDetailsEntityPostUpdate(PostUpdateEvent event) {
            List<LoadEventEntity> eventsList = buildLoadDetailsEntityPostUpdate(event);
            if (!eventsList.isEmpty()) {
                event.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcessImpl(eventsList));
            }
        }

        private List<LoadEventEntity> buildLoadEntityPostUpdateEvents(PostUpdateEvent event) {
            EntityPersister persister = event.getPersister();
            String[] properties = persister.getPropertyNames();
            Object[] oldStates = event.getOldState();
            Object[] newStates = event.getState();

            List<LoadEventEntity> eventsList = new ArrayList<LoadEventEntity>();

            for (int i = 0; i < properties.length; i++) {
                if (LoadTrackingFields.containsField(properties[i]) && ObjectUtils.notEqual(oldStates[i], newStates[i])) {
                    LoadEntity entity = (LoadEntity) event.getEntity();
                    LoadTrackingFields field = LoadTrackingFields.getByFieldName(properties[i]);
                    List<LoadEventEntity> eventEntityList =
                            LoadEventBuilder.buildLoadEventList(entity, event.getSession(), field, oldStates[i], newStates[i]);

                    if (eventEntityList != null && !eventEntityList.isEmpty()) {
                        eventsList.addAll(eventEntityList);
                    }
                }
                
                if ("numbers".equalsIgnoreCase(properties[i])) {
                    LoadEntity loadEntity = (LoadEntity) event.getEntity();
                    LoadNumbersEntity oldValue = (LoadNumbersEntity)oldStates[i];
                    LoadNumbersEntity newValue = (LoadNumbersEntity)newStates[i];
                    
                    if (!StringUtils.equals(oldValue.getCarrierQuoteNumber(), newValue.getCarrierQuoteNumber())) {
                        eventsList.add(LoadEventBuilder.buildQuoteNumberEvent(loadEntity.getId(), newValue.getCarrierQuoteNumber()));
                    }
                }
            }
            return eventsList;
        }

        private List<LoadEventEntity> buildLoadDetailsEntityPostUpdate(PostUpdateEvent event) {
            EntityPersister persister = event.getPersister();
            String[] properties = persister.getPropertyNames();
            Object[] oldStates = event.getOldState();
            Object[] newStates = event.getState();
            List<LoadEventEntity> eventsList = new ArrayList<LoadEventEntity>();

            for (int i = 0; i < properties.length; i++) {
                if (LoadDetailsTrackingFields.containField(properties[i]) && doesFieldChanged(oldStates[i], newStates[i])) {
                    LoadDetailsTrackingFields field = LoadDetailsTrackingFields.getByFieldName(properties[i]);
                    LoadDetailsEntity loadDetail = (LoadDetailsEntity) event.getEntity();
                    Optional<LoadEventEntity> optionalEvent = LoadEventBuilder.buildLoadDetailsEvent(loadDetail, field, oldStates[i], newStates[i]);

                    if (optionalEvent.isPresent()) {
                        eventsList.add(optionalEvent.get());
                    }
                }
            }

            return eventsList;
        }
    }

    /**
     * Post Insert Listener.
     * 
     * @author Artem Arapov
     *
     */
    public static class PostInsertListener implements PostInsertEventListener {

        private static final long serialVersionUID = -8869744348470314468L;

        private static final String SOURCE_FIELD = "sourceInd";

        @Override
        public void onPostInsert(PostInsertEvent event) {
            if (event.getEntity() instanceof LoadEntity) {
                final List<LoadEventEntity> eventsList = loadEntityPostInsert(event);
                if (!eventsList.isEmpty()) {
                    event.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcessImpl(eventsList));
                }
            }
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }

        private List<LoadEventEntity> loadEntityPostInsert(PostInsertEvent event) {
            List<LoadEventEntity> eventsList = new ArrayList<LoadEventEntity>();
            EntityPersister persister = event.getPersister();
            String[] properties = persister.getPropertyNames();
            Object[] states = event.getState();

            for (int i = 0; i < properties.length; i++) {
                if (SOURCE_FIELD.equalsIgnoreCase(properties[i]) && states[i] != null) {
                    LoadEntity loadEntity = (LoadEntity) event.getEntity();
                    String source = ObjectUtils.toString(states[i]);

                    LoadEventEntity eventEntity = LoadEventBuilder.buildSourceEvent(loadEntity.getId(), source);
                    eventsList.add(eventEntity);

                    if (source.equalsIgnoreCase("EDI")) {
                        Optional<LoadEventEntity> vendorBillEvent = LoadEventBuilder.buildInitialVendorBillEvent(loadEntity);
                        if (vendorBillEvent.isPresent()) {
                            eventsList.add(vendorBillEvent.get());
                        }
                    }

                    if (loadEntity.getNumbers() != null && loadEntity.getNumbers().getCarrierQuoteNumber() != null) {
                        eventsList.add(LoadEventBuilder.buildQuoteNumberEvent(loadEntity.getId(), loadEntity.getNumbers().getCarrierQuoteNumber()));
                    }
                }
            }

            return eventsList;
        }
    }

    private static class AfterTransactionCompletionProcessImpl implements AfterTransactionCompletionProcess {

        private List<LoadEventEntity> eventsList;

        AfterTransactionCompletionProcessImpl(List<LoadEventEntity> eventsList) {
            this.eventsList = eventsList;
        }

        @Override
        public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
            if (success) {
                saveListOfEvents(eventsList, session.getFactory().getCurrentSession());
            }
        }
    }

    private static boolean doesFieldChanged(Object oldValue, Object newValue) {
        if (oldValue instanceof Date && newValue instanceof Date) {
            Date oldDate = (Date) oldValue;
            Date newDate = (Date) newValue;
            return !DateUtils.truncatedEquals(oldDate, newDate, Calendar.DATE);
        } else {
            return ObjectUtils.notEqual(oldValue, newValue);
        }
    }

    private static void saveListOfEvents(List<LoadEventEntity> eventList, Session session) {
        CacheMode mode = session.getCacheMode();
        session.setCacheMode(CacheMode.IGNORE);
        try {
            for (LoadEventEntity event : eventList) {
                session.save(event);
            }
            session.flush();
        } finally {
            session.setCacheMode(mode);
        }
    }
}
