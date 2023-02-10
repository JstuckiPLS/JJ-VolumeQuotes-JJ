package com.pls.ltlrating.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPalletPricingDetailsDao;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlZonesEntity;

/**
 * Implementation of {@link LtlPalletPricingDetailsDao}.
 *
 * @author Artem Arapov
 *
 */
@Transactional
@Repository
public class LtlPalletPricingDetailsDaoImpl extends AbstractDaoImpl<LtlPalletPricingDetailsEntity, Long> implements
        LtlPalletPricingDetailsDao {

    @Autowired
    private LtlZonesDao zonesDao;

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlPalletPricingDetailsEntity> findByStatusAndDetailId(Long detailId, Status status) {
        return getCurrentSession()
                .createCriteria(LtlPalletPricingDetailsEntity.class)
                .add(Restrictions.and(Restrictions.eq("status", status),
                        Restrictions.eq("profileDetailId", detailId))).addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlPalletPricingDetailsEntity> findActiveAndEffective(Long detailId) {
        return getCurrentSession()
                .createCriteria(LtlPalletPricingDetailsEntity.class)
                .add(Restrictions.and(Restrictions.eq("profileDetailId", detailId),
                        Restrictions.eq("status", Status.ACTIVE),
                        Restrictions.or(Restrictions.isNull("expDate"),
                        Restrictions.gt("expDate", new Date())))).addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public void updateStatus(Long priceDetailId, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPalletPricingDetailsEntity.UPDATE_STATUS_STATEMENT);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameter("id", priceDetailId);
        query.executeUpdate();
    }

    @Override
    public void inactivateByDetailId(Long detailId, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPalletPricingDetailsEntity.INACTIVATE_BY_PROFILE_STATEMENT);
        query.setParameter("id", detailId);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public void clone(Long copyFromDetailId, Long copyToDetailId) {
        List<LtlPalletPricingDetailsEntity> sourceList = this.findActiveAndEffective(copyFromDetailId);
        List<LtlPalletPricingDetailsEntity> clonedList = cloneList(sourceList);

        for (LtlPalletPricingDetailsEntity item : clonedList) {
            item.setProfileDetailId(copyToDetailId);

            // Get the zone ids
            item.setZoneFrom(zonesDao.getMatchingZoneByName(copyToDetailId, item.getZoneFrom()).getId());
            item.setZoneTo(zonesDao.getMatchingZoneByName(copyToDetailId, item.getZoneTo()).getId());

            this.saveOrUpdate(item);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlPalletPricingDetailsEntity> findAllCspChildsCopyedFrom(Long copiedFrom) {
        Query query = getCurrentSession().getNamedQuery(LtlPalletPricingDetailsEntity.FIND_CSP_ENTITY_BY_COPIED_FROM);
        query.setParameter("id", copiedFrom);

        return query.list();
    }

    @Override
    public void updateStatusInCSPByCopiedFrom(Long copiedFromId, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(LtlPalletPricingDetailsEntity.UPDATE_CSP_STATUS_STATEMENT);
        query.setParameter("ownerId", copiedFromId);
        query.setParameter("status", status.getCode());
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    private List<LtlPalletPricingDetailsEntity> cloneList(List<LtlPalletPricingDetailsEntity> source) {
        List<LtlPalletPricingDetailsEntity> cloneList = new ArrayList<LtlPalletPricingDetailsEntity>(source.size());

        for (LtlPalletPricingDetailsEntity item : source) {
            LtlPalletPricingDetailsEntity clone = new LtlPalletPricingDetailsEntity(item);
            cloneList.add(clone);
        }

        return cloneList;
    }

    @Override
    public boolean areZonesMissing(Long copyFromProfileId, Long copyToProfileId) {
        return getCurrentSession().getNamedQuery(LtlZonesEntity.GET_MISSING_ZONES)
                .setParameter("fromProfileId", copyFromProfileId)
                .setParameter("toProfileId", copyToProfileId)
                .setMaxResults(1)
                .uniqueResult() != null;
    }
}
