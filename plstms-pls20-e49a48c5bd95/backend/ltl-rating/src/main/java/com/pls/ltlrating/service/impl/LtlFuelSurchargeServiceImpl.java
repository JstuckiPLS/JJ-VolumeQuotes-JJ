package com.pls.ltlrating.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.SurchargeFileImportResult;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlFuelSurchargeDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.service.LtlFuelSurchargeService;
import com.pls.ltlrating.service.impl.file.LtlFuelSurchargeDocumentParser;

/**
 * Implementation for {@link LtlFuelSurchargeService}.
 *
 * @author Stas Norochevskiy
 *
 */
@Service
@Transactional(readOnly = true)
public class LtlFuelSurchargeServiceImpl implements LtlFuelSurchargeService {

    private static final String BLANKET = "BLANKET";

    @Autowired
    private LtlFuelSurchargeDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    private LtlFuelSurchargeDocumentParser excelParser = new LtlFuelSurchargeDocumentParser();

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlFuelSurchargeEntity saveFuelSurcharge(LtlFuelSurchargeEntity entity) {
        return (entity.getId() == null) ? addFuelSurchargeEntity(entity) : updateFuelSurchargeEntity(entity);
    }

    @Override
    public List<LtlFuelSurchargeEntity> getActiveFuelSurchargeByProfileDetailId(Long profileDetailId) {
        return dao.getActiveFuelSurchargeByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlFuelSurchargeEntity> importFuelSurchargeByProfileDetailIdFromFile(InputStream stream, String extension, Long profileDetailId,
            SurchargeFileImportResult surchargeFileImportResult) throws ImportException {

        FileExtensionType extType = FileExtensionType.getByValue(extension);
        if (extType == null) {
            throw new IllegalArgumentException("Unsupported extension of file. Please, check your file");
        }
        List<LtlFuelSurchargeEntity> importedEntities = null;
        try {
            importedEntities = excelParser.parse(stream, extType);
            for (LtlFuelSurchargeEntity importedEntity : importedEntities) {
                importedEntity.setLtlPricingProfileId(profileDetailId);
            }

            dao.updateStatusToInactiveByProfileId(profileDetailId, SecurityUtils.getCurrentPersonId());
            this.importFuelSurchargeByProfileDetailId(importedEntities);

            if (excelParser.isIncorrectHeader()) {
                surchargeFileImportResult.setIncorrectHeader(true);
            } else if (!excelParser.getInvalidRecords().isEmpty()) {
                surchargeFileImportResult.setIncorrectData(true);
            } else {
                surchargeFileImportResult.setOk(true);
            }
        } catch (ImportFileInvalidDataException e) {
            surchargeFileImportResult.setIncorrectData(true);
        }
        return importedEntities;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlFuelSurchargeEntity> importFuelSurchargeByProfileDetailId(List<LtlFuelSurchargeEntity> ltlFuelSurchargeEntities) {

        saveAllFuelSurcharges(ltlFuelSurchargeEntities);

        return ltlFuelSurchargeEntities;
    }

    @Override
    public BigDecimal getFuelSurchargeByFuelCharge(BigDecimal charge) {
        return dao.getFuelSurchargeByFuelCharge(charge);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyToCSP) {

        dao.updateStatusToInactiveByProfileId(copyToProfileId, SecurityUtils.getCurrentPersonId());

        List<LtlFuelSurchargeEntity> sourceList = this.getActiveFuelSurchargeByProfileDetailId(copyFromProfileId);
        List<LtlFuelSurchargeEntity> clonedList = cloneList(sourceList, copyToProfileId);

        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToCSP && profileDao.findPricingTypeByProfileDetailId(copyToProfileId).equalsIgnoreCase(BLANKET)) {
            copyToCSPProfiles(clonedList, copyToProfileId);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlFuelSurchargeEntity> saveAllFuelSurcharges(List<LtlFuelSurchargeEntity> ltlFuelSurcharges) {
        for (LtlFuelSurchargeEntity surcharge : ltlFuelSurcharges) {
            saveFuelSurcharge(surcharge);
        }

        return ltlFuelSurcharges;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStatus(List<Long> ids, Status status, Long profileDetailId) {
        Long personId = SecurityUtils.getCurrentPersonId();

        dao.updateStatusByListOfIds(ids, status, personId);
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ids, status, personId);
        }
    }

    private List<LtlFuelSurchargeEntity> cloneList(List<LtlFuelSurchargeEntity> source, Long profileDetailId) {
        List<LtlFuelSurchargeEntity> clonedList = new ArrayList<LtlFuelSurchargeEntity>(source.size());

        for (LtlFuelSurchargeEntity item : source) {
            LtlFuelSurchargeEntity clone = new LtlFuelSurchargeEntity(item);
            clone.setLtlPricingProfileId(profileDetailId);

            clonedList.add(clone);
        }

        return clonedList;
    }

    private void copyToCSPProfiles(List<LtlFuelSurchargeEntity> sourceList, Long parentProfileDetailId) {
        dao.inactivateCSPByProfileDetailId(parentProfileDetailId, SecurityUtils.getCurrentPersonId());
        List<BigInteger> childDetailsIds = profileDao.findChildCSPDetailByParentDetailId(parentProfileDetailId);
        for (BigInteger detailId : childDetailsIds) {
            List<LtlFuelSurchargeEntity> clonedList = cloneList(sourceList, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private LtlFuelSurchargeEntity addFuelSurchargeEntity(LtlFuelSurchargeEntity entity) {
        LtlFuelSurchargeEntity savedEntity = dao.saveOrUpdate(entity);
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricingProfileId()).equalsIgnoreCase(BLANKET)) {
            addEntityToChildCSPs(savedEntity);
        }

        return savedEntity;
    }

    private void addEntityToChildCSPs(LtlFuelSurchargeEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getLtlPricingProfileId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlFuelSurchargeEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlFuelSurchargeEntity source, Long profileDetailId) {
        LtlFuelSurchargeEntity entity = createChildCopyOfPricingDetailEntity(source, new LtlFuelSurchargeEntity());
        entity.setLtlPricingProfileId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private LtlFuelSurchargeEntity updateFuelSurchargeEntity(LtlFuelSurchargeEntity entity) {
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricingProfileId()).equalsIgnoreCase(BLANKET)) {
            updateEntityInChildCSPs(entity);
        }

        return dao.saveOrUpdate(entity);
    }

    private void updateEntityInChildCSPs(LtlFuelSurchargeEntity entity) {
        List<LtlFuelSurchargeEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlFuelSurchargeEntity child : childs) {
            LtlFuelSurchargeEntity updatedChild = createChildCopyOfPricingDetailEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlFuelSurchargeEntity createChildCopyOfPricingDetailEntity(final LtlFuelSurchargeEntity source, final LtlFuelSurchargeEntity child) {
        child.setMaxRate(source.getMaxRate());
        child.setMinRate(source.getMinRate());
        child.setStatus(source.getStatus());
        child.setSurcharge(source.getSurcharge());
        child.setCopiedFrom(source.getId());

        return child;
    }
}
