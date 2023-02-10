package com.pls.ltlrating.batch.migration;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.processor.PriceItemProcessable;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Persister class to save(update) pricing items.
 *
 * @author Alex Krychenko.
 */
public class LtlPricingItemPersister implements ItemWriter<LtlPricingItem> {
    private static final Logger LOG = LoggerFactory.getLogger(LtlPricingItemPersister.class);

    private List<PriceItemProcessable> itemProcessors;

    @Override
    public void write(List<? extends LtlPricingItem> list) throws Exception {
        LOG.debug("Persist pricing items[size = {}]", CollectionUtils.isNotEmpty(list) ? list.size() : 0);
        for (LtlPricingItem item : list) {
            itemProcessors.stream().anyMatch(processor -> processor.processItem(item));
        }
        itemProcessors.stream().forEach(PriceItemProcessable::persist);
    }

    public void setItemProcessors(List<PriceItemProcessable> itemProcessors) {
        this.itemProcessors = itemProcessors;
    }
}
