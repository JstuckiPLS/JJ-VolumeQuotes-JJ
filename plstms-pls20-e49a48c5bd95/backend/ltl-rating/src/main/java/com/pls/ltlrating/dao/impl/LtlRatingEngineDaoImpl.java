package com.pls.ltlrating.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.dao.LtlRatingEngineDao;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingAccessorialsVO;
import com.pls.ltlrating.shared.LtlRatingFSTriggerVO;
import com.pls.ltlrating.shared.LtlRatingGuaranteedVO;
import com.pls.ltlrating.shared.LtlRatingProfileLTLLCSummaryVO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;

/**
 * Class where we get LTL rate profiles for the given parameters.
 *
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
@SuppressWarnings("unchecked")
public class LtlRatingEngineDaoImpl implements LtlRatingEngineDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<LtlRatingProfileVO> getCustomerPricingProfile(GetOrderRatesCO criteria) {
        Query custProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_CUSTOMER_PRICING_PROFILE)
                .setParameter("shipperOrgId", criteria.getShipperOrgId())
                .setParameter("shipDate", criteria.getShipDate());

        setOriginCriteria(custProfQuery, criteria);
        setDestCriteria(custProfQuery, criteria);

        return custProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class)).list();
    }

    @Override
    public List<LtlRatingProfileVO> getBenchmarkRates(GetOrderRatesCO criteria, Set<Long> carrierOrgIds) {
        if (carrierOrgIds == null || carrierOrgIds.isEmpty()) {
            return null;
        }

        Query bmProfileQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_BENCHMARK_RATES)
                .setParameter("shipDate", criteria.getShipDate())
                .setParameter("movementType", getEnumNameAsString(criteria.getMovementType()))
                .setParameter("shipperOrgId", criteria.getShipperOrgId())
                .setParameterList("carrierOrgIds", carrierOrgIds)
                .setParameter("totalWeight", criteria.getTotalWeight())
                .setParameter("milemakerMiles", criteria.getMilemakerMiles(), IntegerType.INSTANCE)
                .setParameter("pcmilerMiles", criteria.getPcmilerMiles(), IntegerType.INSTANCE);

        setOriginCriteria(bmProfileQuery, criteria);
        setDestCriteria(bmProfileQuery, criteria);
        setCommodityClass(criteria, bmProfileQuery);

        return bmProfileQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class)).list();
    }

    @Override
    public List<LtlRatingProfileVO> getCarrierRates(GetOrderRatesCO criteria, List<Long> palletCarriers) {
        Query carrProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_CARRIER_RATES)
                .setParameter("totalWeight", criteria.getTotalWeight())
                .setParameter("milemakerMiles", criteria.getMilemakerMiles(), IntegerType.INSTANCE)
                .setParameter("pcmilerMiles", criteria.getPcmilerMiles(), IntegerType.INSTANCE)
                .setParameter("shipperOrgId", criteria.getShipperOrgId(), LongType.INSTANCE)
                .setParameter("movementType", getEnumNameAsString(criteria.getMovementType()), StringType.INSTANCE)
                .setParameter("shipDate", criteria.getShipDate())
                .setParameter("carrierOrgId", criteria.getCarrierOrgId(), LongType.INSTANCE)
                .setParameter("serviceType", getEnumNameAsString(criteria.getServiceType()), StringType.INSTANCE)
                .setParameterList("palletCarriers", palletCarriers)
                .setString("specificProfiles", CollectionUtils.isEmpty(criteria.getPricingProfileIDs()) ? "N" : "Y")
                .setParameterList("pricingProfileIDs",
                        CollectionUtils.isEmpty(criteria.getPricingProfileIDs()) ? Arrays.asList(-1L) : criteria.getPricingProfileIDs());

        setOriginCriteria(carrProfQuery, criteria);
        setDestCriteria(carrProfQuery, criteria);
        setCommodityClass(criteria, carrProfQuery);

        return carrProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class)).list();
    }

    @Override
    public List<LtlRatingProfileVO> getCarrierRatesWithUseBlanket(GetOrderRatesCO criteria, List<Long> carriersWithPricing,
            List<Long> pricProfDtlId) {

        if (carriersWithPricing.isEmpty()) {
            carriersWithPricing.add(-1L);
        }
        if (pricProfDtlId.isEmpty()) {
            pricProfDtlId.add(-1L);
        }
        Query query = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_CARRIER_RATES_WITH_USE_BLANKET);
        query.setParameter("shipperOrgId", criteria.getShipperOrgId(), LongType.INSTANCE);
        query.setParameter("shipDate", criteria.getShipDate());
        query.setParameterList("pricProfDtlId", pricProfDtlId);
        query.setParameterList("carriersWithPricing", carriersWithPricing);
        query.setParameter("carrierId", criteria.getCarrierOrgId() == null ? -1 : criteria.getCarrierOrgId());
        query.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class));
        return query.list();
    }

    @Override
    public List<LtlRatingProfileVO> getBlanketAPIProfiles(Long shipperOrgId, Date shipDate) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_BLANKET_API_PROFILES);
        query.setParameter("shipperOrgId", shipperOrgId, LongType.INSTANCE);
        query.setParameter("shipDate", shipDate);
        query.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class));
        return query.list();
    }
    
    @Override
    public List<LtlRatingProfileVO> getLTLLCProfiles(GetOrderRatesCO criteria) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_LTLLC_PROFILES);
        query.setParameter("shipperOrgId", criteria.getShipperOrgId(), LongType.INSTANCE);
        query.setParameter("carrierOrgId", criteria.getCarrierOrgId(), LongType.INSTANCE);
        query.setParameter("shipDate", criteria.getShipDate());
        query.setString("specificProfiles", CollectionUtils.isEmpty(criteria.getPricingProfileIDs()) ? "N" : "Y");
        query.setParameterList("pricingProfileIDs",
                CollectionUtils.isEmpty(criteria.getPricingProfileIDs()) ? Arrays.asList(-1L) : criteria.getPricingProfileIDs());
        setOriginCriteria(query, criteria);
        setDestCriteria(query, criteria);
        
        query.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class));
        return query.list();
    }
    
    @Override
    public List<LtlRatingProfileLTLLCSummaryVO> getLTLLCProfileSummaries(GetOrderRatesCO criteria){
        Query query = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_LTLLC_PROFILE_SUMMARIES);
        query.setParameter("shipperOrgId", criteria.getShipperOrgId(), LongType.INSTANCE);
        query.setParameter("carrierOrgId", criteria.getCarrierOrgId(), LongType.INSTANCE);
        query.setParameter("shipDate", criteria.getShipDate());
        
        query.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileLTLLCSummaryVO.class));
        return query.list();
    }

    @Override
    public List<LtlRatingFSTriggerVO> getFSTriggers(GetOrderRatesCO criteria, Set<Long> profileDetailIds, Map<FuelWeekDays, Date> fsEffDays) {
        Query fsProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_FS_TRIGGERS)
                .setParameter("shipDate", criteria.getShipDate())
                .setParameterList("profileDetailIds", profileDetailIds) //get Profile detail ids from previous query - pricing details query.
                .setParameter("monday", fsEffDays.get(FuelWeekDays.MON))
                .setParameter("tuesday", fsEffDays.get(FuelWeekDays.TUE))
                .setParameter("wednesday", fsEffDays.get(FuelWeekDays.WED))
                .setParameter("thursday", fsEffDays.get(FuelWeekDays.THU))
                .setParameter("friday", fsEffDays.get(FuelWeekDays.FRI))
                .setParameter("saturday", fsEffDays.get(FuelWeekDays.SAT))
                .setParameter("sunday", fsEffDays.get(FuelWeekDays.SUN))
                .setParameter("last_week_monday", fsEffDays.get(FuelWeekDays.MON_1))
                .setParameter("last_week_tuesday", fsEffDays.get(FuelWeekDays.TUE_1))
                .setParameter("last_week_wednesday", fsEffDays.get(FuelWeekDays.WED_1))
                .setParameter("last_week_thursday", fsEffDays.get(FuelWeekDays.THU_1))
                .setParameter("last_week_friday", fsEffDays.get(FuelWeekDays.FRI_1))
                .setParameter("last_week_saturday", fsEffDays.get(FuelWeekDays.SAT_1))
                .setParameter("last_week_sunday", fsEffDays.get(FuelWeekDays.SUN_1));

        setOriginCriteria(fsProfQuery, criteria);

        return fsProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingFSTriggerVO.class)).list();
    }

    @Override
    public List<LtlRatingGuaranteedVO> getGuaranteedRates(GetOrderRatesCO criteria, Set<Long> profileDetailIds) {
        Query guaranProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_GUARANTEED_RATES)
                .setParameter("movementType", getEnumNameAsString(criteria.getMovementType()))
                .setParameter("totalWeight", criteria.getTotalWeight())
                .setParameter("milemakerMiles", criteria.getMilemakerMiles(), IntegerType.INSTANCE)
                .setParameter("pcmilerMiles", criteria.getPcmilerMiles(), IntegerType.INSTANCE)
                .setParameter("guaranteed", criteria.getGuaranteedTime())
                .setParameter("shipDate", criteria.getShipDate())
                .setParameterList("profileDetailIds", profileDetailIds); //get Profile detail ids from previous query - pricing details query.

        setOriginCriteria(guaranProfQuery, criteria);
        setDestCriteria(guaranProfQuery, criteria);

        return guaranProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingGuaranteedVO.class)).list();
    }

    @Override
    public List<LtlRatingAccessorialsVO> getAccessorialRates(GetOrderRatesCO criteria, boolean isSpecificAccessorials, Set<Long> profileDetailIds) {
        Query accProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_ACCESSORIAL_RATES)
                .setParameterList("profileDetailIds", profileDetailIds) //get Profile detail ids from previous query - pricing details query.
                .setParameter("movementType", getEnumNameAsString(criteria.getMovementType()))
                .setParameter("shipDate", criteria.getShipDate())
                .setParameter("totalWeight", criteria.getTotalWeight())
                .setParameter("milemakerMiles", criteria.getMilemakerMiles(), IntegerType.INSTANCE)
                .setParameter("pcmilerMiles", criteria.getPcmilerMiles(), IntegerType.INSTANCE)
                .setParameter("maxLength", criteria.getMaxLength())
                .setParameter("maxWidth", criteria.getMaxWidth());

        setOriginCriteria(accProfQuery, criteria);
        setDestCriteria(accProfQuery, criteria);

        if (isSpecificAccessorials) {
            List<String> accessorials = criteria.getAccessorialTypes();
            if (criteria.getMaterials().stream().anyMatch(m -> m.getLength() != null || m.getWidth() != null || m.getHeight() != null)) {
                accessorials = new ArrayList<>();
                accessorials.add(LtlAccessorialType.OVER_DIMENSION.getCode());
                if (!CollectionUtils.isEmpty(criteria.getAccessorialTypes())) {
                    accessorials.addAll(criteria.getAccessorialTypes());
                }
            }
            accProfQuery.setParameterList("accessorialTypes", accessorials);
            accProfQuery.setParameter("specificAccessorials", 1);
        } else {
            accProfQuery.setParameterList("accessorialTypes", Arrays.asList("FAKE_ACC_TYPE"));
            accProfQuery.setParameter("specificAccessorials", 0);
        }

        return accProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingAccessorialsVO.class)).list();
    }

    @Override
    public List<LtlRatingProfileVO> getPalletRates(GetOrderRatesCO criteria, List<PricingType> pricingTypes, Set<Long> bmCarrierOrgIds) {
        Query palletProfQuery = sessionFactory.getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_PALLET_RATES)
                // get Pallet pricing only for these pricing types.
                .setParameterList("pricingTypes", pricingTypes.stream().collect(Collectors.mapping(Enum::name, Collectors.toList())))
                .setParameter("shipperOrgId", criteria.getShipperOrgId(), LongType.INSTANCE)
                .setParameter("totalQuantity", criteria.getTotalQuantity())
                .setParameter("movementType", getEnumNameAsString(criteria.getMovementType()))
                .setParameter("totalWeight", criteria.getTotalWeight())
                .setParameter("shipDate", criteria.getShipDate())
                .setParameterList("bmCarrierOrgIds", CollectionUtils.isEmpty(bmCarrierOrgIds) ? Arrays.asList(-1L) : bmCarrierOrgIds)
                .setParameter("isBenchmarkOnly", pricingTypes.size() == 1 && PricingType.BENCHMARK == pricingTypes.get(0) ? 1 : 0)
                .setParameter("serviceType", getEnumNameAsString(criteria.getServiceType()), StringType.INSTANCE)
                .setParameter("carrierOrgId", criteria.getCarrierOrgId(), LongType.INSTANCE);

        setOriginCriteria(palletProfQuery, criteria);
        setDestCriteria(palletProfQuery, criteria);

        return palletProfQuery.setResultTransformer(Transformers.aliasToBean(LtlRatingProfileVO.class)).list();
    }

    private void setOriginCriteria(Query query, GetOrderRatesCO criteria) {
        AddressVO originAddress = criteria.getOriginAddress();
        if ("USA".equalsIgnoreCase(originAddress.getCountryCode()) || "MEX".equalsIgnoreCase(originAddress.getCountryCode())) {
            query.setParameter("origin3DigitZip", originAddress.getFormattedPostalCode().substring(0, 3));
        } else if ("CAN".equalsIgnoreCase(originAddress.getCountryCode())) {
            query.setParameter("origin3DigitZip", originAddress.getFormattedPostalCode().substring(0, 6));
        }
        query.setParameter("originZip", originAddress.getFormattedPostalCode());
        query.setParameter("originCity", originAddress.getCity().replaceAll(" ", ""));
        query.setParameter("originState", originAddress.getStateCode());
        query.setParameter("originCountry", originAddress.getCountryCode());
    }

    private void setDestCriteria(Query query, GetOrderRatesCO criteria) {
        AddressVO destAddress = criteria.getDestinationAddress();
        if ("USA".equalsIgnoreCase(destAddress.getCountryCode()) || "MEX".equalsIgnoreCase(destAddress.getCountryCode())) {
            query.setParameter("dest3DigitZip", destAddress.getFormattedPostalCode().substring(0, 3));
        } else if ("CAN".equalsIgnoreCase(destAddress.getCountryCode())) {
            query.setParameter("dest3DigitZip", destAddress.getFormattedPostalCode().substring(0, 6));
        }
        query.setParameter("destZip", destAddress.getFormattedPostalCode());
        query.setParameter("destCity", destAddress.getCity().replaceAll(" ", ""));
        query.setParameter("destState", destAddress.getStateCode());
        query.setParameter("destCountry", destAddress.getCountryCode());
    }

    /*
     * To fetch Carrier Liability - Used/New amount if there is a single class.
     * And to fetch FAK mapping info.
     */
    private void setCommodityClass(GetOrderRatesCO criteria, Query query) {
        if (criteria.getCommodityClassSet() != null && criteria.getCommodityClassSet().size() == 1) {
            query.setParameter("commodityClassEnum", criteria.getCommodityClassSet().iterator().next().name());
        } else {
            query.setParameter("commodityClassEnum", null, StringType.INSTANCE);
        }
    }

    private String getEnumNameAsString(Enum<?> e) {
        return e == null ? null : e.name();
    }
}
