package com.pls.invoice.service.impl.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementes {@link InvoiceService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Override
    public List<LoadAdjustmentBO> getSortedInvoices(Long invoiceId) {
        List<LoadEntity> loads = invoiceDao.getLoadsByInvoiceId(invoiceId);
        List<FinancialAccessorialsEntity> adjustments = invoiceDao.getAdjustmentsByInvoiceId(invoiceId);

        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(loads.size() + adjustments.size());
        for (LoadEntity load : loads) {
            invoices.add(new LoadAdjustmentBO(load));
        }
        for (FinancialAccessorialsEntity adj : adjustments) {
            invoices.add(new LoadAdjustmentBO(adj));
        }

        Collections.sort(invoices, new InvoicesComparator());
        return invoices;
    }
 
    /**
     * Invoices Comparator.
     * 
     * @author Aleksandr Leshchenko
     */
    private final class InvoicesComparator implements Comparator<LoadAdjustmentBO> {
        @Override
        public int compare(LoadAdjustmentBO o1, LoadAdjustmentBO o2) {
            InvoiceSortType sortType = null;
            CompareToBuilder builder = new CompareToBuilder();
            builder.append(getBillTo(o1).getId(), getBillTo(o2).getId());
            if (ObjectUtils.equals(getBillTo(o1).getId(), getBillTo(o2).getId())) {
                sortType = getBillTo(o1).getInvoiceSettings().getSortType();
            }

            if (sortType != null) {
                if (sortType == InvoiceSortType.BOL) {
                    builder.append(getLoad(o1).getNumbers().getBolNumber(), getLoad(o2).getNumbers().getBolNumber());
                } else if (sortType == InvoiceSortType.GL_NUM) {
                    builder.append(getLoad(o1).getNumbers().getGlNumber(), getLoad(o2).getNumbers().getGlNumber());
                } else if (sortType == InvoiceSortType.DELIV_DATE) {
                    builder.append(getLoad(o1).getDestination().getDeparture(), getLoad(o2).getDestination().getDeparture());
                }
                builder.append(getLoad(o1).getId(), getLoad(o2).getId()).append(getAdjustmentId(o1), getAdjustmentId(o2));
            }
            return builder.build();
        }

        private BillToEntity getBillTo(LoadAdjustmentBO bo) {
            return bo.getAdjustment() == null ? bo.getLoad().getBillTo() : bo.getAdjustment().getCostDetailItems().iterator().next().getBillTo();
        }

        private LoadEntity getLoad(LoadAdjustmentBO bo) {
            return bo.getAdjustment() == null ? bo.getLoad() : bo.getAdjustment().getLoad();
        }

        private Long getAdjustmentId(LoadAdjustmentBO bo) {
            return bo.getAdjustment() == null ? null : bo.getAdjustment().getId();
        }
    }
}
