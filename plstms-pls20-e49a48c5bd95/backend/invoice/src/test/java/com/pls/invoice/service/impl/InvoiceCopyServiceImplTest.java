package com.pls.invoice.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.StandardServletEnvironment;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.service.ShipmentService;

/**
 * The Class InvoiceCopyServiceImplTest.
 * 
 * @author Sergii Belodon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring/testContext.xml" })
public class InvoiceCopyServiceImplTest extends BaseServiceITClass {

    @Mock
    private SharedDriveServiceImpl sharedDriveService;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private StandardServletEnvironment environment;
/*
    @InjectMocks
    @Autowired
    private InvoiceCopyServiceImpl invoiceCopyService;
*/
    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    @Ignore
    public void testPdfCopying() throws ApplicationException, IOException {
        File tempFile = File.createTempFile("temp", "tmp");
        try {
            LoadEntity load = new LoadEntity();
            HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
            LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
            loadCostDetailsEntity.setStatus(Status.ACTIVE);
            costDetails.add(loadCostDetailsEntity);
            load.setActiveCostDetails(costDetails);
            load.getActiveCostDetail().setInvoiceNumber("invoiceNumber");
            Mockito.when(shipmentService.findById(1L)).thenReturn(load);
            Mockito.when(docFileNamesResolver.buildDirectPath(Mockito.anyString())).thenReturn(tempFile.getParent());
            Mockito.when(sharedDriveService.storeInvoiceCopy(Mockito.anyString(), Mockito.any())).thenCallRealMethod();

            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
            LoadDocumentEntity loadDocumentEntity = new LoadDocumentEntity();
            loadDocumentEntity.setLoadId(1L);
            loadDocumentEntity.setDocFileName(tempFile.getName());
            invoiceDocuments.put(InvoiceDocument.PDF, loadDocumentEntity);
            List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);
            invoices.add(new LoadAdjustmentBO(load));
            BillToEntity billTo = new BillToEntity();
            billTo.setInvoiceSettings(new InvoiceSettingsEntity());
            billTo.getInvoiceSettings().setInvoiceType(InvoiceType.CBI);
            //invoiceCopyService.storeInvoicesCopy(invoices, invoiceDocuments, 1L, billTo);
        } finally {
            tempFile.deleteOnExit();
        }
    }
}