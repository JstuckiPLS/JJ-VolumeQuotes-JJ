package com.pls.shipment.service.edi.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.organization.CarrierEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;
import com.pls.shipment.service.impl.edi.parser.EDI990Parser;

/**
 * Test cases for {@link EDI990Parser} class.
 *
 * @author Alexander Nalapko
 */
@RunWith(MockitoJUnitRunner.class)
public class EDI990ParserTest extends AbstractEDIParserTest {
    private static final String CARRIER_SCAC = "RDWY";
    private static final String TRANSACTION_SET_ID = "990";
    private static final String EDI_FILE_NAME_A = "CTSI990_Acceptance.txt";
    private static final String EDI_FILE_NAME_D_V9 = "CTSI990-Decline_V9.txt";
    private static final String EDI_FILE_NAME_D_K1 = "CTSI990-Decline_K1.txt";
    private static final int ENTITIES_COUNT = 1;

    private static final String PRO = "120101020";
    private static final String BOL = "333333";
    private static final String SCAC = "RDWY";

    @Mock
    private CarrierEntity carrier;

    @Mock
    private AbstractEDIParser.DataProvider dataProvider;

    //@InjectMocks
    private EDI990Parser sut;

    @Before
    public void setUp() {

        Map<String, String>  loadTrackingStatus = new HashMap<String, String>();
        Map<String, String>  loadReasonTrackingStatus = new HashMap<String, String>();

        loadTrackingStatus.put("A", "Reservation Accepted");
        loadTrackingStatus.put("D", "Reservation Cancelled");

        loadReasonTrackingStatus.put("CC", "Capacity Constraints");
        loadReasonTrackingStatus.put("A07", "Refused by Consignee");

        Mockito.when(dataProvider.getLoadTrackingStatusTypes()).thenReturn(loadTrackingStatus);
        Mockito.when(dataProvider.getLoadReasonTrackingStatusTypes()).thenReturn(loadReasonTrackingStatus);

        sut = new EDI990Parser(carrier, dataProvider);

        Mockito.when(sut.getCarrier().getScac()).thenReturn(CARRIER_SCAC);
    }

    @After
    public void tearDown() {
        deleteFile(EDI_FILE_NAME_A);
        deleteFile(EDI_FILE_NAME_D_K1);
        deleteFile(EDI_FILE_NAME_D_V9);
    }

    @Test
    public void testParseEDI990_A() throws Exception {
        EDIFile file = getEdiFile(EDI_FILE_NAME_A, TRANSACTION_SET_ID, CARRIER_SCAC);
        EDIParseResult<LoadTrackingEntity> parseResult = sut.parse(file);
        Assert.assertNotNull(parseResult);
        List<LoadTrackingEntity> loadTrackingEntities = parseResult.getParsedEntities();
        Assert.assertNotNull(loadTrackingEntities);
        Assert.assertEquals(ENTITIES_COUNT, loadTrackingEntities.size());

        LoadTrackingEntity entity = loadTrackingEntities.get(0);
        Assert.assertEquals(PRO, entity.getPro());
        Assert.assertEquals(BOL, entity.getBol());
        Assert.assertEquals(SCAC, entity.getScac());
        Assert.assertEquals("A", entity.getStatusCode());
    }

    @Test
    public void testParseEDI990_D_K1() throws Exception {
        EDIFile file = getEdiFile(EDI_FILE_NAME_D_K1, TRANSACTION_SET_ID, CARRIER_SCAC);
        EDIParseResult<LoadTrackingEntity> parseResult = sut.parse(file);
        Assert.assertNotNull(parseResult);
        List<LoadTrackingEntity> loadTrackingEntities = parseResult.getParsedEntities();
        Assert.assertNotNull(loadTrackingEntities);
        Assert.assertEquals(ENTITIES_COUNT, loadTrackingEntities.size());

        LoadTrackingEntity entity = loadTrackingEntities.get(0);
        Assert.assertEquals(BOL, entity.getBol());
        Assert.assertEquals(SCAC, entity.getScac());
        Assert.assertEquals("D", entity.getStatusCode());
        Assert.assertEquals("CC", entity.getStatusReasonCode());
        Assert.assertEquals("D:Reservation Cancelled by carrier. Capacity Constraints - CAPACITY CONSTRAINTS.", entity.getFreeMessage());
    }

    @Test
    public void testParseEDI990_D_V9() throws Exception {
        EDIFile file = getEdiFile(EDI_FILE_NAME_D_V9, TRANSACTION_SET_ID, CARRIER_SCAC);
        EDIParseResult<LoadTrackingEntity> parseResult = sut.parse(file);
        Assert.assertNotNull(parseResult);
        List<LoadTrackingEntity> loadTrackingEntities = parseResult.getParsedEntities();
        Assert.assertNotNull(loadTrackingEntities);
        Assert.assertEquals(ENTITIES_COUNT, loadTrackingEntities.size());

        LoadTrackingEntity entity = loadTrackingEntities.get(0);
        Assert.assertEquals(BOL, entity.getBol());
        Assert.assertEquals(SCAC, entity.getScac());
        Assert.assertEquals("D", entity.getStatusCode());
        Assert.assertEquals("A07", entity.getStatusReasonCode());
        Assert.assertEquals("D:Reservation Cancelled by carrier. Refused by Consignee.", entity.getFreeMessage());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateEDI990() {
        sut.create(Arrays.asList(new LoadTrackingEntity()), null);
    }
}
