package com.pls.invoice.domain.xml.finance.salesorder;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pls.invoice.domain.xml.finance.FinanceInfoLine;

/**
 * SalesLine JAXB-oriented object.
 *
 * @author Denis Zhupinsky (Team International)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SalesLine implements FinanceInfoLine {

    private static final long serialVersionUID = 6523525214526525415L;

    @XmlElement(name = "ItemType")
    private String itemType;

    @XmlElement(name = "UnitCost")
    private BigDecimal unitCost;

    @XmlElement(name = "Total")
    private BigDecimal total;

    @XmlElement(name = "Quantity")
    private Long quantity;

    @XmlElement(name = "UnitType")
    private String unitType;

    @XmlElement(name = "Comments")
    private String comments;

    public String getItemType() {
        return itemType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Long getQuantity() {
        return quantity;
    }

    public String getUnitType() {
        return unitType;
    }

    public String getComments() {
        return comments;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
