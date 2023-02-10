package com.pls.documentmanagement.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;

/**
 * Implementation of LoadDocumentDao interface.
 * 
 * @author Pavani Challa
 * 
 */
@Repository
@Transactional
public class LoadDocumentDaoImpl extends AbstractDaoImpl<LoadDocumentEntity, Long> implements LoadDocumentDao {
    @Override
    public void updatePaperworkReceived(Long loadId) {
        getCurrentSession().createSQLQuery("update loads lds set cust_req_doc_recv_flag = 'Y', date_modified = LOCALTIMESTAMP, "
                                + " modified_by = 0, version = version + 1, frt_bill_recv_date = (case when "
                                + " frt_bill_recv_flag = 'Y' then LOCALTIMESTAMP else frt_bill_recv_date end) where lds.load_id = :loadId "
                                + " and (select count(distinct rd.document_type) from loads l inner join BILL_TO_REQ_DOC rd "
                                + " on rd.BILL_TO_ID = l.BILL_TO and rd.status = 'A' and rd.shipper_req_type = 'REQUIRED' left join "
                                + " image_metadata im on im.load_id = l.load_id and im.document_type = rd.document_type and im.status = 'A' "
                                + " where l.load_id = lds.load_id and im.image_meta_id is null) = 0")
                .setParameter("loadId", loadId)
                .executeUpdate();

        getCurrentSession().flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getMissingPaperworkForLoad(Long loadId) {
        StringBuilder sql = new StringBuilder("select distinct rd.document_type \"documentType\" from loads l ")
                .append(" inner join BILL_TO_REQ_DOC rd on rd.BILL_TO_ID = l.BILL_TO and rd.status = 'A' and ")
                .append(" rd.shipper_req_type in ('REQUIRED', 'ON_AVAIL') left join image_metadata im on im.load_id = l.load_id ")
                .append(" and im.status = 'A' and im.document_type = rd.document_type ")
                .append(" where l.load_id = :loadId and im.image_meta_id is null");

        Query query = getCurrentSession().createSQLQuery(sql.toString()).addScalar("documentType", StandardBasicTypes.STRING)
                .setParameter("loadId", loadId);

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getDocumentIdsForLoad(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_BY_LOAD_ID);
        query.setParameter(LoadDocumentEntity.LOAD_ID_PARAM, loadId);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentDocumentInfoBO> getDocumentsInfoForShipment(Long shipmentId) {
        Query query = getCurrentSession().getNamedQuery(ShipmentDocumentInfoBO.QUERY_BY_LOAD_ID);
        return getDocumentsInfoQuery(query.getQueryString(), shipmentId).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentDocumentInfoBO> getDocumentsInfoForManualBol(Long manualBolId) {
        Query query = getCurrentSession().getNamedQuery(ShipmentDocumentInfoBO.QUERY_BY_MANUAL_BOL_ID);
        return getDocumentsInfoQuery(query.getQueryString(), manualBolId).list();
    }

    @Override
    public void deleteDocuments(List<Long> docIds) {
        if (docIds != null && !docIds.isEmpty()) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(LoadDocumentEntity.STATUS_PARAM, Status.INACTIVE);
            parameters.put(LoadDocumentEntity.DOC_IDS_PARAM, docIds);
            executeNamedQueryUpdate(LoadDocumentEntity.Q_UPDATE_STATUS_BY_DOC_IDS, parameters);
        }
    }

    @Override
    public List<LoadDocumentEntity> findDocumentsForLoad(Long loadId, String docType) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(LoadDocumentEntity.LOAD_ID_PARAM, loadId);
        parameters.put(LoadDocumentEntity.DOCUMENT_TYPE_PARAM, docType);
        return findByNamedQuery(LoadDocumentEntity.Q_BY_LOAD_ID_AND_DOC_TYPE, parameters);
    }

    @Override
    public List<LoadDocumentEntity> findReqDocumentsForLoad(Long loadId) {
        List<BigInteger> documentIds = findRequiredAndAvailableDocumentsByLoadId(loadId);
        List<Long> ids = new ArrayList<Long>();
        for (BigInteger documentId : documentIds) {
            ids.add(documentId.longValue());
        }
        return getAll(ids);
    }

    @Override
    public List<LoadDocumentEntity> findDocumentsForManualBol(Long manualBolId, String docType) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(LoadDocumentEntity.MANUAL_BOL_ID_PARAM, manualBolId);
        parameters.put(LoadDocumentEntity.DOCUMENT_TYPE_PARAM, docType);
        return findByNamedQuery(LoadDocumentEntity.Q_BY_MANUAL_BOL_ID_AND_DOC_TYPE, parameters);
    }

