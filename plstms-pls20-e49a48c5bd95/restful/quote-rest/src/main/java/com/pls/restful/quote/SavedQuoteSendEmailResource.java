package com.pls.restful.quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.email.dto.EmailDetailsDTO;
import com.pls.quote.service.impl.email.SavedQuoteEmailSender;

/**
 * REST for sending email related to Saved Quotes.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/quote/email")
public class SavedQuoteSendEmailResource {
    @Autowired
    private SavedQuoteEmailSender emailSender;

    /**
     * Send email for Saved Quote.
     * 
     * @param emailDetails
     *            params with email information
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendQuoteEmail(@RequestBody EmailDetailsDTO emailDetails) {
        emailSender.sendQuoteEmail(emailDetails.getRecipients(), emailDetails.getSubject(), emailDetails.getContent(), emailDetails.getLoadId());
    }

    /**
     * Get content of email for sending Saved Quote.
     * 
     * @param quoteId
     *            {@link SavedQuoteEntity#getId(}
     * @return content
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getQuoteEmailContent(@RequestParam("quoteId") Long quoteId) {
        return emailSender.getPayloadForQuoteEmail(quoteId);
    }
}
