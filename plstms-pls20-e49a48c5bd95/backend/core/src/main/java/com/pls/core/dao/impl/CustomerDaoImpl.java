package com.pls.core.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.user.UserEntity;

/**
 * {@link com.pls.core.dao.CustomerDao} implementation.
 * 
 * @author Denis Zhupinsky
 */
@Repository
@Transactional
public class CustomerDaoImpl extends AbstractDaoImpl<CustomerEntity, Long> implements CustomerDao {

    @Override
    public CustomerEntity findCustomerByName(String name) {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("name", name);
        List<CustomerEntity> customers = findByNamedQuery(CustomerEntity.Q_BY_NAME, parameters);
        if (!customers.isEmpty()) {
            return customers.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean checkCustomerNameExists(String name) {
        return findCustomerByName(name) != null;
    }

    @Override
    public boolean checkCustomerNameExists(Long customerId, Long networkId, String name) {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("name", name);
        parameters.put("customerId", customerId);
        parameters.put("networkId", networkId);
        return !findByNamedQuery(CustomerEntity.Q_BY_NAME_WITHOUT_ID, parameters).isEmpty();
    }

    @Override
    public List<CustomerEntity> findCustomersByFederalTaxId(String federalTaxId) {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("federalTaxId", federalTaxId);
        return findByNamedQuery(CustomerEntity.Q_BY_FEDERAL_TAX_ID, parameters);
    }

    @Override
    public boolean checkCustomerFederalTaxIdExists(String federalTaxId) {
        return !findCustomersByFederalTaxId(federalTaxId).isEmpty();
    }

    @Override
    public boolean checkCustomerExists(Long customerId) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_COUNT_CUSTOMERS_BY_ID);
        query.setParameter("customerId", customerId);
        query.setMaxResults(1);
        return !Long.valueOf(0).equals(query.uniqueResult());
    }

    @Override
    public CustomerEntity update(CustomerEntity customer) {
        getCurrentSession().merge(customer);
        getCurrentSession().flush();
        getCurrentSession().clear();
        getCurrentSession().refresh(customer);
        return customer;
    }

