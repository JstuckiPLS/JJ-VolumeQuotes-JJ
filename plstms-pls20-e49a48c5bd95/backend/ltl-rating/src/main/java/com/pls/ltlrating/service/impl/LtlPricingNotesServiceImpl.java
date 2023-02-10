package com.pls.ltlrating.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.ltlrating.dao.LtlPricingNotesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.service.LtlPricingNotesService;
import com.pls.ltlrating.service.validation.LtlPricingNotesValidator;

/**
 * Implementation of {@link LtlPricingNotesService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class LtlPricingNotesServiceImpl implements LtlPricingNotesService {

    private static final String BLANKET = "BLANKET";

    @Autowired
    private LtlPricingNotesDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlPricingNotesValidator.class)
    private Validator<LtlPricingNotesEntity> validator;

    @Override
    public void saveNotes(LtlPricingNotesEntity entity) throws ValidationException {
        setModification(entity);
        validator.validate(entity);

        boolean needChildCopy = profileDao.findPricingTypeByProfileId(entity.getPricingProfileId()).equalsIgnoreCase(BLANKET);

        dao.saveOrUpdate(entity);
        if (needChildCopy) {
            copyEntityToChildCSPs(entity);
        }
    }

    @Override
    public LtlPricingNotesEntity getNotesById(Long id) {
        return dao.find(id);
    }

    @Override
    public List<LtlPricingNotesEntity> getNotesByProfileId(Long profileId) {
        return dao.findByProfileId(profileId);
    }

    private void setModification(LtlPricingNotesEntity entity) {
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy(SecurityUtils.getCurrentPersonId());
            entity.setCreatedDate(new Date());
        }
    }

    private void copyEntityToChildCSPs(LtlPricingNotesEntity entity) {
        List<Long> childIds = profileDao.findChildCSPByProfileId(entity.getPricingProfileId());

        for (Long profileId : childIds) {
            LtlPricingNotesEntity child = new LtlPricingNotesEntity(entity);
            child.setPricingProfileId(profileId);
            setModification(child);

            dao.saveOrUpdate(child);
        }
    }
}
