package com.pls.extint.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.extint.dao.ApiLogDao;
import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.shared.ApiCriteriaCO;
import com.pls.extint.shared.ApiLogVO;

/**
 * Implementation class of {@link ApiLogDao}.
 * 
 * @author Pavani Challa
 * 
 */
@Transactional
@Repository
public class ApiLogDaoImpl extends AbstractDaoImpl<ApiLogEntity, Long> implements ApiLogDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<ApiLogVO> findByCriteria(ApiCriteriaCO criteriaCO) {
        Criteria criteria = getCriteria();
        addProjections(criteria);

        if (criteriaCO.getFromDate() != null && criteriaCO.getToDate() != null) {
            criteria.add(Restrictions.between("modification.createdDate", criteriaCO.getFromDate(), criteriaCO.getToDate()));
        }
        if (criteriaCO.getBol() != null) {
            criteria.add(Restrictions.eq("bol", criteriaCO.getBol()));
        }
        if (criteriaCO.getLoadId() != null) {
            criteria.add(Restrictions.eq("loadId", criteriaCO.getLoadId()));
        }
        if (criteriaCO.getShipperReferenceNumber() != null) {
            criteria.add(Restrictions.eq("shipperReferenceNumber", criteriaCO.getShipperReferenceNumber()));
        }
        if (criteriaCO.getCarrierReferenceNumber() != null) {
            criteria.add(Restrictions.eq("carrierReferenceNumber", criteriaCO.getCarrierReferenceNumber()));
        }

        return (List<ApiLogVO>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApiLogVO> getAllLogs() {
        Criteria criteria = getCriteria();
        addProjections(criteria);

        return (List<ApiLogVO>) criteria.list();
    }

    private void addProjections(Criteria criteria) {
        criteria.setProjection(
                Projections.distinct(Projections.projectionList().add(Projections.property("id"), "id")
                        .add(Projections.property("apiTypeId"), "apiTypeId").add(Projections.property("loadId"), "loadId")
                        .add(Projections.property("bol"), "bol").add(Projections.property("carrierReferenceNumber"), "carrierReferenceNumber")
                        .add(Projections.property("shipperReferenceNumber"), "shipperReferenceNumber")
                        .add(Projections.property("errorMsg"), "errorMsg").add(Projections.property("status"), "status")
                        .add(Projections.property("trackingStatus"), "trackingStatus")
                        .add(Projections.property("modification.createdDate"), "createdDate")
                        .add(Projections.property("modification.createdBy"), "createdBy")
                        .add(Projections.property("modification.modifiedDate"), "modifiedDate")
                        .add(Projections.property("modification.modifiedBy"), "modifiedBy"))).setResultTransformer(
                Transformers.aliasToBean(ApiLogVO.class));
    }

    @Override
    public String getLatestTrackingNote(Long loadId, Long apiLogId) {
        StringBuilder sql = new StringBuilder(" select al.tracking_status \"trackingStatus\" from api_log al ")
            .append(" inner join api_types at on at.api_type_id = al.api_type_id and at.api_category = 'TRACKING' ")
            .append(" where al.load_id = :loadId and al.api_log_id != :apiLogId order by al.api_log_id desc");

        Query query = getCurrentSession().createSQLQuery(sql.toString()).setParameter("loadId", loadId).setParameter("apiLogId", apiLogId);
        query.setMaxResults(1);

        return (String) query.uniqueResult();
    }

}
