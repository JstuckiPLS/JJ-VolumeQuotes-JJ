angular.module('PageObjectsModule').factory('CustomerInvoicesPreferencesPageObject', [function() {
    return {
        editCustomerInvoicesPreferencesSelector: '[data-pls-bill-to-invoice-preferences]',
        processingTimeTypeaheadSelector: 'input[data-pls-pickup-window="preferencesModel.pickupWindow"]',
        processingTimeZoneSelector: 'select[data-ng-model="billTo.invoicePreferences.processingTimezone.localOffset"]',
        processingPeriodSelector: 'select[data-ng-model="billTo.invoicePreferences.processingPeriod"]',
        processingDaySelector: 'select[data-ng-model="billTo.invoicePreferences.processingDayOfWeek"]',
        checkboxEdiInvoice: 'billTo.invoicePreferences.ediInvoice',
        excelOptionsSelector: 'preferencesModel.xlsSelectedDocument',
        pdfOptionsSelector: 'preferencesModel.pdfSelectedDocument',
        paymentMethodSelector: 'select[data-ng-model="billTo.paymentMethod"]',
        emailForCreditCardSelector: 'billTo.creditCardEmail',

        currencyCode: '#currencyCode',
        invoiceType: '#invoiceType',
        cbiInvoiceType: '#cbiInvoiceType',
        xlsDocument: '#xlsDocument',
        pdfDocument: '#pdfDocument',
        sortType: '#sortType',
        releaseDay: '#releaseDay',

        setEmailForCreditCard : function(value) {
            input(this.emailForCreditCardSelector).enter(value);
        },
        invoiceTypeSelect: 'billTo.invoicePreferences.invoiceType',
        setInvoiceType: function(invoiceType) {
            select(this.invoiceTypeSelect).option(invoiceType);
        },
        cbiInvoiceTypeSelect: 'billTo.invoicePreferences.cbiInvoiceType',
        setCbiInvoiceType: function(cbiInvoiceType) {
            select(this.cbiInvoiceTypeSelect).option(cbiInvoiceType);
        },
        processingTypeSelect: 'billTo.invoicePreferences.processingType',
        setProcessingType: function(processingType) {
            select(this.processingTypeSelect).option(processingType);
        },
        paymentMethodSelect: 'billTo.paymentMethod',
        setPaymentMethod: function(paymentMethodSelector) {
            select(this.paymentMethodSelect).option(paymentMethodSelector);
        },
        processingPeriodSelect: 'billTo.invoicePreferences.processingPeriod',
        setProcessingPeriod : function(processingPeriod) {
            select(this.processingPeriodSelect).option(processingPeriod);
        },

        dayOfWeekSelect: 'billTo.invoicePreferences.processingDayOfWeek',
        setDayOfWeek: function(val) {
            select(this.dayOfWeekSelect).option(val);
        },
        releaseDayOfWeekSelect: 'billTo.invoicePreferences.releaseDayOfWeek',
        setReleaseDayOfWeek: function(val) {
            select(this.releaseDayOfWeekSelect).option(val);
        },
        currencyCodeSelect: 'billTo.currency',
        setCurrencyCode: function(val) {
            select(this.currencyCodeSelect).option(val);
        },
        setPaperWorkRequirements: function(customerReqVal) {
            select('billTo.invoicePreferences.requiredDocuments[row.rowIndex].customerRequestType').option(customerReqVal);
        },
        checkedEdiInvoice: function() {
            input(this.checkboxEdiInvoice).check();
        },
        setExcelOption : function(excelOption) {
            select(this.excelOptionsSelector).option(excelOption);
        },
        setPdfOptions : function(pdfOption) {
            select(this.pdfOptionsSelector).option(pdfOption);
        }
    };
}]);