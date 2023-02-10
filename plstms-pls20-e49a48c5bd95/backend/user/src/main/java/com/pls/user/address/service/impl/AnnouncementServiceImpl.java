package com.pls.user.address.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.user.address.service.AnnouncementService;
import com.pls.user.dao.AnnouncementDao;
import com.pls.user.dao.AnnouncementHistoryDao;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.AnnouncementHistoryEntity;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * {@AnnouncementService} implementation.
 * 
 * @author Alexander Nalapko
 *
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementDao dao;

    @Autowired
    private AnnouncementHistoryDao historyDao;

    @Override
    public List<AnnouncementBO> getAll(Date from, Date to, List<AnnouncementStatus> statuses) {
        return dao.getAll(from, to, statuses);
    }

    @Override
    public List<AnnouncementBO> getAllUnpublished() {
        return dao.getAllUnpublished();
    }

    @Override
    public void saveAnnouncement(AnnouncementEntity announcement) {
        dao.saveOrUpdate(announcement);
    }

    @Override
    public AnnouncementEntity findAnnouncementById(Long announcementId) {
        return dao.find(announcementId);
    }

    @Override
    public Long getUnreadAnnouncementsCount() {
        return dao.getUnreadAnnouncementsCount();
    }

    @Override
    public void delete(Long id) {
        dao.delete(id);
    }

    @Override
    public void cancel(Long id) {
        dao.cancel(id);

    }

    @Override
    public void copy(Long id) throws EntityNotFoundException {
        dao.copy(id);
    }

    @Override
    public void publish(Long id) {
        AnnouncementEntity entity = dao.find(id);
        UserEntity announcer = new UserEntity();
        announcer.setId(SecurityUtils.getCurrentPersonId());
        entity.setAnnouncer(announcer);
        entity.setStatus(AnnouncementStatus.PUBLISHED);
        entity.setPublishedDate(new Date());
        dao.saveOrUpdate(entity);
    }

    @Override
    public void markAsRead(Long announcementId) {
        Long personId = SecurityUtils.getCurrentPersonId();

        if (historyDao.isAnnouncementRead(personId, announcementId)) {
            return;
        }
        AnnouncementHistoryEntity announcementHistory = new AnnouncementHistoryEntity();
        announcementHistory.setAnnouncementId(announcementId);
        announcementHistory.setReadDate(new Date());
        announcementHistory.setPersonId(personId);
        historyDao.saveOrUpdate(announcementHistory);
    }
}