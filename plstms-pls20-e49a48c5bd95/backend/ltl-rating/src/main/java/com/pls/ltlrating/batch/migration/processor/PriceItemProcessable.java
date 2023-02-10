package com.pls.ltlrating.batch.migration.processor;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;

/**
 * Interface for price item processors.
 *
 * @author Alex Krychenko.
 */
public interface PriceItemProcessable {

    /**
     * Method that is called to process pricing item. If item is processed returns true, otherwise false.
     *
     * @param item - item to process.
     * @return true if item is processed
     */
    boolean processItem(LtlPricingItem item);

    /**
     * Mathod that is called to persist processed items.
     */
    void persist();
}
