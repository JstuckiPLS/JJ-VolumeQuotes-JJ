package com.pls.shipment.service.impl.edi.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pb.x12.Cf;
import org.pb.x12.Context;
import org.pb.x12.FormatException;
import org.pb.x12.Loop;
import org.pb.x12.Segment;
import org.pb.x12.X12;
import org.pb.x12.X12Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EDIValidationException;
import com.pls.shipment.domain.edi.EDIData;
import com.pls.shipment.domain.edi.EDIElement;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDIElementErrorCode;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.edi.parser.EDIParser;
import com.pls.shipment.service.edi.parser.EDIQualifiersPlsReference;
import com.pls.shipment.service.impl.edi.parser.enums.EDIBaseElement;
import com.pls.shipment.service.impl.edi.utils.EDIValidationUtils;

/**
 * Abstract EDI parser.
 *
 * @param <T>
 *            type of EDI parser
 * @author Mikhail Boldinov, 28/08/13
 */
public abstract class AbstractEDIParser<T> implements EDIParser<T> {

    public static final String INTERCHANGE_CONTROL_HEADER = "Interchange Control Header";
    public static final String INTERCHANGE_CONTROL_TRAILER = "Interchange Control Trailer";
    public static final String FUNCTIONAL_GROUP_HEADER = "Functional Group Header";
    public static final String FUNCTIONAL_GROUP_TRAILER = "Functional Group Trailer";
    public static final String TRANSACTION_SET_HEADER = "Transaction Set Header";
    public static final String TRANSACTION_SET_TRAILER = "Transaction Set Trailer";

    public static final String ILLEGAL_ELEMENT_CHARACTERS = "[*~\r\n]";
    public static final String QUALIFIER_SEPARATOR = ",";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String ediFileName;
    private Cf config;
    private CarrierEntity carrier;
    private Map<String, String> qualifiers;
    private DataProvider dataProvider;
    private EDIData data;
    private Context context;

