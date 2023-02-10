package com.pls.dto.address;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Country DTO.
 *
 * @author Artem Arapov
 */

@XmlRootElement
public class CountryDTO {
    private String id;

    private String name;

    private String dialingCode;

    /**
     * Default constructor.
     */
    public CountryDTO() {
    }

    /**
     * Constructor.
     *
     * @param id country code
     */
    public CountryDTO(String id) {
        this.id = id;
    }

    /**
     * Get Country id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Set Country id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get Country name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set Country Name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Country Dialing Code.
     *
     * @return the dialingCode
     */
    public String getDialingCode() {
        return dialingCode;
    }

    /**
     * Set Country Dialing Code.
     *
     * @param dialingCode the name to set
     */
    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CountryDTO)) {
            return false;
        }
        CountryDTO other = (CountryDTO) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }

    /**
     * Country Items.
     */
    public enum Items {

        CANADA("CAN") {
            @Override
            public boolean is(CountryDTO dto) {
                return !(dto == null || dto.getId() == null) && getCode().equalsIgnoreCase(dto.getId());
            }
        };

        private String code;

        /**
         * Constructor.
         *
         * @param code is country code
         */
        Items(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        /**
         * Define if a given country corresponds to dto.
         *
         * @param dto actual dto
         * @return true if corresponds
         */
        public abstract boolean is(CountryDTO dto);

    }
}
