/**
 * 
 */
package com.pls.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author rfiroz
 *
 */
@Entity
@Table(name = "Holidays")
@NamedQueries({ @NamedQuery(name = "Holiday.findByCountryCode",
        query = "FROM HolidayEntity o WHERE o.countryCode = :countryCode") })
public class HolidayEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HOLIDAYS_SEQUENCE")
    @SequenceGenerator(name = "HOLIDAYS_SEQUENCE", sequenceName = "HOLIDAYS_SEQ", allocationSize = 1)
    @Column(name = "HOLIDAY_ID")
    private Long id;

    @Column(name = "COUNTRY_CODE", nullable = false)
    private String countryCode;

    @Column(name = "HOLIDAY_YEAR", nullable = false)
    private String holidayYear;

    @Column(name = "HOLIDAY_DATE", nullable = false)
    private Date holidayDate;

    @Column(name = "HOLIDAY_NAME", nullable = false)
    private String holidayName;

    @Column(name = "ACTIVE", nullable = true)
    private boolean active;

    @Column(name = "FLOATING", nullable = true)
    private boolean floating;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode
     *            the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the holidayYear
     */
    public String getHolidayYear() {
        return holidayYear;
    }

    /**
     * @param holidayYear
     *            the holidayYear to set
     */
    public void setHolidayYear(String holidayYear) {
        this.holidayYear = holidayYear;
    }

    /**
     * @return the holidayDate
     */
    public Date getHolidayDate() {
        return holidayDate;
    }

    /**
     * @param holidayDate
     *            the holidayDate to set
     */
    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    /**
     * @return the holidayName
     */
    public String getHolidayName() {
        return holidayName;
    }

    /**
     * @param holidayName
     *            the holidayName to set
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the floating
     */
    public boolean isFloating() {
        return floating;
    }

    /**
     * @param floating
     *            the floating to set
     */
    public void setFloating(boolean floating) {
        this.floating = floating;
    }

}
