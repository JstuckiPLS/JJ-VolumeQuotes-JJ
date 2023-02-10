package com.pls.ltlrating.service.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pls.ltlrating.domain.LtlPricGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;

/**
 * Service for optimizing geo details. It allows to reduce the number of Geo Values by removing duplicates,
 * joining Zip Codes to Zip ranges, etc.
 *
 * @author Aleksandr Leshchenko
 */
@Component
public class GeoOptimizingHelper {
    private static final Pattern FIVE_DIGITS_RANGE = Pattern.compile(GeoHelper.FIVE_DIGITS_RANGE);

    /**
     * Method to optimize geo details for more optimal storage in DB.
     * 
     * @param entity
     *            price details.
     */
    public void improveGeoDetails(LtlPricingDetailsEntity entity) {
        boolean anythingChanged;
        do {
            anythingChanged = false;
            anythingChanged |= removeDuplicates(entity);
            anythingChanged |= joinGeoServices(entity);
            anythingChanged |= joinZipCodesAndRanges(entity);
        } while (anythingChanged);
    }

    /*
     * Removes one of USA -> CAN, USA -> CAN.
     * Removes one of PA,PA.
     */
    private boolean removeDuplicates(LtlPricingDetailsEntity price) {
        boolean anythingChanged = false;
        Set<LtlPricingGeoServicesEntity> geoServices = price.getGeoServices();
        geoServices.forEach(gs -> {
            removeGeoDetailsDuplicates(gs.getOriginDetails());
            removeGeoDetailsDuplicates(gs.getDestinationDetails());
        });
        Map<String, List<LtlPricingGeoServicesEntity>> map = geoServices.stream()
                .collect(Collectors.groupingBy(i -> toString(i.getOriginDetails()) + '*' + toString(i.getDestinationDetails())));
        for (List<LtlPricingGeoServicesEntity> value : map.values()) {
            if (value.size() > 1) {
                geoServices.removeAll(value.subList(1, value.size()));
                anythingChanged = true;
            }
        }
        return anythingChanged;
    }

    /*
     * Join 16055 -> USA and 16066 -> USA to 16055,16066 -> USA.
     */
    private boolean joinGeoServices(LtlPricingDetailsEntity price) {
        boolean anythingChanged = false;
        Set<LtlPricingGeoServicesEntity> geoServices = price.getGeoServices();
        Map<String, List<LtlPricingGeoServicesEntity>> originGroups = geoServices.stream()
                .collect(Collectors.groupingBy(i -> toString(i.getOriginDetails())));
        Map<String, List<LtlPricingGeoServicesEntity>> destinationGroups = geoServices.stream()
                .collect(Collectors.groupingBy(i -> toString(i.getDestinationDetails())));
        if (originGroups.size() < destinationGroups.size()) {
            for (List<LtlPricingGeoServicesEntity> geoServicesGroup : originGroups.values()) {
                if (geoServicesGroup.size() > 1) {
                    LtlPricingGeoServicesEntity firstGeoService = geoServicesGroup.get(0);
                    geoServicesGroup.listIterator(1).forEachRemaining(geoService -> {
                        geoService.getDestinationDetails().forEach(i -> i.setGeoService(firstGeoService));
                        firstGeoService.getDestinationDetails().addAll(geoService.getDestinationDetails());
                        geoServices.remove(geoService);
                    });
                    anythingChanged = true;
                }
            }
        } else if (originGroups.size() > destinationGroups.size() || destinationGroups.size() < geoServices.size()) {
            for (List<LtlPricingGeoServicesEntity> geoServicesGroup : destinationGroups.values()) {
                if (geoServicesGroup.size() > 1) {
                    LtlPricingGeoServicesEntity firstGeoService = geoServicesGroup.get(0);
                    geoServicesGroup.listIterator(1).forEachRemaining(geoService -> {
                        geoService.getOriginDetails().forEach(i -> i.setGeoService(firstGeoService));
                        firstGeoService.getOriginDetails().addAll(geoService.getOriginDetails());
                        geoServices.remove(geoService);
                    });
                    anythingChanged = true;
                }
            }
        }
        return anythingChanged;
    }

