package com.pls.dtobuilder.organization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationFaxPhoneEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.organization.OrganizationVoicePhoneEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.dto.enums.CustomerStatusReason;
import com.pls.dto.organization.CustomerDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;
import com.pls.dtobuilder.address.BillToDTOBuilder;

/**
 * Builder for conversion customer entity to customer profile DTO.
 *
 * @author Denis Zhupinsky (TEAM International)
 */
public class CustomerDTOBuilder extends AbstractDTOBuilder<CustomerEntity, CustomerDTO> {
    private static final BillToDTOBuilder BILL_TO_DTO_BUILDER = new BillToDTOBuilder();
    private static final AddressBookEntryDTOBuilder ADDRESS_DTO_BUILDER = new AddressBookEntryDTOBuilder();

    private CustomerDataProvider customerDataProvider;

    /**
     * Constructor.
     *
     * @param customerDataProvider data provider for customer builder.
     */
    public CustomerDTOBuilder(CustomerDataProvider customerDataProvider) {
        this.customerDataProvider = customerDataProvider;
    }

    @Override
    public CustomerEntity buildEntity(CustomerDTO dto) {
        CustomerEntity customer = getNewOrExistingCustomer(dto);
        customer.setName(dto.getName());
        customer.setStatus(OrganizationStatus.valueOf(dto.getStatus().name()));
        customer.setFederalTaxId(dto.getFederalTaxId());
        customer.setEdiAccount(dto.getEdiAccount());
        customer.setAddress(ADDRESS_DTO_BUILDER.buildEntity(dto.getAddress()));
        customer.setContactFirstName(dto.getContactFirstName());
        customer.setContactLastName(dto.getContactLastName());
        customer.setContactEmail(dto.getContactEmail());
        customer.setContract(dto.isContract());
        customer.setEffectiveDate(dto.getStartDate());
        customer.setExpirationDate(dto.getEndDate());
        customer.setStatusReason(dto.getStatusReason() != null ? dto.getStatusReason().getValue() : null);
        customer.setCreateOrdersFromVendorBills(dto.getCreateOrdersFromVendorBills());
        customer.setGenerateConsigneeInvoice(dto.isGenerateConsigneeInvoice());
        customer.setDisplayLogoOnBol(dto.getDisplayLogoOnBol());
        customer.setDisplayLogoOnShipLabel(dto.getDisplayLogoOnShipLabel());
        customer.setInternalNote(dto.getInternalNote());
        customer.setPrintBarcode(dto.isPrintBarcode());
        setEntityPhone(dto.getContactPhone(), customer);
        setEntityFax(dto.getContactFax(), customer);
        setEntityBillTo(dto, customer);
        setLocation(dto.getLocationId(), customer);
        return customer;
    }

    private void setLocation(Long locationId, CustomerEntity customer) {
        Iterator<OrganizationLocationEntity> iterator = customer.getLocations().iterator();
        if (customer.getId() == null) {
            iterator.next().setDefaultNode(true);
            return;
        }
        long defaultLocation = locationId == null ? Long.MIN_VALUE : locationId;
        while (iterator.hasNext()) {
            OrganizationLocationEntity location = iterator.next();
            location.setDefaultNode(location.getId().longValue() == defaultLocation);
        }
    }

    @Override
    public CustomerDTO buildDTO(CustomerEntity customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setVersion(customer.getVersion());
        dto.setName(customer.getName());
        dto.setStatus(CustomerDTO.CustomerStatus.valueOf(customer.getStatus().name()));
        dto.setStatusReason(CustomerStatusReason.getByValue(customer.getStatusReason()));
        dto.setFederalTaxId(customer.getFederalTaxId());
        dto.setEdiAccount(customer.getEdiAccount());
        dto.setAddress(ADDRESS_DTO_BUILDER.buildDTO(customer.getAddress()));
        dto.setContactFirstName(customer.getContactFirstName());
        dto.setContactLastName(customer.getContactLastName());
        dto.setContactEmail(customer.getContactEmail());
        dto.setNetworkId(customer.getNetworkId());
        if (customer.getNetwork() != null) {
            dto.setNetwork(customer.getNetwork().getName());
        }
        dto.setCompanyCode(customer.getCompanyCode());
        dto.setInternalNote(customer.getInternalNote());
        setDtoPhone(customer.getPhone(), dto);
        setDtoFax(customer.getFax(), dto);
        setDtoBillTo(customer.getBillToAddresses(), dto);
        setUnitAndCostCenterCodes(customer.getId(), dto);
        if (customer.getCompanyCodeEntity() != null) {
            dto.setDescription(customer.getCompanyCodeEntity().getDescription());
        }

        if (customer.isContract()) {
            dto.setContract(true);
            dto.setStartDate(customer.getEffectiveDate());
            dto.setEndDate(customer.getExpirationDate());
        }

        dto.setCreditLimit(customer.getCreditLimit());
        dto.setCreateOrdersFromVendorBills(customer.getCreateOrdersFromVendorBills());
        dto.setGenerateConsigneeInvoice(customer.isGenerateConsigneeInvoice());
        dto.setDisplayLogoOnBol(customer.getDisplayLogoOnBol());
        dto.setDisplayLogoOnShipLabel(customer.getDisplayLogoOnShipLabel());
        dto.setPrintBarcode(customer.isPrintBarcode());
        Iterator<OrganizationLocationEntity> iterator = customer.getLocations().iterator();
        while (iterator.hasNext()) {
            OrganizationLocationEntity location = iterator.next();
            if (BooleanUtils.isTrue(location.getDefaultNode())) {
                dto.setLocationId(location.getId());
            }
        }
        return dto;
    }

