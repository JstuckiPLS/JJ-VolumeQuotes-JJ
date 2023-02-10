package com.pls.user.address.service;

import java.util.Date;
import java.util.List;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * Service to manage {@link AnnouncementEntity} information.
 * 
 * @author Nalapko Alexander
 * 
 */
public interface AnnouncementService {

    /**
     * Get all announcements for a period of time.
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

    /**
     * Get all unpublished announcements.
     *
     * @return List of found announcements
     */
    List<AnnouncementBO> getAllUnpublished();

    /**
     * Create new announcement.
     * 
     * @param announcement
     *            Not <code>null</code> {@link AnnouncementEntity}.
     */
    void saveAnnouncement(AnnouncementEntity announcement);

    /**
     * Search announcement by specified id.
     * 
     * @param announcementId
     *            id of announcement
     * @return found announcement
     */
    AnnouncementEntity findAnnouncementById(Long announcementId);

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
     *             when announcement not found.
     */
    void copy(Long id) throws EntityNotFoundException;

    /**
     * Publish the announcement. The status is set to PUBLISHED.
     * 
     * @param id
     *            announcement to be published
     */
    void publish(Long id);

    /**
     * Mark an announcement as read.
     * 
     * @param announcementId
     *            Not <code>null</code> {@link AnnouncementEntity#getId()}
     */
    void markAsRead(Long announcementId);

    /**
     * Get count of unread announcements.
     * 
     * @return count of unread announcements
     */
    Long getUnreadAnnouncementsCount();
}
