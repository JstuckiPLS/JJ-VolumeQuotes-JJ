package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CallContext complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallContext", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", propOrder = {
    "company",
    "language",
    "logonAsUser",
    "messageId",
    "partitionKey",
    "propertyBag"
})
@SuppressWarnings("PMD")
public class CallContext {

    @XmlElementRef(name = "Company", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", type
        = JAXBElement.class)
    protected JAXBElement<String> company;
    @XmlElementRef(name = "Language", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", type
        = JAXBElement.class)
    protected JAXBElement<String> language;
    @XmlElementRef(name = "LogonAsUser", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", type
        = JAXBElement.class)
    protected JAXBElement<String> logonAsUser;
    @XmlElementRef(name = "MessageId", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", type
        = JAXBElement.class)
    protected JAXBElement<String> messageId;
    @XmlElementRef(name = "PartitionKey", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts",
        type = JAXBElement.class)
    protected JAXBElement<String> partitionKey;
    @XmlElementRef(name = "PropertyBag", namespace = "http://schemas.microsoft.com/dynamics/2010/01/datacontracts", type
        = JAXBElement.class)
    protected JAXBElement<ArrayOfKeyValueOfstringstring> propertyBag;

    /**
     * Gets the value of the company property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setCompany(JAXBElement<String> value) {
        this.company = value;
    }

    /**
     * Gets the value of the language property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setLanguage(JAXBElement<String> value) {
        this.language = value;
    }

    /**
     * Gets the value of the logonAsUser property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getLogonAsUser() {
        return logonAsUser;
    }

    /**
     * Sets the value of the logonAsUser property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setLogonAsUser(JAXBElement<String> value) {
        this.logonAsUser = value;
    }

    /**
     * Gets the value of the messageId property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setMessageId(JAXBElement<String> value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the partitionKey property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getPartitionKey() {
        return partitionKey;
    }

    /**
     * Sets the value of the partitionKey property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setPartitionKey(JAXBElement<String> value) {
        this.partitionKey = value;
    }

    /**
     * Gets the value of the propertyBag property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link ArrayOfKeyValueOfstringstring }{@code >}
     *
     */
    public JAXBElement<ArrayOfKeyValueOfstringstring> getPropertyBag() {
        return propertyBag;
    }

    /**
     * Sets the value of the propertyBag property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link ArrayOfKeyValueOfstringstring }{@code >}
     *
     */
    public void setPropertyBag(JAXBElement<ArrayOfKeyValueOfstringstring> value) {
        this.propertyBag = value;
    }

}
