package com.pls.shipment.service.impl.edi.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.edi.handler.EDI990ParseResultHandler;

/**
 * {@link EDI990ParseResultHandler} implementation for EDI 990.
 *
 * @author Alexandr Nalapko
 */
@Service
public class EDI990ParseResultHandlerImpl extends AbstractEDI990and997ParseResultHandlerImpl implements
        EDI990ParseResultHandler {

    private static final String EDI_NUMBER = "990";

    @Override
    public List<Integer> handle(EDIParseResult<LoadTrackingEntity> parseResult) throws Exception {
        String ediFileName = parseResult.getEdiFile().getName();
        List<Integer> unprocessedEntities = new ArrayList<Integer>();
        if (parseResult.getStatus() == EDIParseResult.Status.SUCCESS) {
            try {
                for (LoadTrackingEntity loadTracking : parseResult.getParsedEntities()) {
                    List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(loadTracking.getScac(),
                            loadTracking.getBol());
                    if (loads.size() == 1) {
                        saveLoadTracking(loadTracking, loads.get(0));
                    } else {
                        unprocessedEntities.add(parseResult.getParsedEntities().indexOf(loadTracking));
                    }
                }
            } catch (Exception e) {
                ediEmailSender.loadTrackingFailed(ediFileName, e.getMessage(), EDI_NUMBER, null);
                throw e;
            }
            if (!unprocessedEntities.isEmpty()) {
                List<String> bolNumbers = new ArrayList<String>();
                for (Integer index : unprocessedEntities) {
                    bolNumbers.add(parseResult.getParsedEntities().get(index).getBol());
                }
                String errorMsg = String.format(
                        "Saving Shipment Tracking failed. Unable to find Shipment by BOL# '%s' for Carrier '%s'",
                        StringUtils.join(bolNumbers, "','"), parseResult.getParsedEntities().get(0).getScac());
                ediEmailSender.loadTrackingFailed(ediFileName, errorMsg, EDI_NUMBER, null);
                if (unprocessedEntities.size() == parseResult.getParsedEntities().size()) {
                    throw new ApplicationException(errorMsg);
                }
            }
        } else {
            ediEmailSender.loadTrackingFailed(ediFileName, parseResult.getErrorMsg(), EDI_NUMBER, null);
        }
        return unprocessedEntities;
    }
}
