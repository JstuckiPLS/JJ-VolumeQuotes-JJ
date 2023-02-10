/**
 * Financial Board scenarios.
 * 
 * @author Sergey Vovchuk, Sahil Thakkar
 */

describe('Financial Board scenarios', function() {
    var $injector, financialBoard, loginLogoutPageObject, historyPage, transactionalPage, consolidatedPage, auditPage, errorsPage, billToCustomerPageObject;

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        financialBoard = $injector.get('FinancialBoardPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        billToCustomerPageObject = $injector.get('BillToCustomersPageObject');
        
        historyPage = financialBoard.history;
        transactionalPage = financialBoard.transactional;
        consolidatedPage = financialBoard.consolidated;
        auditPage = financialBoard.audit;
        errorsPage = financialBoard.errors;
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should open invoice history page', function () { 
        browser().navigateTo('#/financialBoard/history');
        expect(element(historyPage.controller, 'Div with ng-controller="FinancialBoardHistoryController"').count()).toBe(1);

        expect(angularElement(historyPage.inputCustomer, 'Customer field')).toHaveClass('ng-invalid');
        expect(angularElement(historyPage.inputCustomer, 'Customer field')).toHaveClass('ng-invalid-required');

        expect(historyPage.getSearchButtonDisplay()).toBe('disabled');
        expect(historyPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getViewButtonDisplay()).toBe('disabled');
        expect(historyPage.getExportButtonDisplay()).toBe('disabled');
        expect(historyPage.getReprocessButtonDisplay()).toBe('disabled');

        expect(historyPage.getInvoicesRowsCount()).toBe(0);
    });

    it('Checks search invoice history', function() {
        historyPage.setCustomer('PLS SHIPPER');
        historyPage.setBol('BOL%-T');
        historyPage.clickSearchButton();
        expect(historyPage.getInvoicesRowsCount()).toBe(1);
        expect(historyPage.getViewButtonDisplay()).toBe('disabled');
        expect(historyPage.getExportButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getReprocessButtonDisplay()).toBe('disabled');
    });

    it('Should open re-process invoice dialog for transactional invoice', function() {
        historyPage.selectFirstRow();
        expect(historyPage.getViewButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getReprocessButtonDisplay()).not().toBe('disabled');
        historyPage.clickViewButton();
        expect(element('[data-ng-controller="EditSalesOrderCtrl"]', 'Div with ng-controller="EditSalesOrderCtrl"').count()).toBe(1);
        historyPage.clickCancel();
        expect(historyPage.getReprocessInvoiceDialogDisplay()).toBe("none");
        historyPage.clickReprocessButton();
        expect(historyPage.getReprocessInvoiceDialogDisplay()).not().toBe("none");
        historyPage.clickCancelReProcess();
        historyPage.clickClearButton();
        expect(historyPage.getInvoicesRowsCount()).toBe(0);

        historyPage.setCustomer('PLS SHIPPER'); 
        expect(angularElement(historyPage.inputCustomer, 'Customer field')).toHaveClass('ng-valid');
        expect(angularElement(historyPage.inputCustomer, 'Customer field')).toHaveClass('ng-valid-required');
        expect(angularElement(historyPage.inputFromDate, 'From date field')).toHaveClass('ng-invalid-required');
        expect(angularElement(historyPage.inputFromDate, 'To date field')).toHaveClass('ng-invalid-required');
        expect(historyPage.getSearchButtonDisplay()).toBe('disabled');
        historyPage.clickClearButton();
        expect(angularElement(historyPage.inputCustomer, 'Customer field')).toHaveClass('ng-invalid-required');
        historyPage.setBol('BOL%-T');
        historyPage.clickSearchButton();
        expect(historyPage.getInvoicesRowsCount()).toBe(1);
        historyPage.clickClearButton();
    });

    it('Should open re-process invoice dialog for consolidated invoices', function() {
        historyPage.setBol('%');
        historyPage.clickSearchButton();
        expect(historyPage.getInvoicesRowsCount()).toBe(2);
        expect(historyPage.getViewButtonDisplay()).toBe('disabled');
        expect(historyPage.getExportButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getReprocessButtonDisplay()).toBe('disabled');
        historyPage.clickInvoiceSortHeader();
        historyPage.selectSecondRow();
        expect(historyPage.getViewButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getReprocessButtonDisplay()).not().toBe('disabled');
        expect(historyPage.getCbiDetailsDialogDisplay()).toBe("none");
        historyPage.clickViewButton();
        expect(historyPage.getCbiDetailsDialogDisplay()).not().toBe("none");
        historyPage.clickCloseViewConsolidatedInvoiceButton();
        expect(historyPage.getCbiDetailsDialogDisplay()).toBe("none");
        historyPage.clickReprocessButton();
        expect(historyPage.getCbiReprocessRowsCount()).toBe(1);
        expect(historyPage.getReprocessToFinanceCbiButton()).toBe('disabled');
        historyPage.clickCloseReprocessFinancialCbiButton();
    });

    it('Should open transactional invoice page', function() {
        browser().navigateTo('#/financialBoard/transactional');
        expect(element(transactionalPage.controller, 'Div with ng-controller="FinancialBoardTransactionalController"').count())
                .toBe(1);
    });

    it('Should open consolidated invoice page', function() {
        browser().navigateTo('#/financialBoard/consolidated');
        expect(
                element(consolidatedPage.controller, 'Div with ng-controller="FinancialBoardConsolidatedController"')
                        .count()).toBe(1);
    });
    
    it('Should open consolidated invoice page and check for the clickable URL', function() {
        browser().navigateTo('#/financialBoard/consolidated');
        expect(
                element(consolidatedPage.controller, 'Div with ng-controller="FinancialBoardConsolidatedController"')
                        .count()).toBe(1);
        
        expect(element(consolidatedPage.editBillToInformationForCustomerLink).count()).toBe(1);
        element(consolidatedPage.editBillToInformationForCustomerLink, 'Go to Edit Bill To Tab').click();
        
        sleep(1);
        
        expect(element(billToCustomerPageObject.billToTabSelector, 'check BillTo tab exists').count()).toBe(1);

        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
        expect(element(billToCustomerPageObject.editButtonSelector, 'check edit button exists').count()).toBe(1);

        expect(element(billToCustomerPageObject.billToControllerSelector, 'div containing data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
        expect(element(billToCustomerPageObject.customersGridSelector, 'check bill to address grid').count()).toBe(1);
    });

    it('Should open invoice audit page', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/financialBoard/audit');
        expect(element(auditPage.controller, 'Div with ng-controller="FinancialBoardAuditController"').count())
                .toBe(1);
    });

    it('Should open invoice errors page', function() {
        browser().navigateTo('#/financialBoard/errors');
        expect(element(errorsPage.controller, 'Div with ng-controller="FinancialBoardErrorsController"').count())
                .toBe(1);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});