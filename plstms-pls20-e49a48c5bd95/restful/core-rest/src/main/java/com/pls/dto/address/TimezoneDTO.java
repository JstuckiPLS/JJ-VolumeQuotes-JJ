package com.pls.dto.address;

/**
 * DTO for timezones.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class TimezoneDTO {
    private String code;
    private String name;
    private byte localOffset;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getLocalOffset() {
        return localOffset;
    }

    public void setLocalOffset(byte localOffset) {
        this.localOffset = localOffset;
    }
}
