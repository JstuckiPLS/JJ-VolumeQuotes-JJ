package com.pls.invoice.domain.xml.finance;

import java.util.Date;

/**
 * Interface to setup based info table for JAXB serialization.
 *
 * @author Sergey Kirichenko
 */
public interface FinanceInfoTable {

    /**
     * Sets currency code.
     * 
     * @param currency
     *            currency value
     */
    void setCurrency(String currency);

    /**
     * sets carrier value.
     * 
     * @param carrier
     *            carrier field
     */
    void setCarrier(String carrier);

    /**
     * get Ar type.
     * 
     * @return boolean value.
     */
    Boolean isArType();

    /**
     * scac value.
     * 
     * @param scac
     *            scac value
     */
    void setScac(String scac);

    /**
     * PersonId value.
     * 
     * @param personId
     *            personId value
     */
    void setPersonId(Long personId);

    /**
     * shipment no.
     * 
     * @param shipmentNo
     *            shipment number
     */
    void setShipmentNo(String shipmentNo);

    /**
     * Sets adjustment date.
     * 
     * @param adjDate
     *            adjustment date
     */
    void setAdjDate(Date adjDate);

    /**
     * bol value.
     * 
     * @param bol
     *            bol value
     */
    void setBol(String bol);

    /**
     * customer org ID.
     * 
     * @param customerOrgId
     *            cust info
     */
    void setCustomerOrgId(Long customerOrgId);

    /**
     * Sets destination city.
     * 
     * @param destCity
     *            destination city
     */
    void setDestCity(String destCity);

    /**
     * Sets destination state.
     * 
     * @param destState
     *            destination state
     */
    void setDestState(String destState);

    /**
     * destination country code.
     * 
     * @param destCountry
     *            dest country
     */
    void setDestCountry(String destCountry);

    /**
     * Sets GL date.
     * 
     * @param glDate
     *            GL date
     */
    void setGlDate(Date glDate);

    /**
     * Sets invoice number.
     * 
     * @param invoiceNum
     *            invoice number
     */
    void setInvoiceNum(String invoiceNum);

    /**
     * Sets network id.
     * 
     * @param networkId
     *            network id
     */
    void setNetworkId(String networkId);

    /**
     * Sets origin city.
     * 
     * @param originCity
     *            origin city
     */
    void setOriginCity(String originCity);

    /**
     * Sets origin state.
     * 
     * @param originState
     *            origin state
     */
    void setOriginState(String originState);

    /**
     * origin country code.
     * 
     * @param originCountry
     *            origin country
     */
    void setOriginCountry(String originCountry);

    /**
     * Sets PRO number.
     * 
     * @param proNumber
     *            PRO number
     */
    void setProNumber(String proNumber);

    /**
     * business unit info.
     * 
     * @param businessUnit
     *            business unit
     */
    void setBusinessUnit(String businessUnit);

    /**
     * cost center details.
     * 
     * @param costCenter
     *            cost center
     */
    void setCostCenter(String costCenter);

    /**
     * department info.
     * 
     * @param department
     *            department info
     */
    void setDepartment(String department);

    /**
     * Sets Biller Type.
     * 
     * @param billerType
     *            The biller Type
     */
    void setBillerType(String billerType);

    /**
     * Sets Load ID.
     * 
     * @param loadId
     *            Load ID
     */
    void setLoadId(Long loadId);

    /**
     * Sets Request ID.
     * 
     * @param requestId
     *            Request ID
     */
    void setRequestId(String requestId);

    /**
     * Sets Adjustment - Faa Detail ID.
     * 
     * @param faaDetailId
     *            Adjustment Id
     */
    void setFaaDetailId(String faaDetailId);

}
