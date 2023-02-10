package com.pls.shipment.service.impl.edi.parser;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentDateShortStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentDateStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentTimeStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toDateTimeStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pb.x12.Context;
import org.pb.x12.Loop;
import org.pb.x12.Segment;
import org.pb.x12.X12;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.impl.edi.EDISequencesFetcher;
import com.pls.shipment.domain.edi.EDIData;
import com.pls.shipment.domain.edi.EDIElement;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIFunctionalGroup;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.edi.EDISegment;
import com.pls.shipment.domain.edi.EDITransaction;
import com.pls.shipment.domain.enums.EDITransactionSet;

/**
 * EDI Producer for X12 997 - Motor Carrier Load Tender.
 * 
 * @param <T>
 *            class for EDIParseResult<T>
 * 
 * @author Alexander Nalapko
 */
@Service
@Transactional
public class EDI997Producer<T> {

    private static final char SEGMENT_SEPARATOR = '~';
    private static final char ELEMENT_SEPARATOR = '*';

    private static final char COMPOSITE_ELEMENT_SEPARATOR = ':'; // ??????????
    private static final String DEFAULT_SENDER_ID = "PTLC"; // !!!!!???

    private static final String FUNCTIONAL_GROUP_RESPONSE_HEADER = "AK1";
    private static final String TRANSACTION_SET_RESPONSE_HEADER = "AK2";
    private static final String DATA_SEGMENT_NOTE = "AK3";
    private static final String DATA_ELEMENT_NOTE = "AK4";
    private static final String TRANSACTION_SET_RESPONSE_TRAILER = "AK5";
    private static final String FUNCTIONAL_GROUP_RESPONSE_TRAILER = "AK9";

    @Autowired
    private EDISequencesFetcher ediSequencesFetcher;

    @Value("${mode.production}")
    private Boolean productionMode;

    /**
     * For edi214/210.
     * 
     * @param ediParseResult
     *            EDIParseResult<T>
     * @return EDIFile
     * @throws IOException
     *             IOException
     */
    public EDIFile create(EDIParseResult<T> ediParseResult) throws IOException {
        String scac = ediParseResult.getEdiFile().getCarrierScac();
        EDIData ediData = ediParseResult.getEdiData();
        EDITransactionSet ediTransactionSet = ediParseResult.getTransactionSet();
        Context context = new Context(SEGMENT_SEPARATOR, ELEMENT_SEPARATOR, COMPOSITE_ELEMENT_SEPARATOR);
        X12 x12 = new X12(context);

        String currentTime = getCurrentTimeStr();
        String isaId = element(toStr(ediSequencesFetcher.getNextISA()), 9, "0", true);
        String gsId = element(toStr(ediSequencesFetcher.getNextGS()), 9, "0", true);
        String transactionSetId = element("1", 4, "0", true);
        String senderId = StringUtils.defaultIfBlank(ediParseResult.getReceiverId(), DEFAULT_SENDER_ID);

        Loop loopIsa = addInterchangeControlHeader(x12, scac, currentTime, isaId, isProductionMode(), senderId); // ISA
        Loop loopGs = addFunctionalGroupHeader(loopIsa, scac, currentTime, gsId, senderId); // GS
        Loop loopSt = addTransactionSetHeader(loopGs, transactionSetId); // ST

        int loopStChildSegmentsCount = 4; // ST,SE,A1,A9
        for (Entry<String, EDIFunctionalGroup> entryFunctionalGroup : ediData.getElements().entrySet()) {

            EDIFunctionalGroup functionalGroup = entryFunctionalGroup.getValue();
            Loop fg = addFunctionalGroupResponseHeader(loopSt, functionalGroup.getCode(), functionalGroup.getId()); // A1
            int acceptedTransaction = 0;
            for (Entry<String, EDITransaction> entryTransaction : functionalGroup.getElements().entrySet()) {
                EDITransaction transaction = entryTransaction.getValue();
                if (!transaction.isRejected()) {
                    acceptedTransaction++;
                }
                Loop ts = addTransactionSetResponseHeader(fg, ediTransactionSet.getId(), transaction.getId()); // AK2
                addTransactionData(transaction, ts);
                loopStChildSegmentsCount += ts.getLoops().size();
                addTransactionSetResponseTrailer(fg, transaction.isRejected() ? "R" : "A"); // AK5
            }
            addFunctionalGroupResponseTrailer(loopSt, getStatus(acceptedTransaction, functionalGroup.getElements().size()),
                    String.valueOf(functionalGroup.getElements().size()),
                    String.valueOf(functionalGroup.getElements().size()), String.valueOf(acceptedTransaction)); // A9
            loopStChildSegmentsCount += fg.getLoops().size();
        }
        addTransactionSetTrailer(loopGs, loopStChildSegmentsCount, transactionSetId); // SE
        addFunctionalGroupTrailer(loopIsa, gsId); // GE
        addInterchangeControlTrailer(x12, isaId); // IEA

        return getEDIFile(getFileName(ediParseResult.getEdiFile().getName()), scac, x12);
    }

