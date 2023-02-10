package com.pls.ax.custopenbalance.client.proxy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ArrayOfCustOpenBalance complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCustOpenBalance", namespace = "http://schemas.datacontract.org/2004/07/Dynamics.Ax.Application",
    propOrder = {
        "custOpenBalance"
    })
@SuppressWarnings("PMD")
public class ArrayOfCustOpenBalance {

    @XmlElement(name = "CustOpenBalance", nillable = true)
    protected List<CustOpenBalance> custOpenBalance;

    /**
     * Gets the value of the custOpenBalance property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the custOpenBalance property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustOpenBalance().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link CustOpenBalance }
     *
     * @return A list of CustOpenBalance instances.
     *
     */
    public List<CustOpenBalance> getCustOpenBalance() {
        if (custOpenBalance == null) {
            custOpenBalance = new ArrayList<CustOpenBalance>();
        }
        return this.custOpenBalance;
    }

}
