package com.pls.dtobuilder.address;


import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.dto.address.BillToDefaultValuesDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.PhoneDTOBuilder;


/**
 * Builder for conversation between BillToDefaultValuesEntity and BillToDefaultValuesDTO.
 * 
 * @author Dmitriy Davydenko
 *
 */
public class BillToDefaultValuesDTOBuilder extends AbstractDTOBuilder<BillToDefaultValuesEntity, BillToDefaultValuesDTO> {

    private PhoneDTOBuilder phoneDTOBuilder = new PhoneDTOBuilder();
    private final DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider
     *            data provider of existing data to update
     */
    public BillToDefaultValuesDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public BillToDefaultValuesDTO buildDTO(BillToDefaultValuesEntity entity) {
        BillToDefaultValuesDTO billToDefaultValues = new BillToDefaultValuesDTO();
        if (entity != null) {
            if (entity.getDirection() != null) {
                billToDefaultValues.setDirection(entity.getDirection().getCode());
            }
            if (entity.getEdiDirection() != null) {
                billToDefaultValues.setEdiDirection(entity.getEdiDirection().getCode());
            }
            if (entity.getManualBolDirection() != null) {
                billToDefaultValues.setManualBolDirection(entity.getManualBolDirection().getCode());
            }
            billToDefaultValues.setEdiPayTerms(entity.getEdiPayTerms());
            billToDefaultValues.setPayTerms(entity.getPayTerms());
            billToDefaultValues.setManualBolPayTerms(entity.getManualBolPayTerms());
            billToDefaultValues.setId(entity.getId());
            billToDefaultValues.setEdiCustomsBroker(entity.getEdiCustomsBroker());
            billToDefaultValues.setEdiCustomsBrokerPhone(phoneDTOBuilder.buildDTO(entity.getEdiCustomsBrokerPhone()));
        }
        return billToDefaultValues;
    }

    @Override
    public BillToDefaultValuesEntity buildEntity(BillToDefaultValuesDTO dto) {
        BillToDefaultValuesEntity billToDefaultValuesEntity = null;
        if (dto != null) {
            billToDefaultValuesEntity = (dataProvider != null && dto.getId() != null)
                    ? dataProvider.getDefaultValues(dto.getId()) : new BillToDefaultValuesEntity();
            billToDefaultValuesEntity.setDirection(ShipmentDirection.getByCode(dto.getDirection()));
            billToDefaultValuesEntity.setEdiDirection(ShipmentDirection.getByCode(dto.getEdiDirection()));
            billToDefaultValuesEntity.setManualBolDirection(ShipmentDirection.getByCode(dto.getManualBolDirection()));
            billToDefaultValuesEntity.setEdiPayTerms(dto.getEdiPayTerms());
            billToDefaultValuesEntity.setManualBolPayTerms(dto.getManualBolPayTerms());
            billToDefaultValuesEntity.setPayTerms(dto.getPayTerms());
            billToDefaultValuesEntity.setEdiCustomsBroker(dto.getEdiCustomsBroker());
            billToDefaultValuesEntity.setEdiCustomsBrokerPhone(phoneDTOBuilder.buildEntity(dto.getEdiCustomsBrokerPhone()));
        }
        return billToDefaultValuesEntity;
    }

    /**
     * Data provider for update.
     */
    public interface DataProvider {
        /**
         * Get existing default values settings.
         * 
         * @param id
         *            of invoice settings
         *
         * @return {@link BillToDefaultValuesEntity}
         */
        BillToDefaultValuesEntity getDefaultValues(Long id);
    }
}
