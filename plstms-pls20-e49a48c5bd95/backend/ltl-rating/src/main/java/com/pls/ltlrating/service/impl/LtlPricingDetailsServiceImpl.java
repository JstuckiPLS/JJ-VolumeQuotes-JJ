package com.pls.ltlrating.service.impl;

import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingDetailsDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.service.LtlPricingDetailsService;
import com.pls.ltlrating.service.validation.LtlPricingDetailsValidator;

/**
 * Implementation of {@link LtlPricingDetailsService}.
 *
 * @author Artem Arapov
 */
@Service
@Transactional
public class LtlPricingDetailsServiceImpl implements LtlPricingDetailsService {

    @Autowired
    private LtlPricingDetailsDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlPricingDetailsValidator.class)
    private Validator<LtlPricingDetailsEntity> validator;

    @Autowired
    private GeoOptimizingHelper geoHelper;

    public void setDao(LtlPricingDetailsDao dao) {
        this.dao = dao;
    }

    @Override
    public LtlPricingDetailsEntity savePricingDetail(LtlPricingDetailsEntity entity) throws ValidationException {
        validator.validate(entity);
        geoHelper.improveGeoDetails(entity);
        return dao.saveOrUpdate(entity);
    }

    @Override
    public LtlPricingDetailsEntity getPricingDetailById(Long id) {
        return dao.find(id);
    }

    @Override
    public List<PricingDetailListItemVO> getActivePricingDetailsByProfileDetailId(Long profileDetailId) {
        return dao.findActiveAndEffectiveByProfileDetailId(profileDetailId);
    }

    @Override
    public List<PricingDetailListItemVO> getInactivePricingDetailsByProfileDetailId(Long profileDetailId) {
        return dao.findArchivedPrices(profileDetailId);
    }

    @Override
    public List<PricingDetailListItemVO> getExpiredPricingDetailsByProfileDetailId(Long profileDetailId) {
        return dao.findExpiredByProfileDetailId(profileDetailId);
    }

    @Override
    public List<PricingDetailListItemVO> inactivatePricingDetails(List<Long> pricingDetailIds, Long profileDetailId, Boolean isActiveList) {
        dao.updateStatus(pricingDetailIds, Status.INACTIVE, SecurityUtils.getCurrentPersonId());

        return isActiveList ? getActivePricingDetailsByProfileDetailId(profileDetailId)
                : getExpiredPricingDetailsByProfileDetailId(profileDetailId);
    }

    @Override
    public List<PricingDetailListItemVO> reactivatePricingDetails(List<Long> pricingDetailIds, Long profileDetailId) {
        dao.updateStatus(pricingDetailIds, Status.ACTIVE, SecurityUtils.getCurrentPersonId());

        return getInactivePricingDetailsByProfileDetailId(profileDetailId);
    }

    @Override
    public List<PricingDetailListItemVO> expiratePricingDetails(List<Long> pricingDetailIds, Long profileDetailId) {
        dao.updateStatusToExpired(pricingDetailIds, SecurityUtils.getCurrentPersonId());
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(PricingType.BLANKET.name())) {
            dao.expirateCSPByCopiedFrom(pricingDetailIds, SecurityUtils.getCurrentPersonId());
        }

        return getActivePricingDetailsByProfileDetailId(profileDetailId);
    }

    @Override
    public void copyFrom(Long copyFromProfileId, Long copyToProfileId) {
        dao.updateStatusToInactiveByProfileId(copyToProfileId, SecurityUtils.getCurrentPersonId());

        List<LtlPricingDetailsEntity> prices = dao.findActiveAndEffectiveForProfile(copyFromProfileId);
        for (LtlPricingDetailsEntity price : prices) {
            dao.evict(price);
            price.setLtlPricProfDetailId(copyToProfileId);
            price.setCopiedFrom(price.getId());
            price.setId(null);
            // need to create new collections because Hibernate isn't handling it properly
            price.setGeoServices(new HashSet<>(price.getGeoServices()));
            price.setFakMapping(new HashSet<>(price.getFakMapping()));
            price.getGeoServices().forEach(geoService -> {
                geoService.setId(null);
                // need to create new collections because Hibernate isn't handling it properly
                geoService.setOriginDetails(new HashSet<>(geoService.getOriginDetails()));
                geoService.setDestinationDetails(new HashSet<>(geoService.getDestinationDetails()));
                geoService.getOriginDetails().forEach(detail -> detail.setId(null));
                geoService.getDestinationDetails().forEach(detail -> detail.setId(null));
            });
            price.getFakMapping().forEach(m -> m.setId(null));
        }
        dao.persistBatch(prices);
    }
}
