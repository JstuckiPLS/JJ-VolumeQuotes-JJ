package com.pls.shipment.service.impl.edi.utils;

import com.pls.core.exception.EDIValidationException;
import com.pls.shipment.domain.enums.EDITransactionSet;
import org.pb.x12.Loop;

import java.util.List;

/**
 * EDI files validation utils.
 *
 * @author Mikhail Boldinov, 06/03/14
 */
public final class EDIValidationUtils {
    private EDIValidationUtils() {
    }

    /**
     * Checks that {@link Loop} contains child loop(s).
     *
     * @param parentLoop     loop to find child loop(s) in
     * @param loopConfigName config name of loop to find
     * @param segmentId      name of loop to find
     * @throws EDIValidationException thrown if the check is not passed
     */
    public static void validateLoopsExist(Loop parentLoop, String loopConfigName, String segmentId) throws EDIValidationException {
        List<Loop> loops = parentLoop.findLoop(loopConfigName);
        if (loops == null) {
            throw new EDIValidationException(String.format("No '%s' segments found.", segmentId));
        }
    }

    /**
     * Checks that {@link Loop} contains child loops and their count is the same as expected.
     *
     * @param parentLoop     loop to find child loop(s) in
     * @param loopConfigName config name of loop to find
     * @param segmentId      name of loop to find
     * @param expectedCount  expected loop(s) count
     * @throws EDIValidationException thrown if the check is not passed
     */
    public static void validateLoopsCount(Loop parentLoop, String loopConfigName, String segmentId, int expectedCount) throws EDIValidationException {
        validateLoopsExist(parentLoop, loopConfigName, segmentId);
        List<Loop> loops = parentLoop.findLoop(loopConfigName);
        if (loops.size() != expectedCount) {
            throw new EDIValidationException(String.format("Invalid '%s' segment count: %s", segmentId, loops.size()));
        }
    }

    /**
     * Checks that ISA and GS segments have expected carrier SCAC code.
     *
     * @param scac     expected SCAC
     * @param isaValue SCAC of ISA segment
     * @param gsValue  SCAC of GS segment
     * @throws EDIValidationException thrown if the check is not passed
     */
    public static void validateScac(String scac, String isaValue, String gsValue) throws EDIValidationException {
        validateElement(scac, isaValue, String.format("'ISA' segment has invalid SCAC code: expected '%s', found '%s'", scac, isaValue));
        validateElement(scac, gsValue, String.format("'GS' segment has invalid SCAC code: expected '%s', found '%s'", scac, gsValue));
    }

    /**
     * Checks that actual element value is the same as expected one.
     *
     * @param expectedValue expected element value
     * @param actualValue   actual element value
     * @param errorMessage  error message to throw if check is not passed
     * @throws EDIValidationException thrown if the check is not passed
     */
    public static void validateElement(String expectedValue, String actualValue, String errorMessage) throws EDIValidationException {
        if (!expectedValue.equals(actualValue)) {
            throw new EDIValidationException(errorMessage);
        }
    }

    /**
     * Checks that transaction set has correct value.
     *
     * @param expected expected transaction set id
     * @param actual   actual transaction set id
     * @throws EDIValidationException thrown if the check is not passed
     */
    public static void validateTransactionSet(EDITransactionSet expected, String actual) throws EDIValidationException {
        validateElement(expected.getId(), actual,
                String.format("Transaction set ID validation failed: expected '%s', found '%s'", expected.getId(), actual));
    }
}
