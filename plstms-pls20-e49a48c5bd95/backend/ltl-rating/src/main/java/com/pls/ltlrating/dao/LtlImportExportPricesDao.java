package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;

/**
 * LtlImportExportPricesDao class javadoc.
 *
 * @author Alex Krychenko.
 */
public interface LtlImportExportPricesDao {
    /**
     * Method that returns sub list of the prices for export.
     *
     * @param pageNumber - number of the page
     * @param pageSize   - page size
     * @return {@link List} of {@link LtlPricingItem}
     */
    List<LtlPricingItem> exportPrices(int pageNumber, int pageSize);

    /**
     * Method that returns sub list of the accessorials for export.
     *
     * @param pageNumber - number of the page
     * @param pageSize   - page size
     * @return {@link List} of {@link LtlPricingItem}
     */
    List<LtlPricingItem> exportAccessorials(int pageNumber, int pageSize);

    /**
     * Method that returns sub list of the fuel surcharges for export.
     *
     * @param pageNumber - number of the page
     * @param pageSize   - page size
     * @return {@link List} of {@link LtlPricingItem}
     */
    List<LtlPricingItem> exportFuel(int pageNumber, int pageSize);

    /**
     * Method that returns sub list of the pallet prices for export.
     *
     * @param pageNumber - number of the page
     * @param pageSize   - page size
     * @return {@link List} of {@link LtlPricingItem}
     */
    List<LtlPricingItem> exportPaletPrices(int pageNumber, int pageSize);

    /**
     * Method that tries to find existing {@link LtlPricingDetailsEntity} which is corresponds to {@link LtlPricingItem}.
     *
     * @param item - {@link LtlPricingItem} that is used as filter to search appropriate {@link LtlPricingDetailsEntity}
     * @return {@link LtlPricingDetailsEntity}
     */
    LtlPricingDetailsEntity findPricingDetailByPricingItem(LtlPricingItem item);

    /**
     * Method that tries to find existing {@link LtlAccessorialsEntity} which is corresponds to {@link LtlPricingItem}.
     *
     * @param item - {@link LtlPricingItem} that is used as filter to search appropriate {@link LtlAccessorialsEntity}
     * @return {@link LtlAccessorialsEntity}
     */
    LtlAccessorialsEntity findAccessorialByPricingItem(LtlPricingItem item);


    /**
     * Method that tries to find existing {@link LtlPalletPricingDetailsEntity} which is corresponds to {@link LtlPricingItem}.
     *
     * @param item - {@link LtlPricingItem} that is used as filter to search appropriate {@link LtlPalletPricingDetailsEntity}
     * @return {@link LtlPalletPricingDetailsEntity}
     */
    LtlPalletPricingDetailsEntity findPalletByPricingItem(LtlPricingItem item);
}
