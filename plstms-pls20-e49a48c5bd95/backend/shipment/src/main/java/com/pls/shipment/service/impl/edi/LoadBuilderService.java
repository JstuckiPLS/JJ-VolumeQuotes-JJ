package com.pls.shipment.service.impl.edi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.NotificationTypesDao;
import com.pls.core.dao.TimeZoneDao;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.AddressVO;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.mileage.service.MileageService;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.domain.enums.ShipmentSourceIndicator;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.HazmatInfoJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.MaterialJaxbBO;
import com.pls.shipment.domain.sterling.TrackingNotificationJaxbBO;
import com.pls.shipment.domain.sterling.TransitDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.YesNo;
import com.pls.shipment.service.ProductService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Builds the load object using the received BO.
 *
 * @author Yasaman Honarvar
 *
 */
@Service("loadBuilderService")
@Transactional(readOnly = true)
public class LoadBuilderService {

    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private BillToDao billToDao;

    @Autowired
    private TimeZoneDao timeZoneDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private NotificationTypesDao notificationTypeDao;

    @Autowired
    private OrganizationLocationDao locationDao;

    @Autowired
    private MileageService mileageService;

    @Autowired
    private DBUtilityService dbUtilityService;

    /**
     * Creates or modifies an existing load.
     *
     * @param tender
     *            the BO object used for creating the load
     * @param existingLoad
     *            load entity for the shipment if the shipment exists
     * @param originAddr
     *            Address Book record for origin
     * @param destAddr
     *            Address Book record for destination.
     * @return the created or modified LoadEntity object
     * @throws ApplicationException
     *             thrown for any exceptions occurred while creating/updating load
     */
    public LoadEntity createOrModifyLoad(LoadMessageJaxbBO tender, LoadEntity existingLoad, UserAddressBookEntity originAddr,
            UserAddressBookEntity destAddr) throws ApplicationException {
        LoadEntity load = (existingLoad == null) ? new LoadEntity() : existingLoad;

        if (load.getOrganization() == null) {
            load.setOrganization(findCustomer(tender.getCustomerOrgId()));
        }

        buildLoad(tender, load);
        setupBillToAndLocation(load, tender.getCustomerBillToId(), tender.getCustomerLocationId());
        buildLoadDetails(load, tender, originAddr, destAddr);
        setMaterials(load, tender);
        setNotifications(load, tender.getTrackingNotifications());
        // setTransactionDates(load, tender.getTransactionDates());
        load.setMileage(mileageService.getMileage(getAddressVO(originAddr.getAddress()), getAddressVO(destAddr.getAddress()),
                MileageCalculatorType.MILE_MAKER));

        // Setting up carrier
        if (tender.getScac() != null && load.getStatus() == null) {
            setupCarrier(load, tender.getScac());
            load.setStatus(ShipmentStatus.BOOKED);
        } else if (load.getStatus() == null || load.getStatus() == ShipmentStatus.CANCELLED) {
            load.setStatus(ShipmentStatus.OPEN);
            load.setAwardDate(null);
        }

        return load;
    }

    private void buildLoad(LoadMessageJaxbBO loadTender, LoadEntity load) {
        load.setSourceInd(ShipmentSourceIndicator.EDI.name());
        if (StringUtils.isNotEmpty(loadTender.getPayTerms())) {
            load.setPayTerms(PaymentTerms.getByCode(loadTender.getPayTerms()));
        }

        load.setShipmentDirection(ShipmentDirection.getByCode(loadTender.getInboundOutbound()));
        setIdentificationValues(load, loadTender);

        // Setting Notes and Instructions
        load.setSpecialInstructions(loadTender.getPickupNotes());
        load.setDeliveryNotes(loadTender.getDeliveryNotes());
        load.setBolInstructions(loadTender.getShippingLabelNotes());
    }

    private void setNotifications(LoadEntity load, List<TrackingNotificationJaxbBO> trackingNotifications) {
        if (trackingNotifications != null) {
            if (load.getLoadNotifications() == null) {
                load.setLoadNotifications(new HashSet<LoadNotificationsEntity>(trackingNotifications.size()));
            } else {
                load.getLoadNotifications().clear();
            }
            for (TrackingNotificationJaxbBO notification : trackingNotifications) {
                LoadNotificationsEntity loadNotification = new LoadNotificationsEntity();
                NotificationTypeEntity notificationType = notificationTypeDao.getNotificationTypesById(notification.getNotificationType());
                loadNotification.setNotificationType(notificationType);
                loadNotification.setEmailAddress(notification.getEmailAddress());
                loadNotification.setLoad(load);
                load.getLoadNotifications().add(loadNotification);
            }
        }
    }

