package com.pls.shipment.service.impl.edi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.sterling.AcknowledgementJaxbBO;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * This Service is provided to facilitate EDI997 processing.
 *
 * @author Jasmin Dhamelia
 *
 */

@Service("acknowledgement")
@Transactional(readOnly = true)
public class AcknowledgementServiceImpl implements IntegrationService<AcknowledgementJaxbBO> {

    @Autowired
    protected LtlShipmentDao ltlShipmentDao;

    @Autowired
    protected LoadTrackingDao loadTrackingDao;

    @Autowired
    protected EDIEmailSender ediEmailSender;

    protected static List<String> rejectStatusList = Arrays.asList(new String[] { "R", "M", "W", "X" });

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processMessage(AcknowledgementJaxbBO acknowledgementBO) throws ApplicationException {
        LoadEntity load = getLoad(acknowledgementBO);
        LoadTrackingEntity loadTrackingEntity = new LoadTrackingEntity();
        loadTrackingEntity.setStatusCode(StringUtils.trimToNull(acknowledgementBO.getStatus()));
        loadTrackingEntity.setFreeMessage(acknowledgementBO.getTransactionSetId());
        loadTrackingEntity.setTrackingDate(acknowledgementBO.getRecvDateTime());
        loadTrackingEntity.setCreatedBy(acknowledgementBO.getPersonId());
        loadTrackingEntity.setSource(EDI_997);
        if (load != null) {
            try {
                saveLoadTracking(loadTrackingEntity, load);
            } catch (Exception e) {
                ediEmailSender.loadTrackingFailed(acknowledgementBO.getB2biFileName(), e.getMessage(), EDI_997.toString(),
                        Collections.singletonList(load.getId()));
                throw e;
            }
        }
    }

    /**
     * returns load entity.
     *
     * @param acknowledgementBO
     *            ack business object.
     * @return load entity.
     */
    private LoadEntity getLoad(AcknowledgementJaxbBO acknowledgementBO) {
        if (acknowledgementBO.getLoadId() != null) {
            return ltlShipmentDao.find(acknowledgementBO.getLoadId());
        } else if (!StringUtils.isBlank(acknowledgementBO.getScac())) {
            List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(acknowledgementBO.getScac(), acknowledgementBO.getBol());
            return loads.size() == 1 ? loads.get(0) : null;
        } else if (acknowledgementBO.getCustomerOrgId() != null) {
            return ltlShipmentDao.findShipmentByShipmentNumber(acknowledgementBO.getCustomerOrgId(),
                    acknowledgementBO.getShipmentNo());
        }
        return null;
    }

    /**
     * * save Load Tracking.
     *
     * @param loadTracking
     *            {@link LoadTrackingEntity}
     * @param load
     *            {@link LoadEntity}
     */
    protected void saveLoadTracking(LoadTrackingEntity loadTracking, LoadEntity load) {
        loadTracking.setLoad(load);
        loadTrackingDao.saveOrUpdate(loadTracking);
        if (rejectStatusList.contains(loadTracking.getStatusCode())) {
            if (loadTracking.getLoad().getOrganization().getNetworkId() == LTL_NETWORK) {
                ediEmailSender.forLTLDistributionList(load, loadTracking.getFreeMessage());
            } else {
                // Sends an Email to the Creator of the object.
                ediEmailSender.forCreatedUser(load.getModification().getCreatedUser().getEmail(), load, loadTracking.getFreeMessage());
                // Sends an Email to the Account Executive.
                ediEmailSender.forCreatedUser(load.getLocation().getActiveAccountExecutive().getUser().getEmail(), load,
                        loadTracking.getFreeMessage());
            }
        }
    }
}
