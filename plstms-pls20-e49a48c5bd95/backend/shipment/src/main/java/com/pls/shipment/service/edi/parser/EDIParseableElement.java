package com.pls.shipment.service.edi.parser;

/**
 * Interface which describes single element from EDI document.
 *
 * @author Mikhail Boldinov, 10/02/14
 */
public interface EDIParseableElement {

    /**
     * EDI segment name.
     *
     * @return segment name
     */
    String getSegment();

    /**
     * EDI segment config name. The value set in parser config.
     *
     * @return segment config name
     */
    String getConfigName();

    /**
     * EDI element's index within EDI segment.
     *
     * @return element index
     */
    int getIndex();

    /**
     * PLS value of EDI element. Human readable value, used for logging.
     *
     * @return pls value
     */
    String getPlsValue();

    /**
     * Element's ID in Data Element Dictionary.
     *
     * @return element id
     */
    String getElementId();

    /**
     * Element mandatory flag.
     *
     * @return <code>true</code> if element is mandatory, otherwise <code>false</code>
     */
    boolean isMandatory();
}
