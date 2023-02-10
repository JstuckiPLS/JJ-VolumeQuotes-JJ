/**
 * This scenario creates load for invoicing.
 * 
 * @author Aleksandr Leshchenko
 */
describe('Create Sales Order for Invoicing functionality and process to finance', function() {
    var loginLogoutPageObject, editDataSalesOrderCreateObject, trackingBoardPageObject, financialBoardPageObject,
        transactionalPageObject, editCustomersPageObject, billToCustomerPageObject, customerInvoicesPreferencesPageObject;
    var bol = "BOL#" + new Date().getTime();
    var transactionalBillTo = 'Altria Group, Inc.';
    var cbiBillTo = 'Stripco Inc';

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        editDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject');
        trackingBoardPageObject = $injector.get('TrackingBoardPageObject');
        financialBoardPageObject = $injector.get('FinancialBoardPageObject');
        editCustomersPageObject = $injector.get('EditCustomersPageObject');
        billToCustomerPageObject = $injector.get('BillToCustomersPageObject');
        customerInvoicesPreferencesPageObject = $injector.get('CustomerInvoicesPreferencesPageObject');

        transactionalPageObject = financialBoardPageObject.transactional;
        consolidatedPageObject = financialBoardPageObject.consolidated;
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    function addVendorBillAndOverrideDateHold() {
        trackingBoardPageObject.unbilled.clickRow();
        trackingBoardPageObject.unbilled.clickEditButton();
        editDataSalesOrderCreateObject.clickAddVendorBillButton();
        editDataSalesOrderCreateObject.clickSaveVendorBillButton();
        editDataSalesOrderCreateObject.clickSaveEditSalesOrderButton();
        trackingBoardPageObject.unbilled.clickRow();
        trackingBoardPageObject.unbilled.clickOverrideDateHoldButton();
    }

    it('Should create delivered sales order with costs', function() {
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        editDataSalesOrderCreateObject.createSalesOrder(transactionalBillTo, bol + "-T");
        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/unbilled');
        expect(browser().location().path()).toBe("/trackingBoard/unbilled");
        trackingBoardPageObject.unbilled.searchByBOL(bol + "-T");
        expect(trackingBoardPageObject.unbilled.getRowsCount()).toBe(1);
    });
    it('should add vendor bill and override date hold', function() {
        addVendorBillAndOverrideDateHold();
    });

    it('should open transactional invoice page and process invoices to Finance', function() {
        browser().navigateTo('#/financialBoard/transactional');
        expect(element(transactionalPageObject.controller, 'Div with ng-controller="FinancialBoardTransactionalController"').count()).toBe(1);
        transactionalPageObject.searchByBOL('BOL');
        transactionalPageObject.selectFirstRow();
        transactionalPageObject.uncheckProcessOnSchedule();
        expect(transactionalPageObject.getOverrideScheduledProcessButtonDisplay()).not().toBe('disabled');
        transactionalPageObject.clickOverrideScheduledProcessButton();
        expect(transactionalPageObject.getCloseProcessResultsDialogButtonDisplay()).not().toBe('disabled');
        transactionalPageObject.clickProcessInvoicesToFinanceButton();
        expect(transactionalPageObject.getProcessInvoicesToFinanceButtonDisplay()).not().toBe('disabled');
        transactionalPageObject.clickCloseProcessResultsDialogButton();
    });

    it('Edit Bill To invoice type to CBI', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectLastRow();
        element(editCustomersPageObject.editButtonSelector, 'edit customer').click();
        element(billToCustomerPageObject.billToTabSelector, 'go to BillTo tab').click();
        element('.ngViewport', 'Important! scroll grit to the bottom').query(function(elements, done) {
            var maxBootomPosition = 5000;
            elements.scrollTop(maxBootomPosition);
            done();
        });
        sleep(1);
        editCustomersPageObject.selectRowWithText(cbiBillTo);
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to Invoice Pref tab').click();
        customerInvoicesPreferencesPageObject.setCbiInvoiceType('FIN');

        expect(element(billToCustomerPageObject.saveButtonSelector, 'check save button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();
    });

    it('Should create delivered sales order', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        editDataSalesOrderCreateObject.createSalesOrder(cbiBillTo, bol + "-CBI");

        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/unbilled');
        expect(browser().location().path()).toBe("/trackingBoard/unbilled");
        trackingBoardPageObject.unbilled.searchByBOL(bol + "-CBI");
        expect(trackingBoardPageObject.unbilled.getRowsCount()).toBe(1);
    });

    it('should add vendor bill and override date hold', function() {
        addVendorBillAndOverrideDateHold();
    });

    it('should open consolidated invoices page and process invoices to Finance', function() {
        browser().navigateTo('#/financialBoard/consolidated');
        expect(element(consolidatedPageObject.controller, 'Div with ng-controller="FinancialBoardConsolidatedController"').count()).toBe(1);
        consolidatedPageObject.selectLastRow();
        consolidatedPageObject.selectConsolidatedLoadsGridFirstRow();
        var today = new Date();
        var filterDate = today = today.getMonth() + 1 + '/' +today.getDate() + '/' + today.getFullYear();
        consolidatedPageObject.setFilterDate(filterDate);
        consolidatedPageObject.clickProcessInvoicesButton();
        consolidatedPageObject.clickProcessInvoicesToFinanceButton();
        consolidatedPageObject.clickCloseProcessResultsDialogButton();
    });

    it('Should logout from application', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});