package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.FreightBillPayToService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.BillToRequiredField;
import com.pls.core.shared.Countries;
import com.pls.core.shared.Status;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadAdditionalFieldsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.ShipmentRequestedByNoteEntity;
import com.pls.shipment.domain.enums.CommodityCd;
import com.pls.shipment.domain.enums.ShipmentSourceIndicator;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.SalesOrderService;
import com.pls.shipment.service.ShipmentBillToUtils;

/**
 * Implementation of {@link com.pls.shipment.service.SalesOrderService}.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService {
    private static final String CARRIER_FINAL_LINEHAUL_REF_TYPE = "CRA";
    private static final String SHIPPER_FINAL_LINEHAUL_REF_TYPE = "SRA";
    private static final String BENCHMARK_REF_TYPE = "SBR";
    private static final String FUEL_SURCHARGE_REF_TYPE = "FS";

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private FreightBillPayToService freightBillPayToService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private UserAddressBookDao userAddressBookDao;

    @Autowired
    private LtlRatingEngineService ratingService;

    @Override
    public LoadEntity createNewOrder(Long vendorBillId, Long customerId) throws ApplicationException {
        CarrierInvoiceDetailsEntity carrierInvoiceDetailsEntity = carrierInvoiceService.getById(vendorBillId);
        CustomerEntity customer = customerDao.get(customerId);
        return createNewOrder(carrierInvoiceDetailsEntity, customer);
    }

    @Override
    public LoadEntity createNewOrder(CarrierInvoiceDetailsEntity carrierInvoiceDetails, CustomerEntity customer) throws ApplicationException {
        if (customer != null) {
            validateActiveCustomer(customer.getId());
        }
        return createNew(carrierInvoiceDetails, customer);
    }

    /**
     * Check whether the customer is active.
     * 
     * @param customerId id of customer
     * @throws ApplicationException if customer inactive
     */
    private void validateActiveCustomer(Long customerId) throws ApplicationException {
        Boolean isActiveCustomer = customerDao.isActiveCustomer(customerId);
        if (isActiveCustomer != null && !isActiveCustomer) {
            throw new ApplicationException("You can not update or save data for inactive customer");
        }
    }

    private void initBillToDefaultValues(LoadEntity load, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        if (load.getBillTo() != null && load.getBillTo().getBillToDefaultValues() != null) {
            BillToDefaultValuesEntity defaultValues = load.getBillTo().getBillToDefaultValues();
            if (carrierInvoiceDetails.getPaymentTerms() == null) {
                load.setPayTerms(defaultValues.getEdiPayTerms());
            }
            load.setShipmentDirection(defaultValues.getEdiDirection());
            if (isBorderPassing(carrierInvoiceDetails)) {
                load.setCustomsBroker(defaultValues.getEdiCustomsBroker());
                load.setCustomsBrokerPhone(PhoneUtils.format(defaultValues.getEdiCustomsBrokerPhone()));
            }
        }
        if (carrierInvoiceDetails.getPaymentTerms() != null) {
            load.setPayTerms(carrierInvoiceDetails.getPaymentTerms());
        }
    }

    private boolean isBorderPassing(CarrierInvoiceDetailsEntity details) {
        return (details.getOriginAddress() != null && details.getDestinationAddress() != null)
                && !Objects.equals(details.getOriginAddress().getCountryCode(), details.getDestinationAddress().getCountryCode());
    }

    private LoadEntity createNew(CarrierInvoiceDetailsEntity carrierInvoiceDetails, CustomerEntity customer) {
        LoadEntity load = new LoadEntity();
        initLoad(load, carrierInvoiceDetails);
        initCustomer(load, customer);

        LoadDetailsEntity originDetail = initOriginDetails(load, carrierInvoiceDetails);
        initProducts(originDetail, carrierInvoiceDetails);
        Long personId = getPersonId(carrierInvoiceDetails);
        CarrierInvoiceAddressDetailsEntity originAddress = carrierInvoiceDetails.getOriginAddress();
        if (originAddress != null) {
            UserAddressBookEntity address = initLoadDetails(originAddress, originDetail, customer, personId);
            initDefaultWindow(originDetail, address);
        }

        LoadDetailsEntity destinationDetail = initDestinationDetails(load, carrierInvoiceDetails);
        CarrierInvoiceAddressDetailsEntity destinationAddress = carrierInvoiceDetails.getDestinationAddress();
        if (destinationAddress != null) {
            initLoadDetails(destinationAddress, destinationDetail, customer, personId);
        }
        initBillToDefaultValues(load, carrierInvoiceDetails);

        if (load.getOrigin().getAddress() != null && load.getDestination().getAddress() != null) {
            load.setRoute(buildRoute(load.getOrigin().getAddress(), load.getDestination().getAddress(), personId));
        }

        if (carrierInvoiceDetails.getDeliveryDate() != null) {
            load.setStatus(ShipmentStatus.DELIVERED);
        } else if (carrierInvoiceDetails.getActualPickupDate() != null) {
            load.setStatus(ShipmentStatus.IN_TRANSIT);
        }
        setDefaultvalues(load);
        initLoadCosts(load);
        LOGGER.info("Load was successfully created from Vendor Bill");

        return load;
    }

    private LoadDetailsEntity initDestinationDetails(LoadEntity load, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        LoadDetailsEntity destinationDetail = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationDetail);
        destinationDetail.setLoad(load);
        destinationDetail.setDeparture(carrierInvoiceDetails.getDeliveryDate());
        destinationDetail.setScheduledArrival(carrierInvoiceDetails.getEstDeliveryDate());
        destinationDetail.setEarlyScheduledArrival(carrierInvoiceDetails.getEstDeliveryDate());
        return destinationDetail;
    }
    private LoadDetailsEntity initOriginDetails(LoadEntity load, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        LoadDetailsEntity originDetail = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        load.addLoadDetails(originDetail);
        originDetail.setLoad(load);
        originDetail.setDeparture(carrierInvoiceDetails.getActualPickupDate());
        originDetail.setScheduledArrival(carrierInvoiceDetails.getActualPickupDate());
        originDetail.setEarlyScheduledArrival(carrierInvoiceDetails.getActualPickupDate());
        return originDetail;
    }

    private void setDefaultvalues(LoadEntity load) {
        if (load.getBillTo() != null) {
            Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules = ShipmentBillToUtils.getMatchedRules(load);
            LoadNumbersEntity numbers = load.getNumbers();
            // BOL
            setBolNumber(filteredRules, numbers);
            // GL
            setGlNumber(filteredRules, numbers);
            // PO
            setPoNumber(filteredRules, numbers);
            // PU
            setPuNumber(filteredRules, numbers);
            // SO
            setSoNumber(filteredRules, numbers);
            // SR
            setRefNumber(filteredRules, numbers);
            // TR
            setTrailerNumber(filteredRules, numbers);
            // PRO
            setProNumber(filteredRules, numbers);
            // JOB
            setJobNumbers(load.getId(), numbers, filteredRules);
            // RB
            setRequestedBy(load, filteredRules);
            // CARGO
            setCargoValue(load, filteredRules);
        }
    }

    private void setProNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getProNumber() == null && filteredRules.containsKey(BillToRequiredField.PRO)) {
            numbers.setProNumber(filteredRules.get(BillToRequiredField.PRO).getDefaultValue());
        }
    }

    private void setTrailerNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getTrailerNumber() == null && filteredRules.containsKey(BillToRequiredField.TRAILER)) {
            numbers.setTrailerNumber(filteredRules.get(BillToRequiredField.TRAILER).getDefaultValue());
        }
    }

    private void setRefNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getRefNumber() == null && filteredRules.containsKey(BillToRequiredField.SHIPPER_REF)) {
            numbers.setRefNumber(filteredRules.get(BillToRequiredField.SHIPPER_REF).getDefaultValue());
        }
    }

    private void setSoNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getSoNumber() == null && filteredRules.containsKey(BillToRequiredField.SO)) {
            numbers.setSoNumber(filteredRules.get(BillToRequiredField.SO).getDefaultValue());
        }
    }

    private void setPuNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getPuNumber() == null && filteredRules.containsKey(BillToRequiredField.PU)) {
            numbers.setPuNumber(filteredRules.get(BillToRequiredField.PU).getDefaultValue());
        }
    }

    private void setPoNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getPoNumber() == null && filteredRules.containsKey(BillToRequiredField.PO)) {
            numbers.setPoNumber(filteredRules.get(BillToRequiredField.PO).getDefaultValue());
        }
    }

    private void setGlNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getGlNumber() == null && filteredRules.containsKey(BillToRequiredField.GL)) {
            numbers.setGlNumber(filteredRules.get(BillToRequiredField.GL).getDefaultValue());
        }
    }

    private void setBolNumber(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            LoadNumbersEntity numbers) {
        if (numbers.getBolNumber() == null && filteredRules.containsKey(BillToRequiredField.BOL)) {
            numbers.setBolNumber(filteredRules.get(BillToRequiredField.BOL).getDefaultValue());
        }
    }


    private void setJobNumbers(Long loadId, LoadNumbersEntity numbers,
            Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules) {
        if (numbers.getJobNumbers() == null && filteredRules.containsKey(BillToRequiredField.JOB)) {
            Set<LoadJobNumbersEntity> jobNumbers = new HashSet<LoadJobNumbersEntity>();
            String jobNumberString = filteredRules.get(BillToRequiredField.JOB).getDefaultValue();
            if (jobNumberString != null && !jobNumberString.isEmpty()) {
                List<String> values = Arrays.asList(jobNumberString.split("\\s*,\\s*"));
                for (String value : values) {
                    LoadJobNumbersEntity jobNumber = new LoadJobNumbersEntity(loadId, value);
                    jobNumbers.add(jobNumber);
                }
            }
            numbers.setJobNumbers(jobNumbers);
        }
    }

    private void setRequestedBy(LoadEntity load, Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules) {
        if (load.getRequestedBy() == null && filteredRules.containsKey(BillToRequiredField.REQUESTED_BY)) {
            String requestedByString = filteredRules.get(BillToRequiredField.REQUESTED_BY).getDefaultValue();
            if (requestedByString != null && !requestedByString.isEmpty()) {
                ShipmentRequestedByNoteEntity note = new ShipmentRequestedByNoteEntity();
                note.setActiveStatus(true);
                note.setLoad(load);
                note.setNote(filteredRules.get(BillToRequiredField.REQUESTED_BY).getDefaultValue());
                load.setRequestedBy(note);
            }
        }
    }

    private void setCargoValue(LoadEntity load, Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules) {
        if (load.getLoadAdditionalFields() == null && filteredRules.containsKey(BillToRequiredField.CARGO)) {
            String cargoString = filteredRules.get(BillToRequiredField.CARGO).getDefaultValue();
            if (cargoString != null && !cargoString.isEmpty()) {
                LoadAdditionalFieldsEntity cargo = new LoadAdditionalFieldsEntity();
                cargo.setLoad(load);
                cargo.setCargoValue(new BigDecimal(cargoString));
                load.setLoadAdditionalFields(cargo);
            }
        }
    }

    private void initDefaultWindow(LoadDetailsEntity origin, UserAddressBookEntity address) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(origin.getDeparture() == null ? new Date() : origin.getDeparture());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (address.getPickupFrom() != null) {
            Calendar pickupFrom = Calendar.getInstance();
            pickupFrom.setTime(address.getPickupFrom());
            calendar.set(Calendar.HOUR, pickupFrom.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, pickupFrom.get(Calendar.MINUTE));
            calendar.set(Calendar.AM_PM, pickupFrom.get(Calendar.AM_PM));
        } else {
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }
        origin.setArrivalWindowStart(calendar.getTime());
        origin.setEarlyScheduledArrival(calendar.getTime());

        if (address.getPickupTo() != null) {
            Calendar pickupTo = Calendar.getInstance();
            pickupTo.setTime(address.getPickupTo());
            calendar.set(Calendar.HOUR, pickupTo.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, pickupTo.get(Calendar.MINUTE));
            calendar.set(Calendar.AM_PM, pickupTo.get(Calendar.AM_PM));
        } else {
            calendar.set(Calendar.HOUR, 11);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }
        origin.setArrivalWindowEnd(calendar.getTime());
        origin.setScheduledArrival(calendar.getTime());
    }

    private void initProducts(LoadDetailsEntity originDetail, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        originDetail.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        Long personId = getPersonId(carrierInvoiceDetails);
        for (CarrierInvoiceLineItemEntity invoiceItem : carrierInvoiceDetails.getCarrierInvoiceLineItems()) {
            if (invoiceItem.getWeight() != null && invoiceItem.getCommodityClass() != null) {
                LoadMaterialEntity material = new LoadMaterialEntity();
                material.getModification().setCreatedBy(personId);
                material.getModification().setModifiedBy(personId);
                material.setWeight(invoiceItem.getWeight());
                material.setQuantity(ObjectUtils.toString(invoiceItem.getQuantity()));
                material.setPackageType(getPackageTypeForInvoiceItem(invoiceItem.getPackagingCode()));
                material.setProductDescription(invoiceItem.getDescription());
                material.setNmfc(invoiceItem.getNmfc());
                material.setCommodityClass(invoiceItem.getCommodityClass());
                material.setPickupDate(carrierInvoiceDetails.getActualPickupDate());
                material.setLoadDetail(originDetail);
                originDetail.getLoadMaterials().add(material);
            }
        }
    }

    private PackageTypeEntity getPackageTypeForInvoiceItem(String packageCode) {
        if (StringUtils.isNotBlank(packageCode)) {
            PackageTypeEntity packageType = new PackageTypeEntity();
            packageType.setId(packageCode);
            return packageType;
        }
        return null;
    }

    private void initLoad(LoadEntity load, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        load.setStatus(ShipmentStatus.DELIVERED);
        load.setCommodity(CommodityCd.MISC.name());
        load.setSourceInd(ShipmentSourceIndicator.EDI.name());
        load.setPieces(carrierInvoiceDetails.getTotalQuantity());
        load.setPersonId(getPersonId(carrierInvoiceDetails));
        load.setCarrier(carrierInvoiceDetails.getCarrier());
        load.getNumbers().setProNumber(carrierInvoiceDetails.getProNumber());
        load.getNumbers().setPoNumber(carrierInvoiceDetails.getPoNumber());
        load.getNumbers().setRefNumber(carrierInvoiceDetails.getShipperRefNumber());
        load.getNumbers().setBolNumber(carrierInvoiceDetails.getBolNumber());
        load.getNumbers().setPuNumber(carrierInvoiceDetails.getReferenceNumber());
        load.setWeight(carrierInvoiceDetails.getTotalWeight() != null
                ? carrierInvoiceDetails.getTotalWeight().setScale(0, RoundingMode.CEILING).intValue() : 0);
        load.setMileage(0);
        if (carrierInvoiceDetails.getDeliveryDate() != null && carrierInvoiceDetails.getActualPickupDate() != null) {
            long travelTime = Duration.between(
                    carrierInvoiceDetails.getDeliveryDate().toInstant(),
                    carrierInvoiceDetails.getActualPickupDate().toInstant()).abs().toMinutes();
            load.setTravelTime(Long.min(travelTime, LtlRatingEngineService.MAX_TRANSIT_TIME));
        }
        load.setFreightBillPayTo(freightBillPayToService.getDefaultFreightBillPayTo());

        load.getVendorBillDetails().setFrtBillNumber(carrierInvoiceDetails.getInvoiceNumber());

        load.getVendorBillDetails().setFrtBillAmount(carrierInvoiceDetails.getTotalCharges());
        load.getVendorBillDetails().setFrtBillRecvFlag(true);
        load.getVendorBillDetails().setFrtBillRecvBy(getPersonId(carrierInvoiceDetails));
    }

    private void initCustomer(LoadEntity load, CustomerEntity customer) {
        if (customer != null) {
            load.setOrganization(customer);
            OrganizationLocationEntity location = null;
            if (!customer.getDefaultLocations().isEmpty()) {
                location = customer.getDefaultLocations().iterator().next();
                load.setLocationId(location.getId());
            } else if (customer.getLocations().size() == 1) {
                location = customer.getLocations().iterator().next();
                load.setLocationId(location.getId());
            }
            if (!customer.getDefaultBillTo().isEmpty()) {
                load.setBillTo(customer.getDefaultBillTo().iterator().next());
            } else if (location != null && location.getBillTo() != null) {
                load.setBillTo(location.getBillTo());
            } else if (customer.getBillTos().size() == 1) {
                load.setBillTo(customer.getBillTos().iterator().next());
            }
        }
    }

    private UserAddressBookEntity initLoadDetails(CarrierInvoiceAddressDetailsEntity carrierInvoiceAddress, LoadDetailsEntity loadDetails,
            CustomerEntity customer, Long personId) {
        List<UserAddressBookEntity> userAdressBooks = Collections.emptyList();
        if (customer != null && customer.getId() != null) {
            userAdressBooks = userAddressBookDao.searchUserAddressBookEntries(
                    carrierInvoiceAddress.getAddress1(),
                    carrierInvoiceAddress.getAddress2(),
                    carrierInvoiceAddress.getCity(),
                    carrierInvoiceAddress.getPostalCode(),
                    carrierInvoiceAddress.getCountryCode(),
                    carrierInvoiceAddress.getState(),
                    customer.getId());
        }

        UserAddressBookEntity userAddress;
        if (userAdressBooks.isEmpty()) {
            userAddress = getLoadAddress(carrierInvoiceAddress, customer == null ? null : customer.getId(), personId);
            if (customer != null) {
                userAddressBookDao.persistWithFlush(userAddress);
            }
        } else {
            userAddress = userAdressBooks.iterator().next();
        }
        loadDetails.setAddress(userAddress.getAddress());
        loadDetails.setAddressCode(userAddress.getAddressCode());
        loadDetails.setContact(userAddress.getAddressName());
        loadDetails.setContactName(userAddress.getContactName());
        return userAddress;
    }

    private RouteEntity buildRoute(AddressEntity origin, AddressEntity destination, Long currentUserId) {
        RouteEntity route = new RouteEntity();
        route.setCreatedBy(currentUserId);

        route.setDestCity(destination.getCity());
        route.setDestCountry(destination.getCountry().getId());
        route.setDestState(destination.getStateCode());
        route.setDestZip(destination.getZip());

        route.setOriginCity(origin.getCity());
        route.setOriginCountry(origin.getCountry().getId());
        route.setOriginState(origin.getStateCode());
        route.setOriginZip(origin.getZip());
        return route;
    }

    private UserAddressBookEntity getLoadAddress(CarrierInvoiceAddressDetailsEntity vendorBillAddress, Long customerId, Long personId) {
        UserAddressBookEntity addressBook = new UserAddressBookEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1(vendorBillAddress.getAddress1());
        address.setAddress2(vendorBillAddress.getAddress2());

        CountryEntity country = new CountryEntity();
        country.setId(vendorBillAddress.getCountryCode());
        address.setCountry(country);
        address.setCity(vendorBillAddress.getCity());
        address.setStateCode(vendorBillAddress.getState());
        address.setZip(vendorBillAddress.getPostalCode());
        address.setStatus(Status.ACTIVE);

        addressBook.setAddress(address);
        addressBook.setAddressName(vendorBillAddress.getAddressName());
        addressBook.setStatus(Status.ACTIVE);
        addressBook.setOrgId(customerId);
        addressBook.getModification().setCreatedBy(personId);
        addressBook.getModification().setModifiedBy(personId);
        addressBook.setType(AddressType.SHIPPING);
        return addressBook;
    }

    private void initLoadCosts(LoadEntity load) {
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);
        costDetail.setTotalCost(BigDecimal.ZERO);
        costDetail.setTotalRevenue(BigDecimal.ZERO);
        costDetail.setLoad(load);
        costDetail.setShipDate(load.getOrigin().getDeparture() == null
                ? load.getOrigin().getEarlyScheduledArrival()
                : load.getOrigin().getDeparture());

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        load.getCostDetails().add(costDetail);

        if (load.getOrganization() != null && load.getOrganization().getId() != null) {
            updateLoadWithPricingDetails(load);
        }
    }

    private void updateLoadWithPricingDetails(LoadEntity load) {
        GetOrderRatesCO order = getCriteriaForPricing(load);
        LtlPricingResult result = getPricingForLoad(order);
        if (result != null) {
            load.setTravelTime((long) (result.getTransitTime() * 24 * 60));
            load.setMileage(result.getTotalMiles());
            load.getDestination().setEarlyScheduledArrival(result.getTransitDate());
            load.getDestination().setScheduledArrival(result.getTransitDate());

            LoadCostDetailsEntity costDetail = load.getActiveCostDetail();
            costDetail.setPricingProfileDetailId(result.getProfileId());
            costDetail.setServiceType(result.getServiceType());
            costDetail.setNewLiability(result.getNewProdLiability());
            costDetail.setUsedLiability(result.getUsedProdLiability());
            costDetail.setProhibitedCommodities(result.getProhibitedCommodities());
            costDetail.setGuaranteedNameForBOL(result.getBolCarrierName());
            costDetail.setTotalCost(result.getTotalCarrierCost());
            costDetail.setTotalRevenue(result.getTotalShipperCost());

            updateCostDetailItems(costDetail, load.getCarrier().getId(), load.getBillTo(), result);

            load.getNumbers().setCarrierQuoteNumber(result.getCarrierQuoteNumber());
            load.getNumbers().setServiceLevelCode(result.getServiceLevelCode());
            load.getNumbers().setServiceLevelDescription(result.getServiceLevelDescription());
        } else {
            LOGGER.info("No costs found for load created from Vendor Bill");
        }
    }

    private LtlPricingResult getPricingForLoad(GetOrderRatesCO order) {
        if (!checkCriteriaBeforePricingCall(order)) {
            return null;
        }
        List<LtlPricingResult> pricingResults = ratingService.getRatesSafe(order);
        Optional<LtlPricingResult> result = pricingResults.stream()
                .filter(pricingResult -> ObjectUtils.equals(pricingResult.getCarrierOrgId(), order.getCarrierOrgId())).findFirst();

        if (result.isPresent()) {
            return result.get();
        } else if (!pricingResults.isEmpty()) {
            return pricingResults.iterator().next();
        }
        return null;
    }

    /**
     * We shouldn't have exceptions in pricing because it will roll back trasaction.
     * 
     * @param order
     *            criteria to check
     * @return <code>true</code> if we are safe to make a pricing call.
     */
    private boolean checkCriteriaBeforePricingCall(GetOrderRatesCO order) {
        return checkZipCode(order.getOriginAddress().getPostalCode())
                && checkZipCode(order.getDestinationAddress().getPostalCode())
                && checkCountry(order.getDestinationAddress().getCountryCode())
                && checkCountry(order.getOriginAddress().getCountryCode());
    }

    private boolean checkCountry(String countryCode) {
        return EnumUtils.isValidEnum(Countries.class, countryCode);
    }

    private boolean checkZipCode(String zipCode) {
        return zipCode != null && zipCode.matches("\\d+");
    }

    private GetOrderRatesCO getCriteriaForPricing(LoadEntity load) {
        GetOrderRatesCO order = new GetOrderRatesCO();
        order.setShipperOrgId(load.getOrganization().getId());
        order.setCarrierOrgId(load.getCarrier().getId());
        order.setShipDate(load.getOrigin().getScheduledArrival());
        order.setOriginAddress(getAddressCriteria(load.getOrigin()));
        order.setDestinationAddress(getAddressCriteria(load.getDestination()));
        // order.setAccessorialTypes(); // no accessorials are currently being set
        order.setMaterials(load.getOrigin().getLoadMaterials().stream().map(material -> getMaterialCriteria(material)).collect(Collectors.toList()));
        Optional<BigDecimal> totalWeight = load.getOrigin().getLoadMaterials().stream().map(material -> material.getWeight())
                .reduce((weight1, weight2) -> weight1.add(weight2));
        if (totalWeight.isPresent()) {
            order.setTotalWeight(totalWeight.get());
        }
        order.setCommodityClassSet(load.getOrigin().getLoadMaterials().stream().map(material -> material.getCommodityClass())
                .collect(Collectors.toSet()));
        return order;
    }

    private RateMaterialCO getMaterialCriteria(LoadMaterialEntity material) {
        RateMaterialCO materialCriteria = new RateMaterialCO();
        materialCriteria.setWeight(material.getWeight());
        materialCriteria.setWeightUnit("LBS");
        materialCriteria.setHeight(material.getHeight());
        materialCriteria.setWidth(material.getWidth());
        materialCriteria.setLength(material.getLength());
        materialCriteria.setDimensionUnit("IN");
        materialCriteria.setQuantity(getMaterialQuantitySafe(material));
        if (material.getPackageType() != null) {
            materialCriteria.setPackageType(material.getPackageType().getId());
        }
        materialCriteria.setCommodityClassEnum(material.getCommodityClass());
        materialCriteria.setHazmatBool(material.isHazmat());
        materialCriteria.setPieces(material.getPieces());
        materialCriteria.setNmfc(material.getNmfc());
        return materialCriteria;
    }

    private int getMaterialQuantitySafe(LoadMaterialEntity material) {
        Integer quantity;
        try {
            quantity = Integer.parseInt(material.getQuantity());
        } catch (Exception e) {
            quantity = 1;
        }
        return quantity == null || quantity < 1 ? 1 : quantity;
    }

    private AddressVO getAddressCriteria(LoadDetailsEntity loadDetails) {
        AddressVO address = new AddressVO();
        if (loadDetails.getAddress() != null) {
            address.setCity(loadDetails.getAddress().getCity());
            address.setStateCode(loadDetails.getAddress().getStateCode());
            address.setPostalCode(loadDetails.getAddress().getZip());
            address.setCountryCode(loadDetails.getAddress().getCountry().getId());
        }
        return address;
    }

    private void updateCostDetailItems(LoadCostDetailsEntity costDetail, Long carrierId, BillToEntity billTo, LtlPricingResult result) {
        boolean benchmarkExist = true;
        if (result.getBenchmarkFinalLinehaul() == null || result.getBenchmarkFinalLinehaul().equals(BigDecimal.ZERO)
                || result.getBenchmarkFuelSurcharge() == null || result.getBenchmarkFuelSurcharge().equals(BigDecimal.ZERO)) {
            benchmarkExist = false;
        }
        addCostDetailItem(costDetail, CARRIER_FINAL_LINEHAUL_REF_TYPE, CostDetailOwner.C, result.getCarrierFinalLinehaul(), carrierId, billTo);
        addCostDetailItem(costDetail, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.C, result.getCarrierFuelSurcharge(), carrierId, billTo);

        addCostDetailItem(costDetail, SHIPPER_FINAL_LINEHAUL_REF_TYPE, CostDetailOwner.S, result.getShipperFinalLinehaul(), carrierId, billTo);
        addCostDetailItem(costDetail, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.S, result.getShipperFuelSurcharge(), carrierId, billTo);

        addCostDetailItem(costDetail, BENCHMARK_REF_TYPE, CostDetailOwner.B, result.getBenchmarkFinalLinehaul(), carrierId, billTo);
        addCostDetailItem(costDetail, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.B, result.getBenchmarkFuelSurcharge(), carrierId, billTo);

        addAccessorials(costDetail, result.getAccessorials(), benchmarkExist, carrierId, billTo);
    }

    private void addAccessorials(LoadCostDetailsEntity costDetail, List<LtlPricingAccessorialResult> accessorials, boolean benchmarkExist,
            Long carrierId, BillToEntity billTo) {
        if (accessorials != null && !accessorials.isEmpty()) {
            for (LtlPricingAccessorialResult accessorial : accessorials) {
                addCostDetailItem(costDetail, accessorial.getAccessorialType(), CostDetailOwner.S, accessorial.getShipperAccessorialCost(),
                        carrierId, billTo);
                addCostDetailItem(costDetail, accessorial.getAccessorialType(), CostDetailOwner.C, accessorial.getCarrierAccessorialCost(),
                        carrierId, billTo);
                if (benchmarkExist) {
                    addCostDetailItem(costDetail, accessorial.getAccessorialType(), CostDetailOwner.B,
                            accessorial.getBenchmarkAccessorialCost(), carrierId, billTo);
                }
            }
        }
    }

    private void addCostDetailItem(LoadCostDetailsEntity costDetail, String refType, CostDetailOwner owner, BigDecimal total, Long carrierId,
            BillToEntity billTo) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        addCostDetailItem(costDetail, refType, owner, total, carrierId, billTo, null);
    }

    private void addCostDetailItem(LoadCostDetailsEntity costDetail, String refType, CostDetailOwner owner, BigDecimal total, Long carrierId,
            BillToEntity billTo, String note) {
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        costDetailItem.setOwner(owner);
        costDetailItem.setCostDetails(costDetail);
        costDetailItem.setAccessorialType(refType);
        costDetailItem.setSubtotal(total == null ? BigDecimal.ZERO : total.setScale(2, RoundingMode.HALF_UP));
        costDetailItem.setUnitCost(costDetailItem.getSubtotal());
        costDetailItem.setCarrierId(carrierId);
        costDetailItem.setBillTo(billTo);
        costDetailItem.setNote(note);
        costDetail.getCostDetailItems().add(costDetailItem);
    }

    private Long getPersonId(CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        return SecurityUtils.getCurrentPersonId() != null ? SecurityUtils.getCurrentPersonId()
                : carrierInvoiceDetails.getModification().getCreatedBy();
    }
}
