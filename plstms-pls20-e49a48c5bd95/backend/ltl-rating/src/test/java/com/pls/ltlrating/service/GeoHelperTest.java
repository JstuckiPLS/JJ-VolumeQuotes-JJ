package com.pls.ltlrating.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import com.pls.ltlrating.service.impl.GeoHelper;

/**
 * Integration tests for {@link GeoHelper}.
 *
 * @author Ashwini Neelgund
 */
public class GeoHelperTest {

    @Test
    public void testGetGeoServTypeCountry() {
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("USA");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("USA".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("usa");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("USA".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("MEX");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("MEX".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("mex");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("MEX".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("CAN");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("CAN".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("can");
        Assert.assertTrue(7 == geoPair.getLeft().intValue());
        Assert.assertTrue("CAN".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("GBR");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("GBR".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("gbr");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("GBR".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("GB1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("GB1".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServTypeState() {
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("CA");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("CA".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("ca");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("CA".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("AG");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("AG".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("ag");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("AG".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("AB");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("AB".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("ab");
        Assert.assertTrue(6 == geoPair.getLeft().intValue());
        Assert.assertTrue("AB".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("California");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("California".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("Aguascalientes");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("Aguascalientes".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("Alberta");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("Alberta".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("1A");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1A".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("A1".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("12");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("12".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServTypeCity() {
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("Pittsburgh");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("Pittsburgh".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("Mexico City");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("MexicoCity".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("puebla");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("puebla".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("Toronto");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("Toronto".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServTypeCompletePostalCode() {
        //USA complete valid postal code
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("16066");
        Assert.assertTrue(1 == geoPair.getLeft().intValue());
        Assert.assertTrue("16066".equalsIgnoreCase(geoPair.getRight()));

        //MEX complete valid postal code
        geoPair = GeoHelper.getGeoServType("01110");
        Assert.assertTrue(1 == geoPair.getLeft().intValue());
        Assert.assertTrue("01110".equalsIgnoreCase(geoPair.getRight()));

        //CAN complete valid postal code
        geoPair = GeoHelper.getGeoServType("V0M 1A1");
        Assert.assertTrue(1 == geoPair.getLeft().intValue());
        Assert.assertTrue("864877496549".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V0M1A1");
        Assert.assertTrue(1 == geoPair.getLeft().intValue());
        Assert.assertTrue("864877496549".equalsIgnoreCase(geoPair.getRight()));
        // Canadian postal codes can contain characters 'WZwz' except at first position.
        geoPair = GeoHelper.getGeoServType("V0M 1W1");
        Assert.assertTrue(1 == geoPair.getLeft().intValue());
        Assert.assertTrue("864877498749".equalsIgnoreCase(geoPair.getRight()));

        //Invalid USA and MEX postal codes
        geoPair = GeoHelper.getGeoServType("011102");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("011102".equalsIgnoreCase(geoPair.getRight()));

        geoPair = GeoHelper.getGeoServType("0111");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("0111".equalsIgnoreCase(geoPair.getRight()));

        //Canadian postal codes must be of format 'A#A #A#'
        geoPair = GeoHelper.getGeoServType("V0M 121");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0M121".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("10M 1A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("10M1A1".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V07 1A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V071A1".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V0N CA1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0NCA1".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V0N 1A");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0N1A".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V0N 1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0N1".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("V0N 1A1A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0N1A1A1".equalsIgnoreCase(geoPair.getRight()));

        // Canadian postal codes should not start with characters 'DFIOQUWZdfioquwz'
        geoPair = GeoHelper.getGeoServType("W0M 1A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("W0M1A1".equalsIgnoreCase(geoPair.getRight()));
        // Canadian postal codes should not contain characters 'DFIOQUdfioqu'
        geoPair = GeoHelper.getGeoServType("V0Q 1A1");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0Q1A1".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServType3DigitZip() {
        //Valid USA and MEX 3 digit postal code
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("160");
        Assert.assertTrue(2 == geoPair.getLeft().intValue());
        Assert.assertTrue("160".equalsIgnoreCase(geoPair.getRight()));

        //Valid Canadian 3 digit postal code
        geoPair = GeoHelper.getGeoServType("V0M");
        Assert.assertTrue(2 == geoPair.getLeft().intValue());
        Assert.assertTrue("864877".equalsIgnoreCase(geoPair.getRight()));

        //USA and MEX 3 digit postal codes must be all numeric
        geoPair = GeoHelper.getGeoServType("VAM");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("VAM".equalsIgnoreCase(geoPair.getRight()));
        //Canadian postal code must not start with characters 'DFIOQUWZdfioquwz'
        geoPair = GeoHelper.getGeoServType("Z0M");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("Z0M".equalsIgnoreCase(geoPair.getRight()));
        //Canadian postal codes should not contain characters 'DFIOQUdfioqu'
        geoPair = GeoHelper.getGeoServType("V0D");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V0D".equalsIgnoreCase(geoPair.getRight()));
        //Canadian postal codes must be of format 'A#A'
        geoPair = GeoHelper.getGeoServType("V02");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V02".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("1V2");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1V2".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServType3DigitZipRange() {
        //Valid USA and MEX 3 digit postal code range
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("160-687");
        Assert.assertTrue(4 == geoPair.getLeft().intValue());
        Assert.assertTrue("160-687".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("305-357");
        Assert.assertTrue(4 == geoPair.getLeft().intValue());
        Assert.assertTrue("305-357".equalsIgnoreCase(geoPair.getRight()));

        //Valid Canadian 3 digit postal code range
        geoPair = GeoHelper.getGeoServType("V2S-E5C");
        Assert.assertTrue(4 == geoPair.getLeft().intValue());
        Assert.assertTrue("865083-695367".equalsIgnoreCase(geoPair.getRight()));

        //Invalid 3 digit postal code ranges
        geoPair = GeoHelper.getGeoServType("1600-6174");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1600-6174".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("16-61");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("16-61".equalsIgnoreCase(geoPair.getRight()));
        //Canadian postal codes must be of format 'A#A-A#A'
        geoPair = GeoHelper.getGeoServType("V02-E5C");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("V02-E5C".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("1V2-E34");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1V2-E34".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("1V2M-E3C5");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1V2M-E3C5".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("1V-E3");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1V-E3".equalsIgnoreCase(geoPair.getRight()));
    }

    @Test
    public void testGetGeoServTypeCompleteZipRange() {
        //Valid USA and MEX complete postal code range
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType("16066-68711");
        Assert.assertTrue(3 == geoPair.getLeft().intValue());
        Assert.assertTrue("16066-68711".equalsIgnoreCase(geoPair.getRight()));

        //Valid Canadian complete postal code range
        geoPair = GeoHelper.getGeoServType("X1A0A1-X1A9Z9");
        Assert.assertTrue(3 == geoPair.getLeft().intValue());
        Assert.assertTrue("884965486549-884965579057".equalsIgnoreCase(geoPair.getRight()));

        //Invalid complete postal code ranges
        geoPair = GeoHelper.getGeoServType("1600-6174");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("1600-6174".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("160890-686796");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("160890-686796".equalsIgnoreCase(geoPair.getRight()));
        //Canadian postal codes must be of format 'A#A#A#-A#A#A#' and
        //cannot start with characters 'WZwz' and cannot contain
        //characters 'DFIOQUdfioqu'.
        geoPair = GeoHelper.getGeoServType("X1A0A1C5-X1A9Z9C2");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("X1A0A1C5-X1A9Z9C2".equalsIgnoreCase(geoPair.getRight()));
        geoPair = GeoHelper.getGeoServType("X1D0A1-X1A9Z9");
        Assert.assertTrue(5 == geoPair.getLeft().intValue());
        Assert.assertTrue("X1D0A1-X1A9Z9".equalsIgnoreCase(geoPair.getRight()));
    }
}
