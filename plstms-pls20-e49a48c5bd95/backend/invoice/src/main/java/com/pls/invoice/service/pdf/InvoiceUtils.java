package com.pls.invoice.service.pdf;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.pls.core.domain.organization.PlsCustomerTermsEntity;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Utility for processing invoice data.
 *
 * @author Denis Zhupinsky (Team International)
 */
public final class InvoiceUtils {
    private static final String SHIPPER_RATE_LABEL = "Shipper Rate";
    private static final String LINE_HAUL_LABEL = "Line haul";

    private InvoiceUtils() {
    }

    /**
     * Prepare general invoice information from load.
     *
     * @param load load
     * @param adjustment adjustment
     * @return {@link InvoicePdfDetails}
     */
    public static InvoicePdfDetails getInvoiceGeneralInfo(LoadEntity load, FinancialAccessorialsEntity adjustment) {
        InvoicePdfDetails invoiceDetails = new InvoicePdfDetails();
        BigDecimal amountDue;
        Date invoiceDate;
        if (adjustment != null) {
            amountDue = adjustment.getTotalRevenue();
            invoiceDate = adjustment.getGeneralLedgerDate();
        } else {
            amountDue = load.getActiveCostDetail().getTotalRevenue();
            invoiceDate = load.getActiveCostDetail().getGeneralLedgerDate();
        }
        if (invoiceDate == null) {
            // if invoice date was not specified or it was just generated and not sync'ed from DB
            invoiceDate = new Date();
        }
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(invoiceDate);

        int dueDays = 0;
        PlsCustomerTermsEntity payTerms = load.getBillTo().getPlsCustomerTerms();
        if (payTerms != null) {
            dueDays = payTerms.getDueDays();
        }
        dueDate.add(Calendar.DATE, dueDays);

        invoiceDetails.setAmountDue(amountDue);
        invoiceDetails.setInvoiceDate(invoiceDate);
        invoiceDetails.setDueDate(dueDate.getTime());

        return invoiceDetails;
    }

    /**
     * Get description of cost details item.
     *
     * @param costDetailItem cost details item to get description for
     * @return description
     */
    public static String getCostDetailsItemDescription(CostDetailItemEntity costDetailItem) {
        if (costDetailItem.getAccessorialType().equals("SRA")) {
            return costDetailItem.getFinancialAccessorials() != null ? LINE_HAUL_LABEL : SHIPPER_RATE_LABEL;
        } else if (costDetailItem.getAccessorialDictionary() != null) {
            return costDetailItem.getAccessorialDictionary().getDescription();
        }
        return costDetailItem.getAccessorialType();
    }
}
