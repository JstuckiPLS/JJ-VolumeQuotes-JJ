package com.pls.restful;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.bo.GLCodeBO;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.organization.CompanyCodeEntity;
import com.pls.core.service.AccessorialDictionaryService;
import com.pls.core.service.DictionaryTypesService;
import com.pls.core.service.LookupService;
import com.pls.core.service.OrganizationService;
import com.pls.dto.CompanyCodeDTO;
import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CompanyCodesDTOBuilder;
import com.pls.dtobuilder.GLCodesDTOBuilder;

/**
 * Rest service providing static dictionaries. Intended to provide all possible static constants and
 * collections like: dimension units, weight units, classes and so on.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/dictionary")
public class DictionaryResource {

    private static final AbstractDTOBuilder<CompanyCodeEntity, CompanyCodeDTO> COMPANY_CODES_DTO_BUILDER = new CompanyCodesDTOBuilder();

    private static final GLCodesDTOBuilder GLCODE_DTO_BUILDER = new GLCodesDTOBuilder();

    @Autowired
    private AccessorialDictionaryService accessorialDictService;

    @Autowired
    private DictionaryTypesService dictionaryService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LookupService lookupService;

    @Value("${plsPay.url}")
    private String plsPayURL;

    /**
     * Get commodity values.
     * 
     * @return list of CommodityClass
     */
    @RequestMapping(value = "/allAccessorialTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialTypeEntity> getAllAccessorialTypes() {
        return accessorialDictService.getAllAccessorialTypes();
    }

    /**
     * Method returns the list of all notification types.
     * 
     * @return the list of all {@link ValueLabelDTO}.
     */
    @RequestMapping(value = "/notificationTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getNotificationTypes() {
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();
        for (NotificationTypeEntity entity : dictionaryService.getNotificationTypes()) {
            result.add(new ValueLabelDTO(entity.getId(), entity.getDescription()));
        }
        return result;
    }

    /**
     * Get all carrier API details.
     * 
     * @return all carrier API details
     */
    @RequestMapping(value = "/companyCodes", method = RequestMethod.GET)
    @ResponseBody
    public List<CompanyCodeDTO> getCompanyCodes() {
        return COMPANY_CODES_DTO_BUILDER.buildList(organizationService.getCompanyCodes());
    }

    /**
     * Get customer payment terms.
     * 
     * @return customer payment terms
     */
    @RequestMapping(value = "/customerPayTerms", method = RequestMethod.GET)
    @ResponseBody
    public List<KeyValueBO> getCustomerPayTerms() {
        return dictionaryService.getCustomerPayTerms();
    }

    /**
     * Get GL number components.
     * 
     * @return list of {@link GLCodeBO}
     */
    @RequestMapping(value = "/glNumComponents", method = RequestMethod.GET)
    @ResponseBody
    public List<GLCodeBO> getGLNumberComponents() {
        return GLCODE_DTO_BUILDER.buildListWithoutValueField(lookupService.getGLNumberComponents());
    }

    /**
     * Get lookup values for Brand Industrial Services customer.
     * 
     * @return list of {@link GLCodeBO}
     */
    @RequestMapping(value = "/brandNumComponents", method = RequestMethod.GET)
    @ResponseBody
    public List<GLCodeBO> getBrandNumberComponents() {
        return GLCODE_DTO_BUILDER.buildList(lookupService.getBrandNumberComponents());
    }

    /**
     * Get lookup values for Aluma Systems customer.
     * 
     * @return list of {@link GLCodeBO}
     */
    @RequestMapping(value = "/alumaNumComponents", method = RequestMethod.GET)
    @ResponseBody
    public List<GLCodeBO> getAlumaNumberComponents() {
        return GLCODE_DTO_BUILDER.buildList(lookupService.getAlumaNumberComponents());
    }

    /**
     * Get customer payment method.
     * 
     * @return customer payment method
     */
    @RequestMapping(value = "/customerPayMethod", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getCustomerPayMethod() {
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();
        for (LookupValueEntity entity : lookupService.getLookupValuesForPayMethod()) {
            result.add(new ValueLabelDTO(entity.getLookupValue(), entity.getDescription()));
        }
        return result;
    }

    /**
     * Get PLS Pay URL.
     * 
     * @return url- PLS pay URL
     */
    @RequestMapping(value = "/plsPayURL", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getPLSPayURL() {
        return plsPayURL;
    }

    /**
     * Get GL number components.
     * 
     * @return list of {@link GLCodeBO}
     */
    @RequestMapping(value = "/getGLNumberForFreightCharge", method = RequestMethod.GET)
    @ResponseBody
    public List<GLCodeBO> getGLNumberForFreightCharges() {
        return GLCODE_DTO_BUILDER.buildList(lookupService.getGLValuesForFreightCharge());
    }
}
