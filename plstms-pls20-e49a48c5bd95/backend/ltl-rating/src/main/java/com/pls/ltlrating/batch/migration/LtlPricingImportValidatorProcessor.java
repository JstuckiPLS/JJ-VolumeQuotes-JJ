package com.pls.ltlrating.batch.migration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.batch.migration.model.enums.TariffGeoType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Item processor to validate {@link LtlPricingItem} if it isn't throw attached error and it will be latter
 * processed by skip listener.
 * <p>
 * See {@link ItemProcessor}.
 *
 * @author Aleksandr Leshchenko
 */
public class LtlPricingImportValidatorProcessor implements ItemProcessor<LtlPricingItem, LtlPricingItem> {

    @Autowired
    private TariffMatcher tariffMatcher;

    private static final List<String> CANADIAN_GEO_VALUES = Arrays.asList("CAN", "NL", "PE", "NS", "NB", "QC", "ON", "MB", "SK", "AB", "BC",
            "YT", "NT", "NU");

    @Override
    public LtlPricingItem process(final LtlPricingItem item) throws Exception {
        if (!item.isValid()) {
            throw item.getError();
        }
        validateItem(item);
        processSmc3Tariff(item);
        return item;
    }

    private void validateItem(final LtlPricingItem item) throws ImportException {
        if (item.getProfileId() == null || item.getProfileDetailId() == null) {
            throw new ImportException("Item doesn't have value for profile ID or profile detail ID");
        }
        if (item.getItemType() != LtlPricingItemType.FUEL
                && (StringUtils.isBlank(item.getOrigin()) || StringUtils.isBlank(item.getDestination()))
                && (item.getItemId() == null || item.getItemId() == -1)) {
            throw new ImportException("Item doesn't have value for origin and/or destination");
        }
        if (StringUtils.equalsIgnoreCase(PricingType.BLANKET_CSP.name(), item.getProfilePriceType())) {
            throw new ImportException("Importing of Blanket/CSP tariffs is disabled");
        }
    }

    private void processSmc3Tariff(final LtlPricingItem item) throws ImportException {
        if (item.getItemType() == LtlPricingItemType.PRICE && item.getItemId() == null && StringUtils.isNotBlank(item.getItemName())) {
            item.setItemName(tariffMatcher.mapToSmc3TariffName(item.getItemName(), toTariffGeoType(item)));
        }
    }

    private TariffGeoType toTariffGeoType(final LtlPricingItem item) {
        Stream<String> geoStream = null;
        if (StringUtils.isNotBlank(item.getOrigin())) {
            geoStream = Arrays.stream(item.getOrigin().split(","));
        }
        if (StringUtils.isNotBlank(item.getDestination())) {
            Stream<String> geoStream2 = Arrays.stream(item.getDestination().split(","));
            if (geoStream == null) {
                geoStream = geoStream2;
            } else {
                geoStream = Stream.concat(geoStream, geoStream2);
            }
        }
        if (geoStream != null) {
            switch (geoStream.map(LtlPricingImportValidatorProcessor::toGeoInt).reduce((a, b) -> a | b).orElse(0)) {
                case 2:
                    return TariffGeoType.CANADA_ONLY;
                case 3:
                    return TariffGeoType.US_CANADA;
                default:
                    return TariffGeoType.US_ONLY;
            }
        }
        return TariffGeoType.US_ONLY;
    }

    private static Integer toGeoInt(final String geoValue) {
        return StringUtils.isBlank(geoValue) ? 0 : CANADIAN_GEO_VALUES.contains(geoValue) ? 2 : 1;
    }
}
