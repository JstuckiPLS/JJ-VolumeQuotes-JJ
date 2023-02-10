package com.pls.core.dao.impl.address;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.address.AddressDao;
import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.bo.ContactInfoBO;
import com.pls.core.domain.bo.ContactInfoSetBO;
import com.pls.core.shared.AddressVO;

/**
 * {@link com.pls.core.dao.address.AddressDao} implementation.
 *
 * @author Gleb Zgonikov
 */
@Repository
@Transactional
public class AddressDaoImpl extends AbstractDaoImpl<AddressEntity, Long> implements AddressDao {

    @Override
    public ContactInfoSetBO findContactInfoSet(Long userId) {
        ContactInfoSetBO result = new ContactInfoSetBO();
        result.setCustomerRep(findCustomerRep());
        result.setPlsCorporate(findPlsCorporate());
        result.setSalesRep(findSalesRep());
        result.setTerminal(findTerminal());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AddressEntity> findAddressesByAddressVO(AddressVO address) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("city", address.getCity().toUpperCase()));
        criteria.add(Restrictions.eq("stateCode", address.getStateCode().toUpperCase()));
        criteria.add(Restrictions.eq("zip", address.getPostalCode().toUpperCase()));

        if (address.getAddress1() != null && !"".equals(address.getAddress1().trim())) {
            criteria.add(Restrictions.eq("address1", address.getAddress1().toUpperCase()));
        }

        if (address.getAddress2() != null && !"".equals(address.getAddress2().trim())) {
            criteria.add(Restrictions.eq("address2", address.getAddress2().toUpperCase()));
        }

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RouteEntity findRouteByAddresses(Long originAddressId, Long destinationAddressId) {
        Query query = getCurrentSession().getNamedQuery(RouteEntity.Q_BY_ADDRESS);
        query.setLong("originAddressId", originAddressId);
        query.setLong("destinationAddressId", destinationAddressId);
        List<RouteEntity> routes = query.list();
        return routes.isEmpty() ? null : routes.get(0);
    }

    private ContactInfoBO findTerminal() {
        ContactInfoBO bo = new ContactInfoBO();
        bo.setEmail("terminals.com");
        bo.setFax("456-456-456");
        bo.setName("Terminal");
        bo.setPhone("654-654-654");
        return bo;
    }

    private ContactInfoBO findSalesRep() {
        ContactInfoBO bo = new ContactInfoBO();
        bo.setEmail("sales.rep@pls.com");
        bo.setFax("345-345-345");
        bo.setName("Sales Rep");
        bo.setPhone("543-543-543");
        return bo;
    }

    private ContactInfoBO findPlsCorporate() {
        ContactInfoBO bo = new ContactInfoBO();
        bo.setEmail("pls.corporate@pls.com");
        bo.setFax("123-123-123");
        bo.setName("Pls Corporate");
        bo.setPhone("321-321-321");
        return bo;
    }

    private ContactInfoBO findCustomerRep() {
        ContactInfoBO bo = new ContactInfoBO();
        bo.setEmail("customer.rep@pls.com");
        bo.setFax("234-234-234");
        bo.setName("Customer Rep");
        bo.setPhone("432-432-432");
        return bo;
    }
}
