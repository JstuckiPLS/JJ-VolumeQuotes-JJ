package com.pls.shipment.domain.edi;

/**
 * EDI functional group.
 * <p/>
 * EDI logical structure placed within "GS" and "GE" segments, which contains set of EDI transactions.
 *
 * @author Mikhail Boldinov, 25/07/14
 */
public class EDIFunctionalGroup extends EDIStructure<EDITransaction> {

    private String code;

    /**
     * Constructor.
     *
     * @param id EDI functional group id
     */
    protected EDIFunctionalGroup(String id) {
        super(id);
    }

    @Override
    public EDITransaction create(String id) {
        return new EDITransaction(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("'%s' : '%s'", getId(), code);
    }
}
