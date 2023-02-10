package com.pls.restful.rating;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.core.service.rating.RatingAccessorialTypeService;
import com.pls.core.shared.Status;
import com.pls.dto.ValueLabelDTO;

/**
 * Accessorial Type Resource.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/accessorial-types")
public class AccessorialTypeResource {
    @Autowired
    private RatingAccessorialTypeService service;

    /**
     * Get all {@link AccessorialTypeEntity}.
     * 
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> getAllAccessorialTypes() {
        return service.getAllAccessorialType();
    }

    /**
     * Get active {@link AccessorialTypeEntity}.
     * 
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> getActiveAccessorialType() {
        return service.getAllAccessorialTypeByStatus(Status.ACTIVE);
    }

    /**
     * Get inactive {@link AccessorialTypeEntity}.
     * 
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    @RequestMapping(value = "/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> getInactiveAccessorialType() {
        return service.getAllAccessorialTypeByStatus(Status.INACTIVE);
    }

    /**
     * Get {@link AccessorialTypeEntity} by specified <code>accessorialCode</code>.
     * 
     * @param code
     *            code of {@link AccessorialTypeEntity} which should be finded.
     * @return {@link AccessorialTypeEntity.
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public AccessorialTypeEntity getAccessorialByCode(@PathVariable("code") String code) {
        return service.getByCode(code);
    }

    /**
     * Save corresponded entity.
     * 
     * @param entity
     *            {@link AccessorialTypeEntity} entity to save.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveAccessorial(@RequestBody AccessorialTypeEntity entity) {
        service.saveAccessorialType(entity);
    }

    /**
     * Inactivate {@link AccessorialTypeEntity} by specified codes.
     * 
     * @param codes
     *            List of codes to make inactive
     * @return {@link List} of {@link AccessorialTypeEntity} with <code>Status.ACTIVE</code>.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<AccessorialTypeEntity> inactivateAccessorialTypes(@RequestBody List<String> codes) {
        service.updateStatus(codes, Status.INACTIVE);
        return service.getAllAccessorialType();
    }

    /**
     * Activate {@link AccessorialTypeEntity} by specified codes.
     * 
     * @param codes
     *            List of codes to make active
     * @return {@link List} of {@link AccessorialTypeEntity} with <code>Status.INACTIVE</code>.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseBody
    public List<AccessorialTypeEntity> activateAccessorialTypes(@RequestBody List<String> codes) {
        service.updateStatus(codes, Status.ACTIVE);
        return service.getAllAccessorialType();
    }

    /**
     * Check if {@link AccessorialTypeEntity} already exists by specified code.
     * 
     * @param code
     *            - code which should be verified.
     * @return <code>true</code> if code already exists, <code>true</code> in other case.
     */
    @RequestMapping(value = "/{code}/exists", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkAccessorialCodeExists(@PathVariable("code") String code) {
        return service.checkAccessorialCodeExists(code);
    }

    /**
     * Get Applicable To items.
     * 
     * @return {@link List} of {@link ValueLabelDTO}.
     */
    @RequestMapping(value = "/applicable-to-units", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getAccessorialTypesApplicableTo() {
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();
        for (ApplicableToUnit unit : ApplicableToUnit.values()) {
            result.add(new ValueLabelDTO(unit.toString(), unit.getDescription()));
        }

        return result;
    }

    /**
     *  Fetch all active accessorial types matching group criteria.
     * 
     * @param group - <em>PICKUP</em> or <em>DELIVERY</em> accessorial group
     * @return list of accessorial types matching group criteria
     */
    @RequestMapping(value = "/listByGroup", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> listAccessorialsByGroup(@RequestParam("group") String group) {
        return service.listAccessorialTypesByGroup(group);
    }

    /**
     *  Checks whether or not accessorial type id already used in shipment of quote.
     * 
     * @param code - accessorial type id to be looked for through shipments and saved quotes
     * @return - true if specified accessorial id found and false otherwise
     */
    @RequestMapping(value = "/isUnique", method = RequestMethod.GET)
    @ResponseBody
    public boolean isAccessorialTypeIdUnique(@RequestParam("accessorialTypeCode") String code) {
        return service.isAccessorialTypeUnique(code);
    }
}
