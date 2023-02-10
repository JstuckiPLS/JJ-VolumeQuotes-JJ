package com.pls.shipment.service.edi.parser;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.organization.CarrierEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;
import com.pls.shipment.service.impl.edi.parser.EDI214Parser;
import com.pls.shipment.service.impl.edi.utils.EDIUtils;

/**
 * Test cases for {@link EDI214Parser} class.
 *
 * @author Mikhail Boldinov, 06/03/14
 */
@RunWith(MockitoJUnitRunner.class)
public class EDI214ParserTest extends AbstractEDIParserTest {
    private static final String CARRIER_SCAC = "RDWY";
    private static final String TRANSACTION_SET_ID = "214";
    private static final String EDI_FILE_NAME = "CTSI214.txt";
    private static final int ENTITIES_COUNT = 184;

    private static final String PRO = "1090673631";
    private static final String BOL = "061339";
    private static final String SCAC = "RDWY";
    private static final String STATUS_CODE = "P1";
    private static final String STATUS_REASON_CODE = "NS";
    private static final String TRACKING_DATE = "20130819";
    private static final String TRACKING_TIME = "072500";
    private static final String TRACKING_TIMEZONE = "ET";
    private static final String TRACKING_CITY = "CARLISLE";
    private static final String TRACKING_STATE = "PA";
    private static final String TRACKING_COUNTRY = "US";
    private static final String EDI_ACCOUNT = "BOB BARKER";

    @Mock
    private CarrierEntity carrier;

    @Mock
    private AbstractEDIParser.DataProvider dataProvider;

    @InjectMocks
    private EDI214Parser sut;

    @Before
    public void setUp() {
        Mockito.when(sut.getCarrier().getScac()).thenReturn(CARRIER_SCAC);
    }

    @After
    public void tearDown() {
        deleteFile(EDI_FILE_NAME);
    }

    @Test
    public void testParseEDI214() throws Exception {
        EDIParseResult<LoadTrackingEntity> parseResult = sut.parse(getEdiFile(EDI_FILE_NAME, TRANSACTION_SET_ID, CARRIER_SCAC));
        Assert.assertNotNull(parseResult);
        List<LoadTrackingEntity> loadTrackingEntities = parseResult.getParsedEntities();
        Assert.assertNotNull(loadTrackingEntities);
        Assert.assertEquals(ENTITIES_COUNT, loadTrackingEntities.size());
        for (int i = 0; i < loadTrackingEntities.size(); i++) {
            Assert.assertNotNull(loadTrackingEntities.get(i));
            switch (i) {
                case 0:
                    assertEntityValid(loadTrackingEntities.get(i));
                    break;
                case 1:
                    Assert.assertEquals(loadTrackingEntities.get(i - 1).getBol(), loadTrackingEntities.get(i).getBol());
                    Assert.assertEquals(loadTrackingEntities.get(i - 1).getScac(), loadTrackingEntities.get(i).getScac());
                    Assert.assertEquals(loadTrackingEntities.get(i - 1).getPro(), loadTrackingEntities.get(i).getPro());
                    break;
                case 2:
                    Assert.assertEquals("20130819090300", EDIUtils.toDateTimeStr(loadTrackingEntities.get(i).getDepartureTime()));
                    break;
                case 3:
                    Assert.assertEquals("20130819072900", EDIUtils.toDateTimeStr(loadTrackingEntities.get(i).getDepartureTime()));
                    break;
                default:
                    break;
            }
        }
    }

    private static void assertEntityValid(LoadTrackingEntity entity) {
        Assert.assertEquals(PRO, entity.getPro());
        Assert.assertEquals(BOL, entity.getBol());
        Assert.assertEquals(SCAC, entity.getScac());
        Assert.assertEquals(STATUS_CODE, entity.getStatusCode());
        Assert.assertEquals(STATUS_REASON_CODE, entity.getStatusReasonCode());
        Assert.assertEquals(TRACKING_DATE + TRACKING_TIME, EDIUtils.toDateTimeStr(entity.getDepartureTime()));
        Assert.assertEquals(TRACKING_TIMEZONE, entity.getTimezoneCode());
        Assert.assertEquals(TRACKING_CITY, entity.getCity());
        Assert.assertEquals(TRACKING_STATE, entity.getState());
        Assert.assertEquals(TRACKING_COUNTRY, entity.getCountry());
        Assert.assertEquals(EDI_ACCOUNT, entity.getEdiAccount());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateEDI210() {
        sut.create(Arrays.asList(new LoadTrackingEntity()), null);
    }
}
