package com.pls.restful.invoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.ResponseVO;
import com.pls.dto.ValueLabelDTO;
import com.pls.dto.invoice.InvoiceErrorDTO;
import com.pls.dtobuilder.invoice.InvoiceErrorsDTOBuilder;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.InvoiceErrorService;
import com.pls.restful.TransactionalReadOnly;
import com.pls.restful.TransactionalReadWrite;

/**
 * Shipment financial board resource.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@TransactionalReadOnly
@RequestMapping("/invoice/financial/board/errors")
public class InvoiceErrorResource {
    private final InvoiceErrorsDTOBuilder invoiceErrorsDTOBuilder = new InvoiceErrorsDTOBuilder();

    @Autowired
    private InvoiceErrorService errorService;

    /**
     * Get all active invoice errors.
     *
     * @return list of {@link InvoiceErrorDTO}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<InvoiceErrorDTO> getErrors() {
        return invoiceErrorsDTOBuilder.buildList(errorService.getInvoiceErrors());
    }

    /**
     * Get count of available active customer invoice errors.
     *
     * @return {@link ValueLabelDTO} object with value field
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public ValueLabelDTO getErrorsCount() {
        return new ValueLabelDTO(errorService.getCountOfInvoiceErrors(), null);
    }

    /**
     * Cancel specified by id invoice error.
     *
     * @param errorId
     *            id of invoice error
     * @throws EntityNotFoundException
     *             if invoice error with specified id was not found
     */
    @RequestMapping(value = "/{errorId}/cancel", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @TransactionalReadWrite
    public void cancelInvoiceError(@PathVariable("errorId") long errorId) throws EntityNotFoundException {
        errorService.cancelError(errorId);
    }

    /**
     * Re-process invoice accordingly to invoice error information.
     *
     * @param errorId
     *            id of invoice error
     * @param bo
     *            email subject and comments if available
     * @return error text if re-processing failed
     */
    @RequestMapping(value = "/{errorId}/reprocess", method = RequestMethod.PUT)
    @TransactionalReadWrite
    @ResponseBody
    public ResponseVO reprocessInvoiceError(@PathVariable("errorId") long errorId, @RequestBody SendToFinanceBO bo) {
        String error = null;
        if (bo.getInvoiceProcessingDetails() != null) {
            InvoiceProcessingBO invoiceBillToDetails = bo.getInvoiceProcessingDetails().get(0);
            error = errorService.reprocessError(errorId, invoiceBillToDetails.getSubject(),
                    invoiceBillToDetails.getComments(), SecurityUtils.getCurrentPersonId());
        }
        return new ResponseVO(error);
    }

    /**
     * Get subject of invoice email that should be sent to customer.
     * 
     * @param errorId
     *            id of invoice error
     * @return email subject or <code>null</code>
     */
    @RequestMapping(value = "/{errorId}/subject", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getEmailSubjectForReprocessError(@PathVariable("errorId") long errorId) {
        return new ResponseVO(errorService.getEmailSubjectForReprocessError(errorId));
    }
}
