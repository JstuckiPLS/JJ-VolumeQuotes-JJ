
package com.pls.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CompanyCodeDao;
import com.pls.core.dao.OrgServiceDao;
import com.pls.core.dao.OrganizationAPIDetailsDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.SimpleOrganizationDao;
import com.pls.core.domain.organization.CompanyCodeEntity;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.OrganizationService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.OrganizationAPIDetailsValidator;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;

/**
 * Implementation for {@link OrganizationService}.
 *
 * @author Alexander Nalapko
 *
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private CompanyCodeDao companyCodesDao;

    @Autowired
    private OrganizationDao dao;

    @Autowired
    private SimpleOrganizationDao simpleOrgDao;

    @Autowired
    private OrganizationAPIDetailsDao apiDetails;

    @Autowired
    private OrgServiceDao orgServiceDao;

    @Resource(type = OrganizationAPIDetailsValidator.class)
    private Validator<OrganizationAPIDetailsEntity> validator;

    @Override
    public List<SimpleOrganizationEntity> getOrganizationsByNameAndType(String orgType,
            String name, Integer limit, Integer offset) {
        return  simpleOrgDao.getOrganizationsByNameAndType(orgType, name, limit, offset);
    }

    @Override
    public void updateLogoForOrganization(Long orgId, Long docId) throws EntityNotFoundException {
        dao.updateLogoForOrganization(orgId, docId, SecurityUtils.getCurrentPersonId());
    }

    @Override
    public void saveCarrierAPIDetails(OrganizationAPIDetailsEntity details) throws ValidationException {
        validator.validate(details);
        dao.saveCarrierAPIDetails(details);
    }

    @Override
    public OrganizationAPIDetailsEntity getCarrierAPIDetailsByOrgId(Long orgId) {
        return apiDetails.getCarrierAPIDetailsByOrgId(orgId);
    }

    @Override
    public SimpleOrganizationEntity getSimpleOrganizationById(Long id) {
        return  simpleOrgDao.find(id);
    }

    @Override
    public String getOrganizationNameByOrgId(Long orgId) {
        return simpleOrgDao.find(orgId).getName();
    }

    @Override
    public Long getImageByOrganizationId(Long orgId) {
        OrganizationEntity organization = dao.find(orgId);
        if (organization != null) {
            return organization.getLogoId();
        }
        return null;
    }

    @Override
    public OrgServiceEntity getServicesByOrgId(Long orgId) {
        OrgServiceEntity orgService = orgServiceDao.getServicesByOrgId(orgId);
        return orgService != null ? orgService : new OrgServiceEntity();
    }

    @Override
    public OrgServiceEntity saveOrgServices(OrgServiceEntity orgServices) {
        return orgServiceDao.saveOrUpdate(orgServices);
    }

    @Override
    public List<CompanyCodeEntity> getCompanyCodes() {
        return companyCodesDao.getAll();
    }

    @Override
    public boolean unsubscribeFromPaperworkEmails(Long orgId) {
        return dao.unsubscribeFromPaperworkEmails(orgId);
    }
}
