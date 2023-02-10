package com.pls.user.restful.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.CustomerServiceContactInfoType;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.ZipDTO;

/**
 * Full info about {@link UserEntity}. This is enough big about of data so this class should be used only for
 * user management purposes. Please, for other purposes use more lightweight DTO.
 * 
 * @author Denis Zhupinsky
 */
public class UserDTO {
    private Long personId;
    private String userId;

    private String firstName;
    private String fullName;
    private String lastName;
    private String email;
    private String address1;
    private String address2;
    private CountryDTO country;
    private ZipDTO zip;
    private PhoneBO fax;
    private PhoneBO phone;
    private ParentOrganizationBO parentOrganization;
    private List<Long> networkIds = new ArrayList<Long>();
    private UserAdditionalContactInfoBO additionalInfo;
    private UserAdditionalContactInfoBO defaultInfo;
    private CustomerServiceContactInfoType customerServiceContactInfoType;
    private final List<CustomerUserDTO> customers = new ArrayList<CustomerUserDTO>();
    private List<Long> roles = new ArrayList<Long>();
    private List<Long> permissions = new ArrayList<Long>();
    private String promoCode;
    private BigDecimal discount;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public PhoneBO getFax() {
        return fax;
    }

    public void setFax(PhoneBO fax) {
        this.fax = fax;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ParentOrganizationBO getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(ParentOrganizationBO parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    public List<Long> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Long> permissions) {
        this.permissions = permissions;
    }

    public List<CustomerUserDTO> getCustomers() {
        return customers;
    }

    public List<Long> getNetworkIds() {
        return networkIds;
    }

    public void setNetworkIds(List<Long> networkIds) {
        this.networkIds = networkIds;
    }

    public UserAdditionalContactInfoBO getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(UserAdditionalContactInfoBO additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public UserAdditionalContactInfoBO getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(UserAdditionalContactInfoBO defaultInfo) {
        this.defaultInfo = defaultInfo;
    }

    public CustomerServiceContactInfoType getCustomerServiceContactInfoType() {
        return customerServiceContactInfoType;
    }

    public void setCustomerServiceContactInfoType(CustomerServiceContactInfoType customerServiceContactInfoType) {
        this.customerServiceContactInfoType = customerServiceContactInfoType;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}