    private void buildLoadDetails(LoadEntity load, LoadMessageJaxbBO loadTender, UserAddressBookEntity originAddr, UserAddressBookEntity destAddr)
            throws ValidationException {
        if (loadTender.getAddresses() != null) {
            LoadDetailsEntity origin = load.getOrigin();
            if (origin == null) {
                origin = createLoadDetails(load, PointType.ORIGIN, LoadAction.PICKUP);
            }
            updateLoadDetails(origin, loadTender.getAddress(AddressType.ORIGIN), originAddr);

            LoadDetailsEntity destination = load.getDestination();
            if (destination == null) {
                destination = createLoadDetails(load, PointType.DESTINATION, LoadAction.DELIVERY);
            }
            updateLoadDetails(destination, loadTender.getAddress(AddressType.DESTINATION), destAddr);
        }
    }

    private LoadDetailsEntity createLoadDetails(LoadEntity load, PointType pointType, LoadAction actionType) {
        LoadDetailsEntity details = new LoadDetailsEntity(actionType, pointType);
        details.setLoad(load);
        load.addLoadDetails(details);

        return details;
    }

    private void updateLoadDetails(LoadDetailsEntity detail, AddressJaxbBO address, UserAddressBookEntity addressBook) throws ValidationException {
        detail.setContact(addressBook.getAddressName());
        detail.setAddressCode(addressBook.getAddressCode());
        detail.setContactName(addressBook.getContactName());
        detail.setContactPhone(formatPhone(addressBook.getPhone()));
        detail.setContactFax(formatPhone(addressBook.getFax()));
        detail.setContactEmail(addressBook.getEmail());
        detail.setAddress(addressBook.getAddress());
        detail.setNotes(detail.getPointType() == PointType.ORIGIN ? addressBook.getPickupNotes() : addressBook.getDeliveryNotes());

        // Set time zone
        TimeZoneEntity arrivalTimeZone = timeZoneDao.findByCountryZip(address.getCountryCode(), address.getPostalCode());
        detail.setEarlyScheduledArrivalTimeZone(arrivalTimeZone);
        detail.setScheduledArrivalTimeZone(arrivalTimeZone);

        if (address.getTransitDate() != null) {
            setArrivalWindow(detail, address.getTransitDate(),
                    (detail.getPointType() == PointType.ORIGIN ? addressBook.getPickupFrom() : addressBook.getDeliveryFrom()),
                    (detail.getPointType() == PointType.ORIGIN ? addressBook.getPickupTo() : addressBook.getDeliveryTo()));
        }
    }

    private String formatPhone(PhoneEntity phoneNumber) {
        if (phoneNumber != null) {
            return PhoneUtils.format(phoneNumber);
        }

        return null;
    }

    private void setArrivalWindow(LoadDetailsEntity loadDetail, TransitDateJaxbBO arrivalTime, Time fromTime, Time toTime) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        Calendar arrivalCalendar = Calendar.getInstance(Locale.US);

        arrivalCalendar.setTime(arrivalTime.getFromDate());
        if (fromTime != null) {
            calendar.setTime(fromTime);
            arrivalCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            arrivalCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        }
        loadDetail.setArrivalWindowStart(arrivalCalendar.getTime());
        loadDetail.setEarlyScheduledArrival(arrivalCalendar.getTime());

