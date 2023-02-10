package com.pls.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.integration.AuditBO;

/**
 * Test class testing IntegrationAuditServiceImpl functionalities.
 *
 * @author Yasaman Honarvar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationAuditServiceImplTest {

    @Mock
    private IntegrationAuditDao intDao;

    @Mock
    private PlainModificationObject modification;

    @InjectMocks
    IntegrationAuditServiceImpl integrationAuditService;

    private static final Long AUDIT_ID = 1L;
    private static final Long AUDIT_DETAILS_ID = 10L;
    @SuppressWarnings("deprecation")
    private static final Date DATE = new Date(2014, 10, 5);

    @Before
    public void initialize() {
        AuditDetailEntity detail = createAuditDetail(createAudit());
        when(intDao.getLogDetailsByAuditId(AUDIT_ID)).thenReturn(detail);
        when(modification.getCreatedDate()).thenReturn(DATE);
        when(intDao.getLogsByCriteria(DATE, DATE, null, null, null, null, null, null)).thenReturn(createAuditBO());
    }

    private List<AuditBO> createAuditBO() {
        AuditBO auditBO = new AuditBO();
        List<AuditBO> result = new ArrayList<AuditBO>(1);
        result.add(auditBO);
        return result;
    }

    private AuditEntity createAudit() {
        AuditEntity audit = new AuditEntity();
        audit.setId(AUDIT_ID);
        return audit;
    }

    private AuditDetailEntity createAuditDetail(AuditEntity audit) {
        AuditDetailEntity detail = new AuditDetailEntity();
        detail.setId(AUDIT_DETAILS_ID);
        audit.setAuditDetail(detail);
        detail.setAudit(audit);

        return detail;
    }

    @Test
    public void testGetsLogs() {
        integrationAuditService.getLogs(DATE, DATE, null, null, null, null, null, null);
        verify(intDao, times(1)).getLogsByCriteria(DATE, DATE, null, null, null, null, null, null);
    }

    @Test
    public void testGetsLogDetails() {
        AuditDetailEntity detail = integrationAuditService.getLogDetails(AUDIT_ID);
        assertEquals(AUDIT_DETAILS_ID, detail.getId());
        verify(intDao, times(1)).getLogDetailsByAuditId(AUDIT_ID);
    }

    @Test
    public void testSaveLogs() {
        AuditEntity audit = createAudit();
        integrationAuditService.saveLog(audit);
        verify(intDao, times(1)).saveOrUpdate(audit);
    }

    @Test
    public void testGetsLogsByPartialDateFrom() {
        integrationAuditService.getLogs(DATE, null, null, null, null, null, null, null);
        verify(intDao, times(1)).getLogsByCriteria(DATE, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetsLogsByPartialDateTo() {
        integrationAuditService.getLogs(null, DATE, null, null, null, null, null, null);
        verify(intDao, times(1)).getLogsByCriteria(null, DATE, null, null, null, null, null, null);
    }

}
