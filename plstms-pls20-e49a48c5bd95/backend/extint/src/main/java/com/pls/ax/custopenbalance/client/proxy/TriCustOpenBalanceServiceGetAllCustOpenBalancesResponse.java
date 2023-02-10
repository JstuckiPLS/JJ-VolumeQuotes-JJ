package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "response"
})
@XmlRootElement(name = "TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse", namespace
    = "http://schemas.microsoft.com/dynamics/2011/01/services")
@SuppressWarnings("PMD")
public class TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse {

    @XmlElementRef(name = "response", namespace = "http://schemas.microsoft.com/dynamics/2011/01/services", type
        = JAXBElement.class)
    protected JAXBElement<TriCustTransListDC> response;

    /**
     * Gets the value of the response property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link TriCustTransListDC }{@code >}
     *
     */
    public JAXBElement<TriCustTransListDC> getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link TriCustTransListDC }{@code >}
     *
     */
    public void setResponse(JAXBElement<TriCustTransListDC> value) {
        this.response = value;
    }

}
