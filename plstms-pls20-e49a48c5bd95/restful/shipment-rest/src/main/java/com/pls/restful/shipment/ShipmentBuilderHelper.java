package com.pls.restful.shipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.CustomerService;
import com.pls.core.service.FreightBillPayToService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.address.BillToService;
import com.pls.dtobuilder.savedquote.SavedQuoteDTOBuilder;
import com.pls.dtobuilder.shipment.AuditShipmentCostDTOBuilder;
import com.pls.dtobuilder.shipment.CostDetailItemDTOBuilder;
import com.pls.dtobuilder.shipment.LtlPricingProposalEntityBuilder;
import com.pls.dtobuilder.shipment.PricingDetailItemDTOBuilder;
import com.pls.dtobuilder.shipment.SavedQuotePricDtlsDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentDTOBuilder.DataProvider;
import com.pls.dtobuilder.shipment.ShipmentDocumentDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentEventDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentGridTooltipDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentNoteDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentNotificationSourceItemDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentTrackingDTOBuilder;
import com.pls.dtobuilder.shipment.VendorBillDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.quote.service.SavedQuoteService;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * 
 * Helper to get Shipment Builders.
 *
 * @author Brichak Aleksandr
 */
@Controller
public class ShipmentBuilderHelper {

    @Autowired
    private AddressService addressService;
    @Autowired
    private BillToService billToService;
    @Autowired
    private FreightBillPayToService freightBillPayToService;
    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private CarrierInvoiceService carrierInvoiceService;
    @Autowired
    private SavedQuoteService savedQuoteService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LtlProfileDetailsService ltlProfileDetailsService;

    private final ShipmentDTOBuilder shipmentBuilder = new ShipmentDTOBuilder(new DataProvider() {
        @Override
        public AddressEntity getAddress(Long id) {
            return id == null ? null : addressService.getAddressEntityById(id);
        }

        @Override
        public BillToEntity getBillTo(Long id) {
            return billToService.getBillTo(id);
        }

        @Override
        public LoadEntity getLoadById(Long id) {
            return shipmentService.getShipmentWithAllDependencies(id);
        }

        @Override
        public FreightBillPayToEntity getDefaultFreightBillPayTo() {
            return freightBillPayToService.getDefaultFreightBillPayTo();
        }

        @Override
        public PackageTypeEntity findPackageType(String id) {
            return packageTypeDictionaryService.getById(id);
        }

        @Override
        public QuotedBO getPrimaryLoadCostDetail(Long loadId) {
            return shipmentService.getPrimaryLoadCostDetail(loadId);
        }

        @Override
        public Boolean isPrintBarcode(Long customerId) {
            return customerService.isPrintBarcode(customerId);
        }

        @Override
        public LtlPricingProfileEntity getProfileById(Long pricingProfileDetailId) {
            return ltlProfileDetailsService.getProfileById(pricingProfileDetailId);
        }
    });

    private final VendorBillDTOBuilder vendorBillDTOBuilder = new VendorBillDTOBuilder(
            new VendorBillDTOBuilder.DataProvider() {
                @Override
                public CarrierInvoiceDetailsEntity getCarrierInvoiceDetails(Long id) {
                    return carrierInvoiceService.getById(id);
                }

                @Override
                public CarrierInvoiceAddressDetailsEntity getOriginAddress(Long id) {
                    return carrierInvoiceService.getCarrierInvoiceAddressDetailsEntityById(id);
                }

                @Override
                public CarrierInvoiceAddressDetailsEntity getDestinationAddress(Long id) {
                    return carrierInvoiceService.getCarrierInvoiceAddressDetailsEntityById(id);
                }
            });

    private SavedQuoteDTOBuilder savedQuoteDTOBuilder = new SavedQuoteDTOBuilder(
            new SavedQuoteDTOBuilder.DataProvider() {
                @Override
                public PackageTypeEntity findPackageType(String id) {
                    return packageTypeDictionaryService.getById(id);
                }

                @Override
                public SavedQuoteEntity findSavedQuoteById(Long id) {
                    return savedQuoteService.getSavedQuoteById(id);
                }
            });

    private static final CostDetailItemDTOBuilder COST_DETAIL_ITEM_BUILDER = new CostDetailItemDTOBuilder();
    private static final ShipmentNoteDTOBuilder SHIPMENT_NOTE_BUILDER = new ShipmentNoteDTOBuilder();
    private static final PricingDetailItemDTOBuilder PRICING_DETAIL_BUILDER = new PricingDetailItemDTOBuilder();
    private static final SavedQuotePricDtlsDTOBuilder SAVED_QUOTE_PRIC_DTL_BUILDER = new SavedQuotePricDtlsDTOBuilder();
    private static final LtlPricingProposalEntityBuilder PRICING_PROPOSAL_BUILDER = new LtlPricingProposalEntityBuilder();
    private static final ShipmentGridTooltipDTOBuilder SHIPMENT_GRID_TOOLTIP_BUILDER = new ShipmentGridTooltipDTOBuilder();
    private static final ShipmentDocumentDTOBuilder SHIPMENT_DOCS_BUILDER = new ShipmentDocumentDTOBuilder();
    private static final ShipmentEventDTOBuilder EVENT_BUILDER = new ShipmentEventDTOBuilder();
    private static final ShipmentTrackingDTOBuilder TRACKING_BUILDER = new ShipmentTrackingDTOBuilder();
    private static final AuditShipmentCostDTOBuilder AUDIT_SHIPMENT_COST_DETAILS_BUILDER = new AuditShipmentCostDTOBuilder();
    private static final ShipmentNotificationSourceItemDTOBuilder SHIPMENT_NOTIFICATION_SOURCE_BUILDER =
            new ShipmentNotificationSourceItemDTOBuilder();

