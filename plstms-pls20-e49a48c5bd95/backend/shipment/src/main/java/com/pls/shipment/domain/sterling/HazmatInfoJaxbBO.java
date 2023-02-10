package com.pls.shipment.domain.sterling;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Hazmat information incase it exists.
 * 
 * @author Jasmin Dhamelia
 * 
 */

@XmlRootElement(name = "HazmatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class HazmatInfoJaxbBO implements Serializable {

    private static final long serialVersionUID = 4812702379679937653L;

    @XmlElement(name = "UNNum")
    private String unNum;

    @XmlElement(name = "PackagingGroupNum")
    private String packagingGroupNum;

    @XmlElement(name = "EmergencyCompany")
    private String emergencyCompany;

    @XmlElement(name = "EmergencyPhone")
    private String emergencyPhone;

    @XmlElement(name = "EmergencyContractNum")
    private String emergencyContractNum;

    @XmlElement(name = "EmergencyInstr")
    private String emergencyInstr;

    @XmlElement(name = "Class")
    private String hzClass;

    public String getUnNum() {
        return unNum;
    }

    public void setUnNum(String unNum) {
        this.unNum = unNum;
    }

    public String getPackagingGroupNum() {
        return packagingGroupNum;
    }

    public void setPackagingGroupNum(String packagingGroupNum) {
        this.packagingGroupNum = packagingGroupNum;
    }

    public String getEmergencyCompany() {
        return emergencyCompany;
    }

    public void setEmergencyCompany(String emergencyCompany) {
        this.emergencyCompany = emergencyCompany;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyContractNum() {
        return emergencyContractNum;
    }

    public void setEmergencyContractNum(String emergencyContractNum) {
        this.emergencyContractNum = emergencyContractNum;
    }

    public String getEmergencyInstr() {
        return emergencyInstr;
    }

    public void setEmergencyInstr(String emergencyInstr) {
        this.emergencyInstr = emergencyInstr;
    }

    public String getHzClass() {
        return hzClass;
    }

    public void setHzClass(String hzClass) {
        this.hzClass = hzClass;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getHzClass()).append(getUnNum()).append(getPackagingGroupNum())
                .append(getEmergencyCompany()).append(getEmergencyPhone()).append(getEmergencyContractNum()).append(getEmergencyInstr());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof HazmatInfoJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                HazmatInfoJaxbBO other = (HazmatInfoJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getHzClass(), other.getHzClass()).append(getUnNum(), other.getUnNum())
                        .append(getPackagingGroupNum(), other.getPackagingGroupNum()).append(getEmergencyCompany(), other.getEmergencyCompany())
                        .append(getEmergencyPhone(), other.getEmergencyPhone()).append(getEmergencyContractNum(), other.getEmergencyContractNum())
                        .append(getEmergencyInstr(), other.getEmergencyInstr());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Class", getHzClass()).append("UNNum", getUnNum())
                .append("PackagingGroupNum", getPackagingGroupNum()).append("EmergencyCompany", getEmergencyCompany())
                .append("EmergencyPhone", getEmergencyPhone()).append("EmergencyContractNum", getEmergencyContractNum())
                .append("EmergencyInstr", getEmergencyInstr());

        return builder.toString();
    }
}
