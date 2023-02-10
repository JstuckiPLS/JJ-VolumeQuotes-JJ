package com.pls.restful.invoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.address.AddressService;
import com.pls.dto.invoice.CustomerCreditBillingDTO;
import com.pls.dtobuilder.invoice.CustomerCreditBillingDTOBuilder;
import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;
import com.pls.invoice.service.CustomerInvoiceService;
import com.pls.restful.TransactionalReadOnly;

/**
 * Resource provides invoice functionality to customers.
 *
 * @author Alexander Kirichenko
 */
@Controller
@TransactionalReadOnly
@RequestMapping("/customer/{customerId}/user/{userId}/invoice")
public class CustomerInvoiceResource {

    @Autowired
    private CustomerInvoiceService service;

    @Autowired
    private AddressService addressService;

    /**
     * Method finds and returns {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions.
     * @param customerId - id of customer
     * @param userId - id of user
     * @param queryParams - filtered parameters {@link CustomerInvoiceCO}
     * @return - {@link List} of {@link CustomerInvoiceBO} for specif customer and specified conditions
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public List<CustomerInvoiceBO> findCustomerInvoices(@PathVariable("customerId") Long customerId, @PathVariable("userId") Long userId,
                                                        @RequestBody CustomerInvoiceCO queryParams) {
        return service.findCustomerInvoices(customerId, queryParams);
    }

    /**
     * Method returns {@link CustomerInvoiceSummaryBO}.
     * @param customerId - id of customer
     * @param userId - id of user
     * @return {@link CustomerInvoiceSummaryBO}
     */
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public CustomerInvoiceSummaryBO getCustomerInvoiceSummary(@PathVariable("customerId") Long customerId, @PathVariable("userId") Long userId) {
        return service.getCustomerInvoiceSummary(customerId);
    }

    /**
     * Returns {@link CustomerCreditBillingDTO} with populate customer's credit and billing information.
     *
     * @param customerId id of customer
     * @param userId   id of logged in user
     * @return {@link CustomerCreditBillingDTO}
     */
    @RequestMapping(value = "/credit-billing", method = RequestMethod.GET)
    @ResponseBody
    public CustomerCreditBillingDTO getCreditAndBillingInfo(@PathVariable("customerId") final Long customerId,
                                                            @PathVariable("userId") final Long userId) {
        CustomerCreditInfoBO customerCreditInfo = service.getCustomerCreditInfo(customerId);
        CustomerCreditBillingDTOBuilder dtoBuilder = new CustomerCreditBillingDTOBuilder(new CustomerCreditBillingDTOBuilder.DataProvider() {
            @Override
            public List<BillToEntity> getBillToList() {
                return addressService.getOrgBillToAddresses(customerId, null);
            }
        });
        return dtoBuilder.buildDTO(customerCreditInfo);
    }
}
