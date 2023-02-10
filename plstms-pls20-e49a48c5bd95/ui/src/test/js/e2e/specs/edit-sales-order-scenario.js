/**
 * 
 * @author Sergii Belodon
 */
describe('Pricing details functionality', function() {
    var loginLogoutPageObject, editDataSalesOrderCreateObject, trackingBoardPageObject, editCustomersPageObject, editDataSalesOrderCreateObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        trackingBoard = $injector.get('TrackingBoardPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        editCustomersPageObject = $injector.get('EditCustomersPageObject');
        auditTabObject = $injector.get('AuditTabObject');
        docsTabObject = $injector.get('DocsTabObject');
        editDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject');
        allPage = trackingBoard.all;
    });
    function visitEditCustomerProfile() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectFirstRow();

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'edit customer').click();
    }
    function visitViewSalesOrderDocsTab() {
        browser().navigateTo('#/trackingBoard/all');
        expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardAllShipmentsController"').count()).toBe(1);
        allPage.setLoadId(1);
        allPage.clickButtonSearch();
        allPage.clickRow();
        allPage.clickView();
        docsTabObject.clickTab();
    }

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should set "Generate Consignee Invoice" checkbox checked in Edit customer profile', function() {
        visitEditCustomerProfile();
        expect(isChecked(editCustomersPageObject.genereteConsigneeInvoiceCheckbox)).toBe(false);
        editCustomersPageObject.checkGenerateConsigneeInvoice();
        expect(isChecked(editCustomersPageObject.genereteConsigneeInvoiceCheckbox)).toBe(true);
        editCustomersPageObject.saveChanges();
        disableLocationChangeCheck();
    });

    it('Should see Regenerate consignee Invoice Button on docs EditSalesOrder docs tab', function() {
        visitViewSalesOrderDocsTab();
        expect(element(docsTabObject.regenerateConsigneeInvoiceButton).attr('disabled')).not().toBeDefined();
        editDataSalesOrderCreateObject.clickCloseEditSalesOrderButton();

    });
    it('Should uncheck "Generate Consignee Invoice" checkbox in Edit Customer Profile', function() {
        visitEditCustomerProfile();
        expect(isChecked(editCustomersPageObject.genereteConsigneeInvoiceCheckbox)).toBe(true);
        editCustomersPageObject.checkGenerateConsigneeInvoice();
        expect(isChecked(editCustomersPageObject.genereteConsigneeInvoiceCheckbox)).toBe(false);
        editCustomersPageObject.saveChanges();
        disableLocationChangeCheck();
    });
    it('Should not see Regenerate consignee Invoice Button on docs EditSalesOrder docs tab', function() {
        visitViewSalesOrderDocsTab();
        expect(element(docsTabObject.regenerateConsigneeInvoiceButton)).not().toBeDefined();
    });

    it('checks pricing details', function() {
        browser().navigateTo('#/trackingBoard/all');
        expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardAllShipmentsController"').count()).toBe(1);
        allPage.setLoadId(1);
        allPage.clickButtonSearch();
        allPage.clickRow();
        allPage.clickView();
        element(auditTabObject.auditTabLink).click();
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(0)').text()).toBe('1');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(1)').text()).toBe('100');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(2)').text()).toBe('50');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(3)').text()).toBe('some product');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(4)').text()).toBe('1550');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(5)').text()).toBe('2');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(6)').text()).toBe('$56.06');
        expect(element(auditTabObject.active.costDetailsGrid + ' [ng-row]:first [ng-cell-text]:eq(7)').text()).toBe('$68.95');

        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(0) [ng-cell-text]:eq(0)').text()).toBe('Total');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(0) [ng-cell-text]:eq(1)').text()).toBe('$68.95');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(1) [ng-cell-text]:eq(0)').text()).toBe('Accessorial Total');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(1) [ng-cell-text]:eq(1)').text()).toBe('$0.00');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(2) [ng-cell-text]:eq(0)').text()).toBe('CZAR Minimum Floor');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(2) [ng-cell-text]:eq(1)').text()).toBe('$88.00');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(3) [ng-cell-text]:eq(0)').text()).toBe('Total');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(3) [ng-cell-text]:eq(1)').text()).toBe('$88.00');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(4) [ng-cell-text]:eq(0)').text()).toBe('80% Discount');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(4) [ng-cell-text]:eq(1)').text()).toBe('$70.40');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(5) [ng-cell-text]:eq(0)').text()).toBe('Total After Discount');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(5) [ng-cell-text]:eq(1)').text()).toBe('$17.60');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(6) [ng-cell-text]:eq(0)').text()).toBe('Minimum PLS Cost');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(6) [ng-cell-text]:eq(1)').text()).toBe('$90.00');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(7) [ng-cell-text]:eq(0)').text()).toBe('Total After Applying PLS Minimum Cost');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(7) [ng-cell-text]:eq(1)').text()).toBe('$80.00');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(8) [ng-cell-text]:eq(0)').text()).toBe('22% Fuel Surcharge');
        expect(element(auditTabObject.active.accessorialsGrid + ' [ng-row]:eq(8) [ng-cell-text]:eq(1)').text()).toBe('$0.00');
    });
});