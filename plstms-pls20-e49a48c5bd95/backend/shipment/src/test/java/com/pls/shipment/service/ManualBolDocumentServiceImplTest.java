package com.pls.shipment.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.impl.DocumentServiceImpl;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.service.impl.ManualBolDocumentServiceImpl;
import com.pls.shipment.service.pdf.BolPdfDocumentParameter;
import com.pls.shipment.service.pdf.BolPdfDocumentWriter;
import com.pls.shipment.service.pdf.ShippingLabelsDocumentParameter;
import com.pls.shipment.service.pdf.ShippingLabelsPdfDocumentWriter;

/**
 * Test cases for {@link ManualBolDocumentServiceImpl} class.
 * 
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ManualBolDocumentServiceImplTest {

    private static final List<ShipmentDocumentInfoBO> DOCUMENTS_INFO = new ArrayList<ShipmentDocumentInfoBO>();

    private static final Long DOCUMENT_ID = (long) Math.random() * 100;

    private static final Long MANUAL_BOL_ID = (long) Math.random() * 100;

    private static final Long USER_ID = (long) Math.random() * 100;

    private static final Long CUSTOMER_ID = (long) Math.random() * 100;

    @Mock
    private LoadDocumentDao documentDao;

    @Mock
    private BolPdfDocumentWriter bolPdfDocumentWriter;

    @Mock
    private ShippingLabelsPdfDocumentWriter shippingLabelsPdfDocumentWriter;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @Mock
    private DocumentServiceImpl documentService;

    @Mock
    private UserInfoDao userInfoDao;

    @InjectMocks
    private ManualBolDocumentServiceImpl sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);
    }

    @Test
    public void shouldReturnDocumentsList() throws Exception {
        when(documentDao.getDocumentsInfoForManualBol(DOCUMENT_ID)).thenReturn(DOCUMENTS_INFO);

        List<ShipmentDocumentInfoBO> actualList = sut.getDocumentsList(DOCUMENT_ID);
        Assert.assertNotNull(actualList);

        verify(documentDao).getDocumentsInfoForManualBol(DOCUMENT_ID);
    }

    @Test
    public void shouldCreateShippingLabelDocument() throws Exception {
        ManualBolEntity manualBolEntity = createRandomManualBolEntity();

        doNothing().when(shippingLabelsPdfDocumentWriter).write(any(ShippingLabelsDocumentParameter.class), any(OutputStream.class));

        File tempFile = File.createTempFile("generatedDocTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        document.setId(DOCUMENT_ID);

        when(documentDao.findDocumentsForManualBol(Mockito.eq(DOCUMENT_ID), Mockito.eq(DocumentTypes.SHIPPING_LABELS.getDbValue())))
                        .thenReturn(ImmutableList.of(document));
        when(docFileNamesResolver.buildManualBolPath(document)).thenReturn(path);
        when(docFileNamesResolver.buildManualBolDocumentFileName(document)).thenReturn(tempFile.getName());
        when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        when(documentService.savePreparedDocument(document)).thenReturn(document);

        sut.createShippingLabelDocument(manualBolEntity);

        verify(documentDao).findDocumentsForManualBol(MANUAL_BOL_ID, DocumentTypes.SHIPPING_LABELS.getDbValue());
        verify(docFileNamesResolver).buildManualBolPath(document);
        verify(docFileNamesResolver).buildManualBolDocumentFileName(document);
        verify(shippingLabelsPdfDocumentWriter).write(any(ShippingLabelsDocumentParameter.class), any(OutputStream.class));
        verify(documentService).savePreparedDocument(document);
    }

    @Test
    public void shouldCreateBolDocument() throws Exception {
        ManualBolEntity manualBolEntity = createRandomManualBolEntity();

        doNothing().when(bolPdfDocumentWriter).write(any(BolPdfDocumentParameter.class), any(OutputStream.class));

        File tempFile = File.createTempFile("generatedDocTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        document.setId(DOCUMENT_ID);

        UserEntity user = createRandomUserEntity();

        when(userInfoDao.getUserEntityById(SecurityUtils.getCurrentPersonId())).thenReturn(user);
        when(documentDao.findDocumentsForManualBol(Mockito.eq(DOCUMENT_ID), Mockito.eq(DocumentTypes.BOL.getDbValue())))
                        .thenReturn(ImmutableList.of(document));
        when(docFileNamesResolver.buildManualBolPath(document)).thenReturn(path);
        when(docFileNamesResolver.buildManualBolDocumentFileName(document)).thenReturn(tempFile.getName());
        when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        when(documentService.savePreparedDocument(document)).thenReturn(document);

        sut.createBolDocument(manualBolEntity, true);

        verify(documentDao).findDocumentsForManualBol(MANUAL_BOL_ID, DocumentTypes.BOL.getDbValue());
        verify(docFileNamesResolver).buildManualBolPath(document);
        verify(docFileNamesResolver).buildManualBolDocumentFileName(document);
        verify(bolPdfDocumentWriter).write(any(BolPdfDocumentParameter.class), any(OutputStream.class));
        verify(documentService).savePreparedDocument(document);
    }

    private ManualBolEntity createRandomManualBolEntity() {
        ManualBolEntity manualBolEntity = new ManualBolEntity();
        manualBolEntity.setId(MANUAL_BOL_ID);
        ManualBolAddressEntity origin = new ManualBolAddressEntity(PointType.ORIGIN);
        ManualBolAddressEntity destination = new ManualBolAddressEntity(PointType.DESTINATION);
        AddressEntity originAddress = new AddressEntity();
        AddressEntity destinationAddress = new AddressEntity();
        originAddress.setId((long) Math.random() * 100);
        destinationAddress.setId((long) Math.random() * 100);
        origin.setAddress(originAddress);
        destination.setAddress(destinationAddress);
        manualBolEntity.addAddress(origin);
        manualBolEntity.addAddress(destination);
        manualBolEntity.setMaterials(new HashSet<ManualBolMaterialEntity>());

        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        manualBolEntity.setOrganization(customer);

        return manualBolEntity;
    }

    private UserEntity createRandomUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(USER_ID);

        return entity;
    }
}
