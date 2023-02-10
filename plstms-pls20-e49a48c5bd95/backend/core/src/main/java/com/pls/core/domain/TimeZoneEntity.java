package com.pls.core.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Timezone information entity.
 *
 * @author Viacheslav Krot
 */
@Entity
@Immutable
@Table(name = "TIMEZONE")
public class TimeZoneEntity implements Serializable {

    public static final String Q_FIND_BY_COUNTRY_ZIP = "com.pls.core.domain.TimeZoneEntity.Q_FIND_BY_COUNTRY_ZIP";

    private static final long serialVersionUID = -8507938314586848797L;

    @Id
    @Column(name = "LOCAL_OFFSET")
    private byte localOffset;

    @Column(name = "TIMEZONE_CODE")
    private String code;

    @Column(name = "TIMEZONE_NAME")
    private String name;

    @Column(name = "TIMEZONE")
    private byte timezone;

    public byte getTimezone() {
        return timezone;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public byte getLocalOffset() {
        return localOffset;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLocalOffset(byte localOffset) {
        this.localOffset = localOffset;
    }
}
