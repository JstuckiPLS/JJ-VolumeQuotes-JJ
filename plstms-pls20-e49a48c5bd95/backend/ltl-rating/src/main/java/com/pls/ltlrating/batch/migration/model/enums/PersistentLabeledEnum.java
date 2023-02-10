package com.pls.ltlrating.batch.migration.model.enums;

/**
 * Interface to provide common behavior for all enums within price import/export which can be persisted.
 *
 * @param <E> generic for persistent enum.
 * @author Alex Krychenko
 */
public interface PersistentLabeledEnum<E extends Enum> extends HasLabel {

    /**
     * Method returns reference to persistent enum instance.
     *
     * @return persistent enum instance
     */
    E getPersistentEnum();
}
