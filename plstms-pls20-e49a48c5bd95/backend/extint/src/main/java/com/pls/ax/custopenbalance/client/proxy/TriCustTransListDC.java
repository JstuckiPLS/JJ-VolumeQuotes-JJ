package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TriCustTransListDC complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TriCustTransListDC", namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", propOrder = {
    "parmCustTransList"
})
@SuppressWarnings("PMD")
public class TriCustTransListDC
    extends XppObjectBase {

    @XmlElementRef(name = "parmCustTransList", namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application", type = JAXBElement.class)
    protected JAXBElement<ArrayOfCustOpenBalance> parmCustTransList;

    /**
     * Gets the value of the parmCustTransList property.
     *
     * @return
     * possible object is
     * {@link JAXBElement }{@code <}{@link ArrayOfCustOpenBalance }{@code >}
     *
     */
    public JAXBElement<ArrayOfCustOpenBalance> getParmCustTransList() {
        return parmCustTransList;
    }

    /**
     * Sets the value of the parmCustTransList property.
     *
     * @param value
     * allowed object is
     * {@link JAXBElement }{@code <}{@link ArrayOfCustOpenBalance }{@code >}
     *
     */
    public void setParmCustTransList(JAXBElement<ArrayOfCustOpenBalance> value) {
        this.parmCustTransList = value;
    }

}