    /**
     * Constructor.
     *
     * @param carrier
     *            EDI carrier
     * @param dataProvider
     *            data provider for EDI parser
     */
    public AbstractEDIParser(CarrierEntity carrier, DataProvider dataProvider) {
        this.carrier = carrier;
        this.dataProvider = dataProvider;
        if (this.dataProvider != null) {
            this.qualifiers = this.dataProvider.getQualifiers();
        }
        loadCf();
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    private void loadCf() {
        config = new Cf("");
        Cf edi = config.addChild(INTERCHANGE_CONTROL_HEADER, "ISA");
        config.addChild(INTERCHANGE_CONTROL_TRAILER, "IEA");

        Cf group = edi.addChild(FUNCTIONAL_GROUP_HEADER, "GS");
        edi.addChild(FUNCTIONAL_GROUP_TRAILER, "GE");

        Cf transactionConfig = getTransactionConfig();
        if (transactionConfig != null) {
            group.addChild(transactionConfig);
        }
        group.addChild(TRANSACTION_SET_TRAILER, "SE");
    }

    /**
     * Gets transaction configurable element from appropriate parser implementation.
     *
     * @return {@link Cf}
     */
    protected abstract Cf getTransactionConfig();

    /**
     * EDI {@link X12} element getter.
     *
     * @param file
     *            EDI file
     * @return EDI X12 object.
     * @throws IOException
     *             thrown if any I/O errors occur while reading the file
     * @throws FormatException
     *             thrown if EDI file is not correctly formatted
     * @throws EDIValidationException
     *             thrown if EDI file validation is not passed
     */
    protected X12 getX12(EDIFile file) throws IOException, FormatException, EDIValidationException {
        this.ediFileName = file.getName();
        this.data = new EDIData(file.getName());
        InputStream fileContent = null;
        X12 x12 = null;
        try {
            fileContent = file.getNewFileContent();
            x12 = (X12) new X12Parser(config).parse(fileContent);
            context = x12.getContext();
            validate(x12, file.getTransactionSet());
        } finally {
            IOUtils.closeQuietly(fileContent);
        }

        return x12;
    }

    /**
     * Get Receiver ID from ISA segment.
     * 
     * @param file
     *            EDI File
     * @return Receiver ID
     * @throws FormatException
     *             exception
     * @throws IOException
     *             exception
     * @throws EDIValidationException
     *             exception
     */
    protected String getReceiverId(EDIFile file) throws EDIValidationException, IOException, FormatException {
        String receiverId = null;
        Segment segment = getX12(file).findSegment("ISA").get(0);
        if (segment != null && segment.size() > 8) {
            receiverId = segment.getElement(8);
        }
        return StringUtils.trimToNull(receiverId);
    }

    /**
     * Creates new {@link EDIFile}.
     * 
     * @param fileName
     *            created EDI file name
     * @param scac
     *            carrier SCAC
     * @param x12
     *            {@link X12} element
     * @return created EDI file
     * @throws IOException
     *             thrown if any I/O errors occur while writing the file
     */
    protected EDIFile getEDIFile(String fileName, String scac, X12 x12) throws IOException {
        this.ediFileName = fileName;
        EDIFile ediFile = new EDIFile();
        ediFile.setTransactionSet(EDITransactionSet._204);
        ediFile.setName(ediFileName);
        ediFile.setCarrierScac(scac);
        ediFile.setGs(this.dataProvider.getGs());
        ediFile.setIsa(this.dataProvider.getIsa());
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
     * Gets qualifier for Carrier by {@link EDIQualifiersPlsReference}. If no specific qualifiers are set for
     * customer returns default qualifier.
     *
     * @param qualifiersPlsReference
     *            {@link EDIQualifiersPlsReference}
     * @return qualifier used by carrier
     */
    protected String getQualifier(EDIQualifiersPlsReference qualifiersPlsReference) {
        if (qualifiers.get(qualifiersPlsReference.getName()) != null) {
            return qualifiers.get(qualifiersPlsReference.getName());
        } else {
            return qualifiersPlsReference.getDefaultValue();
        }
    }

    /**
     * Gets qualifiers for Carrier by {@link EDIQualifiersPlsReference}. If no specific qualifiers are set for
     * customer returns default qualifiers.
     *
     * @param qualifiersPlsReference
     *            {@link EDIQualifiersPlsReference}
     * @return qualifiers used by carrier
     */
    protected String getQualifier(EDIQualifiersPlsReference... qualifiersPlsReference) {
        StringBuilder qualifiers = new StringBuilder();
        for (int i = 0; i < qualifiersPlsReference.length; i++) {
            qualifiers.append(getQualifier(qualifiersPlsReference[i]));
            if (i < qualifiersPlsReference.length) {
                qualifiers.append(QUALIFIER_SEPARATOR);
            }
        }
        return qualifiers.toString();
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    @Override
    public EDIFile getEDIFileWithoutSelectedEntities(EDIFile file, List<Integer> resultIndexes)
            throws EDIValidationException, IOException, FormatException {
        X12 x12 = getX12(file);
        Loop isaLoop = x12.getLoop(0);
        Loop gsLoop = isaLoop.getLoops().get(0);

        List<Integer> transactions = getIndexesOfTransactionByResultIndexes(gsLoop, resultIndexes);

        Collections.sort(transactions, Collections.reverseOrder());
        for (int transaction : transactions) {
            gsLoop.removeLoop(transaction * 2);
            gsLoop.removeLoop(transaction * 2); // need to remove loop second time because gsLoop contains
                                                // both ST (transaction start) and SE (transaction end) loops
        }

        return getTempFile(x12, file);
    }

    /**
     * Index of transaction can be different from index of parsed entity, because one transaction can contain
     * more than one parsed entity. But parsed entities are sorted the same way as transactions.
     * 
     * @param gsLoop
     *            GS segment loop
     * @param resultIndexes
     *            list of indexes for parsed entities
     * @return indexes of transaction to which parsed entities belong
     */
    protected List<Integer> getIndexesOfTransactionByResultIndexes(Loop gsLoop, List<Integer> resultIndexes) {
        return resultIndexes;
    }

    private EDIFile getTempFile(X12 x12, EDIFile originFile) throws IOException {
        StringBuilder baseName = new StringBuilder(FilenameUtils.getBaseName(originFile.getFile().getName()))
                .append(UUID.randomUUID());
        StringBuilder extension = new StringBuilder(FilenameUtils.getExtension(originFile.getFile().getName()));
        if (!StringUtils.isEmpty(extension)) {
            extension.insert(0, '.');
        }
        File resultFile = File.createTempFile(baseName.toString(), extension.toString());
        resultFile.deleteOnExit();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(resultFile);
            IOUtils.write(x12.toString(), outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        EDIFile ediFile = new EDIFile();
        ediFile.setFile(resultFile);
        ediFile.setCarrierScac(originFile.getCarrierScac());
        ediFile.setTransactionSet(originFile.getTransactionSet());
        return ediFile;
    }

    /**
     * Gets value of first found {@link EDIParseableElement} in provided source {@link Loop}.
     *
     * @param loop
     *            X-12 source loop
     * @param element
     *            {@link com.pls.shipment.service.edi.parser.EDIParseableElement} element
     * @return {@link com.pls.shipment.domain.edi.EDIElement}
     */
    protected EDIElement getElementValue(Loop loop, EDIParseableElement element) {
        return getElementValue(loop, element, 0);
    }

    /**
     * Gets value of {@link EDIParseableElement} specified by index in provided source {@link Loop}.
     *
     * @param loop
     *            X-12 source loop
     * @param element
     *            {@link com.pls.shipment.service.edi.parser.EDIParseableElement} element
     * @param loopIndex
     *            loop index
     * @return {@link EDIElement}
     */
    protected EDIElement getElementValue(Loop loop, EDIParseableElement element, int loopIndex) {
        EDIElement ediElement = new EDIElement(element.toString());
        ediElement.setDictionaryId(element.getElementId());
        ediElement.setIndex(element.getIndex());

        Segment segment = findSegment(loop, element.getConfigName(), loopIndex);
        if (segment != null) {
            try {
                ediElement.setValue(segment.getElement(element.getIndex()));
                if (element.isMandatory() && ediElement.getValue().isEmpty()) {
                    ediElement.setErrorCode(EDIElementErrorCode.MANDATORY_MISSING);
                }
            } catch (IndexOutOfBoundsException e) {
                String msg = String
                        .format("Unable to get value '%s'. EDI: '%s', transaction ID: '%s', segment: '%s', element: '%s'. Caused by: %s",
                                element.getPlsValue(), ediFileName, getTransactionId(loop), element.getSegment(),
                                element.toString(), e);
                if (element.isMandatory()) {
                    ediElement.setErrorCode(EDIElementErrorCode.INVALID_CHARACTER);
                    ediElement.setErrorMsg(msg);
                }
                logger.warn(msg);
            }
        } else {
            if (element.isMandatory()) {
                ediElement.setErrorCode(EDIElementErrorCode.MANDATORY_MISSING);
            }
        }
        String loopName = loop.getSegments().isEmpty() ? loop.getName() : loop.getSegment(0).getElement(0);
        data.addElement(ediElement, getGroupId(loop), getGroupCode(loop), getTransactionId(loop), element.getSegment(),
                loopName, getSegmentLineNumber(getTransaction(loop), segment));
        return ediElement;
    }

    /**
     * Retrieves the X-12 {@link Segment} from provided {@link Loop} by specified child loop name and segments
     * index.
     *
     * @param loop
     *            X-12 source loop
     * @param segmentName
     *            segment name to find
     * @param loopIndex
     *            loop index
     * @return found {@link Segment}
     */
    protected Segment findSegment(Loop loop, String segmentName, int loopIndex) {
        if (loop.hasLoop(segmentName)) {
            try {
                return loop.findLoop(segmentName).get(loopIndex).getSegment(0);
            } catch (IndexOutOfBoundsException e) {
                logger.warn(String.format(
                        "Unable to get segment. Index out of bounds. Transaction ID: '%s', loop: '%s'. %s",
                        getTransactionId(loop), segmentName, e.getMessage()));
            }
        }
        return null;
    }

    /**
     * Adds a X12 segment to the specified {@link Loop} and populates it with provided elements if condition
     * is <code>true</code>.
     *
     * @param condition
     *            condition which indicates whether segment should be created or not
     * @param parentLoop
     *            {@link Loop} to add segment to
     * @param segmentName
     *            segment name
     * @param elements
     *            segment elements
     * @return {@link Loop} with added segment if condition is <code>true</code>, otherwise <code>null</code>
     */
    protected Loop addSegment(boolean condition, Loop parentLoop, String segmentName, String... elements) {
        if (condition) {
            return addSegment(parentLoop, segmentName, elements);
        }
        return null;
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
    protected Loop addSegment(Loop parentLoop, String segmentName, String... elements) {
        Loop loop = parentLoop.addChild(segmentName);
        Segment segment = loop.addSegment(segmentName);
        for (String value : elements) {
            if (value != null) {
                segment.addElement(value.replaceAll(ILLEGAL_ELEMENT_CHARACTERS, ""));
            } else {
                segment.addElement("");
            }
        }
        return loop;
    }

    /**
     * Retrieves the X-12 {@link Loop}'s count in provided {@link Loop}.
     *
     * @param loop
     *            X-12 source loop
     * @param loopName
     *            loop name to find
     * @return count of segment with given name
     */
    protected int getLoopsCount(Loop loop, String loopName) {
        return loop.findLoop(loopName).size();
    }

    /**
     * {@link EDIData} getter.
     *
     * @return EDI data
     */
    protected EDIData getEdiData() {
        return data;
    }

    private void validate(X12 x12, EDITransactionSet transactionSet) throws EDIValidationException {
        EDIValidationUtils.validateLoopsCount(x12, INTERCHANGE_CONTROL_HEADER, "ISA", 1);
        Loop isaLoop = x12.findLoop(INTERCHANGE_CONTROL_HEADER).get(0);
        EDIValidationUtils.validateLoopsExist(isaLoop, FUNCTIONAL_GROUP_HEADER, "GS");

        String scac = getCarrier().getScac();
        EDIValidationUtils.validateScac(scac, getElementValue(x12, EDIBaseElement.ISA_SCAC).getValue().trim(),
                getElementValue(isaLoop, EDIBaseElement.GS_SCAC).getValue());

        List<Loop> gsLoops = isaLoop.findLoop(FUNCTIONAL_GROUP_HEADER);
        for (Loop gsLoop : gsLoops) {
            EDIValidationUtils.validateLoopsExist(gsLoop, TRANSACTION_SET_HEADER, "ST");
            EDIValidationUtils.validateTransactionSet(transactionSet,
                    getElementValue(gsLoop, EDIBaseElement.ST_TRANSACTION_SET_ID).getValue());
        }
    }

    private String getTransactionId(Loop loop) {
        String transactionId = "";
        Segment segment = getTransactionSegment(loop);
        if (segment != null && segment.size() > 2) {
            transactionId = segment.getElement(2);
        }
        return transactionId;
    }

    private String getGroupId(Loop loop) {
        String groupId = "";
        Segment segment = getGroupSegment(loop);
        if (segment != null && segment.size() > 6) {
            groupId = segment.getElement(6);
        }
        return groupId;
    }

    private String getGroupCode(Loop loop) {
        String groupFId = "";
        Segment segment = getGroupSegment(loop);
        if (segment != null && segment.size() > 1) {
            groupFId = segment.getElement(1);
        }
        return groupFId;
    }

    private Segment getTransactionSegment(Loop loop) {
        return getParentSegment(loop, "ST");
    }

    private Segment getGroupSegment(Loop loop) {
        return getParentSegment(loop, "GS");
    }

    private Segment getParentSegment(Loop loop, String segmentName) {
        if (loop.getParent() != null && !loop.getParent().getSegments().isEmpty()) {
            Segment segment = loop.getSegment(0);
            if (!segment.getElements().isEmpty()) {
                if (segmentName.equals(segment.getElement(0))) {
                    return segment;
                } else {
                    return getParentSegment(loop.getParent(), segmentName);
                }
            }
        }
        return null;
    }

    private Loop getTransaction(Loop loop) {
        if (!loop.getSegments().isEmpty()) {
            if ("ST".equals(loop.getSegment(0).getElement(0))) {
                return loop;
            } else if (loop.getParent() != null) {
                return getTransaction(loop.getParent());
            } else {
                return null;
            }
        }
        return null;
    }

    private int getSegmentLineNumber(Loop transaction, Segment segment) {
        if (transaction == null || segment == null) {
            return 0;
        }
        String transactionData = transaction.toString();
        String segmentData = segment.toString();
        String segmentSeparator = String.valueOf(context.getSegmentSeparator());
        int segmentOffset = transactionData.indexOf(segmentData);
        return StringUtils.countMatches(transactionData.substring(0, segmentOffset), segmentSeparator);
    }

    /**
     * Data provider interface for EDI parser.
     */
    public interface DataProvider {

        /**
         * Gets next ID value for ISA segment from DB sequence.
         *
         * @return ISA id
         */
        Long getNextIsaId();

        /**
         * Gets next ID value for GS segment from DB sequence.
         *
         * @return GS id
         */
        Long getNextGSId();

        /**
         * Gets all EDI qualifiers for Carrier.
         *
         * @return carrier EDI qualifiers Map
         */
        Map<String, String> getQualifiers();

        /**
         * Gets EDI admin user ID.
         *
         * @return user id
         */
        Long getEdiUserId();

        /**
         * Gets lists of Tracking Status.
         * 
         *
         * @return Map of code, description
         */
        Map<String, String> getLoadTrackingStatusTypes();

        /**
         * Gets lists of Reason Tracking Status.
         * 
         *
         * @return Map of code, description
         */
        Map<String, String> getLoadReasonTrackingStatusTypes();

        /**
         * Gets ID value for GS segment from DB sequence.
         *
         * @return GS id
         */
        Long getGs();

        /**
         * Gets ID value for ISA segment from DB sequence.
         *
         * @return ISA id
         */
        Long getIsa();
    }
}
