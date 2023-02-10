package com.pls.ltlrating.batch.analysis;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

import com.pls.ltlrating.batch.analysis.model.AnalysisItem;

/**
 * Item reader for Freight Analysis.
 *
 * @author Aleksandr Leshchenko
 */
public class AnalysisItemReader implements ItemReader<AnalysisItem> {
    @Value("#{jobParameters['pricing.analysis.rowId']}")
    private Long rowId;

    @Value("#{jobParameters['pricing.analysis.tariffId']}")
    private Long tariffId;

    @Value("#{jobParameters['pricing.analysis.customerId']}")
    private Long customerId;

    private boolean isItemRead = false;

    @Override
    public AnalysisItem read() throws Exception {
        if (isItemRead) {
            return null;
        }
        isItemRead = true;
        AnalysisItem item = new AnalysisItem();
        item.setRowId(rowId);
        item.setTariffId(tariffId);
        item.setCustomerId(customerId);
        return item;
    }
}
