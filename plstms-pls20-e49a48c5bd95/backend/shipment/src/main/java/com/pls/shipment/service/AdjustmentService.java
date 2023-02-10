package com.pls.shipment.service;

import java.util.List;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.bo.AdjustmentLoadInfoBO;

/**
 * Service for working with Adjustments.
 * 
 * @author Aleksandr Leshchenko
 */
public interface AdjustmentService {
    /**
     * Method saves adjustments for invoiced sales order.
     * 
     * @param loadId
     *            shipment identifier
     * @param adjustmentsToSave
     *            list of adjustments to save. Can be empty!
     * @param adjustmentsToRemove
     *            list of adjustments to remove. Can be empty!
     * @param loadInfo
     *            load information that needs to be saved. Can be empty! If not empty then load information
     *            should be updated.
     * @param materials
     *            load materials that need to be saved. Can be empty! If not empty then load information
     *            should be updated.
     * @throws ApplicationException
     *             can occur in one of the following cases:
     *             <ul>
     *             <li>load is not invoiced</li>
     *             <li>user tries to update already invoiced adjustment</li>
     *             <li>some adjustment is missing in both adjustmentsToSave and adjustmentsToRemove lists, but
     *             present in load</li>
     *             </ul>
     */
    void saveAdjustments(Long loadId, List<AdjustmentBO> adjustmentsToSave, List<AdjustmentBO> adjustmentsToRemove,
            AdjustmentLoadInfoBO loadInfo, List<LoadMaterialEntity> materials) throws ApplicationException;
}
