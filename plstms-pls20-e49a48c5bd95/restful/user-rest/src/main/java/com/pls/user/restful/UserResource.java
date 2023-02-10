package com.pls.user.restful;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.core.domain.enums.UserSearchType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.AuthService;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.location.service.OrganizationLocationService;
import com.pls.restful.util.ResourceParamsUtils;
import com.pls.shipment.service.impl.validation.PromoCodeValidator;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.domain.UserSettingsEntity;
import com.pls.user.domain.bo.CustomerUserSearchFieldBO;
import com.pls.user.domain.bo.UserCustomerBO;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.domain.bo.UserNotificationsBO;
import com.pls.user.restful.dto.CustomerUserDTO;
import com.pls.user.restful.dto.UserCredentialsDTO;
import com.pls.user.restful.dto.UserDTO;
import com.pls.user.restful.dto.UserEmailItemDTO;
import com.pls.user.restful.dto.UserEmailPhoneItemDTO;
import com.pls.user.restful.dto.UserListItemDTO;
import com.pls.user.restful.dto.UserSettingsDTO;
import com.pls.user.restful.dtobuilder.UserDTOBuilder;
import com.pls.user.restful.dtobuilder.UserDTOBuilder.DataProvider;
import com.pls.user.restful.dtobuilder.UserEmailItemDTOBuilder;
import com.pls.user.restful.dtobuilder.UserEmailPhoneItemDTOBuilder;
import com.pls.user.restful.dtobuilder.UserListItemDTOBuilder;
import com.pls.user.restful.dtobuilder.UserSettingsDTOBuilder;
import com.pls.user.service.UserService;
import com.pls.user.service.exc.PasswordsDoNotMatchException;

import freemarker.template.TemplateException;

