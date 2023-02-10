package com.pls.shipment.domain.edi;

/**
 * EDI transaction.
 * <p/>
 * EDI logical structure placed within "ST" and "SE" segments, which contains set of EDI segments.
 *
 * @author Mikhail Boldinov, 25/07/14
 */
public class EDITransaction extends EDIStructure<EDISegment> {

    private boolean rejected;

    /**
     * Constructor.
     *
     * @param id EDI transaction id
     */
    public EDITransaction(String id) {
        super(id);
    }

    @Override
    public EDISegment create(String id) {
        return new EDISegment(id);
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public String toString() {
        return String.format("'%s' : %s", getId(), rejected ? "REJECTED" : "ACCEPTED");
    }
}
