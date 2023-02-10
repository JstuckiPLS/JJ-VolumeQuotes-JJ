package com.pls.shipment.integration.ltllifecycle.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.integration.ltllifecycle.dto.FreightClass;
import com.pls.ltlrating.integration.ltllifecycle.dto.HazmatPackingGroupType;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.AddressDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ContactDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.DispatchItemDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.DispatchItemDTO.HazmatDetailsDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentCancelRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentDispatchRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.ShipmentTrackRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentDispatchResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.ShipmentTrackingResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.service.LTLLifecycleRestClient;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

@Service("shipment-ltlLifecycle-integration-service")
public class LTLLifecycleIntegrationService {
    
    private static final String PLS20_APPCODE = "PLS20";
    private static final LocalTime DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_START = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_END = LocalTime.of(16, 0);

    @Autowired
    private LTLLifecycleRestClient ltlLifecycleRestClient;
    
    @Autowired
    private LtlProfileDetailsService ltlProfileDetailsService;

    /** Dispatches load using LTL lifecycle.
     * Sets load entity's PU number based on the PU number returned by LTLLifecycle. */
    public void dispatchLoad(LoadEntity load) {
        ShipmentDispatchRequestDTO dispatchRequest = createLtlLifecycleDispatchRequest(load, isBuiltWithCSPprofile(load));
        
        ShipmentDispatchResultDTO dispatchResult = ltlLifecycleRestClient.dispatch(dispatchRequest); 
        
        // update load
        load.setExternalUuid(dispatchResult.getLoadUUID());
        if (dispatchResult.getPu() != null && load.getNumbers().getPuNumber() == null) {
            load.getNumbers().setPuNumber(dispatchResult.getPu());
        }
        
    }
    
    public void cancelLoad(LoadEntity load) {
        ShipmentCancelRequestDTO cancelRequest = new ShipmentCancelRequestDTO();
        cancelRequest.setRequesterApp(PLS20_APPCODE);
        cancelRequest.setLoadUUID(load.getExternalUuid());
        
        ltlLifecycleRestClient.cancel(cancelRequest);
    }
    
