package com.pls.dtobuilder.shipment;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.pls.core.dao.impl.FreightBillPayToDaoImpl;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.enums.PaymentTermsDTO;
import com.pls.dto.shipment.JobNumberDTO;
import com.pls.dto.shipment.ManualBolAddressDTO;
import com.pls.dto.shipment.ManualBolDTO;
import com.pls.dto.shipment.ManualBolMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.FreightBillPayToDTOBuilder;
import com.pls.dtobuilder.address.BillToDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.dtobuilder.product.ManualBolMaterialDTOBuilder;
import com.pls.shipment.domain.LoadAdditionalFieldsEntity;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolJobNumberEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.domain.ManualBolNumbersEntity;
import com.pls.shipment.domain.ManualBolRequestedByNoteEntity;

/**
 * Manual BOL Builder.
 * 
 * @author Alexander Nalapko
 */
public class ManualBolDTOBuilder extends AbstractDTOBuilder<ManualBolEntity, ManualBolDTO> {
    private static final int ELEVEN_HOURS = 11;
    private static final int TWELVE_HOURS = 12;
    private static final int THIRTY_MINUTES = 30;

    private DataProvider dataProvider;

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();
    private final BillToDTOBuilder billToBuilder = new BillToDTOBuilder();
    private final CarrierDTOBuilder carrierBuilder = new CarrierDTOBuilder();
    private final FreightBillPayToDTOBuilder freightBillPayToDTOBuilder = new FreightBillPayToDTOBuilder();
    private final ManualBolMaterialDTOBuilder manualBolMaterialDTOBuilder = new ManualBolMaterialDTOBuilder();

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            data provider
     */
    public ManualBolDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        manualBolMaterialDTOBuilder.setDataProvider(dataProvider);
    }

    @Override
    public ManualBolDTO buildDTO(ManualBolEntity bo) {
        ManualBolDTO dto = new ManualBolDTO();
        dto.setId(bo.getId());
        if (bo.getBillTo() != null) {
            dto.setBillTo(billToBuilder.buildDTO(bo.getBillTo()));
        }
        if (bo.getPayTerms() != null) {
            dto.setPaymentTerms(PaymentTermsDTO.valueOf(bo.getPayTerms().name()));
        }
        if (bo.getShipmentDirection() != null) {
            dto.setShipmentDirection(bo.getShipmentDirection().getCode());
        }
        dto.setBol(bo.getNumbers().getBolNumber());
        dto.setCarrier(carrierBuilder.buildDTO(bo.getCarrier()));
        dto.setDeliveryNotes(bo.getDeliveryNotes());
        dto.setDestination(getAddressDTO(bo.getDestination()));
        buildFreightBillPayToDTO(dto, bo);
        dto.setGlNumber(bo.getNumbers().getGlNumber());
        dto.setOpBolNumber(bo.getNumbers().getOpBolNumber());
        dto.setPartNumber(bo.getNumbers().getPartNumber());
        if (bo.getLocation() != null) {
            dto.setLocation(getLocationBO(bo.getLocation()));
        }
        dto.setMaterials(manualBolMaterialDTOBuilder.buildList(bo.getMaterials()));
        dto.setOrganizationId(bo.getOrganization().getId());
        dto.setCustomerName(bo.getOrganization().getName());
        dto.setOrigin(getAddressDTO(bo.getOrigin()));
        dto.setPickupDate(bo.getPickupDate());
        dto.setPickupNotes(bo.getPickupNotes());
        dto.setPo(bo.getNumbers().getPoNumber());
        dto.setPro(bo.getNumbers().getProNumber());
        dto.setPu(bo.getNumbers().getPuNumber());

        buildPickUpWindow(bo, dto);
        buildDeliveryWindow(bo, dto);

        if (bo.getRequestedBy() != null) {
            dto.setRequestedBy(bo.getRequestedBy().getNote());
        }

        dto.setRef(bo.getNumbers().getRefNumber());
        dto.setShippingNotes(bo.getShippingNotes());
        dto.setSoNumber(bo.getNumbers().getSoNumber());
        dto.setJobNumbers(buildJobNumberDtoList(bo.getNumbers().getJobNumbers()));
        dto.setStatus(bo.getStatus());
        dto.setTrailer(bo.getNumbers().getTrailerNumber());
        dto.setVersion(bo.getVersion());
        dto.setCustomsBroker(bo.getCustomsBroker());
        dto.setCustomsBrokerPhone(PhoneUtils.parse(bo.getCustomsBrokerPhone()));
        if (bo.getLoadAdditionalFields() != null) {
            dto.setCargoValue(bo.getLoadAdditionalFields().getCargoValue());
        }
        return dto;
    }

    @Override
    public ManualBolEntity buildEntity(ManualBolDTO dto) {
        ManualBolEntity bo;
        if (dto.getId() != null) {
            bo  = dataProvider.getManualBol(dto.getId());
        } else {
            bo = new ManualBolEntity();
        }

        bo.setBillTo(dataProvider.getBillTo(dto.getBillTo().getId()));
        if (dto.getPaymentTerms() != null) {
            bo.setPayTerms(PaymentTerms.valueOf(dto.getPaymentTerms().name()));
        }

        if (dto.getShipmentDirection() != null) {
            bo.setShipmentDirection(ShipmentDirection.getByCode(dto.getShipmentDirection()));
        }

        bo.getNumbers().setBolNumber(dto.getBol());
        bo.setCarrier(dataProvider.getCarrier(dto.getCarrier().getId()));
        bo.setDeliveryNotes(dto.getDeliveryNotes());
        buildFreightBillPayTo(bo, dto);
        bo.getNumbers().setGlNumber(dto.getGlNumber());
        bo.getNumbers().setOpBolNumber(dto.getOpBolNumber());
        bo.getNumbers().setPartNumber(dto.getPartNumber());
        buildJobNumbers(dto, bo);
        buildQuoteMaterials(dto, bo);
        updateOrganizationAndLocation(bo, dto.getOrganizationId(), dto.getLocation().getId());

        addOrUpdateAddress(bo, dto.getOrigin(), PointType.ORIGIN);
        addOrUpdateAddress(bo, dto.getDestination(), PointType.DESTINATION);

        if (dto.getRequestedBy() != null) {
            setRequestedBy(dto.getRequestedBy(), bo);
        }

        bo.setPickupDate(dto.getPickupDate());
        bo.setPickupNotes(dto.getPickupNotes());
        bo.getNumbers().setPoNumber(dto.getPo());
        bo.getNumbers().setProNumber(dto.getPro());
        bo.getNumbers().setPuNumber(dto.getPu());
        addPickupAndDeliveryWindowInfo(bo, dto);
        bo.getNumbers().setRefNumber(dto.getRef());
        bo.setShippingNotes(dto.getShippingNotes());
        bo.getNumbers().setSoNumber(dto.getSoNumber());
        bo.getNumbers().setTrailerNumber(dto.getTrailer());
        bo.setVersion(dto.getVersion());
        bo.setCustomsBroker(dto.getCustomsBroker());
        bo.setCustomsBrokerPhone(dto.getCustomsBrokerPhone() != null ? PhoneUtils.format(dto.getCustomsBrokerPhone()) : "");
        this.createAdditionalFields(dto, bo);
        return bo;
    }

    private void createAdditionalFields(ManualBolDTO dto, ManualBolEntity entity) {
        if (entity.getLoadAdditionalFields() == null) {
            LoadAdditionalFieldsEntity loadAddfields = new LoadAdditionalFieldsEntity();
            loadAddfields.setCargoValue(dto.getCargoValue());
            entity.setLoadAdditionalFields(loadAddfields);
            loadAddfields.setManualBol(entity);
        } else {
            entity.getLoadAdditionalFields().setCargoValue(dto.getCargoValue());
        }
    }

    private void setRequestedBy(String requestedBy, ManualBolEntity bo) {
        ManualBolRequestedByNoteEntity reqEntity = null;
        if (bo.getRequestedBy() != null) {
            reqEntity = bo.getRequestedBy();
        } else {
            reqEntity = new ManualBolRequestedByNoteEntity();
        }
        reqEntity.setLoad(bo);
        if (requestedBy.equalsIgnoreCase("")) {
            reqEntity.setNote(" ");
        } else {
            reqEntity.setNote(requestedBy);
        }
        bo.setRequestedBy(reqEntity);
    }
    private List<JobNumberDTO> buildJobNumberDtoList(Set<ManualBolJobNumberEntity> entities) {
        if (entities != null) {
            Collection<JobNumberDTO> dtoList = Collections2.transform(entities, new Function<ManualBolJobNumberEntity, JobNumberDTO>() {
                @Override
                public JobNumberDTO apply(ManualBolJobNumberEntity input) {
                    return new JobNumberDTO(input.getId(), input.getJobNumber());
                }
            });

            return ImmutableList.copyOf(dtoList);
        } else {
            return Collections.emptyList();
        }
    }

    private void buildJobNumbers(ManualBolDTO dto, ManualBolEntity entity) {
        ManualBolNumbersEntity numbersEntity = entity.getNumbers();
        List<JobNumberDTO> dtoList = getJobNumbersDtoList(dto);
        if (numbersEntity.getJobNumbers() == null) {
            Set<ManualBolJobNumberEntity> initSet = new HashSet<ManualBolJobNumberEntity>(dtoList.size());
            numbersEntity.setJobNumbers(initSet);
        }

        Set<ManualBolJobNumberEntity> jobEntitySet = numbersEntity.getJobNumbers();

        for (JobNumberDTO dtoItem : dtoList) {
            Long jobId = dtoItem.getId();
            ManualBolJobNumberEntity jobEntity = findJobNumberById(jobEntitySet, jobId);
            if (jobEntity != null) {
                jobEntity.setJobNumber(dtoItem.getJobNumber());
            } else {
                jobEntity = new ManualBolJobNumberEntity(entity.getId(), dtoItem.getJobNumber());
                jobEntitySet.add(jobEntity);
            }
        }

        removeJobNumbers(jobEntitySet, dtoList);
    }

    private List<JobNumberDTO> getJobNumbersDtoList(ManualBolDTO dto) {
        if (dto.getJobNumbers() == null) {
            return Collections.emptyList();
        } else {
            dto.getJobNumbers().removeIf(num -> StringUtils.isBlank(num.getJobNumber()));
            return dto.getJobNumbers();
        }
    }

    private ManualBolJobNumberEntity findJobNumberById(Set<ManualBolJobNumberEntity> jobNumbers, Long id) {
        for (ManualBolJobNumberEntity entity : jobNumbers) {
            if (entity.getId() != null && entity.getId().equals(id)) {
                return entity;
            }
        }

        return null;
    }

    private void removeJobNumbers(Set<ManualBolJobNumberEntity> entities, List<JobNumberDTO> dtos) {
        if (entities.size() != dtos.size()) {
            Iterator<ManualBolJobNumberEntity> iterator = entities.iterator();
            while (iterator.hasNext()) {
                if (!isJobDtoExists(iterator.next().getId(), dtos)) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean isJobDtoExists(final Long jobId, List<JobNumberDTO> dtos) {
        Optional<JobNumberDTO> obj = Optional.absent();
        if (jobId != null) {
            obj = Iterables.tryFind(dtos, new Predicate<JobNumberDTO>() {
                @Override
                public boolean apply(JobNumberDTO input) {
                    return input.getId().equals(jobId);
                }
            });
        }

        return obj.isPresent();
    }

    private void addOrUpdateAddress(ManualBolEntity bol, ManualBolAddressDTO dto, PointType type) {
        ManualBolAddressEntity address = null;
        if (type == PointType.ORIGIN) {
            address = bol.getOrigin();
        } else if (type == PointType.DESTINATION) {
            address = bol.getDestination();
        }
        if (address == null) {
            address = new ManualBolAddressEntity(type);
            address.setManualBol(bol);
            bol.addAddress(address);
        }
        address.setId(dto.getId());
        address.setAddress(dataProvider.getAddress(dto.getAddressId()));
        address.setAddress1(dto.getAddress1());
        address.setAddress2(dto.getAddress2());
        address.setAddressCode(dto.getAddressCode());
        address.setContact(dto.getAddressName());
        address.setContactEmail(dto.getEmail());
        address.setContactFax(dto.getFax() != null ? PhoneUtils.format(dto.getFax()) : "");
        address.setContactName(dto.getContactName());
        address.setContactPhone(dto.getPhone() != null ? PhoneUtils.format(dto.getPhone()) : "");
        address.setDeliveryNotes(dto.getDeliveryNotes());
        address.setPickupNotes(dto.getPickupNotes());
        address.setInternalPickupNotes(dto.getInternalPickupNotes());
        address.setInternalDeliveryNotes(dto.getInternalDeliveryNotes());
        address.setVersion(dto.getVersion());
    }

    private ShipmentLocationBO getLocationBO(OrganizationLocationEntity locationEntity) {
        ShipmentLocationBO location = new ShipmentLocationBO();
        if (locationEntity != null) {
            location.setId(locationEntity.getId());
            location.setName(locationEntity.getLocationName());
            if (locationEntity.getBillTo() != null) {
                location.setBillToId(locationEntity.getBillTo().getId());
            }
        }
        return location;
    }

    private ManualBolAddressDTO getAddressDTO(ManualBolAddressEntity entity) {
        ManualBolAddressDTO dto = new ManualBolAddressDTO();
        dto.setAddressId(entity.getAddress().getId());
        dto.setAddress1(entity.getAddress1());
        dto.setAddress2(entity.getAddress2());
        dto.setAddressCode(entity.getAddressCode());
        dto.setAddressName(entity.getContact());
        dto.setEmail(entity.getContactEmail());
        dto.setFax(PhoneUtils.parse(entity.getContactFax()));
        dto.setContactName(entity.getContactName());
        dto.setPhone(PhoneUtils.parse(entity.getContactPhone()));
        dto.setZip(zipDTOBuilder.buildDTO(entity.getAddress().getZipCode()));
        dto.setDeliveryNotes(entity.getDeliveryNotes());
        dto.setInternalDeliveryNotes(entity.getInternalDeliveryNotes());
        dto.setId(entity.getId());
        dto.setPickupNotes(entity.getPickupNotes());
        dto.setInternalPickupNotes(entity.getInternalPickupNotes());
        dto.setVersion(entity.getVersion());
        return dto;

    }

    private void updateOrganizationAndLocation(ManualBolEntity entity, Long orgId, Long locId) {
        if (entity.getOrganization() == null || !entity.getOrganization().getId().equals(orgId)) {
            CustomerEntity organization = new CustomerEntity();
            organization.setId(orgId);
            organization.setPrintBarcode(dataProvider.isPrintBarcode(orgId));
            entity.setOrganization(organization);
        }
        if (entity.getLocation() == null || !entity.getLocation().getId().equals(orgId)) {
            entity.setLocation(new OrganizationLocationEntity());
            entity.getLocation().setId(locId);
        }
    }

    private void buildFreightBillPayTo(final ManualBolEntity entity, ManualBolDTO dto) {
        if (dto.getFreightBillPayTo() == null
                || FreightBillPayToDaoImpl.DEFAULT_FREIGHT_BILL_PAY_TO_ID.equals(dto.getFreightBillPayTo().getId())) {
            entity.setFreightBillPayTo(dataProvider.getDefaultFreightBillPayTo());
        } else if (dto.getFreightBillPayTo() != null && dto.getFreightBillPayTo().getId() != null) {
            entity.setFreightBillPayTo(dataProvider.getFreightBillPayTo(dto.getFreightBillPayTo().getId()));
        } else {
            entity.setFreightBillPayTo(buildFreightBillPayToFromDTO(entity, dto));
        }
    }

    private FreightBillPayToEntity buildFreightBillPayToFromDTO(final ManualBolEntity entity, ManualBolDTO dto) {
        FreightBillPayToDTOBuilder builder = new FreightBillPayToDTOBuilder();
        builder.setDataProvider(new FreightBillPayToDTOBuilder.DataProvider() {
            @Override
            public FreightBillPayToEntity getFreightBillPayTo() {
                return entity.getFreightBillPayTo();
            }
        });

        return builder.buildEntity(dto.getFreightBillPayTo());
    }

    private void buildFreightBillPayToDTO(ManualBolDTO dto, ManualBolEntity entity) {
        if (entity.getFreightBillPayTo() == null) {
            entity.setFreightBillPayTo(dataProvider.getDefaultFreightBillPayTo());
        }
        dto.setFreightBillPayTo(freightBillPayToDTOBuilder.buildDTO(entity.getFreightBillPayTo()));
    }

    private void buildPickUpWindow(ManualBolEntity bo, ManualBolDTO dto) {
        if (bo.getPickupWindowFrom() != null && bo.getPickupWindowTo() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bo.getPickupWindowFrom());
            dto.setPickupWindowFrom(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
            calendar.setTime(bo.getPickupWindowTo());
            dto.setPickupWindowTo(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
        }
    }

    private void buildDeliveryWindow(ManualBolEntity bo, ManualBolDTO dto) {
        if (bo.getDeliveryWindowFrom() != null && bo.getDeliveryWindowTo() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bo.getDeliveryWindowFrom());
            dto.setDeliveryWindowFrom(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
            calendar.setTime(bo.getDeliveryWindowTo());
            dto.setDeliveryWindowTo(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
        }
    }

    private void addPickupAndDeliveryWindowInfo(ManualBolEntity entity, ManualBolDTO dto) {
        addPickupWindow(entity, dto);
        addDeliveryWindow(entity, dto);
    }

    private void addPickupWindow(ManualBolEntity entity, ManualBolDTO dto) {
        if (dto.getPickupDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dto.getPickupDate());

            prepareCalendarFromPickupDeliveryWindowDTO(calendar, dto.getPickupWindowFrom(), false);
            entity.setPickupWindowFrom(calendar.getTime());

            prepareCalendarFromPickupDeliveryWindowDTO(calendar, dto.getPickupWindowTo(), true);
            entity.setPickupWindowTo(calendar.getTime());
        }
    }

    private void addDeliveryWindow(ManualBolEntity entity, ManualBolDTO dto) {
        Calendar calendar = Calendar.getInstance();
        if (dto.getDeliveryWindowFrom() != null) {
            prepareCalendarFromPickupDeliveryWindowDTO(calendar, dto.getDeliveryWindowFrom(), false);
            entity.setDeliveryWindowFrom(calendar.getTime());
        }
        if (dto.getDeliveryWindowTo() != null) {
            prepareCalendarFromPickupDeliveryWindowDTO(calendar, dto.getDeliveryWindowTo(), true);
            entity.setDeliveryWindowTo(calendar.getTime());
        }
    }

    private void buildQuoteMaterials(ManualBolDTO dto, ManualBolEntity entity) {
        Set<ManualBolMaterialEntity> lmList = entity.getMaterials();
        if (lmList == null) {
            lmList = new HashSet<ManualBolMaterialEntity>(dto.getMaterials().size());
            entity.setMaterials(lmList);
        }

        List<ManualBolMaterialDTO> dtoMaterials = dto.getMaterials();
        for (ManualBolMaterialDTO material : dtoMaterials) {
            Long materialId = material.getId();
            ManualBolMaterialEntity materialEntity = findById(lmList, materialId);
            if (materialEntity == null) {
                materialEntity = manualBolMaterialDTOBuilder.buildEntity(material);
                materialEntity.setManualBol(entity);
                lmList.add(materialEntity);
            } else {
                manualBolMaterialDTOBuilder.updateEntity(materialEntity, material);
            }
        }

        removeNotExistMaterials(lmList, dtoMaterials);
    }

    private void prepareCalendarFromPickupDeliveryWindowDTO(Calendar calendar, PickupAndDeliveryWindowDTO windowDTO,
            boolean isTo) {
        if (windowDTO != null && windowDTO.getAm() != null) {
            calendar.set(Calendar.HOUR, windowDTO.getHours() == TWELVE_HOURS ? 0 : windowDTO.getHours());
            calendar.set(Calendar.MINUTE, windowDTO.getMinutes());
            calendar.set(Calendar.AM_PM, windowDTO.getAm() ? Calendar.AM : Calendar.PM);
        } else {
            if (isTo) {
                calendar.set(Calendar.HOUR, ELEVEN_HOURS);
                calendar.set(Calendar.MINUTE, THIRTY_MINUTES);
                calendar.set(Calendar.AM_PM, Calendar.PM);
            } else {
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.AM_PM, Calendar.AM);
            }
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private ManualBolMaterialEntity findById(Set<ManualBolMaterialEntity> lmList, Long id) {
        ManualBolMaterialEntity result = null;
        for (ManualBolMaterialEntity material : lmList) {
            Long materialId = material.getId();
            if (materialId != null && materialId.equals(id)) {
                result = material;
                break;
            }
        }
        return result;
    }

    private void removeNotExistMaterials(Set<ManualBolMaterialEntity> lmList, List<ManualBolMaterialDTO> dtoMaterials) {
        if (lmList.size() != dtoMaterials.size()) {
            Iterator<ManualBolMaterialEntity> materialsIterator = lmList.iterator();
            while (materialsIterator.hasNext()) {
                if (isMaterialRemoved(dtoMaterials, materialsIterator.next().getId())) {
                    materialsIterator.remove();
                }
            }
        }
    }

    private boolean isMaterialRemoved(List<ManualBolMaterialDTO> dtoMaterials, Long id) {
        if (id == null) {
            return false;
        }
        for (ManualBolMaterialDTO materialDTO : dtoMaterials) {
            if (id.equals(materialDTO.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider extends ManualBolMaterialDTOBuilder.DataProvider {
        /**
         * Get manual BOL by id.
         * 
         * @param id
         *            {@link ManualBolEntity#getId()}
         * @return {@link ManualBolEntity}
         */
        ManualBolEntity getManualBol(Long id);

        /**
         * Get bill to by id.
         * 
         * @param id
         *            {@link BillToEntity#getId()}
         * @return {@link BillToEntity}
         */
        BillToEntity getBillTo(Long id);

        /**
         * Get Ltl Address for update.
         * 
         * @param id
         *            {@link AddressEntity#getId()}
         * @return {@link AddressEntity}
         */
        AddressEntity getAddress(Long id);

        /**
         * Get Carrier for update.
         * 
         * @param id
         *            {@link CarrierEntity#getId()}
         * @return {@link CarrierEntity}
         */
        CarrierEntity getCarrier(Long id);

        /**
         * Get instance of default {@link FreightBillPayToEntity}.
         * 
         * @return {@link FreightBillPayToEntity}
         */
        FreightBillPayToEntity getDefaultFreightBillPayTo();

        /**
         * Get instance of {@link FreightBillPayToEntity}.
         * 
         * @param id Not <code>null</code> unique identifier.
         * @return {@link FreightBillPayToEntity}
         */
        FreightBillPayToEntity getFreightBillPayTo(Long id);

        /**
         * Check should we add a barcode to the BOL document.
         * 
         * @param customerId
         *            id of customer
         * @return true in case when barcode should be added to the BOL document
         */
        Boolean isPrintBarcode(Long customerId);
    }
}
