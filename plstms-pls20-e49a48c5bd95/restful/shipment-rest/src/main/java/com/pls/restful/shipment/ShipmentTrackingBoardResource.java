package com.pls.restful.shipment;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.ValueLabelDTO;
import com.pls.dto.enums.DateRange;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.dtobuilder.util.RegularSearchQueryBOBuilder;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;
import com.pls.shipment.service.ManualBolService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.ShipmentTrackingBoardService;

/**
 * Shipment tracking board resource.
 * 
 * @author Viacheslav Krot
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/shipment/tracking/board")
public class ShipmentTrackingBoardResource {

    private static final String DON_T_HAVE_PERMISSIONS_MSG = "You don't have permissions to fulfill this request.";

    @Autowired
    private ShipmentTrackingBoardService service;

    @Autowired
    private ManualBolService manualBolService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ContactInfoService contactInfoService;

    /**
     * See {@link ShipmentTrackingBoardService#acknowledgeAlerts(long)}.
     * 
     * @param shipmentId
     *            id of shipment
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/alert/acknowledge/{shipmentId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void acknowledeAlerts(@PathVariable("shipmentId") long shipmentId) {
        service.acknowledgeAlerts(shipmentId);
    }

    /**
     * See {@link ShipmentTrackingBoardService#countOfActiveAlerts()}.
     * 
     * @return {@link ValueLabelDTO} with filled value property
     */
    @RequestMapping(value = "/alert/count", method = RequestMethod.GET)
    @ResponseBody
    public ValueLabelDTO getActiveAlertsCount() {
        return new ValueLabelDTO(service.countOfActiveAlerts(), null);
    }

    /**
     * Get shipments with alerts.
     * 
     * @return list of shipments with alerts.
     * @throws AccessDeniedException
     *             don't have permissions.
     */
    @RequestMapping(value = "/alert", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentTrackingBoardAlertListItemBO> getAlertShipments() throws AccessDeniedException {
        if (userPermissionsService.hasCapability(Capabilities.BOARD_ALERT_PAGE_VIEW.name())) {
            return service.getAlertShipments();
        } else {
            throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
        }
    }

    /**
     * Get shipments in BOOKED status.
     * 
     * @return list of shipments in BOOKED status.
     * @throws AccessDeniedException
     *             don't have permissions.
     */
    @RequestMapping(value = "/booked", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentTrackingBoardBookedListItemBO> getBookedShipments() throws AccessDeniedException {
        if (userPermissionsService.hasCapability(Capabilities.BOARD_BOOKED_PAGE_VIEW.name())) {
            return service.getBookedShipments(SecurityUtils.getCurrentPersonId());
        } else {
            throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
        }
    }

    /**
     * Get shipments in OPEN status.
     * 
     * @param toDate
     *            to date
     * @param fromDate
     *            from date
     * 
     * @return list of shipments in OPEN status.
     * @throws ApplicationException
     *             if the wrong Bol Number of inappropriate wildcard pattern was entered.
     * @throws AccessDeniedException
     *             don't have permissions.
     */
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentTrackingBoardListItemBO> getOpenShipments(@QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) throws ApplicationException, AccessDeniedException {
        if (userPermissionsService.hasCapability(Capabilities.BOARD_OPEN_PAGE_VIEW.name())) {
            RegularSearchQueryBO search = new RegularSearchQueryBO();
            search.setFromDate(DateUtils.getFromDate(DateRange.DEFAULT, fromDate));
            search.setToDate(DateUtils.getFromDate(DateRange.DEFAULT, toDate));
            return service.getOpenShipments(SecurityUtils.getCurrentPersonId(), search);
        } else {
            throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
        }
    }

    /**
     * Get undelivered shipments.
     * 
     * @return undelivered shipments.
     * @throws AccessDeniedException
     *             don't have permissions.
     */
    @RequestMapping(value = "/undelivered", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentTrackingBoardListItemBO> getUndeliveredShipments() throws AccessDeniedException {
        List<ShipmentTrackingBoardListItemBO> result = service.getUndeliveredShipments();
        if (!userPermissionsService.hasCapability(Capabilities.VIEW_ACTIVE_SHIPMENTS_COST_DETAILS.name())) {
            if (userPermissionsService.hasCapability(Capabilities.VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY.name())) {
                for (ShipmentTrackingBoardListItemBO shipmentTrackingBoardListItemBO : result) {
                    shipmentTrackingBoardListItemBO.setCost(null);
                    shipmentTrackingBoardListItemBO.setMargin(null);
                }
            } else if (userPermissionsService.hasCapability(Capabilities.VIEW_ACTIVE_SHIPMENTS.name())) {
                for (ShipmentTrackingBoardListItemBO shipmentTrackingBoardListItemBO : result) {
                    shipmentTrackingBoardListItemBO.setCost(null);
                    shipmentTrackingBoardListItemBO.setMargin(null);
                    shipmentTrackingBoardListItemBO.setRevenue(null);
                }
            } else {
                throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
            }
        }
        return result;
    }

    /**
     * Get unbilled shipments.
     * 
     * @return unbilled shipments.
     * @throws AccessDeniedException
     *             don't have permissions.
     */
    @RequestMapping(value = "/unbilled", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> getUnbilledShipments() throws AccessDeniedException {
        if (userPermissionsService.hasCapability(Capabilities.BOARD_DELIVERED_PAGE_VIEW.name())) {
            return service.getUnbilledShipments(SecurityUtils.getCurrentPersonId());
        } else {
            throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
        }
    }

    /**
     * Dispatch shipment.
     *
     * @param shipmentId
     *            id of shipment to dispatch
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist
     * @throws InternalJmsCommunicationException
     *             if load tender message can not be published to external integration message queue
     * @throws EdiProcessingException
     *             throws exception when the EDI cannot be processed.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/dispatch/{shipmentId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void dispatchShipment(@PathVariable("shipmentId") Long shipmentId) throws ApplicationException {
        shipmentService.dispatchShipment(shipmentId);
    }

    /**
     * Get all shipments.
     * 
     * @param request
     *            standard http servlet request
     * @return all shipments.
     * @throws AccessDeniedException
     *             don't have permissions.
     * @throws ApplicationException
     *             if WildCard search parameter has wrong format
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> getAllShipments(HttpServletRequest request) throws ApplicationException,
            AccessDeniedException {
        RegularSearchQueryBO data = new RegularSearchQueryBOBuilder(request).build();

        List<ShipmentListItemBO> result = service.getAllShipments(data, SecurityUtils.getCurrentPersonId());
        if (!userPermissionsService.hasCapability(Capabilities.VIEW_ALL_SHIPMENTS_COST_DETAILS.name())) {
            if (userPermissionsService.hasCapability(Capabilities.VIEW_ALL_SHIPMENTS_REVENUE_ONLY.name())) {
                for (ShipmentListItemBO shipmentListItemBO : result) {
                    shipmentListItemBO.setBillingStatus(null);
                    shipmentListItemBO.setTotal(null);
                    shipmentListItemBO.setMargin(null);
                }
            } else if (userPermissionsService.hasCapability(Capabilities.VIEW_ALL_SHIPMENTS.name())) {
                for (ShipmentListItemBO shipmentListItemBO : result) {
                    shipmentListItemBO.setBillingStatus(null);
                    shipmentListItemBO.setTotal(null);
                    shipmentListItemBO.setMargin(null);
                    shipmentListItemBO.setRevenue(null);
                }
            } else {
                throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
            }
        }
        addManualBol(result, data, request);
        return result;
    }

    private void addManualBol(List<ShipmentListItemBO> result, RegularSearchQueryBO data, HttpServletRequest request) {
        if (userPermissionsService.hasCapability(Capabilities.VIEW_MANUAL_BOL.name())
                && Boolean.valueOf(request.getParameter("withManualBol"))) {
            result.addAll(manualBolService.findAllShipment(data, SecurityUtils.getCurrentPersonId()));
        }
    }

    /**
     * REST service to update shipment financial status manually if we don't need wait for 7 days.
     * 
     * @param shipmentId
     *            id of Shipment
     * 
     * @throws ApplicationException
     *             if unable to override date hold
     */
    @RequestMapping(value = "/override/{shipmentId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void overrideDateHold(@PathVariable("shipmentId") Long shipmentId) throws ApplicationException {
        userPermissionsService.checkCapability(Capabilities.OVERRIDE_DATE_HOLD.name());
        shipmentService.overrideDateHold(shipmentId);
    }

    /**
     * Fetches shortened contact info.
     * 
     * @param shipmentId
     *            - load id to be used to retrieve contact information.
     * @return contact name, phone and email address.
     */
    @RequestMapping(value = "/contact-info", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public UserAdditionalContactInfoBO getContactInformation(@RequestParam("shipmentId") Long shipmentId) {
        LoadEntity load = shipmentService.findById(shipmentId);
        if (load.getLocation() != null && load.getLocation().getActiveAccountExecutive() != null) {
            return contactInfoService.getContactInfo(load.getLocation().getActiveAccountExecutive().getUser());
        } else {
            return contactInfoService.getContactInfo(load.getModification().getCreatedUser());
        }
    }

    /**
     * Get all manual bol.
     * 
     * @param request
     *            standard http servlet request
     * @return all manual BOL.
     * @throws ApplicationException
     *             if the wrong Bol Number of inappropriate wildcard pattern was entered.
     * @throws AccessDeniedException
     *             don't have permissions.
     * 
     */
    @RequestMapping(value = "/allManualBol", method = RequestMethod.GET)
    @ResponseBody
    public List<ManualBolListItemBO> getAllManualBol(HttpServletRequest request) throws ApplicationException,
            AccessDeniedException {
        if (userPermissionsService.hasCapability(Capabilities.VIEW_MANUAL_BOL.name())) {
            return manualBolService.findAll(new RegularSearchQueryBOBuilder(request).build(),
                    SecurityUtils.getCurrentPersonId());
        } else {
            throw new AccessDeniedException(DON_T_HAVE_PERMISSIONS_MSG);
        }
    }

    /**
     * Returns List of load ids matched by specified pro number and carrier org id.
     * 
     * @param proNum
     *            - Pro Number.
     * @param orgId
     *            - Carrier org id.
     * @return List of load ids.
     */
    @RequestMapping(value = "/matchedLoads", method = RequestMethod.GET)
    @ResponseBody
    public List<BigDecimal> getMatchedLoadsByProAndOrgId(@RequestParam("proNum") String proNum,
            @RequestParam("orgId") Long orgId) {
        return shipmentService.getMatchedLoadsByProAndOrgId(proNum, orgId);
    }

    /**
     * Get hold shipments.
     * 
     * @return hold shipments.
     */
    @RequestMapping(value = "/hold", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> getHoldShipments() {
        return service.getHoldShipments();
    }
}
