package com.pls.dtobuilder.quote;

import com.pls.dto.address.ZipDTO;
import com.pls.dto.quote.SavedQuoteListItemDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Saved Quote List Item DTO Builder.
 *
 * @author Ivan Shapovalov.
 */
public class SavedQuoteListItemDTOBuilder extends AbstractDTOBuilder<SavedQuoteBO, SavedQuoteListItemDTO> {
    public static final String MULTIPLE_COMMODITY_CLASSES_LABEL = "Multi";

    @Override
    public SavedQuoteListItemDTO buildDTO(SavedQuoteBO bo) {
        SavedQuoteListItemDTO dto = new SavedQuoteListItemDTO();
        dto.setId(bo.getId());
        dto.setPricingProfileId(bo.getPricingProfileId());
        dto.setQuoteId(bo.getQuoteId());
        dto.setVolumeQuoteId(bo.getVolumeQuoteId());
        buildRoutes(dto, bo);
        dto.setCarrierName(bo.getCarrierName());
        if (bo.getCustomerName() != null) {
            dto.setCustomerName(bo.getCustomerName());
        }
        dto.setWeight(bo.getWeight());
        dto.setCommodityClass(bo.getCommodityClass());
        dto.setEstimatedTransitTime(bo.getEstimatedTransitTime());
        dto.setCarrierCost(bo.getCarrierCost());
        dto.setCustomerRevenue(bo.getCustomerRevenue());
        dto.setShipperBaseRate(bo.getShipperBaseRate());
        dto.setCostOverride(bo.getCostOverride());
        dto.setRevenueOverride(bo.getRevenueOverride());
        dto.setLoadId(bo.getLoadId());
        return dto;
    }

    @Override
    public SavedQuoteBO buildEntity(SavedQuoteListItemDTO savedQuoteListItemDTOs) {
        throw new UnsupportedOperationException("SavedQuoteBO cannot be built from the SavedQuoteListItemDTO");
    }

    private void buildRoutes(SavedQuoteListItemDTO dto, SavedQuoteBO bo) {
        ZipDTO originZipDto = new ZipDTO();
        originZipDto.setZip(bo.getOriginZip());
        originZipDto.setCity(bo.getOriginCity());
        originZipDto.setState(bo.getOriginState());
        dto.setOrigin(originZipDto);
        ZipDTO destZipDto = new ZipDTO();
        destZipDto.setZip(bo.getDestZip());
        destZipDto.setCity(bo.getDestCity());
        destZipDto.setState(bo.getDestState());
        dto.setDestination(destZipDto);
    }

}
