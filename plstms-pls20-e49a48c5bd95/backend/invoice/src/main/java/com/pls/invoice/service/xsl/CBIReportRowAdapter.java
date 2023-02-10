package com.pls.invoice.service.xsl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;

/**
 * Adapts LoadEntity or FinancialAccessorialsEntity for the CBI report row data.
 *
 * @author Sergey Kirichenko
 */
public class CBIReportRowAdapter {

    private static final String LINEHAUL_TYPE = "SRA";
    private static final String CARRIER_TYPE = "CRA";
    private static final String TRANSACTION_TYPE = "TX";
    private static final String FUEL_TYPE = "FS";
    private static final String BENCHMARK_TYPE = "SBR";

    private final LoadEntity load;
    private final FinancialAccessorialsEntity adjustment;

    /**
     * Builds BO from LoadEntity.
     *
     * @param load load entity
     */
    public CBIReportRowAdapter(LoadEntity load) {
        this.load = load;
        adjustment = null;
    }

    /**
     * Builds BO from FinancialAccessorialsEntity.
     *
     * @param adjustment is a {@link FinancialAccessorialsEntity}
     */
    public CBIReportRowAdapter(FinancialAccessorialsEntity adjustment) {
        this.adjustment = adjustment;
        this.load = adjustment.getLoad();
    }

    public String getBillToLocation() {
        return load.getLocation().getLocationName();
    }

    public String getGlNumber() {
        return load.getNumbers().getGlNumber();
    }

    public String getShipperName() {
        return load.getOrganization().getName();
    }

    public String getLoadId() {
        return load.getId().toString();
    }

    public String getFreightCompanyName() {
        return load.getFreightBillPayTo() != null ? load.getFreightBillPayTo().getCompany() : "";
    }

    /**
     * Calculates commodity class.
     *
     * @return class as a {@link String}
     */
    public String getCommodityClass() {
        String result = "";
        Set<LoadMaterialEntity> items = load.getOrigin().getLoadMaterials();
        if (!items.isEmpty()) {
            result = items.size() == 1 ? items.iterator().next().getCommodityClass().getDbCode() : "multi";
        }
        return result;
    }

    /**
     * Get concatenate commodity class.
     * 
     * @return concatenated commodity classes
     */
    public String getCommodityClassString() {
        List<String> result = new ArrayList<String>();
        if (load.getOrigin() != null) {
            Set<LoadMaterialEntity> items = load.getOrigin().getLoadMaterials();
            for (LoadMaterialEntity item : items) {
                result.add(item.getCommodityClass().getDbCode());
            }
        }
        return StringUtils.join(result, ";");
    }

    public String getShipmentDirection() {
        return load.getShipmentDirection() != null ? load.getShipmentDirection().getDescription() : "";
    }

    public String getShipmentDirectionCode() {
        return load.getShipmentDirection() != null ? load.getShipmentDirection().getCode() : "";
    }

    public String getPaymentTerms() {
        return load.getPayTerms() != null ? load.getPayTerms().getDescription() : "";
    }

    public String getPaymentTermsCode() {
        return load.getPayTerms() != null ? load.getPayTerms().getPaymentTermsCode() : "";
    }

    public Date getShipDate() {
        return load.getOrigin().getDeparture();
    }

    public Date getDeliveryDate() {
        return load.getDestination().getDeparture();
    }

    public String getRefNumber() {
        return load.getNumbers().getRefNumber();
    }

    public String getPuNumber() {
        return load.getNumbers().getPuNumber();
    }

    public String getBolNumber() {
        return load.getNumbers().getBolNumber();
    }

    public String getSoNumber() {
        return load.getNumbers().getSoNumber();
    }

    public String getPoNumber() {
        return load.getNumbers().getPoNumber();
    }

    public String getProNumber() {
        return load.getNumbers().getProNumber();
    }

    public String getCarrierSCAC() {
        return load.getCarrier().getScac();
    }

    public String getCarrierName() {
        return load.getCarrier().getName();
    }

