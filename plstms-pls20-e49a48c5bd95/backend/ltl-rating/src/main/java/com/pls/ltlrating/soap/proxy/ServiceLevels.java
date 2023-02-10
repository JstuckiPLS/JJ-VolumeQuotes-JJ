//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.23 at 04:27:03 PM EEST 
//


package com.pls.ltlrating.soap.proxy;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ServiceLevels complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceLevels">
 *   &lt;complexContent>
 *     &lt;extension base="{http://dayrossgroup.com/web/public/webservices/shipmentServices}DataMapping">
 *       &lt;sequence>
 *         &lt;element name="Division" type="{http://dayrossgroup.com/web/public/webservices/shipmentServices}Division"/>
 *         &lt;element name="ServiceLevelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ShipmentCharges" type="{http://dayrossgroup.com/web/public/webservices/shipmentServices}ArrayOfShipmentCharge" minOccurs="0"/>
 *         &lt;element name="ExpectedDeliveryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="TotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="TransitTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevels", propOrder = {
    "division",
    "serviceLevelCode",
    "description",
    "shipmentCharges",
    "expectedDeliveryDate",
    "totalAmount",
    "transitTime"
})
public class ServiceLevels
    extends DataMapping
{

    @XmlElement(name = "Division", required = true)
    protected Division division;
    @XmlElement(name = "ServiceLevelCode")
    protected String serviceLevelCode;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "ShipmentCharges")
    protected ArrayOfShipmentCharge shipmentCharges;
    @XmlElement(name = "ExpectedDeliveryDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expectedDeliveryDate;
    @XmlElement(name = "TotalAmount", required = true)
    protected BigDecimal totalAmount;
    @XmlElement(name = "TransitTime")
    protected String transitTime;

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link Division }
     *     
     */
    public Division getDivision() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link Division }
     *     
     */
    public void setDivision(Division value) {
        this.division = value;
    }

    /**
     * Gets the value of the serviceLevelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceLevelCode() {
        return serviceLevelCode;
    }

    /**
     * Sets the value of the serviceLevelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceLevelCode(String value) {
        this.serviceLevelCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the shipmentCharges property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfShipmentCharge }
     *     
     */
    public ArrayOfShipmentCharge getShipmentCharges() {
        return shipmentCharges;
    }

    /**
     * Sets the value of the shipmentCharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfShipmentCharge }
     *     
     */
    public void setShipmentCharges(ArrayOfShipmentCharge value) {
        this.shipmentCharges = value;
    }

    /**
     * Gets the value of the expectedDeliveryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    /**
     * Sets the value of the expectedDeliveryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpectedDeliveryDate(XMLGregorianCalendar value) {
        this.expectedDeliveryDate = value;
    }

    /**
     * Gets the value of the totalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the value of the totalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalAmount(BigDecimal value) {
        this.totalAmount = value;
    }

    /**
     * Gets the value of the transitTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransitTime() {
        return transitTime;
    }

    /**
     * Sets the value of the transitTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransitTime(String value) {
        this.transitTime = value;
    }

}
