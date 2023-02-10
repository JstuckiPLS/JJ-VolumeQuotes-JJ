package com.pls.shipment.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.shipment.dao.DocumentTypeDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.impl.ShipmentDocumentServiceImpl;
import com.pls.shipment.service.pdf.BolPdfDocumentParameter;
import com.pls.shipment.service.pdf.BolPdfDocumentWriter;
import com.pls.shipment.service.pdf.Printable;
import com.pls.shipment.service.pdf.ShippingLabelsDocumentParameter;
import com.pls.shipment.service.pdf.ShippingLabelsPdfDocumentWriter;

/**
 * Test cases for {@link com.pls.shipment.service.impl.ShipmentDocumentServiceImpl} class.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentDocumentServiceImplTest {

    private static final Long USER_ID = (long) (Math.random() * 10000);

    private static final Long DOCUMENT_ID = 2L;
    private static final List<ShipmentDocumentInfoBO> DOCUMENTS_INFO = new ArrayList<ShipmentDocumentInfoBO>();

    private static final Long SHIPMENT_ID = 10L;

    @Mock
    private LoadDocumentDao documentDao;

    @Mock
    private DocumentTypeDao documentTypeDao;

    @Mock
    private DocumentService documentService;

    @Mock
    private LtlShipmentDao loadDao;

    @Mock
    private BolPdfDocumentWriter bolPdfDocumentWriter;

    @Mock
    private ShippingLabelsPdfDocumentWriter shippingLabelsPdfDocumentWriter;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @Mock
    private UserInfoDao userInfoDao;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private UserAddressBookDao userAddressBookDao;

    @InjectMocks
    private ShipmentDocumentServiceImpl shipmentDocumentService;

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        when(documentDao.getDocumentsInfoForShipment(DOCUMENT_ID)).thenReturn(DOCUMENTS_INFO);

    }

    @Test
    public void testGetDocumentListWithNormalCase() throws Exception {
        List<ShipmentDocumentInfoBO> result = shipmentDocumentService.getDocumentList(SHIPMENT_ID);

        Assert.assertNotNull(result);

        verify(documentDao).getDocumentsInfoForShipment(SHIPMENT_ID);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testGetDocumentListWithNull() throws Exception {
        shipmentDocumentService.getDocumentList(null);
    }

    @Test
    public void testGenerateAndStoreTempBol() throws Exception {
        final LoadEntity load = new LoadEntity();

        byte[] bolPdfContent = new byte[10];

        new Random().nextBytes(bolPdfContent);

        doNothing().when(bolPdfDocumentWriter).write(any(BolPdfDocumentParameter.class), any(OutputStream.class));
        File tempFile = File.createTempFile("generatedDocTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(DocumentTypes.BOL.getDbValue(), MimeTypes.PDF)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        Mockito.when(customerDao.find(anyLong())).thenReturn(null);

        shipmentDocumentService.generateAndStoreTempBol(load, 1L, false, false);
        Mockito.verify(documentService).prepareTempDocument(Matchers.eq(DocumentTypes.BOL.getDbValue()), Matchers.eq(MimeTypes.PDF));
        Mockito.verify(docFileNamesResolver).buildDirectPath(Matchers.eq(path));
        Mockito.verify(documentService).savePreparedDocument(Matchers.eq(document));

        verify(bolPdfDocumentWriter).write(Matchers.argThat(new ArgumentMatcher<BolPdfDocumentParameter>() {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof BolPdfDocumentParameter) {
                    BolPdfDocumentParameter parameter = (BolPdfDocumentParameter) argument;
                    return load.equals(parameter.getLoad()) && parameter.isPreview();
                }

                return false;
            }
        }), Matchers.any(OutputStream.class));
    }

    @Test
    public void testGenerateAndStoreTempDocuments() throws Exception {
        final LoadEntity load = new LoadEntity();
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        AddressEntity originAddress = new AddressEntity();
        AddressEntity destinationAddress = new AddressEntity();
        originAddress.setId(1L);
        destinationAddress.setId(2L);
        origin.setAddress(originAddress);
        destination.setAddress(destinationAddress);
        load.addLoadDetails(origin);
        load.addLoadDetails(destination);

        byte[] labelPdfContent = new byte[10];

        new Random().nextBytes(labelPdfContent);

        doNothing().when(shippingLabelsPdfDocumentWriter).write(any(ShippingLabelsDocumentParameter.class), any(OutputStream.class));

        File tempFile = File.createTempFile("generatedDocTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(DocumentTypes.SHIPPING_LABELS.getDbValue(), MimeTypes.PDF)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);

        shipmentDocumentService.generateAndStoreTempShippingLabels(load, 1L, 1L, Printable.DEFAULT_TEMPLATE);
        Mockito.verify(documentService).prepareTempDocument(Matchers.eq(DocumentTypes.SHIPPING_LABELS.getDbValue()), Matchers.eq(MimeTypes.PDF));
        Mockito.verify(docFileNamesResolver).buildDirectPath(Matchers.eq(path));
        Mockito.verify(documentService).savePreparedDocument(Matchers.eq(document));

        verify(shippingLabelsPdfDocumentWriter).write(Matchers.argThat(new ArgumentMatcher<ShippingLabelsDocumentParameter>() {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof ShippingLabelsDocumentParameter) {
                    ShippingLabelsDocumentParameter parameter = (ShippingLabelsDocumentParameter) argument;
                    return load.equals(parameter.getLoad());
                }

                return false;
            }
        }), Matchers.any(OutputStream.class));
    }

    @Test
    public void testFindDocumentTypes() {
        List<LoadDocumentTypeEntity> list = new ArrayList<LoadDocumentTypeEntity>();
        LoadDocumentTypeEntity documentTypeEntity = new LoadDocumentTypeEntity();
        documentTypeEntity.setId(1L);
        list.add(documentTypeEntity);
        when(documentTypeDao.getLoadDocumentType()).thenReturn(list);
        List<LoadDocumentTypeEntity> actualList = shipmentDocumentService.findDocumentTypes();
        verify(documentTypeDao).getLoadDocumentType();
        Assert.assertEquals(list, actualList);
    }

    @Test
    public void testGetDocumentTypeByStringName() throws Exception {
        String documentTypeStr = "INVOICE";

        LoadDocumentTypeEntity expected = new LoadDocumentTypeEntity();
        expected.setDocTypeString(documentTypeStr);
        Mockito.when(documentTypeDao.findByDocTypeString(documentTypeStr, LoadDocumentTypeEntity.class)).thenReturn(expected);

        LoadDocumentTypeEntity actual = shipmentDocumentService.getDocumentTypeByStringName(documentTypeStr);

        Mockito.verify(documentTypeDao).findByDocTypeString(Matchers.eq(documentTypeStr), Matchers.eq(LoadDocumentTypeEntity.class));

        Assert.assertEquals(expected, actual);
    }
}
