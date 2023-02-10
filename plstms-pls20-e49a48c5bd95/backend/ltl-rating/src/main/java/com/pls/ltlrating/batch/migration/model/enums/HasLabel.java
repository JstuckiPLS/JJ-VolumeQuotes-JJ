package com.pls.ltlrating.batch.migration.model.enums;

/**
 * Interface to provide common behavior for all enums within price import/export.
 *
 * @author Alex Krychenko on 2/4/16.
 */
public interface HasLabel {
    /**
     * Method returns labell for enum value.
     *
     * @return label.
     */
    String getLabel();
}
