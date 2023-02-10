package com.pls.restful.shipment.webservices.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.restful.shipment.webservices.domain.LtlAcknowledgementRequest;
import com.pls.restful.shipment.webservices.domain.LtlAcknowledgementResponse;
import com.pls.restful.shipment.webservices.domain.LtlLoadMessageRequest;
import com.pls.restful.shipment.webservices.domain.LtlLoadMessageResponse;
import com.pls.restful.shipment.webservices.domain.LtlLoadTenderAckRequest;
import com.pls.restful.shipment.webservices.domain.LtlLoadTenderAckResponse;
import com.pls.restful.shipment.webservices.domain.LtlLoadTrackingRequest;
import com.pls.restful.shipment.webservices.domain.LtlLoadTrackingResponse;
import com.pls.restful.shipment.webservices.domain.LtlLoadUpdateRequest;
import com.pls.restful.shipment.webservices.domain.LtlLoadUpdateResponse;
import com.pls.restful.shipment.webservices.domain.LtlVendorInvoiceRequest;
import com.pls.restful.shipment.webservices.domain.LtlVendorInvoiceResponse;
import com.pls.shipment.domain.sterling.LoadUpdateJaxbBO;
import com.pls.shipment.service.impl.edi.sterling.jms.producer.SterlingEDIIncomingJMSMessageProducer;

/**
 * Endpoint class which handles all the SOAP requests from Sterling.
 * 
 * @author Jasmin Dhamelia
 * @author Aleksandr Leshchenko
 */
@Endpoint
@Transactional(readOnly = true)
public class SterlingIncomingEndpoint {
    private static final String TARGET_NAMESPACE = "http://com.pls.load";
    private static final String RESPONSE_SUCCESS = "OK";

    @Autowired
    private SterlingEDIIncomingJMSMessageProducer producer;


    /**
     * Creates a new shipment/updates existing shipment/cancels existing shipment based on operation type for the customer org id sent in the request.
     * 
     * @param request
     *            the incoming EDI 204 request
     * @throws ApplicationException
     *            Application exception for exceptions thrown during the shipment creation/update/cancellation.
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlLoadMessageRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlLoadMessageResponse processLtlLoadMessage(@RequestPayload LtlLoadMessageRequest request) throws ApplicationException {
        request.getLtlLoadMessage().setPersonId(SecurityUtils.getCurrentPersonId());
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(request.getLtlLoadMessage(),
                EDIMessageType.EDI204_STERLING_MESSAGE_TYPE);
        producer.publish(message);
        return new LtlLoadMessageResponse(RESPONSE_SUCCESS);
    }

    /**
     * Updates the tracking for shipment with the functional acknowledgement.
     * 
     * @param request
     *            the incoming EDI 997 request
     * @throws ApplicationException
     *            Application exception for exceptions thrown while updating tracking tab
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlAcknowledgementRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlAcknowledgementResponse processLtlAcknowledgement(@RequestPayload LtlAcknowledgementRequest request) throws ApplicationException {
        request.getLtlAcknowledgement().setPersonId(SecurityUtils.getCurrentPersonId());
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(request.getLtlAcknowledgement(),
                EDIMessageType.EDI997_STERLING_MESSAGE_TYPE);
        producer.publish(message);
        return new LtlAcknowledgementResponse(RESPONSE_SUCCESS);
    }

    /**
     * Updates the load with the tracking information received from Carrier.
     * 
     * @param request
     *            the incoming EDI 214 request
     * @throws ApplicationException
     *            Application exception for exceptions thrown while update the load.
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlLoadTrackingRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlLoadTrackingResponse processLtlLoadTracking(@RequestPayload LtlLoadTrackingRequest request) throws ApplicationException {
        request.getLtlLoadTracking().setPersonId(SecurityUtils.getCurrentPersonId());
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(request.getLtlLoadTracking(),
                EDIMessageType.EDI214_STERLING_MESSAGE_TYPE);
        producer.publish(message);
        return new LtlLoadTrackingResponse(RESPONSE_SUCCESS);
    }

    /**
     * Updates the load with tender acknowledgement from Carrier.
     * 
     * @param request
     *            the incoming EDI 990 request
     * @throws ApplicationException
     *            Application exception for exceptions thrown while updating the load
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlLoadTenderAckRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlLoadTenderAckResponse processLtlLoadTenderAck(@RequestPayload LtlLoadTenderAckRequest request) throws ApplicationException {
        request.getLtlLoadTenderAck().setPersonId(SecurityUtils.getCurrentPersonId());
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(request.getLtlLoadTenderAck(),
                EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);
        producer.publish(message);
        return new LtlLoadTenderAckResponse(RESPONSE_SUCCESS);
    }

    /**
     * Update the carrier invoice for the shipment.
     * 
     * @param request
     *            the incoming EDI 210 request
     * @throws ApplicationException
     *            Application exception for exceptions thrown while updating the vendor invoice.
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlVendorInvoiceRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlVendorInvoiceResponse processLtlVendorInvoice(@RequestPayload LtlVendorInvoiceRequest request) throws ApplicationException {
        request.getLtlVendorInvoice().setPersonId(SecurityUtils.getCurrentPersonId());
        SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(request.getLtlVendorInvoice(),
                EDIMessageType.EDI210_STERLING_MESSAGE_TYPE);
        producer.publish(message);
        return new LtlVendorInvoiceResponse(RESPONSE_SUCCESS);
    }
    
    /**
     * Updates existing shipment with provided identifier, updating only provided non-null values.
     * 
     * @param request
     *            the incoming update request
     * @throws ApplicationException
     * @return success if the operation is successful.
     */
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @PayloadRoot(localPart = "LtlLoadUpdateRequest", namespace = TARGET_NAMESPACE)
    @ResponsePayload
    public LtlLoadUpdateResponse processLtlLoadUpdate(@RequestPayload LtlLoadUpdateRequest request) throws ApplicationException {
        for(LoadUpdateJaxbBO loadUpdate : request.getLtlLoadUpdates()) {
            loadUpdate.setPersonId(SecurityUtils.getCurrentPersonId());
            SterlingIntegrationMessageBO message = new SterlingIntegrationMessageBO(loadUpdate, EDIMessageType.LOAD_UPDATE_MESSAGE_TYPE);
            producer.publish(message);
        }
        
        return new LtlLoadUpdateResponse(RESPONSE_SUCCESS);
    }
}