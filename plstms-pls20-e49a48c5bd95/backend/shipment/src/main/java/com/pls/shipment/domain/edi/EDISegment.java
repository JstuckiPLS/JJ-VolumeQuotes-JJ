package com.pls.shipment.domain.edi;

import java.util.ArrayList;
import java.util.List;

/**
 * EDI segment.
 * <p/>
 * EDI logical structure which represents a single line of EDI file.
 *
 * @author Mikhail Boldinov, 27/07/14
 */
public class EDISegment extends EDIStructure<EDIElement> {

    public static final String PARENT_LOOP_DELIMITER = "->";

    private String parentLoop;

    private String lineNum;

    private List<EDIElement> errors = new ArrayList<EDIElement>();

    /**
     * Constructor.
     *
     * @param id segment id
     */
    protected EDISegment(String id) {
        super(id.split(PARENT_LOOP_DELIMITER)[0]);
        this.lineNum = id.split(PARENT_LOOP_DELIMITER)[1];
    }

    @Override
    public EDIElement create(String id) {
        return new EDIElement(id);
    }

    /**
     * Collects information about elements which were parsed with errors.
     *
     * @param element {@link EDIElement}
     */
    public void addError(EDIElement element) {
        errors.add(element);
    }

    public List<EDIElement> getErrors() {
        return errors;
    }

    public String getParentLoop() {
        return parentLoop;
    }

    public void setParentLoop(String parentLoop) {
        this.parentLoop = parentLoop;
    }

    public String getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return String.format("'%s'%s'%s' - errors: %s", parentLoop, PARENT_LOOP_DELIMITER, getId(), errors.size());
    }
}
