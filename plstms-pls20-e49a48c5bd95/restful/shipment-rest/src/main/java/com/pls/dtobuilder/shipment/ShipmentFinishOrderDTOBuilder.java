package com.pls.dtobuilder.shipment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.dto.ShipmentNotificationsDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.shipment.JobNumberDTO;
import com.pls.dto.shipment.ShipmentFinishOrderDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.product.ShipmentMaterialDTOBuilder;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.domain.ShipmentRequestedByNoteEntity;

/**
 * DTO builder for {@link ShipmentFinishOrderDTO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentFinishOrderDTOBuilder extends AbstractDTOBuilder<LoadEntity, ShipmentFinishOrderDTO> {
    private static final int ELEVEN = 11;
    private static final int TWELVE = 12;
    private static final int THIRTY = 30;

    private final ShipmentNotificationsDTOBuilder notificationBuilder = new ShipmentNotificationsDTOBuilder();

    private final ShipmentMaterialDTOBuilder shipmentMaterialDTOBuilder = new ShipmentMaterialDTOBuilder();

    private DataProvider dataProvider;

    @Override
    public ShipmentFinishOrderDTO buildDTO(LoadEntity entity) {
        ShipmentFinishOrderDTO dto = new ShipmentFinishOrderDTO();

        buildShipmentNotifications(dto, entity.getLoadNotifications());

        if (entity.getDestination() != null) {
            dto.setActualDeliveryDate(entity.getDestination().getDeparture());
            dto.setEstimatedDelivery(entity.getDestination().getScheduledArrival());
            buildDeliveryWindow(entity.getDestination(), dto);
            dto.setDestinationDetailsId(entity.getDestination().getId());
            dto.setDestinationVersion(entity.getDestination().getVersion());
        }
        if (entity.getOrigin() != null) {
            dto.setActualPickupDate(entity.getOrigin().getDeparture());
            dto.setPickupDate(entity.getOrigin().getEarlyScheduledArrival());
            buildPickUpWindow(entity.getOrigin(), dto);

            dto.setOriginDetailsId(entity.getOrigin().getId());
            dto.setQuoteMaterials(shipmentMaterialDTOBuilder.buildList(entity.getOrigin().getLoadMaterials()));
            dto.setOriginVersion(entity.getOrigin().getVersion());
        } else {
            dto.setQuoteMaterials(Collections.<ShipmentMaterialDTO>emptyList());
        }
        dto.setRef(entity.getNumbers().getRefNumber());
        dto.setPoNumber(entity.getNumbers().getPoNumber());
        dto.setPuNumber(entity.getNumbers().getPuNumber());
        dto.setSoNumber(entity.getNumbers().getSoNumber());
        dto.setGlNumber(entity.getNumbers().getGlNumber());
        dto.setOpBolNumber(entity.getNumbers().getOpBolNumber());
        dto.setPartNumber(entity.getNumbers().getPartNumber());
        dto.setTrailerNumber(entity.getNumbers().getTrailerNumber());
        dto.setJobNumbers(buildJobNumbersDTO(entity.getNumbers().getJobNumbers()));

        dto.setDeliveryNotes(entity.getDeliveryNotes());
        dto.setPickupNotes(entity.getSpecialInstructions());
        dto.setShippingLabelNotes(entity.getBolInstructions());

        if (entity.getRequestedBy() != null) {
            dto.setRequestedBy(entity.getRequestedBy().getNote());
        }

        return dto;
    }

    @Override
    public LoadEntity buildEntity(ShipmentFinishOrderDTO dto) {
        if (dataProvider == null) {
            throw new IllegalStateException("DataProvider must not be null!");
        }
        LoadEntity entity = dataProvider.getLoadEntity();

        buildShipmentNotifications(entity, dto.getShipmentNotifications());

        entity.getNumbers().setRefNumber(dto.getRef());
        entity.getNumbers().setPoNumber(dto.getPoNumber());
        entity.getNumbers().setPuNumber(dto.getPuNumber());

        entity.getNumbers().setGlNumber(dto.getGlNumber());
        entity.getNumbers().setOpBolNumber(dto.getOpBolNumber());
        entity.getNumbers().setPartNumber(dto.getPartNumber());
        entity.getNumbers().setSoNumber(dto.getSoNumber());
        entity.getNumbers().setTrailerNumber(dto.getTrailerNumber());

        entity.setDeliveryNotes(dto.getDeliveryNotes());
        entity.setSpecialInstructions(dto.getPickupNotes());
        entity.setBolInstructions(dto.getShippingLabelNotes());

        if (dto.getRequestedBy() != null) {
            setRequestedBy(dto.getRequestedBy(), entity);
        }

        buildLoadDetails(dto, entity);
        buildQuoteMaterials(dto, entity);
        buildJobNumbers(dto, entity);

        return entity;
    }

    private void setRequestedBy(String requestedBy, LoadEntity entity) {
        ShipmentRequestedByNoteEntity reqEntity = null;
        if (entity.getRequestedBy() != null) {
            reqEntity = entity.getRequestedBy();
        } else {
            reqEntity = new ShipmentRequestedByNoteEntity();
        }
        reqEntity.setLoad(entity);
        if (requestedBy.equalsIgnoreCase("")) {
            reqEntity.setNote(" ");
        } else {
            reqEntity.setNote(requestedBy);
        }
        entity.setRequestedBy(reqEntity);
    }

    private void buildLoadDetails(ShipmentFinishOrderDTO dto, LoadEntity entity) {
        LoadDetailsEntity origin = entity.getOrigin();
        LoadDetailsEntity destination = entity.getDestination();
        if (origin == null) {
            origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
            origin.setLoad(entity);
            entity.addLoadDetails(origin);

        } else {
            origin.setVersion(dto.getOriginVersion());
        }
        if (destination == null) {
            destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
            destination.setLoad(entity);
            entity.addLoadDetails(destination);
        } else {
            destination.setVersion(dto.getDestinationVersion());
        }

        updateLoadDetailsAddress(dataProvider.getOriginAddress(), origin);
        updateLoadDetailsAddress(dataProvider.getDestinationAddress(), destination);

        addPickupInformation(origin, dto);

        Calendar calendar = Calendar.getInstance();
        if (dto.getDeliveryWindowFrom() != null) {
            setDataToCalendarFromPickupWindowDTO(calendar, dto.getDeliveryWindowFrom(), false);
            destination.setArrivalWindowStart(calendar.getTime());
        }
        if (dto.getDeliveryWindowTo() != null) {
            setDataToCalendarFromPickupWindowDTO(calendar, dto.getDeliveryWindowTo(), true);
            destination.setArrivalWindowEnd(calendar.getTime());
        }
        destination.setDeparture(dto.getActualDeliveryDate());

        setEstimatedDeliveryDate(dto, entity);

        origin.setDeparture(dto.getActualPickupDate());
    }

    private void setEstimatedDeliveryDate(ShipmentFinishOrderDTO dto, LoadEntity entity) {
        if (dto.getEstimatedDelivery() == null || entity.getDestination().getScheduledArrival() == null
                || !DateUtils.isSameDay(dto.getEstimatedDelivery(), entity.getDestination().getScheduledArrival())) {
            // update scheduled delivery date only if it's changed in order not to loose time information
            entity.getDestination().setScheduledArrival(dto.getEstimatedDelivery());
        }
        if (entity.getDestination().getScheduledArrival() != null
                && entity.getDestination().getScheduledArrival().before(entity.getOrigin().getScheduledArrival())) {
            entity.getDestination().setScheduledArrival(entity.getOrigin().getScheduledArrival());
        }
        if (entity.getDestination().getEarlyScheduledArrival() != null
                && entity.getDestination().getEarlyScheduledArrival().before(entity.getOrigin().getEarlyScheduledArrival())) {
            entity.getDestination().setEarlyScheduledArrival(entity.getOrigin().getEarlyScheduledArrival());
        }
    }

    private void buildQuoteMaterials(ShipmentFinishOrderDTO dto, LoadEntity entity) {
        LoadDetailsEntity origin = entity.getOrigin();
        if (origin == null) {
            return;
        }

        Set<LoadMaterialEntity> lmList = origin.getLoadMaterials();
        if (lmList == null) {
            lmList = new HashSet<LoadMaterialEntity>(dto.getQuoteMaterials().size());
            origin.setLoadMaterials(lmList);
        }

        BigDecimal weight = BigDecimal.ZERO;
        Integer pieces = 0;
        entity.setHazmat(false);
        List<ShipmentMaterialDTO> dtoMaterials = dto.getQuoteMaterials();
        for (ShipmentMaterialDTO material : dtoMaterials) {
            Long materialId = material.getId();
            LoadMaterialEntity loadMaterialEntity = findById(lmList, materialId);
            if (loadMaterialEntity == null) {
                loadMaterialEntity = shipmentMaterialDTOBuilder.buildEntity(material);
                loadMaterialEntity.setPickupDate(origin.getArrivalWindowStart());
                loadMaterialEntity.setLoadDetail(origin);
                lmList.add(loadMaterialEntity);
            } else {
                shipmentMaterialDTOBuilder.updateEntity(loadMaterialEntity, material);
            }
            if (loadMaterialEntity.isHazmat()) {
                entity.setHazmat(true);
            }
            weight = weight.add(material.getWeight());
            if (StringUtils.isNotBlank(material.getQuantity())) {
                pieces += Integer.parseInt(material.getQuantity());
            }
        }
        entity.setWeight(weight.setScale(0, RoundingMode.CEILING).intValue());
        entity.setPieces(pieces);

        removeNotExistMaterials(lmList, dtoMaterials);
    }

    private LoadMaterialEntity findById(Set<LoadMaterialEntity> dtoMaterials, Long id) {
        LoadMaterialEntity result = null;
        for (LoadMaterialEntity material : dtoMaterials) {
            Long materialId = material.getId();
            if (materialId != null && materialId.equals(id)) {
                result = material;
                break;
            }
        }
        return result;
    }

    private void removeNotExistMaterials(Set<LoadMaterialEntity> lmList, List<ShipmentMaterialDTO> dtoMaterials) {
        if (lmList.size() != dtoMaterials.size()) {
            Iterator<LoadMaterialEntity> materialsIterator = lmList.iterator();
            while (materialsIterator.hasNext()) {
                if (isMaterialRemoved(dtoMaterials, materialsIterator.next().getId())) {
                    materialsIterator.remove();
                }
            }
        }
    }

    private boolean isMaterialRemoved(List<ShipmentMaterialDTO> dtoMaterials, Long id) {
        if (id == null) {
            return false;
        }
        for (ShipmentMaterialDTO materialDTO : dtoMaterials) {
            if (id.equals(materialDTO.getId())) {
                return false;
            }
        }
        return true;
    }

    private void updateLoadDetailsAddress(AddressBookEntryDTO addressDTO, LoadDetailsEntity loadDetailsEntity) {
        if (addressDTO != null && loadDetailsEntity != null) {
            loadDetailsEntity.setContact(addressDTO.getAddressName());
            loadDetailsEntity.setAddressCode(addressDTO.getAddressCode());
            loadDetailsEntity.setContactName(addressDTO.getContactName());
            loadDetailsEntity.setContactPhone(PhoneUtils.format(addressDTO.getPhone()));
            loadDetailsEntity.setContactFax(PhoneUtils.format(addressDTO.getFax()));
            loadDetailsEntity.setContactEmail(addressDTO.getEmail());

            setAddress(addressDTO, loadDetailsEntity);
            loadDetailsEntity.setNotes(loadDetailsEntity.getPointType() == PointType.ORIGIN ? addressDTO.getPickupNotes()
                    : addressDTO.getDeliveryNotes());
            loadDetailsEntity.setInternalNotes(loadDetailsEntity.getPointType() == PointType.ORIGIN ? addressDTO.getInternalPickupNotes()
                    : addressDTO.getInternalDeliveryNotes());
        }
    }

    private void setAddress(AddressBookEntryDTO addressDTO, LoadDetailsEntity loadDetailsEntity) {
        if (addressDTO.getAddressId() == null) {
            loadDetailsEntity.setAddress(fillUpAddressEntity(addressDTO, loadDetailsEntity.getAddress()));
        } else {
            loadDetailsEntity.setAddress(dataProvider.getAddress(addressDTO.getAddressId()));
        }
    }

    private AddressEntity fillUpAddressEntity(AddressBookEntryDTO addressDTO, AddressEntity addressEntity) {
        if (addressEntity != null) {
            addressEntity.setAddress1(addressDTO.getAddress1());
            addressEntity.setAddress2(addressDTO.getAddress2());

            CountryEntity country = new CountryEntity();
            country.setName(addressDTO.getCountry().getName());
            country.setPhoneCode(addressDTO.getCountry().getDialingCode());
            country.setId(addressDTO.getCountry().getId());

            addressEntity.setCountry(country);
            addressEntity.setCity(addressDTO.getZip().getCity());
            addressEntity.setStateCode(addressDTO.getZip().getState());
            addressEntity.setZip(addressDTO.getZip().getZip());
        }
        return addressEntity;
    }

    private void addPickupInformation(LoadDetailsEntity entity, ShipmentFinishOrderDTO dto) {
        if (dto.getPickupDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dto.getPickupDate());

            setDataToCalendarFromPickupWindowDTO(calendar, dto.getPickupWindowFrom(), false);
            entity.setArrivalWindowStart(calendar.getTime());
            entity.setEarlyScheduledArrival(calendar.getTime());

            setDataToCalendarFromPickupWindowDTO(calendar, dto.getPickupWindowTo(), true);
            entity.setArrivalWindowEnd(calendar.getTime());
            entity.setScheduledArrival(calendar.getTime());
        }
    }

    private void setDataToCalendarFromPickupWindowDTO(Calendar calendar, PickupAndDeliveryWindowDTO pickupWindowDTO,
            boolean isTo) {
        if (pickupWindowDTO != null && pickupWindowDTO.getAm() != null) {
            calendar.set(Calendar.HOUR, pickupWindowDTO.getHours() == TWELVE ? 0 : pickupWindowDTO.getHours());
            calendar.set(Calendar.MINUTE, pickupWindowDTO.getMinutes());
            calendar.set(Calendar.AM_PM, pickupWindowDTO.getAm() ? Calendar.AM : Calendar.PM);
        } else {
            if (isTo) {
                calendar.set(Calendar.HOUR, ELEVEN);
                calendar.set(Calendar.MINUTE, THIRTY);
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

    private void buildShipmentNotifications(ShipmentFinishOrderDTO dto,
            Set<LoadNotificationsEntity> loadNotifications) {
        if (loadNotifications != null && !loadNotifications.isEmpty()) {
            dto.setShipmentNotifications(notificationBuilder.buildList(loadNotifications));
        }
    }

    private void buildShipmentNotifications(LoadEntity entity, List<ShipmentNotificationsDTO> shipmentNotifications) {
        if (shipmentNotifications != null) {
            Set<LoadNotificationsEntity> loadNotifications = entity.getLoadNotifications();

            if (loadNotifications == null) {
                loadNotifications = new HashSet<LoadNotificationsEntity>(shipmentNotifications.size());
                entity.setLoadNotifications(loadNotifications);
            } else {
                loadNotifications.clear();
            }

            for (ShipmentNotificationsDTO shipmentNotification : shipmentNotifications) {
                LoadNotificationsEntity loadNotificationsEntity = notificationBuilder
                        .buildEntity(shipmentNotification);
                loadNotificationsEntity.setLoad(entity);
                loadNotifications.add(loadNotificationsEntity);
            }
        }
    }

    private void buildPickUpWindow(LoadDetailsEntity origin, ShipmentFinishOrderDTO dto) {
        if (origin.getArrivalWindowStart() != null && origin.getArrivalWindowEnd() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(origin.getArrivalWindowStart());
            dto.setPickupWindowFrom(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
            calendar.setTime(origin.getArrivalWindowEnd());
            dto.setPickupWindowTo(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
        }
    }

    private void buildDeliveryWindow(LoadDetailsEntity destination, ShipmentFinishOrderDTO dto) {
        if (destination.getArrivalWindowStart() != null && destination.getArrivalWindowEnd() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(destination.getArrivalWindowStart());
            dto.setDeliveryWindowFrom(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
            calendar.setTime(destination.getArrivalWindowEnd());
            dto.setDeliveryWindowTo(new PickupAndDeliveryWindowDTO(calendar.get(Calendar.HOUR), calendar
                    .get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == Calendar.AM));
        }
    }

    private List<JobNumberDTO> buildJobNumbersDTO(Set<LoadJobNumbersEntity> jobNumbers) {
        if (jobNumbers == null) {
            return Collections.emptyList();
        } else {
            return jobNumbers.stream().map(t -> new JobNumberDTO(t.getId(), t.getJobNumber())).collect(Collectors.toList());
        }
    }

    private void buildJobNumbers(ShipmentFinishOrderDTO dto, LoadEntity entity) {
        LoadNumbersEntity numbersEntity = entity.getNumbers();
        List<JobNumberDTO> dtoList = getJobNumbersDtoList(dto);
        if (numbersEntity.getJobNumbers() == null) {
            Set<LoadJobNumbersEntity> initSet = new HashSet<LoadJobNumbersEntity>(dtoList.size());
            numbersEntity.setJobNumbers(initSet);
        }

        Set<LoadJobNumbersEntity> jobEntitySet = numbersEntity.getJobNumbers();

        for (JobNumberDTO dtoItem : dtoList) {
            Long jobId = dtoItem.getId();
            LoadJobNumbersEntity jobEntity = findJobNumberById(jobEntitySet, jobId);
            if (jobEntity != null) {
                jobEntity.setJobNumber(dtoItem.getJobNumber());
            } else {
                jobEntity = new LoadJobNumbersEntity(entity.getId(), dtoItem.getJobNumber());
                jobEntitySet.add(jobEntity);
            }
        }

        removeJobNumbers(jobEntitySet, dtoList);
    }

    private List<JobNumberDTO> getJobNumbersDtoList(ShipmentFinishOrderDTO dto) {
        if (dto.getJobNumbers() == null) {
            return Collections.emptyList();
        } else {
            dto.getJobNumbers().removeIf(num -> StringUtils.isBlank(num.getJobNumber()));
            return dto.getJobNumbers();
        }
    }

    private LoadJobNumbersEntity findJobNumberById(Set<LoadJobNumbersEntity> jobNumbers, Long id) {
        for (LoadJobNumbersEntity entity : jobNumbers) {
            if (entity.getId() != null && entity.getId().equals(id)) {
                return entity;
            }
        }

        return null;
    }

    private void removeJobNumbers(Set<LoadJobNumbersEntity> entities, List<JobNumberDTO> dtos) {
        if (entities.size() != dtos.size()) {
            Iterator<LoadJobNumbersEntity> iterator = entities.iterator();
            while (iterator.hasNext()) {
                LoadJobNumbersEntity entity = iterator.next();
                if (entity.getId() != null && !isJobDtoExists(entity.getId(), dtos)) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean isJobDtoExists(final Long jobId, List<JobNumberDTO> dtos) {
        return dtos.stream().anyMatch(item -> item.getId() != null && item.getId().equals(jobId));
    }

    /**
     * Setter for data provider.
     * 
     * @param dataProvider
     *            data provider to set
     */
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        shipmentMaterialDTOBuilder.setDataProvider(dataProvider);
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider extends ShipmentMaterialDTOBuilder.DataProvider {
        /**
         * Provider for {@link LoadEntity}.
         * 
         * @return {@link LoadEntity}
         */
        LoadEntity getLoadEntity();

        /**
         * Provider for Origin Address in case of creating new entity.
         * 
         * @return {@link com.pls.dto.address.AddressBookEntryDTO}
         */
        AddressBookEntryDTO getOriginAddress();

        /**
         * Provider for Destination Address in case of creating new entity.
         * 
         * @return {@link com.pls.dto.address.AddressBookEntryDTO}
         */
        AddressBookEntryDTO getDestinationAddress();

        /**
         * Get Ltl Address for update.
         * 
         * @param id
         *            {@link AddressEntity#getId()}
         * @return {@link AddressEntity}
         */
        AddressEntity getAddress(Long id);
    }
}