        arrivalCalendar.setTime(arrivalTime.getToDate());
        if (toTime != null) {
            calendar.setTime(toTime);
            arrivalCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            arrivalCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        }
        loadDetail.setArrivalWindowEnd(arrivalCalendar.getTime());
        loadDetail.setScheduledArrival(arrivalCalendar.getTime());
    }

    /**
     * Creates the notes entity if available.
     *
     * @param load
     *            load for which notes has to be created
     * @param tender
     *            EDI request
     * @return the notes entity created
     */
    public ShipmentNoteEntity buildNotes(LoadEntity load, LoadMessageJaxbBO tender) {
        if (tender.getNotes() != null) {
            ShipmentNoteEntity note = new ShipmentNoteEntity();
            note.setNote(tender.getNotes());
            note.setLoadId(load.getId());
            if (tender.getNotesAuthor() != null) {
                note.getModification().setCreatedBy(tender.getNotesAuthor());
            }

            return note;
        }

        return null;
    }

    private void setupBillToAndLocation(LoadEntity load, Long billToId, Long locationId) {
        if (load.getLocation() == null || load.getLocation().getId() != locationId) {
            load.setLocation(locationDao.find(locationId));
            load.setLocationId(locationId);
        }

        if (billToId != null && (load.getBillTo() == null || load.getBillTo().getId() != billToId)) {
            load.setBillTo(billToDao.find(billToId));
        } else if (load.getBillTo() == null) {
            load.setBillTo(load.getLocation().getBillTo());
        }
    }

    private void setMaterials(LoadEntity load, LoadMessageJaxbBO tender) throws EntityNotFoundException, ValidationException {
        if (tender.getMaterials() != null) {
            LoadDetailsEntity origin = load.getOrigin();
            Map<String, PackageTypeEntity> packagingTypes = new HashMap<String, PackageTypeEntity>();

            Set<LoadMaterialEntity> oldLoadMaterials = origin.getLoadMaterials();
            if (oldLoadMaterials == null) {
                oldLoadMaterials = new HashSet<LoadMaterialEntity>();
                origin.setLoadMaterials(oldLoadMaterials);
            }

            List<MaterialJaxbBO> materials = tender.getMaterials();
            BigDecimal weight = BigDecimal.ZERO;
            Integer pieces = 0;
            Integer quantity = 0;
            load.setHazmat(false);
            if (materials != null) {
                for (MaterialJaxbBO material : materials) {
                    // Find The Product or Create a New One
                    LtlProductEntity referencedProduct = findOrCreateReferencedProduct(material, load, packagingTypes);

                    // see if this material exist in the load
                    LoadMaterialEntity loadMaterialEntity = findBykeyInfo(oldLoadMaterials, material);
                    // if material doesn't exists create and add to the
                    // tenderLoadMaterials
                    if (loadMaterialEntity == null) {
                        // create a new loadMaterialEntity
                        loadMaterialEntity = new LoadMaterialEntity();
                        oldLoadMaterials.add(loadMaterialEntity);
                    }

                    loadMaterialEntity.setReferencedProduct(referencedProduct);
                    setMaterial(loadMaterialEntity, material, load, packagingTypes);
                    weight = weight.add(material.getWeight());
                    quantity += (material.getQuantity() == null ? 0 : material.getQuantity());
                    if (material.getPieces() != null) {
                        pieces += material.getPieces();
                    }
                }
            }

            load.setWeight(weight.setScale(0, RoundingMode.CEILING).intValue());
            load.setPieces(pieces);
            load.setCommodity("MISC");
            removeNotExistMaterials(oldLoadMaterials, tender.getMaterials());
        }
    }

    /**
     * Returns true if any of material is updated in the EDI request.
     *
     * @param load
     *            load entity
     * @param tender
     *            EDI Request for creating/updating the load
     * @return true if any of material is updated.
     */
    public boolean isMaterialsUpdated(LoadEntity load, LoadMessageJaxbBO tender) {
        if (tender.getMaterials() != null) {
            Set<LoadMaterialEntity> loadMaterials = load.getOrigin().getLoadMaterials();
            if (loadMaterials == null) {
                loadMaterials = new HashSet<LoadMaterialEntity>();
            }

            if (tender.getMaterials().size() != loadMaterials.size()) {
                return true;
            }

            List<MaterialJaxbBO> materials = tender.getMaterials();
            for (MaterialJaxbBO material : materials) {
                LoadMaterialEntity loadMaterialEntity = findBykeyInfo(loadMaterials, material);
                if (loadMaterialEntity == null || !keyFieldsEqual(loadMaterialEntity, material)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeNotExistMaterials(Set<LoadMaterialEntity> oldLoadMaterials, List<MaterialJaxbBO> materials) {
        if (oldLoadMaterials.size() != materials.size()) {
            Iterator<LoadMaterialEntity> materialsIterator = oldLoadMaterials.iterator();
            while (materialsIterator.hasNext()) {
                if (shouldRemoveMaterial(materials, materialsIterator.next())) {
                    materialsIterator.remove();
                }
            }
        }
    }

    private boolean shouldRemoveMaterial(List<MaterialJaxbBO> materials, LoadMaterialEntity loadMaterial) {
        for (MaterialJaxbBO material : materials) {
            if (isEqualMaterialKey(loadMaterial, material)) {
                return false;
            }
        }
        return true;
    }

    private boolean keyFieldsEqual(LoadMaterialEntity item1, MaterialJaxbBO material) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(item1.getPackageType().getId(), material.getPackagingType())
                .append(item1.getQuantity(), String.valueOf(material.getQuantity())).append(item1.getWeight(), material.getWeight());
        return equalsBuilder.isEquals();
    }

    private LoadMaterialEntity findBykeyInfo(Set<LoadMaterialEntity> loadMaterials, MaterialJaxbBO material) {
        LoadMaterialEntity result = null;
        if (loadMaterials != null) {
            for (LoadMaterialEntity loadMaterial : loadMaterials) {
                if (isEqualMaterialKey(loadMaterial, material)) {
                    result = loadMaterial;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isEqualMaterialKey(LoadMaterialEntity item1, MaterialJaxbBO material) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(item1.getProductDescription(), material.getProductDesc())
                .append(item1.getCommodityClass(), CommodityClass.convertFromDbCode(material.getCommodityClassCode()))
                .append(item1.isHazmat(), material.getHazmat() == YesNo.YES);
        return equalsBuilder.isEquals();
    }

    private void setMaterial(LoadMaterialEntity loadMaterial, MaterialJaxbBO material, LoadEntity load,
            Map<String, PackageTypeEntity> packagingTypes) {
        loadMaterial.setWeight(material.getWeight());
        loadMaterial.setCommodityClass(loadMaterial.getReferencedProduct().getCommodityClass());
        loadMaterial.setProductCode(loadMaterial.getReferencedProduct().getProductCode());
        loadMaterial.setProductDescription(loadMaterial.getReferencedProduct().getDescription());
        loadMaterial.setLength(material.getLength());
        loadMaterial.setWidth(material.getWidth());
        loadMaterial.setHeight(material.getHeight());
        loadMaterial.setQuantity(material.getQuantity().toString());
        if (material.getPackagingType() != null) {
            if (packagingTypes.get(material.getPackagingType()) == null) {
                packagingTypes.put(material.getPackagingType(), packageTypeDictionaryService.getById(material.getPackagingType()));
            }

            loadMaterial.setPackageType(packagingTypes.get(material.getPackagingType()));
        }
        loadMaterial.setStackable((material.getStackable() == YesNo.YES));
        loadMaterial.setPieces(material.getPieces());
        loadMaterial.setNmfc(material.getNmfc());
        loadMaterial.setHazmat(material.getHazmat() == YesNo.YES);
        if (loadMaterial.isHazmat()) {
            load.setHazmat(true);
            setHazmatInfo(loadMaterial, material.getHazmatInfo());
        }

        loadMaterial.setPickupDate(load.getOrigin().getArrivalWindowStart());
        loadMaterial.setLoadDetail(load.getOrigin());
    }

    private LtlProductEntity findOrCreateReferencedProduct(MaterialJaxbBO material, LoadEntity load, Map<String, PackageTypeEntity> packagingTypes)
            throws EntityNotFoundException, ValidationException {

        LtlProductEntity product = productService.findUniqueProductByInfo(load.getOrganization().getId(),
                CommodityClass.convertFromDbCode(material.getCommodityClassCode()), material.getProductCode());
        if (product == null) {
            product = productService.findProductByInfo(material.getProductDesc(), load.getOrganization().getId(),
                    (material.getHazmat() == YesNo.YES), CommodityClass.convertFromDbCode(material.getCommodityClassCode()));
        }
        if (product == null) {
            product = new LtlProductEntity();
            product.setCommodityClass(CommodityClass.convertFromDbCode(material.getCommodityClassCode()));
            product.setCustomerId(load.getOrganization().getId());
            product.setDescription(material.getProductDesc());
            if (material.getHazmat() == YesNo.YES) {
                product.setHazmatInfo(setupLtlHazmatInfo(material.getHazmatInfo()));
            }
            product.setNmfcNum(material.getNmfc());

            if (material.getPackagingType() != null) {
                if (packagingTypes.get(material.getPackagingType()) == null) {
                    packagingTypes.put(material.getPackagingType(), packageTypeDictionaryService.getById(material.getPackagingType()));
                }
                product.setPackageType(packagingTypes.get(material.getPackagingType()));
            }

            product.setProductCode(material.getProductCode());
            product.setShared(true);
            productService.saveOrUpdateProduct(product, product.getCustomerId(), load.getPersonId(), true);
            dbUtilityService.flushSession();
        }
        return product;
    }

    private LtlProductHazmatInfo setupLtlHazmatInfo(HazmatInfoJaxbBO hazmatInfo) {
        LtlProductHazmatInfo info = new LtlProductHazmatInfo();
        if (hazmatInfo != null) {
            info.setEmergencyCompany(hazmatInfo.getEmergencyCompany());
            info.setEmergencyContract(hazmatInfo.getEmergencyContractNum());
            info.setEmergencyPhone(getPhoneEmbeddable(hazmatInfo.getEmergencyPhone()));
            info.setHazmatClass(hazmatInfo.getHzClass());
            info.setInstructions(hazmatInfo.getEmergencyInstr());
            info.setPackingGroup(hazmatInfo.getPackagingGroupNum());
            info.setUnNumber(hazmatInfo.getUnNum());
        }
        return info;
    }

    private void setHazmatInfo(LoadMaterialEntity loadMaterial, HazmatInfoJaxbBO info) {
        if (info != null) {
            loadMaterial.setPackingGroup(info.getPackagingGroupNum());
            loadMaterial.setHazmatClass(info.getHzClass());
            loadMaterial.setEmergencyCompany(info.getEmergencyCompany());
            loadMaterial.setHazmatInstruction(info.getEmergencyInstr());
            loadMaterial.setEmergencyPhone(getPhoneEmbeddable(info.getEmergencyPhone()));
            loadMaterial.setUnNumber(info.getUnNum());
            loadMaterial.setEmergencyContract(info.getEmergencyContractNum());
        }
    }

    private PhoneEmbeddableObject getPhoneEmbeddable(String phoneNumber) {
        if (StringUtils.isNotEmpty(phoneNumber)) {
            PhoneBO phone = PhoneUtils.parse(phoneNumber);
            PhoneEmbeddableObject phoneEmb = new PhoneEmbeddableObject();
            phoneEmb.setAreaCode(phone.getAreaCode());
            phoneEmb.setCountryCode(phone.getCountryCode());
            phoneEmb.setNumber(phone.getNumber());

            return phoneEmb;
        }

        return null;
    }

    private void setupCarrier(LoadEntity load, String scac) {
        CarrierEntity carrier = findCarrier(scac);
        load.setCarrier(carrier);
    }

    private void setIdentificationValues(LoadEntity load, LoadMessageJaxbBO tender) {
        load.getNumbers().setRefNumber(tender.getShipmentNo());
        load.getNumbers().setProNumber(tender.getProNumber());
        if (tender.getBol() != null) {
            load.getNumbers().setBolNumber(tender.getBol());
        }
        load.getNumbers().setGlNumber(tender.getGlNumber());
        load.getNumbers().setPuNumber(tender.getPickupNum());
        load.getNumbers().setPoNumber(tender.getPoNum());
        load.getNumbers().setSoNumber(tender.getSoNum());
        load.getNumbers().setTrailerNumber(tender.getTrailerNum());
        load.setPersonId(SecurityUtils.getCurrentPersonId());
    }

    private CustomerEntity findCustomer(Long orgId) {
        return customerDao.find(orgId);
    }

    private CarrierEntity findCarrier(String scac) {
        return carrierDao.findByScac(scac);
    }

    private AddressVO getAddressVO(AddressEntity record) {
        AddressVO address = new AddressVO();
        address.setAddress1(record.getAddress1());
        address.setAddress2(record.getAddress2());
        address.setCity(record.getCity());
        address.setStateCode(record.getStateCode());
        address.setPostalCode(record.getZip());
        if (record.getCountryCode() != null) {
            address.setCountryCode(record.getCountryCode());
        } else if (record.getCountry() != null) {
            address.setCountryCode(record.getCountry().getId());
        }

        return address;
    }
}
