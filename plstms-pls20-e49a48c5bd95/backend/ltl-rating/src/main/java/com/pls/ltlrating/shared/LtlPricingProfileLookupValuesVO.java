package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.List;

import com.pls.core.domain.LtlLookupValueEntity;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.extint.shared.AvailableCarrierVO;
import com.pls.extint.shared.DataModuleVO;
import com.pls.ltlrating.domain.LtlPricingCarrierTypesEntity;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;
import com.pls.ltlrating.domain.MileageTypesEntity;

/**
 * Lookup values VO for Pricing Profile screens.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingProfileLookupValuesVO implements Serializable {

    private static final long serialVersionUID = 543321879331132323L;

    private Long ltlPricingProfileId;
    private List<LtlPricingTypesEntity> pricingTypes;
    private List<LtlPricingCarrierTypesEntity> carrierTypes;
    private List<MileageTypesEntity> mileageTypes;
    private List<LtlLookupValueEntity> mscaleValues;
    private List<DataModuleVO> smc3Tariffs;
    private List<AvailableCarrierVO> smc3Carriers;
    private OrganizationAPIDetailsEntity selectedCarrierAPIDetails;

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }
    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }
    public List<LtlPricingTypesEntity> getPricingTypes() {
        return pricingTypes;
    }
    public void setPricingTypes(List<LtlPricingTypesEntity> pricingTypes) {
        this.pricingTypes = pricingTypes;
    }
    public List<LtlPricingCarrierTypesEntity> getCarrierTypes() {
        return carrierTypes;
    }
    public void setCarrierTypes(List<LtlPricingCarrierTypesEntity> carrierTypes) {
        this.carrierTypes = carrierTypes;
    }
    public List<MileageTypesEntity> getMileageTypes() {
        return mileageTypes;
    }
    public void setMileageTypes(List<MileageTypesEntity> mileageTypes) {
        this.mileageTypes = mileageTypes;
    }
    public List<LtlLookupValueEntity> getMscaleValues() {
        return mscaleValues;
    }
    public void setMscaleValues(List<LtlLookupValueEntity> mscaleValues) {
        this.mscaleValues = mscaleValues;
    }
    public List<AvailableCarrierVO> getSmc3Carriers() {
        return smc3Carriers;
    }
    public void setSmc3Carriers(List<AvailableCarrierVO> smc3Carriers) {
        this.smc3Carriers = smc3Carriers;
    }
    public OrganizationAPIDetailsEntity getSelectedCarrierAPIDetails() {
        return selectedCarrierAPIDetails;
    }
    public void setSelectedCarrierAPIDetails(OrganizationAPIDetailsEntity selectedCarrierAPIDetails) {
        this.selectedCarrierAPIDetails = selectedCarrierAPIDetails;
    }
    public List<DataModuleVO> getSmc3Tariffs() {
        return smc3Tariffs;
    }
    public void setSmc3Tariffs(List<DataModuleVO> smc3Tariffs) {
        this.smc3Tariffs = smc3Tariffs;
    }

}
