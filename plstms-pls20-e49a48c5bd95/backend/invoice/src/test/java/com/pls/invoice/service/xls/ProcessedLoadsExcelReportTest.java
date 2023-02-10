package com.pls.invoice.service.xls;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.util.TestUtils;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;
import com.pls.invoice.service.impl.FinancialBoardServiceImpl;

/**
 * Test cases for {@link FinancialBoardServiceImpl} class.
 * 
 * @author Alexander Nalapko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessedLoadsExcelReportTest {

        @InjectMocks
        private FinancialBoardServiceImpl serviceImpl;

        @Before
        public void init() {
            TestUtils.instantiateClassPathResource("transactionalProcessedReport", serviceImpl);
            TestUtils.instantiateClassPathResource("cbiProcessedReport", serviceImpl);
        }

        @Test
        public void testGetCBIProcessedReport() throws IOException {
            FileInputStreamResponseEntity reportsBO = serviceImpl.getCBIProcessedReport(new ProcessedLoadsReportBO());
            Assert.assertNotNull(reportsBO);
        }

        @Test
        public void testGetTransactionalProcessedReport() throws IOException {
            FileInputStreamResponseEntity reportsBO = serviceImpl.getTransactionalProcessedReport(new ProcessedLoadsReportBO());
            Assert.assertNotNull(reportsBO);
        }
    }
