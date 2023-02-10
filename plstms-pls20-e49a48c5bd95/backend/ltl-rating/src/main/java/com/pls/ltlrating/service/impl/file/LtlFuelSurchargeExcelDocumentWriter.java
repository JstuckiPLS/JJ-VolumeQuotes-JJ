package com.pls.ltlrating.service.impl.file;

import com.pls.core.service.file.ExcelDocumentWriter;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;

/**
 * Write list of {@link LtlFuelSurchargeEntity} to byte array as a excel document.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeExcelDocumentWriter extends ExcelDocumentWriter<LtlFuelSurchargeEntity> {

    @Override
    public String[] getHeaders() {
        return new String[] { "Min. Rate", "Max. Rate", "Surcharge" };
    }

    @Override
    public String[] getRowFromEntity(LtlFuelSurchargeEntity entity) {
        return new String[] {
                entity.getMinRate().toString(),
                entity.getMaxRate().toString(),
                entity.getSurcharge().toString()
                };
    }

}
