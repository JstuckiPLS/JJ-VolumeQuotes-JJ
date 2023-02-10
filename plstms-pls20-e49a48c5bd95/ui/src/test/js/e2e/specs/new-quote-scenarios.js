describe('New Quote functionality', function() {
    var $injector, buildOrderPageObject, loginLogoutPageObject, startQuotePageObject, finishOrderPageObject, selectCarrierPageObject,
        shipmentDetailsPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        buildOrderPageObject = $injector.get('BuildOrderPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        startQuotePageObject = $injector.get('RateQuotePageObject');
        finishOrderPageObject = $injector.get('FinishOrderPageObject');
        selectCarrierPageObject = $injector.get('SelectCarrierPageObject');
        shipmentDetailsPageObject = $injector.get('ShipmentDetailsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should open start quote page', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
        startQuotePageObject.setCustomer("PLS SHIPPER");
    });

    it('should create new quote', function() {
        startQuotePageObject.setOriginZip('12345');
        startQuotePageObject.setDestinationZip('44136');
        startQuotePageObject.setWeight(1000);
        startQuotePageObject.setCommodityClass(6);
        startQuotePageObject.selectProduct('Budweiser 24545214SKU');
        startQuotePageObject.clickGetQuote();
        expect(
                element(selectCarrierPageObject.selectCarrierControllerSelector,
                        'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        selectCarrierPageObject.selectFirstRow();
        selectCarrierPageObject.clickBook();

        buildOrderPageObject.setOriginAddressName('ADDR_NAME110');
        buildOrderPageObject.setDestinationAddressName('4 ADDR');
        buildOrderPageObject.setBillTo('Haynes International, INC.');
        buildOrderPageObject.setBillToPayTerms(1);
        buildOrderPageObject.setLocation("MAIN STEEL");
        buildOrderPageObject.clickNextButton();

        finishOrderPageObject.setRef(11);
        finishOrderPageObject.setPO(11);
        finishOrderPageObject.setPU(11);
        finishOrderPageObject.setSO(22);
        finishOrderPageObject.setGL(33);
        finishOrderPageObject.setCargoValue(100500);
        finishOrderPageObject.setRequestedBy(100500);
        finishOrderPageObject.setPickupNotes('Pickup Notes');
        finishOrderPageObject.setDeliveryNotes('Delivery Notes');
        finishOrderPageObject.setTrailer(44);

        var bol = "BOL#" + new Date().getTime();
        finishOrderPageObject.setBOL(bol);
        finishOrderPageObject.setDeliveryFromWindow('12:00 AM');
        finishOrderPageObject.setDeliveryToWindow('12:30 AM');
        finishOrderPageObject.clickNextButton();
        finishOrderPageObject.clickBookItButton();
        finishOrderPageObject.clickAgreeButton();
        finishOrderPageObject.clickBookItButton();
        

        expect(shipmentDetailsPageObject.getShipmentDialog().css("display")).not().toBe("none");
        shipmentDetailsPageObject.clickShipmentDetailsCloseButton();
        expect(shipmentDetailsPageObject.getShipmentDialog()).not().toBeDefined();

        browser().navigateTo('#/trackingBoard/undelivered');
        expect(browser().location().path()).toBe("/trackingBoard/undelivered");
        setValue(shipmentDetailsPageObject.bolSearchField, bol);
        expect(shipmentDetailsPageObject.getGridRowCount()).toBe(1);
    });

    it('should change zip Code when changed Address name', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
        startQuotePageObject.setCustomer("PLS SHIPPER");
        startQuotePageObject.setOriginZip('12345');
        startQuotePageObject.setDestinationZip('44136');
        
        expect(startQuotePageObject.getOriginZip()).toBe('12345');
        expect(startQuotePageObject.getDestinationZip()).toBe('44136');

        startQuotePageObject.setOriginAddressName('LT-00000003, 8 ADDR, Bill Johnson, 1205 DEARBORN DR, COLUMBUS, OH, 43085');
        startQuotePageObject.setDestinationAddressName('LT-00000003, 8 ADDR, Bill Johnson, 1205 DEARBORN DR, COLUMBUS, OH, 43085');

        expect(startQuotePageObject.getOriginZip()).toBe('43085');
        expect(startQuotePageObject.getDestinationZip()).toBe('43085');

        startQuotePageObject.setOriginZip('12345');
        startQuotePageObject.setDestinationZip('44136');

        expect(startQuotePageObject.getDestinationAddressName()).toBe('');
        expect(startQuotePageObject.getOriginAddressName()).toBe('');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});