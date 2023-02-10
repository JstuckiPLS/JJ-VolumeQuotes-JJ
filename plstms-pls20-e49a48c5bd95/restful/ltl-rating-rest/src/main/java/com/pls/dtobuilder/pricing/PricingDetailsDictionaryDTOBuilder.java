package com.pls.dtobuilder.pricing;

import java.util.Arrays;

import com.pls.core.domain.enums.LtlServiceType;
import com.pls.dto.pricing.PricingDetailsDictionaryDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.LtlMarginType;

/**
 * DTO Builder for {@link PricingDetailsDictionaryDTO}.
 * 
 * @author Artem Arapov
 * 
 */
public class PricingDetailsDictionaryDTOBuilder extends AbstractDTOBuilder<Void, PricingDetailsDictionaryDTO> {

    private final LtlCostTypeDTOBuilder costTypeBuilder = new LtlCostTypeDTOBuilder();

    private final LtlMarginTypeDTOBuilder marginTypeBuilder = new LtlMarginTypeDTOBuilder();

    private final LtlServiceTypeDTOBuilder serviceTypeBuilder = new LtlServiceTypeDTOBuilder();

    private final FuelWeekDaysDTOBuilder fuelWeekdaysBuilder = new FuelWeekDaysDTOBuilder();

    @Override
    public PricingDetailsDictionaryDTO buildDTO(Void bo) {
        PricingDetailsDictionaryDTO dto = new PricingDetailsDictionaryDTO();

        dto.setLtlCostTypes(costTypeBuilder.buildList(Arrays.asList(LtlCostType.values())));
        dto.setLtlMarginTypes(marginTypeBuilder.buildList(Arrays.asList(LtlMarginType.values())));
        dto.setLtlServiceTypes(serviceTypeBuilder.buildList(Arrays.asList(LtlServiceType.values())));
        dto.setFuelWeekDays(fuelWeekdaysBuilder.buildList(Arrays.asList(FuelWeekDays.values())));

        return dto;
    }

    @Override
    public Void buildEntity(PricingDetailsDictionaryDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
