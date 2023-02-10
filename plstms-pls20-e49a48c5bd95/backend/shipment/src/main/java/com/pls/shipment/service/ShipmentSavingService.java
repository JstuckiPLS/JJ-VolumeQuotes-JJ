package com.pls.shipment.service;

import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Shipment Service for creating / updating {@link LoadEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface ShipmentSavingService {
    /**
     * Updates shipment with final data when 'Book It' action performed.
     * 
     * @param loadEntity
     *            LTL shipment entity to be updated
     * @param autoDispatch
     *            if true then load should skip Booked phase
     * @param customerUserId
     *            id of customer user who created load or who was selected by PLS User as responsible for
     * @param proposal
     *            to save cost details
     * @param currentUserId
     *            id of current user
     * @return single LoadEntity.
     * @throws ApplicationException application exception
     * @throws InternalJmsCommunicationException if EDI processing fails
     * @throws EdiProcessingException thrown when EDI processing fails
     */
    LoadEntity bookShipment(LoadEntity loadEntity, boolean autoDispatch, Long customerUserId, ShipmentProposalBO proposal,
            Long currentUserId)
                    throws ApplicationException, InternalJmsCommunicationException, EdiProcessingException;

    /**
     * Save or update shipment.
     * 
     * @param loadEntity
     *            LTL shipment entity to be updated
     * @param proposal
     *            to save cost details
     * @param customerUserId
     *            id of customer user who created load or who was selected by PLS User as responsible for
     * @param currentUserId
     *            id of current user
     * @return single {@link LoadEntity}.
     * @throws ApplicationException application exception
     * @throws EdiProcessingException thrown when EDI processing fails
     * @throws InternalJmsCommunicationException if load tender message can not be published to external integration message queue
     */
    LoadEntity save(LoadEntity loadEntity, ShipmentProposalBO proposal, Long customerUserId, Long currentUserId)
            throws ApplicationException, InternalJmsCommunicationException, EdiProcessingException;

    /**
     * Update load with cost details.
     * 
     * @param entity
     *            load to update
     * @param proposal
     *            dto with data about selected proposal
     * @param currentUserId
     *            id of current user
     */
    void updateLoadWithCostDetails(LoadEntity entity, ShipmentProposalBO proposal, Long currentUserId);

    /**
     * Method fills load entity with accessorials built from cost detail items.<br>
     * This needs to be done only for saving load from Sales Order, because we don't have accessorials there.
     * 
     * @param entity
     *            - load entity {@link LoadEntity}
     * @param dto
     *            - selected proposition dto {@link ShipmentProposalBO}
     */
    void fillAccessorialsFromCostDetailItems(LoadEntity entity, ShipmentProposalBO dto);

    /**
     * Get new active cost detail.
     * 
     * @param proposal
     *            - {@link ShipmentProposalBO}
     * @param load
     *            - {@link LoadEntity}
     * @return {@link LoadCostDetailsEntity}.
     */
    LoadCostDetailsEntity getNewActiveCostDetail(ShipmentProposalBO proposal, LoadEntity load);

    /** 
     * Update carrier quote number on load (based on provided proposal).
     */
    void updateLoadWithQuoteNumber(LoadEntity load, ShipmentProposalBO proposal);

    /**
     * Saves a load in the database, and updates its documents.
     * @param loadEntity the load to save
     * @return the same load after saving
     * @throws ApplicationException
     */
    LoadEntity save(LoadEntity loadEntity) throws ApplicationException;
}
