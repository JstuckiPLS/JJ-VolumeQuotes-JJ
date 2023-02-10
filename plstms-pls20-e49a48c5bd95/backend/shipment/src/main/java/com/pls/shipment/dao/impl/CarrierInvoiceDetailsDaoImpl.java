package com.pls.shipment.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;

/**
 * Implementation of {@link CarrierInvoiceDetailsDao}.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
@Repository
@Transactional
public class CarrierInvoiceDetailsDaoImpl extends AbstractDaoImpl<CarrierInvoiceDetailsEntity, Long> implements CarrierInvoiceDetailsDao {
 
    @Value("${schedulers.archiveOldVendorBillDays}")
    private int days;

    @SuppressWarnings("unchecked")
    @Override
    public List<CarrierInvoiceDetailsListItemBO> getUnmatched() {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_GET_UNMATCHED);
        query.setParameter("days", days);
        query.setResultTransformer(new AliasToBeanResultTransformer(CarrierInvoiceDetailsListItemBO.class));

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CarrierInvoiceDetailsListItemBO> getArchived() {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_GET_ARCHIVED);
        query.setParameter("days", days);
        query.setResultTransformer(new AliasToBeanResultTransformer(CarrierInvoiceDetailsListItemBO.class));

        return query.list();
    }

    @Override
    public void updateStatusAndMatchedLoad(Long carrierInvoiceId, Status status, Long matchedLoadId) {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_UPDATE_STATUS_AND_MATCHED_LOAD);
        query.setParameter("carrierInvoiceId", carrierInvoiceId);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.setParameter("modifiedDate", new Date());
        query.setParameter("matchedLoadId", matchedLoadId);
        query.executeUpdate();
    }

    @Override
    public void updateStatus(Long carrierInvoiceId, Status status, Date modifiedDate, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_ARCHIVE);
        query.setParameter("carrierInvoiceId", carrierInvoiceId);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameter("modifiedDate", modifiedDate);
        query.executeUpdate();
    }

    @Override
    public void updateStatuses(List<Long> carrierInvoiceIds, Status status, Date modifiedDate, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_ARCHIVE_LIST);
        query.setParameterList("carrierInvoiceIds", carrierInvoiceIds);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameter("modifiedDate", modifiedDate);
        query.executeUpdate();
    }

    @Override
    public void archiveOldUnmatched() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("days", days);
        executeNamedQueryUpdate(CarrierInvoiceDetailsEntity.Q_ARCHIVE_UNMATCHED, parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CarrierInvoiceDetailsEntity> getEDIVendorBillsForLoad(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(CarrierInvoiceDetailsEntity.Q_GET_EDI_FOR_LOAD);
        query.setLong("loadId", loadId);
        return query.list();
    }
}
