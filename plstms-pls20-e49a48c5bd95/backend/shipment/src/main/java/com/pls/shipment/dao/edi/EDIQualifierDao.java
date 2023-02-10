package com.pls.shipment.dao.edi;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.edi.EDIQualifierEntity;

import java.util.List;

/**
 * DAO for {@link EDIQualifierEntity}.
 *
 * @author Mikhail Boldinov, 29/08/13
 */
public interface EDIQualifierDao extends AbstractDao<EDIQualifierEntity, Long> {

    /**
     * Retrieves all EDI qualifiers for specified carrier and transaction set ID.
     *
     * @param carrierId        carrier ID
     * @param transactionSetId transaction set ID
     * @return list of {@link EDIQualifierEntity}
     */
    List<EDIQualifierEntity> getQualifiersForCarrier(Long carrierId, String transactionSetId);
}
