package com.pls.quote.service;

import java.util.Date;
import java.util.List;

import com.pls.core.exception.ApplicationException;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Saved Quote service.
 *
 * @author Mikhail Boldinov, 27/03/13
 */
public interface SavedQuoteService {

    /**
     * Returns list of saved Quotes which was saved from LTL Rater wizard on Select Carrier step. Used to get
     * list of Quotes which was saved without booking for specified organization by specified user or his
     * subordinate users when customer is specified. If customer is not specified then returns list of saved
     * quotes for all the organizations that the user has been assigned to and list of quotes with no organization.
     * 
     *
     * @param organizationId
     *            to filter only loads saved by current organization users
     * @param toDate - used to filter loads based on date range
     * @param fromDate - used to filter loads based on date range
     * @return list of saved quotes
     *
     * @throws ApplicationException if anything will happen.
     */
    List<SavedQuoteBO> findSavedQuotes(Long organizationId, Date fromDate, Date toDate) throws ApplicationException;

    /**
     * Gets saved quote.
     *
     * @param quoteId {@link com.pls.shipment.domain.SavedQuoteEntity#getId()}
     * @return {@link com.pls.shipment.domain.SavedQuoteEntity}
     */
    SavedQuoteEntity getSavedQuoteById(Long quoteId);

    /**
     * Saves quote.
     * 
     * @param savedQuote
     *            quote to save
     * @param organizationId
     *            to bind quote with organization
     * @param proposal
     *            to save cost details
     * @return saved quote
     */
    SavedQuoteEntity saveQuote(SavedQuoteEntity savedQuote, Long organizationId, ShipmentProposalBO proposal);

    /**
     * Deletes quote logically.
     *
     * @param quoteId quote ID to delete
     */
    void deleteSavedQuote(Long quoteId);

    /**
     * Gets saved quote pricing details.
     *
     * @param quoteId {@link com.pls.shipment.domain.SavedQuoteEntity#getId()}
     * @return {@link com.pls.shipment.domain.SavedQuotePricDtlsEntity}
     */
    SavedQuotePricDtlsEntity getSavedQuotePricDtls(Long quoteId);

    /**
     * Returns list of load id's associated with the saved quote.
     *
     * @param quoteId
     *            {@link com.pls.shipment.domain.SavedQuoteEntity#getId()}
     * @return list of load id's.
     */
    List<Long> getListOfLoadIds(Long quoteId);
}
