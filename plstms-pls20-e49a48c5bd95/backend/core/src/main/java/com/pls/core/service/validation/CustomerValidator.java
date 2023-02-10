package com.pls.core.service.validation;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.service.validation.support.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator for customer entities.
 *
 * @author Viacheslav Krot
 */
@Component
public class CustomerValidator extends AbstractValidator<CustomerEntity> {

    @Autowired
    private CustomerDao dao;

    public void setDao(CustomerDao dao) {
        this.dao = dao;
    }

    @Autowired
    private AddressValidator addressValidator;

    @Autowired
    private OrganizationPhoneValidator organizationPhoneValidator;

    @Override
    protected void validateImpl(CustomerEntity customer) {
        asserts.notEmpty(customer.getName(), "name");

        if (isNew(customer)) {
            if (dao.checkCustomerNameExists(customer.getName())) {
                asserts.fail("name", ValidationError.UNIQUE);
            }
        } else {
            if (dao.checkCustomerNameExists(customer.getId(), customer.getNetworkId(), customer.getName())) {
                asserts.fail("name", ValidationError.UNIQUE);
            }
        }
        asserts.notNull(customer.getAddress(), "address");
        validateComponent(addressValidator, customer.getAddress(), "address");
        asserts.notEmpty(customer.getContactFirstName(), "contactFirstName");
        asserts.notEmpty(customer.getContactLastName(), "contactLastName");
        asserts.notNull(customer.getPhone(), "phone");
        validateComponent(organizationPhoneValidator, customer.getPhone(), "phone");
        if (customer.getFax() != null) {
            validateComponent(organizationPhoneValidator, customer.getFax(), "fax");
        }
        asserts.notEmpty(customer.getContactEmail(), "contactEmail");
        if (customer.getStatus() != null && customer.getStatus() != OrganizationStatus.ACTIVE) {
            asserts.notEmpty(customer.getStatusReason(), "statusReason");
        }

        asserts.notNull(customer.getModification(), "modification");
    }

    private boolean isNew(CustomerEntity customer) {
        return customer.getId() == null;
    }
}
