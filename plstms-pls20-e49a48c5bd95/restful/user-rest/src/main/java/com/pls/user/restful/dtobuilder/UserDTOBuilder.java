package com.pls.user.restful.dtobuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.CustomerServiceContactInfoType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.NetworkUserEntity;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserAdditionalContactInfoEntity;
import com.pls.core.domain.user.UserAddressEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserNotificationsEntity;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.CountryDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.restful.dto.CustomerLocationUserDTO;
import com.pls.user.restful.dto.CustomerUserDTO;
import com.pls.user.restful.dto.UserDTO;

/**
 * Builder to prepare {@link UserDTO} object and restore {@link UserEntity} from {@link UserDTO}.
 * 
 * @author Denis Zhupinsky
 */
public class UserDTOBuilder extends AbstractDTOBuilder<UserEntity, UserDTO> {

    private DataProvider dataProvider;

    private final CountryDTOBuilder countryDTOBuilder = new CountryDTOBuilder();
    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            will be used for {@link UserDTOBuilder#buildEntity(UserDTO)} in case of update.
     */
    public UserDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public UserDTO buildDTO(UserEntity userEntity) {
        UserDTO result = new UserDTO();
        result.setPersonId(userEntity.getId());
        result.setUserId(userEntity.getLogin());
        result.setFirstName(userEntity.getFirstName());
        result.setLastName(userEntity.getLastName());
        result.setFullName(UserNameBuilder.buildFullName(userEntity.getFirstName(), userEntity.getLastName()));
        result.setEmail(userEntity.getEmail());
        buildAddressDTO(userEntity, result);
        result.setPhone(buildPhoneDTO(userEntity.getActiveUserPhoneByType(PhoneType.VOICE)));
        result.setFax(buildPhoneDTO(userEntity.getActiveUserPhoneByType(PhoneType.FAX)));

        result.setNetworkIds(buildNetworkIds(userEntity.getNetworkUsers()));
        result.setParentOrganization(getParentOrganizationDTO(userEntity.getParentOrganization()));
        result.getCustomers().addAll(getCustomersDTO(userEntity.getCustomerUsers()));

        result.setCustomerServiceContactInfoType(userEntity.getCustomerServiceContactInfoType());
        result.setDefaultInfo(dataProvider.getContactInfo(null));
        result.setAdditionalInfo(dataProvider.getContactInfo(userEntity));

        result.getPermissions().addAll(getUserPermissions(userEntity.getId()));
        result.getRoles().addAll(getUserGroups(userEntity.getId()));

        if (!userEntity.getPromoCodes().isEmpty()) {
            PromoCodeEntity promoCode = userEntity.getPromoCodes().stream().findFirst().get();
            result.setDiscount(promoCode.getPercentage());
            result.setPromoCode(promoCode.getCode());
        }
        return result;
    }

    @Override
    public UserEntity buildEntity(UserDTO dto) {
        UserEntity result = dataProvider.findUserById(dto.getPersonId());
        if (result == null) {
            result = new UserEntity();
            result.setUserStatus(UserStatus.ACTIVE);
        }
        result.setLogin(dto.getUserId());
        result.setFirstName(dto.getFirstName());
        result.setLastName(dto.getLastName());
        result.setEmail(dto.getEmail());
        buildAddressEntity(dto, result);
        ParentOrganizationBO parentOrganization = dto.getParentOrganization();
        if (parentOrganization != null) {
            result.setParentOrgId(parentOrganization.getOrganizationId());
        }
        prepareUserPhone(result, dto.getPhone(), PhoneType.VOICE);
        prepareUserPhone(result, dto.getFax(), PhoneType.FAX);
        prepareAdditionalContactInfo(result, dto);
        prepareUserNetworks(result, dto);
        prepareUserAssignedCustomers(result, dto);
        buildPromoCode(dto, result);

        return result;
    }

    private void buildPromoCode(UserDTO dto, UserEntity result) {
        Set<PromoCodeEntity> promoCodes = result.getPromoCodes();
        if (promoCodes.isEmpty()) {
            buildPromoCodeEntity(dto, result, null);
        } else {
            PromoCodeEntity promoCode = promoCodes.stream().findFirst().get();
            if (isPromoCodeChanged(dto, promoCode)) {
                result.getPromoCodes().stream().forEach((p) -> p.setStatus(Status.INACTIVE));
                buildPromoCodeEntity(dto, result, promoCode.getTermsAndConditionsVersion());
            }
        }
    }

    private void buildPromoCodeEntity(UserDTO dto, UserEntity result, Long termsAndConditionsVersion) {
        if (StringUtils.isNotEmpty(dto.getPromoCode()) && dto.getDiscount() != null) {
            PromoCodeEntity promoCode = new PromoCodeEntity();
            promoCode.setCode(dto.getPromoCode());
            promoCode.setPercentage(dto.getDiscount());
            promoCode.setStatus(Status.ACTIVE);
            promoCode.setAccountExecutive(result);
            promoCode.setTermsAndConditionsVersion(termsAndConditionsVersion);
            result.getPromoCodes().add(promoCode);
        }
    }

    private Collection<CustomerUserDTO> getCustomersDTO(List<CustomerUserEntity> customerUsers) {
        Map<Long, CustomerUserDTO> customersDTO = new HashMap<Long, CustomerUserDTO>();
        for (CustomerUserEntity customerUser : customerUsers) {
            if (customerUser.getStatus() == Status.ACTIVE
                    && dataProvider.isCurrentUserAssignedToCustomerLocation(customerUser.getCustomerId(), customerUser.getLocationId())) {
                CustomerUserDTO customerUserDTO;
                if (customersDTO.containsKey(customerUser.getCustomerId())) {
                    customerUserDTO = customersDTO.get(customerUser.getCustomerId());
                } else {
                    customerUserDTO = new CustomerUserDTO();
                    customerUserDTO.setCustomerId(customerUser.getCustomerId());
                    customerUserDTO.setCustomerName(customerUser.getCustomer().getName());
                    setCustomerUserLocationsInformation(customerUser, customerUserDTO);
                    customersDTO.put(customerUserDTO.getCustomerId(), customerUserDTO);
                    customerUserDTO.setLocations(new ArrayList<CustomerLocationUserDTO>());
                }
                if (customerUserDTO.getAssignmentDate() == null
                        || customerUserDTO.getAssignmentDate().before(customerUser.getModification().getModifiedDate())) {
                    customerUserDTO.setAssignmentDate(customerUser.getModification().getModifiedDate());
                }
                if (customerUser.getLocationId() != null) {
                    customerUserDTO.getLocations().add(getLocationDTO(customerUser));
                }
            }
        }
        return customersDTO.values();
    }

    private void setCustomerUserLocationsInformation(CustomerUserEntity customerUser, CustomerUserDTO customerUserDTO) {
        List<AssociatedCustomerLocationBO> locations = dataProvider.getCustomerLocationsAssignedToCurrentUser(customerUser.getCustomerId());
        customerUserDTO.setLocationsCount(new Long(locations.size()));
        customerUserDTO.setMultipleAE(false);
        if (!locations.isEmpty()) {
            AssociatedCustomerLocationBO uniqueLocation = null;
            for (AssociatedCustomerLocationBO location : locations) {
                if (uniqueLocation == null || StringUtils.isBlank(uniqueLocation.getAccountExecutive())) {
                    uniqueLocation = location;
                } else if (!StringUtils.isBlank(location.getAccountExecutive())
                        && !StringUtils.equals(uniqueLocation.getAccountExecutive(), location.getAccountExecutive())) {
                    customerUserDTO.setAccountExecutive("Multiple Account Executives");
                    customerUserDTO.setMultipleAE(true);
                    return;
                }
            }
            customerUserDTO.setAccountExecutive(uniqueLocation.getAccountExecutive());
        }
    }

    private CustomerLocationUserDTO getLocationDTO(CustomerUserEntity customerUser) {
        CustomerLocationUserDTO dto = new CustomerLocationUserDTO();
        dto.setLocationId(customerUser.getLocationId());
        dto.getNotifications().addAll(customerUser.getActiveNotifications());
        return dto;
    }

    private ParentOrganizationBO getParentOrganizationDTO(OrganizationEntity parentOrg) {
        if (parentOrg != null) {
            ParentOrganizationBO bo = new ParentOrganizationBO();
            bo.setOrganizationId(parentOrg.getId());
            bo.setOrganizationName(parentOrg.getName());
            bo.setCustomer(isCustomerUser(parentOrg));
            return bo;
        }
        return null;
    }

    private void buildAddressDTO(UserEntity userEntity, UserDTO result) {
        UserAddressEntity userAddress = userEntity.getUserAddress();
        if (userAddress != null) {
            AddressEntity address = userAddress.getAddress();
            result.setAddress1(address.getAddress1());
            result.setAddress2(address.getAddress2());
            ZipDTO zip = zipDTOBuilder.buildDTO(address.getZipCode());
            result.setZip(zip);
            result.setCountry(zip.getCountry());
        }
    }

    private void buildAddressEntity(UserDTO dto, UserEntity result) {
        UserAddressEntity userAddress = result.getUserAddress();
        if (userAddress == null) {
            userAddress = new UserAddressEntity();
            userAddress.setUser(result);
            result.getAddresses().add(userAddress);
        }
        AddressEntity address = userAddress.getAddress();
        if (address == null) {
            address = new AddressEntity();
            userAddress.setAddress(address);
        }
        address.setCountry(countryDTOBuilder.buildEntity(dto.getCountry()));
        address.setCity(dto.getZip().getCity());
        address.setStateCode(dto.getZip().getState());
        address.setZip(dto.getZip().getZip());
        address.setAddress1(dto.getAddress1());
        address.setAddress2(dto.getAddress2());
    }

    private List<Long> getUserPermissions(Long userId) {
        List<Long> permissionsIds = new ArrayList<Long>();
        List<UserCapabilityEntity> capabilities = dataProvider.getCapabilities(userId);
        Set<String> currentUserCapabilities = SecurityUtils.getCapabilities();
        for (UserCapabilityEntity capability : capabilities) {
            if (Status.ACTIVE == capability.getStatus() && currentUserCapabilities.contains(capability.getCapability().getName())) {
                permissionsIds.add(capability.getCapabilityId());
            }
        }
        return permissionsIds;
    }

    private List<Long> getUserGroups(Long userId) {
        List<Long> groupsIds = new ArrayList<Long>();
        List<UserGroupEntity> userGroups = dataProvider.getGroups(userId);
        List<UserGroupEntity> currentUserGroups = dataProvider.getGroups(SecurityUtils.getCurrentPersonId());
        for (UserGroupEntity group : userGroups) {
            if (Status.ACTIVE == group.getStatus() && isGroupExists(currentUserGroups, group.getGroupId())) {
                groupsIds.add(group.getGroupId());
            }
        }
        return groupsIds;
    }

    private boolean isGroupExists(List<UserGroupEntity> groups, Long groupId) {
        for (UserGroupEntity group : groups) {
            if (Status.ACTIVE == group.getStatus() && ObjectUtils.equals(group.getGroupId(), groupId)) {
                return true;
            }
        }
        return false;
    }

    private PhoneBO buildPhoneDTO(UserPhoneEntity phone) {
        PhoneBO result = null;
        if (phone != null) {
            result = new PhoneBO();
            result.setCountryCode(phone.getCountryCode());
            result.setAreaCode(phone.getAreaCode());
            result.setNumber(phone.getNumber());
            if (phone.getType() != PhoneType.FAX) {
                result.setExtension(phone.getExtension());
            }
        }
        return result;
    }

    private List<Long> buildNetworkIds(List<NetworkUserEntity> networkUsers) {
        List<Long> result = new ArrayList<Long>();
        if (networkUsers != null && !networkUsers.isEmpty()) {
            List<SimpleValue> currentUserNetworks = dataProvider.findNetworksForCurrentUser();
            for (NetworkUserEntity networkUser : networkUsers) {
                if (networkUser.getStatus() == Status.ACTIVE && isNetworkPresent(currentUserNetworks, networkUser.getNetworkId())) {
                    result.add(networkUser.getNetworkId());
                }
            }
        }
        return result;
    }

    private boolean isNetworkPresent(List<SimpleValue> networks, Long networkId) {
        for (SimpleValue network : networks) {
            if (ObjectUtils.equals(network.getId(), networkId)) {
                return true;
            }
        }
        return false;
    }

    private void prepareAdditionalContactInfo(UserEntity entity, UserDTO dto) {
        if (dto.getAdditionalInfo() != null && dto.getCustomerServiceContactInfoType() == CustomerServiceContactInfoType.CUSTOM) {
            populateAdditionalContactInfo(entity, dto.getAdditionalInfo());
        } else {
            entity.setAdditionalInfo(null);
        }
        entity.setCustomerServiceContactInfoType(dto.getCustomerServiceContactInfoType());
    }

    private void populateAdditionalContactInfo(UserEntity userEntity, UserAdditionalContactInfoBO dto) {
        UserAdditionalContactInfoEntity entity = userEntity.getAdditionalInfo();
        if (entity == null) {
            entity = new UserAdditionalContactInfoEntity();
            entity.setContactType("EXTERNAL");
            entity.setUser(userEntity);
            userEntity.setAdditionalInfo(entity);
        }
        entity.setContactName(dto.getContactName());
        entity.setEmail(dto.getEmail());
        preparePhoneEntity(entity, dto.getPhone());
    }

    private void preparePhoneEntity(UserAdditionalContactInfoEntity entity, PhoneBO dto) {
        PhoneEntity phone = entity.getPhone();
        if (phone == null) {
            phone = new PhoneEntity();
            entity.setPhone(phone);
        }

        phone.setAreaCode(dto.getAreaCode());
        phone.setCountryCode(dto.getCountryCode());
        phone.setNumber(dto.getNumber());
        phone.setType(PhoneType.VOICE);
        phone.setExtension(dto.getExtension());
    }

    private void prepareUserPhone(UserEntity userEntity, PhoneBO phone, PhoneType type) {
        if (phone != null) {
            UserPhoneEntity phoneEntity = findOrCreateUserPhone(userEntity, type);
            phoneEntity.setStatus(Status.ACTIVE);
            phoneEntity.setCountryCode(phone.getCountryCode());
            phoneEntity.setAreaCode(phone.getAreaCode());
            phoneEntity.setNumber(phone.getNumber());
            if (type != PhoneType.FAX) {
                phoneEntity.setExtension(phone.getExtension());
            }
        } else {
            UserPhoneEntity phoneEntity = userEntity.getActiveUserPhoneByType(type);
            if (phoneEntity != null) {
                phoneEntity.setStatus(Status.INACTIVE);
            }
        }
    }

    private UserPhoneEntity findOrCreateUserPhone(UserEntity userEntity, PhoneType type) {
        UserPhoneEntity result = null;
        for (UserPhoneEntity phoneEntity : userEntity.getPhones()) {
            if (phoneEntity.getType().equals(type) && phoneEntity.getCustomerUser() == null) {
                result = phoneEntity;
                break;
            }
        }
        if (result == null) {
            result = new UserPhoneEntity();
            result.setType(type);
            result.setUser(userEntity);
            userEntity.getPhones().add(result);
        }
        return result;
    }

    private CustomerUserEntity findCustomerUser(UserEntity user, Long customerId, Long locationId) {
        for (CustomerUserEntity customerUser : user.getCustomerUsers()) {
            if (ObjectUtils.equals(customerId, customerUser.getCustomerId()) && ObjectUtils.equals(locationId, customerUser.getLocationId())) {
                return customerUser;
            }
        }
        return null;
    }

    private CustomerUserEntity findOrCreateCustomerUser(UserEntity user, Long customerId, Long locationId) {
        CustomerUserEntity result = findCustomerUser(user, customerId, locationId);
        if (result == null) {
            result = new CustomerUserEntity();
            result.setUser(user);
            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            result.setCustomer(customer);
            result.setLocationId(locationId);
            user.getCustomerUsers().add(result);
        }
        return result;
    }

    private UserNotificationsEntity findNotification(CustomerUserEntity customerUser, String notification) {
        for (UserNotificationsEntity notificationEntity : customerUser.getNotifications()) {
            if (StringUtils.equals(notification, notificationEntity.getNotificationType())) {
                return notificationEntity;
            }
        }
        return null;
    }

    private void prepareNotification(CustomerUserEntity customerUser, List<String> notificationTypes) {
        for (UserNotificationsEntity notificationEntity : customerUser.getNotifications()) {
            notificationEntity.setStatus(Status.INACTIVE);
        }
        if (notificationTypes != null) {
            for (String notification : notificationTypes) {
                UserNotificationsEntity notificationEntity = findNotification(customerUser, notification);
                if (notificationEntity == null) {
                    notificationEntity = new UserNotificationsEntity();
                    notificationEntity.setNotificationType(notification);
                    notificationEntity.setCustomerUser(customerUser);
                    customerUser.getNotifications().add(notificationEntity);
                }
                notificationEntity.setStatus(Status.ACTIVE);
            }
        }
    }

    private void prepareUserAssignedCustomers(UserEntity entity, UserDTO dto) {
        // add new customers
        if (dto.getCustomers() != null) {
            for (CustomerUserDTO customer : dto.getCustomers()) {
                if (!dataProvider.isCurrentUserAssignedToCustomerLocation(customer.getCustomerId(), null)) {
                    continue;
                }
                CustomerUserEntity customerUser = findOrCreateCustomerUser(entity, customer.getCustomerId(), null);
                customerUser.setStatus(Status.ACTIVE);
                for (CustomerLocationUserDTO location : customer.getLocations()) {
                    if (!dataProvider.isCurrentUserAssignedToCustomerLocation(customer.getCustomerId(), location.getLocationId())) {
                        continue;
                    }
                    CustomerUserEntity customerUserLocation = findOrCreateCustomerUser(entity, customer.getCustomerId(),
                            location.getLocationId());
                    customerUserLocation.setStatus(Status.ACTIVE);
                    prepareNotification(customerUserLocation, location.getNotifications());
                }
            }
        }
        // remove unassigned customers
        for (CustomerUserEntity customerUser : entity.getCustomerUsers()) {
            if (isCustomerUserAbsent(customerUser, dto.getCustomers())
                    && dataProvider.isCurrentUserAssignedToCustomerLocation(customerUser.getCustomerId(), customerUser.getLocationId())) {
                customerUser.setStatus(Status.INACTIVE);
            }
        }
    }

    private boolean isCustomerUserAbsent(CustomerUserEntity customerUser, List<CustomerUserDTO> customers) {
        if (customers == null) {
            return true;
        }
        for (CustomerUserDTO customer : customers) {
            if (ObjectUtils.equals(customer.getCustomerId(), customerUser.getCustomerId())) {
                if (customerUser.getLocationId() == null) {
                    return false;
                } else {
                    return findLocationById(customer.getLocations(), customerUser.getLocationId()) == null;
                }
            }
        }
        return true;
    }

    private CustomerLocationUserDTO findLocationById(List<CustomerLocationUserDTO> locations, Long locationId) {
        for (CustomerLocationUserDTO location : locations) {
            if (ObjectUtils.equals(location.getLocationId(), locationId)) {
                return location;
            }
        }
        return null;
    }

    private NetworkUserEntity findNetworkUser(UserEntity user, Long networkId) {
        for (NetworkUserEntity networkUser : user.getNetworkUsers()) {
            if (ObjectUtils.equals(networkId, networkUser.getNetworkId())) {
                return networkUser;
            }
        }
        return null;
    }

    private NetworkUserEntity findOrCreateNetworkUser(UserEntity entity, Long networkId) {
        NetworkUserEntity result = findNetworkUser(entity, networkId);
        if (result == null) {
            result = new NetworkUserEntity();
            result.setUser(entity);
            result.setNetworkId(networkId);
            entity.getNetworkUsers().add(result);
        }
        return result;
    }

    private void inactivateNetworkUser(UserEntity entity, Long networkId) {
        NetworkUserEntity networkUser = findNetworkUser(entity, networkId);
        if (networkUser != null) {
            networkUser.setStatus(Status.INACTIVE);
        }
    }

    private void prepareUserNetworks(UserEntity entity, UserDTO dto) {
        List<SimpleValue> networksAvailableForCurrentUser = dataProvider.findNetworksForCurrentUser();
        for (SimpleValue network : networksAvailableForCurrentUser) {
            if (dto.getNetworkIds() == null || !dto.getNetworkIds().contains(network.getId())) {
                inactivateNetworkUser(entity, network.getId());
            } else {
                NetworkUserEntity networkUser = findOrCreateNetworkUser(entity, network.getId());
                networkUser.setStatus(Status.ACTIVE);
            }
        }
    }

    private boolean isCustomerUser(OrganizationEntity organization) {
        return StringUtils.equals("SHIPPER", organization.getOrgType());
    }

    private boolean isPromoCodeChanged(UserDTO dto, PromoCodeEntity promoCode) {
        return ((promoCode.getPercentage() != null ^ dto.getDiscount() != null)
                || (promoCode.getPercentage() != null && dto.getDiscount() != null
                        && promoCode.getPercentage().compareTo(dto.getDiscount()) != 0)
                || !StringUtils.equals(promoCode.getCode(), dto.getPromoCode()));
    }

    /**
     * Data provider for update action.
     *
     * @author Aleksandr Leshchenko
     */
    public interface DataProvider {
        /**
         * Find existing user by ID.
         *
         * @param personId
         *            {@link UserEntity#getId()} value. May be nullable.
         * @return Not <code>null</code> {@link UserEntity} if user was found. Otherwise returns
         *         <code>null</code>.
         */
        UserEntity findUserById(Long personId);

        /**
         * Get groups for user.
         * 
         * @param personId
         *            user id
         * @return list of groups
         */
        List<UserGroupEntity> getGroups(Long personId);

        /**
         * Get capabilities for user.
         * 
         * @param personId
         *            user id
         * @return list of capabilities
         */
        List<UserCapabilityEntity> getCapabilities(Long personId);

        /**
         * Get contact info for user.
         * 
         * @param userEntity
         *            user or <code>null</code>
         * @return user contact info or default contact info when user is <code>null</code>
         */
        UserAdditionalContactInfoBO getContactInfo(UserEntity userEntity);

        /**
         * Find list of networks that current user can see.
         * 
         * @return list of found networks
         */
        List<SimpleValue> findNetworksForCurrentUser();

        /**
         * Check if current user has access to customer location.
         * 
         * @param customerId
         *            id of customer
         * @param locationId
         *            id of location. can be <code>null</code>.
         * @return <code>true</code> if user has access. <code>false</code> otherwise.
         */
        boolean isCurrentUserAssignedToCustomerLocation(Long customerId, Long locationId);

        /**
         * Get list of locations assigned to current user for specified customer.
         * 
         * @param customerId
         *            id of customer
         * @return list of locations
         */
        List<AssociatedCustomerLocationBO> getCustomerLocationsAssignedToCurrentUser(Long customerId);
    }
}
