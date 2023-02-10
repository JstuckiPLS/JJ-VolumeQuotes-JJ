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
 * <p>Java class for ShipmentCharge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentCharge">
 *   &lt;complexContent>
 *     &lt;extension base="{http://dayrossgroup.com/web/public/webservices/shipmentServices}DataEntity">
 *       &lt;sequence>
 *         &lt;element name="IdentityValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceLevelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Sequence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ChargeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Currency" type="{http://dayrossgroup.com/web/public/webservices/shipmentServices}Currency"/>
 *         &lt;element name="AlternateExchangeRate" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="AlternateCurrency" type="{http://dayrossgroup.com/web/public/webservices/shipmentServices}Currency"/>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpectedDeliveryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="TransitTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentCharge", propOrder = {
    "identityValue",
    "serviceLevelCode",
    "sequence",
    "chargeCode",
    "description",
    "amount",
    "currency",
    "alternateExchangeRate",
    "alternateCurrency",
    "result",
    "additionalInfo",
    "expectedDeliveryDate",
    "transitTime",
    "totalAmount"
})
public class ShipmentCharge
    extends DataEntity
{

    @XmlElement(name = "IdentityValue")
    protected String identityValue;
    @XmlElement(name = "ServiceLevelCode")
    protected String serviceLevelCode;
    @XmlElement(name = "Sequence")
    protected int sequence;
    @XmlElement(name = "ChargeCode")
    protected String chargeCode;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Amount", required = true)
    protected BigDecimal amount;
    @XmlElement(name = "Currency", required = true)
    protected Currency currency;
    @XmlElement(name = "AlternateExchangeRate", required = true)
    protected BigDecimal alternateExchangeRate;
    @XmlElement(name = "AlternateCurrency", required = true)
    protected Currency alternateCurrency;
    @XmlElement(name = "Result")
    protected String result;
    @XmlElement(name = "AdditionalInfo")
    protected String additionalInfo;
    @XmlElement(name = "ExpectedDeliveryDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expectedDeliveryDate;
    @XmlElement(name = "TransitTime")
    protected String transitTime;
    @XmlElement(name = "TotalAmount", required = true)
    protected BigDecimal totalAmount;

    /**
     * Gets the value of the identityValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityValue() {
        return identityValue;
    }

    /**
     * Sets the value of the identityValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityValue(String value) {
        this.identityValue = value;
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
     * Gets the value of the sequence property.
     * 
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     */
    public void setSequence(int value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the chargeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargeCode() {
        return chargeCode;
    }

    /**
     * Sets the value of the chargeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargeCode(String value) {
        this.chargeCode = value;
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
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

    /**
     * Gets the value of the alternateExchangeRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAlternateExchangeRate() {
        return alternateExchangeRate;
    }

    /**
     * Sets the value of the alternateExchangeRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAlternateExchangeRate(BigDecimal value) {
        this.alternateExchangeRate = value;
    }

    /**
     * Gets the value of the alternateCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getAlternateCurrency() {
        return alternateCurrency;
    }

    /**
     * Sets the value of the alternateCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setAlternateCurrency(Currency value) {
        this.alternateCurrency = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInfo(String value) {
        this.additionalInfo = value;
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

}