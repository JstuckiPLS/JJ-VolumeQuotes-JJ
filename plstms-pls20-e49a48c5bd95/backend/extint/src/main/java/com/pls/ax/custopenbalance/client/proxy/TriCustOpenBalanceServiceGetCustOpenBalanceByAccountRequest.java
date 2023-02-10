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
    "accountNum"
})
@XmlRootElement(name = "TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest", namespace
    = "http://schemas.microsoft.com/dynamics/2011/01/services")
@SuppressWarnings("PMD")
public class TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest {

    @XmlElementRef(name = "_accountNum", namespace = "http://schemas.microsoft.com/dynamics/2011/01/services", type
        = JAXBElement.class)
    protected JAXBElement<String> accountNum;

    /**
     * Gets the value of the accountNum property.
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getAccountNum() {
        return accountNum;
    }

    /**
     * Sets the value of the accountNum property.
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAccountNum(JAXBElement<String> value) {
        this.accountNum = value;
    }

}
