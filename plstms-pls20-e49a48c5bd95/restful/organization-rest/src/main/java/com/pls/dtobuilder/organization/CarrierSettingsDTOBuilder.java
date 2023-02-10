package com.pls.dtobuilder.organization;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.EdiRejectedCustomerEntity;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.core.domain.organization.PaperworkEmailEntity;
import com.pls.dto.KeyValueDTO;
import com.pls.dto.organization.CarrierSettingsDTO;
import com.pls.dto.organization.PaperworkDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Carrier Data Transfer Object Builder.
 * 
 * @author Artem Arapov
 *
 */
public final class CarrierSettingsDTOBuilder extends AbstractDTOBuilder<CarrierEntity, CarrierSettingsDTO> {

    private static final OrgServiceDTOBuilder ORG_SERVICE_DTO_BUILDER = new OrgServiceDTOBuilder();

    private DataProvider dataProvider;

    /**
     * Default Constructor.
     */
    private CarrierSettingsDTOBuilder() {
    }

    /**
     * Returns instance of {@link CarrierSettingsDTOBuilder}.
     * 
     * @return {@link CarrierSettingsDTOBuilder}
     */
    public static CarrierSettingsDTOBuilder builder() {
        return new CarrierSettingsDTOBuilder();
    }

    /**
     * Adds implementation of {@link DataProvider} to {@link CarrierSettingsDTOBuilder}.
     * 
     * @param dataProvider - Not <code>null</code>. Implementation of {@link DataProvider}.
     * @return current instance of {@link CarrierDTOBuidler}.
     */
    public CarrierSettingsDTOBuilder addDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;

        return this;
    }

    @Override
    public CarrierSettingsDTO buildDTO(CarrierEntity bo) {
        CarrierSettingsDTO dto = new CarrierSettingsDTO();
        dto.setId(bo.getId());
        dto.setName(bo.getName());
        dto.setScac(bo.getScac());
        dto.setActualScac(bo.getActualScac());
        if (bo.getOrgServiceEntity() != null) {
            dto.setOrgService(ORG_SERVICE_DTO_BUILDER.buildDTO(bo.getOrgServiceEntity()));
        } else {
            dto.setOrgService(ORG_SERVICE_DTO_BUILDER.buildDTO(new OrgServiceEntity()));
        }

        if (bo.getPaperworkEmail() != null) {
            PaperworkDTO paperwork = new PaperworkDTO();
            paperwork.setDontRequestPaperwork(bo.getPaperworkEmail().isDontRequestPaperwork());
            paperwork.setEmail(bo.getPaperworkEmail().getEmail());
            dto.setPaperwork(paperwork);
        }

        List<KeyValueDTO> rejectedCustomers = bo.getRejectedCustomers().stream().
                map(t -> new KeyValueDTO(t.getCustomerId(), t.getCustomer().getName())).collect(Collectors.toList());

        dto.setRejectedCustomers(rejectedCustomers);

        return dto;
    }

    @Override
    public CarrierEntity buildEntity(CarrierSettingsDTO dto) {
        CarrierEntity entity = getEntity(dto.getId());
        buildOrgServices(dto, entity);
        buildRejectedCustomers(dto, entity);
        buildPaperworkEmail(dto, entity);

        return entity;
    }

    private void buildOrgServices(CarrierSettingsDTO dto, CarrierEntity entity) {
        OrgServiceEntity newEntity = ORG_SERVICE_DTO_BUILDER.buildEntity(dto.getOrgService());
        if (entity.getOrgServiceEntity() != null) {
            OrgServiceEntity rawEntity = entity.getOrgServiceEntity();
            rawEntity.setImaging(newEntity.getImaging());
            rawEntity.setInvoice(newEntity.getInvoice());
            rawEntity.setManualTypeEmail(newEntity.getManualTypeEmail());
            rawEntity.setOrgId(entity.getId());
            rawEntity.setPickup(newEntity.getPickup());
            rawEntity.setRating(newEntity.getRating());
            rawEntity.setTracking(newEntity.getTracking());
        } else {
            newEntity.setOrgId(entity.getId());
            entity.setOrgServiceEntity(newEntity);
        }
    }

    private void buildRejectedCustomers(CarrierSettingsDTO dto, CarrierEntity entity) {
        if (entity.getRejectedCustomers() == null) {
            entity.setRejectedCustomers(Sets.newHashSetWithExpectedSize(dto.getRejectedCustomers().size()));
        }

        for (KeyValueDTO item : dto.getRejectedCustomers()) {
            if (!isCustomerAlreadyExistsInEntity(item.getId(), entity)) {
                EdiRejectedCustomerEntity customer = new EdiRejectedCustomerEntity();
                customer.setCarrierId(dto.getId());
                customer.setCustomerId(item.getId());
                entity.getRejectedCustomers().add(customer);
            }
        }

        removeUnnecessaryCustomersFromEntity(entity, dto);
    }

    private void buildPaperworkEmail(CarrierSettingsDTO dto, CarrierEntity entity) {
        if (entity.getPaperworkEmail() == null) {
            PaperworkEmailEntity paperworkEmailEntity = new PaperworkEmailEntity();
            paperworkEmailEntity.setDontRequestPaperwork(dto.getPaperwork().isDontRequestPaperwork());
            paperworkEmailEntity.setEmail(dto.getPaperwork().getEmail());
            paperworkEmailEntity.setOrgId(entity.getId());
            entity.setPaperworkEmail(paperworkEmailEntity);
        } else {
            PaperworkEmailEntity paperworkEmailEntity = entity.getPaperworkEmail();
            paperworkEmailEntity.setDontRequestPaperwork(dto.getPaperwork().isDontRequestPaperwork());
            paperworkEmailEntity.setEmail(dto.getPaperwork().getEmail());
        }
    }

    private void removeUnnecessaryCustomersFromEntity(CarrierEntity entity, CarrierSettingsDTO dto) {
        Iterator<EdiRejectedCustomerEntity> iterator = entity.getRejectedCustomers().iterator();
        while (iterator.hasNext()) {
            EdiRejectedCustomerEntity item = iterator.next();
            if (!isCustomerAlreadyExistsInDTO(item.getCustomerId(), dto)) {
                iterator.remove();
            }
        }
    }

    private boolean isCustomerAlreadyExistsInEntity(Long id, CarrierEntity entity) {
        return entity.getRejectedCustomers().stream().filter(t -> t.getCustomerId().equals(id)).findFirst().isPresent();
    }

    private boolean isCustomerAlreadyExistsInDTO(Long id, CarrierSettingsDTO dto) {
        return dto.getRejectedCustomers().stream().filter(t -> t.getId() != null && (t.getId()).equals(id)).findFirst().isPresent();
    }

    private CarrierEntity getEntity(Long id) {
        return dataProvider.getCarrierEntity(id);
    }

    /**
     * Carrier Data Provider.
     * 
     * @author Artem Arapov
     *
     */
    public interface DataProvider {

        /**
         * Returns existing {@link CarrierEntity} by specified id.
         * 
         * @param id - {@link CarrierEntity#getId()}
         * @return {@link CarrierEntity}
         */
        CarrierEntity getCarrierEntity(Long id);
    }
}
