package com.pls.restful.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.SerializationUtils;

import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.service.ShipmentService;

/**
 * Utility class for working with shipment proposals.
 * 
 * @author Aleksandr Leshchenko
 */
public final class ShipmentProposalUtils {
    private static final String RECENT_PROPOSITIONS_SESSION_ATTRIBUTE = "RECENT_PROPOSITIONS";
    private static final int SAVED_QUOTE_RERATE_DAYS = 7;

    private ShipmentProposalUtils() {
    }

    /**
     * Save copy of shipment propositions to current session.
     * 
     * @param session
     *            instance of current {@link HttpSession}
     * @param shipmentGuid
     *            unique identifier of shipment. Note: this can't be id because shipment may not be created yet
     * @param propositions list of propositions to save
     */
    public static void saveShipmentPropositions(HttpSession session, String shipmentGuid, List<ShipmentProposalBO> propositions) {
        session.setAttribute(RECENT_PROPOSITIONS_SESSION_ATTRIBUTE + shipmentGuid,
                SerializationUtils.clone(new ArrayList<ShipmentProposalBO>(propositions)));
    }

    /**
     * Find proposition from list of items saved in session by id of carrier tariff.<br/>
     * Filters cost detail items based on selected guaranteed by option.
     * 
     * @param session
     *            instance of current {@link HttpSession}
     * @param shipmentGuid
     *            unique identifier of shipment. Note: this can't be id because shipment may not be created yet
     * @param carrierTariffGuid
     *            unique identifier of carrier tariff
     * @param guaranteedBy
     *            selected guaranteedBy option to filter guaranteed cost detail items
     * @return found shipment proposal
     * @throws IllegalArgumentException
     *             if proposal not found
     */
    @SuppressWarnings("unchecked")
    public static ShipmentProposalBO getProposalFromSessionForShipment(HttpSession session, String shipmentGuid, String carrierTariffGuid,
            Long guaranteedBy) {
        List<ShipmentProposalBO> propositions = (List<ShipmentProposalBO>) session
                .getAttribute(ShipmentProposalUtils.RECENT_PROPOSITIONS_SESSION_ATTRIBUTE + shipmentGuid);
        if (propositions != null && carrierTariffGuid != null) {
            for (ShipmentProposalBO proposition : propositions) {
                if (carrierTariffGuid.equals(proposition.getGuid())) {
                    return ShipmentProposalUtils.filterGuaranteedOptions(proposition, guaranteedBy);
                }
            }
        }
        throw new IllegalArgumentException("No carrier tariff found for guid " + carrierTariffGuid);
    }

    /**
     * Filters cost detail items based on selected guaranteed by option.
     * 
     * @param proposition
     *            to filter cost detail items
     * @param guaranteedBy
     *            selected guaranteedBy option to filter guaranteed cost detail items
     * @return proposition with filtered guaranteed cost detail items
     */
    public static ShipmentProposalBO filterGuaranteedOptions(ShipmentProposalBO proposition, final Long guaranteedBy) {
        if (proposition.getCostDetailItems() == null) {
            proposition.setCostDetailItems(new ArrayList<CostDetailItemBO>());
        }
        final Long applicableGuaranteedBy = ShipmentProposalUtils.findApplicableGuaranteedByOption(proposition.getCostDetailItems(), guaranteedBy);
        CollectionUtils.filter(proposition.getCostDetailItems(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CostDetailItemBO costDetailItem = (CostDetailItemBO) object;
                if (!ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(costDetailItem.getRefType())) {
                    // not a guaranteed cost detail item
                    return true;
                } else if (applicableGuaranteedBy == -1) {
                    // applicable to sales order and to rate quote (in case of Any Time option)
                    costDetailItem.setGuaranteedBy(guaranteedBy);
                    return true;
                } else if (applicableGuaranteedBy.equals(costDetailItem.getGuaranteedBy())) {
                    // applicable to rate quote wizard or saved quote
                    return true;
                }
                // guaranteed cost detail item with non-applicable guaranteed by option
                return false;
            }
        });
        return proposition;
    }

    /**
     * Check if saved quote expired.
     * 
     * @param createdDate
     *            date when quote has been created
     * @param pickupDate
     *            date when load should be picked up
     * @return <code>true</code> if expired
     */
    public static boolean isSavedQuoteExpired(Date createdDate, Date pickupDate) {
        Calendar today = Calendar.getInstance();
        Calendar pickup = Calendar.getInstance();
        pickup.setTime(pickupDate);

        if ((today.get(Calendar.DAY_OF_YEAR) != pickup.get(Calendar.DAY_OF_YEAR) || today.get(Calendar.YEAR) != pickup.get(Calendar.YEAR))
                && pickup.before(today)) {
            return true;
        }

        today.add(Calendar.DAY_OF_YEAR, -SAVED_QUOTE_RERATE_DAYS - 1);
        Calendar created = Calendar.getInstance();
        created.setTime(createdDate);
        return (today.get(Calendar.DAY_OF_YEAR) == created.get(Calendar.DAY_OF_YEAR) && today.get(Calendar.YEAR) == created.get(Calendar.YEAR))
                || today.after(created);
    }

    private static Long findApplicableGuaranteedByOption(List<CostDetailItemBO> costDetails, Long guaranteedBy) {
        if (guaranteedBy != null) {
            long mostApplicable = -2L;
            for (CostDetailItemBO costDetailItem : costDetails) {
                if (ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(costDetailItem.getRefType()) && costDetailItem.getGuaranteedBy() != null) {
                    if (guaranteedBy.equals(costDetailItem.getGuaranteedBy())) {
                        mostApplicable = costDetailItem.getGuaranteedBy();
                        break;
                    } else if (costDetailItem.getGuaranteedBy() < guaranteedBy && costDetailItem.getGuaranteedBy() > mostApplicable) {
                        mostApplicable = costDetailItem.getGuaranteedBy();
                    }
                }
            }
            return mostApplicable == -2 ? ShipmentProposalUtils.findMinGuaranteedByOption(costDetails) : mostApplicable;
        }
        return ShipmentProposalUtils.findMaxGuaranteedByOption(costDetails);
    }

    private static Long findMaxGuaranteedByOption(List<CostDetailItemBO> costDetails) {
        long maxGuaranteed = -1;
        for (CostDetailItemBO costDetailItem : costDetails) {
            if (ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(costDetailItem.getRefType()) && costDetailItem.getGuaranteedBy() != null
                    && costDetailItem.getGuaranteedBy() > maxGuaranteed) {
                maxGuaranteed = costDetailItem.getGuaranteedBy();
            }
        }
        return maxGuaranteed;
    }

    private static Long findMinGuaranteedByOption(List<CostDetailItemBO> costDetails) {
        long minGuaranteed = Long.MAX_VALUE;
        for (CostDetailItemBO costDetailItem : costDetails) {
            if ((ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(costDetailItem.getRefType())) && costDetailItem.getGuaranteedBy() != null
                    && costDetailItem.getGuaranteedBy() < minGuaranteed) {
                minGuaranteed = costDetailItem.getGuaranteedBy();
            }
        }
        return minGuaranteed == Long.MAX_VALUE ? -1 : minGuaranteed;
    }
}
