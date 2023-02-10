package com.pls.shipment.domain.sterling;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Transit Date information.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "TransitDate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransitDateJaxbBO implements Serializable {

    private static final long serialVersionUID = -8576879391276907842L;

    @XmlElement(name = "Year")
    private String year;

    @XmlElement(name = "Month")
    private String month;

    @XmlElement(name = "Day")
    private String day;

    @XmlElement(name = "HourFrom")
    private String fromHour;

    @XmlElement(name = "MinFrom")
    private String fromMin;

    @XmlElement(name = "HourTo")
    private String toHour;

    @XmlElement(name = "MinTo")
    private String toMin;

    /**
     * Setting up fromDate.
     * 
     * @param fromDate
     *            from date
     * */
    public void setFromDate(Date fromDate) {
        setFromDateFields(fromDate);

    }

    /**
     * getting a fromDate.
     * 
     * @return from date time
     * */
    public Date getFromDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(day));
        calendar.set(Calendar.HOUR_OF_DAY, StringUtils.isNotEmpty(fromHour) ? Integer.parseInt(fromHour) : 0);
        calendar.set(Calendar.MINUTE, StringUtils.isNotEmpty(fromMin) ? Integer.parseInt(fromMin) : 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Setting up toDate.
     * 
     * @param toDate
     *            to date
     * */
    public void setToDate(Date toDate) {
        setToDateFields(toDate);
    }

    /**
     * getting a toDate.
     * 
     * @return return to date time
     * */
    public Date getToDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(day));
        calendar.set(Calendar.HOUR_OF_DAY, StringUtils.isNotEmpty(toHour) ? Integer.parseInt(toHour) : 23);
        calendar.set(Calendar.MINUTE, StringUtils.isNotEmpty(toMin) ? Integer.parseInt(toMin) : 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getFromHour() {
        return fromHour;
    }

    public void setFromHour(String hourFrom) {
        this.fromHour = hourFrom;
    }

    public String getFromMin() {
        return fromMin;
    }

    public void setFromMin(String minFrom) {
        this.fromMin = minFrom;
    }

    public String getToHour() {
        return toHour;
    }

    public void setToHour(String hourTo) {
        this.toHour = hourTo;
    }

    public String getToMin() {
        return toMin;
    }

    public void setToMin(String minTo) {
        this.toMin = minTo;
    }

    private void setFromDateFields(Date fromDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        year = ((Integer) calendar.get(Calendar.YEAR)).toString();
        month = ((Integer) (calendar.get(Calendar.MONTH) + 1)).toString();
        day = ((Integer) calendar.get(Calendar.DATE)).toString();
        fromHour = ((Integer) calendar.get(Calendar.HOUR_OF_DAY)).toString();
        fromMin = ((Integer) calendar.get(Calendar.MINUTE)).toString();
    }

    private void setToDateFields(Date toDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        toHour = ((Integer) calendar.get(Calendar.HOUR_OF_DAY)).toString();
        toMin = ((Integer) calendar.get(Calendar.MINUTE)).toString();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getYear()).append(getMonth()).append(getDay()).append(getFromHour())
                .append(getFromMin()).append(getToHour()).append(getToMin());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TransitDateJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                TransitDateJaxbBO other = (TransitDateJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getYear(), other.getYear()).append(getMonth(), other.getMonth())
                        .append(getDay(), other.getDay()).append(getFromHour(), other.getFromHour()).append(getFromMin(), other.getFromMin())
                        .append(getToHour(), other.getToHour()).append(getToMin(), other.getToMin());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Year", getYear()).append("Month", getMonth()).append("Day", getDay())
                .append("HourFrom", getFromHour()).append("MinFrom", getFromMin()).append("HourTo", getToHour()).append("MinTo", getToMin());

        return builder.toString();
    }
}
