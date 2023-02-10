package com.pls.shipment.service.impl.edi.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * {@link EDIParseResultHandler} implementation for EDI 990/997.
 *
 * @author Alexandr Nalapko
 */

public abstract class AbstractEDI990and997ParseResultHandlerImpl implements EDIParseResultHandler<LoadTrackingEntity> {

    @Autowired
    protected LtlShipmentDao ltlShipmentDao;

    @Autowired
    protected LoadTrackingDao loadTrackingDao;

    @Autowired
    protected EDIEmailSender ediEmailSender;

    protected static List<String> sendStatus = Arrays.asList(new String[] { "D", "R", "B", "C", "E", "N", "S", "V" });

    @Override
    public abstract List<Integer> handle(EDIParseResult<LoadTrackingEntity> parseResult) throws Exception;

    /**
     * save Load Tracking.
     * 
     * @param loadTracking {@link LoadTrackingEntity}}
     * @param load {@link LoadEntity}}
     */
    protected void saveLoadTracking(LoadTrackingEntity loadTracking, LoadEntity load) {
        loadTracking.setLoad(load);
        loadTrackingDao.saveOrUpdate(loadTracking);
        if (sendStatus.contains(loadTracking.getStatusCode())) {
            if (load.getOrganization().getNetworkId() == 7L) {
                ediEmailSender.forLTLDistributionList(load, loadTracking.getStatus().getDescription());
            } else {
                // TODO add sending email to Account Owner = Account Executive if empty to the
                // user who created an order
                ediEmailSender.forCreatedUser(load.getModification().getCreatedUser().getEmail(), load, loadTracking
                        .getStatus().getDescription());
            }
        }
    }
}
