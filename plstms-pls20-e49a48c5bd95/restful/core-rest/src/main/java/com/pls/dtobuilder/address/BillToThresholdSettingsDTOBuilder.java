package com.pls.dtobuilder.address;

import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.dto.address.BillToThresholdSettingsDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Builder for conversation between BillToThresholdSettingsEntity and BillToThresholdSettingsDTO.
 * 
 * @author Brichak Aleksandr
 *
 */
public class BillToThresholdSettingsDTOBuilder
        extends AbstractDTOBuilder<BillToThresholdSettingsEntity, BillToThresholdSettingsDTO> {

    private final DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider
     *            data provider of existing data to update
     */
    public BillToThresholdSettingsDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public BillToThresholdSettingsDTO buildDTO(BillToThresholdSettingsEntity bo) {
        BillToThresholdSettingsDTO billToThresholdSettings = new BillToThresholdSettingsDTO();
        if (bo != null) {
            billToThresholdSettings.setCostDifference(bo.getThreshold());
            billToThresholdSettings.setTotalRevenue(bo.getTotalRevenue());
            billToThresholdSettings.setMargin(bo.getMargin());
            billToThresholdSettings.setId(bo.getId());
            return billToThresholdSettings;
        }
        return billToThresholdSettings;
    }

    @Override
    public BillToThresholdSettingsEntity buildEntity(BillToThresholdSettingsDTO dto) {
        BillToThresholdSettingsEntity billToThresholdSettings;
        if (dataProvider != null && dto.getId() != null) {
            billToThresholdSettings = dataProvider.getThresholdSettings(dto.getId());
        } else {
            billToThresholdSettings = new BillToThresholdSettingsEntity();
        }
        billToThresholdSettings.setThreshold(dto.getCostDifference());
        billToThresholdSettings.setTotalRevenue(dto.getTotalRevenue());
        billToThresholdSettings.setMargin(dto.getMargin());

        return billToThresholdSettings;
    }

    /**
     * Data provider for update.
     */
    public interface DataProvider {
        /**
         * Get existing threshold settings.
         * 
         * @param id
         *            of invoice settings
         *
         * @return {@link BillToThresholdSettingsEntity}
         */
        BillToThresholdSettingsEntity getThresholdSettings(Long id);
    }
}
