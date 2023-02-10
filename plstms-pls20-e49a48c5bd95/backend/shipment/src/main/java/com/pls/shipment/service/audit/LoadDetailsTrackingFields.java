package com.pls.shipment.service.audit;

import org.apache.commons.lang3.StringUtils;

import com.pls.shipment.domain.LoadDetailsEntity;


/**
 * Fields of {@link LoadDetailsEntity} what should be logged by system.
 *
 * @author Artem Arapov
 *
 */
public enum LoadDetailsTrackingFields {

    ADDRESS("address", "Address"),
    CONTACT("contact", "Contact"),
    DEPARTURE("departure", "Date"),
    SCHEDULEDPICKUP("earlyScheduledArrival", "Scheduled Pickup");


    private String name;

    private String description;

    LoadDetailsTrackingFields(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Returns field description.
     *
     * @return description of field.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks that specified field name exists.
     *
     * @param name - field name.
     * @return <code>TRUE</code> if specified field was found, otherwise returns <code>FALSE</code>
     */
    public static boolean containField(String name) {
        return getByFieldName(name) != null;
    }

    /**
     * Find {@link LoadDetailsTrackingFields} by specified field name.
     *
     * @param name - field name
     * @return {@link LoadDetailsTrackingFields} if specified field name was found, otherwise returns <code>null</code>.
     */
    public static LoadDetailsTrackingFields getByFieldName(String name) {
        LoadDetailsTrackingFields result = null;

        for (LoadDetailsTrackingFields field : LoadDetailsTrackingFields.values()) {
            if (StringUtils.equalsIgnoreCase(field.getName(), name)) {
                result = field;
            }
        }

        return result;
    }
}
