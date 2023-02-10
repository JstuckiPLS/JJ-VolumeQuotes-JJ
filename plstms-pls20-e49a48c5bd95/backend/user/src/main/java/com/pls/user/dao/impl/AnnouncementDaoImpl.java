package com.pls.user.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.user.dao.AnnouncementDao;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * Implementation of {@link AnnouncementDao}.
 * 
 * @author Nalapko Alexander
 * 
 */
@Transactional
@Repository
public class AnnouncementDaoImpl extends AbstractDaoImpl<AnnouncementEntity, Long> implements AnnouncementDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<AnnouncementBO> getAll(Date from, Date to, List<AnnouncementStatus> statuses) {
        Query query = getCurrentSession().getNamedQuery(AnnouncementEntity.Q_GET_BY_STATUS_AND_PERIOD);
        query.setParameter("from", from, DateType.INSTANCE);
        query.setParameter("to", to, DateType.INSTANCE);
        query.setParameterList("statuses", statuses);
        query.setParameter("personId", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);
        return query.setResultTransformer(new AliasToBeanResultTransformer(AnnouncementBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AnnouncementBO> getAllUnpublished() {
        Query query = getCurrentSession().getNamedQuery(AnnouncementEntity.Q_GET_UNPUBLISHED);
        return query.setResultTransformer(new AliasToBeanResultTransformer(AnnouncementBO.class)).list();
    }

    @Override
    public void delete(Long id) {
        updateStatus(id, AnnouncementStatus.INACTIVE);
    }

    @Override
    public void cancel(Long id) {
        updateStatus(id, AnnouncementStatus.CANCEL);
    }

    @Override
    public void copy(Long id) throws EntityNotFoundException {
        AnnouncementEntity entity = find(id);
        saveOrUpdate(entity.copy());
    }

    @Override
    public Long getUnreadAnnouncementsCount() {
        Query query = getCurrentSession().getNamedQuery(AnnouncementEntity.Q_GET_UNREAD_COUNT);
        query.setParameter("personId", SecurityUtils.getCurrentPersonId());
        return (Long) query.uniqueResult();
    }


    private void updateStatus(Long id, AnnouncementStatus status) {
        Query query = getCurrentSession().getNamedQuery(AnnouncementEntity.Q_UPDATE_STATUS);
        query.setParameter("id", id);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.executeUpdate();
    }
}
