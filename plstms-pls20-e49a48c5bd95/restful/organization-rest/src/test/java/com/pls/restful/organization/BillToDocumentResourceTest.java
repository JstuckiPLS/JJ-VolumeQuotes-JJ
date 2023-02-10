package com.pls.restful.organization;

import com.pls.documentmanagement.service.RequiredDocumentService;
import com.pls.restful.address.BillToDocumentResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Resource for {@link} CustomerDocumentResource class.
 * 
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class BillToDocumentResourceTest {
    @InjectMocks private BillToDocumentResource sut;

    private Long customerId;

    @Mock private RequiredDocumentService documentService;

    @Before
    public void setUp() {
        customerId = new Long(1);
    }

    @Test
    public void testGetRequiredDocuments() {
        sut.getRequiredDocuments(customerId);

        verify(documentService, times(1)).getRequiredDocumentsOfShipmentTypes(customerId);
    }
}
