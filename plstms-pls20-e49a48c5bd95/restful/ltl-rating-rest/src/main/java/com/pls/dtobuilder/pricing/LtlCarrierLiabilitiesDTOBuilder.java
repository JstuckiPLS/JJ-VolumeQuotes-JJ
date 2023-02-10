package com.pls.dtobuilder.pricing;

import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.LtlCarrierLiabilitiesDTO;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * DTO Builder for {@link LtlCarrierLiabilitiesDTO}.
 * 
 * @author Pavani Challa
 * 
 */
public final class LtlCarrierLiabilitiesDTOBuilder {
    private static final LtlCarrierLiabilitiesDTOBuilder INSTANCE = new LtlCarrierLiabilitiesDTOBuilder();

    private LtlCarrierLiabilitiesDTOBuilder() {

    }

    /**
     * Method for returning an instance of builder.
     * @return an instance of the builder.
     */
    public static LtlCarrierLiabilitiesDTOBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Returns all liabilities including the freight classes that are not saved for the profile. If the
     * isPallet flag is true, then only one item is returned in the list. If there are more than one item
     * previously saved for the profile, then a new DTO is created and returned. If the isPallet flag is
     * false, then all liabilities including all freight classes are returned.
     * 
     * @param liabilities
     *            liabilities for building the list
     * @param isPallet
     *            flag indicating if the list is for a pallet profile
     * @return list of all liabilities for the profile
     */
    public static List<LtlCarrierLiabilitiesDTO> getAllLiabilities(List<LtlCarrierLiabilitiesEntity> liabilities,
            boolean isPallet) {
        List<LtlCarrierLiabilitiesDTO> allLiabilities = new ArrayList<LtlCarrierLiabilitiesDTO>();

        if (isPallet) {
            if (liabilities.size() != 1 || liabilities.get(0).getFreightClass() != null) {
                allLiabilities.add(new LtlCarrierLiabilitiesDTO(null, new LtlCarrierLiabilitiesEntity()));
            } else {
                allLiabilities.add(new LtlCarrierLiabilitiesDTO(null, liabilities.get(0)));
            }
        } else {
            // Add an empty record for the freight classes that do not have liabilities.
            for (CommodityClass freightClass : CommodityClass.values()) {
                Boolean found = false;
                for (LtlCarrierLiabilitiesEntity liability : liabilities) {
                    if (liability.getFreightClass() == freightClass) {
                        allLiabilities.add(new LtlCarrierLiabilitiesDTO(freightClass.getDbCode(), liability));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    allLiabilities.add(new LtlCarrierLiabilitiesDTO(freightClass.getDbCode(),
                            new LtlCarrierLiabilitiesEntity()));
                }
            }
        }

        return allLiabilities;
    }
}