    @Override
    public ProductListPrimarySort getProductListPrimarySort(Long customerId) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_PRODUCT_LIST_PRIMARY_SORT);
        query.setParameter("id", customerId);
        return (ProductListPrimarySort) query.uniqueResult();
    }

    @Override
    public void updateProductListPrimarySort(Long customerId, ProductListPrimarySort productListPrimarySort) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_UPDATE_PRODUCT_LIST_PRIMARY_SORT);
        query.setParameter("sort", productListPrimarySort);
        query.setParameter("id", customerId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleValue> findAccountExecutiveList(String filter, int count) {
        return getCurrentSession().getNamedQuery(UserEntity.Q_FIND_ACCOUNT_EXECUTIVES_USERS_LIST)
                .setParameter("filter", "%" + filter + "%")
                .setMaxResults(count)
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ParentOrganizationBO> findCustomersForUserByName(Long personId, String name, int limit) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_ASSOCIATED_WITH_USER_BY_NAME)
                .setLong("personId", personId)
                .setString("name", "%" + StringUtils.upperCase(name) + "%")
                .setMaxResults(limit)
                .setResultTransformer(new AliasToBeanResultTransformer(ParentOrganizationBO.class))
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleValue> getCustomerIdNameTuplesByName(String name, int limit, int offset, Boolean status, Long personId) {
        OrganizationStatus userStatus = status ? null : OrganizationStatus.ACTIVE;
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_FIND_CUSTOMERS_BY_NAME)
                .setParameter("name", "%" + name.toLowerCase(Locale.getDefault()) + "%")
                .setParameter("status", userStatus)
                .setParameter("personId", personId)
                .setFirstResult(offset).setMaxResults(limit).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleValue> getAccountExecutives() {
        SQLQuery query = (SQLQuery) getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_ALL_ACCOUNT_EXECUTIVES)
                .setLong("plsOrgId", SecurityDaoImpl.DEFAULT_PLS_ORG_ID)
                .setString("capability", Capabilities.ACCOUNT_EXECUTIVE.name())
                .setResultTransformer(new AliasToBeanResultTransformer(SimpleValue.class));
        query.addScalar("id", StandardBasicTypes.LONG);
        query.addScalar("name", StandardBasicTypes.STRING);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomerCreditInfoBO getCustomerCreditInfo(Long customerId) {
         List<CustomerCreditInfoBO> customerCreditInfoList = getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_CREDIT_INFO).
                setParameter("orgId", customerId).setResultTransformer(new BasicTransformerAdapter() {
            private static final long serialVersionUID = -272826901248169183L;

            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                CustomerCreditInfoBO customerCreditInfoBO = new CustomerCreditInfoBO();
                customerCreditInfoBO.setTaxId((String) tuple[0]);
                if (tuple[1] != null) {
                    customerCreditInfoBO.setCreditLimit(new BigDecimal((Long) tuple[1]));
                }
                return customerCreditInfoBO;
            }
        }).list();
        return customerCreditInfoList.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerListItemBO> getByStatusAndName(Long personId, OrganizationStatus status, String name, String businessUnitName) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_BY_STATUS_AND_NAME)
                .setParameter("personId", personId)
                .setParameter("status", status).setParameter("name", StringUtils.lowerCase(name))
                .setParameter("businessUnitName", StringUtils.trimToNull(businessUnitName))
                .setResultTransformer(Transformers.aliasToBean(CustomerListItemBO.class))
                .list();
    }

    @Override
    public boolean isCustomerChangedForFinance(CustomerEntity customer) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_IS_CHANGED).setParameter("name", customer.getName())
                .setParameter("taxId", customer.getFederalTaxId()).setParameter("id", customer.getId());
        Object uniqueResult = query.uniqueResult();
        return uniqueResult == Boolean.TRUE;
    }

    @Override
    public Boolean isActiveCustomer(Long customerId) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_CHECK_CUSTOMER_BY_ACTIVE_STATUS);
        query.setParameter("customerId", customerId);
        return query.list().size() > 0;
    }

    @Override
    public CustomerEntity findCustomerByEDINumber(String ediNumber) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_FIND_CUSTOMER_BY_EDI_NUMBER);
        query.setParameter("ediNumber", ediNumber);
        return (CustomerEntity) query.uniqueResult();
    }

    @Override
    public boolean checkEDINumberExists(String ediNumber) {
        Query query = getCurrentSession().getNamedQuery(CustomerEntity.Q_CHECK_EDI_NUMBER_EXISTS);
        query.setParameter("ediNumber", ediNumber);
        return query.list().size() > 0;
    }

    @Override
    public Long getCreditLimit(Long customerId) {
        return (Long) getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_CREDIT_LIMIT)
                .setParameter("customerId", customerId)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AssociatedCustomerLocationBO> getAssociatedCustomerLocations(Long customerId, Long currentUserId, Long userId) {
        return getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_LOCATIONS_FOR_ASSOCIATED_CUSTOMER)
                .setParameter("customerId", customerId)
                .setParameter("currentUserId", currentUserId)
                .setParameter("userId", userId)
                .setResultTransformer(new AliasToBeanResultTransformer(AssociatedCustomerLocationBO.class))
                .list();
    }

    @Override
    public Boolean getCreditLimitRequired(Long customerId) {
        return (Boolean) getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_CREDIT_LIMIT_REQUIRED)
                .setLong("customerId", customerId)
                .uniqueResult();
    }

    @Override
    public String getInternalNote(Long customerId) {
        return (String) getCurrentSession().getNamedQuery(CustomerEntity.Q_GET_INTERNAL_NOTE)
                .setLong("customerId", customerId)
                .uniqueResult();
    }

    @Override
    public Boolean isPrintBarcode(Long customerId) {
        return (Boolean) getCurrentSession().getNamedQuery(CustomerEntity.Q_IS_PRINT_BARCODE)
                .setLong("customerId", customerId).uniqueResult();
    }
}
