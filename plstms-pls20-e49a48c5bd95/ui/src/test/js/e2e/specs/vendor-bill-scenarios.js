/**
 * This scenario checks Vendor Bill functionality.
 * @author Alexander Kirichenko
 */
describe('Vendor Bill functionality. ', function () {
    var $injector, loginLogoutPageObject, vendorBillsPageObject, editDataSalesOrderCreateObject;

    var salesOrderData = {
        origin: '43210',
        originFull: 'COLUMBUS, OH, 43210',
        destination: '01010',
        destinationFull: 'BRIMFIELD, MA, 01010',
        bol: 'bol',
        pro: 'pro'
    };

    beforeEach(function () {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        vendorBillsPageObject = $injector.get('VendorBillsPageObject');
        editDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject');
    });

    it('Should login into application', function () {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should open vendor bills list page.', function () {
        browser().navigateTo('#/vendorBill');
        expect(browser().location().path()).toBe("/vendorBill/unmatched");
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(5);
        expect(element(vendorBillsPageObject.list.gridColShipper).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.gridColConsignee).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.unmatchedTabSelector).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.archiveTabSelector).count()).toBe(1);
        expect(angularElement(vendorBillsPageObject.list.unmatchedTabSelector)).toHaveClass("active");
        expect(angularElement(vendorBillsPageObject.list.archiveTabSelector)).not().toHaveClass("active");
        expect(element(vendorBillsPageObject.list.unmatchedButtonsBlockSelector).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.archivedButtonsBlockSelector).count()).toBe(1);
        expect(vendorBillsPageObject.list.getSearchSalesOrderDisplay()).toBe("disabled");
        expect(vendorBillsPageObject.list.getViewVendorBillDisplay()).toBe("disabled");
        expect(vendorBillsPageObject.list.getArchiveVendorBillDisplay()).toBe("disabled");
    });

    it('Should allow you to work properly with selected vendor bill.', function () {
        vendorBillsPageObject.list.searchByOrigin('53029');
        vendorBillsPageObject.list.selectFirstRow();
        expect(vendorBillsPageObject.list.getSearchSalesOrderDisplay()).not().toBe("disabled");
        expect(vendorBillsPageObject.list.getViewVendorBillDisplay()).not().toBe("disabled");
        expect(vendorBillsPageObject.list.getArchiveVendorBillDisplay()).not().toBe("disabled");
    });

    it('Should archive first vendor bill in grid.', function () { 
        expect(vendorBillsPageObject.confirmArchiveDialog.getDialogDisplay()).toBe("none");
        vendorBillsPageObject.list.archiveSelectedVendorBill();
        expect(vendorBillsPageObject.confirmArchiveDialog.getDialogDisplay()).not().toBe("none");
        vendorBillsPageObject.confirmArchiveDialog.selectReason();
        vendorBillsPageObject.confirmArchiveDialog.confirmArchiveVendorBill();
        vendorBillsPageObject.list.searchByOrigin('');
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(4);
    });

    it('Should switch to archive tab.', function () {
        vendorBillsPageObject.list.selectArchiveTab();
        expect(angularElement(vendorBillsPageObject.list.archiveTabSelector)).toHaveClass("active");
        expect(angularElement(vendorBillsPageObject.list.unmatchedTabSelector)).not().toHaveClass("active");
        expect(element(vendorBillsPageObject.list.unmatchedButtonsBlockSelector).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.archivedButtonsBlockSelector).count()).toBe(1);
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(11);
        expect(vendorBillsPageObject.list.getUnArchiveVendorBillDisplay()).toBe("disabled");
        expect(vendorBillsPageObject.list.getViewVendorBillDisplay()).toBe("disabled");
    });

    it('Should move to unmatched selected archive vendor bill.', function () {
        vendorBillsPageObject.list.selectFirstRow();
        expect(vendorBillsPageObject.list.getUnArchiveVendorBillDisplay()).not().toBe("disabled");
        expect(vendorBillsPageObject.list.getViewVendorBillDisplay()).not().toBe("disabled");
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(11);
        vendorBillsPageObject.list.unArchiveSelectedVendorBill();
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(10);
        vendorBillsPageObject.list.selectUnmatchedTab();
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(5);
    });

    it('Should open edit vendor bill dialog.', function () {
        vendorBillsPageObject.list.selectFirstRow();
        expect(vendorBillsPageObject.list.getDialogDisplay()).toBe("none");
        vendorBillsPageObject.list.viewSelectedVendorBill();
        expect(vendorBillsPageObject.list.getDialogDisplay()).not().toBe("none");
    });

    it('Should open Search Sales Orders page.', function () {
        browser().navigateTo('#/vendorBill');
        expect(browser().location().path()).toBe("/vendorBill/unmatched");
        vendorBillsPageObject.list.selectFirstRow();
        expect(vendorBillsPageObject.list.getSearchSalesOrderDisplay()).not().toBe("disabled");
        vendorBillsPageObject.list.showSearchSalesOrderTab();
        expect(element(vendorBillsPageObject.salesOrdersPage.controller).count()).toBe(1);
    });

    it('Should empty fields on the Search Sales Orders page.', function () {
        vendorBillsPageObject.salesOrdersPage.setBol(salesOrderData.bol);
        vendorBillsPageObject.salesOrdersPage.setPro(salesOrderData.pro);
        vendorBillsPageObject.salesOrdersPage.setOrigin(salesOrderData.origin);
        vendorBillsPageObject.salesOrdersPage.setDestination(salesOrderData.destination);
        expect(element(vendorBillsPageObject.salesOrdersPage.origin).val()).toBe(salesOrderData.originFull);
        expect(element(vendorBillsPageObject.salesOrdersPage.destination).val()).toBe(salesOrderData.destinationFull);
        expect(element(vendorBillsPageObject.salesOrdersPage.bol).val()).toBe(salesOrderData.bol);
        expect(element(vendorBillsPageObject.salesOrdersPage.pro).val()).toBe(salesOrderData.pro);
        vendorBillsPageObject.salesOrdersPage.clearSalesOrderField();
        expect(element(vendorBillsPageObject.salesOrdersPage.origin).val()).toBe('');
        expect(element(vendorBillsPageObject.salesOrdersPage.destination).val()).toBe('');
        expect(element(vendorBillsPageObject.salesOrdersPage.bol).val()).toBe('');
        expect(element(vendorBillsPageObject.salesOrdersPage.pro).val()).toBe('');
        vendorBillsPageObject.list.closeSalesOrderDialog();
    });

    it('Should open Edit Sales Order dialog.', function () {
        vendorBillsPageObject.list.searchByOrigin('STRONGSVILLE');
        vendorBillsPageObject.list.selectFirstRow();
        expect(vendorBillsPageObject.list.getEditSalesOrderDialogDisplay()).toBe("none");
        vendorBillsPageObject.list.createSalesOrder();
        expect(vendorBillsPageObject.list.getEditSalesOrderDialogDisplay()).not().toBe("none");
        vendorBillsPageObject.list.setCustomer('PLS SHIPPER');
        vendorBillsPageObject.salesOrdersPage.closeEditSalesOrderPage();
    });

    it('Should Create Sales Order from Vendor Bill.', function () {
        browser().navigateTo('#/vendorBill');

        vendorBillsPageObject.list.searchByOrigin('STRONGSVILLE');
        vendorBillsPageObject.list.selectFirstRow();
        vendorBillsPageObject.list.createSalesOrder();
        vendorBillsPageObject.list.setCustomer('PLS SHIPPER');
        vendorBillsPageObject.list.setOrigin('17402');
        vendorBillsPageObject.list.setDestination('90723');

        vendorBillsPageObject.list.setActualPickupDate('11/05/2014');
        vendorBillsPageObject.list.setActualDeliveryDate('11/05/2014');
        vendorBillsPageObject.list.selectProduct('Budweiser 24545214SKU');
        vendorBillsPageObject.list.setWeight('100');
        vendorBillsPageObject.list.addCostDetails();
        vendorBillsPageObject.list.setCostDetailDescription('AD - ADMINISTRA');
        vendorBillsPageObject.list.setRevenue('100');
        vendorBillsPageObject.list.setCost('50');
        vendorBillsPageObject.list.saveCostDetails();
        editDataSalesOrderCreateObject.setPro(salesOrderData.pro);
        vendorBillsPageObject.list.selectAddressesTab();
        vendorBillsPageObject.list.setLocation('COLUMBIA, SC');
        vendorBillsPageObject.list.setOriginAddressName('ADDR_NAME18');
        vendorBillsPageObject.list.setDestinationAddressName('ADDR_NAME19');
        vendorBillsPageObject.list.selectDetailsTab();
        vendorBillsPageObject.list.setBOL('555');
        vendorBillsPageObject.list.setPo('555');
        vendorBillsPageObject.list.setPu('555');
        vendorBillsPageObject.list.setShipperRef('555');
        vendorBillsPageObject.list.setTrailer('555');
        vendorBillsPageObject.list.setSO('555');
        vendorBillsPageObject.list.setGL('555');
        vendorBillsPageObject.list.setCargo(555);
        vendorBillsPageObject.list.setRequestedBy('555');
        vendorBillsPageObject.list.setDeliveryFromWindow('08:00 AM');
        vendorBillsPageObject.list.setDeliveryToWindow('09:30 AM');
        vendorBillsPageObject.list.updateOrder();
        expect(vendorBillsPageObject.list.getEditSalesOrderDialogDisplay()).toBe("none");
        browser().navigateTo('#/vendorBill');
        expect(element(vendorBillsPageObject.list.gridRow).count()).toBe(4);
    });

    it('Should logout from application.', function () {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});