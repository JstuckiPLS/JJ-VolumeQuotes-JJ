package com.pls.dtobuilder.shipment;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.shared.Status;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PlainAddressDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.enums.PaymentTermsDTO;
import com.pls.dto.shipment.CarrierInvoiceCostItemDTO;
import com.pls.dto.shipment.CarrierInvoiceLineItemDTO;
import com.pls.dto.shipment.VendorBillDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;

/**
 * DTO builder for {@link CarrierInvoiceDetailsEntity}.
 *
 * @author Mikhail Boldinov, 01/10/13
 */
public class VendorBillDTOBuilder extends AbstractDTOBuilder<CarrierInvoiceDetailsEntity, VendorBillDTO> {

    private final CarrierDTOBuilder carrierDTOBuilder = new CarrierDTOBuilder();
    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();
    private final CarrierInvoiceLineItemDTOBuilder carrierInvoiceLineItemDTOBuilder = new CarrierInvoiceLineItemDTOBuilder();
    private final CarrierInvoiceCostItemDTOBuilder carrierInvoiceCostItemDTOBuilder = new CarrierInvoiceCostItemDTOBuilder();

    private final DataProvider dataProvider;

    /**
     * Default constructor.
     */
    public VendorBillDTOBuilder() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param dataProvider data provider for update
     */
    public VendorBillDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public VendorBillDTO buildDTO(CarrierInvoiceDetailsEntity entity) {
        VendorBillDTO dto = new VendorBillDTO();
        dto.setId(entity.getId());
        dto.setCarrier(carrierDTOBuilder.buildDTO(entity.getCarrier()));
        if (entity.getOriginAddress() !=  null) {
            dto.setOriginAddress(buildAddressDTO(entity.getOriginAddress()));
        }
        if (entity.getDestinationAddress() !=  null) {
            dto.setDestinationAddress(buildAddressDTO(entity.getDestinationAddress()));
        }
        dto.setActualPickupDate(entity.getActualPickupDate());
        dto.setEstimatedDeliveryDate(entity.getEstDeliveryDate());
        dto.setActualDeliveryDate(entity.getDeliveryDate());
        dto.setVendorBillDate(entity.getInvoiceDate());
        dto.setVendorBillNumber(entity.getInvoiceNumber());
        dto.setWeight(entity.getTotalWeight());
        dto.setPro(entity.getProNumber());
        dto.setBol(entity.getBolNumber());
        dto.setPo(entity.getPoNumber());
        dto.setPu(entity.getReferenceNumber());
        dto.setQuoteId(entity.getShipperRefNumber());
        dto.setAmount(entity.getNetAmount());
        dto.setMatchedLoadId(entity.getMatchedLoadId());
        dto.setEdi(entity.getEdi());
        if (entity.getPaymentTerms() != null) {
            dto.setPayTerm(PaymentTermsDTO.valueOf(entity.getPaymentTerms().name()));
        }
        if (entity.getCarrierInvoiceLineItems() != null) {
            dto.setLineItems(carrierInvoiceLineItemDTOBuilder.buildList(entity.getCarrierInvoiceLineItems()));
        }
        if (entity.getCarrierInvoiceCostItems() != null) {
            dto.setCostItems(carrierInvoiceCostItemDTOBuilder.buildList(entity.getCarrierInvoiceCostItems()));
        }
        return dto;
    }

    @Override
    public CarrierInvoiceDetailsEntity buildEntity(VendorBillDTO dto) {
        CarrierInvoiceDetailsEntity entity = getNewOrExistingCarrierInvoiceDetailsEntity(dto.getId());
        entity.setStatus(Status.ACTIVE);
        if (dto.getMatchedLoadId() != null) {
            entity.setMatchedLoadId(dto.getMatchedLoadId());
        }
        CarrierDTO carrierDTO = dto.getCarrier();
        if (carrierDTO != null && carrierDTO.getId() != null) {
            CarrierEntity carrier = new CarrierEntity();
            carrier.setId(carrierDTO.getId());
            entity.setCarrier(carrier);
        }
        entity.setActualPickupDate(dto.getActualPickupDate());
        entity.setEstDeliveryDate(dto.getEstimatedDeliveryDate());
        entity.setDeliveryDate(dto.getActualDeliveryDate());
        entity.setInvoiceDate(dto.getVendorBillDate());
        entity.setInvoiceNumber(dto.getVendorBillNumber());
        entity.setTotalWeight(dto.getWeight());
        entity.setProNumber(dto.getPro());
        entity.setBolNumber(dto.getBol());
        entity.setPoNumber(dto.getPo());
        entity.setNetAmount(dto.getAmount());
        entity.setReferenceNumber(dto.getPu());
        entity.setShipperRefNumber(dto.getQuoteId());
        if (dto.getPayTerm() != null) {
            entity.setPaymentTerms(PaymentTerms.valueOf(dto.getPayTerm().name()));
        }
        PlainAddressDTO originAddressDTO = dto.getOriginAddress();
        if (originAddressDTO != null) {
            CarrierInvoiceAddressDetailsEntity originAddress = dataProvider.getOriginAddress(originAddressDTO.getId());
            if (originAddress == null) {
                originAddress = new CarrierInvoiceAddressDetailsEntity();
                originAddress.setAddressType(CarrierInvoiceAddressType.ORIGIN);
            }
            entity.setOriginAddress(buildAddressEntity(originAddress, originAddressDTO));
        }
        PlainAddressDTO destinationAddressDTO = dto.getDestinationAddress();
        if (destinationAddressDTO != null) {
            CarrierInvoiceAddressDetailsEntity destinationAddress = dataProvider.getDestinationAddress(destinationAddressDTO.getId());
            if (destinationAddress == null) {
                destinationAddress = new CarrierInvoiceAddressDetailsEntity();
                destinationAddress.setAddressType(CarrierInvoiceAddressType.DESTINATION);
            }
            entity.setDestinationAddress(buildAddressEntity(destinationAddress, destinationAddressDTO));
        }
        setLineItems(dto, entity);
        setCostItems(dto, entity);
        return entity;
    }