    /*
     * Replace 12345-12346,12347-12349 to 12345-12349.
     * Replace 12345-12346,12347 to 12345-12347.
     * Replace 12345,12346 to 12345-12346.
     */
    private boolean joinZipCodesAndRanges(LtlPricingDetailsEntity price) {
        boolean anythingChanged = false;
        for (LtlPricingGeoServicesEntity geoService : price.getGeoServices()) {
            anythingChanged |= joinZipCodesDetails(geoService.getOriginDetails());
            anythingChanged |= joinZipCodesDetails(geoService.getDestinationDetails());
        }
        return anythingChanged;
    }

    private String toString(Set<LtlPricGeoServiceDetailsEntity> details) {
        return details.stream().map(LtlPricGeoServiceDetailsEntity::getGeoValue).sorted().collect(Collectors.joining("_"));
    }

    /*
     * Removes one of PA,PA.
     */
    private void removeGeoDetailsDuplicates(Set<LtlPricGeoServiceDetailsEntity> details) {
        Map<String, List<LtlPricGeoServiceDetailsEntity>> map = details.stream().collect(Collectors.groupingBy(d -> d.getGeoValue()));
        map.forEach((key, value) -> {
            if (value.size() > 1) {
                details.removeAll(value.subList(1, value.size()));
            }
        });
    }

    private boolean joinZipCodesDetails(Collection<LtlPricGeoServiceDetailsEntity> originalGeoDetails) {
        boolean anythingChanged = false;
        List<LtlPricGeoServiceDetailsEntity> geoDetails = originalGeoDetails.stream().filter(this::isValidZip)
                .sorted(Comparator.comparing(LtlPricGeoServiceDetailsEntity::getGeoValue)).collect(Collectors.toList());
        if (geoDetails.size() > 1) {
            anythingChanged |= joinZipCodesToZipRange(originalGeoDetails, geoDetails);
        }
        List<LtlPricGeoServiceDetailsEntity> zipDetails = originalGeoDetails.stream().filter(this::isValidZip)
                .sorted(Comparator.comparing(LtlPricGeoServiceDetailsEntity::getGeoValue)).collect(Collectors.toList());
        List<LtlPricGeoServiceDetailsEntity> zipRangeDetails = originalGeoDetails.stream().filter(this::isValidZipRange)
                .sorted(Comparator.comparing(LtlPricGeoServiceDetailsEntity::getGeoValue)).collect(Collectors.toList());
        if (!zipDetails.isEmpty() && !zipRangeDetails.isEmpty()) {
            anythingChanged |= joinZipsToRanges(originalGeoDetails, zipDetails, zipRangeDetails);
        }
        zipRangeDetails = originalGeoDetails.stream().filter(this::isValidZipRange)
                .sorted(Comparator.comparing(LtlPricGeoServiceDetailsEntity::getGeoValue)).collect(Collectors.toList());
        if (zipRangeDetails.size() > 1) {
            anythingChanged |= joinRanges(originalGeoDetails, zipRangeDetails);
        }
        return anythingChanged;
    }

    /*
     * Replace 12345-12346,12347-12349 to 12345-12349.
     */
    private boolean joinRanges(Collection<LtlPricGeoServiceDetailsEntity> originalGeoDetails, List<LtlPricGeoServiceDetailsEntity> zipRangeDetails) {
        boolean anythingChanged = false;
        int i = 0;
        for (; i < zipRangeDetails.size() - 1; i++) {
            LtlPricGeoServiceDetailsEntity zipRange = zipRangeDetails.get(i);
            int to = getZipFromRange(zipRange.getGeoValue(), 2);
            LtlPricGeoServiceDetailsEntity nextZipRange = zipRangeDetails.get(i + 1);
            int nextFrom = getZipFromRange(nextZipRange.getGeoValue(), 1);
            int nextTo = getZipFromRange(nextZipRange.getGeoValue(), 2);
            if (to >= nextFrom - 1) {
                if (to < nextTo) {
                    zipRange.setGeoValue(replaceZipInRange(zipRange.getGeoValue(), 2, getOriginalZipFromRange(nextZipRange.getGeoValue(), 2)));
                    zipRange.setGeoValueSearchable(zipRange.getGeoValue());
                }
                originalGeoDetails.remove(nextZipRange);
                zipRangeDetails.remove(nextZipRange);
                i--;
                anythingChanged = true;
            }
        }
        return anythingChanged;
    }

    private boolean isValidZip(LtlPricGeoServiceDetailsEntity geoDetail) {
        return geoDetail.getGeoServType() == 1 && GeoHelper.isFiveDigitZipCode(geoDetail.getGeoValue());
    }

