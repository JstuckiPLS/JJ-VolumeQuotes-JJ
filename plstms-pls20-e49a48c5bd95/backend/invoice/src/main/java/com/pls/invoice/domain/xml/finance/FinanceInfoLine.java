package com.pls.invoice.domain.xml.finance;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Interface to setup basic info for line items for JAXB serialization.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinanceInfoLine extends Serializable {
    /**
     * Getter for Item Type.
     * 
     * @return item type
     */
    String getItemType();

    /**
     * Getter for Unit Cost.
     * 
     * @return unit cost
     */
    BigDecimal getUnitCost();

    /**
     * Getter for Total.
     * 
     * @return total
     */
    BigDecimal getTotal();

    /**
     * Getter for Quantity.
     * 
     * @return quantity
     */
    Long getQuantity();

    /**
     * Getter for Unit Type.
     * 
     * @return unit type
     */
    String getUnitType();

    /**
     * Getter for Comments.
     * 
     * @return comments.
     */
    String getComments();

    /**
     * Setter for Item Type.
     * 
     * @param itemType
     *            item type
     */
    void setItemType(String itemType);

    /**
     * Setter for Unit Cost.
     * 
     * @param unitCost
     *            unit cost
     */
    void setUnitCost(BigDecimal unitCost);

    /**
     * Setter for Total.
     * 
     * @param total
     *            total
     */
    void setTotal(BigDecimal total);

    /**
     * Setter for Quantity.
     * 
     * @param quantity
     *            quantity
     */
    void setQuantity(Long quantity);

    /**
     * Setter for Unit Type.
     * 
     * @param unitType
     *            unit type
     */
    void setUnitType(String unitType);

    /**
     * Setter for Comments.
     * 
     * @param comments
     *            comments.
     */
    void setComments(String comments);
}
