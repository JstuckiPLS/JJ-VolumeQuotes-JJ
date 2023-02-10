package com.pls.extint.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.extint.dao.ApiTypeDao;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.ApiTypeEntity;

/**
 * DAO implementation of ApiTypeDao for loading/saving api types.
 * 
 * @author Pavani Challa
 * 
 */
@Transactional
@Repository
public class ApiTypeDaoImpl extends AbstractDaoImpl<ApiTypeEntity, Long> implements ApiTypeDao {

    private static final String SHIPPER_SPECIFIC = " (shipper_org_id = ? or (shipper_org_id is null or shipper_org_id = -1))";

    private static final String CARRIER_SPECIFIC = " (carrier_org_id = ? or (carrier_org_id is null or carrier_org_id = -1))";

    @Override
    public List<ApiTypeEntity> findByCategory(Long carrierOrgId, Long shipperOrgId, String category, String apiOrgType) {
        Criteria criteria = getCriteria(carrierOrgId, shipperOrgId, category, apiOrgType);
        return getApiTypes(criteria);
    }

    @Override
    public List<ApiTypeEntity> findDocumentApiTypesForLoad(Long loadId, Long carrierOrgId, Long shipperOrgId, String apiOrgType) {
        Criteria criteria = getCriteria(carrierOrgId, shipperOrgId, "IMAGING", apiOrgType);
        criteria.add(Restrictions.sqlRestriction(" (({alias}.api_type = 'ALL') or ({alias}.api_type in (select reqDoc.DOCUMENT_TYPE "
                + "from BILL_TO_REQ_DOC reqDoc left join LOADS l on l.BILL_TO = reqDoc.BILL_TO_ID where l.load_id = ? "
                + "and reqDoc.status = 'A' and reqDoc.SHIPPER_REQ_TYPE in ('REQUIRED', 'ON_AVAIL'))))", loadId, StandardBasicTypes.LONG));
        return getApiTypes(criteria);
    }

    private Criteria getCriteria(Long carrierOrgId, Long shipperOrgId, String category, String apiOrgType) {
        Criteria criteria = getCurrentSession().createCriteria(ApiTypeEntity.class);
        criteria.add(Restrictions.eq("apiCategory", category));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        if ("CARRIER".equalsIgnoreCase(apiOrgType)) {
            criteria.add(Restrictions.eq("carrierOrgId", carrierOrgId));
            criteria.add(Restrictions.sqlRestriction(SHIPPER_SPECIFIC, shipperOrgId == null ? -1 : shipperOrgId, StandardBasicTypes.LONG));
        } else {
            criteria.add(Restrictions.eq("shipperOrgId", shipperOrgId));
            criteria.add(Restrictions.sqlRestriction(CARRIER_SPECIFIC, carrierOrgId == null ? -1 : carrierOrgId, StandardBasicTypes.LONG));
        }
        criteria.add(Restrictions.eq("apiOrgType", apiOrgType));

        return criteria;
    }

    @SuppressWarnings("unchecked")
    private List<ApiTypeEntity> getApiTypes(Criteria criteria) {
        List<ApiTypeEntity> apiTypes = (List<ApiTypeEntity>) criteria.list();
        if (apiTypes != null && !apiTypes.isEmpty()) {
            for (ApiTypeEntity type : apiTypes) {
                if (type.getMetadata() != null && !type.getMetadata().isEmpty()) {
                    type.setReqMetadata(getMetadataForCategory(type.getMetadata(), "REQUEST"));
                    type.setRespMetadata(getMetadataForCategory(type.getMetadata(), "RESPONSE"));
                }
            }
        }

        return apiTypes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getLookupValue(String lookupGroup, Long orgId, String value, boolean isApiLookup) {
        String sql;
        if (isApiLookup) {
            sql = "select API_VALUE from API_LOOKUP_TABLE where LOOKUP_GROUP = :lookupGroup and ORG_ID = :orgId and UPPER(PLS_VALUE) = :value";
        } else {
            sql = "select PLS_VALUE from API_LOOKUP_TABLE where LOOKUP_GROUP = :lookupGroup and ORG_ID = :orgId and UPPER(API_VALUE) = :value";
        }

        Query query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("lookupGroup", lookupGroup).setParameter("orgId", orgId).setParameter("value", value.toUpperCase());

        List<String> results = (List<String>) query.list();
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }

    private List<ApiMetadataEntity> getMetadataForCategory(List<ApiMetadataEntity> metadata, String metadataType) {
        Map<Long, ApiMetadataEntity> metadataMap = new HashMap<Long, ApiMetadataEntity>();
        List<ApiMetadataEntity> hierarchialMetadata = new ArrayList<ApiMetadataEntity>();

        for (ApiMetadataEntity entity : metadata) {
            if (metadataType.equalsIgnoreCase(entity.getMetadataType())) {
                metadataMap.put(entity.getId(), entity);
            }
        }

        for (ApiMetadataEntity entity : metadataMap.values()) {
            if (entity.getParent() == null) {
                hierarchialMetadata.add(entity);
            } else {
                metadataMap.get(entity.getParent()).addChildren(entity);
            }
        }

        metadataMap = null;
        return hierarchialMetadata;
    }
}
