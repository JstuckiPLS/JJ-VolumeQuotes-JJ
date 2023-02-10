package com.pls.shipment.domain.sterling;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.shipment.domain.sterling.enums.TransactionDateType;

/**
 * Contains TransDateType, Year, Month, Day, Hour, Min.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "TransactionDate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionDateJaxbBO implements Serializable {

    private static final long serialVersionUID = -4065566465818534809L;

    @XmlElement(name = "TransDateType")
    private TransactionDateType transDateType;

    @XmlElement(name = "Year")
    private String year;

    @XmlElement(name = "Month")
    private String month;

    @XmlElement(name = "Day")
    private String day;

    @XmlElement(name = "Hour")
    private String hour;

    @XmlElement(name = "Min")
    private String minutes;

    public TransactionDateType getTransDateType() {
        return transDateType;
    }

    public void setTransDateType(TransactionDateType transDateType) {
        this.transDateType = transDateType;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    /**
     * Set the transaction date fields - year, month, day, hour and minutes.
     * 
     * @param transDate
     *            Transaction Date
     * */
    public void setTransDate(Date transDate) {
        setTransDateFields(transDate);

    }

    /**
     * Get the transaction date.
     * 
     * @return transaction date with time
     * */
    public Date getTransDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(day));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minutes));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private void setTransDateFields(Date transDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transDate);
        year = ((Integer) calendar.get(Calendar.YEAR)).toString();
        month = ((Integer) (calendar.get(Calendar.MONTH) + 1)).toString();
        day = ((Integer) calendar.get(Calendar.DATE)).toString();
        hour = ((Integer) calendar.get(Calendar.HOUR_OF_DAY)).toString();
        minutes = ((Integer) calendar.get(Calendar.MINUTE)).toString();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getYear()).append(getMonth()).append(getDay()).append(getHour()).append(getMinutes())
                .append(getTransDateType());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TransactionDateJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TransactionDateJaxbBO other = (TransactionDateJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getYear(), other.getYear()).append(getMonth(), other.getMonth())
                        .append(getDay(), other.getDay()).append(getHour(), other.getHour()).append(getMinutes(), other.getMinutes())
                        .append(getTransDateType(), other.getTransDateType());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Year", getYear()).append("Month", getMonth()).append("Day", getDay())
                .append("Hour", getHour()).append("Minutes", getMinutes()).append("TransactionDateType", getTransDateType());

        return builder.toString();
    }
}
