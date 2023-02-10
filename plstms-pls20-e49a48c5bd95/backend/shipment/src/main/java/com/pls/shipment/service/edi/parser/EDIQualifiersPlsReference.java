package com.pls.shipment.service.edi.parser;

/**
 * Interface for PLS references of EDI qualifiers.
 *
 * @author Mikhail Boldinov, 04/03/14
 */
public interface EDIQualifiersPlsReference {

    /**
     * Gets qualifier PLS reference.
     *
     * @return qualifier PLS reference
     */
    String getName();

    /**
     * Gets default qualifier value for PLS qualifier reference.
     *
     * @return default qualifier value
     */
    String getDefaultValue();
}