    public String getOriginAddress() {
        return getAddressString(load.getOrigin().getAddress());
    }

    public String getOriginAddress1() {
        return load.getOrigin().getAddress().getAddress1();
    }

    public String getOriginCity() {
        return load.getOrigin().getAddress().getCity();
    }

    public String getOriginState() {
        return load.getOrigin().getAddress().getStateCode();
    }

    public String getOriginZip() {
        return load.getOrigin().getAddress().getZip();
    }

    public BigDecimal getResidentialPickupCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.RESIDENTIAL_PICKUP.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getLiftGatePickupCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.LIFT_GATE_PICKUP.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getInsidePickupCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.INSIDE_PICKUP.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getOverDimensionCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.OVER_DIMENSION.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getBlindBolCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.BLIND_BOL.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getLimitedAccessPickupCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.LIMITED_ACCESS_PICKUP.getCode(), CostDetailOwner.S));
    }

    public String getDestinationAddress() {
        return getAddressString(load.getDestination().getAddress());
    }

    public String getDestinationAddress1() {
        return load.getDestination().getAddress().getAddress1();
    }

    public String getDestinationCity() {
        return load.getDestination().getAddress().getCity();
    }

    public String getDestinationState() {
        return load.getDestination().getAddress().getStateCode();
    }

    public String getDestinationZip() {
        return load.getDestination().getAddress().getZip();
    }

    public BigDecimal getResidentialDeliveryCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.RESIDENTIAL_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getLiftGateDeliveryCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.LIFT_GATE_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getInsideDeliveryCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.INSIDE_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getSortSegregateCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.SORT_SEGREGATE_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getNotifyCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.NOTIFY_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public BigDecimal getLimitedAccessDeliveryCost() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LtlAccessorialType.LIMITED_ACCESS_DELIVERY.getCode(), CostDetailOwner.S));
    }

    public String getWeight() {
        return String.valueOf(load.getWeight());
    }

    public String getMileage() {
        return String.valueOf(load.getMileage());
    }

    public BigDecimal getCarrierRate() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(CARRIER_TYPE, CostDetailOwner.C));
    }

    public BigDecimal getLineHaul() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(LINEHAUL_TYPE, CostDetailOwner.S));
    }

    public BigDecimal getTransactionFee() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(TRANSACTION_TYPE, CostDetailOwner.S));
    }

    public BigDecimal getOtherAccessorialsCost() {
        return getAccessorialsCost(new CostDetailMatcherNotInTypes(CostDetailOwner.S, FUEL_TYPE, LINEHAUL_TYPE, TRANSACTION_TYPE,
                LtlAccessorialType.RESIDENTIAL_PICKUP.getCode(), LtlAccessorialType.RESIDENTIAL_DELIVERY.getCode(),
                LtlAccessorialType.LIFT_GATE_PICKUP.getCode(), LtlAccessorialType.LIFT_GATE_DELIVERY.getCode(),
                LtlAccessorialType.INSIDE_PICKUP.getCode(), LtlAccessorialType.INSIDE_DELIVERY.getCode(),
                LtlAccessorialType.OVER_DIMENSION.getCode(), LtlAccessorialType.BLIND_BOL.getCode(),
                LtlAccessorialType.LIMITED_ACCESS_PICKUP.getCode(), LtlAccessorialType.LIMITED_ACCESS_DELIVERY.getCode(),
                LtlAccessorialType.SORT_SEGREGATE_DELIVERY.getCode(), LtlAccessorialType.NOTIFY_DELIVERY.getCode()));
    }

    public BigDecimal getAllAccessorialsCost() {
        return getAccessorialsCost(new CostDetailMatcherNotInTypes(CostDetailOwner.S, CARRIER_TYPE, LINEHAUL_TYPE, FUEL_TYPE, TRANSACTION_TYPE));
    }

    public BigDecimal getFuelSurcharge() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(FUEL_TYPE, CostDetailOwner.S));
    }

    /**
     * Calculates total revenue.
     *
     * @return total revenue as {@link BigDecimal}
     */
    public BigDecimal getTotalRevenue() {
        if (adjustment != null) {
            return adjustment.getTotalRevenue();
        } else {
            return load.getActiveCostDetail().getTotalRevenue();
        }
    }

    public BigDecimal getBenchmark() {
        return getAccessorialsCost(new CostDetailMatcherByTypeAndOwner(BENCHMARK_TYPE, CostDetailOwner.B));
    }

    public BigDecimal getGainShareCost() {
        return getAccessorialsCost(new CostDetailMatcherByOwner(CostDetailOwner.B));
    }

    private Collection<CostDetailItemEntity> getCostItems() {
        Collection<CostDetailItemEntity> costItems;
        if (adjustment != null) {
            costItems = adjustment.getCostDetailItems();
        } else {
            costItems = load.getActiveCostDetail().getCostDetailItems();
        }
        return costItems;
    }

    private String getAddressString(AddressEntity address) {
        String state = StringUtils.isNotBlank(address.getStateCode()) ? ", " + address.getStateCode() : "";
        return address.getCity() + state + ", " + address.getZip();
    }

    private BigDecimal getAccessorialsCost(CostDetailMatcher matcher) {
        BigDecimal result = BigDecimal.ZERO;
        for (CostDetailItemEntity costItem : getCostItems()) {
            if (matcher.isCostItemMatched(costItem)) {
                result = result.add(costItem.getSubtotal());
            }
        }
        return result;
    }

    public String getBillToId() {
        return load.getBillTo() != null ? load.getBillTo().getId().toString() : "";
    }

    public String getCarrierOriginAddressName() {
        return load.getOrigin().getContact();
    }

    public String getCarrierDestinationAddressName() {
        return load.getDestination().getContact();
    }

    public String getInvoiceNumber() {
        return adjustment == null ? load.getActiveCostDetail().getInvoiceNumber() : adjustment.getInvoiceNumber();
    }

    /**
     * Interface to check whether CostDetailItemEntity conform to defined criteria.
     */
    private interface CostDetailMatcher {
        /**
         * Checks whether CostDetailItemEntity conform to defined criteria.
         *
         * @param costItem item to check
         * @return true if item conform to criteria.
         */
        boolean isCostItemMatched(CostDetailItemEntity costItem);
    }

    /**
     * Checks CostDetailItemEntity by type and owner.
     */
    private final class CostDetailMatcherByTypeAndOwner implements CostDetailMatcher {

        private final String type;

        private final CostDetailOwner owner;

        CostDetailMatcherByTypeAndOwner(String type, CostDetailOwner owner) {
            this.type = type;
            this.owner = owner;
        }

        @Override
        public boolean isCostItemMatched(CostDetailItemEntity costItem) {
            return StringUtils.equals(costItem.getAccessorialType(), type) && (owner == null || costItem.getOwner().equals(owner));
        }
    }

    /**
     * Checks CostDetailItemEntity not int type and by owner.
     */
    private final class CostDetailMatcherNotInTypes implements CostDetailMatcher {

        private final List<String> excludedTypes;

        private final CostDetailOwner owner;

        CostDetailMatcherNotInTypes(CostDetailOwner owner, String... excludedTypes) {
            this.owner = owner;
            this.excludedTypes = Arrays.asList(excludedTypes);
        }

        @Override
        public boolean isCostItemMatched(CostDetailItemEntity costItem) {
            return !excludedTypes.contains(costItem.getAccessorialType()) && (owner == null || costItem.getOwner().equals(owner));
        }
    }

    /**
     * Checks CostDetailItemEntity by owner.
     */
    private final class CostDetailMatcherByOwner implements CostDetailMatcher {

        private final CostDetailOwner owner;

        CostDetailMatcherByOwner(CostDetailOwner owner) {
            this.owner = owner;
        }

        @Override
        public boolean isCostItemMatched(CostDetailItemEntity costItem) {
            return costItem.getOwner().equals(owner);
        }
    }
}
