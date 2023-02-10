package com.pls.user.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.user.dao.AnnouncementHistoryDao;
import com.pls.user.domain.AnnouncementHistoryEntity;

/**
 * DAO for {@link AnnouncementHistoryEntity}.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Transactional
@Repository
public class AnnouncementHistoryDaoImpl extends AbstractDaoImpl<AnnouncementHistoryEntity, Long>
        implements AnnouncementHistoryDao {

    @Override
    public boolean isAnnouncementRead(Long personId, Long announcementId) {
        Query query = getCurrentSession().getNamedQuery(AnnouncementHistoryEntity.Q_GET_BY_PERSON_AND_ANNOUNCEMENT);
        query.setParameter("personId", personId);
        query.setParameter("announcementId", announcementId);
        return query.uniqueResult() != null;
    }

}
