package com.pls.ltlrating.integration.ltllifecycle.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.integration.ltllifecycle.dao.OrganizationLtlLifecycleSettingsDao;
import com.pls.ltlrating.integration.ltllifecycle.domain.OrganizationLtlLifecycleSettingsEntity;
import com.pls.ltlrating.integration.ltllifecycle.dto.FreightClass;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.AddressDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.QuoteItemDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.QuoteRequestDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.ltlrating.shared.GetOrderRatesCO;

@Service
public class LTLLifecycleIntegrationService {

    @Autowired
    private LTLLifecycleRestClient ltlLifecycleRestClient;
    
    @Autowired
    private OrganizationLtlLifecycleSettingsDao orgnLtllcSettingsDao;
    
    /** Get quotes from LTL-lifecycle. 
     * @param order the 2.0 quote request.
     * @param customerSpecific whether we want a customer specific quotation (send customer id in the request) 
     * @param currencyCode the currency we want the quotes to be returned in
     * */
    public CompletableFuture<List<QuoteResultDTO>> getRawQuotesAsync(GetOrderRatesCO order, List<String> scacs, boolean customerSpecific, String currencyCode, ShipmentType shipmentType){
        QuoteRequestDTO ltlQuoteRequest = createLtlLifecycleQuoteRequest(order, scacs, customerSpecific, currencyCode, shipmentType);
        return ltlLifecycleRestClient.getQuotesAsync(ltlQuoteRequest);
    }

    /**
     * Create request for ltl-lifecycle-rest from the GetOrderRatesCO object.
     */
    private QuoteRequestDTO createLtlLifecycleQuoteRequest(GetOrderRatesCO order, List<String> scacs, boolean customerSpecific, String currencyCode, ShipmentType shipmentType) {
        QuoteRequestDTO ltlQuoteRequest = new QuoteRequestDTO();
        ltlQuoteRequest.setRequesterApp("PLS20");
        ltlQuoteRequest.setExtractAlternates(true);
        ltlQuoteRequest.setShipmentType(shipmentType);
        
        if (customerSpecific) {
            ltlQuoteRequest.setCustomerId("" + order.getShipperOrgId());
        }
        
        ltlQuoteRequest.setCarrierCodes(scacs);
        ltlQuoteRequest.setCurrencyCode(currencyCode);

        AddressDTO origin = new AddressDTO();

        AddressVO originAddress = order.getOriginAddress();
        origin.setAddress1(originAddress.getAddress1());
        origin.setAddress2(originAddress.getAddress2());
        origin.setCountry(originAddress.getCountryCode());
        origin.setCity(originAddress.getCity());
        origin.setState(originAddress.getStateCode());
        origin.setZip(originAddress.getPostalCode());
        ltlQuoteRequest.setOrigin(origin);

        AddressVO destAddress = order.getDestinationAddress();
        AddressDTO dest = new AddressDTO();
        dest.setAddress1(destAddress.getAddress1());
        dest.setAddress2(destAddress.getAddress2());
        dest.setCountry(destAddress.getCountryCode());
        dest.setCity(destAddress.getCity());
        dest.setState(destAddress.getStateCode());
        dest.setZip(destAddress.getPostalCode());
        ltlQuoteRequest.setDestination(dest);

        ltlQuoteRequest.setPickupDate(order.getShipDate() != null
                ? order.getShipDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now());
        
        List<QuoteItemDTO> items = order.getMaterials().stream().map(orderItem -> {
            QuoteItemDTO item = new QuoteItemDTO();

            item.setProductClass(FreightClass.fromValue(orderItem.getCommodityClassEnum().getDbCode()));

            item.setHeight(getOrDefault(orderItem.getHeightInInches(), new BigDecimal(48)));
            item.setWidth(getOrDefault(orderItem.getWidthInInches(), new BigDecimal(48)));
            item.setLength(getOrDefault(orderItem.getLengthInInches(), new BigDecimal(48)));
            item.setWeight(orderItem.getWeightInLBS()); 
            
            item.setPackageType(orderItem.getPackageType());
            item.setQuantity(orderItem.getQuantity());
            item.setPieces(orderItem.getPieces());

            if(Boolean.TRUE==orderItem.getHazmatBool()) { 
                item.setHazmat(true);
            }
            
            if (orderItem.getNmfc() != null) {
                // orderItem has the nmfc code in #####-## or ##### format
                String[] nmfcParts = orderItem.getNmfc().split("-");
                item.setNmfcItemCode(nmfcParts[0]);
                item.setNmfcSubCode(nmfcParts.length > 1 ? nmfcParts[1] : null);
            }

            return item;
        }).collect(Collectors.toList());

        ltlQuoteRequest.setItems(items);

        ltlQuoteRequest.setAdditionalServices(order.getAccessorialTypes());
        
        ltlQuoteRequest.setGuaranteedBy(guaranteedStringFromGuaranteedTimeNumber(order.getGuaranteedTime()));
        
        if(order.getShipperOrgId()!=null) {
            try {
                OrganizationLtlLifecycleSettingsEntity settings = orgnLtllcSettingsDao.get(order.getShipperOrgId());
                ltlQuoteRequest.setTimeout(settings.getApiTimeout());
            } catch (EntityNotFoundException e) {
                // do nothing
            }
        }

        return ltlQuoteRequest;
    }
    
    private String guaranteedStringFromGuaranteedTimeNumber(Integer guaranteedTime) {
        if (guaranteedTime == null) {
            return null;
        }
        return String.format("%d:%02d", (guaranteedTime / 100), (guaranteedTime % 100)); // returns the time in format like 14:00
    }
    
    private BigDecimal getOrDefault(BigDecimal value, BigDecimal def) {
        return value != null ? value : def;
    }

}
