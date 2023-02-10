/**
 * 
 */
package com.pls.core.domain.bo.dashboard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * daily load activity been.
 * 
 * @author Alexander Nalapko
 */
public class DailyLoadActivityBO {

    private Date pickup;
    private String weekday;
    private String scac;
    private String bound;
    private BigDecimal classCode;
    private String destState;
    private String origState;
    private String customer;
    private BigDecimal total;

    public Date getPickup() {
        return pickup;
    }
    public void setPickup(Date pickup) {
        this.pickup = pickup;
    }
    public String getWeekday() {
        return weekday;
    }
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
    public String getScac() {
        return scac;
    }
    public void setScac(String scac) {
        this.scac = scac;
    }
    public String getBound() {
        return bound;
    }
    public void setBound(String bound) {
        this.bound = bound;
    }

    public BigDecimal getClassCode() {
        return classCode;
    }

    public void setClassCode(BigDecimal classCode) {
        this.classCode = classCode;
    }
    public String getDestState() {
        return destState;
    }
    public void setDestState(String destState) {
        this.destState = destState;
    }
    public String getOrigState() {
        return origState;
    }
    public void setOrigState(String origState) {
        this.origState = origState;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
