package com.pls.restful.shipment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.shipment.ShipmentEventDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LocationDetailsReportBO;
import com.pls.shipment.domain.bo.LocationLoadDetailsReportBO;
import com.pls.shipment.domain.bo.ShipmentEventBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.enums.LoadEventType;
import com.pls.shipment.service.ShipmentEventService;
import com.pls.shipment.service.ShipmentService;

/**
 * Shipment action REST service.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/shipment")
public class ShipmentDetailsResource {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ShipmentEventService shipmentEventService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilder;

    /**
     * Method findLastShipmentsList is used to search Shipments according to given params, shipments are
     * sorted and count is limited. Defaults are: first - 1, count - 25.
     * 
     * @param customerId
     *            id of customer
     * @param count
     *            max results.
     * @return List<ShipmentDTO>
     */
    @RequestMapping(value = "/last", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> findLastShipments(@PathVariable("customerId") Long customerId,
            @QueryParam("count") int count) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());

        return shipmentService.findLastShipments(customerId, SecurityUtils.getCurrentPersonId(), count);
    }

    /**
     * Get tooltip data for shipments grids by shipment id.
     * 
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment id to find
     * @return shipment data object
     */
    @RequestMapping(value = "/{shipmentId}/tooltip", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentGridTooltipDTO getShipmentTooltipData(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") long shipmentId) {
        userPermissionsService.checkOrganization(customerId);

        LoadEntity loadEntity = shipmentService.getShipmentWithAllDependencies(shipmentId);
        ShipmentGridTooltipDTO dto = shipmentBuilder.getShipmentGridTooltipDTOBuilder().buildDTO(loadEntity);
        if (SecurityUtils.isPlsUser()) {
            shipmentBuilder.getShipmentGridTooltipDTOBuilder().fillPlsUserRelatedData(dto, loadEntity);
        }
        return dto;
    }

    /**
     * Returns shipment pricing details.
     * 
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment identifier
     * @return shipment dto
     * @throws EntityNotFoundException
     *             if shipment not found
     */
    @RequestMapping(value = "/{shipmentId}/pricingDetails", method = RequestMethod.GET)
    @ResponseBody
    public PricingDetailsBO getShipmentPricingDetails(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId) throws EntityNotFoundException {
        userPermissionsService.checkOrganization(customerId);
        LoadPricingDetailsEntity loadPricDtls = shipmentService.getShipmentPricingDetails(shipmentId);
        return shipmentBuilder.getPricingDetailItemDTOBuilder().buildDTO(loadPricDtls);
    }

    /**
     * Returns shipment events.
     *
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment identifier
     * @return list
     */
    @RequestMapping(value = "/{shipmentId}/events", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentEventDTO> findShipmentEvents(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId) {
        userPermissionsService.checkOrganization(customerId);
        List<ShipmentEventDTO> events = new ArrayList<ShipmentEventDTO>();
        
        List<ShipmentEventBO> loadGenericEvents = shipmentEventService.findShipmentEvents(shipmentId);
        if (!SecurityUtils.isPlsUser()) {
            // quote number tracking events should be not visible for non-pls users
            loadGenericEvents.removeIf(i -> i.getEventType() == LoadEventType.LD_QUOTENUM);
        }
        
        events.addAll(shipmentBuilder.getShipmentEventDTOBuilder()
                .buildList(loadGenericEvents));
        events.addAll(shipmentBuilder.getShipmentTrackingDTOBuilder()
                .buildList(shipmentEventService.findShipmentTracking(shipmentId)));
        Collections.sort(events);
        Collections.reverse(events);
        return events;
    }

    /**
     * Returns list of Shipment Audit data.
     * 
     * @param shipmentId
     *            shipment identifier
     * @param customerId
     *            id of customer
     * @return list of {@link LoadAuditBO}
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/{shipmentId}/audit", method = RequestMethod.GET)
    @ResponseBody
    public List<LoadAuditBO> findShipmentAudit(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId) {
        userPermissionsService.checkOrganization(customerId);
        return shipmentEventService.findShipmentAudit(shipmentId);
    }

    /**
     * Get details for Pickups And Deliveries customer report.
     * 
     * @return list of details
     */
    @RequestMapping(value = "/locationDetails", method = RequestMethod.GET)
    @ResponseBody
    public List<LocationDetailsReportBO> getLocationDetails() {
        return shipmentService.getLocationDetails(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get list of load details for Pickups And Deliveries reports.
     * 
     * @param zipDto
     *            zip details (can be <code>null</code>)
     * @param origin
     *            <code>true</code> if specified zip is for origin address
     * @param dateType
     *            date type (-1 means late, 0 - today, 1 - tomorrow)
     * @return list of load details
     */
    @RequestMapping(value = "/locationDetails", method = RequestMethod.PUT)
    @ResponseBody
    public List<LocationLoadDetailsReportBO> getLocationLoadDetails(@RequestBody(required = false) ZipDTO zipDto,
            @PathParam("origin") Boolean origin, @PathParam("dateType") Integer dateType) {
        if (zipDto == null) {
            return shipmentService.getLocationLoadDetails(null, null, null, dateType, SecurityUtils.getCurrentPersonId());
        } else {
            return shipmentService.getLocationLoadDetails(zipDto.getZip(), zipDto.getCity(), origin, dateType,
                    SecurityUtils.getCurrentPersonId());
        }
    }
}
