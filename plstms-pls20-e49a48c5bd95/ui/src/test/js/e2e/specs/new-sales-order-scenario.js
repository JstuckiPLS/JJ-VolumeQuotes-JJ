/**
 * This scenario check create sales order functionality.
 * 
 * @author Dmitry Nikolaenko
 */
describe('Create Sales Order functionality', function() {
    var loginLogoutPageObject, editDataSalesOrderCreateObject, trackingBoardPageObject;

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        editDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject');
        trackingBoardPageObject = $injector.get('TrackingBoardPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should create sales order', function() {
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        editDataSalesOrderCreateObject.setStatus('Open');
        editDataSalesOrderCreateObject.setCustomer('PLS');
        editDataSalesOrderCreateObject.setOrigin(83210);
        editDataSalesOrderCreateObject.setDestination(16820);

        var bol = "BOL#" + new Date().getTime();
        editDataSalesOrderCreateObject.setPro(1);
        editDataSalesOrderCreateObject.setPickupDate('08/10/2014');
        editDataSalesOrderCreateObject.setEstimateDeliveryDate('08/10/2014');
        editDataSalesOrderCreateObject.selectProduct('Budweiser 24545214SKU');
        editDataSalesOrderCreateObject.setWeight(50);
        editDataSalesOrderCreateObject.clickNextButton();

        editDataSalesOrderCreateObject.showOriginAddressDialog();
        editDataSalesOrderCreateObject.setAddressName('City 17');
        editDataSalesOrderCreateObject.setContactName('Dr. Gordon Freeman');
        editDataSalesOrderCreateObject.setAddress1('Seattle');
        editDataSalesOrderCreateObject.setPhoneAreaCode('123');
        editDataSalesOrderCreateObject.setPhoneNumber('1234567');
        editDataSalesOrderCreateObject.saveEditAddress();

        editDataSalesOrderCreateObject.showDestinationAddressDialog();
        editDataSalesOrderCreateObject.setAddressName('City 17');
        editDataSalesOrderCreateObject.setContactName('Dr. Gordon Freeman');
        editDataSalesOrderCreateObject.setAddress1('Seattle');
        editDataSalesOrderCreateObject.setPhoneAreaCode('123');
        editDataSalesOrderCreateObject.setPhoneNumber('1234567');
        editDataSalesOrderCreateObject.saveEditAddress();
        editDataSalesOrderCreateObject.setLocation("MAIN STEEL");
        editDataSalesOrderCreateObject.clickNextButton();

        editDataSalesOrderCreateObject.setBol(bol);
        editDataSalesOrderCreateObject.setPo(2);
        editDataSalesOrderCreateObject.setPu(3);
        editDataSalesOrderCreateObject.setShipperRef(4);
        editDataSalesOrderCreateObject.setSoNumber(1);
        editDataSalesOrderCreateObject.setGlNumber(2);
        editDataSalesOrderCreateObject.setRequestedBy('customer 123');
        editDataSalesOrderCreateObject.setTrailerNumber(3);
        editDataSalesOrderCreateObject.setCargoValue(100500);
        editDataSalesOrderCreateObject.setDeliveryFromWindow('12:00 AM');
        editDataSalesOrderCreateObject.setDeliveryToWindow('12:30 AM');
        editDataSalesOrderCreateObject.clickNextButton();
        editDataSalesOrderCreateObject.clickdDoneButton();

        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/open');
        expect(browser().location().path()).toBe("/trackingBoard/open");
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(18);
    });

    xit('Should select proper address code', function() {
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        editDataSalesOrderCreateObject.setStatus('Open');
        editDataSalesOrderCreateObject.setCustomer('PLS');
        editDataSalesOrderCreateObject.setOrigin(65001);
        editDataSalesOrderCreateObject.setDestination(65001);

        var bol = "BOL#" + new Date().getTime();

        editDataSalesOrderCreateObject.setPro(1);
        editDataSalesOrderCreateObject.setPickupDate('08/10/2014');
        editDataSalesOrderCreateObject.setEstimateDeliveryDate('08/10/2014');
        editDataSalesOrderCreateObject.selectProduct('Budweiser 24545214SKU');
        editDataSalesOrderCreateObject.setWeight(50);
        editDataSalesOrderCreateObject.clickNextButton();
        editDataSalesOrderCreateObject.selectOriginName();
        editDataSalesOrderCreateObject.selectDestinationName();
        editDataSalesOrderCreateObject.selectLastDestinationCode();
        editDataSalesOrderCreateObject.clickNextButton();

        editDataSalesOrderCreateObject.setBol(bol);
        editDataSalesOrderCreateObject.setPo(2);
        editDataSalesOrderCreateObject.setPu(3);
        editDataSalesOrderCreateObject.setShipperRef(4);
        editDataSalesOrderCreateObject.setSoNumber(1);
        editDataSalesOrderCreateObject.setGlNumber(2);
        editDataSalesOrderCreateObject.setRequestedBy(100);
        editDataSalesOrderCreateObject.setTrailerNumber(3);
        editDataSalesOrderCreateObject.setCargoValue(100);
        editDataSalesOrderCreateObject.setDeliveryFromWindow('12:00 AM');
        editDataSalesOrderCreateObject.setDeliveryToWindow('12:30 AM');
        editDataSalesOrderCreateObject.clickNextButton();
        editDataSalesOrderCreateObject.clickdDoneButton();

        browser().navigateTo('#/trackingBoard/open');
        expect(browser().location().path()).toBe("/trackingBoard/open");
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(0);
        trackingBoardPageObject.open.setBol(bol);
        trackingBoardPageObject.open.clickSearch();
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(1);
    });

    it('Should change shipment status', function() {
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        editDataSalesOrderCreateObject.setStatus('Booked');
        expect(element('[data-ng-model="wizardData.shipment.status"] option:selected').text()).toBe('Booked');
        editDataSalesOrderCreateObject.setActualPickupDate('08/10/2014');
        expect(element('[data-ng-model="wizardData.shipment.status"] option:selected').text()).toBe('In-Transit');
        editDataSalesOrderCreateObject.setActualDeliveryDate('08/10/2014');
        expect(element('[data-ng-model="wizardData.shipment.status"] option:selected').text()).toBe('Delivered');

    });

    it('Should logout from application', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});