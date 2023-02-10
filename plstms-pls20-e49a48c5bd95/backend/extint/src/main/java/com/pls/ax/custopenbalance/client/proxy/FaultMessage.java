package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for FaultMessage complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FaultMessage", propOrder = {
    "code",
    "message"
})
@SuppressWarnings("PMD")
public class FaultMessage {

    @XmlElementRef(name = "Code", namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", type
        = JAXBElement.class)
    protected JAXBElement<String> code;
    @XmlElementRef(name = "Message", namespace = "http://schemas.microsoft.com/dynamics/2008/01/documents/Fault", type
        = JAXBElement.class)
    protected JAXBElement<String> message;

    /**
     * Gets the value of the code property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setCode(JAXBElement<String> value) {
        this.code = value;
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
