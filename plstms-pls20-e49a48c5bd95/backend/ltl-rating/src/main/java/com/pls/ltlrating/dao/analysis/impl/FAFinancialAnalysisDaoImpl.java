package com.pls.ltlrating.dao.analysis.impl;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.dao.analysis.FAFinancialAnalysisDao;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * DAO object for {@link FAFinancialAnalysisEntity}.
 *
 * @author Brichak Aleksandr
 *
 */
@Repository
@Transactional
public class FAFinancialAnalysisDaoImpl extends AbstractDaoImpl<FAFinancialAnalysisEntity, Long>
        implements FAFinancialAnalysisDao {

    @Override
    public void updateAnalysisStatus(Long id, FinancialAnalysisStatus status) {
        getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_UPDATE_ANALYSIS_STATUS).setParameter("id", id)
                .setParameter("status", status).setParameter("modifiedBy", SecurityUtils.getCurrentPersonId())
                .executeUpdate();
    }

    @Override
    public FinancialAnalysisStatus getAnalysisStatus(Long id) {
        return (FinancialAnalysisStatus) getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_GET_ANALYSIS_STATUS)
                .setParameter("id", id)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AnalysisItem> getNextAnalysisForProcessing(int maxCount) {
        return getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_GET_FOR_PROCESSING)
                .setResultTransformer(Transformers.aliasToBean(AnalysisItem.class))
                .setMaxResults(maxCount).list();
    }

    @Override
    public boolean markInputDetailAsCompleted(Long detailId) {
        return getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_MARK_ROW_COMPLETED).setLong("rowId", detailId).executeUpdate() > 0;
    }

    @Override
    public boolean markAnalysisAsCompleted(Long analysisId) {
        return getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_MARK_ANALYSIS_COMPLETED).setLong("analysisId", analysisId)
                .executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FreightAnalysisReportStatusBO> getAnalysisJobs() {
        return getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_GET_ANALYSIS_JOBS)
                .setResultTransformer(Transformers.aliasToBean(FreightAnalysisReportStatusBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FAFinancialAnalysisEntity> getNextFreightAnalysisBySeqNumber(Integer seqNumber, Boolean step) {
        return (List<FAFinancialAnalysisEntity>) getCurrentSession()
                .getNamedQuery(FAFinancialAnalysisEntity.Q_GET_NEXT_ANALYSIS_BY_SEQ_NUMBER)
                .setInteger("seqNumber", seqNumber)
                .setBoolean("step", step).list();
    }

    @Override
    public Integer getNextSeqNumber() {
        Long sequence = (Long) getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_GET_NEXT_SEQ_NUMBER).uniqueResult();
        return ObjectUtils.defaultIfNull(sequence, 0L).intValue();
    }

    @Override
    public FAFinancialAnalysisEntity saveOrUpdate(FAFinancialAnalysisEntity entity) {
        FAFinancialAnalysisEntity updatedEntity = super.saveOrUpdate(entity);
        getCurrentSession().flush();
        getCurrentSession().refresh(updatedEntity);
        return updatedEntity;
    }

    @Override
    public void updateAnalysisWithResultFile(Long analysisId, Long documentId) {
        getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.U_SET_DOCUMENT)
                .setLong("analysisId", analysisId)
                .setParameter("documentId", documentId, LongType.INSTANCE)
                .executeUpdate();
    }

    @Override
    public FAFinancialAnalysisEntity getWithDependencies(Long analysisId) {
        return (FAFinancialAnalysisEntity) getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.Q_GET_WITH_DEPENDENCIES)
                .setLong("analysisId", analysisId)
                .uniqueResult();
    }

    @Override
    public void deleteIncorrectOutputDetails() {
        getCurrentSession().getNamedQuery(FAFinancialAnalysisEntity.U_DELETE_INCORRECT_OUTPUT).executeUpdate();
    }
}