/**
 * Users REST resource.
 *
 * @author Denis Zhupinsky
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/user")
public class UserResource {
    private final UserListItemDTOBuilder listItemDTOBuilder = new UserListItemDTOBuilder();
    private final UserEmailItemDTOBuilder emailItemDTOBuilder = new UserEmailItemDTOBuilder();
    private final UserEmailPhoneItemDTOBuilder emailPhoneItemDTOBuilder = new UserEmailPhoneItemDTOBuilder();
    private final UserDTOBuilder userBuilder = new UserDTOBuilder(new DataProvider() {
        @Override
        public UserEntity findUserById(Long personId) {
            return userService.findByPersonId(personId);
        }

        @Override
        public List<SimpleValue> findNetworksForCurrentUser() {
            return userService.getUserActiveNetworks(SecurityUtils.getCurrentPersonId());
        }

        @Override
        public boolean isCurrentUserAssignedToCustomerLocation(Long customerId, Long locationId) {
            return userService.isUserAssignedToCustomerLocation(SecurityUtils.getCurrentPersonId(), customerId, locationId);
        }

        @Override
        public List<UserGroupEntity> getGroups(Long personId) {
            return userService.getGroups(personId);
        }

        @Override
        public List<UserCapabilityEntity> getCapabilities(Long personId) {
            return userService.getCaps(personId);
        }

        @Override
        public UserAdditionalContactInfoBO getContactInfo(UserEntity userEntity) {
            return contactInfoService.getContactInfo(userEntity);
        }

        @Override
        public List<AssociatedCustomerLocationBO> getCustomerLocationsAssignedToCurrentUser(Long customerId) {
            return organizationLocationService.getAssociatedCustomerLocations(customerId, SecurityUtils.getCurrentPersonId(), null);
        }
    });
    
    private final UserSettingsDTOBuilder userSettingsDTOBuilder = new UserSettingsDTOBuilder();

    @Autowired
    private UserPermissionsService permissionsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ContactInfoService contactInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private OrganizationLocationService organizationLocationService;

    @Autowired
    private PromoCodeValidator promoCodeValidator;

    /**
     * Change status for user.
     *
     * @param personId
     *            the id of user to change status
     * @param activate
     *            <code>true</code> to activate or <code>false</code> to deactivate.
     * @throws EntityNotFoundException
     *             when user not found.
     */
    @RequestMapping(value = "/{personId}/status/{activate}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void changeStatus(@PathVariable("personId") Long personId, @PathVariable("activate") boolean activate)
            throws EntityNotFoundException {
        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());
        // TODO check that current user has permissions to manage required user/organization

        if (!activate) {
            SecurityUtils.expireSessions(personId, sessionRegistry);
        }

        userService.changeStatus(personId, activate ? UserStatus.ACTIVE : UserStatus.INACTIVE,
                SecurityUtils.getCurrentPersonId());
    }

    /**
     * Check if user exists and if so - change its password if old one is correct.
     *
     * @param credentials
     *            user credentials
     * @throws PasswordsDoNotMatchException
     *             when old password does not match.
     * @throws EntityNotFoundException
     *             when user not found.
     * @throws ValidationException
     *             when new password has invalid format.
     */
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void changeUserPassword(@RequestBody UserCredentialsDTO credentials)
            throws PasswordsDoNotMatchException, EntityNotFoundException, ValidationException {
        userService.changePassword(SecurityUtils.getCurrentPersonId(), credentials.getCurrentPassword(), credentials.getNewPassword());
        SecurityUtils.expireSessions(SecurityUtils.getCurrentPersonId(), sessionRegistry);
    }

    /**
     * Check if user with following login already exists. Return existing status.
     *
     * @param userId
     *            login of user to check
     * @return if user already exists
     */
    @RequestMapping(value = "/userId/{userId}/valid", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkUserId(@PathVariable("userId") String userId) {
        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());

        return userService.isValidNewUserId(userId, null);
    }

    /**
     * Check if user with following login already exists. Return existing status.
     *
     * @param userId
     *            login of user to check
     * @param personId
     *            Person ID for existed user or null for new user.
     * @return if user already exists
     */
    @RequestMapping(value = "/userId/{userId}/{personId}/valid", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkUserId(@PathVariable("userId") String userId, @PathVariable("personId") Long personId) {
        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());

        return userService.isValidNewUserId(userId, personId);
    }

    /**
     * Create or update user.
     *
     * @param user
     *            Not <code>null</code> {@link UserDTO}
     * @throws ValidationException
     *             Invalid user data was specified
     * @throws IOException
     *             Unable to send email
     * @throws TemplateException
     *             Unable to send email
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveUser(@RequestBody UserDTO user) throws ValidationException, IOException, TemplateException {

        promoCodeValidator.validate(new SimpleValue(user.getPersonId(), user.getPromoCode()));

        if (user.getPersonId() == null || !user.getPersonId().equals(SecurityUtils.getCurrentPersonId())) {
            permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());
        }
        for (Long organizationId : extractOrgIds(user)) {
            permissionsService.checkOrganization(organizationId);
        }

        UserEntity userEntity = userBuilder.buildEntity(user);

        userService.saveUser(userEntity, user.getRoles(), user.getPermissions());

        // if we update ourselves then reset spring auth data
        if (ObjectUtils.equals(user.getPersonId(), SecurityUtils.getCurrentPersonId())) {
            authService.reSetUserAuthentication();
        }
    }

    /**
     * Retrieves filtered list of users.
     *
     * @param status - user status
     * @param businessUnitId - network id
     * @param allBusinessUnits - search with at least one Business Unit enabled
     * @param company - organization
     * @param searchName - wildcard search name
     * @param searchValue - wildcard search value
     * @return list of filtered users
     * @throws ApplicationException if the wrong rest values of inappropriate wildcard pattern was entered.
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public List<UserListItemDTO> getUsersList(
            @RequestParam(value = "status", required = false) boolean status,
            @RequestParam(value = "businessUnitId", required = false) Long businessUnitId,
            @RequestParam(value = "allBusinessUnits", required = false) Boolean allBusinessUnits,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "searchName", required = false) UserSearchType searchName,
            @RequestParam(value = "searchValue", required = false) String searchValue) throws ApplicationException {

        String verifiedCompany = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(company);
        String verifiedsearchValue = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(searchValue);
        List<UserListItemBO> users = userService.searchUsers(SecurityUtils.getCurrentPersonId(),
                status ? UserStatus.ACTIVE : UserStatus.INACTIVE,
                        businessUnitId, allBusinessUnits, verifiedCompany, searchName, verifiedsearchValue);

        return listItemDTOBuilder.buildList(users);
    }

    /**
     * Get list of users by specified filter.
     *
     * @param filter
     *            user name filter
     * @param count
     *            max count of returned items
     * @return list of filtered users
     */
    @RequestMapping(value = "/searchByName", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> getUsersByName(@RequestParam("filter") String filter,
            @RequestParam(value = "count", required = false) Integer count) {

        return userService.findUsers(filter, count);
    }

    /**
     * Get possible parent organizations by name.
     *
     * @param name
     *            parent organization name
     * @param limit
     *            page size
     * @return Not null List.
     */
    @RequestMapping(value = "/parentOrganizationsByName", method = RequestMethod.GET)
    @ResponseBody
    public List<ParentOrganizationBO> getParentOrganizationsByName(@RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());

        return userService.getParentOrganizationByName(name, limit, SecurityUtils.getCurrentPersonId(), SecurityUtils.isPlsUser());
    }

    /**
     * Get user by id.
     *
     * @param personId
     *            id of user to get
     * @return user profile object
     * @throws EntityNotFoundException
     *             when user not found.
     */
    @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
    @ResponseBody
    public UserDTO getUserDetailsById(@PathVariable("personId") Long personId) throws EntityNotFoundException {
        if (!personId.equals(SecurityUtils.getCurrentPersonId())) {
            permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());
        }

        // TODO check that current user has permissions to manage required user/organization

        UserEntity user = userService.findByPersonId(personId);
        if (user == null) {
            throw new EntityNotFoundException("User '" + personId + "' was not found");
        }
        return userBuilder.buildDTO(user);
    }

    /**
     * Reset password for user.
     *
     * @param personId
     *            the id of user to reset password
     * @throws Exception
     *             if password can't be reset
     */
    @RequestMapping(value = "/{personId}/password/reset", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void resetPassword(@PathVariable("personId") Long personId) throws Exception {
        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());
        // TODO check that current user has permissions to manage required user/organization

        SecurityUtils.expireSessions(personId, sessionRegistry);
        userService.resetPasword(personId);
    }

    /**
     * Get user depending on filter value.
     *
     * @param filterValue
     *            value to filter.
     * @return filtered list of users.
     */
    @RequestMapping(value = "/filterEmail", method = RequestMethod.GET)
    @ResponseBody
    public List<UserEmailItemDTO> getUserEmailByFilter(@RequestParam("filterValue") String filterValue) {
        List<UserEmailBO> users = userService.findUsers(SecurityUtils.getCurrentPersonId(), filterValue);
        return emailItemDTOBuilder.buildList(users);
    }

    /**
     * Get current user's phone, email and full name details.
     *
     * @return current user's details
     */
    @RequestMapping(value = "/currentUserContactDetails", method = RequestMethod.GET)
    @ResponseBody
    public UserEmailPhoneItemDTO getCurrentUserContactDetails() {
        return emailPhoneItemDTOBuilder.buildDTO(userService.findByPersonId(SecurityUtils.getCurrentPersonId()));
    }

    /**
     * Get default notifications for users by location.
     *
     * @param customerId
     *            id of the customer.
     * @param locationId
     *            id of user location for which notifications will be searched.
     * @return user notifications.
     */
    @RequestMapping(value = "/userId/{userId}/notifications/{customerId}/location/{locationId}", method = RequestMethod.GET)
    @ResponseBody
    public List<UserNotificationsBO> getDefaultNotificationsForLocation(
            @PathVariable("customerId") Long customerId,
            @PathVariable("locationId") Long locationId) {
        return userService.getUserDefaultNotifications(customerId, locationId);
    }

    /**
     * Returns list of customers associated with current person and filtered by specified criteria.
     *
     * @param searchField
     *            field that should be used for search
     * @param criteria
     *            wildcard search criteria
     * @param userId
     *            id of edited user
     * @return list of customers
     * @throws ApplicationException
     *             if search criteria is invalid
     */
    @RequestMapping(value = "/customers/search", method = RequestMethod.GET)
    @ResponseBody
    public List<UserCustomerBO> searchCustomers(@RequestParam("searchField") CustomerUserSearchFieldBO searchField,
            @RequestParam("criteria") String criteria, @RequestParam(value = "userId", required = false) Long userId)
                    throws ApplicationException {

        permissionsService.checkCapability(Capabilities.USERS_PAGE_VIEW.name());
        String queryParam = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(criteria);
        return userService.getCustomersAssociatedWithUserByCriteria(SecurityUtils.getCurrentPersonId(), userId, searchField, queryParam);
    }

    /**
     * Get list Networks by personId.
     *
     * @return active Networks by logged in user.
     */
    @RequestMapping(value = "/activeNetworks", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> getActiveNetworks() {
        return userService.getUserActiveNetworks(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Return {@link UserAdditionalContactInfoBO} default additional contact information.
     *
     * @return default contact information
     */
    @RequestMapping(value = "/defaultContactInfo", method = RequestMethod.GET)
    @ResponseBody
    public UserAdditionalContactInfoBO getDefaultContactInfo() {
        return contactInfoService.getContactInfo(null);
    }

    /**
     * Get list of teams names.
     *
     * @return list of teams names
     */
    @RequestMapping(value = "/teams", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleValue> getTeams() {
        return userService.getTeams();
    }
    
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public List<UserSettingsDTO> getUserSettingsForLoggedUser() throws EntityNotFoundException {
        Long currentPersonId = SecurityUtils.getCurrentPersonId();
        List<UserSettingsEntity> userSettings = userService.getUserSettings(currentPersonId);
        return userSettingsDTOBuilder.buildList(userSettings);
    }
    
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void setUserSettingsById(@RequestBody UserSettingsDTO userSettingsDTO) throws EntityNotFoundException {
        userService.saveUserSettings(SecurityUtils.getCurrentPersonId(), userSettingsDTO.getKey(), userSettingsDTO.getValue());
    }

    private List<Long> extractOrgIds(UserDTO user) {
        ArrayList<Long> result = new ArrayList<Long>();
        result.add(user.getParentOrganization().getOrganizationId());
        for (CustomerUserDTO customerUser : user.getCustomers()) {
            result.add(customerUser.getCustomerId());
        }
        return result;
    }
}
