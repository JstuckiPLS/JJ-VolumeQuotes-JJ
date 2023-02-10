package com.pls.ax.custopenbalance.client.proxy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ArrayOfInfologMessage complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * @author Thomas Clancy
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfInfologMessage", namespace
    = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services", propOrder = {
        "infologMessage"
    })
@SuppressWarnings("PMD")
public class ArrayOfInfologMessage {

    @XmlElement(name = "InfologMessage", nillable = true)
    protected List<InfologMessage> infologMessage;

    /**
     * Gets the value of the infologMessage property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the infologMessage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfologMessage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link InfologMessage }
     *
     * @return A list of InfologMessage instances.
     *
     */
    public List<InfologMessage> getInfologMessage() {
        if (infologMessage == null) {
            infologMessage = new ArrayList<InfologMessage>();
        }
        return this.infologMessage;
    }

}
