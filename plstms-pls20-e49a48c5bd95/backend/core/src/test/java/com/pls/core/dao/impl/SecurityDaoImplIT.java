package com.pls.core.dao.impl;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.SecurityDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.user.UserEntity;

/**
 * Test cases for {@link SecurityDaoImpl} class.
 * 
 * @author Maxim Medvedev
 */
public class SecurityDaoImplIT extends AbstractDaoTest {

    private static final String CLEAN_ORG_USERS = " UPDATE FLATBED.ORGANIZATION_USERS SET STATUS = 'I'";
    private static final String CREATE_TEST_ORG = "                                                 "
            + " INSERT INTO FLATBED.ORGANIZATIONS (ORG_ID, NAME, EMPLOYER_NUM, ORG_TYPE, STATUS,    "
            + "     DATE_CREATED, CREATED_BY, DATE_MODIFIED, MODIFIED_BY,                           "
            + "     LOC_RATE_OVERRIDE, STATUS_REASON, NETWORK_ID, LTL_RATE_TYPE)                    "
            + " VALUES( :orgId, 'TestOrg', 'TestShipperCode', 'SHIPPER', :status,                   "
            + "     LOCALTIMESTAMP, 0, LOCALTIMESTAMP, '0',                                                       "
            + "     'N', :reason, :network, 'BO')                                                   ";
    /**
     * To simplify SQL queries we generating ID in Java code without DB sequences. This approach can be used
     * in tests and MUST BE AVOIDED in production code.
     */
    private static long idSequence = -100L;

    private static final String INSERT_GROUP = "                                                "
            + " INSERT INTO FLATBED.GROUPS (GROUP_ID, NAME, STATUS, CREATED_BY, MODIFIED_BY)    "
            + " VALUES (:id, :name, 'A', :personId, :personId)                                  ";

    private static final String INSERT_GROUP_CAP = "                                            "
            + " INSERT INTO FLATBED.GROUP_CAPABILITIES (GROUP_CAPABILITY_ID, GROUP_ID, CAPABILITY_ID, STATUS, CREATED_BY, MODIFIED_BY)"
            + " SELECT :id, :groupId, C.CAPABILITY_ID, 'A', :personId, :personId                "
            + " FROM FLATBED.CAPABILITIES C                                                     "
            + " WHERE C.NAME = :capability                                                      ";
    private static final String INSERT_ORG_USER = "                                             "
            + " INSERT INTO FLATBED.ORGANIZATION_USERS (ORG_USER_ID, PERSON_ID, ORG_ID, STATUS, "
            + "     CREATED_BY, MODIFIED_BY, DATE_CREATED, DATE_MODIFIED)                       "
            + " VALUES (-100, :personId, :orgId, :status,                                       "
            + "     :personId, :personId, LOCALTIMESTAMP, LOCALTIMESTAMP)                                     ";
    private static final String INSERT_NETWORK_USERS = "                                        "
            + " INSERT INTO FLATBED.NETWORK_USERS (NETWORK_USER_ID, PERSON_ID, NETWORK_ID, STATUS,  "
            + "     CREATED_BY, MODIFIED_BY, DATE_CREATED, DATE_MODIFIED)                           "
            + " VALUES (-100, :personId, 7, 'A',                                                    "
            + "     :personId, :personId, LOCALTIMESTAMP, LOCALTIMESTAMP)                                         ";

    private static final String INSERT_USER_CAP = "                                                                 "
            + " INSERT INTO FLATBED.USER_CAPABILITIES_XREF (USER_CAPABILITY_ID, CAPABILITY_ID, PERSON_ID, STATUS,    "
            + "                         CREATED_BY, MODIFIED_BY, DATE_CREATED, DATE_MODIFIED)                       "
            + " SELECT NEXTVAL('FLATBED.USER_CAPABILITIES_XREF_SEQ'), C.CAPABILITY_ID, :personId,  'A',                 "
            + "                         :personId, :personId, LOCALTIMESTAMP, LOCALTIMESTAMP                                      "
            + " FROM FLATBED.CAPABILITIES C                                                                         "
            + " WHERE C.NAME = :capability                                                                          ";

    private static final String INSERT_USER_GROUP = "                                                                   "
            + " INSERT INTO FLATBED.USER_GROUPS (USER_GROUP_ID, GROUP_ID, PERSON_ID, STATUS, CREATED_BY, MODIFIED_BY)   "
            + " VALUES (:id, :groupId, :personId, 'A', :personId, :personId)                                            ";

