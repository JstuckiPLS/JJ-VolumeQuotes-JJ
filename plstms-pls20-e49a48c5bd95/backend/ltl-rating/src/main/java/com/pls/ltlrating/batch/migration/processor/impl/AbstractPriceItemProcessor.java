package com.pls.ltlrating.batch.migration.processor.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.Identifiable;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemStatus;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.batch.migration.processor.PriceItemProcessable;

/**
 * Common implementation of price item processor.
 *
 * @param <T> generic for entity type.
 *
 * @author Alex Krychenko
 */
public abstract class AbstractPriceItemProcessor<T extends Identifiable<Long>> implements PriceItemProcessable {

    private Map<LtlPricingItem, T> itemToEntityMap = new HashMap<>();

    private final LtlPricingItemType pricingItemType;

    /**
     * Constructor accept which tipe of pricing item should be processed by this processor.
     *
     * @param pricingItemType - type of pricing item
     */
    public AbstractPriceItemProcessor(final LtlPricingItemType pricingItemType) {
        this.pricingItemType = pricingItemType;
    }


    @Override
    public final boolean processItem(final LtlPricingItem item) {
        if (item != null && item.getItemType() == pricingItemType) {
            if (item.getItemId() == null || item.getStatus() == LtlPricingItemStatus.INACTIVE || item.getEffectiveTo() != null) {
                doProcess(getOrCreateEntity(item), item);
            }
            return true;
        }
        return false;
    }

    @Override
    public final void persist() {
        if (MapUtils.isNotEmpty(itemToEntityMap)) {
            getDaoToPersist().saveOrUpdateBatch(itemToEntityMap.values());
        }
        itemToEntityMap.clear();
    }

    /**
     * Abstract method to convert pricing item to specified entity type.
     *
     * @param pricingItem - pricing item to convert.
     * @return {@link T} converted entity
     */
    protected abstract T toEntity(LtlPricingItem pricingItem);

    /**
     * Abstract method to process converted from pricing item entity.
     * @param entity - converted entity
     * @param item - origin pricing item
     */
    protected abstract void doProcess(T entity, LtlPricingItem item);

    /**
     * Abstract method to return instance of DAO for persistence.
     * @return instance of DAO for persistence
     */
    protected abstract AbstractDao<T, Long> getDaoToPersist();

    /**
     * Abstract method to find {@link T} entity by pricing item.
     * @param item - pricing item as a filter param
     * @return {@link T} correspond entity
     */
    protected abstract T findEntityInDb(LtlPricingItem item);

    private T getOrCreateEntity(final LtlPricingItem item) {
        T entity = findEntity(item);
        if (entity == null) {
            entity = toEntity(item);
            itemToEntityMap.put(item, entity);
        }
        return entity;
    }

    private T findEntity(final LtlPricingItem item) {
        T entity;
        if (itemToEntityMap.containsKey(item)) {
            entity = itemToEntityMap.get(item);
        } else {
            if (item.getItemId() != null && item.getItemId() > 0) {
                entity = getDaoToPersist().find(item.getItemId());
                Assert.notNull(entity, String.format("Item[%s] with id[%s] doesn't exist", item.getItemType().getLabel(), item.getItemId()));
            } else {
                entity = findEntityInDb(item);
            }
            if (entity != null) {
                itemToEntityMap.put(item, entity);
            }
        }
        return entity;
    }
}
