package com.pls.user.restful;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.user.address.service.AnnouncementService;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;
import com.pls.user.restful.dto.AnnouncementDTO;
import com.pls.user.restful.dtobuilder.AnnouncementDTOBuilder;

/**
 * Announcement REST resource.
 * 
 * @author Dmitry Nikolaenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/announcement")
public class AnnouncementResource {

    private final AnnouncementDTOBuilder announcementBuilder = new AnnouncementDTOBuilder(
            new AnnouncementDTOBuilder.AnnouncementDataProvider() {
                @Override
                public AnnouncementEntity getAnnouncementById(Long announcementId) {
                    return announcementService.findAnnouncementById(announcementId);
                }
    });

    @Autowired
    private AnnouncementService announcementService;

    /**
     * Get all published announcements.
     *
     * @param from
     *            is date when announcement starts displaying
     * @param to
     *            is date when announcement ends displaying
     * @param statuses
     *            announcements statuses
     * @return published announcements.
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<AnnouncementBO> getAll(
            @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @RequestParam(value = "status", required = true) List<AnnouncementStatus> statuses) {
        return announcementService.getAll(from, to, statuses);
    }

    /**
     * Get all published announcements.
     *
     * @return unpublished announcements.
     */
    @RequestMapping(value = "/unpublished", method = RequestMethod.GET)
    @ResponseBody
    public List<AnnouncementBO> getUnpublishedAnnouncements() {
        return announcementService.getAllUnpublished();
    }

    /**
     * Create or update announcement.
     * 
     * @param announcement
     *            Not <code>null</code> {@link AnnouncementDTO}
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveAnnouncement(@RequestBody AnnouncementDTO announcement) {
        AnnouncementEntity announcementEntity = announcementBuilder.buildEntity(announcement);
        announcementService.saveAnnouncement(announcementEntity);
    }

    /**
     * Publish announcement.
     * 
     * @param id
     *            announcement to be published
     */
    @RequestMapping(value = "/{id}/publish", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void publishAnnouncement(@PathVariable("id") Long id) {
        announcementService.publish(id);
    }

    /**
     * Get announcement details.
     * 
     * @param id
     *            announcement to be loaded
     * @return the announcement details
     */
    @RequestMapping(value = "/{id}/get", method = RequestMethod.GET)
    @ResponseBody
    public AnnouncementDTO getAnnouncementById(@PathVariable("id") Long id) {
        return announcementBuilder.buildDTO(announcementService.findAnnouncementById(id));
    }

    /**
     * Mark an announcement as read.
     * 
     * @param announcementId
     *            Not <code>null</code> {@link AnnouncementEntity#getId()}
     */
    @RequestMapping(value = "/{announcementId}/markAsRead", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void markAsRead(@PathVariable("announcementId") Long announcementId) {
        announcementService.markAsRead(announcementId);
    }

    /**
     * Copy announcement.
     * 
     * @param announcementId
     *            announcement to be copy.
     * @throws EntityNotFoundException
     *             when announcement not found.
     */
    @RequestMapping(value = "/{announcementId}/copy", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void copyAnnouncement(@PathVariable("announcementId") Long announcementId) throws EntityNotFoundException {
        announcementService.copy(announcementId);
    }

    /**
     * Cancel announcement.
     * 
     * @param announcementId
     *            announcement to be cancel
     */
    @RequestMapping(value = "/{announcementId}/cancel", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void cancelAnnouncement(@PathVariable("announcementId") Long announcementId) {
        announcementService.cancel(announcementId);
    }


    /**
     * Delete announcement.
     * 
     * @param announcementId
     *            announcement to be deleted
     */
    @RequestMapping(value = "/{announcementId}/delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteAnnouncement(@PathVariable("announcementId") Long announcementId) {
        announcementService.delete(announcementId);
    }

    /**
     * Get count of unread announcements.
     * 
     * @return count of unread announcements
     */
    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    @ResponseBody
    public Long getUnreadAnnouncementsCount() {
        return announcementService.getUnreadAnnouncementsCount();
    }
}
