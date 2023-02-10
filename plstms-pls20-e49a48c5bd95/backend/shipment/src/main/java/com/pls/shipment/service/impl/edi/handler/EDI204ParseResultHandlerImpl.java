package com.pls.shipment.service.impl.edi.handler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.shipment.dao.LoadEdiDataDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.domain.LoadEdiDataEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.edi.handler.EDI204ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;

/**
 * {@link EDIParseResultHandler} implementation for EDI 204.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
@Service
public class EDI204ParseResultHandlerImpl implements EDI204ParseResultHandler {

    private static final String TIMEZONE_CODE = "ET";

    public static final String TRACKING_STATUS_CODE = "EC";

    public static final Long EDI204 = 204L;

    @Value("${admin.personId}")
    private Long ediUserId;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private LoadEdiDataDao loadEdiDataDao;

    @Override
    public List<Integer> handle(EDIParseResult<LoadEntity> parseResult) throws Exception {
        List<LoadEntity> loads = parseResult.getParsedEntities();
        if (loads == null || loads.size() != 1) {
            throw new IllegalArgumentException("Unexpected Shipments count. Expected one Shipment to be sent by EDI.");
        }
        LoadEntity load = loads.get(0);
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setSource(EDI204);
        loadTracking.setLoad(load);
        loadTracking.setCreatedBy(ediUserId);
        loadTracking.setCarrier(load.getCarrier());
        loadTracking.setPro(load.getNumbers().getProNumber());
        loadTracking.setBol(load.getNumbers().getBolNumber());
        loadTracking.setScac(load.getCarrier() != null ? load.getCarrier().getScac() : null);
        loadTracking.setStatusCode(TRACKING_STATUS_CODE);
        loadTracking.setTrackingDate(new Date());
        loadTracking.setTimezoneCode(TIMEZONE_CODE);
        loadTrackingDao.saveOrUpdate(loadTracking);

        LoadEdiDataEntity loadEdiData = new LoadEdiDataEntity();
        loadEdiData.setFileName(parseResult.getEdiFile().getName());
        loadEdiData.setLoad(load);
        loadEdiData.setGS(parseResult.getEdiFile().getGs());
        loadEdiData.setISA(parseResult.getEdiFile().getIsa());
        loadEdiData.setSource(EDI204);
        loadEdiDataDao.saveOrUpdate(loadEdiData);
        return Collections.emptyList();
    }
}
