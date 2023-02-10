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
 * @author jblackson
 *
 */
@Entity
@Table(name = "International_Holidays")
@NamedQueries({ 
	@NamedQuery(name = "findHolidayByCountryCodeAndYear",
        query = "FROM InternationalHolidayEntity o WHERE o.countryCode = :countryCode and (o.holidayYear = :holidayYear or o.holidayYear = :holidayYear1) and o.active = true")
})
public class InternationalHolidayEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERNATIONAL_HOLIDAYS_SEQUENCE")
    @SequenceGenerator(name = "INTERNATIONAL_HOLIDAYS_SEQUENCE", sequenceName = "INTERNATIONAL_HOLIDAYS_SEQ", allocationSize = 1)
    @Column(name = "HOLIDAY_ID")
    private Long id;

    @Column(name = "COUNTRY_CODE", nullable = false)
    private String countryCode;
    
    @Column(name = "STATE_CODE", nullable = true)
    private String stateCode;
    
    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "HOLIDAY_YEAR", nullable = false)
    private int holidayYear;

    @Column(name = "HOLIDAY_DATE", nullable = false)
    private Date holidayDate;
    
    @Column(name = "OBSERVED_DATE", nullable = false)
    private Date observedDate;

    @Column(name = "HOLIDAY_NAME", nullable = false)
    private String holidayName;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "OBSERVED", nullable = false)
    private boolean observed;

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

   
    public int getHolidayYear() {
		return holidayYear;
	}

	public void setHolidayYear(int holidayYear) {
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

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getObservedDate() {
		return observedDate;
	}

	public void setObservedDate(Date observedDate) {
		this.observedDate = observedDate;
	}

	public boolean isObserved() {
		return observed;
	}

	public void setObserved(boolean observed) {
		this.observed = observed;
	}
}
