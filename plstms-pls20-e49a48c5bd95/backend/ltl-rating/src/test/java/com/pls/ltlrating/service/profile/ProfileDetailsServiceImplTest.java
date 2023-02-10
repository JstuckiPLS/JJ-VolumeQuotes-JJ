package com.pls.ltlrating.service.profile;


/**
 * Test suite for {@link ProfileDetailsServiceImpl}.
 *
 * @author Andrey Kachur
 *
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(SecurityUtils.class)
public class ProfileDetailsServiceImplTest {
//
//    private static final Long DEFAULT_PERSON_ID = (long) (Math.random() * 50 + 1);
//    private static final Long PROFILE_ID = (long) (Math.random() * 100 + 1);
//
//    @Mock
//    private LtlPricingProfileDao priceProfileDao;
//    @Mock
//    private LtlPricingBlockedCustomersDao ltlPricingBlockedCustomersDao;
//    @Mock
//    private Validator<LtlPricingProfileEntity> pricingValidator;
//    @InjectMocks
//    private ProfileDetailsServiceImpl service;
//
//    private LtlPricingProfileEntity profile;
//
//    @Before
//    public void setUp() {
//        PowerMockito.mockStatic(SecurityUtils.class);
//        PowerMockito.when(SecurityUtils.getCurrentUserId()).thenReturn(DEFAULT_PERSON_ID);
//
//        profile = new LtlPricingProfileEntity();
//        profile.setModification(new ModificationObject());
//        profile.setId(PROFILE_ID);
//    }
//
//    @Test
//    public void shouldSaveProfile() throws ValidationException {
//        Mockito.when(priceProfileDao.getProfileById(PROFILE_ID)).thenReturn(profile);
//
//        LtlPricingProfileEntity saved = service.saveProfile(profile);
//
//        Mockito.verify(priceProfileDao).getProfileById(PROFILE_ID);
//        Assert.assertEquals(DEFAULT_PERSON_ID, saved.getModification().getModifiedBy());
//    }
//
//    @Test
//    public void shouldGetProfileById() {
//        service.getProfileById(PROFILE_ID);
//
//        Mockito.verify(priceProfileDao).getProfileById(PROFILE_ID);
//    }
//
//    @Test
//    public void shouldGetExplicitlyBlockedCustomersByProfileId() {
//        service.getExplicitlyBlockedCustomersByProfileId(PROFILE_ID);
//
//        Mockito.verify(ltlPricingBlockedCustomersDao).getExplicitlyBlockedCustomersByProfileId(PROFILE_ID);
//    }
//
//    @Test
//    public void shouldInactivateProfiles() throws EntityNotFoundException {
//        List<Long> ids = Arrays.asList(new Long[] { 1L, 2L });
//
//        service.inactivateProfiles(ids);
//
//        Mockito.verify(priceProfileDao).updateStatus(LtlPricingProfileStatus.ARCHIVED, ids);
//        Mockito.verify(priceProfileDao).getAll(ids, true);
//    }
//
//    @Test
//    public void shouldInactivateProfile() throws EntityNotFoundException {
//
//        service.inactivateProfile(PROFILE_ID);
//
//        Mockito.verify(priceProfileDao).updateStatus(LtlPricingProfileStatus.ARCHIVED, Arrays.asList(new Long[] { PROFILE_ID }));
//        Mockito.verify(priceProfileDao).getProfileById(PROFILE_ID);
//    }
//
//    @Test
//    public void shouldReactivateProfile() throws EntityNotFoundException {
//
//        service.reactivateProfile(PROFILE_ID);
//
//        Mockito.verify(priceProfileDao).updateStatus(LtlPricingProfileStatus.ACTIVE, Arrays.asList(new Long[] { PROFILE_ID }));
//        Mockito.verify(priceProfileDao).getProfileById(PROFILE_ID);
//    }
//
//    @Test
//    public void shouldReactivateProfiles() throws EntityNotFoundException {
//        List<Long> ids = Arrays.asList(new Long[] { 1L, 2L });
//
//        service.reactivateProfiles(ids);
//
//        Mockito.verify(priceProfileDao).updateStatus(LtlPricingProfileStatus.ACTIVE, ids);
//        Mockito.verify(priceProfileDao).getAll(ids, true);
//    }
//
//    @Test
//    public void shouldGetActiveProfiles() {
//        GetRatesBO ratesBO = new GetRatesBO();
//        List<LtlPricingProfileFilterStatusValue> statuses = new ArrayList<LtlPricingProfileFilterStatusValue>();
//        statuses.add(LtlPricingProfileFilterStatusValue.ACTIVE);
//        ratesBO.setStatuses(statuses);
//        FilterQueryBO filter = new FilterQueryBO();
//        SortQueryBO sort = new SortQueryBO();
//
//        service.getPricingProfiles(ratesBO, filter, sort);
//
//        Mockito.verify(priceProfileDao).getProfilesBySelectedCriteria(ratesBO, filter, sort);
//    }
//
//    @Test
//    public void shouldGetArchivedProfiles() {
//        GetRatesBO ratesBO = new GetRatesBO();
//        List<LtlPricingProfileFilterStatusValue> statuses = new ArrayList<LtlPricingProfileFilterStatusValue>();
//        statuses.add(LtlPricingProfileFilterStatusValue.ARCHIVED);
//        ratesBO.setStatuses(statuses);
//        FilterQueryBO filter = new FilterQueryBO();
//        SortQueryBO sort = new SortQueryBO();
//
//        service.getPricingProfiles(ratesBO, filter, sort);
//
//        Mockito.verify(priceProfileDao).getProfilesBySelectedCriteria(ratesBO, filter, sort);
//    }
//
//    @Test
//    public void shouldGetExpiredProfiles() {
//        GetRatesBO ratesBO = new GetRatesBO();
//        List<LtlPricingProfileFilterStatusValue> statuses = new ArrayList<LtlPricingProfileFilterStatusValue>();
//        statuses.add(LtlPricingProfileFilterStatusValue.EXPIRED);
//        ratesBO.setStatuses(statuses);
//        FilterQueryBO filter = new FilterQueryBO();
//        SortQueryBO sort = new SortQueryBO();
//
//        service.getPricingProfiles(ratesBO, filter, sort);
//
//        Mockito.verify(priceProfileDao).getProfilesBySelectedCriteria(ratesBO, filter, sort);
//    }

}
