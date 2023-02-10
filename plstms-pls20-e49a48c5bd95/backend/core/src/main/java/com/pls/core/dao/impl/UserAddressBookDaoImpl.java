package com.pls.core.dao.impl;

import static com.pls.core.domain.address.UserAddressBookEntity.Q_ADDRESS_AUTOCOMPLETE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.shared.Status;
/**
 * Implementation of {@link com.pls.core.dao.UserAddressBookDao}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Repository
@Transactional
public class UserAddressBookDaoImpl extends AbstractDaoImpl<UserAddressBookEntity, Long> implements UserAddressBookDao {
    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> getCustomerAddressBookForUser(Long orgId, Long personId, boolean filterWarnings, List<AddressType> types) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_GET_CUSTOMER_ADDRESS_BOOK_FOR_USER);
        query.setParameter("orgId", orgId);
        query.setParameter("personId", personId);
        query.setParameter("filterWarnings", filterWarnings ? 1 : 0);
        query.setParameterList("types", types);

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> getAddressBooksForFreightBill(Long customerId, Long userId, String filter) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_GET_ADDRESS_BOOK_FOR_FREIGHT_BILL);
        query.setLong("orgId", customerId);
        query.setParameter("personId", userId, LongType.INSTANCE);
        query.setString("filter", "%" + StringUtils.upperCase(filter.trim()) + "%");
        query.setMaxResults(10);
        return query.list();
    }

    @Override
    public boolean deleteUserAddressBookEntry(Long addressId, Long personId) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_DELETE_ADDRESS_BOOK_ENTRY);
        query.setParameter("id", addressId);
        query.setParameter("personId", personId);

        return query.executeUpdate() != 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigDecimal getNextGeneratedAddressCode() {
        return findUniqueObjectByNamedQuery(UserAddressBookEntity.Q_GET_NEXT_ADDRESS_CODE);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigDecimal getNextGeneratedAddressNameNumber() {
        return findUniqueObjectByNamedQuery(UserAddressBookEntity.Q_GET_NEXT_ADDR_NAME_CODE);
    }

    @Override
    public boolean isAddressUnique(Long orgId, String addressName, String addressCode) {
        FlushMode flushMode = getCurrentSession().getFlushMode();
        getCurrentSession().setFlushMode(FlushMode.MANUAL);
        try {
            return (Long) getCurrentSession().createQuery(
                    "select count(a) from UserAddressBookEntity a where upper(a.addressName)=:name and upper(a.addressCode)=:code "
                                    + "and a.orgId=:id and a.status='A'")
                    .
                    setString("name", StringUtils.defaultString(addressName).toUpperCase(Locale.getDefault())).
                    setString("code", StringUtils.defaultString(addressCode).toUpperCase(Locale.getDefault())).
                    setLong("id", orgId).uniqueResult() == 0;
        } finally {
            getCurrentSession().setFlushMode(flushMode);
        }
    }


    @Override
    public boolean checkAddressCodeExists(Long orgId, String addressCode) {
        // These workarounds with flush mode are needed to avoid pre-commits of UserAddressBookEntity object
        // for which this method is called.
        FlushMode flushMode = getCurrentSession().getFlushMode();
        getCurrentSession().setFlushMode(FlushMode.MANUAL);
        try {
            return (Long) getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_CHECK_ADDRESS_CODE_EXISTS)
                    .setString("code", addressCode.toUpperCase(Locale.getDefault())).setLong("id", orgId)
                    .uniqueResult() > 0;
        } finally {
            getCurrentSession().setFlushMode(flushMode);
        }
    }

    @Override
    public boolean checkAddressNameExists(Long orgId, String addressName) {
        // These workarounds with flush mode are needed to avoid pre-commits of UserAddressBookEntity object for which this method is called.
        FlushMode flushMode = getCurrentSession().getFlushMode();
        getCurrentSession().setFlushMode(FlushMode.MANUAL);
        try {
            return (Long) getCurrentSession().createQuery(
                            "select count(a) from UserAddressBookEntity a where upper(a.addressName)=:name and a.orgId=:id and a.status='A'")
                    .
                    setString("name", addressName.toUpperCase(Locale.getDefault())).
                    setLong("id", orgId).uniqueResult() > 0;
        } finally {
            getCurrentSession().setFlushMode(flushMode);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> findCustomerAddressByCountryAndZip(Long orgId, Long personId, String countryCode,
            String zip, String city, List<AddressType> types) {
        if (orgId == null || StringUtils.isBlank(countryCode) || StringUtils.isBlank(zip) || StringUtils.isBlank(city)) {
            throw new IllegalArgumentException(String.format(
                    "One of the parameters is null or blank " + "[orgId: %d, countryCode: %s, zip: %s, city: %s]", orgId, countryCode, zip, city));
        }

        return getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_GET_BY_ZIP)
                .setLong("orgId", orgId)
                .setString("country", countryCode)
                .setString("zip", zip)
                .setString("city", city)
                .setLong("personId", personId)
                .setParameterList("types", types)
                .list();
    }

    @Override
    public UserAddressBookEntity getUserAddressBookEntryById(Long id) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_FIND_BY_ID);
        query.setParameter("id", id);
        return (UserAddressBookEntity) query.uniqueResult();
    }

    @Override
    public UserAddressBookEntity getUserAddressBookEntryByNameAndCode(String addressName, String addressCode,
            Long customerId) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_FIND_BY_NAME_AND_CODE);
        query.setString("addressName", StringUtils.upperCase(addressName));
        query.setString("addressCode", StringUtils.upperCase(addressCode));
        query.setLong("orgId", customerId);
        query.setMaxResults(1);
        return (UserAddressBookEntity) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> searchUserAddressBookEntries(String address1, String address2, String city, String zip,
            String countryCode, String stateCode, Long customerId) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_SEARCH_ADDRESSES);
        query.setString("address1", address1);
        query.setString("address2", address2);
        query.setString("city", city);
        query.setString("zip", zip);
        query.setString("countryCode", countryCode);
        query.setString("stateCode", stateCode);
        query.setLong("customerId", customerId);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> listFilteredByWord(Long orgId, Long personId, String filterWord) {
        Criteria criteria = getCriteria();
        criteria.createAlias("address", "addr", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("customer", "customer");

        criteria.add(Restrictions.eq("orgId", orgId));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        criteria.add(Restrictions.or(Restrictions.isNull("personId"), Restrictions.eq("personId", personId)));
        criteria.add(Restrictions.or(Restrictions.ilike("contactName", filterWord, MatchMode.ANYWHERE),
                Restrictions.ilike("addr.address1", filterWord, MatchMode.ANYWHERE),
                Restrictions.ilike("addr.address2", filterWord, MatchMode.ANYWHERE),
                Restrictions.ilike("customer.name", filterWord, MatchMode.ANYWHERE)));
        return criteria.list();
    }

    @Override
    public void persistWithFlush(UserAddressBookEntity entity) {
        persist(entity);
        getCurrentSession().flush();
        getCurrentSession().refresh(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> getAddressesByName(Long orgId, Long personId, String addressName, boolean strictSearch) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("orgId", orgId));
        criteria.add(Restrictions.or(Restrictions.isNull("personId"), Restrictions.eq("personId", personId)));
        MatchMode matchMode = strictSearch ? MatchMode.EXACT : MatchMode.ANYWHERE;
        criteria.add(Restrictions.ilike("addressName", addressName, matchMode));
        return criteria.list();
    }

    @Override
    public void resetAndArchived(UserAddressBookEntity address, Long userId) {
        Session currentSession = getCurrentSession();
        currentSession.evict(address);
        UserAddressBookEntity oldAddress = (UserAddressBookEntity) currentSession.get(UserAddressBookEntity.class, address.getId());
        oldAddress.setStatus(Status.INACTIVE);
        oldAddress.getModification().setModifiedBy(userId);
        oldAddress.getModification().setModifiedDate(new Date());
        currentSession.update(oldAddress);
        currentSession.flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserAddressBookEntity> listSuggestions(Long customerId, String query) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(query.toUpperCase());
        builder.append('%');

        Query suggestionsQuery = getCurrentSession().getNamedQuery(Q_ADDRESS_AUTOCOMPLETE);
        suggestionsQuery.setParameter("customerId", customerId);
        suggestionsQuery.setParameter("query", '%' + query.toUpperCase() + '%');
        return suggestionsQuery.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAddressBookEntity> listSuggestions(Long orgId, String addressName, boolean strictSearch) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("orgId", orgId));
        MatchMode matchMode = strictSearch ? MatchMode.EXACT : MatchMode.ANYWHERE;
        criteria.add(Restrictions.ilike("addressName", addressName, matchMode));
        return criteria.list();
    }

    @Override
    public UserAddressBookEntity getCustomerAddressBookEntryByCode(Long orgId, String code) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.Q_FIND_BY_ORG_AND_CODE);
        query.setParameter("orgId", orgId);
        query.setParameter("addressCode", code);
        return (UserAddressBookEntity) query.uniqueResult();
    }

    @Override
    @Transactional
    public void resetDefaultAddressesForCustomer(Long orgId) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.RESET_DEFAULT_ADDRESS_FOR_CUSTOMER);
        query.setParameter("orgId", orgId);
        query.executeUpdate();
    }

    @Override
    public UserAddressBookEntity getDefaultFreightBillPayTo(Long customerId) {
        Query query = getCurrentSession().getNamedQuery(UserAddressBookEntity.GET_DEFAULT_FREIGHT_BILL_PAY_TO_ADDRESS);
        query.setParameter("orgId", customerId);
        return (UserAddressBookEntity) query.uniqueResult();
    }
}
