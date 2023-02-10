package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.LtlPricNwDfltMarginDAO;
import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.organization.LtlPricNwDfltMarginEntity;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.StatusYesNo;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.dao.LtlRatingEngineDao;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;
import com.pls.ltlrating.domain.enums.FuelWeekDays;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.service.LTLLifecycleIntegrationService;
import com.pls.ltlrating.service.LtlCarrierAPIService;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.service.LtlSMC3Service;
import com.pls.ltlrating.service.PricingResultsBuilderService;
import com.pls.ltlrating.shared.AccessorialPricingVO;
import com.pls.ltlrating.shared.CarrierPricingProfilesVO;
import com.pls.ltlrating.shared.CarrierRatingVO;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.LtlRatingAccessorialsVO;
import com.pls.ltlrating.shared.LtlRatingFSTriggerVO;
import com.pls.ltlrating.shared.LtlRatingGuaranteedVO;
import com.pls.ltlrating.shared.LtlRatingMarginVO;
import com.pls.ltlrating.shared.LtlRatingProfileLTLLCSummaryVO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.mileage.service.MileageService;
import com.pls.smc3.dto.ScacRequest;
import com.pls.smc3.dto.ScacResponse;
import com.pls.smc3.dto.TransitRequestDTO;
import com.pls.smc3.dto.TransitResponseDTO;
import com.pls.smc3.service.CarrierTransitClient;

/**
 * Class where we calculate LTL rates for the given parameters.
 *
 * @author Hima Bindu Challa
 */
@Service
@Transactional(readOnly = true)
public class LtlRatingEngineServiceImpl implements LtlRatingEngineService {
    private static final Logger LOG = LoggerFactory.getLogger(LtlRatingEngineServiceImpl.class);

    private static final String LTL_TYPE = "LTL";
    private static final String HAZMAT = "HZC";
    private static final String PALLET_PACKAGE_TYPE = "PLT";
    private static final String SKIDS_PACKAGE_TYPE = "SKD";
    private static final String CARR_CONN_SERV_TYPE_DIRECT = "DIRECT";

    static final String RATING_TYPE_SMC3 = "SMC3";
    static final String RATING_TYPE_API = "CARRIER_API";
    static final String RATING_TYPE_LTLLC = "LTLLC";

    @Autowired
    private OrganizationPricingDao organizationPricingDao;

    @Autowired
    private LtlRatingEngineDao ratingEngineDao;

    @Autowired
    private LtlPricingProfileDao pricingProfileDao;

    @Autowired
    private LtlPricingTerminalInfoDao terminalInfoDao;

    @Autowired
    private CarrierTransitClient transitClient;

    @Autowired
    private MileageService mileageService;

    @Autowired
    private LtlPricNwDfltMarginDAO ltlNetworkDefaultMarginDao;

    @Autowired
    private LtlSMC3Service smc3Service;

    @Autowired
    private LtlCarrierAPIService carrierAPIService;

    @Autowired
    private LTLLifecycleIntegrationService ltlLifecycleIntegrationService;

    @Autowired
    private PricingResultsBuilderService resultsBuilder;
    
    @Autowired
    private UserPermissionsService userPermissionsService;