    private boolean isValidZipRange(LtlPricGeoServiceDetailsEntity geoDetail) {
        return geoDetail.getGeoServType() == 3 && GeoHelper.isFiveDigitZipRange(geoDetail.getGeoValue());
    }

    /*
     * Replace 12345-12346,12347 to 12345-12347.
     */
    private boolean joinZipsToRanges(Collection<LtlPricGeoServiceDetailsEntity> originalGeoDetails,
            List<LtlPricGeoServiceDetailsEntity> zipDetails, List<LtlPricGeoServiceDetailsEntity> zipRangeDetails) {
        boolean anythingChanged = false;
        for (LtlPricGeoServiceDetailsEntity zipRange : zipRangeDetails) {
            int from = getZipFromRange(zipRange.getGeoValue(), 1);
            int to = getZipFromRange(zipRange.getGeoValue(), 2);
            for (LtlPricGeoServiceDetailsEntity zipDetail : zipDetails) {
                int zip = Integer.parseInt(zipDetail.getGeoValue());
                if (zip + 1 == from) {
                    zipRange.setGeoValue(replaceZipInRange(zipRange.getGeoValue(), 1, zipDetail.getGeoValue()));
                    zipRange.setGeoValueSearchable(zipRange.getGeoValue());
                    originalGeoDetails.remove(zipDetail);
                    from = zip;
                    anythingChanged = true;
                } else if (zip - 1 == to) {
                    zipRange.setGeoValue(replaceZipInRange(zipRange.getGeoValue(), 2, zipDetail.getGeoValue()));
                    zipRange.setGeoValueSearchable(zipRange.getGeoValue());
                    originalGeoDetails.remove(zipDetail);
                    to = zip;
                    anythingChanged = true;
                } else if (zip <= to && zip >= from) {
                    originalGeoDetails.remove(zipDetail);
                    anythingChanged = true;
                }
            }
        }
        return anythingChanged;
    }

    private String getOriginalZipFromRange(String geoValue, int group) {
        Matcher matcher = FIVE_DIGITS_RANGE.matcher(geoValue);
        matcher.matches();
        return matcher.group(group);
    }

    private int getZipFromRange(String geoValue, int group) {
        return Integer.parseInt(getOriginalZipFromRange(geoValue, group));
    }

    private String replaceZipInRange(String geoValue, int group, String replacement) {
        Matcher matcher = FIVE_DIGITS_RANGE.matcher(geoValue);
        matcher.matches();
        if (group == 1) {
            return matcher.replaceFirst(replacement + "-$2");
        } else {
            return matcher.replaceFirst("$1-" + replacement);
        }
    }

    /*
     * Replace 12345,12346 to 12345-12346.
     */
    private boolean joinZipCodesToZipRange(Collection<LtlPricGeoServiceDetailsEntity> originalGeoDetails,
            List<LtlPricGeoServiceDetailsEntity> geoDetails) {
        boolean anythingChanged = false;
        LtlPricGeoServiceDetailsEntity detail = geoDetails.get(0);
        int zip = -1;
        if (GeoHelper.isFiveDigitZipCode(detail.getGeoValue())) {
            zip = Integer.parseInt(detail.getGeoValue());
        }
        LtlPricGeoServiceDetailsEntity lastGroupedDetail = null;
        for (int i = 1; i < geoDetails.size(); i++) {
            int newZip = -1;
            if (GeoHelper.isFiveDigitZipCode(geoDetails.get(i).getGeoValue())) {
                newZip = Integer.parseInt(geoDetails.get(i).getGeoValue());
            }
            boolean canBeGrouped = newZip - zip == 1;
            if (canBeGrouped) {
                lastGroupedDetail = geoDetails.get(i);
                originalGeoDetails.remove(geoDetails.get(i));
                anythingChanged = true;
                zip = newZip;
            }
            if (lastGroupedDetail != null && (!canBeGrouped || i == geoDetails.size() - 1)) {
                detail.setGeoServType(3);
                detail.setGeoValue(detail.getGeoValue() + "-" + lastGroupedDetail.getGeoValue());
                detail.setGeoValueSearchable(detail.getGeoValue());
            }
            if (!canBeGrouped) {
                detail = geoDetails.get(i);
                lastGroupedDetail = null;
                zip = newZip;
            }
        }
        return anythingChanged;
    }
}
