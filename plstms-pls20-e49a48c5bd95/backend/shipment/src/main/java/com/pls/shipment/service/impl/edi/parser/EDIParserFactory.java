package com.pls.shipment.service.impl.edi.parser;

import com.pls.core.domain.organization.CarrierEntity;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.parser.EDIParser;

/**
 * Factory class for {@link com.pls.shipment.service.edi.parser.EDIParser} instances.
 *
 * @author Mikhail Boldinov, 30/08/13
 */
public final class EDIParserFactory {
    private EDIParserFactory() {
    }

    /**
     * Factory method which creates appropriate EDIParser object.
     *
     * @param carrier        {@link CarrierEntity}
     * @param transactionSet {@link EDITransactionSet}
     * @param dataProvider   data provider for EDI parser
     * @return instance of {@link com.pls.shipment.service.edi.parser.EDIParser}
     */
    public static EDIParser create(CarrierEntity carrier, EDITransactionSet transactionSet, AbstractEDIParser.DataProvider dataProvider) {
        switch (transactionSet) {
            case _204:
                return new EDI204Producer(carrier, dataProvider);
            case _210:
                return new EDI210Parser(carrier, dataProvider);
            case _214:
                return new EDI214Parser(carrier, dataProvider);
            case _990:
                return new EDI990Parser(carrier, dataProvider);
            case _997:
                return new EDI997Parser(carrier, dataProvider);
            default:
                throw new IllegalArgumentException("Can not get parser for transactionSet: " + transactionSet);
        }
    }
}
