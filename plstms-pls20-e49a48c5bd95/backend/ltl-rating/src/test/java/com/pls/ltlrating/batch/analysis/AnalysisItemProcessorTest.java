package com.pls.ltlrating.batch.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.shared.LtlPricingResult;

/**
 * Test for {@link AnalysisItemProcessor}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AnalysisItemProcessorTest {

    private static final String DESTINATION_ZIP = "19187";

    private static final String ORIGIN_ZIP = "19104";

    private static final String USA = "USA";

    private static final long ROW_ID = 1L;

    @InjectMocks
    private AnalysisItemProcessor analysisItemProcessor;

    @Mock
    private LtlRatingEngineService ratingService;

    @Mock
    private Session mockedSession;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private ZipCodeDao zipDao;

    @Test
    public void testProcess() throws Exception {
        List<LtlPricingResult> resultList = fillRates();
        FAInputDetailsEntity input = fillInput();
        Mockito.when(ratingService.getRates(Mockito.any())).thenReturn(resultList);
        Mockito.when(mockedSession.get(FAInputDetailsEntity.class, 1L)).thenReturn(input);
        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
        List<ZipCodeEntity> originZips = fillOriginZips();
        List<ZipCodeEntity> destinationZips = fillDestinationZips();
        Mockito.when(zipDao.getDefault(USA, ORIGIN_ZIP)).thenReturn(originZips);
        Mockito.when(zipDao.getDefault(USA, DESTINATION_ZIP)).thenReturn(destinationZips);
        AnalysisItem item = fillAnalysisItem();
        analysisItemProcessor.process(item);
    }

    private AnalysisItem fillAnalysisItem() {
        AnalysisItem item = new AnalysisItem();
        item.setCustomerId(1L);
        item.setRowId(ROW_ID);
        return item;
    }

    private List<ZipCodeEntity> fillDestinationZips() {
        List<ZipCodeEntity> destinationZips = new ArrayList<ZipCodeEntity>(1);
        ZipCodeEntity destinationZip = new ZipCodeEntity();
        destinationZip.setZipCode(DESTINATION_ZIP);
        destinationZips.add(destinationZip);
        return destinationZips;
    }

    private List<ZipCodeEntity> fillOriginZips() {
        List<ZipCodeEntity> originZips = new ArrayList<ZipCodeEntity>(1);
        ZipCodeEntity originZip = new ZipCodeEntity();
        originZip.setZipCode(ORIGIN_ZIP);
        originZips.add(originZip);
        return originZips;
    }

    private FAInputDetailsEntity fillInput() {
        FAInputDetailsEntity input = new FAInputDetailsEntity();
        input.setOriginCountry(USA);
        input.setOriginZip(ORIGIN_ZIP);
        input.setDestCountry(USA);
        input.setDestZip(DESTINATION_ZIP);
        input.setMaterials(new HashSet<>());
        input.setAccessorials(new HashSet<>());
        input.setAnalysis(new FAFinancialAnalysisEntity());
        input.getAnalysis().setTariffs(new HashSet<>());
        return input;
    }

    private List<LtlPricingResult> fillRates() {
        List<LtlPricingResult> resultList = new ArrayList<>();
        LtlPricingResult result1 = new LtlPricingResult();
        LtlPricingResult result2 = new LtlPricingResult();
        resultList.add(result1);
        resultList.add(result2);
        result1.setBlockedFrmBkng(StatusYesNo.YES);
        result2.setBlockedFrmBkng(StatusYesNo.NO);
        return resultList;
    }
}
