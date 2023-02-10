package com.pls.dtobuilder.invoice;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.PlainModificationObject;
import com.pls.dto.invoice.InvoiceErrorDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;

/**
 * DTO Builder for convertion from {@link CustomerInvoiceErrorEntity} to {@link InvoiceErrorDTO} and vice versa.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class InvoiceErrorsDTOBuilder extends AbstractDTOBuilder<CustomerInvoiceErrorEntity, InvoiceErrorDTO> {
    @Override
    public InvoiceErrorDTO buildDTO(CustomerInvoiceErrorEntity entity) {
        InvoiceErrorDTO dto = new InvoiceErrorDTO();
        dto.setId(entity.getId());
        PlainModificationObject modification = entity.getModification();
        Date errorDateTime;
        if (modification.getModifiedDate() != null) {
            errorDateTime = modification.getModifiedDate();
        } else {
            errorDateTime = modification.getCreatedDate();
        }
        dto.setDateTime(ZonedDateTime.ofInstant(errorDateTime.toInstant(), ZoneOffset.UTC));
        String userName = UserNameBuilder.buildFullName(modification.getCreatedUser());
        dto.setUserName(userName);
        dto.setMessage(entity.getMessage());
        dto.setInvoiceId(entity.getInvoiceId());

        dto.setEvent(buildEventText(entity));

        return dto;
    }

    private String buildEventText(CustomerInvoiceErrorEntity entity) {
        StringBuilder eventStrBuilder = new StringBuilder(127);
        eventStrBuilder.append("Process invoice: ");

        boolean reasonFilled = false;
        if (BooleanUtils.isFalse(entity.getSentEdi())) {
            eventStrBuilder.append("Failure sending EDI ");
            reasonFilled = true;
        }

        if (BooleanUtils.isFalse(entity.getSentEmail())) {
            if (reasonFilled) {
                eventStrBuilder.append("and ");
            }
            eventStrBuilder.append("Email sending failure ");
            reasonFilled = true;
        }
        if (BooleanUtils.isFalse(entity.getSentToFinance())) {
            if (reasonFilled) {
                eventStrBuilder.append("and ");
            }
            eventStrBuilder.append("Sending to Finance failure ");
        }
        if (BooleanUtils.isFalse(entity.getSentDocuments())) {
            if (reasonFilled) {
                eventStrBuilder.append("and ");
            }
            eventStrBuilder.append("Sending Documents failure ");
        }

        eventStrBuilder.append("for Invoice ID ");
        eventStrBuilder.append(entity.getInvoiceId());
        return eventStrBuilder.toString();
    }

    /**
     * Method is not supported.
     * 
     * @param dto
     *            dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public CustomerInvoiceErrorEntity buildEntity(InvoiceErrorDTO dto) {
        throw new UnsupportedOperationException();
    }
}
