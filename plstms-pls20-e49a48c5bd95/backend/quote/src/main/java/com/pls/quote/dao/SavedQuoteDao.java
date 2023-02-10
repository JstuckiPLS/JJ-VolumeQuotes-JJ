package com.pls.quote.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * DAO for {@link SavedQuoteEntity}.
 *
 * @author Mikhail Boldinov, 20/03/13
 */
public interface SavedQuoteDao extends AbstractDao<SavedQuoteEntity, Long> {
    /**
     * Change status for saved quote with specified Id.
     *
     * @param quoteId id of saved quote which status should be changed.
     * @param status  new status
     */
    void updateStatus(Long quoteId, Status status);

    /**
     * Returns list of Quotes saved before booking for the assigned customers of user and quotes created by
     * user when no customer specified and list of Quotes saved before booking for the specified customer when
     * customer is specified.
     * <p/>
     * 
     * @param customerId
     *            - customer id
     * 
     * @param personId
     *            - current user ID
     * @param toDate
     *            - to date
     * @param fromDate
     *            - from date
     * @return list of saved quotes.
     *
     * @throws ApplicationException
     *             if anything will happen.
     */
    List<SavedQuoteBO> findSavedQuotes(Long customerId, Long personId, Date fromDate, Date toDate);

    /**
     * Returns list of load id's associated with the saved quote.
     *
     * @param quoteId
     *            id of the saved quote.
     * @return list of load id's.
     */
    List<Long> getListOfLoadIds(Long quoteId);

}