    @Override
    public void deleteTempDocument(LoadDocumentEntity loadDocument) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(LoadDocumentEntity.ID_PARAM, loadDocument.getId());
        executeNamedQueryUpdate(LoadDocumentEntity.Q_DELETE_TEMP_DOC, parameters);
    }

    @Override
    public List<LoadDocumentEntity> findTempDocumentsOlderThanSpecifiedDate(Date date) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(LoadDocumentEntity.CREATED_DATE_PARAM, date);

        return findByNamedQuery(LoadDocumentEntity.Q_BY_EARLIER_CREATED_DATE, parameters);
    }

    @Override
    public LoadDocumentEntity findCustomerLogoForBOL(Long customerId) {
        return (LoadDocumentEntity) getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_GET_CUSTOMER_LOGO_FOR_BOL).
                setLong("customerId", customerId)
                .uniqueResult();
    }

    @Override
    public LoadDocumentEntity findCustomerLogoForShipLabel(Long customerId) {
        return (LoadDocumentEntity) getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_GET_CUSTOMER_LOGO_SHIP_LABEL).
                setLong("customerId", customerId)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> findRequiredAndAvailableDocumentsByLoadId(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_GET_REQUIRED_AND_AVAILABLE_DOCUMENTS);
        query.setParameter(LoadDocumentEntity.LOAD_ID_PARAM, loadId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Date> findCreatedDatesForReqDocsByLoadId(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_GET_CREATED_DATE_FOR_REQ_DOCS);
        query.setParameter(LoadDocumentEntity.LOAD_ID_PARAM, loadId);
        return query.list();
    }

    @Override
    public LoadDocumentEntity findDocumentByIdAndToken(Long id, String downloadToken) {
        return (LoadDocumentEntity) getCurrentSession().getNamedQuery(LoadDocumentEntity.Q_GET_DOCUMENT_BY_ID_AND_TOKEN)
                .setLong("id", id)
                .setString("downloadToken", downloadToken)
                .uniqueResult();
    }

    @Override
    public LoadDocumentEntity saveOrUpdate(LoadDocumentEntity entity) {
        LoadDocumentEntity document = super.saveOrUpdate(entity);
        getCurrentSession().flush();
        return document;
    }

    private Query getDocumentsInfoQuery(String query, Long parameter) {
        return getCurrentSession().createSQLQuery(query).
                addScalar(ShipmentDocumentInfoBO.ID_COLUMN, StandardBasicTypes.LONG).
                addScalar(ShipmentDocumentInfoBO.SHIPMENT_ID_COLUMN, StandardBasicTypes.LONG).
                addScalar(ShipmentDocumentInfoBO.NAME_COLUMN, StandardBasicTypes.STRING).
                addScalar(ShipmentDocumentInfoBO.DATE_COLUMN, StandardBasicTypes.TIMESTAMP).
                addScalar(ShipmentDocumentInfoBO.FIRST_NAME_COLUMN, StandardBasicTypes.STRING).
                addScalar(ShipmentDocumentInfoBO.LAST_NAME_COLUMN, StandardBasicTypes.STRING).
                addScalar(ShipmentDocumentInfoBO.DOC_FILE_TYPE, StandardBasicTypes.STRING).
                addScalar(ShipmentDocumentInfoBO.CREATED_DATE_COLUMN, StandardBasicTypes.TIMESTAMP).
                addScalar(ShipmentDocumentInfoBO.FILE_NAME_COLUMN, StandardBasicTypes.STRING).
                setParameter(ShipmentDocumentInfoBO.LOAD_ID_PARAM, parameter).setResultTransformer(Transformers.aliasToBean(ShipmentDocumentInfoBO
                        .class));
    }
}
