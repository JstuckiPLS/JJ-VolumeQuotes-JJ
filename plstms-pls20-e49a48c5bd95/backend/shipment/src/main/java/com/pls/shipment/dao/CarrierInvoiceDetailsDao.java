package com.pls.shipment.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;

/**
 * DAO for {@link CarrierInvoiceDetailsEntity}.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
public interface CarrierInvoiceDetailsDao extends AbstractDao<CarrierInvoiceDetailsEntity, Long> {

    /**
     * Retrieves list of all unmatched Carrier Invoice Details entities.
     *
     * @return list of {@link CarrierInvoiceDetailsListItemBO}
     */
    List<CarrierInvoiceDetailsListItemBO> getUnmatched();

    /**
     * Retrieves list of all archived Carrier Invoice Details entities.
     *
     * @return list of {@link CarrierInvoiceDetailsListItemBO}
     */
    List<CarrierInvoiceDetailsListItemBO> getArchived();

    /**
     * Updates status of {@link CarrierInvoiceDetailsEntity}.
     *
     * @param carrierInvoiceId Carrier Invoice details ID
     * @param status           new {@link Status}
     * @param modifiedDate     modified date
     * @param modifiedBy       modified by
     */
    void updateStatus(Long carrierInvoiceId, Status status, Date modifiedDate, Long modifiedBy);

    /**
     * Updates status of {@link CarrierInvoiceDetailsEntity}.
     *
     * @param carrierInvoiceIds Carrier Invoice details ID list
     * @param status           new {@link Status}
     * @param modifiedDate     modified date
     * @param modifiedBy       modified by
     */
    void updateStatuses(List<Long> carrierInvoiceIds, Status status, Date modifiedDate, Long modifiedBy);

    /**
     * Turns unmatched Carrier Invoice Details entities into archived if they older than <days> days.
     */
    void archiveOldUnmatched();

    /**
     * Retrieves list of Vendor Bills was created via EDI for current load.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * 
     * @return list of {@link CarrierInvoiceDetailsEntity}
     */
    List<CarrierInvoiceDetailsEntity> getEDIVendorBillsForLoad(Long loadId);

    /**
     * Updates status of {@link CarrierInvoiceDetailsEntity}.
     * 
     * @param carrierInvoiceId
     *            Carrier Invoice details ID
     * @param status
     *            new {@link Status}
     * @param matchedLoadId
     *            matchedLoadId
     */
    void updateStatusAndMatchedLoad(Long carrierInvoiceId, Status status, Long matchedLoadId);
}
