package com.pls.ltlrating.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.pls.ltlrating.domain.LtlPricGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;
import com.pls.ltlrating.service.impl.GeoHelper;
import com.pls.ltlrating.service.impl.GeoOptimizingHelper;

/**
 * Test for {@link GeoOptimizingHelper}.
 * 
 * @author Aleksandr Leshchenko.
 */
public class GeoOptimizingHelperTest {

    private GeoOptimizingHelper helper = new GeoOptimizingHelper();

    @Test
    public void shouldOptimizeToSingleGeoService() {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();
        entity.getGeoServices().add(createGeoService("12345,12344,PA,12340-12343,12341-12342,12339,USA,USA,PA,12347,12348,CAN|"
                + "12345,12344,PA,12340-12343,12341,USA,USA,PA,12347,12348,CAN"));
        entity.getGeoServices().add(createGeoService("12339-12345,12347-12348,CAN,PA,USA|12344-12346"));
        entity.getGeoServices().add(createGeoService("12339-12345,12347-12348,CAN,PA,USA|12344-12346"));

        helper.improveGeoDetails(entity);

        verifyResult(entity, "12339-12345,12347-12348,CAN,PA,USA|12340-12348,CAN,PA,USA");
    }

    @Test
    public void shouldOptimizeMultipleGeoValues() {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();
        entity.getGeoServices().add(createGeoService("12339-12345,12347-12348,CAN,PA,USA|12344-12347"));
        entity.getGeoServices().add(createGeoService("12339-12344,12345|12344-12347"));
        entity.getGeoServices().add(createGeoService("12321-12359,12360|12345,12346"));
        entity.getGeoServices().add(createGeoService("12361|12345-12346"));

        helper.improveGeoDetails(entity);

        verifyResult(entity, "12321-12361|12345-12346\n12339-12345,12347-12348,CAN,PA,USA|12344-12347");
    }

    @Test
    public void shouldOptimizeCountries() {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();
        entity.getGeoServices().add(createGeoService("USA|USA"));
        entity.getGeoServices().add(createGeoService("CAN|CAN"));
        entity.getGeoServices().add(createGeoService("USA|CAN"));
        entity.getGeoServices().add(createGeoService("CAN|USA"));

        helper.improveGeoDetails(entity);

        verifyResult(entity, "CAN,USA|CAN,USA");
    }

    private void verifyResult(LtlPricingDetailsEntity entity, String expectedResult) {
        String actualResult = entity.getGeoServices().stream()
                .map(service -> toString(service.getOriginDetails()) + "|" + toString(service.getDestinationDetails())).sorted()
                .collect(Collectors.joining("\n"));
        Assert.assertEquals(expectedResult, actualResult);

        for (LtlPricingGeoServicesEntity geoService : entity.getGeoServices()) {
            checkGeoDetails(geoService.getOriginDetails());
            checkGeoDetails(geoService.getDestinationDetails());
        }
    }

    private void checkGeoDetails(Set<LtlPricGeoServiceDetailsEntity> geoDetails) {
        for (LtlPricGeoServiceDetailsEntity geoDetail : geoDetails) {
            Assert.assertEquals(geoDetail.getGeoValue(), GeoHelper.getGeoServType(geoDetail.getGeoValue()).getKey(),
                    Integer.valueOf(geoDetail.getGeoServType()));
            Assert.assertEquals(geoDetail.getGeoValue(), geoDetail.getGeoValueSearchable());
        }
    }

    private String toString(Set<LtlPricGeoServiceDetailsEntity> details) {
        return details.stream().map(LtlPricGeoServiceDetailsEntity::getGeoValue).sorted().collect(Collectors.joining(","));
    }

    private LtlPricingGeoServicesEntity createGeoService(String geoDetails) {
        LtlPricingGeoServicesEntity geoService = new LtlPricingGeoServicesEntity();
        geoService.setOriginDetails(getGeoDetails(geoDetails.split("\\|")[0]));
        geoService.setDestinationDetails(getGeoDetails(geoDetails.split("\\|")[1]));
        return geoService;
    }

    private HashSet<LtlPricGeoServiceDetailsEntity> getGeoDetails(String geoDetails) {
        HashSet<LtlPricGeoServiceDetailsEntity> geoDetailsSet = new HashSet<>();
        Stream.of(geoDetails.split(",")).forEach(geoDetail -> geoDetailsSet.add(getGeoDetail(geoDetail)));
        return geoDetailsSet;
    }

    private LtlPricGeoServiceDetailsEntity getGeoDetail(String geoValue) {
        LtlPricGeoServiceDetailsEntity geoDetail = new LtlPricGeoServiceDetailsEntity();
        geoDetail.setGeoValue(geoValue);
        geoDetail.setGeoValueSearchable(geoValue);
        geoDetail.setGeoServType(GeoHelper.getGeoServType(geoValue).getKey());
        return geoDetail;
    }
}
