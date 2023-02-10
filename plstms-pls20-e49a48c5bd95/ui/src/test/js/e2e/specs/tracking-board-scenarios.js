/**
 * Tracking Board scenarios.
 * 
 * @author Aleksandr Nalapko
 */
describe('Tracking Board scenarios', function() {
    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        trackingBoard = $injector.get('TrackingBoardPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');

        openPage = trackingBoard.open;
        unbilledPage = trackingBoard.unbilled;
        undeliveredPage = trackingBoard.undelivered;
        bookedPage = trackingBoard.booked;
        alertsPage = trackingBoard.alerts;
        allPage = trackingBoard.all;
        shipmentEntry = trackingBoard.shipmentEntry
    });

    it('should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('checks open shipment page', function() {
        browser().navigateTo('#/trackingBoard/open');
        expect(element(openPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
        openPage.setFromDate('04/28/2008');
        openPage.setToDate('04/28/2008');
        openPage.clickSearch();
        sleep(1);
        expect(openPage.getShipmentsRowsCount()).toBe(1);
        openPage.clickClear();
        openPage.setFromDate('04/28/2008');
        openPage.setToDate('05/03/2008');
        openPage.clickSearch();
        sleep(1);
        expect(openPage.getShipmentsRowsCount()).toBe(3);
        openPage.clickRow();
        openPage.clickView();
        expect(
                element('[data-ng-controller="EditSalesOrderCtrl"]', 'Div with ng-controller="EditSalesOrderCtrl"')
                        .count()).toBe(1);
        openPage.clickCancel();
        sleep(1);
        openPage.clickButtonShipmentEdit();
        expect(browser().location().path()).toMatch("shipment-entry");

        disableLocationChangeCheck();
        shipmentEntry.clickCancelButton();
        sleep(1);
        expect(element(openPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
    });

    it('checks unbilled shipment page', function() {
        browser().navigateTo('#/trackingBoard/unbilled');
        expect(element(unbilledPage.controller, 'Div with ng-controller="TrackingBoardUnbilledController"').count())
                .toBe(1);
        unbilledPage.clickRow();
        unbilledPage.clickButtonShipmentEdit();
        expect(browser().location().path()).toMatch("shipment-entry");

        disableLocationChangeCheck();
        shipmentEntry.clickCancelButton();
        sleep(1);
        expect(element(unbilledPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
    });

    it('checks undelivered shipment page', function() {
        browser().navigateTo('#/trackingBoard/undelivered');
        expect(
                element(undeliveredPage.controller, 'Div with ng-controller="TrackingBoardUndeliveredController"')
                        .count()).toBe(1);
        undeliveredPage.clickRow();
        undeliveredPage.clickButtonShipmentEdit();
        expect(browser().location().path()).toMatch("shipment-entry");

        disableLocationChangeCheck();
        shipmentEntry.clickCancelButton();
        sleep(1);
        expect(element(undeliveredPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
    });

    it('checks booked shipment page', function() {
        browser().navigateTo('#/trackingBoard/booked');
        expect(element(bookedPage.controller, 'Div with ng-controller="TrackingBoardBookedController"').count())
                .toBe(1);
        bookedPage.clickRow();
        bookedPage.clickButtonShipmentEdit();
        expect(browser().location().path()).toMatch("shipment-entry");

        disableLocationChangeCheck();
        shipmentEntry.clickCancelButton();
        sleep(1);
        expect(element(bookedPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
    });

    it('checks all shipment page', function() {
        browser().navigateTo('#/trackingBoard/all');
        expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardAllShipmentsController"').count())
                .toBe(1);
        allPage.setCustomer('PLS SHIPPER');
        allPage.selectDate('1');
        allPage.setFromDate('01/14/2012');
        allPage.setToDate('02/14/2012');
        allPage.clickButtonSearch();
        expect(allPage.getRowCount()).toBe(7);
        allPage.selectDate('2');
        allPage.clickButtonSearch();
        expect(allPage.getRowCount()).toBe(7);
        allPage.clickRow();
        allPage.clickButtonShipmentEdit();
        expect(browser().location().path()).toMatch("shipment-entry");

        disableLocationChangeCheck();
        shipmentEntry.clickCancelButton();
        sleep(1);
        expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardOpenController"').count()).toBe(1);
    });

    it('clear all shipment page', function() {
        allPage.setCarrier('FXNL:FEDEX FREIGHT ECONOMY');
        allPage.setCustomer('PLS SHIPPER');
        allPage.setOrigin('100 PALMS, CA, 92274');
        allPage.setDest('100 PALMS, CA, 92274');
        allPage.setBOL('bol');
        allPage.setPro('pro');
        allPage.setFromDate('12/14/2014');
        allPage.setToDate('12/14/2014');
        expect(element(allPage.inputCarrier).val()).toBe('FXNL:FEDEX FREIGHT ECONOMY');
        expect(element(allPage.inputCustomer).val()).toBe('PLS SHIPPER');
        expect(element(allPage.inputOrigin).val()).toBe('100 PALMS, CA, 92274');
        expect(element(allPage.inputDest).val()).toBe('100 PALMS, CA, 92274');
        expect(element(allPage.inputBOL).val()).toBe('bol');
        expect(element(allPage.inputPro).val()).toBe('pro');
        expect(element(allPage.inputFromDate).val()).toBe('12/14/2014');
        expect(element(allPage.inputToDate).val()).toBe('12/14/2014');

        allPage.clickClearButton();
        allPage.clickClearButton();
        expect(element(allPage.inputCarrier).val()).toBe('');
        expect(element(allPage.inputCustomer).val()).toBe('');
        expect(element(allPage.inputOrigin).val()).toBe('');
        expect(element(allPage.inputDest).val()).toBe('');
        expect(element(allPage.inputBOL).val()).toBe('');
        expect(element(allPage.inputPro).val()).toBe('');
        expect(element(allPage.inputFromDate).val()).toBe('');
        expect(element(allPage.inputToDate).val()).toBe('');
    });

    it('check mandatory fields fromDate and toDate after entering the bol and carrier', function() {
        allPage.setCarrier('FXNL:FEDEX FREIGHT ECONOMY');
        expect(angularElement(allPage.inputFromDate)).toHaveClass('ng-invalid');
        expect(angularElement(allPage.inputToDate)).toHaveClass('ng-invalid');
        allPage.setBOL('bol');
        expect(angularElement(allPage.inputFromDate)).toHaveClass('ng-valid');
        expect(angularElement(allPage.inputToDate)).toHaveClass('ng-valid');
    });

    it('checks email history tab', function() {
      browser().navigateTo('#/trackingBoard/all');
      expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardAllShipmentsController"').count()).toBe(1);
      allPage.setCarrier('');
      allPage.setBOL('');
      allPage.setLoadId('10');
      allPage.clickButtonSearch();
      sleep(1);
      expect(allPage.getRowCount()).toBe(1);
      allPage.clickRow();
      allPage.clickView();
      expect(
              element('[data-ng-controller="EditSalesOrderCtrl"]', 'Div with ng-controller="EditSalesOrderCtrl"')
                      .count()).toBe(1);
      allPage.clickEmailHistoryTab();
      expect(element('div[data-pls-emails-history] div[ng-row]').count()).toBe(3);
  });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});