    private void addTransactionData(EDITransaction transaction, Loop transactionSet) {
        for (Entry<String, EDISegment> transactionSegmentEntry : transaction.getElements().entrySet()) {
            EDISegment segment = transactionSegmentEntry.getValue();
            for (EDIElement element : segment.getErrors()) {
                addDataSegmentNote(transactionSet, segment.getId(), segment.getLineNum(), segment.getParentLoop(), ""); // AK3
                addDataElementNote(transactionSet, String.valueOf(element.getIndex()), String.valueOf(element.getDictionaryId()),
                        String.valueOf(element.getErrorCode().getId()), String.valueOf(element.getId())); // AK4
            }
        }
    }

    private String getStatus(int acceptedTransactions, int transactions) {
        if (acceptedTransactions == transactions) {
            return "A";
        } else if (acceptedTransactions == 0) {
                return "R";
        }
        return "P";
    }

    private String getFileName(String loadIdentifier) {
        return String.format("EDI997_%s_%s.txt", loadIdentifier.replaceAll(".txt", ""), toDateTimeStr(new Date()));
    }

    private EDIFile getEDIFile(String fileName, String scac, X12 x12) throws IOException {
        EDIFile ediFile = new EDIFile();
        ediFile.setTransactionSet(EDITransactionSet._997);
        ediFile.setName(fileName);
        ediFile.setCarrierScac(scac);

        File tempFile = File.createTempFile("ediFileX12", "tmp");
        tempFile.deleteOnExit();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            IOUtils.write(x12.toString(), outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        ediFile.setFile(tempFile);
        return ediFile;
    }

    /**
     * Segment: AK1 Functional Group Response Header.
     * 
     * @param code
     *            Functional Identifier Code. Code identifying a group of application related transaction
     *            sets.
     * 
     * @param number
     *            Group Control Number. Assigned number originated and maintained by the sender.
     */
    private Loop addFunctionalGroupResponseHeader(Loop loop, String code, String number) {
        return addSegment(loop, FUNCTIONAL_GROUP_RESPONSE_HEADER, code, number);
    }

    /**
     * Segment: AK2 Transaction Set Response Header.
     * 
     * @param code
     *            Transaction Set Identifier Code. Code uniquely identifying a Transaction Set
     * @param number
     *            Transaction Set Control Number. Identifying control number that must be unique within the
     *            transaction set functional group assigned by the originator for a transaction set.
     */
    private Loop addTransactionSetResponseHeader(Loop loop, String code, String number) {
        return addSegment(loop, TRANSACTION_SET_RESPONSE_HEADER, code, number);
    }

    /**
     * Segment: AK3 Data Segment Note.
     * 
     * @param segmentCode
     *            Segment ID Code. Code defining the segment ID of the data segment in error (See Appendix A -
     *            Number 77)
     * @param line
     *            Segment Position in Transaction Set. The numerical count position of this data segment from
     *            the start of the transaction set: the transaction set header is count position 1
     * @param loopCode
     *            Loop Identifier Code. The loop ID number given on the transaction set diagram is the value
     *            for this data element in segments LS and LE
     * @param errorCode
     *            Segment Syntax Error Code. Code indicating error found based on the syntax editing of a
     *            segment
     */
    private Loop addDataSegmentNote(Loop loop, String segmentCode, String line, String loopCode, String errorCode) {
        return addSegment(loop, DATA_SEGMENT_NOTE, segmentCode, line, loopCode, errorCode);
    }

    /**
     * Segment: AK4 Data Element Note.
     * 
     * @param position
     *            Position in Segment. Code indicating the relative position of a simple data element, or the
     *            relative position of a composite data. structure combined with the relative position of the
     *            component data element within the composite data structure, in error; the count starts with
     *            1 for the simple data element or composite data structure immediately following the segment
     *            ID
     * @param elementNumber
     *            Data Element Reference Number. Reference number used to locate the data element in the Data
     *            Element Dictionary.
     * @param errorCode
     *            Data Element Syntax Error Code. Code indicating the error found after syntax edits of a data
     *            element.
     * @param badElementCopy
     *            Copy of Bad Data Element. This is a copy of the data element in error.
     */
    private Loop addDataElementNote(Loop loop, String position, String elementNumber, String errorCode,
            String badElementCopy) {
        return addSegment(loop, DATA_ELEMENT_NOTE, position, elementNumber, errorCode, badElementCopy);
    }

    /**
     * Segment: AK5 Transaction Set Response Trailer
     * 
     * 
     * @param acknowledgmentCode
     *            Transaction Set Acknowledgment Code. Code indicating accept or reject condition based on the
     *            syntax editing of the transaction set.
     * @param syntaxErrorCode
     *            Transaction Set Syntax Error Code. Code indicating error found based on the syntax editing
     *            of a transaction set.
     */
    private Loop addTransactionSetResponseTrailer(Loop loop, String acknowledgmentCode, String... syntaxErrorCode) {
        ArrayList<String> params = new ArrayList<String>();
        params.add(acknowledgmentCode);
        params.addAll(Arrays.asList(syntaxErrorCode));
        String[] list = new String[params.size()];
        params.toArray(list);
        return addSegment(loop, TRANSACTION_SET_RESPONSE_TRAILER, list);
    }

    /**
     * Segment: AK9 Functional Group Response Trailer.
     * 
     * @param acknowledgmentCode
     *            Functional Group Acknowledge Code. Code indicating accept or reject condition based on the
     *            syntax editing of the functional group.
     * @param includedSetNumber
     *            Number of Transaction Sets Included . Total number of transaction sets included in the
     *            functional group or interchange (transmission) group terminated by the trailer containing
     *            this data element.
     * @param receivedSetNumber
     *            Number of Received Transaction Sets. Number of Transaction Sets received.
     * @param acceptedSetNumber
     *            Number of Accepted Transaction Sets. Number of accepted Transaction Sets in a Functional
     *            Group.
     * @param syntaxErrorCode
     *            Functional Group Syntax Error Code. Code indicating error found based on the syntax editing
     *            of the functional group header and/or trailer.
     */
    private Loop addFunctionalGroupResponseTrailer(Loop loop, String acknowledgmentCode, String includedSetNumber,
            String receivedSetNumber, String acceptedSetNumber, String... syntaxErrorCode) {
        ArrayList<String> params = new ArrayList<String>();
        params.add(acknowledgmentCode);
        params.add(includedSetNumber);
        params.add(receivedSetNumber);
        params.add(acceptedSetNumber);
        params.addAll(Arrays.asList(syntaxErrorCode));
        String[] list = new String[params.size()];
        params.toArray(list);
        return addSegment(loop, FUNCTIONAL_GROUP_RESPONSE_TRAILER, list);
    }

    /*
     * ISA
     */
    private Loop addInterchangeControlHeader(Loop loop, String scac, String currentTime, String isaId, boolean productionMode, String senderId) {
        return addSegment(loop, "ISA", "00", element(10), "00", element(10), "ZZ", element(senderId, 15), "02",
                element(scac, 15), element(getCurrentDateShortStr(), 6), element(currentTime, 4), "U", "00401", isaId, "0",
                productionMode ? "P" : "T", "^");
    }

    /*
     * GS
     */
    private Loop addFunctionalGroupHeader(Loop loop, String scac, String currentTime, String gsId, String senderId) {
        return addSegment(loop, "GS", "FA", element(senderId, 2, 15), element(scac, 2, 15), element(getCurrentDateStr(), 8),
                element(currentTime, 4, 8), gsId, "X", "004010");
    }

    /*
     * ST
     */
    private Loop addTransactionSetHeader(Loop loop, String transactionSetId) {
        return addSegment(loop, "ST", EDITransactionSet._997.getId(), transactionSetId);
    }

    /*
     * SE
     */
    private Loop addTransactionSetTrailer(Loop loop, int loopStChildSegmentsCount, String transactionSetId) {
        return addSegment(loop, "SE", element(toStr(loopStChildSegmentsCount), 1, 10), transactionSetId);
    }

    /*
     * GE
     */
    private Loop addFunctionalGroupTrailer(Loop loop, String gsId) {
        return addSegment(loop, "GE", "1", gsId);
    }

    /*
     * IEA
     */
    private Loop addInterchangeControlTrailer(Loop loop, String isaId) {
        return addSegment(loop, "IEA", "1", isaId);
    }

    private boolean isProductionMode() {
        return productionMode != null && productionMode;
    }

    /**
     * Adds a X12 segment to the specified {@link Loop} and populates it with provided elements.
     *
     * @param parentLoop
     *            {@link Loop} to add segment to
     * @param segmentName
     *            segment name
     * @param elements
     *            segment elements
     * @return {@link Loop} with added segment
     */
    private Loop addSegment(Loop parentLoop, String segmentName, String... elements) {
        Loop loop = parentLoop.addChild(segmentName);
        Segment segment = loop.addSegment(segmentName);
        for (String value : elements) {
            if (value != null) {
                segment.addElement(value);
            } else {
                segment.addElement("");
            }
        }
        return loop;
    }
}