    private boolean isBuiltWithCSPprofile(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            Long pricingProfileDetailId = load.getActiveCostDetail().getPricingProfileDetailId();
            LtlPricingProfileEntity profile = ltlProfileDetailsService.getProfileById(pricingProfileDetailId);
            return (profile != null && "CSP".equals(profile.getPricingType().getLtlPricingType()));
        }
        return false;
    }
    
    private boolean isQuoteNumberOnBolActivated(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            LtlPricingProfileEntity profile = ltlProfileDetailsService.getProfileById(load.getActiveCostDetail().getPricingProfileDetailId());
            return (profile != null && Boolean.TRUE.equals(profile.getDisplayQuoteNumberOnBol()));
        }
        return false;
    }

    private ShipmentDispatchRequestDTO createLtlLifecycleDispatchRequest(LoadEntity load, boolean customerSpecific) {
        ShipmentDispatchRequestDTO request = new ShipmentDispatchRequestDTO();
        
        request.setRequesterApp(PLS20_APPCODE);
        
        if (customerSpecific) {
            request.setCustomerId("" + load.getOrganization().getId());
        }
        
        request.setCarrierCode(getCarrierCode(load));
        request.setCurrencyCode(load.getCarrier().getCurrencyCode().name());
        
        // origin / destination address and contacts
        request.setOrigin(getAddressDto(load.getOrigin().getAddress()));
        request.setOriginContact(getContactDto(load.getOrigin()));
        request.setDestination(getAddressDto(load.getDestination().getAddress()));
        request.setDestinationContact(getContactDto(load.getDestination()));
        
        request.setRequesterAddress(getAddressDto(load.getFreightBillPayTo().getAddress()));
        request.setRequesterContact(getContactDto(load.getFreightBillPayTo()));
        
        // Pickup / delivery date / windows
        Date pickupDate = load.getOrigin().getEarlyScheduledArrival();
        request.setPickupDate(pickupDate != null
                ? pickupDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        request.setPickupStartTime(load.getOrigin().getArrivalWindowStart() != null
                ? getTime(load.getOrigin().getArrivalWindowStart())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_START);
        request.setPickupEndTime(load.getOrigin().getArrivalWindowEnd() != null
                ? getTime(load.getOrigin().getArrivalWindowEnd())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_END);
        
        Date deliveryDate = load.getDestination().getScheduledArrival();
        request.setPreferredDeliveryDate(deliveryDate != null
                ? deliveryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        request.setDeliveryStartTime(load.getDestination().getArrivalWindowStart() != null
                ? getTime(load.getDestination().getArrivalWindowStart())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_START);
        request.setDeliveryEndTime(load.getDestination().getArrivalWindowEnd() != null
                ? getTime(load.getDestination().getArrivalWindowEnd())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_END);
        
        // Load items + hazmat emergency info
        List<DispatchItemDTO> items = load.getOrigin().getLoadMaterials().stream().map(orderItem -> {
            DispatchItemDTO item = new DispatchItemDTO();

            item.setProductClass(FreightClass.fromValue(orderItem.getCommodityClass().getDbCode()));

            item.setHeight(orderItem.getHeight());
            item.setWidth(orderItem.getWidth());
            item.setLength(orderItem.getLength());
            item.setWeight(orderItem.getWeight()); 
            
            item.setPackageType(orderItem.getPackageType().getId());
            item.setQuantity(orderItem.getQuantity() == null ? null : Integer.parseInt(orderItem.getQuantity()));
            item.setPieces(orderItem.getPieces());
            
            item.setDescription(orderItem.getProductDescription());
            item.setStackable(orderItem.isStackable());
            
            if (orderItem.getNmfc() != null) {
                // orderItem has the nmfc code in #####-## or ##### format
                String[] nmfcParts = orderItem.getNmfc().split("-");
                item.setNmfcItemCode(nmfcParts[0]);
                item.setNmfcSubCode(nmfcParts.length > 1 ? nmfcParts[1] : null);
            }

            if(Boolean.TRUE==orderItem.isHazmat()) { 
                HazmatDetailsDTO hazmatDetails = new HazmatDetailsDTO();
                item.setHazmatDetails(hazmatDetails);

                hazmatDetails.setHazardClass(orderItem.getHazmatClass());
                hazmatDetails.setIdentificationNumber(orderItem.getUnNumber());
                hazmatDetails.setPackingGroupType(getPackingGroupType(orderItem.getPackingGroup()));
                hazmatDetails.setProperShippingName(orderItem.getProductDescription());
                
                //setting emergency contact
                ContactDTO emergencyContact = new ContactDTO();
                hazmatDetails.setEmergencyContact(emergencyContact);
                emergencyContact.setCompanyName(orderItem.getEmergencyCompany());
                emergencyContact.setContactName(orderItem.getEmergencyContract());
                emergencyContact.setPhoneNumber(PhoneUtils.format(orderItem.getEmergencyPhone()));
            }

            return item;
        }).collect(Collectors.toList());

        request.setItems(items);

        // accesorials
        request.setAdditionalServices(load.getLtlAccessorials().stream().map(i->i.getAccessorial().getId()).collect(Collectors.toList()));

        // notes
        request.setPickupNotes(load.getSpecialInstructions());
        request.setDeliveryNotes(load.getDeliveryNotes());
        
        // shipment identificators
        request.setBol(load.getNumbers().getBolNumber());
        request.setPro(load.getNumbers().getProNumber());
        
        // send the quote number only when we are displaying it on BOL document. 
        if(isQuoteNumberOnBolActivated(load)) {
            request.setQuoteNumber(load.getNumbers().getCarrierQuoteNumber());
        }
        request.setServiceLevelCode(load.getNumbers().getServiceLevelCode());
        // in case of VLTL set quote number always
        if(load.getVolumeQuoteId()!=null) {
            request.setQuoteNumber(load.getVolumeQuoteId());
            request.setShipmentType(ShipmentType.VLTL);
        }
        
        return request;
    }

    private HazmatPackingGroupType getPackingGroupType(String packingGroup) {
        if (packingGroup == null) {
            return HazmatPackingGroupType.NONE;
        }
        // in DB we have groups in various formats, like III, PGIII, PG iii, etc...
        String pg = packingGroup.toUpperCase();
        if(pg.contains("III")) {
            return HazmatPackingGroupType.III;
        }
        if(pg.contains("II")) {
            return HazmatPackingGroupType.II;
        }
        if(pg.equals("I") || pg.contains("PGI") || pg.contains("PG I") ) {
            return HazmatPackingGroupType.I;
        }
        return HazmatPackingGroupType.NONE;
    }
    
    private LocalTime getTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private AddressDTO getAddressDto(AddressEntity addressEntity) {
        AddressDTO address = new AddressDTO();

        address.setAddress1(addressEntity.getAddress1());
        address.setAddress2(addressEntity.getAddress2());
        address.setCountry(addressEntity.getCountryCode());
        address.setCity(addressEntity.getCity());
        address.setState(addressEntity.getStateCode());
        address.setZip(addressEntity.getZip());
        
        return address;
    }
    
    private ContactDTO getContactDto(LoadDetailsEntity loadDetails) {
        ContactDTO contact = new ContactDTO();
        contact.setCompanyName(loadDetails.getContact());
        contact.setContactName(loadDetails.getContactName());
        contact.setEmail(loadDetails.getContactEmail());
        contact.setPhoneNumber(loadDetails.getContactPhone());
        contact.setFaxNumber(loadDetails.getContactFax());
        return contact;
    }
    
    private ContactDTO getContactDto(FreightBillPayToEntity freightBillPayTo) {
        ContactDTO contact = new ContactDTO();
        contact.setCompanyName(freightBillPayTo.getCompany());
        contact.setContactName(freightBillPayTo.getContactName());
        contact.setEmail(freightBillPayTo.getEmail());
        contact.setPhoneNumber(PhoneUtils.format(freightBillPayTo.getPhone()));
        contact.setFaxNumber(PhoneUtils.format(freightBillPayTo.getFax()));
        return contact;
    }

    public void initTrackLoad(LoadEntity load) {
        ShipmentTrackRequestDTO trackRequest = createLtlLifecycleTrackRequest(load, isBuiltWithCSPprofile(load));
        
        ShipmentTrackingResultDTO trackingResult = ltlLifecycleRestClient.initTrack(trackRequest); 
        
        // update load
        load.setExternalUuid(trackingResult.getLoadUUID());
    }
    
    private ShipmentTrackRequestDTO createLtlLifecycleTrackRequest(LoadEntity load, boolean customerSpecific) {
        ShipmentTrackRequestDTO request = new ShipmentTrackRequestDTO();
        
        request.setRequesterApp(PLS20_APPCODE);
        
        if (customerSpecific) {
            request.setCustomerId("" + load.getOrganization().getId());
        }
        
        request.setCarrierCode(getCarrierCode(load));
        
        // origin / destination address and contacts
        request.setOrigin(getAddressDto(load.getOrigin().getAddress()));
        request.setOriginContact(getContactDto(load.getOrigin()));
        request.setDestination(getAddressDto(load.getDestination().getAddress()));
        request.setDestinationContact(getContactDto(load.getDestination()));
        
        // Pickup / delivery date / windows
        Date pickupDate = load.getOrigin().getEarlyScheduledArrival();
        request.setPickupDate(pickupDate != null
                ? pickupDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        request.setPickupStartTime(load.getOrigin().getArrivalWindowStart() != null
                ? getTime(load.getOrigin().getArrivalWindowStart())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_START);
        request.setPickupEndTime(load.getOrigin().getArrivalWindowEnd() != null
                ? getTime(load.getOrigin().getArrivalWindowEnd())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_END);
        
        Date deliveryDate = load.getDestination().getScheduledArrival();
        request.setPreferredDeliveryDate(deliveryDate != null
                ? deliveryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        request.setDeliveryStartTime(load.getDestination().getArrivalWindowStart() != null
                ? getTime(load.getDestination().getArrivalWindowStart())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_START);
        request.setDeliveryEndTime(load.getDestination().getArrivalWindowEnd() != null
                ? getTime(load.getDestination().getArrivalWindowEnd())
                : DEFAULT_PICKUP_DELIVERY_TIMEWINDOW_END);

        // shipment identificators
        request.setBol(load.getNumbers().getBolNumber());
        request.setPro(load.getNumbers().getProNumber());
        
        return request;
    }
    
    private String getCarrierCode(LoadEntity load) {
        return load.getCarrier().getActualScac();
    }

}
