package com.pls.ltlrating.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockLaneDao;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.LtlBlockLaneGeoServDetailsEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.LtlBlockLaneService;
import com.pls.ltlrating.service.validation.LtlBlockLaneServicesValidator;

/**
 * Implementation of {@link LtlBlockLaneService}. Service to handle business logic for blocking lanes of
 * specific/all blanket carriers for particular customer.
 *
 * @author Ashwini Neelgund
 *
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class LtlBlockLaneServiceImpl implements LtlBlockLaneService {

    @Autowired
    private LtlBlockLaneDao dao;

    @Resource(type = LtlBlockLaneServicesValidator.class)
    private Validator<LtlBlockLaneEntity> validator;

    @Override
    public List<BlockLaneListItemVO> getActiveBlockedLanesByProfileId(Long profileId) {
        return dao.findActiveAndEffectiveByProfileId(profileId);
    }

    @Override
    public List<BlockLaneListItemVO> getExpiredBlockLaneByProfileId(Long profileId) {
        return dao.findExpiredByProfileId(profileId);
    }

    @Override
    public List<BlockLaneListItemVO> getInactiveBlockLaneByProfileId(Long profileId) {
        return dao.findByStatusAndProfileId(Status.INACTIVE.getStatusCode(), profileId);
    }

    @Override
    public List<BlanketCarrListItemVO> getApplicableBlanketCarrListForCust(Long profileId) {
        return dao.getUnblockedBlanketCarrListForCust(profileId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlBlockLaneEntity saveBlockedLane(LtlBlockLaneEntity entity) throws ValidationException {
        validator.validate(entity);
        boolean isNewEntity = entity.getId() == null;
        entity.setLtlBkLaneOriginGeoServiceDetails(Arrays
                .stream(StringUtils.split(entity.getOrigin() == null ? "" : entity.getOrigin(), ','))
                .map(geoCode -> getGeoServiceDetail(geoCode, entity, GeoType.ORIGIN)).collect(Collectors.toList()));
        entity.setLtlBkLaneDestGeoServiceDetails(
                Arrays.stream(StringUtils.split(entity.getDestination() == null ? "" : entity.getDestination(), ','))
                        .map(geoCode -> getGeoServiceDetail(geoCode, entity, GeoType.DESTINATION))
                        .collect(Collectors.toList()));
        return isNewEntity ? dao.saveOrUpdate(entity) : dao.merge(entity);
    }

    private LtlBlockLaneGeoServDetailsEntity getGeoServiceDetail(String geoCode, LtlBlockLaneEntity geoService,
            GeoType geoType) {
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
        return new LtlBlockLaneGeoServDetailsEntity(geoService, geoCode.trim(), geoType, geoPair.getLeft(),
                geoPair.getRight());
    }

    @Override
    public BlockLaneListItemVO getBlockedLaneById(Long id) {
        return dao.findById(id);
    }

    @Override
    public List<BlockLaneListItemVO> expireBlockedLanes(List<Long> blockedLaneIds, Long profileId) {
        dao.expireBlockedLanes(blockedLaneIds, SecurityUtils.getCurrentPersonId());
        return getActiveBlockedLanesByProfileId(profileId);
    }

    @Override
    public List<BlockLaneListItemVO> inactivateBlockedLanes(List<Long> blockedLaneIds, Long profileId,
            boolean isActive) {
        dao.updateStatusOfBlockedLanes(blockedLaneIds, Status.INACTIVE, SecurityUtils.getCurrentPersonId());
        return isActive ? getActiveBlockedLanesByProfileId(profileId) : getExpiredBlockLaneByProfileId(profileId);
    }

    @Override
    public List<BlockLaneListItemVO> reactivateBlockedLanes(List<Long> blockedLaneIds, Long profileId) {
        dao.updateStatusOfBlockedLanes(blockedLaneIds, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        return getInactiveBlockLaneByProfileId(profileId);
    }
}
