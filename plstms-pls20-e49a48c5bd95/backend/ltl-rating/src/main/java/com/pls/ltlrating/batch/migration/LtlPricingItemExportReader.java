package com.pls.ltlrating.batch.migration;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.dao.LtlImportExportPricesDao;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Custom spring batch item reader that collects data from DB and then transform it to the {@link LtlPricingItem} for export purpose.
 *
 * @author Alex Krychenko.
 */
@Transactional(readOnly = true)
public class LtlPricingItemExportReader extends AbstractPaginatedDataItemReader<LtlPricingItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LtlPricingItemExportReader.class);

    @Autowired
    private LtlImportExportPricesDao importExportPricesDao;

    private int sourceIndex = 0;

    @Override
    protected Iterator<LtlPricingItem> doPageRead() {
        List<LtlPricingItem> ltlPricingItems = null;
        switch (sourceIndex) {
            case 0:
                ltlPricingItems = importExportPricesDao.exportPrices(page, pageSize);
                break;
            case 1:
                ltlPricingItems = importExportPricesDao.exportAccessorials(page, pageSize);
                break;
            case 2:
                ltlPricingItems = importExportPricesDao.exportFuel(page, pageSize);
                break;
            case 3:
                ltlPricingItems = importExportPricesDao.exportPaletPrices(page, pageSize);
                break;
            default:
                LOGGER.info("All sources were processed.");
                break;
        }
        if (CollectionUtils.isNotEmpty(ltlPricingItems)) {
            if (ltlPricingItems.size() < pageSize) {
                page = -1;
                sourceIndex++;
            }
            return ltlPricingItems.iterator();
        }
        sourceIndex = 0;
        return null;
    }
}