    private static final String PRIVILEGE1 = "QUOTES_VIEW";

    private static final String PRIVILEGE2 = "DASHBOARD_SUMMARIES_VIEW";

    private static final String UPDATE_PARENT_ORG_ID = "                "
            + "UPDATE FLATBED.USERS                                     "
            + " SET ORG_ID = :orgId                                     "
            + "WHERE PERSON_ID = :personId                              ";

    // Check that ORGANIZATION with NAME='PLS PRO' is present in test data set and has exactly the same ORG_ID
    private static final Long VALID_PLS_ORG_ID = 38941L;

    private static final Long VALID_ORG_ID = 1L;

    // This user record should exist in test DB but it must be not 'root' user.
    private static final Long VALID_USER_ID = 17L;
    // This user record should exist in test DB but it must be not 'root' user.
    private static final Long VALID_USER_ID2 = 16L;

    @Autowired
    private SecurityDao sut;

    @Autowired
    private UserInfoDao userInfoDao;

    @Test
    public void testIsPlsUserWithInvalidId() {
        Assert.assertFalse(sut.isPlsUser(SecurityDaoImplIT.VALID_PLS_ORG_ID + 1L));
    }

    @Test
    public void testIsPlsUserWithNull() {
        Assert.assertFalse(sut.isPlsUser(null));
    }

    @Test
    public void testIsPlsUserWithValidId() {
        Assert.assertTrue(sut.isPlsUser(SecurityDaoImplIT.VALID_PLS_ORG_ID));
    }

