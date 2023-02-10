package com.pls.shipment.domain.edi;

import com.pls.shipment.domain.enums.EDITransactionSet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * EDI JMS message.
 *
 * @author Mikhail Boldinov, 12/03/14
 */
public class EDIMessage implements Serializable {

    private static final long serialVersionUID = 3454948989473979381L;

    private Long carrierId;
    private List<Long> entityIds;
    private EDITransactionSet transactionSet;
    private Map<String, Object> params;

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public EDITransactionSet getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(EDITransactionSet transactionSet) {
        this.transactionSet = transactionSet;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