    private void setLineItems(VendorBillDTO dto, CarrierInvoiceDetailsEntity entity) {
        List<CarrierInvoiceLineItemDTO> lineItems = dto.getLineItems();
        if (lineItems != null) {
            entity.setCarrierInvoiceLineItems(new HashSet<CarrierInvoiceLineItemEntity>(carrierInvoiceLineItemDTOBuilder.buildEntityList(lineItems)));
            int totalQuantity = 0;
            BigDecimal totalWeight = BigDecimal.ZERO;
            for (CarrierInvoiceLineItemEntity lineItemEntity : entity.getCarrierInvoiceLineItems()) {
                if (lineItemEntity.getWeight() != null) {
                    totalWeight = totalWeight.add(lineItemEntity.getWeight());
                }
                if (lineItemEntity.getQuantity() != null) {
                    totalQuantity += lineItemEntity.getQuantity();
                }
            }
            entity.setTotalQuantity(totalQuantity);
            entity.setTotalWeight(totalWeight);
        }
    }

    private void setCostItems(VendorBillDTO dto, CarrierInvoiceDetailsEntity entity) {
        List<CarrierInvoiceCostItemDTO> costItems = dto.getCostItems();
        BigDecimal totalCharge = BigDecimal.ZERO;
        if (costItems != null) {
            entity.setCarrierInvoiceCostItems(new HashSet<CarrierInvoiceCostItemEntity>(carrierInvoiceCostItemDTOBuilder.buildEntityList(costItems)));
            for (CarrierInvoiceCostItemEntity item : entity.getCarrierInvoiceCostItems()) {
                if (item.getSubtotal() != null) {
                    totalCharge = totalCharge.add(item.getSubtotal());
                }
            }
        }
        entity.setTotalCharges(totalCharge);
    }

    private PlainAddressDTO buildAddressDTO(CarrierInvoiceAddressDetailsEntity addressEntity) {
        PlainAddressDTO addressDTO = new PlainAddressDTO();
        addressDTO.setId(addressEntity.getId());
        addressDTO.setName(addressEntity.getAddressName());
        addressDTO.setAddress1(addressEntity.getAddress1());
        addressDTO.setAddress2(addressEntity.getAddress2());
        ZipDTO zipDTO = zipDTOBuilder.buildDTO(addressEntity.getZipCode());
        addressDTO.setZip(zipDTO);
        addressDTO.setCountry(zipDTO.getCountry());
        return addressDTO;
    }

    private CarrierInvoiceAddressDetailsEntity buildAddressEntity(CarrierInvoiceAddressDetailsEntity addressEntity, PlainAddressDTO addressDTO) {
        addressEntity.setId(addressDTO.getId());
        addressEntity.setAddressName(addressDTO.getName());
        addressEntity.setAddress1(addressDTO.getAddress1());
        addressEntity.setAddress2(addressDTO.getAddress2());
        ZipDTO zipDTO = addressDTO.getZip();
        CountryDTO countryDTO = addressDTO.getCountry();
        if (zipDTO != null) {
            addressEntity.setCity(zipDTO.getCity());
            addressEntity.setState(zipDTO.getState());
            addressEntity.setPostalCode(zipDTO.getZip());
            if (countryDTO == null) {
                countryDTO = zipDTO.getCountry();
            }
        }
        if (countryDTO != null) {
            addressEntity.setCountryCode(countryDTO.getId());
        }
        return addressEntity;
    }

    private CarrierInvoiceDetailsEntity getNewOrExistingCarrierInvoiceDetailsEntity(Long id) {
        CarrierInvoiceDetailsEntity entity = null;
        if (id != null) {
            if (dataProvider != null) {
                entity = dataProvider.getCarrierInvoiceDetails(id);
            } else {
                throw new IllegalArgumentException("For update dataProvider must be used");
            }
        }
        if (entity == null) {
            entity = new CarrierInvoiceDetailsEntity();
            //id with value -1 means address must isn't modified adn this id should be kept for shipment processing
            if (id != null && id == -1L) {
                entity.setId(id);
            }
        }
        return entity;
    }

    /**
     * Carrier Invoice Details data provider for update.
     */
    public interface DataProvider {

        /**
         * Gets Carrier Invoice Details for update.
         *
         * @param id {@link CarrierInvoiceDetailsEntity#getId()}
         * @return {@link CarrierInvoiceDetailsEntity}
         */
        CarrierInvoiceDetailsEntity getCarrierInvoiceDetails(Long id);

        /**
         * Gets Carrier Invoice origin address.
         *
         * @param id {@link CarrierInvoiceAddressDetailsEntity#getId()}
         * @return {@link CarrierInvoiceAddressDetailsEntity}
         */
        CarrierInvoiceAddressDetailsEntity getOriginAddress(Long id);

        /**
         * Gets Carrier Invoice destination address.
         *
         * @param id {@link CarrierInvoiceAddressDetailsEntity#getId()}
         * @return {@link CarrierInvoiceAddressDetailsEntity}
         */
        CarrierInvoiceAddressDetailsEntity getDestinationAddress(Long id);
    }
}
