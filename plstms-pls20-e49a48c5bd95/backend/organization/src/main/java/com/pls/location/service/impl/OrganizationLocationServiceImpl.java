package com.pls.location.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserNotificationsEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.ContactInfoService;
import com.pls.core.shared.Status;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.location.service.OrganizationLocationService;

/**
 * Service implementation for {@link OrganizationLocationService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class OrganizationLocationServiceImpl implements OrganizationLocationService {

    @Autowired
    private OrganizationLocationDao dao;

    @Autowired
    private CustomerUserDao customerUserDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ContactInfoService contactInfoService;

    @Override
    public List<AssociatedCustomerLocationBO> getAssociatedCustomerLocations(Long customerId, Long currentUserId, Long userId) {
        return customerDao.getAssociatedCustomerLocations(customerId, currentUserId, userId);
    }

    @Override
    public List<ShipmentLocationBO> getShipmentLocations(Long customerId, Long personId) {
        return dao.getShipmentLocations(customerId, personId);
    }

    @Override
    public List<CustomerLocationListItemBO> getCustomerLocations(Long organizationId) {
        return dao.getCustomerLocations(organizationId);
    }

    @Override
    public OrganizationLocationEntity getOrganizationLocation(Long locationId) {
        return dao.find(locationId);
    }

    @Override
    public UserAdditionalContactInfoBO getAccountExecutiveByLocationId(Long locationId) {
        try {
            UserEntity user = dao.get(locationId).getActiveAccountExecutive().getUser();
            return contactInfoService.getContactInfo(user);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return contactInfoService.getContactInfoForCurrentUser();
        }
    }

    @Override
    public void saveOrganizationLocation(OrganizationLocationEntity entity, Long oldPersonId) {
        autoAssignUserToCustomer(entity, oldPersonId);
        dao.saveOrUpdate(entity);
    }

    private void autoAssignUserToCustomer(OrganizationLocationEntity location, Long oldPersonId) {
        AccountExecutiveEntity newAE = location.getActiveAccountExecutive();
        if (location.getId() != null && isAccountExecutiveChanged(newAE, oldPersonId)) {
            unAssignUserFromCustomer(oldPersonId, location);
        }

        if (newAE != null) {
            assignUserToCustomer(newAE.getUser().getPersonId(), location.getOrganization().getId());
        }
    }

    private boolean isAccountExecutiveChanged(AccountExecutiveEntity newAE, Long oldPersonId) {
        boolean result = true;
        if (oldPersonId == null || (newAE != null && newAE.getUser().getPersonId().equals(oldPersonId))) {
            result = false;
        }

        return result;
    }

    private void unAssignUserFromCustomer(Long oldPersonId, OrganizationLocationEntity location) {
        if (!dao.isAEExistsForLocationsExcludeSpecified(oldPersonId, location.getOrganization().getId(), location.getId())) {
            CustomerUserEntity oldUserEntity = customerUserDao.findByPersonIdOrgIdLocationId(oldPersonId, location.getOrganization().getId());
            unAssignUser(oldUserEntity);
        }
    }

    private void unAssignUser(CustomerUserEntity entity) {
        if (entity != null) {
            entity.setStatus(Status.INACTIVE);
            for (UserNotificationsEntity notification : entity.getNotifications()) {
                notification.setStatus(Status.INACTIVE);
            }
            customerUserDao.saveOrUpdate(entity);
        }
    }

    private void assignUserToCustomer(Long personId, Long customerId) {
        CustomerUserEntity entity = customerUserDao.findByPersonIdOrgIdLocationId(personId, customerId);
        if (entity != null) {
            activateExistingCustomerUser(entity);
        } else {
            createCustomerUser(personId, customerId);
        }
    }

    private void activateExistingCustomerUser(CustomerUserEntity entity) {
        if (entity.getStatus().equals(Status.INACTIVE)) {
            entity.setStatus(Status.ACTIVE);
            customerUserDao.persist(entity);
        }
    }

    private void createCustomerUser(Long personId, Long customerId) {
        CustomerUserEntity entity = new CustomerUserEntity();
        UserEntity user = new UserEntity();
        user.setId(personId);
        entity.setUser(user);
        CustomerEntity customer = new CustomerEntity();
        customer.setId(customerId);
        entity.setCustomer(customer);

        customerUserDao.persist(entity);
    }
}
