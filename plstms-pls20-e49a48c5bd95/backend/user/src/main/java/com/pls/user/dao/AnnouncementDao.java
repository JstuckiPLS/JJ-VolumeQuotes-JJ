package com.pls.user.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * DAO for {@link AnnouncementEntity}.
 * 
 * @author Nalapko Alexander
 * 
 */
public interface AnnouncementDao extends AbstractDao<AnnouncementEntity, Long> {

    /**
     * Get all unpublished announcements.
     *
     * @return List of found announcements
     */
    List<AnnouncementBO> getAllUnpublished();

    /**
     * Deletes the announcement. The status is set to INACTIVE.
     * 
     * @param id
     *            announcement to be deleted
     */
    void delete(Long id);

    /**
     * Cancels the announcement. The status is set to CANCEL.
     * 
     * @param id
     *            announcement to be canceled
     */
    void cancel(Long id);

    /**
     * Copy the announcement.
     * 
     * @param id
     *            announcement to be copied
     * @throws EntityNotFoundException
     *             when user not found.
     */
    void copy(Long id) throws EntityNotFoundException;

    /**
     * Get count of unread announcements.
     * 
     * @return count of unread announcements
     */
    Long getUnreadAnnouncementsCount();

    /**
     * Get all announcements by time interval and status.
     *
     * @param from
     *            {@link Date}
     * @param to
     *            {@link Date}
     * @param statuses
     *            {@link AnnouncementStatus}
     * @return List of found announcements
     */
    List<AnnouncementBO> getAll(Date from, Date to, List<AnnouncementStatus> statuses);
}
