package com.pls.core.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationAPIDetailsDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.domain.bo.user.ParentOrganizationBO;
import com.pls.core.domain.organization.EflatbedOrganizationEntity;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.PaperworkEmailEntity;
import com.pls.core.exception.EntityNotFoundException;

/**
 * Implementation for {@link com.pls.core.dao.OrganizationDao}.
 * 
 * @author Alexander Nalapko
 * 
 */
@Repository
@Transactional
public class OrganizationDaoImpl extends AbstractDaoImpl<OrganizationEntity, Long> implements OrganizationDao {

    @Autowired
    private OrganizationAPIDetailsDao details;

    @Override
    public void saveCarrierAPIDetails(OrganizationAPIDetailsEntity details) {
        this.details.saveOrUpdate(details);
    }

    @Override
    public void updateLogoForOrganization(Long orgId, Long logoId, Long user) throws EntityNotFoundException {
        OrganizationEntity org = get(orgId);
        org.setLogoId(logoId);
        saveOrUpdate(org);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ParentOrganizationBO> getRootOrganizationByName(String name, int limit) {
        return getCurrentSession().getNamedQuery(EflatbedOrganizationEntity.Q_GET_ACTIVE_BY_NAME)
                .setString("name", "%" + StringUtils.upperCase(name) + "%")
                .setMaxResults(limit)
                .setResultTransformer(new AliasToBeanResultTransformer(ParentOrganizationBO.class))
                .list();
    }

    @Override
    public boolean unsubscribeFromPaperworkEmails(Long orgId) {
        Query query = getCurrentSession().getNamedQuery(PaperworkEmailEntity.Q_UNSUBSCRIBE);
        query.setParameter("orgId", orgId);

        return query.executeUpdate() > 0;
    }
}
