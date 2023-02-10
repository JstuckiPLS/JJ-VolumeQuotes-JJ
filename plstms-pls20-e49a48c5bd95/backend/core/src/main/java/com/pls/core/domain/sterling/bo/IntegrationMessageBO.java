package com.pls.core.domain.sterling.bo;

import java.io.Serializable;

/**
 * Sterling integration BO.
 * 
 * @author Jasmin Dhamelia
 *
 */
public interface IntegrationMessageBO extends Serializable {

    /**
     * Type of the message.
     * 
     * @return MessageType
     */
    String getMessageType();

    /**
     * PersonId value.
     * 
     * @return PersonId
     */
    Long getPersonId();

    /**
     * scac value.
     * 
     * @return scac
     */
    String getScac();

    /**
     * scac value.
     * 
     * @param scac
     *            scac value
     */
    void setScac(String scac);

    /**
     * shipment no.
     * 
     * @return shipment no
     */
    String getShipmentNo();

    /**
     * shipment no.
     * 
     * @param shipmentNo
     *            shipment number
     */
    void setShipmentNo(String shipmentNo);

    /**
     * Load Id.
     * 
     * @return load id
     */
    Long getLoadId();

    /**
     * load ID.
     * 
     * @param loadId
     *            load id
     */
    void setLoadId(Long loadId);

    /**
     * bol value.
     * 
     * @return bol
     */
    String getBol();

    /**
     * bol value.
     * 
     * @param bol
     *            bol value
     */
    void setBol(String bol);

    /**
     * Cust org id.
     * 
     * @return cust info
     */
    Long getCustomerOrgId();

    /**
     * customer org ID.
     * 
     * @param customerOrgId cust info
     */
    void setCustomerOrgId(Long customerOrgId);
}