    /**
     * Get {@link SavedQuoteDTOBuilder} instance.
     * 
     * @return {@link SavedQuoteDTOBuilder}
     */
    public SavedQuoteDTOBuilder getSavedQuoteDTOBuilder() {
        return savedQuoteDTOBuilder;
    }

    /**
     * Get {@link ShipmentNotificationSourceItemDTOBuilder} instance.
     * 
     * @return {@link ShipmentNotificationSourceItemDTOBuilder}
     */
    public ShipmentNotificationSourceItemDTOBuilder getShipmentNotificationSourceItemDTOBuilder() {
        return SHIPMENT_NOTIFICATION_SOURCE_BUILDER;
    }

    /**
     * Get {@link ShipmentEventDTOBuilder} instance.
     * 
     * @return {@link ShipmentEventDTOBuilder}
     */
    public ShipmentEventDTOBuilder getShipmentEventDTOBuilder() {
        return EVENT_BUILDER;
    }

    /**
     * Get {@link ShipmentTrackingDTOBuilder} instance.
     * 
     * @return {@link ShipmentTrackingDTOBuilder}
     */
    public ShipmentTrackingDTOBuilder getShipmentTrackingDTOBuilder() {
        return TRACKING_BUILDER;
    }

    /**
     * Get {@link AuditShipmentCostDTOBuilder} instance.
     * 
     * @return {@link AuditShipmentCostDTOBuilder}
     */
    public AuditShipmentCostDTOBuilder getAuditShipmentCostDTOBuilder() {
        return AUDIT_SHIPMENT_COST_DETAILS_BUILDER;
    }

    /**
     * Get {@link CostDetailItemDTOBuilder} instance.
     * 
     * @return {@link CostDetailItemDTOBuilder}
     */
    public CostDetailItemDTOBuilder getCostDetailItemDTOBuilder() {
        return COST_DETAIL_ITEM_BUILDER;
    }

    /**
     * Get {@link VendorBillDTOBuilder} instance.
     * 
     * @return {@link VendorBillDTOBuilder}
     */
    public VendorBillDTOBuilder getVendorBillDTOBuilder() {
        return vendorBillDTOBuilder;
    }

    /**
     * Get {@link ShipmentDocumentDTOBuilder} instance.
     * 
     * @return {@link ShipmentDocumentDTOBuilder}
     */
    public ShipmentDocumentDTOBuilder getShipmentDocumentDTOBuilder() {
        return SHIPMENT_DOCS_BUILDER;
    }

    /**
     * Get {@link ShipmentGridTooltipDTOBuilder} instance.
     * 
     * @return {@link ShipmentGridTooltipDTOBuilder}
     */
    public ShipmentGridTooltipDTOBuilder getShipmentGridTooltipDTOBuilder() {
        return SHIPMENT_GRID_TOOLTIP_BUILDER;
    }

    /**
     * Get {@link ShipmentNoteDTOBuilder} instance.
     * 
     * @return {@link ShipmentNoteDTOBuilder}
     */
    public ShipmentNoteDTOBuilder getShipmentNoteDTOBuilder() {
        return SHIPMENT_NOTE_BUILDER;
    }

    /**
     * Get {@link PricingDetailItemDTOBuilder} instance.
     * 
     * @return {@link PricingDetailItemDTOBuilder}
     */
    public PricingDetailItemDTOBuilder getPricingDetailItemDTOBuilder() {
        return PRICING_DETAIL_BUILDER;
    }

    /**
     * Get {@link SavedQuotePricDtlsDTOBuilder} instance.
     * 
     * @return {@link SavedQuotePricDtlsDTOBuilder}
     */
    public SavedQuotePricDtlsDTOBuilder getSavedQuotePricDtlsDTOBuilder() {
        return SAVED_QUOTE_PRIC_DTL_BUILDER;
    }

    /**
     * Get {@link LtlPricingProposalEntityBuilder} instance.
     * 
     * @return {@link LtlPricingProposalEntityBuilder}
     */
    public LtlPricingProposalEntityBuilder getLtlPricingProposalBuilder() {
        return PRICING_PROPOSAL_BUILDER;
}

    /**
     * Get {@link ShipmentDTOBuilder} instance.
     * 
     * @return {@link ShipmentDTOBuilder}
     */
    public ShipmentDTOBuilder getShipmentDTOBuilder() {
        return shipmentBuilder;
    }
}
