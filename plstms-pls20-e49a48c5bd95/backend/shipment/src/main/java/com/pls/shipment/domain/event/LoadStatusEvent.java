package com.pls.shipment.domain.event;

import org.springframework.context.ApplicationEvent;

import com.pls.shipment.domain.LoadEntity;

/**
 * This is the event which is created to notify listener an Status Change has occured.
 *
 * @author Yasaman Honarvar
 *
 */

public class LoadStatusEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2736657443238877567L;

    private final LoadEntity load;

    /**
     * The constructor for Event entity which calls ApplicationEvent constructor first and then sets the load value.
     *
     * @param source the source publisher object of the event.
     * @param load the load related to the event.
     */
    public LoadStatusEvent(Object source, LoadEntity load) {
        super(source);
        this.load = load;
    }

    public LoadEntity getLoad() {
        return load;
    }
}
