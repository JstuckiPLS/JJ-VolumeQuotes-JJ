package com.pls.shipment.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.address.PhoneEmbeddableObject;

/**
 * Represents hazmat information for {@link LtlProductEntity}.
 * 
 * @author Maxim Medvedev
 */
@Embeddable
public class LtlProductHazmatInfo implements Serializable {

    public static final int EMERGENCY_COMPANY_LENGTH = 32;
    private static final int EMERGENCY_CONTRACT_LENGTH = 32;
    private static final int EMERGENCY_COUNTRY_CODE_LENGTH = 10;
    private static final int EMERGENCY_AREA_CODE_LENGTH = 10;
    private static final int EMERGENCY_NUMBER_LENGTH = 32;
    public static final int HAZMAT_CLASS_LENGTH = 100;
    public static final int HAZMAT_INSTRUCTIONS_LENGTH = 2000;
    private static final int PACKING_GROUP_LENGTH = 32;

    private static final long serialVersionUID = -8329585135723993424L;

    private static final int UN_NUMBER_LENGTH = 32;

    @Column(name = "EMERGENCY_COMPANY", nullable = true, length = EMERGENCY_COMPANY_LENGTH)
    private String emergencyCompany;

    @Column(name = "EMERGENCY_CONTRACT", nullable = true, length = EMERGENCY_CONTRACT_LENGTH)
    private String emergencyContract;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode",
                    column = @Column(name = "EMERGENCY_COUNTRY_CODE", nullable = true, length = EMERGENCY_COUNTRY_CODE_LENGTH)),
            @AttributeOverride(name = "areaCode",
                    column = @Column(name = "EMERGENCY_AREA_CODE", nullable = true, length = EMERGENCY_AREA_CODE_LENGTH)),
            @AttributeOverride(name = "number",
                    column = @Column(name = "EMERGENCY_NUMBER", nullable = true, length = EMERGENCY_NUMBER_LENGTH)),
            @AttributeOverride(name = "extension",
                    column = @Column(name = "EMERGENCY_EXTENSION", nullable = true))
    })
    private PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();

    @Column(name = "HAZMAT_CLASS", nullable = true, length = HAZMAT_CLASS_LENGTH)
    private String hazmatClass;

    @Column(name = "HAZMAT_INSTRUCTIONS", nullable = true, length = HAZMAT_INSTRUCTIONS_LENGTH)
    private String instructions;

    @Column(name = "PACKING_GROUP", nullable = true, length = PACKING_GROUP_LENGTH)
    private String packingGroup;

    @Column(name = "UN_NUM", nullable = true, length = UN_NUMBER_LENGTH)
    private String unNumber;

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlProductHazmatInfo) {
            if (obj == this) {
                result = true;
            } else {
                LtlProductHazmatInfo rhs = (LtlProductHazmatInfo) obj;
                result = new EqualsBuilder().append(getHazmatClass(), rhs.getHazmatClass())
                        .append(getPackingGroup(), rhs.getPackingGroup()).append(getUnNumber(), rhs.getUnNumber())
                        .append(getEmergencyPhone(), rhs.getEmergencyPhone())
                        .append(getEmergencyCompany(), rhs.getEmergencyCompany())
                        .append(getEmergencyContract(), rhs.getEmergencyContract())
                        .append(getInstructions(), rhs.getInstructions()).isEquals();
            }
        }
        return result;
    }

    /**
     * Get emergencyCompany value.
     * 
     * @return the emergencyCompany.
     */
    public String getEmergencyCompany() {
        return emergencyCompany;
    }

    /**
     * Get emergencyContract value.
     * 
     * @return the emergencyContract.
     */
    public String getEmergencyContract() {
        return emergencyContract;
    }

    /**
     * Get emergencyPhone value.
     * 
     * @return the emergencyPhone.
     */
    public PhoneEmbeddableObject getEmergencyPhone() {
        return emergencyPhone;
    }

    /**
     * Get hazmatClass value.
     * 
     * @return the hazmatClass.
     */
    public String getHazmatClass() {
        return hazmatClass;
    }

    /**
     * Get instructions value.
     * 
     * @return the instructions.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Get packingGroup value.
     * 
     * @return the packingGroup.
     */
    public String getPackingGroup() {
        return packingGroup;
    }

    /**
     * Get unNumber value.
     * 
     * @return the unNumber.
     */
    public String getUnNumber() {
        return unNumber;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getHazmatClass()).append(getPackingGroup()).append(getUnNumber())
                .append(getEmergencyPhone()).append(getEmergencyCompany()).append(getEmergencyContract())
                .append(getInstructions()).toHashCode();
    }

    /**
     * Set emergencyCompany value.
     * 
     * @param emergencyCompany
     *            value to set.
     */
    public void setEmergencyCompany(String emergencyCompany) {
        this.emergencyCompany = emergencyCompany;
    }

    /**
     * Set emergencyContract value.
     * 
     * @param emergencyContract
     *            value to set.
     */
    public void setEmergencyContract(String emergencyContract) {
        this.emergencyContract = emergencyContract;
    }

    /**
     * Set emergencyPhone value.
     * 
     * @param emergencyNumber value to set.
     */
    public void setEmergencyPhone(PhoneEmbeddableObject emergencyNumber) {
        emergencyPhone = emergencyNumber;
    }

    /**
     * Set hazmatClass value.
     * 
     * @param hazmatClass
     *            value to set.
     */
    public void setHazmatClass(String hazmatClass) {
        this.hazmatClass = hazmatClass;
    }

    /**
     * Set instructions value.
     * 
     * @param instructions
     *            value to set.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Set packingGroup value.
     * 
     * @param packingGroup
     *            value to set.
     */
    public void setPackingGroup(String packingGroup) {
        this.packingGroup = packingGroup;
    }

    /**
     * Set unNumber value.
     * 
     * @param unNumber
     *            value to set.
     */
    public void setUnNumber(String unNumber) {
        this.unNumber = unNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("hazmatClass", getHazmatClass())
                .append("packingGroup", getPackingGroup()).append("unNumber", getUnNumber())
                .append("emergencyPhone", getEmergencyPhone()).append("emergencyPhone", getEmergencyPhone())
                .append("emergencyContract", getEmergencyContract()).append("emergencyCompany", getEmergencyCompany())
                .append("instructions", getInstructions()).toString();
    }

}
