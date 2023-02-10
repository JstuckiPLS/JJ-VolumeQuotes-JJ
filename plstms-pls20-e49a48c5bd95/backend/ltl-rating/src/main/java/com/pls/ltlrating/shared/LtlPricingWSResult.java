package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO that contains data of WS response.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingWSResult implements Serializable {

    private static final long serialVersionUID = 925127821232132234L;

    private Integer code;
    private String error;
    private Integer profileCount;
    private List<LtlPricingDetailsWSResult> profiles;

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public Integer getProfileCount() {
        return profileCount;
    }
    public void setProfileCount(Integer profileCount) {
        this.profileCount = profileCount;
    }
    public List<LtlPricingDetailsWSResult> getProfiles() {
        return profiles;
    }
    public void setProfiles(List<LtlPricingDetailsWSResult> profiles) {
        this.profiles = profiles;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("code", code)
                .append("error", error)
                .append("profileCount", profileCount)
                .append("profiles", profiles);
        return builder.toString();
    }

}
