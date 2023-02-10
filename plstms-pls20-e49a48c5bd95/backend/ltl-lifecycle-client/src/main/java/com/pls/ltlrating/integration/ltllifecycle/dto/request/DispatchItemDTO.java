package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import com.pls.ltlrating.integration.ltllifecycle.dto.HazmatPackingGroupType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DispatchItemDTO extends ItemDTO {

    private HazmatDetailsDTO hazmatDetails;

    @Data
    public static class HazmatDetailsDTO {

        private String identificationNumber;

        private String hazardClass;

        private HazmatPackingGroupType packingGroupType;

        /** This is the shipping name of the item, needs to be accurate based on MSDS, otherwise shipment might be rejected. */
        private String properShippingName;

        private ContactDTO emergencyContact;

    }

}
