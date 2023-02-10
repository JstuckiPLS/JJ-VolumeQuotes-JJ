package com.pls.dtobuilder.organization;

import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.dto.organization.CustomerLocationDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for {@link CustomerLocationDTO}.
 * 
 * @author Artem Arapov
 *
 */
public class CustomerLocationDTOBuilder extends AbstractDTOBuilder<OrganizationLocationEntity, CustomerLocationDTO> {

    private DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider - data provider for location builder.
     */
    public CustomerLocationDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public CustomerLocationDTO buildDTO(OrganizationLocationEntity bo) {
        CustomerLocationDTO dto = new CustomerLocationDTO();
        dto.setId(bo.getId());
        dto.setName(bo.getLocationName());
        dto.setDefaultNode(bo.getDefaultNode());

        AccountExecutiveEntity activeAccountExecutive = bo.getActiveAccountExecutive();

        if (activeAccountExecutive != null) {
            dto.setAccExecPersonId(activeAccountExecutive.getUser().getId());
            dto.setAccExecStartDate(activeAccountExecutive.getEffectiveDate());
            dto.setAccExecEndDate(activeAccountExecutive.getExpirationDate());
        }

        BillToEntity billTo = bo.getBillTo();
        if (billTo != null) {
            dto.setBillToId(billTo.getId());
        }

        return dto;
    }

    @Override
    public OrganizationLocationEntity buildEntity(CustomerLocationDTO dto) {
        OrganizationLocationEntity entity = getOrganizationLocationEntity(dto.getId(), dto.getOrgId());

        entity.setLocationName(dto.getName());
        buildAccountExecutiveEntity(dto, entity);
        buildBillToEntity(dto, entity);

        return entity;
    }

    private OrganizationLocationEntity getOrganizationLocationEntity(Long locationId, Long orgId) {
        if (locationId != null) {
            return dataProvider.getOrganizationLocation(locationId);
        }

        OrganizationLocationEntity location = new OrganizationLocationEntity();
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(orgId);
        location.setOrganization(organization);

        return location;
    }

    private void buildAccountExecutiveEntity(CustomerLocationDTO dto, OrganizationLocationEntity locationEntity) {
        AccountExecutiveEntity activeAccExec  = locationEntity.getActiveAccountExecutive();
        if (dto.getAccExecPersonId() == null && activeAccExec != null) {
            activeAccExec.setStatus(Status.INACTIVE);
        } else if (dto.getAccExecPersonId() != null) {
            UserEntity user = new UserEntity();
            user.setId(dto.getAccExecPersonId());

            if (activeAccExec == null) {
                activeAccExec = new AccountExecutiveEntity(locationEntity, user);
                locationEntity.getAccountExecutives().add(activeAccExec);
            } else {
                activeAccExec.setUser(user);
            }

            activeAccExec.setEffectiveDate(dto.getAccExecStartDate());
            activeAccExec.setExpirationDate(dto.getAccExecEndDate());
        }
    }

    private void buildBillToEntity(CustomerLocationDTO dto, OrganizationLocationEntity locationEntity) {
        BillToEntity billTo = null;
        if (dto.getBillToId() != null) {
            billTo = new BillToEntity();
            billTo.setId(dto.getBillToId());
        }

        locationEntity.setBillTo(billTo);
    }

    /**
     * Location data provider.
     */
    public interface DataProvider {

        /**
         * Returns {@link OrganizationLocationEntity} by specified id.
         * 
         * @param id - id of customer location.
         * @return {@link OrganizationLocationEntity}.
         */
        OrganizationLocationEntity getOrganizationLocation(Long id);
    }
}
