package com.pls.shipment.service.edi.parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.pb.x12.FormatException;

import com.pls.core.exception.EDIValidationException;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;

/**
 * EDI parser interface.
 *
 * @param <T> type of EDI parser
 * @author Mikhail Boldinov, 30/08/13
 */
public interface EDIParser<T> {

    /**
     * Parses EDI file.
     *
     * @param file EDI file to parse
     * @return {@link EDIParseResult}
     * @throws IOException            thrown if any I/O errors occur while reading the file
     * @throws FormatException        thrown if EDI file is not correctly formatted
     * @throws EDIValidationException thrown if EDI file validation is not passed
     */
    EDIParseResult<T> parse(EDIFile file) throws IOException, FormatException, EDIValidationException;

    /**
     * Creates {@link EDIFile} from the data retrieved from provided entities.
     *
     * @param entities entities to generate EDI file from
     * @param params   EDI custom parameters map
     * @return generated {@link EDIFile}
     * @throws IOException if io processing of EDI data fails
     */
    EDIFile create(List<T> entities, Map<String, Object> params) throws IOException;

    /**
     * Get copy of EDI File without specified transactions.
     * 
     * @param file
     *            that contains original parsed file.
     * @param resultIndexes
     *            list of parsed entities indexes which transactions should not appear in resulting file.
     * @return a copy of EDI File without specified transactions.
     * @throws FormatException
     *             exception
     * @throws IOException
     *             exception
     * @throws EDIValidationException
     *             exception
     */
    EDIFile getEDIFileWithoutSelectedEntities(EDIFile file, List<Integer> resultIndexes) throws EDIValidationException, IOException,
            FormatException;
}
