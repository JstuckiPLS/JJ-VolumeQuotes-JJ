package com.pls.shipment.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.shipment.domain.event.LoadStatusEvent;
import com.pls.shipment.service.edi.LoadTrackingService;

/**
 * Event listener class waits for StatusEvents to call LoadTrackingService.
 *
 * @author Yasaman Honarvar
 *
 */
@Component
public class LoadStatusEventHandler implements ApplicationListener<LoadStatusEvent> {

    @Autowired
    private LoadTrackingService loadTrackingService;

    private static final Logger LOG = LoggerFactory.getLogger(LoadStatusEventHandler.class);

    @Override
    public void onApplicationEvent(LoadStatusEvent event) {
        try {
            loadTrackingService.sendMessage(event.getLoad());
        } catch (InternalJmsCommunicationException e) {
            LOG.error("Could not sent EDI 214 to the customer", e);
        }
    }

}