    private CustomerEntity getNewOrExistingCustomer(CustomerDTO dto) {
        CustomerEntity result = null;
        if (dto.getId() != null) {
            result = customerDataProvider.getCustomerById(dto.getId());
        }
        if (result == null) {
            result = new CustomerEntity();
            OrganizationLocationEntity organizationLocation = new OrganizationLocationEntity();
            organizationLocation.setOrganization(result);
            organizationLocation.setLocationName(dto.getLocation());
            organizationLocation.setStatus(Status.ACTIVE);
            organizationLocation.setDefaultNode(true);
            UserEntity accountExecutiveUser = customerDataProvider.findByPersonId(dto.getAccountExecutive());
            AccountExecutiveEntity accountExecutive = new AccountExecutiveEntity(organizationLocation, accountExecutiveUser);
            organizationLocation.getAccountExecutives().add(accountExecutive);
            result.setLocations(new HashSet<OrganizationLocationEntity>());
            result.getLocations().add(organizationLocation);
            result.setNetworkId(dto.getNetworkId());
            result.setCompanyCode(dto.getCompanyCode());
        } else {
            result.setVersion(dto.getVersion());
        }
        return result;
    }

    private void setEntityPhone(PhoneBO phoneDTO, CustomerEntity customer) {
        if (!isPhoneEmpty(phoneDTO)) {
            OrganizationVoicePhoneEntity phone = customer.getPhone();
            if (phone == null) {
                phone = new OrganizationVoicePhoneEntity();
            }
            phone.setDialingCode(phoneDTO.getCountryCode());
            phone.setAreaCode(phoneDTO.getAreaCode());
            phone.setPhoneNumber(phoneDTO.getNumber());
            phone.setExtension(phoneDTO.getExtension());
            phone.setOrganization(customer);
            customer.setPhone(phone);
        }
    }

    private void setEntityFax(PhoneBO faxDTO, CustomerEntity customer) {
        if (!isFaxEmpty(faxDTO)) {
            OrganizationFaxPhoneEntity fax = customer.getFax();
            if (fax == null) {
                fax = new OrganizationFaxPhoneEntity();
            }
            fax.setDialingCode(faxDTO.getCountryCode());
            fax.setAreaCode(faxDTO.getAreaCode());
            fax.setPhoneNumber(faxDTO.getNumber());
            fax.setOrganization(customer);
            customer.setFax(fax);
        } else {
            customer.setFax(null);
        }
    }

    private void setEntityBillTo(CustomerDTO customerDTO, CustomerEntity customer) {
        Set<BillToEntity> billToEntityAddress = customer.getBillToAddresses();
        if (customerDTO.getBillTo() != null) {
            BillToEntity billTo = getBillToEntity(customerDTO, billToEntityAddress);
            billTo.setDefaultNode(true);
            billTo.setOrganization(customer);
            if (billToEntityAddress == null) {
                billToEntityAddress = new HashSet<BillToEntity>();
                customer.setBillToAddresses(billToEntityAddress);
                billToEntityAddress.add(billTo);
            }
            setEntityDefaultBillTo(billToEntityAddress, billTo);
            if (!billToEntityAddress.isEmpty() && billTo.getId() != null) {
                Iterator<OrganizationLocationEntity> locationIterator = customer.getLocations().iterator();
                while (locationIterator.hasNext()) {
                    OrganizationLocationEntity location = locationIterator.next();
                    if (location.getDefaultNode().booleanValue()) {
                        location.setBillTo(billTo);
                    }
                }
            }
            if (customer.getId() != null && billTo.getId() == null) {
                resetAllBillToAddresses(billToEntityAddress);
            }
        } else {
            resetAllBillToAddresses(billToEntityAddress);
        }
    }

