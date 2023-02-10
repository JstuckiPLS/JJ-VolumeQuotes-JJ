package com.pls.dtobuilder.proposal;

import org.apache.commons.lang3.StringUtils;

import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.shared.FreightAnalysisTariffBO;

/**
 * Build {@link FATariffsEntity} from {@link FreightAnalysisTariffBO}.
 * 
 * @author Brichak Aleksandr
 */

public class FreightAnalysisTarifsDTOBuilder extends AbstractDTOBuilder<FATariffsEntity, FreightAnalysisTariffBO> {

    /**
     * This method should not be used.
     * 
     * @throws UnsupportedOperationException.
     */
    @Override
    public FreightAnalysisTariffBO buildDTO(FATariffsEntity bo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FATariffsEntity buildEntity(FreightAnalysisTariffBO bo) {
        FATariffsEntity result = new FATariffsEntity();
        result.setCustomerId(bo.getCustomerId());
        result.setPricingProfileId(bo.getLtlPricingProfileId());
        result.setTariffName(StringUtils.defaultIfBlank(bo.getSmc3TariffName(), bo.getRateName()));
        result.setTariffType(bo.getPricingType());
        return result;
    }

}

