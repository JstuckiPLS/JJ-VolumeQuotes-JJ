package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for InfologMessage complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfologMessage", namespace
    = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", propOrder = {
        "infologMessageType",
        "message"
    })
@SuppressWarnings("PMD")
public class InfologMessage {

    @XmlElement(name = "InfologMessageType")
    protected InfologMessageType infologMessageType;
    @XmlElementRef(name = "Message", namespace
        = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", type = JAXBElement.class)
    protected JAXBElement<String> message;

    /**
     * Gets the value of the infologMessageType property.
     *
     * @return possible object is {@link InfologMessageType }
     *
     */
    public InfologMessageType getInfologMessageType() {
        return infologMessageType;
    }

    /**
     * Sets the value of the infologMessageType property.
     *
     * @param value allowed object is {@link InfologMessageType }
     *
     */
    public void setInfologMessageType(InfologMessageType value) {
        this.infologMessageType = value;
    }

    /**
     * Gets the value of the message property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setMessage(JAXBElement<String> value) {
        this.message = value;
    }

}
