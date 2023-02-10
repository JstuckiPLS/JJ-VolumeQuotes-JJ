describe('Shipment Entry functionality', function() {
    var $injector, buildOrderPageObject, loginLogoutPageObject, startQuotePageObject, finishOrderPageObject,
        selectCarrierPageObject, shipmentDetailsPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        shipmentEntryPageObject = $injector.get('ShipmentEntryPageObject');
        trackingBoardPageObject = $injector.get('TrackingBoardPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should open shipment entry page', function() {
        browser().navigateTo('#/shipment-entry');
        expect(browser().location().path()).toBe("/shipment-entry");
        shipmentEntryPageObject.setCustomer("PLS SHIPPER");
        shipmentEntryPageObject.setOriginAddressName('ADDR_NAME110, Alethea Hypolite, ONE RIVER ROAD');
        shipmentEntryPageObject.setDestinationAddressName('4 ADDR, Jack Davis, NO ADDRESS');
        shipmentEntryPageObject.setWeight(1000);
        shipmentEntryPageObject.setCommodityClass(6);
        shipmentEntryPageObject.selectProduct('Budweiser 24545214SKU');
        shipmentEntryPageObject.clickGetQuote();
        expect(shipmentEntryPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        shipmentEntryPageObject.setLocation("MAIN STEEL");
        shipmentEntryPageObject.setRef(11);
        shipmentEntryPageObject.setPO(11);
        shipmentEntryPageObject.setPU(11);
        shipmentEntryPageObject.setSO(22);
        shipmentEntryPageObject.setGL(33);
        shipmentEntryPageObject.setRequestedBy('Test text');
        shipmentEntryPageObject.setPickupNotes('Pickup Notes');
        shipmentEntryPageObject.setDeliveryNotes('Delivery Notes');
        shipmentEntryPageObject.setTrailer(44);
        shipmentEntryPageObject.setBOL('testBol');
        shipmentEntryPageObject.setCargoValue(100500);

        expect(shipmentEntryPageObject.getLoadIdText()).toBe("");
        shipmentEntryPageObject.clickAgreeButton();
        shipmentEntryPageObject.clickBookItButton();
        expect(shipmentEntryPageObject.getShipmentDialog().css("display")).not().toBe("none");
        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/alerts');
        shipmentEntryPageObject.clickFirstRow();
        trackingBoardPageObject.open.clickButtonShipmentEdit();
        expect(shipmentEntryPageObject.getLoadIdText()).toMatch(/[\d]+/);
    });

    it('check ProfileId value', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/shipment-entry');
        expect(browser().location().path()).toBe("/shipment-entry");
        shipmentEntryPageObject.setCustomer("PLS SHIPPER");
        shipmentEntryPageObject.setOriginAddressName('ADDR_NAME110, Alethea Hypolite, ONE RIVER ROAD');
        shipmentEntryPageObject.setDestinationAddressName('4 ADDR, Jack Davis, NO ADDRESS');
        shipmentEntryPageObject.setWeight(1000);
        shipmentEntryPageObject.setCommodityClass(6);
        shipmentEntryPageObject.selectProduct('Budweiser 24545214SKU');
        shipmentEntryPageObject.clickGetQuote();
        expect(shipmentEntryPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        shipmentEntryPageObject.clickBlockedRow();
        expect(shipmentEntryPageObject.getProfileIdVal()).toBe('Profile ID: 50 (LITECZ02_20070903_20718-RWM)');
        shipmentEntryPageObject.setCostOverride(23);
        expect(shipmentEntryPageObject.getProfileIdVal()).toBe('Profile ID: 50 (LITECZ02_20070903_20718-RWM)');
        shipmentEntryPageObject.setRevenueOverride(3);
        expect(shipmentEntryPageObject.getProfileIdVal()).toBe('Profile ID: 50 (LITECZ02_20070903_20718-RWM)');
    });

    it('should copy load and book it', function() {
        browser().navigateTo('#/shipment-entry');
        expect(browser().location().path()).toBe("/shipment-entry");
        shipmentEntryPageObject.setCustomer("PLS SHIPPER");
        shipmentEntryPageObject.clickCopyShipmentButton();
        expect(shipmentEntryPageObject.getCopyFromDialog().css("display")).not().toBe("none");
        shipmentEntryPageObject.searchByBOL('testBol');
        shipmentEntryPageObject.clickFirstRow();
        shipmentEntryPageObject.clickCopyButton();
        expect(shipmentEntryPageObject.getCopyFromDialog().css("display")).toBe("block");
        expect(shipmentEntryPageObject.getOriginCode()).toBe('109 ADDRESS');
        expect(shipmentEntryPageObject.getDestinationCode()).toBe('LT-00000002');
        expect(shipmentEntryPageObject.getOriginAddress1()).toBe('ONE RIVER ROAD');
        expect(shipmentEntryPageObject.getDestinationAddress1()).toBe('NO ADDRESS');
        expect(shipmentEntryPageObject.getMaterialsGridRowCount()).toBe(1);
        shipmentEntryPageObject.clickGetQuoteButton();
        expect(shipmentEntryPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        shipmentEntryPageObject.setLocation("MAIN STEEL");
        shipmentEntryPageObject.setRef('q1');
        shipmentEntryPageObject.setPO('q2');
        shipmentEntryPageObject.setPU('q3');
        shipmentEntryPageObject.setSO('q4');
        shipmentEntryPageObject.setGL('q5');
        shipmentEntryPageObject.setRequestedBy('Test');
        shipmentEntryPageObject.setPickupNotes('Pickup Notes');
        shipmentEntryPageObject.setDeliveryNotes('Delivery Notes');
        shipmentEntryPageObject.setShippingLabelNotes('Shipping Label Notes');
        shipmentEntryPageObject.setTrailer('q6');
        shipmentEntryPageObject.setBOL('q7');
        shipmentEntryPageObject.setCargoValue(100);
        shipmentEntryPageObject.clickAgreeButton();
        shipmentEntryPageObject.clickBookItButton();

        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/alerts');
        shipmentEntryPageObject.clickFirstRow();
        trackingBoardPageObject.open.clickButtonShipmentEdit();
        expect(shipmentEntryPageObject.getLoadIdText()).toMatch(/[\d]+/);
    });

    it('should edit load', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/undelivered');
        trackingBoardPageObject.undelivered.searchByBOL('testBol');
        trackingBoardPageObject.undelivered.clickFirstRow();
        trackingBoardPageObject.undelivered.clickButtonShipmentEdit();
        shipmentEntryPageObject.setRef('ref1');
        shipmentEntryPageObject.setPO('po2');
        shipmentEntryPageObject.setPU('pu3');
        shipmentEntryPageObject.setSO('so4');
        shipmentEntryPageObject.setGL('gl5');
        shipmentEntryPageObject.setRequestedBy('changed Test text')
        shipmentEntryPageObject.setPickupNotes('Pickup Notes edit');
        shipmentEntryPageObject.setDeliveryNotes('Delivery Notes edit');
        shipmentEntryPageObject.setShippingLabelNotes('Shipping Label edit');
        shipmentEntryPageObject.setTrailer('trailer6');
        var bol = 'testBol1' + new Date().getTime();
        shipmentEntryPageObject.setBOL(bol);
        shipmentEntryPageObject.setCargoValue(500);
        shipmentEntryPageObject.clickBookItButton();
        expect(shipmentEntryPageObject.getShipmentEntrySaveDialog().css("display")).not().toBe("none");
        shipmentEntryPageObject.clickOkShipmentEntrySaveDialogButton();
        sleep(1);
        expect(shipmentEntryPageObject.getShipmentDialog().css("display")).not().toBe("none");
        shipmentEntryPageObject.clickCloseShipmentDetailsButton();
        sleep(1);
        disableLocationChangeCheck();
        expect(browser().location().path()).toBe("/trackingBoard/undelivered");
        trackingBoardPageObject.undelivered.searchByBOL(bol);
        expect(trackingBoardPageObject.undelivered.getShipmentsRowsCount()).toBe(1);
    });

    it('should edit load in Open status without selected carrier', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/trackingBoard/open');
        expect(browser().location().path()).toBe("/trackingBoard/open");
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(18);
        trackingBoardPageObject.open.searchByShipper('City');
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(1);
        trackingBoardPageObject.open.clickFirstRow();
        trackingBoardPageObject.open.clickButtonShipmentEdit();
        expect(shipmentEntryPageObject.getPropositionsGridRowCount()).toBe(0);
        var bol = 'testBol2' + new Date().getTime();
        shipmentEntryPageObject.setBOL(bol);
        shipmentEntryPageObject.clickBookItButton();
        expect(shipmentEntryPageObject.getShipmentEntrySuccessWarningDialog().css("display")).not().toBe("none");
        shipmentEntryPageObject.clickOkShipmentEntrySuccessWarningDialogButton();
        sleep(1);
        disableLocationChangeCheck();
        expect(browser().location().path()).toBe("/trackingBoard/open");
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(18);
        trackingBoardPageObject.open.searchByBOL(bol);
        expect(trackingBoardPageObject.open.getShipmentsRowsCount()).toBe(1);
    });

    it('user should not be able to book load with proposal marked as "Exclude from booking"', function() {
        browser().navigateTo('#/shipment-entry');
        expect(browser().location().path()).toBe("/shipment-entry");
        shipmentEntryPageObject.setCustomer("PLS SHIPPER");
        shipmentEntryPageObject.setOriginAddressName('ADDR_NAME110, Alethea Hypolite, ONE RIVER ROAD');
        shipmentEntryPageObject.setDestinationAddressName('4 ADDR, Jack Davis, NO ADDRESS');
        shipmentEntryPageObject.setWeight(1000);
        shipmentEntryPageObject.setCommodityClass(6);
        shipmentEntryPageObject.selectProduct('Budweiser 24545214SKU');
        shipmentEntryPageObject.clickGetQuote();
        expect(shipmentEntryPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        shipmentEntryPageObject.clickBlockedRow();
        shipmentEntryPageObject.setLocation("MAIN STEEL");
        shipmentEntryPageObject.setRef('q1');
        shipmentEntryPageObject.setPO('q2');
        shipmentEntryPageObject.setPU('q3');
        shipmentEntryPageObject.setSO('q4');
        shipmentEntryPageObject.setGL('q5');
        shipmentEntryPageObject.setPickupNotes('Pickup Notes');
        shipmentEntryPageObject.setDeliveryNotes('Delivery Notes');
        shipmentEntryPageObject.setShippingLabelNotes('Shipping Label Notes');
        shipmentEntryPageObject.setTrailer('q6');
        shipmentEntryPageObject.setBOL('q7');
        shipmentEntryPageObject.clickAgreeButton();
        expect(shipmentEntryPageObject.getBookItButtonDisplay()).toBe('disabled');
    });
});