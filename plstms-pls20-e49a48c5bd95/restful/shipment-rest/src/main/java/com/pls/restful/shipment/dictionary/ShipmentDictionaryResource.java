package com.pls.restful.shipment.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.CustomerService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.shared.BillToRequiredField;
import com.pls.dto.PackageTypeDTO;
import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.dictionary.FinancialReasonDTOBuilder;
import com.pls.dtobuilder.product.PackageTypeDTOBuilder;
import com.pls.invoice.service.InvoiceAuditService;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.service.dictionary.FinancialReasonsDictionaryService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Shipment Dictionary related REST service.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/shipment/dictionary")
public class ShipmentDictionaryResource {
    private static final FinancialReasonDTOBuilder FINANCIAL_REASON_DTO_BUILDER = new FinancialReasonDTOBuilder();
    private static final PackageTypeDTOBuilder PACKAGE_TYPE_DTO_BUILDER = new PackageTypeDTOBuilder();
    private static final ImmutableMap<String, Capabilities> NETWORK_PERMISSION_MAP = ImmutableMap.of("AG",
            Capabilities.ADD_CUSTOMER_FOR_AGENT_BUSINESS_UNIT, "CS", Capabilities.ADD_CUSTOMER_FOR_CARRIER_SALES_BUSINESS_UNIT, "LT",
            Capabilities.ADD_CUSTOMER_FOR_LTL_BUSINESS_UNIT, "N2", Capabilities.ADD_CUSTOMER_FOR_PLS_BUSINESS_UNIT, "FS",
            Capabilities.ADD_CUSTOMER_FOR_PLS_FREIGHT_SOLUTIONS_BUSINESS_UNIT);

    @Autowired
    private FinancialReasonsDictionaryService financialReasonsDictionaryService;

    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;

    @Autowired
    private InvoiceAuditService auditService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserPermissionsService userPermissionService;

    /**
     * Get list of all applicable reasons for adjustments.
     * 
     * @return list of all applicable reasons for adjustments.
     */
    @RequestMapping(value = "/financialReasons", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getFinancialReasonsForAdjustments() {
        return FINANCIAL_REASON_DTO_BUILDER.buildList(financialReasonsDictionaryService.getFinancialReasonsForAdjustments());
    }

    /**
     * Get list of all package types.
     *
     * @return list of {@link PackageTypeDTO}.
     */
    @RequestMapping(value = "/packageType", method = RequestMethod.GET)
    @ResponseBody
    public List<PackageTypeDTO> getAllPackageTypes() {
        return PACKAGE_TYPE_DTO_BUILDER.buildList(packageTypeDictionaryService.getAllPackageTypes());
    }

    /**
     * Get list BillingAuditReasonCode.
     * 
     * @return all reason's code which are reasonType MANUAL.
     */
    @RequestMapping(value = "/auditReasonCode", method = RequestMethod.GET)
    @ResponseBody
    public List<LdBillAuditReasonCodeEntity> getBillingAuditReasonCode() {
        return auditService.getListAuditReasonCodeForReasonType();
    }

    /**
     * Get list Networks.
     * 
     * @return all Networks.
     */
    @RequestMapping(value = "/getAllNetworks", method = RequestMethod.GET)
    @ResponseBody
    public List<NetworkListItemBO> getNetworks() {
        List<NetworkListItemBO> networks = customerService.getAllNetworks();
        List<NetworkListItemBO> filteredNetworks = new ArrayList<>();
        for (NetworkListItemBO network : networks) {
            if (NETWORK_PERMISSION_MAP.containsKey(network.getUnitCode())
                    && !userPermissionService.hasCapability(NETWORK_PERMISSION_MAP.get(network.getUnitCode()).name())) {
                continue;
            }
            filteredNetworks.add(network);
        }
        return filteredNetworks;
    }

    /**
     * Method returns the list of all billTo required fields.
     * 
     * @return the list of all {@link ValueLabelDTO}.
     */
    @RequestMapping(value = "/billToReqField", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getBillToRequiredField() {
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();
        for (BillToRequiredField billToRequiredField : Arrays.asList(BillToRequiredField.values())) {
            result.add(new ValueLabelDTO(billToRequiredField.getCode(), billToRequiredField.getDescription()));
        }
        return result;
    }
}
