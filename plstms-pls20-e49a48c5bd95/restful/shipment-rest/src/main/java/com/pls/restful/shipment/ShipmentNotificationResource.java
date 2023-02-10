package com.pls.restful.shipment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.shipment.ShipmentNotificationSourceItemDTO;
import com.pls.shipment.service.ShipmentNotificationSourceService;

/**
 * Shipment notification REST service.
 * 
 * @author Brichak Aleksandr
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/sourceNotifications")
public class ShipmentNotificationResource {

    @Autowired
    private ShipmentBuilderHelper shipmentBuilderUtils;

    @Autowired
    private ShipmentNotificationSourceService shipmentNotificationSourceService;

    /**
     * Method returns the list of sources for shipment notifications.
     * 
     * @param customerId
     *            - id of customer
     * @return {@link List} of {@link ShipmentNotificationSourceItemDTO}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentNotificationSourceItemDTO> getShipmentNotificationsSourceItems(@PathVariable("customerId") Long customerId) {

        return shipmentBuilderUtils.getShipmentNotificationSourceItemDTOBuilder()
                .buildList(shipmentNotificationSourceService
                .getShipmentNotificationSourceItems(customerId, SecurityUtils.getCurrentPersonId()));
    }
}