    @Test
    public void testLoadOrganizationsWithInactiveLinks() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_PLS_ORG_ID);
        Long orgId = createTestOrg(true);
        createAssociation(SecurityDaoImplIT.VALID_USER_ID, orgId, false);

        Set<Long> result = sut.loadOrganizations(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.VALID_PLS_ORG_ID));
    }

    @Test
    public void testLoadOrganizationsWithInactiveOrganizationsForPLSUser() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_PLS_ORG_ID);
        Long orgId = createTestOrg(false);
        createAssociation(SecurityDaoImplIT.VALID_USER_ID, orgId, true);

        Set<Long> result = sut.loadOrganizations(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.VALID_PLS_ORG_ID));
    }

    @Test
    public void testLoadOrganizationsWithInactiveOrganizations() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_ORG_ID);
        Long orgId = createTestOrg(false);
        createAssociation(SecurityDaoImplIT.VALID_USER_ID, orgId, true);

        Set<Long> result = sut.loadOrganizations(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.VALID_ORG_ID));
    }

    @Test
    public void testLoadOrganizationsWithInvalidUser() {
        cleanCustomerUsers();
        Set<Long> result = sut.loadOrganizations(-100500L);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLoadOrganizationsWithNormalCase() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_PLS_ORG_ID);
        Long orgId = createTestOrg(true);
        createAssociation(SecurityDaoImplIT.VALID_USER_ID, orgId, true);

        Set<Long> result = sut.loadOrganizations(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.VALID_PLS_ORG_ID));
    }

    @Test
    public void testLoadOrganizationsWithNull() {
        cleanCustomerUsers();
        Set<Long> result = sut.loadOrganizations(null);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLoadOrganizationsWithoutOrganizations() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_PLS_ORG_ID);

        Set<Long> result = sut.loadOrganizations(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.VALID_PLS_ORG_ID));
    }

    @Test
    public void testLoadPrivilegesWithGroupPrivilages() {
        Long groupId = createGroup("TestGroup");
        addGroupCapablility(groupId, SecurityDaoImplIT.PRIVILEGE1);
        addUser(groupId, SecurityDaoImplIT.VALID_USER_ID);
        flushAndClearSession();

        Set<String> result = sut.loadCapabilities(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.PRIVILEGE1));
    }

    @Test
    public void testLoadPrivilegesWithInvalidUserId() {
        createUserCapability(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.PRIVILEGE1);

        Set<String> result = sut.loadCapabilities(SecurityDaoImplIT.VALID_USER_ID2);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLoadPrivilegesWithNullId() {
        Set<String> result = sut.loadCapabilities(null);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLoadPrivilegesWithUserAndGroupPrivilages() {
        Long group = createGroup("TestGroup");
        addGroupCapablility(group, SecurityDaoImplIT.PRIVILEGE1);
        addUser(group, SecurityDaoImplIT.VALID_USER_ID);
        createUserCapability(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.PRIVILEGE2);

        Set<String> result = sut.loadCapabilities(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.PRIVILEGE1));
        Assert.assertTrue(result.contains(SecurityDaoImplIT.PRIVILEGE2));

    }

    @Test
    public void testLoadPrivilegesWithUserPrivilages() {
        createUserCapability(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.PRIVILEGE1);

        Set<String> result = sut.loadCapabilities(SecurityDaoImplIT.VALID_USER_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(SecurityDaoImplIT.PRIVILEGE1));
    }

    @Test
    public void shouldCheckAssignedCustomerViaNetwork() {
        cleanCustomerUsers();
        setParentOrg(SecurityDaoImplIT.VALID_USER_ID, SecurityDaoImplIT.VALID_PLS_ORG_ID);
        Long orgId = createTestOrg(true);
        createNetworkUserAssociation(SecurityDaoImplIT.VALID_USER_ID);

        boolean result = sut.isCustomerAssignedThroughNetwork(SecurityDaoImplIT.VALID_USER_ID, orgId);

        Assert.assertTrue(result);
    }

    private void addGroupCapablility(Long groupId, String capability) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_GROUP_CAP);
        query.setParameter("id", SecurityDaoImplIT.idSequence--);
        query.setParameter("groupId", groupId);
        query.setParameter("capability", capability);
        query.setParameter("personId", SecurityDaoImplIT.VALID_USER_ID);
        Assert.assertEquals(1, query.executeUpdate());
    }

    private void addUser(Long groupId, Long personId) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_USER_GROUP);
        query.setParameter("id", SecurityDaoImplIT.idSequence--);
        query.setParameter("groupId", groupId);
        query.setParameter("personId", personId);
        Assert.assertEquals(1, query.executeUpdate());
    }

    private void cleanCustomerUsers() {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.CLEAN_ORG_USERS);
        query.executeUpdate();
    }

    private void createAssociation(Long personId, Long orgId, boolean status) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_ORG_USER);
        query.setParameter("orgId", orgId);
        query.setParameter("personId", personId);
        query.setParameter("status", status ? "A" : "I");
        query.executeUpdate();
    }

    private void createNetworkUserAssociation(Long personId) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_NETWORK_USERS);
        query.setParameter("personId", personId);
        query.executeUpdate();
    }

    private Long createGroup(String groupName) {
        long result = SecurityDaoImplIT.idSequence--;
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_GROUP);
        query.setParameter("id", result);
        query.setParameter("name", groupName);
        query.setParameter("personId", SecurityDaoImplIT.VALID_USER_ID);
        Assert.assertEquals(1, query.executeUpdate());
        return result;
    }

    private Long createTestOrg(boolean status) {
        long result = -100L;
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.CREATE_TEST_ORG);
        query.setParameter("orgId", result);
        query.setParameter("status", status ? "A" : "I");
        query.setParameter("reason", status ? "ACTOPREQ" : "EXP");
        query.setLong("network", 7L);
        Assert.assertEquals(1, query.executeUpdate());
        return result;
    }

    private void createUserCapability(Long personId, String capability) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.INSERT_USER_CAP);
        query.setParameter("capability", capability);
        query.setParameter("personId", personId);
        Assert.assertEquals(1, query.executeUpdate());
    }

    private void setParentOrg(Long personId, Long orgId) {
        Query query = getSession().createSQLQuery(SecurityDaoImplIT.UPDATE_PARENT_ORG_ID);
        query.setParameter("personId", personId);
        query.setParameter("orgId", orgId);
        Assert.assertEquals(1, query.executeUpdate());
    }

    @Test
    public void testSaveLastLoginDate() {
        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.setTime(new Date());

        sut.saveLastLoginDateByPersonId(VALID_USER_ID);

        UserEntity actualEntity = userInfoDao.getUserEntityById(VALID_USER_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertNotNull(actualEntity.getLastLoginDate());

        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actualEntity.getLastLoginDate());
        Assert.assertEquals(expectedCalendar.get(Calendar.DATE), actualCalendar.get(Calendar.DATE));
        Assert.assertEquals(expectedCalendar.get(Calendar.MONTH), actualCalendar.get(Calendar.MONTH));
        Assert.assertEquals(expectedCalendar.get(Calendar.YEAR), actualCalendar.get(Calendar.YEAR));
    }
}
