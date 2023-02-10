/**
 * Financial Board page objects.
 * 
 * @author Sergey Vovchuk
 */
angular.module('PageObjectsModule').factory('FinancialBoardPageObject', [ function() {
    return {
        history : {
            controller : '[data-ng-controller="FinancialBoardHistoryController"]',
            inputInvoice : 'invoiceNumber',
            inputPro : 'proNumber',
            inputBol : 'bolNumber',
            inputCustomer : 'input[data-ng-model="selectedCustomer"]',
            inputFromDate : 'fromDate',
            inputToDate : 'toDate',
            inputLoadId : 'loadId',
            gridRows : 'div[data-ng-grid="invoicesGrid"] [ng-row]',
            gridFirstRow: 'div[data-ng-grid="invoicesGrid"] [ng-row]:first',
            gridSecondRow: 'div[data-ng-grid="invoicesGrid"] [ng-row]:eq(1)',
            searchButton : 'button[data-ng-click="refreshTable()"]',
            clearButton : 'button[data-ng-click="resetSearch()"]',
            viewButton : 'button[data-ng-click="viewDetails()"]',
            exportButton : 'button[data-ng-click="exportInvoices()"]',
            reProcessButton : 'button[data-ng-click="showReprocessingDialog()"]',
            cbiReprocessGrid: 'div[data-ng-grid="cbiReprocessGrid"] [ng-row]',
            reprocessToFinanceCbiButton : 'button[data-ng-click="reprocessToFinance()"]',
            closeReprocessFinancialCbiButton: 'button[data-ng-click="reprocessFinancialPopup.close()"]',
            closeViewConsolidatedInvoiceButton: 'button[data-ng-click="cbiDetailsPopup.close()"]',
            invoiceSortHeader: '.colt2',

            cancelButton : '.a_cancelButton',

            reProcessDialog : '.a_financialReProcessDialog',
            reProcessCancelButton : 'button[data-ng-click="reprocessDialog.close()"]',

            cbiDetailsDialog : '#cbi-details',
            cbiDialogClose : 'button[data-ng-click="cbiDetailsPopup.close()"]',

            setInvoice : function(value) {
                input(this.inputInvoice).enter(value);
            },
            setPro : function(value) {
                input(this.inputPro).enter(value);
            },
            setBol : function(value) {
                input(this.inputBol).enter(value);
            },
            setCustomer: function(customerName){
                setValue(this.inputCustomer, customerName);
            },
            setFromDate : function(value) {
                input(this.inputFromDate).enter(value);
            },
            setToDate : function(value) {
                input(this.inputToDate).enter(value);
            },
            setLoadId : function(value) {
                input(this.inputLoadId).enter(value);
            },
            clickCancel : function() {
                element(this.cancelButton).click();
            },
            clickSearchButton : function() {
                element(this.searchButton).click();
            },
            clickClearButton : function() {
                element(this.clearButton).click();
            },
            clickViewButton : function() {
                element(this.viewButton).click();
            },
            clickExportButton : function() {
                element(this.exportButton).click();
            },
            clickReprocessButton : function() {
                element(this.reProcessButton).click();
            },
            clickCancelReProcess : function() {
                element(this.reProcessCancelButton).click();
            },
            clickCloseCbiDialog : function() {
                element(this.cbiDialogClose).click();
            },
            getInvoicesRowsCount : function() {
                return element(this.gridRows).count();
            },
            selectFirstRow: function() {
                element(this.gridFirstRow).click();
            },
            selectSecondRow: function() {
                element(this.gridSecondRow).click();
            },
            getSearchButtonDisplay : function () {
                return element(this.searchButton).attr("disabled");
            },
            getClearButtonDisplay : function () {
                return element(this.clearButton).attr("disabled");
            },
            getViewButtonDisplay : function () {
                return element(this.viewButton).attr("disabled");
            },
            getExportButtonDisplay : function () {
                return element(this.exportButton).attr("disabled");
            },
            getReprocessButtonDisplay : function () {
                return element(this.reProcessButton).attr("disabled");
            },
            getReprocessInvoiceDialogDisplay : function () {
                return element(this.reProcessDialog).css("display");
            },
            getCbiDetailsDialogDisplay : function () {
                return element(this.cbiDetailsDialog).css("display");
            },
            getCbiReprocessRowsCount : function() {
                return element(this.cbiReprocessGrid).count();
            },
            getReprocessToFinanceCbiButton : function() {
                return element(this.reprocessToFinanceCbiButton).attr("disabled");
            },
            clickCloseReprocessFinancialCbiButton : function() {
                element(this.closeReprocessFinancialCbiButton).click();
            },
            clickCloseViewConsolidatedInvoiceButton : function() {
                element(this.closeViewConsolidatedInvoiceButton).click();
            },
            clickInvoiceSortHeader : function() {
                element(this.invoiceSortHeader).click();
            }
        },

        transactional : {
            controller : '[data-ng-controller="FinancialBoardTransactionalController"]',
            inputSearchByBOL: 'input[ng-model="col.searchValue"]:eq(7)',
            inputSearchByCustomer: 'input[ng-model="col.searchValue"]:eq(3)',
            processOnSchedule: 'input[data-ng-checked="row.entity.approved"]',
            gridFirstRow: '[ng-row]:eq(0)',
            overrideScheduledProcessButton: 'button[data-ng-click="processInvoices()"]',
            processInvoicesToFinanceButton: 'button[data-ng-click="processInvoicesToFinance()"]',
            closeProcessResultsDialogButton: 'button[data-ng-click="closeProcessResultsDialog()"]',

            searchByBOL: function(value) {
                setValue(this.inputSearchByBOL, value);
            },
            searchByCustomer: function(value) {
                setValue(this.inputSearchByCustomer, value);
            },
            uncheckProcessOnSchedule: function() {
                element(this.processOnSchedule).click();
            },
            selectFirstRow: function() {
                element(this.gridFirstRow).click();
            },
            clickOverrideScheduledProcessButton : function() {
                element(this.overrideScheduledProcessButton).click();
            },
            clickProcessInvoicesToFinanceButton : function() {
                element(this.processInvoicesToFinanceButton).click();
            },
            clickCloseProcessResultsDialogButton : function() {
                element(this.closeProcessResultsDialogButton).click();
            },
            getOverrideScheduledProcessButtonDisplay : function () {
                return element(this.overrideScheduledProcessButton).attr("disabled");
            },
            getProcessInvoicesToFinanceButtonDisplay : function () {
                return element(this.processInvoicesToFinanceButton).attr("disabled");
            },
            getCloseProcessResultsDialogButtonDisplay : function () {
                return element(this.closeProcessResultsDialogButton).attr("disabled");
            },
        },

        consolidated : {
            controller : '[data-ng-controller="FinancialBoardConsolidatedController"]',
            cbiInvoicesGridRows : 'div[data-ng-grid="cbiModel.consolidatedInvoicesGrid"] [ng-row]',
            cbiInvoicesGridFirstRow: 'div[data-ng-grid="cbiModel.consolidatedInvoicesGrid"] [ng-row]:first',
            cbiInvoicesGridLastRow: '[ng-row]:last',
            consolidatedLoadsGridFirstRow: 'div[data-ng-model="cbiModel.cbiLoads"] [ng-row]:first',
            editBillToInformationForCustomerLink: 'a[href="#/customer/1/billTo/22"]',
            cbiFilterDate: 'cbiModel.filterDate',
            processInvoicesButton: 'button[data-ng-click="processInvoices()"]',
            processInvoicesToFinanceButton: 'button[data-ng-click="processInvoicesToFinance()"]',
            closeProcessResultsDialogButton: 'button[data-ng-click="closeProcessResultsDialog()"]',

            setFilterDate : function(value) {
                input(this.cbiFilterDate).enter(value);
            },
            clickProcessInvoicesButton : function() {
                element(this.processInvoicesButton).click();
            },
            clickProcessInvoicesToFinanceButton : function() {
                element(this.processInvoicesToFinanceButton).click();
            },
            selectLastRow: function() {
                element(this.cbiInvoicesGridLastRow).click();
            },
            selectConsolidatedLoadsGridFirstRow: function() {
                element(this.consolidatedLoadsGridFirstRow).click();
            },
            clickCloseProcessResultsDialogButton : function() {
                element(this.closeProcessResultsDialogButton).click();
            },
        },

        audit : {
            controller : '[data-ng-controller="FinancialBoardAuditController"]',
        },

        errors : {
            controller : '[data-ng-controller="FinancialBoardErrorsController"]',
        }
    };
} ]);