    private void resetAllBillToAddresses(Set<BillToEntity> billToEntityAddress) {
        for (BillToEntity billTo: billToEntityAddress) {
            billTo.setDefaultNode(false);
        }
    }

    /**
     * Method creates new BillToEntity or updates existing one with data from DTO.
     *
     * @param dto                 BillTO DTO
     * @param billToEntityAddress existing bill to entities
     * @return {@link BillToEntity}
     */
    private BillToEntity getBillToEntity(CustomerDTO dto, Set<BillToEntity> billToEntityAddress) {
        BillToEntity result = null;
        Long billToId = dto.getBillTo().getId();
        if (billToEntityAddress != null && !billToEntityAddress.isEmpty() && billToId != null) {
            for (BillToEntity billToEntity : billToEntityAddress) {
                if (billToId.equals(billToEntity.getId())) {
                    result = billToEntity;
                    break;
                }
            }
        }
        if (result == null) {
            result = BILL_TO_DTO_BUILDER.buildEntity(dto.getBillTo());
            result.getBillingInvoiceNode().setNetworkId(dto.getNetworkId());
        }
        return result;
    }

    /**
     * Reset default flag for bill to entities.
     *
     * @param billToEntityAddress existing bill to entities
     * @param billTo              default bill to
     */
    private void setEntityDefaultBillTo(Set<BillToEntity> billToEntityAddress, BillToEntity billTo) {
        if (billTo.getId() == null) {
            return;
        }
        for (BillToEntity billToEntity : billToEntityAddress) {
            if (billToEntity.isDefaultNode() && !billTo.getId().equals(billToEntity.getId())) {
                billToEntity.setDefaultNode(false);
            }
        }
    }

    private boolean isFaxEmpty(PhoneBO phone) {
        return phone == null
                || (phone.getCountryCode() == null || phone.getCountryCode().isEmpty())
                && (phone.getAreaCode() == null || phone.getAreaCode().isEmpty())
                && (phone.getNumber() == null || phone.getNumber().isEmpty());
    }

    private boolean isPhoneEmpty(PhoneBO phone) {
        return isFaxEmpty(phone) && (phone.getExtension() == null || phone.getExtension().isEmpty());
    }

    private void setDtoPhone(OrganizationVoicePhoneEntity phoneEntity, CustomerDTO dto) {
        if (phoneEntity != null) {
            PhoneBO phone = new PhoneBO();
            phone.setCountryCode(phoneEntity.getDialingCode());
            phone.setAreaCode(phoneEntity.getAreaCode());
            phone.setNumber(phoneEntity.getPhoneNumber());
            phone.setExtension(phoneEntity.getExtension());
            dto.setContactPhone(phone);
        }
    }

    private void setDtoFax(OrganizationFaxPhoneEntity faxEntity, CustomerDTO dto) {
        if (faxEntity != null) {
            PhoneBO fax = new PhoneBO();
            fax.setCountryCode(faxEntity.getDialingCode());
            fax.setAreaCode(faxEntity.getAreaCode());
            fax.setNumber(faxEntity.getPhoneNumber());
            dto.setContactFax(fax);
        }
    }

    private void setDtoBillTo(Set<BillToEntity> billToAddresses, CustomerDTO dto) {
        if (billToAddresses != null && !billToAddresses.isEmpty()) {
            BillToEntity defaultBillTo = null;
            for (BillToEntity billToEntity : billToAddresses) {
                if (billToEntity.isDefaultNode()) {
                    defaultBillTo = billToEntity;
                    break;
                }
            }
            if (defaultBillTo != null) {
                dto.setBillTo(BILL_TO_DTO_BUILDER.buildDTO(defaultBillTo));
            }
        }
    }

    private void setUnitAndCostCenterCodes(Long orgId, CustomerDTO dto) {
        UnitAndCostCenterCodesBO codes = customerDataProvider.getUnitAndCostCenterCodes(orgId);
        if (codes != null) {
            dto.setUnitCode(codes.getUnitCode());
            dto.setCostCenterCode(codes.getCostCenterCode());
        }
    }

    /**
     * Customer data provider.
     */
    public interface CustomerDataProvider {

        /**
         * Gets customer by id.
         *
         * @param customerId id of customer
         * @return {@link CustomerEntity}
         */
        CustomerEntity getCustomerById(Long customerId);

        /**
         * Gets UserEntity by id.
         *
         * @param personId
         *            id of user
         * @return {@link UserEntity}
         */
        UserEntity findByPersonId(Long personId);

        /**
         * Get Network Unit and Cost Center codes.
         * 
         * @param orgId
         *            id of Organization
         * @return {@link UnitAndCostCenterCodesBO}.
         */
        UnitAndCostCenterCodesBO getUnitAndCostCenterCodes(Long orgId);
    }
}
