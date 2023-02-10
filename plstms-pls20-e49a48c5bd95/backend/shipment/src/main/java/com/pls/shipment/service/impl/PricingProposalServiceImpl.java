package com.pls.shipment.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.LtlPricProcMaterialsDao;
import com.pls.shipment.dao.LtlPricingProposalsDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlPricPropMaterialsEntity;
import com.pls.shipment.domain.LtlPricingProposalsEntity;
import com.pls.shipment.service.PricingProposalService;

/**
 * Asynchronous implementation of {@link PricingProposalService}.
 * 
 * @author Hima Bindu Challa
 */
@Service
@Transactional
@Async
public class PricingProposalServiceImpl implements PricingProposalService {

    @Autowired
    private LtlPricingProposalsDao ltlPricingProposalsDao;

    @Autowired
    private LtlPricProcMaterialsDao ltlPricProcMaterialsDao;

    @Override
    public void savePricingProposals(List<LtlPricingProposalsEntity> proposalEntities) {
        Long loadId = proposalEntities.iterator().next().getLoadId();
        if (loadId != null) {
            ltlPricingProposalsDao.inactivatePricingProposals(loadId);
        }
        ltlPricingProposalsDao.saveOrUpdateBatch(proposalEntities);
    }

    @Override
    public void updateLoadId(Long loadId, Long quoteId) {
        ltlPricingProposalsDao.createForLoadByQuoteId(loadId, quoteId);
    }

    @Override
    public void createPricingProposalMaterials(LoadEntity load) {
        ltlPricProcMaterialsDao.inactivatePricingProposalMaterials(load.getId());

        List<LtlPricPropMaterialsEntity> pricPropMaterials = new ArrayList<LtlPricPropMaterialsEntity>();
        for (LoadMaterialEntity loadMaterialEntity : load.getOrigin().getLoadMaterials()) {
            LtlPricPropMaterialsEntity pricPropMaterial = new LtlPricPropMaterialsEntity();
            pricPropMaterial.setLoadId(load.getId());
            pricPropMaterial.setWeight(loadMaterialEntity.getWeight());
            pricPropMaterial.setLength(loadMaterialEntity.getLength());
            pricPropMaterial.setWidth(loadMaterialEntity.getWidth());
            pricPropMaterial.setHeight(loadMaterialEntity.getHeight());
            pricPropMaterial.setCommodityClass(loadMaterialEntity.getCommodityClass());
            if (loadMaterialEntity.getQuantity() != null && !loadMaterialEntity.getQuantity().isEmpty()) {
                pricPropMaterial.setQuantity(Integer.parseInt(loadMaterialEntity.getQuantity()));
            }
            pricPropMaterial.setPieces(loadMaterialEntity.getPieces());
            pricPropMaterial.setHazmat(loadMaterialEntity.isHazmat());
            pricPropMaterial.setPackageType(loadMaterialEntity.getPackageType().getId());
            pricPropMaterial.setProductDescription(loadMaterialEntity.getProductDescription());
            pricPropMaterial.setReferencedProductId(loadMaterialEntity.getReferencedProductId());
            pricPropMaterials.add(pricPropMaterial);
        }

        ltlPricProcMaterialsDao.saveOrUpdateBatch(pricPropMaterials);
    }

}
