package com.pls.dtobuilder.organization;

import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.dto.organization.OrganizationFreightBillPayToDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.FreightBillPayToDTOBuilder;

/**
 * Organization Freight Bill Pay To DTO Builder.
 * 
 * @author Artem Arapov
 *
 */
public class OrgFreightBillPayToDTOBuilder extends AbstractDTOBuilder<OrganizationFreightBillPayToEntity, OrganizationFreightBillPayToDTO> {

    private static final FreightBillPayToDTOBuilder FREIGHT_BILL_PAY_TO_BUILDER = new FreightBillPayToDTOBuilder();

    private DataProvider dataProvider;

    /**
     * Constructor which accept {@link DataProvider}.
     * 
     * @param dataProvider Not <code>null</code> instance of {@link DataProvider}.
     */
    public OrgFreightBillPayToDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public OrganizationFreightBillPayToDTO buildDTO(OrganizationFreightBillPayToEntity bo) {
        OrganizationFreightBillPayToDTO dto = new OrganizationFreightBillPayToDTO();
        dto.setId(bo.getId());
        dto.setOrgId(bo.getOrgId());
        dto.setFreightBillPayTo(FREIGHT_BILL_PAY_TO_BUILDER.buildDTO(bo.getFreightBillPayTo()));

        return dto;
    }

    @Override
    public OrganizationFreightBillPayToEntity buildEntity(OrganizationFreightBillPayToDTO dto) {
        OrganizationFreightBillPayToEntity entity = getNewOrExistingEntity(dto.getId());
        entity.setOrgId(dto.getOrgId());
        entity.setFreightBillPayTo(FREIGHT_BILL_PAY_TO_BUILDER.buildEntity(dto.getFreightBillPayTo()));

        return entity;
    }

    private OrganizationFreightBillPayToEntity getNewOrExistingEntity(Long id) {
        if (id == null) {
            return new OrganizationFreightBillPayToEntity();
        } else {
            if (dataProvider != null) {
                return dataProvider.getEntityById(id);
            } else {
                throw new IllegalArgumentException("DataProvider is not initialized");
            }
        }
    }

    /**
     * Data Provider Interface.
     * 
     * @author Artem Arapov
     *
     */
    public interface DataProvider {

        /**
         * Returns {@link OrganizationFreightBillPayToEntity} by specified id.
         * 
         * @param id Not <code>null</code> identifier of entity.
         * @return Existing entity if it was found, otherwise returns <code>null</code>
         */
        OrganizationFreightBillPayToEntity getEntityById(Long id);
    }
}