    @Override
    public List<LtlPricingResult> getRatesSafe(GetOrderRatesCO ratesCO) {
        try {
            return ObjectUtils.defaultIfNull(getRates(ratesCO), Collections.<LtlPricingResult>emptyList());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlPricingResult> getRates(GetOrderRatesCO ratesCO) throws Exception {
        LOG.info("******** START - Pricing ********");
        
        if(!userPermissionsService.hasCapability(Capabilities.REQUEST_VLTL_RATES.name())) {
            ratesCO.setRequestVLTLRates(false);
        }

        OrganizationPricingEntity orgPricing = getOrganizationPricing(ratesCO.getShipperOrgId());
        if (orgPricing == null) {
            return null;
        }
        
        LOG.info("pricing 1 - refining criteria");
        refineCriteria(ratesCO);
        
        LOG.info("pricing - Requesting quotes from LTL-Lifecycle (async)...");
        CompletableFuture<LTLLCQuotesContainer> ltllcQuotesContainerFuture = requestLTLLCRates(ratesCO);
        
        LOG.info("pricing 2 - getting mileage info");
        populateMileageInformation(ratesCO);
        LOG.info("pricing 3 - getting carrier rates based on profiles");
        LtlRatingVO carrierProfileVO = getCarrierRates(ratesCO, getCarrierPricingProfiles(ratesCO), true);
        LOG.info("pricing 4");
        if (carrierProfileVO == null || carrierProfileVO.getCarrierPricingDetails().isEmpty()) {
            return null;
        }

        List<LtlRatingProfileVO> palletRates = null;
        if (ratesCO.getShipperOrgId() != null && ratesCO.isPalletType()) {
            List<PricingType> pricingTypes = Collections.singletonList(PricingType.BENCHMARK);
            palletRates = ratingEngineDao.getPalletRates(ratesCO, pricingTypes, carrierProfileVO.getCarrierPricingDetails().keySet());
        }
        LOG.info("pricing 5");
        removeResultsForCarriersWithBenchmarkPalletRates(palletRates, carrierProfileVO.getCarrierPricingDetails());
        LOG.info("pricing 6 - getting benchmark rates");
        LtlRatingVO benchmarkProfileVO = getBenchmarkRates(ratesCO, palletRates, carrierProfileVO.getCarrierPricingDetails().keySet(),
                orgPricing.getIncludeBenchmarkAcc());
        removeResultsForInvalidGainshareRates(carrierProfileVO, benchmarkProfileVO, orgPricing.getGainshare());
        
        LOG.info("started getting SMC3 rates");
        smc3Service.populateSMC3Rates(ratesCO, carrierProfileVO, benchmarkProfileVO);
        LOG.info("finished getting SMC3 rates");
        
        if (isRequestApi(ratesCO)) {
            LOG.info("started getting carrier API rates");
            carrierAPIService.populateRates(ratesCO, carrierProfileVO);
            LOG.info("finished getting carrier API rates");
        }

        LOG.info("Processing quotes from LTL-Lifecycle");
        populateLTLLCRates(carrierProfileVO, ltllcQuotesContainerFuture);

        removeResultsForInvalidGainshareRates(carrierProfileVO, benchmarkProfileVO, orgPricing.getGainshare());
        LOG.info("pricing 7 - Retrieving customer margin information");
        LtlRatingMarginVO margin = getCustomerMargin(ratesCO, orgPricing);
        LOG.info("pricing 8 - Applying margins and rules to rates");
        return resultsBuilder.getPricingResults(ratesCO, orgPricing, carrierProfileVO, benchmarkProfileVO, margin);
    }
    
    /** Initiate LTL lifecycle quote retrieval, if there is any pricing profile with LTLLC rating type. */
    private CompletableFuture<LTLLCQuotesContainer> requestLTLLCRates(GetOrderRatesCO ratesCO) {
        CompletableFuture<LTLLCQuotesContainer> quotesContainerFuture = CompletableFuture.supplyAsync(()->{
            // find out which scacs we need to get quotes for; mapping of currencyCode to list of SCACs
            Map<String, LinkedList<String>> blanketScacs = new HashMap<String, LinkedList<String>>();
            Map<String, LinkedList<String>> cspScacs = new HashMap<String, LinkedList<String>>();
            
            // getting the scac codes we are probably interested in (so we can do the request to ltllc as soon as possible, to conserve waiting time)
            // this does not take into account profile blockings, and priorities.
            // code later will pick, based on profiles, from the results what is needed.
            List<LtlRatingProfileLTLLCSummaryVO> profileSummaries = ratingEngineDao.getLTLLCProfileSummaries(ratesCO);
            
            for(LtlRatingProfileLTLLCSummaryVO pSummary : profileSummaries) {
                Map<String, LinkedList<String>> scacsList;
                if(pSummary.getPricingType()==PricingType.BLANKET) {
                    scacsList = blanketScacs;
                } else if(pSummary.getPricingType()==PricingType.CSP) {
                    scacsList = cspScacs;
                } else {
                    continue;
                }
                
                String currency = pSummary.getCurrencyCode().name();
                if(!scacsList.containsKey(currency)) {
                    scacsList.put(currency, new LinkedList<String>());
                }
                scacsList.get(currency).add(pSummary.getScac());
            }
            
            LTLLCQuotesContainer quotesContainer = new LTLLCQuotesContainer();
            
            // get rates from ltllc for blanket profiles (e.g. for P44, using default account group)
            for(Entry<String, LinkedList<String>> entry : blanketScacs.entrySet()) {
                if(ratesCO.isRequestLTLRates()) {
                    LOG.info("...Requesting quotes from LTL Lifecycle (Blanket)");
                    quotesContainer.futureRawQuotes.add(ltlLifecycleIntegrationService.getRawQuotesAsync(ratesCO, entry.getValue(), false, entry.getKey(), ShipmentType.LTL));
                }
                if(ratesCO.isRequestVLTLRates()) {
                    LOG.info("...Requesting quotes from LTL Lifecycle (Blanket-VLTL)");
                    quotesContainer.futureRawQuotes.add(ltlLifecycleIntegrationService.getRawQuotesAsync(ratesCO, entry.getValue(), false, entry.getKey(), ShipmentType.VLTL));
                }
            }
            
            // get rates from ltllc for specific customer (e.g. for P44, using a customer specific account group)
            for(Entry<String, LinkedList<String>> entry : cspScacs.entrySet()) {
                if(ratesCO.isRequestLTLRates()) {
                    LOG.info("...Requesting quotes from LTL Lifecycle (CSP)");
                    quotesContainer.futureRawQuotes.add(ltlLifecycleIntegrationService.getRawQuotesAsync(ratesCO, entry.getValue(), true, entry.getKey(), ShipmentType.LTL));
                }
                if(ratesCO.isRequestVLTLRates()) {
                    LOG.info("...Requesting quotes from LTL Lifecycle (CSP-VLTL)");
                    quotesContainer.futureRawQuotes.add(ltlLifecycleIntegrationService.getRawQuotesAsync(ratesCO, entry.getValue(), true, entry.getKey(), ShipmentType.VLTL));
                }
            }
            
            return quotesContainer;
        });
        
       return quotesContainerFuture;
    }

    private static class LTLLCQuotesContainer {
        private LinkedList<CompletableFuture<List<QuoteResultDTO>>> futureRawQuotes = new LinkedList<>();
        
        private Map<LTLLCQuoteKey, List<QuoteResultDTO>> getQuotesMap() throws InterruptedException, ExecutionException{
            Stream<QuoteResultDTO> combinedStream = futureRawQuotes.stream()
                    .map(CompletableFuture::join)
                    .filter(i->i != null)
                    .flatMap(i->i.stream());
            
            return combinedStream.collect(Collectors.groupingBy(i -> new LTLLCQuoteKey(i.getCarrier().getCode(), i.getCurrencyCode(), i.getCustomerId()!=null)));
        }
    }
    
    private void populateLTLLCRates(LtlRatingVO carrierProfileVO, CompletableFuture<LTLLCQuotesContainer> quotesContainerFuture) {
        List<LtlRatingProfileVO> ltllcRatingProfiles = carrierProfileVO.getCarrierPricingDetails().values().stream().flatMap(List::stream)
        .flatMap(this::streamCarrierAndShipperRates)
        .filter(Objects::nonNull)
        .filter(i->RATING_TYPE_LTLLC.equals(i.getRatingCarrierType()))
        .collect(Collectors.toList());

        if(!ltllcRatingProfiles.isEmpty()) {
            try {
                LTLLCQuotesContainer quotesContainer = quotesContainerFuture.get(1, TimeUnit.MINUTES);

                // build scac|currency|isCSP|ltlVltl->quote from ltllc quotes
                Map<LTLLCQuoteKey, List<QuoteResultDTO>> scacToQuoteMap = quotesContainer.getQuotesMap();
                        
                // for each profile, search for quote by scac, apply rates
                Iterator<LtlRatingProfileVO> profilesIterator = ltllcRatingProfiles.iterator();
                while (profilesIterator.hasNext()) {
                    LtlRatingProfileVO rProfile = profilesIterator.next();
                    if(Boolean.TRUE.equals(rProfile.getDispatchWithLtllc())) {
                        rProfile.setIntegrationTypeEnum(CarrierIntegrationType.LTLLC);
                    }
                    
                    String scac = rProfile.getActualCarrierScac() == null ? rProfile.getScac() : rProfile.getActualCarrierScac();
                    
                    LTLLCQuoteKey ltllcQuoteKey = 
                            new LTLLCQuoteKey(scac, rProfile.getCurrencyCode().name(), rProfile.getPricingType() == PricingType.CSP);
                    
                    List<QuoteResultDTO> quoteResults = scacToQuoteMap.get(ltllcQuoteKey);
                    
                    if (quoteResults != null && !quoteResults.isEmpty()) {
                        rProfile.setLtllcResponses(quoteResults);
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Failed to retrieve quotes from LTL lifecycle." , ex);
            }
        }

        removeInvalidLTLLCProfiles(carrierProfileVO);
    }

    private void removeInvalidLTLLCProfiles(LtlRatingVO carrierProfileVO) {
        Iterator<Entry<Long, List<CarrierRatingVO>>> iterator = carrierProfileVO.getCarrierPricingDetails().entrySet().iterator();
        while (iterator.hasNext()) {
            Optional<LtlRatingProfileVO> invalidProfile = iterator.next().getValue().stream().flatMap(this::streamCarrierAndShipperRates)
                    .filter(Objects::nonNull).filter(profile -> LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(profile.getRatingCarrierType())
                            && (profile.getLtllcResponses() == null || profile.getLtllcResponses().isEmpty()) )
                    .findAny();
            if (invalidProfile.isPresent()) {
                LOG.warn("No LTL Lifecycle quote found for following ProfileDetailId: {}, {}", invalidProfile.get().getProfileDetailId(), invalidProfile.get().getScac());
                iterator.remove();
            }
        }
    }

    private Stream<LtlRatingProfileVO> streamCarrierAndShipperRates(CarrierRatingVO r) {
        return Stream.of(r.getRate().getPricingDetails(), r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails());
    }

    private OrganizationPricingEntity getOrganizationPricing(Long shipperOrgId) {
        // Customer is optional
        if (shipperOrgId != null) {
            /*
             * Check if the Customer Pricing is active or not. This is setting on customer pricing screen. If
             * not active, then no carrier profiles will be shown on the Quote/Order screen. Only an error
             * message should be shown saying - "No rating available" or something like that.
             */
            return organizationPricingDao.getActivePricing(shipperOrgId);
        }
        return new OrganizationPricingEntity();
    }

    private LtlRatingVO getBenchmarkRates(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> palletRates, Set<Long> carrierIds,
            StatusYesNo includeBenchmarkAcc) {
        // Benchmarks are always associated with a customer.
        if (ratesCO.getShipperOrgId() != null) {
            // Get all the matching benchmark rate profiles.Note that for benchmark rates, we don't pull
            // accessorials and the accessorials are pass-through values.
            List<LtlRatingProfileVO> benchmarkPricingProfiles = ratingEngineDao.getBenchmarkRates(ratesCO, carrierIds);
            if (CollectionUtils.isNotEmpty(palletRates)) {
                benchmarkPricingProfiles.addAll(palletRates);
            }

            // Get valid BENCHMARK rates and iterate over the BENCHMARK rates for customer and get the base
            // cost.The base cost is also used for Gainshare margin calculation.
            return getCarrierRates(ratesCO, benchmarkPricingProfiles, includeBenchmarkAcc == StatusYesNo.YES);
        }
        return null;
    }

    private void removeResultsForCarriersWithBenchmarkPalletRates(List<LtlRatingProfileVO> palletRates,
            Map<Long, List<CarrierRatingVO>> carrierPricingDetails) {
        if (CollectionUtils.isNotEmpty(palletRates)) {
            palletRates.forEach(palletProfile -> carrierPricingDetails.remove(palletProfile.getCarrierOrgId()));
        }
    }

    private void removeResultsForInvalidGainshareRates(LtlRatingVO carrierProfileVO, LtlRatingVO benchmarkProfileVO, StatusYesNo gainshare) {
        if (gainshare == StatusYesNo.YES) {
            Iterator<Long> iterator = carrierProfileVO.getCarrierPricingDetails().keySet().iterator();
            while (iterator.hasNext()) {
                Long carrierId = iterator.next();
                if (getBenchmarkProfile(benchmarkProfileVO, carrierId) == null) {
                    iterator.remove();
                }
            }
        }
    }

    private LtlRatingMarginVO getCustomerMargin(GetOrderRatesCO ratesCO, OrganizationPricingEntity orgPricing) {
        // Get Customer Margin values to use the same for any profiles.
        LtlRatingMarginVO marginVO = null;
        if (ratesCO.getShipperOrgId() != null && orgPricing.getDefaultMargin() == null) {
            marginVO = getMarginDetails(ratesCO);
        }
        if (marginVO == null) {
            marginVO = new LtlRatingMarginVO();
        }
        BigDecimal defaultMargin = orgPricing.getDefaultMargin();
        BigDecimal defaultMinMarginAmt = orgPricing.getDefaultMinMarginAmt();
        if (ratesCO.getShipperOrgId() == null) {
            // If the Customer information is not defined then get the margin information if defined at
            // Network level for LTL.
            LtlPricNwDfltMarginEntity ltlPricNwDfltMargin = ltlNetworkDefaultMarginDao.getDefaultLTLMargin();
            if (ltlPricNwDfltMargin != null) {
                defaultMargin = ltlPricNwDfltMargin.getMarginPerc();
                defaultMinMarginAmt = ltlPricNwDfltMargin.getMinMarginAmt();
            }
        }
        if (marginVO.getPricingDetail() != null && marginVO.getPricingDetail().getMinMarginFlat() != null) {
            marginVO.setMinMarginFlatAmount(marginVO.getPricingDetail().getMinMarginFlat());
        } else {
            marginVO.setMinMarginFlatAmount(ObjectUtils.defaultIfNull(defaultMinMarginAmt, BigDecimal.ZERO));
        }
        marginVO.setDefaultMarginPercent(ObjectUtils.defaultIfNull(defaultMargin, BigDecimal.ZERO));
        return marginVO;
    }

    private void populateMileageInformation(GetOrderRatesCO ratesCO) throws InterruptedException {
        ExecutorService mileagePool = Executors.newFixedThreadPool(2);
        mileagePool.execute(() -> {
            ratesCO.setMilemakerMiles(mileageService.getMileage(ratesCO.getOriginAddress(),
                    ratesCO.getDestinationAddress(), MileageCalculatorType.MILE_MAKER));

        });
        mileagePool.execute(() -> {
            ratesCO.setPcmilerMiles(mileageService.getMileage(ratesCO.getOriginAddress(),
                    ratesCO.getDestinationAddress(), MileageCalculatorType.PC_MILER));

        });
        mileagePool.shutdown();
        mileagePool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

    private List<LtlRatingProfileVO> getCarrierPricingProfiles(GetOrderRatesCO ratesCO) {
        List<LtlRatingProfileVO> palletRates = null;
        List<Long> pricProfDtlId = new ArrayList<Long>();
        List<Long> carriersWithPricing = new ArrayList<Long>();

        //Get Pallet pricing if the Order Package type = Pallet 40X80.
        if (ratesCO.isPalletType() && ratesCO.isRequestLTLRates()) {
            List<PricingType> pricingTypes = new ArrayList<PricingType>();
            pricingTypes.add(PricingType.BLANKET);
            pricingTypes.add(PricingType.BLANKET_CSP);
            pricingTypes.add(PricingType.CSP);
            pricingTypes.add(PricingType.BUY_SELL);
            palletRates = ratingEngineDao.getPalletRates(ratesCO, pricingTypes, null);
            for (LtlRatingProfileVO palletProfile : palletRates) {
                carriersWithPricing.add(palletProfile.getCarrierOrgId());
                pricProfDtlId.add(palletProfile.getProfileDetailId());
            }
            LOG.info("Got pallet rates");
        }

        if (carriersWithPricing.isEmpty()) {
            carriersWithPricing.add(-1L);
        }

        //Get all the matching carrier rate profiles for the given criteria - Origin/Destination, Mileage, Weight, etc.
        List<LtlRatingProfileVO> carrierRates = ratingEngineDao.getCarrierRates(ratesCO, carriersWithPricing);
        for (LtlRatingProfileVO profile : carrierRates) {
            carriersWithPricing.add(profile.getCarrierOrgId());
            pricProfDtlId.add(profile.getProfileDetailId());
        }
        LOG.info("Got carrier rates");

        if(ratesCO.isRequestLTLRates()) {
            // Get all carrier pricing's where either buy/sell has the use blanket flag set but have only profile
            // defined but not the pricing's. As the earlier query gets the profile only if matching lane
            // exists, but this is not necessary when use blanket flag is set as the corresponding blanket
            // profile defined for this carrier is used.
            carrierRates.addAll(ratingEngineDao.getCarrierRatesWithUseBlanket(ratesCO, carriersWithPricing, pricProfDtlId));
            LOG.info("Got carrier rates with use blanket");
        }

        carrierRates.addAll(getAPIProfiles(ratesCO));
        LOG.info("Got API profiles");

        // Add LTLLC blanket profiles + CSP without geo specific pricings
        carrierRates.addAll(ratingEngineDao.getLTLLCProfiles(ratesCO));
        LOG.info("Got blanket LTLLC profiles");
        
        //set service type for profiles without pricing, to ensure they will not be filtered out in later steps
        for(LtlRatingProfileVO pricingProfile : carrierRates) {
            if(RATING_TYPE_LTLLC.equalsIgnoreCase(pricingProfile.getRatingCarrierType()) && pricingProfile.getServiceType()==null) {
                pricingProfile.setServiceTypeEnum(LtlServiceType.BOTH);
            }
        }

        if (palletRates != null) {
            carrierRates.addAll(palletRates);
        }
        
        //filter out unnsupported profile types if only VLTL requested
        if(!ratesCO.isRequestLTLRates()) {
            carrierRates.removeIf(it -> !LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(it.getRatingCarrierType()));
        }
        
        return carrierRates;
    }

    private List<LtlRatingProfileVO> getAPIProfiles(GetOrderRatesCO ratesCO) {
        if (isRequestApi(ratesCO) && ratesCO.isRequestLTLRates()) {
            return ratingEngineDao.getBlanketAPIProfiles(ratesCO.getShipperOrgId(), ratesCO.getShipDate());
        }
        return Collections.emptyList();
    }

    private boolean isApiProfile(LtlRatingProfileVO pricingProfile) {
        return RATING_TYPE_API.equalsIgnoreCase(pricingProfile.getRatingCarrierType());
    }

    private boolean isLTLLCProfile(LtlRatingProfileVO pricingProfile) {
        return RATING_TYPE_LTLLC.equalsIgnoreCase(pricingProfile.getRatingCarrierType());
    }

    private boolean isRequestApi(GetOrderRatesCO ratesCO) {
        return "CAN".equals(ratesCO.getOriginAddress().getCountryCode()) && "CAN".equals(ratesCO.getDestinationAddress().getCountryCode());
    }

    private LtlRatingVO getCarrierRates(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> carrierProfiles, boolean includeAccessorials) {
        if (CollectionUtils.isEmpty(carrierProfiles)) {
            return null;
        }

        //If any of the buy/sell pricing is using blanket as part of it then the corresponding valid blanket
        //pricing needs to be retrieved and be used in further processing.
        //buySellProfilesToBeOverriden is used to store the list of buy/sell profiles that use blanket as
        //part of it and have corresponding blanket profile available. This list is used in further logic
        //to update some of the blanket profile's FS,GD and Acc details with buy/sell profile's details.
        List<LtlRatingProfileVO> buySellProfilesToBeOverriden = new ArrayList<LtlRatingProfileVO>();
        Iterator<LtlRatingProfileVO> carrierRatesIterator = carrierProfiles.iterator();
        while (carrierRatesIterator.hasNext()) {
            LtlRatingProfileVO buySellCarrRate = carrierRatesIterator.next();
            if ("Y".equalsIgnoreCase(buySellCarrRate.getUseBlanket())) {
                carrierProfiles.stream().filter(r -> isBlanketForBuySell(r, buySellCarrRate)).forEach(blanketRate -> {
                    // there should be only one active blanket profile for carrier, but there can be few rows
                    // with different settings for the same profile based on origin and destination locations
                    buySellCarrRate.setBlanketProfileId(blanketRate.getProfileId());
                    blanketRate.setPricingTypeEnum(buySellCarrRate.getPricingType());
                    blanketRate.setPricingDetailTypeEnum(buySellCarrRate.getPricingDetailType());
                    blanketRate.setProfileId(buySellCarrRate.getProfileId());
                    buySellProfilesToBeOverriden.add(buySellCarrRate);
                });
                carrierRatesIterator.remove();
            }
        }

        Map<String, ScacResponse> carriersTransitMap = getCarriersTransitInformation(ratesCO, carrierProfiles);
        removeUnapplicableProfiles(carrierProfiles, carriersTransitMap);
        updateCarrierProfilesWithTransitInfo(ratesCO, carrierProfiles, carriersTransitMap);

        LtlRatingVO ltlRatingVO = new LtlRatingVO();
        ltlRatingVO.setCarrierPricingDetails(getValidPricingDetails(carrierProfiles));

        /*
         * FS:Fuel Surcharge is super tricky. This logic has to be done in multiple steps.
         * 1. Get all Matching effective days for all weekdays for the given ship date.
         * 2. Get the Fuel trigger values from DB based on the above values and origin.
         */
        if (!ltlRatingVO.getValidCarrierProfileDetailIds().isEmpty()) {
            List<LtlRatingFSTriggerVO> fsTriggers = getFSTriggers(ratesCO, buySellProfilesToBeOverriden, ltlRatingVO);
            updatePricingDetailsWithValidFuelSurcharge(fsTriggers, ltlRatingVO.getCarrierPricingDetails());
        }

        if (includeAccessorials && !ltlRatingVO.getValidCarrierProfileDetailIds().isEmpty()) {
            Map<Long, LtlServiceType> carrierServiceTypeMap = new HashMap<Long, LtlServiceType>();
            carrierProfiles.forEach(pricingProfile -> {
                carrierServiceTypeMap.put(pricingProfile.getCarrierOrgId(), 
                        RATING_TYPE_LTLLC.equalsIgnoreCase(pricingProfile.getRatingCarrierType()) ? LtlServiceType.BOTH : getServiceType(pricingProfile, carriersTransitMap)
                        );
            });
            //Again Check if guaranteed service requested and if yes, get them.
            if (ratesCO.getGuaranteedTime() != null) {
                List<LtlRatingGuaranteedVO> guaranteedRates = getGuaranteedRates(ratesCO, buySellProfilesToBeOverriden, ltlRatingVO);
                updateRatingVoWithValidGuaranteedRates(guaranteedRates, ratesCO, ltlRatingVO, carrierServiceTypeMap);
            }

            if (isCalculateLtlAccessorials(ratesCO)) {
                updateRatingVOWithValidAccessorials(ratesCO, ltlRatingVO, buySellProfilesToBeOverriden, carrierServiceTypeMap, true);
            }
            updateRatingVOWithValidAccessorials(ratesCO, ltlRatingVO, buySellProfilesToBeOverriden, carrierServiceTypeMap, false);
        }

        return ltlRatingVO;
    }

    private boolean isCalculateLtlAccessorials(GetOrderRatesCO ratesCO) {
    	boolean check = CollectionUtils.isNotEmpty(ratesCO.getAccessorialTypes()) || isImplicitOverDimentionalAccessorial(ratesCO);
        return CollectionUtils.isNotEmpty(ratesCO.getAccessorialTypes()) || isImplicitOverDimentionalAccessorial(ratesCO);
    }

    private void updateCarrierProfilesWithTransitInfo(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> carrierProfiles,
            Map<String, ScacResponse> carriersTransitMap) {
        Iterator<LtlRatingProfileVO> iterator = carrierProfiles.iterator();
        while (iterator.hasNext()) {
            LtlRatingProfileVO pricingProfile = iterator.next();
            if (!pricingProfile.getCarrierOrgId().equals(-1L)) {
                if(RATING_TYPE_LTLLC.equalsIgnoreCase(pricingProfile.getRatingCarrierType())) {
                    String scac = ObjectUtils.defaultIfNull(pricingProfile.getActualCarrierScac(), pricingProfile.getScac());
                    
                    if (carriersTransitMap.containsKey(scac)) {
                        // we will use CarrierConnect provided transit time as fallback in event ltllc does not provide it.
                        Integer transitTime = carriersTransitMap.get(scac).getDays();
                        if (transitTime != null) {
                            pricingProfile.setTransitTime(transitTime);
                            pricingProfile.setTransitDate(getTransitDate(ratesCO.getShipDate(), transitTime));
                        }
                    }
                    continue;
                }

                Integer transitTime = getTransitTime(pricingProfile, carriersTransitMap);
                if (transitTime == null || transitTime > LtlRatingEngineService.MAX_TRANSIT_TIME) {
                    iterator.remove();
                }
                pricingProfile.setTransitTime(transitTime);
                pricingProfile.setTransitDate(getTransitDate(ratesCO.getShipDate(), transitTime));
            }
        }
    }

    private List<LtlRatingGuaranteedVO> getGuaranteedRates(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> buySellProfilesToBeOverriden,
            LtlRatingVO ltlRatingVO) {
        List<LtlRatingGuaranteedVO> guaranteedRates = ratingEngineDao.getGuaranteedRates(ratesCO,
                ltlRatingVO.getValidCarrierProfileDetailIds());
        //Update the blanket guaranteed pricing's details that are to be used as part of that buy/sell pricing.
        for (LtlRatingGuaranteedVO guaranteedRate : guaranteedRates) {
            LtlRatingProfileVO buySellProfile = buySellProfilesToBeOverriden.stream()
                    .filter(p -> p.getBlanketProfileId().equals(guaranteedRate.getProfileId())).findFirst().orElse(null);
            if (buySellProfile != null) {
                guaranteedRate.setProfileId(buySellProfile.getProfileId());
                guaranteedRate.setPricingTypeEnum(buySellProfile.getPricingType());
                guaranteedRate.setProfileDetailTypeEnum(buySellProfile.getPricingDetailType());
            }
        }
        return guaranteedRates;
    }

    private List<LtlRatingFSTriggerVO> getFSTriggers(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> buySellProfilesToBeOverriden,
            LtlRatingVO ltlRatingVO) {
        List<LtlRatingFSTriggerVO> fsTriggers = new ArrayList<LtlRatingFSTriggerVO>();
        fsTriggers.addAll(getFsTriggersFromDB(ltlRatingVO, ratesCO));
        ltlRatingVO.getCarrierPricingDetails().values().stream().flatMap(List::stream)
                .forEach(ratingVO -> {
                    if (BooleanUtils.isTrue(ratingVO.getRate().getPricingDetails().getIsExcludeFuel())) {
                        fsTriggers.add(buildFsTriggerVoForProfWithExcludeFuel(ratingVO.getRate().getPricingDetails()));
                    }
                    if (ratingVO.getShipperRate() != null
                            && BooleanUtils.isTrue(ratingVO.getShipperRate().getPricingDetails().getIsExcludeFuel())) {
                        fsTriggers.add(buildFsTriggerVoForProfWithExcludeFuel(ratingVO.getShipperRate().getPricingDetails()));
                    }
                });
        // Update the blanket fuel pricing's details that are to be used as part of that buy/sell pricing.
        for (LtlRatingFSTriggerVO fsTrigger : fsTriggers) {
            LtlRatingProfileVO buySellProfile = buySellProfilesToBeOverriden.stream()
                    .filter(p -> p.getBlanketProfileId().equals(fsTrigger.getProfileId())).findFirst().orElse(null);
            if (buySellProfile != null) {
                fsTrigger.setProfileId(buySellProfile.getProfileId());
                fsTrigger.setPricingTypeEnum(buySellProfile.getPricingType());
                fsTrigger.setProfileDetailTypeEnum(buySellProfile.getPricingDetailType());
            }
        }
        return fsTriggers;
    }

    private boolean isBlanketForBuySell(LtlRatingProfileVO carrierRate, LtlRatingProfileVO buySellCarrRate) {
        return !carrierRate.getProfileId().equals(buySellCarrRate.getProfileId())
                && carrierRate.getCarrierOrgId().equals(buySellCarrRate.getCarrierOrgId())
                && carrierRate.getPricingType() == PricingType.BLANKET;
    }

    private void updateRatingVOWithValidAccessorials(GetOrderRatesCO ratesCO, LtlRatingVO ltlRatingVO,
            List<LtlRatingProfileVO> buySellProfilesToBeOverriden, Map<Long, LtlServiceType> carrierServiceTypeMap, boolean ltlAccessorials) {
        if (!ltlRatingVO.getValidCarrierProfileDetailIds().isEmpty()) {
            List<LtlRatingAccessorialsVO> accessorials = ratingEngineDao.getAccessorialRates(ratesCO, ltlAccessorials,
                    ltlRatingVO.getValidCarrierProfileDetailIds());
            // Update the blanket accessorials pricing's detail that are to be used as part of that buy/sell pricing.
            for (LtlRatingProfileVO buySellProfile : buySellProfilesToBeOverriden) {
                for (LtlRatingAccessorialsVO acc : accessorials) {
                    if (buySellProfile.getBlanketProfileId().equals(acc.getProfileId())) {
                        acc.setProfileId(buySellProfile.getProfileId());
                        acc.setPricingTypeEnum(buySellProfile.getPricingType());
                        acc.setProfileDetailTypeEnum(buySellProfile.getPricingDetailType());
                    }
                }
            }
            updatePricingDetailsWithValidAccessorialRates(accessorials, ratesCO, ltlRatingVO.getCarrierPricingDetails(), ltlAccessorials,
                    carrierServiceTypeMap);
        }
    }

    private List<LtlRatingFSTriggerVO> getFsTriggersFromDB(LtlRatingVO ltlRatingVO, GetOrderRatesCO ratesCO) {
        if (!ltlRatingVO.getValidCarrierProfileDetailIds().isEmpty()) {
            Map<FuelWeekDays, Date> publishedDates = FuelSurchargeHelper.getPublishedDates(ratesCO.getShipDate());
            return ratingEngineDao.getFSTriggers(ratesCO, ltlRatingVO.getValidCarrierProfileDetailIds(), publishedDates);
        }
        return Collections.emptyList();
    }

    private LtlRatingFSTriggerVO buildFsTriggerVoForProfWithExcludeFuel(LtlRatingProfileVO profileVO) {
        LtlRatingFSTriggerVO fsTrigger = new LtlRatingFSTriggerVO();
        fsTrigger.setProfileId(profileVO.getProfileId());
        fsTrigger.setProfileDetailId(profileVO.getProfileDetailId());
        fsTrigger.setCarrierOrgId(profileVO.getCarrierOrgId());
        fsTrigger.setProfileDetailTypeEnum(profileVO.getPricingDetailType());
        fsTrigger.setPricingTypeEnum(profileVO.getPricingType());
        fsTrigger.setIsExcludeFuel(profileVO.getIsExcludeFuel());
        return fsTrigger;
    }

    private CarrierRatingVO getBenchmarkProfile(LtlRatingVO benchmarkProfileVO, Long carrierOrgId) {
        if (benchmarkProfileVO != null) {
            List<CarrierRatingVO> details = benchmarkProfileVO.getCarrierPricingDetails().get(carrierOrgId);
            if (details == null) {
                details = benchmarkProfileVO.getCarrierPricingDetails().get(-1L);
            }
            if (CollectionUtils.isNotEmpty(details)) {
                return details.get(0); // no problem, there is definitely only one (Benchmark) profile in the list
            }
        }
        return null;
    }

    private Map<Long, List<CarrierRatingVO>> getValidPricingDetails(List<LtlRatingProfileVO> rates) {
        Map<Long, List<CarrierRatingVO>> carrierPricingDetails = new HashMap<Long, List<CarrierRatingVO>>();

        for (LtlRatingProfileVO rate : rates) {
            Long orgId = ObjectUtils.defaultIfNull(rate.getCarrierOrgId(), -1L);
            if (!carrierPricingDetails.containsKey(orgId)) {
                carrierPricingDetails.put(orgId, new ArrayList<CarrierRatingVO>());
            }
            CarrierRatingVO carrierRatingVO = carrierPricingDetails.get(orgId).stream().filter(d -> d.getPricingType() == rate.getPricingType())
                    .findFirst().orElse(null);
            if (carrierRatingVO == null) {
                carrierRatingVO = new CarrierRatingVO();
                carrierRatingVO.setPricingType(rate.getPricingType());
                carrierRatingVO.setProfileId(rate.getProfileId());
                carrierPricingDetails.get(orgId).add(carrierRatingVO);
            }
            boolean isShipperRate = PricingType.BUY_SELL == rate.getPricingType() && rate.getPricingDetailType() == PricingDetailType.SELL;
            CarrierPricingProfilesVO profilesVO = isShipperRate ? carrierRatingVO.getShipperRate() : carrierRatingVO.getRate();
            if (profilesVO == null) {
                profilesVO = new CarrierPricingProfilesVO();
                if (isShipperRate) {
                    carrierRatingVO.setShipperRate(profilesVO);
                } else {
                    carrierRatingVO.setRate(profilesVO);
                }
            }
            if (profilesVO.getPricingDetails() == null || profilesVO.getPricingDetails().getGeoLevel() > rate.getGeoLevel()) {
                profilesVO.setPricingDetails(rate);
            }
        }
        removeInvalidCarrierRates(carrierPricingDetails,
                rate -> rate.getRate() == null || rate.getRate().getPricingDetails() == null || rate.getPricingType() == PricingType.BUY_SELL
                        && (rate.getShipperRate() == null || rate.getShipperRate().getPricingDetails() == null));

        return carrierPricingDetails;
    }

    private void removeInvalidCarrierRates(Map<Long, List<CarrierRatingVO>> carrierPricingDetails,
            Function<CarrierRatingVO, Boolean> isInvalidProfile) {
        Iterator<Entry<Long, List<CarrierRatingVO>>> it = carrierPricingDetails.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, List<CarrierRatingVO>> next = it.next();
            List<CarrierRatingVO> ratings = next.getValue();
            Iterator<CarrierRatingVO> ratingsIt = ratings.iterator();
            while (ratingsIt.hasNext()) {
                CarrierRatingVO rate = ratingsIt.next();
                if (isInvalidProfile.apply(rate)) {
                    ratingsIt.remove();
                }
            }
            if (ratings.isEmpty()) {
                it.remove();
            }
        }
    }

    private boolean isInvalidFuelSurcharge(CarrierPricingProfilesVO rate) {
        return rate != null && rate.getFuelSurcharge() == null && !(isApiProfile(rate.getPricingDetails()) || isLTLLCProfile(rate.getPricingDetails()) );
    }

    private void updatePricingDetailsWithValidFuelSurcharge(List<LtlRatingFSTriggerVO> rates, Map<Long, List<CarrierRatingVO>> pricingDetails) {
        for (LtlRatingFSTriggerVO rate : rates) {
            Long orgId = ObjectUtils.defaultIfNull(rate.getCarrierOrgId(), -1L);
            CarrierRatingVO carrierRatingVO = pricingDetails.get(orgId).stream()
                    .filter(d -> d.getProfileId().equals(rate.getProfileId())).findFirst().orElse(null);
            if (carrierRatingVO != null) {
                boolean isShipperRate = PricingType.BUY_SELL == rate.getPricingType() && rate.getProfileDetailType() == PricingDetailType.SELL;
                CarrierPricingProfilesVO profilesVO = isShipperRate ? carrierRatingVO.getShipperRate() : carrierRatingVO.getRate();
                if (profilesVO != null && (profilesVO.getFuelSurcharge() == null || BooleanUtils.isTrue(rate.getIsExcludeFuel())
                        || profilesVO.getFuelSurcharge().getGeoLevel() > rate.getGeoLevel())) {
                    profilesVO.setFuelSurcharge(rate);
                }
            }
        }
        removeInvalidCarrierRates(pricingDetails,
                rate -> isInvalidFuelSurcharge(rate.getRate()) || isInvalidFuelSurcharge(rate.getShipperRate()));
    }

    private boolean isInvalidGuaranteedPricing(CarrierPricingProfilesVO rate, GetOrderRatesCO ratesCO) {
        // for now we don't allow guaranteed deliveries for API pricing types as we don't know how to get this data from carriers.
        // We allow LTL lifecycle pricing types.
        return rate != null && (!rate.getGuaranteedPricing().containsKey(ratesCO.getGuaranteedTime()) || isApiProfile(rate.getPricingDetails())) && !isLTLLCProfile(rate.getPricingDetails());
    }

    private void updateRatingVoWithValidGuaranteedRates(List<LtlRatingGuaranteedVO> rates, GetOrderRatesCO ratesCO,
            LtlRatingVO ltlRatingVO, Map<Long, LtlServiceType> carrierServiceTypeMap) {
        for (LtlRatingGuaranteedVO guaranteedRate : rates) {
            Long orgId = ObjectUtils.defaultIfNull(guaranteedRate.getCarrierOrgId(), -1L);
            CarrierRatingVO carrierRatingVO = ltlRatingVO.getCarrierPricingDetails().get(orgId).stream()
                    .filter(d -> d.getProfileId().equals(guaranteedRate.getProfileId())).findFirst().orElse(null);
            if (carrierRatingVO != null) {
                LtlServiceType carrierServiceType = carrierServiceTypeMap.get(orgId);
                LtlServiceType rateServiceType = ObjectUtils.defaultIfNull(guaranteedRate.getServiceType(), LtlServiceType.BOTH);

                // If the Carrier Service Type returned by CarrierConnect does not match with the Service Type
                // of the rate, then ignore the rate.
                if (LtlServiceType.BOTH == carrierServiceType || LtlServiceType.BOTH == rateServiceType
                        || carrierServiceType.equals(rateServiceType)) {
                    boolean isShipperRate = PricingType.BUY_SELL == guaranteedRate.getPricingType()
                            && guaranteedRate.getProfileDetailType() == PricingDetailType.SELL;
                    CarrierPricingProfilesVO profilesVO = isShipperRate ? carrierRatingVO.getShipperRate() : carrierRatingVO.getRate();
                    profilesVO.getGuaranteedPricing().put(guaranteedRate.getTime(), guaranteedRate);
                }
            }
        }
        removeIncorrectGuaranteedRates(ltlRatingVO, ratesCO.getMovementType());
        removeInvalidCarrierRates(ltlRatingVO.getCarrierPricingDetails(),
                rate -> isInvalidGuaranteedPricing(rate.getRate(), ratesCO) || isInvalidGuaranteedPricing(rate.getShipperRate(), ratesCO));
    }

    private void removeIncorrectGuaranteedRates(LtlRatingVO ltlRatingVO, MoveType movementType) {
        boolean specificMovementTypeExists = ltlRatingVO.getCarrierPricingDetails().values().stream().flatMap(List::stream)
                .flatMap(r -> Stream.concat(r.getRate().getGuaranteedPricing().values().stream(),
                        r.getShipperRate() == null ? Stream.empty() : r.getShipperRate().getGuaranteedPricing().values().stream()))
                .anyMatch(r -> r.getMovementType() == movementType);
        if (specificMovementTypeExists) {
            ltlRatingVO.getCarrierPricingDetails().values().stream().flatMap(List::stream)
                    .flatMap(r -> Stream.of(r.getRate().getGuaranteedPricing(),
                            r.getShipperRate() == null ? null : r.getShipperRate().getGuaranteedPricing()))
                    .filter(Objects::nonNull).forEach(map -> map.entrySet().removeIf(e -> e.getValue().getMovementType() == MoveType.BOTH));
        }
        ltlRatingVO.getCarrierPricingDetails().values().stream().flatMap(List::stream).forEach(vo -> {
            if (vo.getPricingType() == PricingType.BUY_SELL) {
                // remove guaranteed time if it exists only in one buy or sell rate
                Collection<?> disjunction = CollectionUtils.disjunction(vo.getRate().getGuaranteedPricing().keySet(),
                        vo.getShipperRate().getGuaranteedPricing().keySet());
                vo.getRate().getGuaranteedPricing().keySet().removeAll(disjunction);
                vo.getShipperRate().getGuaranteedPricing().keySet().removeAll(disjunction);
            }
        });
    }

    private void updatePricingDetailsWithValidAccessorialRates(List<LtlRatingAccessorialsVO> rates, GetOrderRatesCO ratesCO,
            Map<Long, List<CarrierRatingVO>> pricingDetails, boolean ltlAccessorials, Map<Long, LtlServiceType> carrierServiceTypeMap) {
        for (LtlRatingAccessorialsVO rate : rates) {
            Long orgId = ObjectUtils.defaultIfNull(rate.getCarrierOrgId(), -1L);
            CarrierRatingVO carrierRatingVO = pricingDetails.get(orgId).stream()
                    .filter(d -> d.getProfileId().equals(rate.getProfileId())).findFirst().orElse(null);
            if (carrierRatingVO != null && isValidServiceType(rate.getServiceType(), carrierServiceTypeMap, orgId)) {
                boolean isShipperRate = PricingType.BUY_SELL == rate.getPricingType() && rate.getProfileDetailType() == PricingDetailType.SELL;
                CarrierPricingProfilesVO profilesVO = isShipperRate ? carrierRatingVO.getShipperRate() : carrierRatingVO.getRate();
                AccessorialPricingVO accessorial = getAccessorial(profilesVO, rate, ltlAccessorials);
                if (accessorial.getGeoLevel() == rate.getGeoLevel()) {
                    accessorial.getPrices().add(rate);
                } else if (accessorial.getGeoLevel() > rate.getGeoLevel()) {
                    accessorial.setGeoLevel(rate.getGeoLevel());
                    accessorial.getPrices().clear();
                    accessorial.getPrices().add(rate);
                }
            }
        }

        removeIncorrectAccessorialsRates(pricingDetails, ltlAccessorials, ratesCO.getMovementType());
        if (ltlAccessorials) {
            int accessorialsCount = CollectionUtils.size(ratesCO.getAccessorialTypes());
            if (isImplicitOverDimentionalAccessorial(ratesCO)) {
                accessorialsCount++;
            }
            final int count = accessorialsCount;
            removeInvalidCarrierRates(pricingDetails,
                    rate -> !(isApiProfile(rate.getRate().getPricingDetails()) || isLTLLCProfile(rate.getRate().getPricingDetails())) && (rate.getRate().getLtlAccessorials().size() != count
                    || (rate.getShipperRate() != null && rate.getShipperRate().getLtlAccessorials().size() != count)));
        }
    }

    /*
     * If the Carrier Service Type returned by CarrierConnect does not match with the Service Type of the
     * rate, then ignore the rate.
     */
    private boolean isValidServiceType(LtlServiceType serviceType, Map<Long, LtlServiceType> carrierServiceTypeMap, Long orgId) {
        LtlServiceType carrierServiceType = carrierServiceTypeMap != null ? carrierServiceTypeMap.get(orgId) : LtlServiceType.BOTH;
        LtlServiceType rateServiceType = ObjectUtils.defaultIfNull(serviceType, LtlServiceType.BOTH);
        return carrierServiceTypeMap == null || LtlServiceType.BOTH == carrierServiceType || LtlServiceType.BOTH == rateServiceType
                || rateServiceType.equals(carrierServiceType);
    }

    private void removeIncorrectAccessorialsRates(Map<Long, List<CarrierRatingVO>> pricingDetails, boolean ltlAccessorials, MoveType movementType) {
        boolean specificMovementTypeExists = pricingDetails.values().stream().flatMap(List::stream)
                .flatMap(r -> Stream.concat(getAccessorials(r.getRate(), ltlAccessorials).values().stream(),
                        r.getShipperRate() == null ? Stream.empty() : getAccessorials(r.getShipperRate(), ltlAccessorials).values().stream()))
                .flatMap(r -> r.getPrices().stream())
                .anyMatch(r -> r.getMovementType() == movementType);
        if (specificMovementTypeExists) {
            pricingDetails.values().stream().flatMap(List::stream)
                    .flatMap(r -> Stream.of(getAccessorials(r.getRate(), ltlAccessorials),
                            r.getShipperRate() == null ? null : getAccessorials(r.getShipperRate(), ltlAccessorials)))
                    .filter(Objects::nonNull).forEach(this::removeAccessorialsWithoutSpecificMovementType);
        }

        pricingDetails.values().stream().flatMap(List::stream).forEach(vo -> {
            if (vo.getPricingType() == PricingType.BUY_SELL && !isApiProfile(vo.getRate().getPricingDetails())) {
                // remove accessorial if it exists only in one buy or sell rate
                Set<String> carrierAccessorials = getAccessorials(vo.getRate(), ltlAccessorials).keySet();
                Set<String> shipperAccessorials = getAccessorials(vo.getShipperRate(), ltlAccessorials).keySet();
                Collection<String> disjunction = CollectionUtils.disjunction(carrierAccessorials, shipperAccessorials);
                carrierAccessorials.removeAll(disjunction);
                shipperAccessorials.removeAll(disjunction);
            }
        });
    }

    private void removeAccessorialsWithoutSpecificMovementType(Map<String, AccessorialPricingVO> accessorialsMap) {
        for (Iterator<Entry<String, AccessorialPricingVO>> it = accessorialsMap.entrySet().iterator(); it.hasNext();) {
            AccessorialPricingVO accessorial = it.next().getValue();
            accessorial.getPrices().removeIf(a -> a.getMovementType() == MoveType.BOTH);
            if (accessorial.getPrices().isEmpty()) {
                it.remove();
            }
        }
    }

    /**
     * We should get OverDimensional costs if dimensions are specified for at least one product.
     *
     * @param ratesCO
     *            criteria object
     * @return <code>true</code> if no explicit OverDimensional accessorial specified, but some products have
     *         dimensions specified.
     */
    public static boolean isImplicitOverDimentionalAccessorial(GetOrderRatesCO ratesCO) {
    	boolean check = (CollectionUtils.isEmpty(ratesCO.getAccessorialTypes())
                || !ratesCO.getAccessorialTypes().contains(LtlAccessorialType.OVER_DIMENSION.getCode()))
                && ratesCO.getMaterials().stream().anyMatch(m -> m.getLength() != null || m.getWidth() != null || m.getHeight() != null);
        return (CollectionUtils.isEmpty(ratesCO.getAccessorialTypes())
                || !ratesCO.getAccessorialTypes().contains(LtlAccessorialType.OVER_DIMENSION.getCode()))
                && ratesCO.getMaterials().stream().anyMatch(m -> m.getLength() != null || m.getWidth() != null || m.getHeight() != null);
    }

    private AccessorialPricingVO getAccessorial(CarrierPricingProfilesVO profilesVO, LtlRatingAccessorialsVO rate, boolean ltlAccessorials) {
        Map<String, AccessorialPricingVO> accessorials = getAccessorials(profilesVO, ltlAccessorials);
        AccessorialPricingVO accessorial = accessorials.get(rate.getAccessorialType());
        if (accessorial != null) {
            return accessorial;
        } else {
            AccessorialPricingVO accessorialPricingVO = new AccessorialPricingVO();
            accessorialPricingVO.setGeoLevel(rate.getGeoLevel());
            accessorials.put(rate.getAccessorialType(), accessorialPricingVO);
            return accessorialPricingVO;
        }
    }

    private Map<String, AccessorialPricingVO> getAccessorials(CarrierPricingProfilesVO profilesVO, boolean ltlAccessorials) {
        return ltlAccessorials ? profilesVO.getLtlAccessorials() : profilesVO.getAddlAccessorials();
    }

    /*
     * This is for BLANKET profiles. For BLANKET profiles we dont set margin instead use the margin set for
     * the customer level. And if there are more than one BLANKET profile, we dont need to get every time the
     * customer level margin as it is same at customer level and hence for the order.
     */
    private LtlRatingMarginVO getMarginDetails(GetOrderRatesCO ratesCO) {
        LtlRatingMarginVO marginVO = new LtlRatingMarginVO();

        Long marginProfileDetailId = pricingProfileDao.findMarginProfileDetailIdByOrgId(ratesCO.getShipperOrgId());

        if (marginProfileDetailId == null) {
            return marginVO;
        }

        Set<Long> validMarginProfDetailIds = Collections.singleton(marginProfileDetailId);

        List<LtlRatingProfileVO> customerPricingProfile = ratingEngineDao.getCustomerPricingProfile(ratesCO);

        Map<Long, List<CarrierRatingVO>> pricingDetails = getValidPricingDetails(customerPricingProfile);
        CarrierRatingVO marginRatingVO = getMarginRating(ratesCO, pricingDetails);

        //TODO: GET ONLY VALID MOVEMENT TYPE PROFILES. IF SAME PROFILE HAS MORE THAN ONE PRICING RECORD, THEN GET ONLY
        // - COULD BE MULTIPLE BUT SPECIFIC MOVEMENT TYPE VALUES.

        //Set up FuelSurcharge
        /*
         * FS:Fuel Surcharge is super tricky. This logic has to be done in multiple steps.
         * 1. Get all Matching effective days for all weekdays for the given ship date.
         * 2. Get the Fuel trigger values from DB based on the bove values and origin.
         */
        if (ratesCO.getShipperOrgId() != null) {
            Map<FuelWeekDays, Date> publishedDates = FuelSurchargeHelper.getPublishedDates(ratesCO.getShipDate());
            List<LtlRatingFSTriggerVO> fsTriggers = ratingEngineDao.getFSTriggers(ratesCO, validMarginProfDetailIds, publishedDates);
            updatePricingDetailsWithValidFuelSurcharge(fsTriggers, pricingDetails);
        }

        if (marginRatingVO != null) {
            marginVO.setPricingDetail(marginRatingVO.getRate().getPricingDetails());
            if (ratesCO.getShipperOrgId() != null && marginRatingVO.getRate().getFuelSurcharge() != null) {
                marginVO.setFuelSurcharge(marginRatingVO.getRate().getFuelSurcharge());
            }
        }
        marginVO.setGuaranteed(getMarginGuaranteedRates(ratesCO, validMarginProfDetailIds));
        marginVO.getAccessorials().putAll(getMarginAccessorialRates(ratesCO, pricingDetails, validMarginProfDetailIds, marginRatingVO));

        return marginVO;
    }

    private Map<String, AccessorialPricingVO> getMarginAccessorialRates(GetOrderRatesCO ratesCO, Map<Long, List<CarrierRatingVO>> pricingDetails,
            Set<Long> validMarginProfDetailIds, CarrierRatingVO marginRatingVO) {
        Map<String, AccessorialPricingVO> validAccessorialRates = new HashMap<>();
        // Check if there are any accessorials requested and if yes, get them.
        if (isCalculateLtlAccessorials(ratesCO)) {
            List<LtlRatingAccessorialsVO> ltlAccessorials = ratingEngineDao.getAccessorialRates(ratesCO, true, validMarginProfDetailIds);
            if (!ltlAccessorials.isEmpty()) { //Then reset valid profile details ids list and set it back with new list.
                updatePricingDetailsWithValidAccessorialRates(ltlAccessorials, ratesCO, pricingDetails, true, null);

                validAccessorialRates.putAll(marginRatingVO.getRate().getLtlAccessorials());
            }
        }

        List<LtlRatingAccessorialsVO> addlAccessorials = ratingEngineDao.getAccessorialRates(ratesCO, false, validMarginProfDetailIds);
        if (!addlAccessorials.isEmpty()) {
            updatePricingDetailsWithValidAccessorialRates(addlAccessorials, ratesCO, pricingDetails, false, null);
            validAccessorialRates.putAll(marginRatingVO.getRate().getAddlAccessorials());
        }
        return validAccessorialRates;
    }

    private LtlRatingGuaranteedVO getMarginGuaranteedRates(GetOrderRatesCO ratesCO, Set<Long> validMarginProfDetailIds) {
        LtlRatingGuaranteedVO guaranteedRate = null;
        if (ratesCO.getGuaranteedTime() != null) {
            List<LtlRatingGuaranteedVO> marginGuaranteed = ratingEngineDao.getGuaranteedRates(ratesCO, validMarginProfDetailIds);
            if (!marginGuaranteed.isEmpty()) { // Then reset valid profile details ids list and set it back with new list.
                guaranteedRate = marginGuaranteed.get(0);
            }
        }
        return guaranteedRate;
    }

    private CarrierRatingVO getMarginRating(GetOrderRatesCO ratesCO, Map<Long, List<CarrierRatingVO>> pricingDetails) {
        CarrierRatingVO marginRatingVO = null;
        List<CarrierRatingVO> ratings = pricingDetails.get(ratesCO.getShipperOrgId());
        if (ratings != null && !ratings.isEmpty()) {
            Optional<CarrierRatingVO> margin = ratings.stream().filter(r -> r.getPricingType() == PricingType.MARGIN).findAny();
            if (margin.isPresent()) {
                marginRatingVO = margin.get();
            }
        }
        return marginRatingVO;
    }

    private void refineCriteria(GetOrderRatesCO ratesCO) {
        if(!ratesCO.isRequestLTLRates() && !ratesCO.isRequestVLTLRates()) {
            ratesCO.setRequestLTLRates(true);//by default get LTL rates
        }
        
        /*Get the total weight, total quantity, total pieces to pass the same to SMC3 or API for getting base/discounted rates.
          Also need to find the appropriate CarrierRateProfile/CustomerRateProfile when the profile needs to picked by
          MinWeight/MaxWeight range.
        */
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (RateMaterialCO material : ratesCO.getMaterials()) {
            if (material.getWeight() != null) {
                BigDecimal weight = material.getWeight().setScale(0, BigDecimal.ROUND_DOWN);
                material.setWeight(weight);
                totalWeight = totalWeight.add(material.getWeight());
            }
        }
        ratesCO.setTotalWeight(totalWeight);
        ratesCO.setTotalQuantity(ratesCO.getMaterials().stream().filter(m -> m.getQuantity() != null).mapToInt(RateMaterialCO::getQuantity).sum());
        ratesCO.setTotalPieces(ratesCO.getMaterials().stream().filter(m -> m.getPieces() != null).mapToInt(RateMaterialCO::getPieces).sum());
        ratesCO.setPalletType(ratesCO.getMaterials().stream().map(RateMaterialCO::getPackageType)
                .allMatch(packageType -> PALLET_PACKAGE_TYPE.equalsIgnoreCase(packageType) || SKIDS_PACKAGE_TYPE.equalsIgnoreCase(packageType)));
        ratesCO.setCommodityClassSet(ratesCO.getMaterials().stream().map(RateMaterialCO::getCommodityClassEnum).collect(Collectors.toSet()));
        ratesCO.setMaxLength(ratesCO.getMaterials().stream().map(RateMaterialCO::getLength).filter(Objects::nonNull).max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO));
        ratesCO.setMaxWidth(ratesCO.getMaterials().stream().map(RateMaterialCO::getWidth).filter(Objects::nonNull).max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO));

        //If any of the material is Hazmat, then whole order is considered Hazmat and Hazmat accessorial charge is added to final price.
        if (ratesCO.getMaterials().stream().map(RateMaterialCO::getHazmatBool).anyMatch(BooleanUtils::isTrue)) {
            if (ratesCO.getAccessorialTypes() == null) {
                ratesCO.setAccessorialTypes(new ArrayList<String>());
            }
            ratesCO.getAccessorialTypes().add(HAZMAT);
        }

        //Check if the Shipment is inter-state or intra-state shipment.
        if (ratesCO.getOriginAddress().getStateCode().equals(ratesCO.getDestinationAddress().getStateCode())
                && ratesCO.getOriginAddress().getCountryCode().equals(ratesCO.getDestinationAddress().getCountryCode())) {
            ratesCO.setMovementType(MoveType.INTRA);
        } else {
            ratesCO.setMovementType(MoveType.INTER);
        }

        fixZipCode(ratesCO.getOriginAddress());
        fixZipCode(ratesCO.getDestinationAddress());
    }

    private void fixZipCode(AddressVO address) {
        //For Canadian codes. We should not have space in zipcode to get rates from SMC3.
        address.setPostalCode(address.getPostalCode().replaceAll(" ", ""));

        //For Canadian postal codes we need to convert the alphanumeric postal code to string containing its ASCII equivalent.
        if ("CAN".equalsIgnoreCase(address.getCountryCode())) {
            address.setFormattedPostalCode(GeoHelper.getGeoServType(address.getPostalCode()).getRight());
        } else {
            address.setFormattedPostalCode(address.getPostalCode());
        }
    }

    private Date getTransitDate(Date shipDate, Integer transitTime) {
        if (transitTime != null && transitTime > 0) {
            Date transitDate = shipDate;
            for (int i = 1; i <= transitTime; i++) {
                transitDate = DateUtils.addDays(transitDate, 1);

                while (DateUtility.isWeekendOrHoliday(transitDate)) {
                    transitDate = DateUtils.addDays(transitDate, 1);
                }
            }
            return transitDate;
        }
        return null;
    }

    private Integer getTransitTime(LtlRatingProfileVO pricingProfile, Map<String, ScacResponse> carriersTransitMap) {
        Integer transitTime = pricingProfile.getTransitTime();
        if (RATING_TYPE_SMC3.equalsIgnoreCase(pricingProfile.getRatingCarrierType())) {
            String scac = ObjectUtils.defaultIfNull(pricingProfile.getActualCarrierScac(), pricingProfile.getScac());
            if (carriersTransitMap.containsKey(scac)) {
                transitTime = carriersTransitMap.get(scac).getDays();
            }
        }
        // If package type is pallet/skids then the transit time is pulled from the transit
        // days set in pallet pricing(done in dao logic). If it is not set there then it would be
        // pulled from the terminals tab. For other package types transit time would be pulled from
        // the terminal tab. If it is not set then would be left as default zero value and would be
        // displayed as N/A. For manual rating type even if the transit time is not set for a carrier
        // profile, it would not be ignored.
        if (transitTime == null || transitTime <= 0) {
            LtlPricingTerminalInfoEntity terminalEntity = terminalInfoDao.findActiveByProfileDetailId(pricingProfile.getProfileDetailId());
            if (terminalEntity != null && terminalEntity.getTransiteTime() != null) {
                transitTime = terminalEntity.getTransiteTime().intValue();
            }
        }
        return transitTime;
    }

    private LtlServiceType getServiceType(LtlRatingProfileVO pricingProfile, Map<String, ScacResponse> carriersTransitMap) {
        if (pricingProfile.getCarrierOrgId().equals(-1L)) {
            return LtlServiceType.DIRECT;
        }
        if (LtlServiceType.BOTH == pricingProfile.getServiceType()) {
            return LtlServiceType.BOTH;
        }
        LtlServiceType serviceType = pricingProfile.getServiceType();
        if (RATING_TYPE_SMC3.equalsIgnoreCase(pricingProfile.getRatingCarrierType())) {
            String scac = ObjectUtils.defaultIfNull(pricingProfile.getActualCarrierScac(), pricingProfile.getScac());
            serviceType = getTransitServiceType(carriersTransitMap, scac);
        }
        return serviceType;
    }

    private Map<String, ScacResponse> getCarriersTransitInformation(GetOrderRatesCO ratesCO, List<LtlRatingProfileVO> carrierProfiles) {
        if (carrierProfiles.stream()
                .anyMatch(p -> (RATING_TYPE_SMC3.equalsIgnoreCase(p.getRatingCarrierType()) || RATING_TYPE_LTLLC.equalsIgnoreCase(p.getRatingCarrierType())) && !p.getCarrierOrgId().equals(-1L))) {
            // it looks like CarrierOrgId == -1 is true only for Benchmark Profiles
            TransitRequestDTO transitRequest = new TransitRequestDTO();
            transitRequest.setOrigin(new AddressVO(ratesCO.getOriginAddress()));
            transitRequest.setDestination(new AddressVO(ratesCO.getDestinationAddress()));
            transitRequest.setScacRequests(getCarrierTransitRequests(carrierProfiles));
            transitRequest.setPickupDate(ratesCO.getShipDate());

            LOG.info("started getting transit info");
            TransitResponseDTO transitResponse = transitClient.getTransitInformation(transitRequest);
            LOG.info("finished getting transit info");
            if (transitResponse != null && CollectionUtils.isNotEmpty(transitResponse.getScacResponses())) {
                return transitResponse.getScacResponses().stream().collect(Collectors.toMap(ScacResponse::getScac, Function.identity()));
            }
        }
        return new HashMap<String, ScacResponse>();
    }

    private void removeUnapplicableProfiles(List<LtlRatingProfileVO> carrierProfiles, Map<String, ScacResponse> carriersTransitMap) {
        Iterator<LtlRatingProfileVO> iterator = carrierProfiles.iterator();
        while (iterator.hasNext()) {
            LtlRatingProfileVO pricingProfile = iterator.next();
            // it looks like CarrierOrgId == -1 is true only for Benchmark Profiles
            if (!pricingProfile.getCarrierOrgId().equals(-1L) && 
                    (  RATING_TYPE_SMC3.equalsIgnoreCase(pricingProfile.getRatingCarrierType()) || RATING_TYPE_LTLLC.equalsIgnoreCase(pricingProfile.getRatingCarrierType())  )) {
                String scac = ObjectUtils.defaultIfNull(pricingProfile.getActualCarrierScac(), pricingProfile.getScac());
                if (LtlServiceType.BOTH != pricingProfile.getServiceType()
                        && (!carriersTransitMap.containsKey(scac)
                                || getTransitServiceType(carriersTransitMap, scac) != pricingProfile.getServiceType())) {
                    iterator.remove();
                }
            }
        }
    }

    private LtlServiceType getTransitServiceType(Map<String, ScacResponse> carriersTransitMap, String scac) {
        ScacResponse scacVO = carriersTransitMap.get(scac);
        if (scacVO == null) return null;
        if (CARR_CONN_SERV_TYPE_DIRECT.equalsIgnoreCase(scacVO.getOriginServiceType())
                && CARR_CONN_SERV_TYPE_DIRECT.equalsIgnoreCase(scacVO.getDestinationServiceType())) {
            return LtlServiceType.DIRECT;
        }
        return LtlServiceType.INDIRECT;
    }

    private Set<ScacRequest> getCarrierTransitRequests(List<LtlRatingProfileVO> carrierProfiles) {
        Set<ScacRequest> scacRequestList = new HashSet<ScacRequest>(); // Transit time - partial criteria
        Set<String> scacCodes = new HashSet<String>(); // Transit time - partial criteria
        for (LtlRatingProfileVO carrRate : carrierProfiles) {
            // If carrier is setup as Manual rating type then no needs to make a call to carrier connect to
            // get the transit information.
            if (RATING_TYPE_SMC3.equalsIgnoreCase(carrRate.getRatingCarrierType()) || RATING_TYPE_LTLLC.equalsIgnoreCase(carrRate.getRatingCarrierType())) {
                String scac = ObjectUtils.defaultIfNull(carrRate.getActualCarrierScac(), carrRate.getScac());
                if (scacCodes.add(scac)) {
                    // You need to get the transit information now and set the same. The reason you have to get
                    // now is,
                    // we need the Service Type values to find and use appropriate carriers
                    // Build SCAC request list to get Transit Time in the later step
                    ScacRequest scacRequest = new ScacRequest();
                    scacRequest.setScac(scac);
                    scacRequest.setMethod(LTL_TYPE);
                    scacRequestList.add(scacRequest);
                }
            }
        }
        return scacRequestList;
    }
}
