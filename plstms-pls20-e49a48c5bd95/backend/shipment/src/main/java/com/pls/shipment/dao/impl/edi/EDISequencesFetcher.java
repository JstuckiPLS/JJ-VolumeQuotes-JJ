package com.pls.shipment.dao.impl.edi;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sequence fetcher for EDI identifiers.
 *
 * @author Mikhail Boldinov, 23/10/13
 */
@Repository
@Transactional
public class EDISequencesFetcher {

    private static final String ISA_NUM_SEQUENCE_NAME = "ISA_NUM_SEQ";
    private static final String GS_NUM_SEQUENCE_NAME = "GS_NUM_SEQ";

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Fetches EDI ISA identifier.
     *
     * @return ISA identifier
     */
    public Long getNextISA() {
        return getNext(ISA_NUM_SEQUENCE_NAME);
    }

    /**
     * Fetches EDI GS identifier.
     *
     * @return GS identifier
     */
    public Long getNextGS() {
        return getNext(GS_NUM_SEQUENCE_NAME);
    }

    private Long getNext(String sequenceName) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getSequenceNextValSQLQuery(sequenceName))
                .addScalar("num", StandardBasicTypes.LONG);
        return (Long) query.uniqueResult();
    }

    private String getSequenceNextValSQLQuery(String sequenceName) {
        return String.format("SELECT nextval('flatbed.%s') AS num", sequenceName);
    }
}
