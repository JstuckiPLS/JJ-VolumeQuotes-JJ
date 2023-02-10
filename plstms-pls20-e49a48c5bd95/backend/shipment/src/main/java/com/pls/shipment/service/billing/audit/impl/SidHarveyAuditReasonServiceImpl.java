package com.pls.shipment.service.billing.audit.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.shared.AccessorialType;
import com.pls.core.shared.Reasons;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.billing.audit.SidHarveyAuditReasonService;

/**
 * Implements {@link SidHarveyAuditReasonService}.
 * 
 * @author Brichak Aleksandr
 */
@Service
@Transactional
public class SidHarveyAuditReasonServiceImpl implements SidHarveyAuditReasonService {

    static final BigDecimal MAX_REVENUE = new BigDecimal(1000);

    static final BigDecimal MAX_COST = new BigDecimal(800);

    static final BigDecimal MAX_MARGIN = new BigDecimal(5);

    static final Long SID_HARVEY_ID = 193362L;

    @Override
    public List<Reasons> getBillingAuditReasonForSidHarvey(LoadEntity load) {
        if (SID_HARVEY_ID.equals(load.getOrganization().getId())) {
            return getPriceReasons(load);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isSidHarvey(Long id) {
        return SID_HARVEY_ID.equals(id);
    }

    private List<Reasons> getPriceReasons(LoadEntity load) {
        List<Reasons> result = new ArrayList<Reasons>(4);
        if (isMarginReview(load)) {
            result.add(Reasons.MARGIN_REVIEW);
        }
        if (isAccessorialReview(load)) {
            result.add(Reasons.ACCESSORIAL_REVIEW);
        }
        if (isHighRevenueReview(load)) {
            result.add(Reasons.HIGH_REVENUE_REVIEW);
        }
        if (isHighCostReview(load)) {
            result.add(Reasons.HIGH_COST_REVIEW);
        }
        return result;
    }

    private boolean isMarginReview(LoadEntity load) {
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        BigDecimal percentMargin = activeCostDetail.getMargin();
        BigDecimal totalMargin = activeCostDetail.getTotalRevenue().subtract(activeCostDetail.getTotalCost());
        return percentMargin.compareTo(MAX_MARGIN) < 0 && totalMargin.compareTo(MAX_MARGIN) > 0;
    }

    private boolean isAccessorialReview(LoadEntity load) {
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        BigDecimal totalAccessorial = BigDecimal.ZERO;
        Set<CostDetailItemEntity> accesorials = activeCostDetail.getCostDetailItems();
        for (CostDetailItemEntity accesorial : accesorials) {
            if (!isBaseAccessorials(accesorial.getAccessorialType())) {
                totalAccessorial = addTotalAccessorialsCost(totalAccessorial, accesorial);
                if (isRevenueNotEqualCost(accesorial.getAccessorialType(), accesorials) || totalAccessorial.compareTo(BigDecimal.TEN) > 0
                        || isEmptyMiscNote(accesorial)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHighRevenueReview(LoadEntity load) {
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        return activeCostDetail.getTotalRevenue().compareTo(MAX_REVENUE) > 0;
    }

    private boolean isHighCostReview(LoadEntity load) {
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        return activeCostDetail.getTotalCost().compareTo(MAX_COST) > 0;
    }

    private boolean isRevenueNotEqualCost(String accessorialType, Set<CostDetailItemEntity> accesorials) {
        BigDecimal cost = null;
        BigDecimal revenue = null;
        for (CostDetailItemEntity accesorial : accesorials) {
            if (accessorialType.equalsIgnoreCase(accesorial.getAccessorialType())) {
                if (accesorial.getOwner() == CostDetailOwner.S) {
                    revenue = accesorial.getSubtotal();
                } else if (accesorial.getOwner() == CostDetailOwner.C) {
                    cost = accesorial.getSubtotal();
                }
            }
        }
        return ObjectUtils.defaultIfNull(cost, BigDecimal.ZERO).compareTo(ObjectUtils.defaultIfNull(revenue, BigDecimal.ZERO)) != 0;
    }

    private boolean isBaseAccessorials(String accesorialType) {
        return accesorialType.equalsIgnoreCase(AccessorialType.FUEL_SURCHARGE.getType())
                || accesorialType.equalsIgnoreCase(AccessorialType.CARRIER_BASE_RATE.getType())
                || accesorialType.equalsIgnoreCase(AccessorialType.SHIPPER_BASE_RATE.getType());
    }

    private boolean isEmptyMiscNote(CostDetailItemEntity accesorial) {
        return accesorial.getAccessorialType().equalsIgnoreCase(AccessorialType.MISC.getType()) && accesorial.getOwner() == CostDetailOwner.S
                && StringUtils.isBlank(accesorial.getNote());
    }

    private BigDecimal addTotalAccessorialsCost(BigDecimal totalAccessorial, CostDetailItemEntity accesorial) {
        if (accesorial.getOwner().compareTo(CostDetailOwner.C) == 0) {
            return totalAccessorial.add(accesorial.getSubtotal());
        }
        return totalAccessorial;
    }
}
