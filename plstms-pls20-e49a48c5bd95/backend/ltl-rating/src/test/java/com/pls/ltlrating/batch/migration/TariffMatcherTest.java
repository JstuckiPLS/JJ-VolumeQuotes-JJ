package com.pls.ltlrating.batch.migration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xml.transform.ResourceSource;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.extint.shared.DataModuleVO;
import com.pls.ltlrating.batch.migration.model.enums.TariffGeoType;
import com.pls.smc3.service.SMC3Service;
import com.smc.products.datamodule.DataModule;
import com.smc.webservices.AvailableTariffsResponse;

/**
 * Test cases for {@link TariffMatcher}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/test-configuration.xml")
public class TariffMatcherTest {

    @Autowired
    private Jaxb2Marshaller testMarshaller;

    @Value("classpath:smc3/availableTariffs.xml")
    private Resource availableTariffsResource;

    private List<DataModuleVO> availableTariffs = new ArrayList<>();


    @Mock
    private SMC3Service smc3Service;

    @InjectMocks
    private TariffMatcher tariffMatcher;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(smc3Service.getAvailableTariffs()).thenReturn(availableTariffs);
        tariffMatcher.beforeJob(null);
    }

    @After
    public void tearDown() {
        tariffMatcher.afterJob(null);
    }

    @Test
    public void shoudlMatchTariff() throws ImportException {
        assertEquals("AACTCZ02_20050502_9315-RWM", tariffMatcher.mapToSmc3TariffName("AACTCZ02_20050502", TariffGeoType.CANADA_ONLY));
        assertEquals("AACTCZ02_20050502_9315-RWM", tariffMatcher.mapToSmc3TariffName("AACTCZ02_20050502_9315-RWM", TariffGeoType.US_ONLY));
        assertEquals("AVRTCZ02_20070416_11563-RWM", tariffMatcher.mapToSmc3TariffName("AVRTCZ02_20070416", TariffGeoType.US_ONLY));
        assertEquals("AVRTCZ02_20070416_11563-RWM", tariffMatcher.mapToSmc3TariffName("AVRTCZ02_20070416", TariffGeoType.US_CANADA));
        assertEquals("CTS59901_20030801_18246-RWMCC", tariffMatcher.mapToSmc3TariffName("CTS59901_20030801", TariffGeoType.CANADA_ONLY));
        assertEquals("LITECZ02_20001001_20796-RWM", tariffMatcher.mapToSmc3TariffName("LITECZ02_20001001", TariffGeoType.US_ONLY));
        assertEquals("LITECZ02_20001001_20796-RWM", tariffMatcher.mapToSmc3TariffName("LITECZ02_20001001_19700-RWM", TariffGeoType.US_ONLY));
    }

    @Test(expected = ImportException.class)
    public void shouldNotFindTariff() throws ImportException {
        tariffMatcher.mapToSmc3TariffName("NONEXISTING_20050502", TariffGeoType.US_CANADA);
    }

    public void shouldNotFindTariffNonExistingGeoType() throws ImportException {
        assertEquals("EXL50001_20090202_17072-RWMUC", tariffMatcher.mapToSmc3TariffName("EXL50001_20090202", TariffGeoType.CANADA_ONLY));
    }

    @Test(expected = ImportException.class)
    public void shouldFailNullTariffName() throws ImportException {
        tariffMatcher.mapToSmc3TariffName(null, TariffGeoType.US_CANADA);
    }

    @Test(expected = ImportException.class)
    public void shouldFailEmptyTariffName() throws ImportException {
        tariffMatcher.mapToSmc3TariffName("   ", TariffGeoType.US_CANADA);
    }

    @Test(expected = ImportException.class)
    public void shouldFailNullTariffGeoType() throws ImportException {
        tariffMatcher.mapToSmc3TariffName("AACTCZ02_20050502", null);
    }

    @PostConstruct
    public void parseAvailableTariffs() throws IOException {
        AvailableTariffsResponse availableTariffsResponse = (AvailableTariffsResponse) testMarshaller.unmarshal(new ResourceSource(
                availableTariffsResource));
        for (DataModule dataModule : availableTariffsResponse.getOut().getDataModule()) {
            DataModuleVO tariffData = new DataModuleVO();
            tariffData.setDescription(dataModule.getDescription());
            tariffData.setEffectiveDate(dataModule.getEffectiveDate());
            tariffData.setProductNumber(dataModule.getProductNumber());
            tariffData.setTariffName(dataModule.getTariffName() + "_" + dataModule.getEffectiveDate() + "_"
                                     + dataModule.getProductNumber());
            availableTariffs.add(tariffData);
        }
    }
}
