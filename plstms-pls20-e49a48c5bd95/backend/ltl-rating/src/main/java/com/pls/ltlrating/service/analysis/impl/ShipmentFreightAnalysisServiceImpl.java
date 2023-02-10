package com.pls.ltlrating.service.analysis.impl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.shared.StatusYesNo;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.ltlrating.dao.analysis.FAFinancialAnalysisDao;
import com.pls.ltlrating.dao.analysis.FAInputDetailsDao;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.service.analysis.FreightAnalysisImportExportService;
import com.pls.ltlrating.service.analysis.ShipmentFreightAnalysisService;
import com.pls.ltlrating.shared.FreightAnalysisDetailsBO;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * Implementation for {@link ShipmentFreightAnalysisService}.
 *
 * @author Brichak Aleksandr
 *
 */
@Service
@Transactional
public class ShipmentFreightAnalysisServiceImpl implements ShipmentFreightAnalysisService {

    @Autowired
    private FAFinancialAnalysisDao financialAnalysisDao;

    @Autowired
    DocumentService documentService;

    @Autowired
    private FreightAnalysisImportExportService fileService;

    @Autowired
    private FAInputDetailsDao inputDetailsDao;

    @Autowired
    private CarrierDao carrierDao;

    @Override
    public void updateAnalysisStatus(Long id, FinancialAnalysisStatus status) {
        financialAnalysisDao.updateAnalysisStatus(id, status);
        if (status == FinancialAnalysisStatus.Processing) {
            financialAnalysisDao.updateAnalysisWithResultFile(id, null);
        }
    }

    @Override
    public FAFinancialAnalysisEntity addNewPricing(FreightAnalysisDetailsBO bo, List<FATariffsEntity> tariffs) throws Exception {
        documentService.moveAndSaveTempDocPermanently(bo.getUploadedDocId(), null, DocumentTypes.UNKNOWN.getDbValue());
        LoadDocumentEntity inputDocument = documentService.loadDocumentWithoutContent(bo.getUploadedDocId());
        Set<FAInputDetailsEntity> inputDetails = fileService.importInputDetailsFromFile(inputDocument.getId());
        FAFinancialAnalysisEntity analysis = buildFAAnalysisEntity(bo, tariffs, inputDocument, inputDetails);
        return financialAnalysisDao.saveOrUpdate(analysis);
    }

    @Override
    public List<FreightAnalysisReportStatusBO> getAnalysisJobs() {
        return financialAnalysisDao.getAnalysisJobs();
    }

    @Override
    public void swapAnalysisJobs(Long analysisId, Boolean isStepUP) {
        FAFinancialAnalysisEntity currentFA = financialAnalysisDao.find(analysisId);
        Integer currentSeqNum = currentFA.getSeq();
        List<FAFinancialAnalysisEntity> analysisJob = financialAnalysisDao
                .getNextFreightAnalysisBySeqNumber(currentSeqNum, isStepUP);
        if (analysisJob != null && !analysisJob.isEmpty()) {
            FAFinancialAnalysisEntity exchangeFA = isStepUP
                    ? analysisJob.stream().min(Comparator.comparingInt(FAFinancialAnalysisEntity::getSeq)).get()
                    : analysisJob.stream().max(Comparator.comparingInt(FAFinancialAnalysisEntity::getSeq)).get();
            currentFA.setSeq(exchangeFA.getSeq());
            exchangeFA.setSeq(currentSeqNum);
            financialAnalysisDao.saveOrUpdate(currentFA);
            financialAnalysisDao.saveOrUpdate(exchangeFA);
        }
    }

    @Override
    public FAFinancialAnalysisEntity copyAnalysisJob(FreightAnalysisDetailsBO bo, List<FATariffsEntity> tariffs) throws Exception {
        LoadDocumentEntity inputDocument = documentService.loadDocumentWithoutContent(bo.getUploadedDocId());
        Set<FAInputDetailsEntity> inputDetails = new HashSet<FAInputDetailsEntity>(inputDetailsDao.getInputDetailsByFAId(bo.getAnalysisId()));
        FAFinancialAnalysisEntity analysisEntity = buildFAAnalysisEntity(bo, tariffs, inputDocument, inputDetails);
        return financialAnalysisDao.saveOrUpdate(analysisEntity);
    }

    private void prepareInputDetails(Set<FAInputDetailsEntity> inputDetails, FAFinancialAnalysisEntity analysisEntity) {
        inputDetails.forEach((inputDetail) -> {
            if (inputDetail.getId() != null) {
                inputDetailsDao.evict(inputDetail);
                inputDetail.setId(null);
                inputDetail.setOutputDetails(null);
                inputDetail.setCompleted(StatusYesNo.NO);
            } else if (StringUtils.isNotBlank(inputDetail.getCarrierScac())) {
                inputDetail.setCarrier(carrierDao.findByScac(inputDetail.getCarrierScac()));
            }
            inputDetail.getMaterials().forEach((m) -> {
                m.setId(null);
                m.setInputDetails(inputDetail);
            });
            inputDetail.getAccessorials().forEach((a) -> {
                a.setId(null);
                a.setInputDetails(inputDetail);
            });
            if (inputDetail.getOutputDetails() != null) {
                inputDetail.getOutputDetails().forEach((d) -> {
                    d.setId(null);
                    d.setInputDetails(inputDetail);
                });
            }
            inputDetail.setAnalysis(analysisEntity);
        });
    }

    private FAFinancialAnalysisEntity buildFAAnalysisEntity(FreightAnalysisDetailsBO bo, List<FATariffsEntity> tariffs,
            LoadDocumentEntity inputDocument, Set<FAInputDetailsEntity> inputDetails) {
        FAFinancialAnalysisEntity analysisEntity = new FAFinancialAnalysisEntity();
        analysisEntity.setInputFile(inputDocument);
        analysisEntity.setInputFileName(bo.getUploadedFileName());
        analysisEntity.setOutputFileName(bo.getCompletedFileName());
        analysisEntity.setTariffs(tariffs.stream().peek(tariff -> tariff.setAnalysis(analysisEntity)).collect(Collectors.toSet()));
        analysisEntity.setBlockIndirectServiceType(bo.getBlockIndirectServiceType());
        analysisEntity.setSeq(financialAnalysisDao.getNextSeqNumber());
        prepareInputDetails(inputDetails, analysisEntity);
        analysisEntity.setInputDetails(inputDetails);
        return analysisEntity;
    }
}
