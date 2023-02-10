package com.pls.shipment.service.edi.handler;

import java.util.List;

import com.pls.core.domain.Identifiable;
import com.pls.shipment.domain.edi.EDIParseResult;

/**
 * Interface for handling {@link EDIParseResult}.
 *
 * @param <T> parse result entity type
 * @author Mikhail Boldinov, 05/03/14
 */
public interface EDIParseResultHandler<T extends Identifiable<Long>> {

    /**
     * Handles {@link EDIParseResult}.
     *
     * @param parseResult
     *            {@link EDIParseResult}
     * @return list of non-processed transactions indexes (0, 2, 5, etc.).
     * @throws Exception
     *             if any exceptions occurs while handling the Parse Result
     */
    List<Integer> handle(EDIParseResult<T> parseResult) throws Exception;
}
