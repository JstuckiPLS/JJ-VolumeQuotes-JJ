package com.pls.ltlrating.service.impl.file;

import java.math.BigDecimal;
import java.util.Collection;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.BaseDocumentParser;
import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;


/**
 * Parser for getting {@link LtlFuelSurchargeEntity} from excel.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeDocumentParser
    extends BaseDocumentParser<LtlFuelSurchargeEntity, LtlFuelSurchargeFieldsDescription> {

    private boolean incorrectHeader = false;

    public boolean isIncorrectHeader() {
        return incorrectHeader;
    }

    @Override
    protected LtlFuelSurchargeFieldsDescription parseHeaderColumn(String headerString) {
        return LtlFuelSurchargeFieldsDescription.getFromHeaderText(headerString);
    }

    @Override
    protected LtlFuelSurchargeEntity parseRecord() throws ImportFileInvalidDataException {
        LtlFuelSurchargeEntity entity = new LtlFuelSurchargeEntity();
        entity.setMinRate(readBigDecimal(LtlFuelSurchargeFieldsDescription.LOW_RANGE));
        entity.setMaxRate(readBigDecimal(LtlFuelSurchargeFieldsDescription.HIGH_RANGE));
        entity.setSurcharge(readBigDecimal(LtlFuelSurchargeFieldsDescription.SURCHARGE));
        return entity;
    }

    @Override
    protected void validateHeader(Collection<LtlFuelSurchargeFieldsDescription> headerData)
            throws ImportFileParseException {
        for (LtlFuelSurchargeFieldsDescription descriptor : LtlFuelSurchargeFieldsDescription.values()) {
            if (descriptor.isRequired() && !headerData.contains(descriptor)) {
                incorrectHeader = true;
            }
        }
    }

    private BigDecimal readBigDecimal(LtlFuelSurchargeFieldsDescription headerId) throws ImportFileInvalidDataException {
        Field field = getColumnData(headerId);

        if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(headerId));
        }

        return new BigDecimal(field.getString().replaceAll("[$%]", ""));
    }

    private String prepareColumnNotFoundMessage(LtlFuelSurchargeFieldsDescription field) {
        return "Column '" + field.getHeader() + "' was not found for '" + getRecordName() + "' row.";
    }
}
