package com.pls.invoice.domain.xml.finance.vendinvoice;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pls.invoice.domain.xml.finance.FinanceInfoLine;

/**
 * VendInvoiceInfoLine JAXB-oriented object.
 *
 * @author Denis Zhupinsky (Team International)
 */
@XmlRootElement(name = "VendInvoiceLineItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class VendInvoiceInfoLine implements FinanceInfoLine {

    private static final long serialVersionUID = 3302157591158976319L;

    @XmlElement(name = "ItemType")
    private String itemType;

    @XmlElement(name = "UnitCost")
    private BigDecimal unitCost;

    @XmlElement(name = "Total")
    private BigDecimal total;

    @XmlElement(name = "UnitType")
    private String unitType;

    @XmlElement(name = "Quantity")
    private Long quantity;

    @XmlElement(name = "Comments")
    private String comments;

    public String getItemType() {
        return itemType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
