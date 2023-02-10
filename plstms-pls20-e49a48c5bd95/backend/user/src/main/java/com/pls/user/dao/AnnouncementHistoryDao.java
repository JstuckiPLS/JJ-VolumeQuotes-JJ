package com.pls.user.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.user.domain.AnnouncementHistoryEntity;

/**
 * Dao for {@link AnnouncementHistoryEntity}.
 * 
 * @author Brichak Aleksandr
 * 
 */
public interface AnnouncementHistoryDao extends AbstractDao<AnnouncementHistoryEntity, Long> {

    /**
     * Get announcements by person.
     *
     * @param personId
     *            Id of person.
     * @param announcementId
     *            id of announcement.
     * 
     * @return {@code}true{@code} if announcement was read {@code}false{@code} otherwise.
     */
    boolean isAnnouncementRead(Long personId, Long announcementId);

}
