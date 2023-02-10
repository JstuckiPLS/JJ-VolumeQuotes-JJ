package com.pls.shipment.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.shipment.domain.bo.ShipmentNotificationSourceItemBo;
import com.pls.shipment.service.ShipmentNotificationSourceService;

/**
 * Service implementation provides sources for shipment notification.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class ShipmentNotificationSourceServiceImpl implements ShipmentNotificationSourceService {

    @Autowired
    private CustomerUserDao customerUserDao;

    @Autowired
    private UserAddressBookDao userAddressBookDao;

    @Override
    public List<ShipmentNotificationSourceItemBo> getShipmentNotificationSourceItems(Long customerId, Long personId) {
        List<ShipmentNotificationSourceItemBo> result = new ArrayList<ShipmentNotificationSourceItemBo>();
        prepareNotificationForOrganization(result, customerUserDao.getActive(customerId));
        prepareNotificationForAddress(result, userAddressBookDao.getCustomerAddressBookForUser(customerId, personId, true,
                Arrays.asList(AddressType.values())));
        return result;
    }

    private void prepareNotificationForAddress(List<ShipmentNotificationSourceItemBo> result, List<UserAddressBookEntity> sources) {
        for (UserAddressBookEntity entity : sources) {
            ShipmentNotificationSourceItemBo bo = new ShipmentNotificationSourceItemBo();
            bo.setId(entity.getId());
            bo.setEmail(entity.getEmail());
            bo.setContactName(entity.getContactName());
            AddressEntity addressEntity = entity.getAddress();
            if (addressEntity != null) {
                bo.setOrigin(addressEntity.getCity() + ", " + addressEntity.getStateCode() + ", " + addressEntity.getZip());
            }
            bo.setPhone(entity.getPhone());
            result.add(bo);
        }
    }

    private void prepareNotificationForOrganization(List<ShipmentNotificationSourceItemBo> result, List<CustomerUserEntity> sources) {
        for (CustomerUserEntity entity : sources) {
            ShipmentNotificationSourceItemBo bo = new ShipmentNotificationSourceItemBo();
            bo.setId(entity.getId());
            UserEntity user = entity.getUser();
            bo.setEmail(user.getEmail());
            bo.setContactName(UserNameBuilder.buildFullName(user.getFirstName(), user.getLastName()));
            if (entity.getCustomer() != null) {
                bo.setName(entity.getCustomer().getName());
            }
            bo.setPhone(entity.findFirstActivePhone(PhoneType.VOICE));
            if (entity.getAddress() != null && entity.getAddress().getAddress() != null) {
                AddressEntity addressEntity = entity.getAddress().getAddress();
                bo.setOrigin(addressEntity.getCity() + ", " + addressEntity.getStateCode() + ", " + addressEntity.getZip());
            }
            result.add(bo);
        }
    }
}
