package com.pls.ax.custopenbalance.client.proxy;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CustOpenBalance complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustOpenBalance", namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application",
    propOrder = {
        "accountNum",
        "amountCUR",
        "amountMST"
    })
@SuppressWarnings("PMD")
public class CustOpenBalance
    extends XppObjectBase {

    @XmlElementRef(name = "AccountNum", namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application",
        type = JAXBElement.class)
    protected JAXBElement<String> accountNum;
    @XmlElement(name = "AmountCUR")
    protected BigDecimal amountCUR;
    @XmlElement(name = "AmountMST")
    protected BigDecimal amountMST;

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

    /**
     * Gets the value of the amountCUR property.
     *
     * @return possible object is {@link BigDecimal }
     *
     */
    public BigDecimal getAmountCUR() {
        return amountCUR;
    }

    /**
     * Sets the value of the amountCUR property.
     *
     * @param value allowed object is {@link BigDecimal }
     *
     */
    public void setAmountCUR(BigDecimal value) {
        this.amountCUR = value;
    }

    /**
     * Gets the value of the amountMST property.
     *
     * @return possible object is {@link BigDecimal }
     *
     */
    public BigDecimal getAmountMST() {
        return amountMST;
    }

    /**
     * Sets the value of the amountMST property.
     *
     * @param value allowed object is {@link BigDecimal }
     *
     */
    public void setAmountMST(BigDecimal value) {
        this.amountMST = value;
    }

}
