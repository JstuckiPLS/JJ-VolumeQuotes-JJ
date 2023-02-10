package com.pls.core.service.address.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.pls.core.dao.BillToDao;
import com.pls.core.dao.TimeZoneDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.dao.address.AddressDao;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ContactInfoSetBO;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.validation.UserAddressBookValidator;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.service.xls.AddressesReportExcelBuilder;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;

/**
 * Address service.
 *
 * @author Gleb Zgonikov
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addrDao;

    @Autowired
    private UserAddressBookDao userAddressBookDao;

    @Autowired
    private BillToDao billToDao;

    @Autowired
    private TimeZoneDao timeZoneDao;

    @Resource(type = UserAddressBookValidator.class)
    private Validator<UserAddressBookEntity> userAddressBookValidator;

    @Value("/templates/Addresses_Export_Template.xlsx")
    private ClassPathResource reportResource;

    @Override
    public ContactInfoSetBO findContactInfoSet(Long userId) {
        return addrDao.findContactInfoSet(userId);
    }

    @Override
    public List<BillToEntity> getOrgBillToAddresses(Long organizationId, Currency currency) {
        return billToDao.findBillTo(organizationId, currency);
    }

    @Override
    public boolean deleteAddressBookEntry(final Long addrId, final Long personId) {
        return userAddressBookDao.deleteUserAddressBookEntry(addrId, personId);
    }

    @Override
    public void saveOrUpdate(final UserAddressBookEntity address, Long organizationId, Long personId, boolean isSelf)
            throws ValidationException {
        saveOrUpdate(address, organizationId, personId, false, isSelf);
    }

    @Override
    public void saveOrUpdate(UserAddressBookEntity address, Long organizationId, Long personId, boolean copyAddress,
            boolean isSelf) throws ValidationException {

        if (isSelf) {
            address.setPersonId(personId);
        } else {
            address.setPersonId(null);
        }

        address.setOrgId(organizationId);

        if (copyAddress && address.getAddress() != null && address.getAddress().getId() != null) {
            AddressEntity origAddress = address.getAddress();
            AddressEntity newAddressEntity = new AddressEntity();
            newAddressEntity.setAddress1(origAddress.getAddress1());
            newAddressEntity.setAddress2(origAddress.getAddress2());
            newAddressEntity.setStatus(origAddress.getStatus());
            newAddressEntity.setCity(origAddress.getCity());
            newAddressEntity.setCountry(origAddress.getCountry());
            newAddressEntity.setState(origAddress.getState());
            newAddressEntity.setStateCode(origAddress.getStateCode());
            newAddressEntity.setZip(origAddress.getZip());
            newAddressEntity.setLatitude(origAddress.getLatitude());
            newAddressEntity.setLongitude(origAddress.getLongitude());
            addrDao.evict(origAddress);
            address.setAddress(newAddressEntity);
        }
        if (address.getIsDefault() != null && address.getIsDefault()) {
            userAddressBookDao.resetDefaultAddressesForCustomer(organizationId);
        }
        userAddressBookValidator.validate(address);
        userAddressBookDao.saveOrUpdate(address);
    }

    @Override
    public UserAddressBookEntity getCustomerAddressById(final Long addrId) {
        return userAddressBookDao.getUserAddressBookEntryById(addrId);
    }

    @Override
    public UserAddressBookEntity getCustomerAddressByCode(Long orgId, String code) {
        return userAddressBookDao.getCustomerAddressBookEntryByCode(orgId, code);
    }

    @Override
    public UserAddressBookEntity getCustomerAddressByNameAndCode(String addressName, String addressCode,
            Long customerId) {
        return userAddressBookDao.getUserAddressBookEntryByNameAndCode(addressName, addressCode, customerId);
    }

    @Override
    public List<UserAddressBookEntity> getCustomerAddressBookForUser(Long customerId, Long personId, boolean filterWarnings, AddressType[] types) {
        return userAddressBookDao.getCustomerAddressBookForUser(customerId, personId, filterWarnings, getDefaultAddressTypes(types));
    }

    @Override
    public List<UserAddressBookEntity> getCustomerAddressBookByCountryAndZip(String countryCode, String zipCode, String city,
            Long customerId, Long personId, AddressType[] types) {
        return userAddressBookDao.findCustomerAddressByCountryAndZip(customerId, personId, countryCode, zipCode, city, getDefaultAddressTypes(types));
    }

    @Override
    public boolean isAddressNameExists(String addressName, Long orgId) {
        return userAddressBookDao.checkAddressNameExists(orgId, addressName);
    }

    @Override
    public boolean isAddressCodeExists(String addressCode, Long orgId) {
        return userAddressBookDao.checkAddressCodeExists(orgId, addressCode);
    }

    @Override
    public boolean isAddressUnique(String addressName, String addressCode, Long orgId) {
        return userAddressBookDao.isAddressUnique(orgId, addressName, addressCode);
    }

    @Override
    public Long getAddressId(AddressVO address) {
        if (address == null) {
            return null;
        }

        return getNewOrExistingAddress(address).getId();
    }


    @Override
    public AddressEntity getNewOrExistingAddress(AddressVO address) {
        if (address == null) {
            return null;
        }

        correctCareOf(address);

        List<AddressEntity> existingAddresses = addrDao.findAddressesByAddressVO(address);

        if (existingAddresses.isEmpty()) {
            // create the address
            return addrDao.saveOrUpdate(createAddressEntity(address));
        } else {
            return existingAddresses.get(0);
        }
    }

    @Override
    public AddressVO getAddressVOById(Long id) {
        AddressEntity entity = addrDao.find(id);
        if (entity != null) {
            return getAddressVOFromAddressEntity(entity);
        }
        return null;
    }

    @Override
    public TimeZoneEntity findTimeZoneByCountryZip(String countryCode, String zipCode) {
        return timeZoneDao.findByCountryZip(countryCode, zipCode);
    }

    @Override
    public AddressEntity getAddressEntityById(Long id) {
        return addrDao.find(id);
    }

    public void setUserAddressBookDao(final UserAddressBookDao userAddressBookDao) {
        this.userAddressBookDao = userAddressBookDao;
    }

    public void setValidator(Validator<UserAddressBookEntity> validator) {
        this.userAddressBookValidator = validator;
    }

    public void setTimeZoneDao(TimeZoneDao timeZoneDao) {
        this.timeZoneDao = timeZoneDao;
    }

    @Override
    public FileInputStreamResponseEntity export(Long orgId, Long userId) throws IOException {
        return new AddressesReportExcelBuilder(reportResource).generateReport(userAddressBookDao
                .getCustomerAddressBookForUser(orgId, userId, false, Arrays.asList(AddressType.values())));
    }

    @Override
    public UserAddressBookEntity getDefaultFreightBillPayToAddresses(Long customerId) {
        return userAddressBookDao.getDefaultFreightBillPayTo(customerId);
    }

    @Override
    public List<UserAddressBookEntity> listSuggestions(Long customerId, String query) {
        return userAddressBookDao.listSuggestions(customerId, query);
    }

    @Override
    public List<UserAddressBookEntity> listSuggestions(Long customerId, String query, boolean strict) {
        return userAddressBookDao.listSuggestions(customerId, query, strict);
    }

    @Override
    public List<UserAddressBookEntity> getAddressBooksForFreightBill(Long customerId, Long userId, String filter) {
        if (StringUtils.isBlank(filter)) {
            return Collections.emptyList();
        }
        return userAddressBookDao.getAddressBooksForFreightBill(customerId, userId, filter);
    }

    /**
     * Locate care/of information in address1 and switch with address2
     *
     * @return TRUE if a correction was made
     */
    private boolean correctCareOf(AddressVO address) {
        // don't switch with address 2 if there is no address 2
        if ((address.getAddress2() == null) || address.getAddress2().trim().equals("")) {
            return false;
        }

        // RegEx of C[<special>]O where <special> is one of blank space, forward slash or period
        String exp = "C[ /\\.]O.*";

        // don't switch with address 2 if it's also a C/O
        if (address.getAddress2().toUpperCase().matches(exp)) {
            return false;
        }

        String strAddress1 = address.getAddress1().toUpperCase();
        if (strAddress1.matches(exp)) {
            strAddress1 = address.getAddress1();
            address.setAddress1(address.getAddress2());
            address.setAddress2(strAddress1);
            return true;
        }
        return false;
    }

    private AddressEntity createAddressEntity(AddressVO address) {
        AddressEntity entity = new AddressEntity();
        entity.setAddress1(address.getAddress1());
        entity.setAddress2(address.getAddress2());
        entity.setCity(address.getCity());
        entity.setStateCode(address.getStateCode());
        entity.setZip(address.getPostalCode());
        CountryEntity country = new CountryEntity();
        country.setId(address.getCountryCode());
        entity.setCountry(country);
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    private AddressVO getAddressVOFromAddressEntity(AddressEntity entity) {
        AddressVO address = new AddressVO();
        address.setAddress1(entity.getAddress1());
        address.setAddress2(entity.getAddress2());
        address.setCity(entity.getCity());
        address.setStateCode(entity.getStateCode());
        address.setPostalCode(entity.getZip());
        address.setCountryCode(entity.getCountry().getId());

        return address;
    }

    private List<AddressType> getDefaultAddressTypes(AddressType[] types) {
        List<AddressType> addressTypes;
        if (types == null) {
            addressTypes = Lists.newArrayList(AddressType.SHIPPING, AddressType.BOTH);
        } else {
            addressTypes = Lists.newArrayList(types);
        }

        return addressTypes;
    }
}
