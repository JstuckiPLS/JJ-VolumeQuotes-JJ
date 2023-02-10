package com.pls.shipment.service.impl.edi.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.shipment.dao.LoadEdiDataDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.edi.handler.EDI997ParseResultHandler;

/**
 * {@link EDI997ParseResultHandler} implementation for EDI 997.
 *
 * @author Alexandr Nalapko
 */
@Service
public class EDI997ParseResultHandlerImpl extends AbstractEDI990and997ParseResultHandlerImpl implements
        EDI997ParseResultHandler {

    private static final String EDI_NUMBER = "997";

    @Autowired
    private LoadEdiDataDao loadEdiDataDao;

    @Override
    public List<Integer> handle(EDIParseResult<LoadTrackingEntity> parseResult) throws Exception {
        String ediFileName = parseResult.getEdiFile().getName();
        List<Integer> unprocessedEntities = new ArrayList<Integer>();
        if (parseResult.getStatus() == EDIParseResult.Status.SUCCESS) {
            try {
                for (LoadTrackingEntity loadTracking : parseResult.getParsedEntities()) {
                    LoadEntity load = loadEdiDataDao.getLoadByGS(parseResult.getEdiFile().getGs());
                    if (load != null) {
                        saveLoadTracking(loadTracking, load);
                    } else {
                        unprocessedEntities.add(parseResult.getParsedEntities().indexOf(loadTracking));
                    }
                }
            } catch (Exception e) {
                ediEmailSender.loadTrackingFailed(ediFileName, e.getMessage(), EDI_NUMBER, null);
                throw e;
            }
        } else {
            ediEmailSender.loadTrackingFailed(ediFileName, parseResult.getErrorMsg(), EDI_NUMBER, null);
        }
        return unprocessedEntities;
    }
}
