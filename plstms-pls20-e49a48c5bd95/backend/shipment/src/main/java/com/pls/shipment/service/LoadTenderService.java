package com.pls.shipment.service;

import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.shipment.domain.LoadEntity;

/**
 * EDI 204 Motor Carrier Load Tender interface.
 *
 * @author Mikhail Boldinov, 22/01/14
 */
public interface LoadTenderService {

    /**
     * Tenders an offer for a shipment to a carrier. <br/>
     * The method <b>MUST</b> be invoked in the following cases:
     * <ul>
     * <li><b>Shipment Tender</b> - new shipment is dispatched</li>
     * <li><b>Shipment Cancellation</b> - dispatched shipment is cancelled</li>
     * <li><b>Shipment Update</b> - dispatched shipment is updated</li>
     * </ul>
     *
     * @param load
     *            {@link LoadEntity} to process
     * @param prevCarrier
     *            id of carrier to which the load was tendered before
     * @param oldStatus
     *            old shipment status
     * @param currentStatus
     *            current shipment status
     * @throws InternalJmsCommunicationException
     *             if load tender message can not be published to external integration message queue.
     * @throws EdiProcessingException
     *             if there is any exception sending EDI to carrier.
     */
    void tenderLoad(LoadEntity load, CarrierEntity prevCarrier, ShipmentStatus oldStatus, ShipmentStatus currentStatus)
            throws InternalJmsCommunicationException, EdiProcessingException;
}
