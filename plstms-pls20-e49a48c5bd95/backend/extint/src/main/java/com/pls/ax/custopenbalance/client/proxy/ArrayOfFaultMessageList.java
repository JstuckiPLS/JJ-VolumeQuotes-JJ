package com.pls.ax.custopenbalance.client.proxy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ArrayOfFaultMessageList complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFaultMessageList", propOrder = {
    "faultMessageList"
})
@SuppressWarnings("PMD")
public class ArrayOfFaultMessageList {

    @XmlElement(name = "FaultMessageList", nillable = true)
    protected List<FaultMessageList> faultMessageList;

    /**
     * Gets the value of the faultMessageList property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the faultMessageList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFaultMessageList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link FaultMessageList }
     *
     * @return A list of FaultMessageList instances.
     *
     */
    public List<FaultMessageList> getFaultMessageList() {
        if (faultMessageList == null) {
            faultMessageList = new ArrayList<FaultMessageList>();
        }
        return this.faultMessageList;
    }

}
