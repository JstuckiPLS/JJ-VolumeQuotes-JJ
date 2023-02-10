package com.pls.shipment.domain.edi;

import org.apache.commons.lang3.StringUtils;

/**
 * Class to store parsed EDI data.
 *
 * @author Mikhail Boldinov, 24/07/14
 */
public class EDIData extends EDIStructure<EDIFunctionalGroup> {

    /**
     * Constructor.
     *
     * @param ediFileName parsed EDI file name
     */
    public EDIData(String ediFileName) {
        super(ediFileName);
    }

    /**
     * Adds {@link EDIElement} into EDI Data object.
     *
     * @param element        {@link EDIElement}
     * @param groupId        EDI functional group id
     * @param groupCode      EDI functional group code
     * @param transactionId  EDI transaction id
     * @param segmentId      EDI segment id
     * @param parentLoopName name of parent loop
     * @param segmentLineNum line number of elements segment within a transaction
     */
    public void addElement(EDIElement element, String groupId, String groupCode, String transactionId, String segmentId, String parentLoopName,
                           int segmentLineNum) {
        if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(transactionId)) {
            return;
        }

        EDIFunctionalGroup group = get(groupId);
        group.setCode(groupCode);
        EDITransaction transaction = group.get(transactionId);
        EDISegment segment = transaction.get(StringUtils.join(segmentId, EDISegment.PARENT_LOOP_DELIMITER, String.valueOf(segmentLineNum)));
        segment.setParentLoop(parentLoopName);
        if (!element.isValid()) {
            segment.addError(element);
            transaction.setRejected(true);
        }
    }

    @Override
    public EDIFunctionalGroup create(String id) {
        return new EDIFunctionalGroup(id);
    }

    /**
     * EDI file name getter.
     *
     * @return name of parsed EDI file
     */
    public String getEdiFileName() {
        return getId();
    }

    @Override
    public String toString() {
        return getEdiFileName();
    }
}
