describe('Unit test for TrackingBoardAlertsController', function() {
    var scope;
    var alertService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        alertService = $injector.get('TrackingBoardAlertService');

        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        var controller = $injector.get('$controller');
        controller('TrackingBoardAlertsController', {
            '$scope': scope,
            'TrackingBoardAlertService': alertService
        });
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(0);
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should init model', function() {
        spyOn(scope, 'loadAlerts');
        scope.init();

        expect(scope.loadAlerts).toHaveBeenCalled();
    });

    it('should load alerts', function() {
        spyOn(alertService, 'alert');
        scope.loadAlerts();

        expect(alertService.alert).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should edit shipment', function() {
        var shipment = {
            id: 1, 
            newAlert: {newAlert: alert}
        };
        scope.selectedShipments.push(shipment);
        spyOn(alertService, 'acknowledgeAlerts');
        spyOn(scope, 'editShipmentDetails');

        scope.editShipment();

        expect(alertService.acknowledgeAlerts).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function));
        expect(scope.editShipmentDetails).toHaveBeenCalledWith(shipment);
    });

    it('should check close handler', function() {
        scope.selectedShipments.push({id: 1});
        spyOn(scope, 'loadAlerts');

        scope.closeHandler();

        expect(scope.loadAlerts).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should return normal font style for entity row', function() {
        var entity = {};
        expect('normal').toEqual(scope.getRowFontStyle(entity));
    });

    it('should return bold font style for entity row', function() {
        var entity = {newAlert: {}};
        expect('bold').toEqual(scope.getRowFontStyle(entity));
    });

    it('should prove "NDL" alert type should be of the red color', function() {
        var entity = {
            alertTypes: 'NDL, whatever'
        };
        expect(scope.getRowBackgroundColorStyle(entity)).toBeTruthy();
    });

    it('should prove "30M" alert type should be of the red color', function() {
        var entity = {
            alertTypes: '30M, whatever'
        };
        expect(scope.getRowBackgroundColorStyle(entity)).toBeTruthy();
    });

    it('should prove any ather types of alerts should not be of red color', function() {
        var entity = {
            alertTypes: 'wherever, whatever'
        };
        expect(scope.getRowBackgroundColorStyle(entity)).toBeFalsy();
    });

    it('should open terminal info dialog', function() {
        spyOn(scope, '$broadcast');
        var shipmentId = 1;
        scope.openTerminalInfoModalDialog(shipmentId);

        expect(scope.$broadcast).toHaveBeenCalledWith('event:openTerminalInfoDialog', shipmentId);
    });

    it('should check shipments grid', function() {
        expect(scope.shipmentsGrid).toBeDefined();
        expect(scope.shipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.shipmentsGrid.data).toEqual('shipments');
        expect(scope.shipmentsGrid.multiSelect).toBeFalsy();
        expect(scope.shipmentsGrid.selectedItems).toEqual(scope.selectedShipments);
        expect(scope.shipmentsGrid.primaryKey).toEqual('id');
        expect(scope.shipmentsGrid.rowTemplate).toEqual("<div data-ng-class=\"{'text-error' : getRowBackgroundColorStyle(row.entity)}\">"
                + "<div data-ng-style=\"{ 'cursor': row.cursor, 'font-weight': getRowFontStyle(row.entity) }\""
                + " ng-repeat=\"col in renderedColumns\" ng-class=\"col.colIndex()\" class=\"ngCell {{col.cellClass}}\" ng-cell></div></div>");

        expect(scope.shipmentsGrid.columnDefs.length).toBe(23);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');
        expect(scope.shipmentsGrid.columnDefs[0].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('network');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Business Unit');
        expect(scope.shipmentsGrid.columnDefs[1].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('accountExecutive');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('Account Executive');
        expect(scope.shipmentsGrid.columnDefs[2].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('id');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('Load ID');
        expect(scope.shipmentsGrid.columnDefs[3].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('bol');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('BOL');
        expect(scope.shipmentsGrid.columnDefs[4].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('pro');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('Pro#');
        expect(scope.shipmentsGrid.columnDefs[5].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('pieces');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('QTY');
        expect(scope.shipmentsGrid.columnDefs[6].width).toEqual('2%');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('weight');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Total Weight');
        expect(scope.shipmentsGrid.columnDefs[7].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Customer');
        expect(scope.shipmentsGrid.columnDefs[8].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('carrierName');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('Carrier');
        expect(scope.shipmentsGrid.columnDefs[9].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[9].cellTemplate).toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">'+
                '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>'+
                '</div>');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('integrationType');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('EDI');
        expect(scope.shipmentsGrid.columnDefs[10].width).toEqual('3%');
        expect(scope.shipmentsGrid.columnDefs[10].cellFilter).toEqual('integrationTypeFilter');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('shipmentStatus');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Status');
        expect(scope.shipmentsGrid.columnDefs[11].width).toEqual('2%');
        expect(scope.shipmentsGrid.columnDefs[11].cellFilter).toEqual('shipmentStatus');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('pickupDate');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Pickup Date');
        expect(scope.shipmentsGrid.columnDefs[12].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[12].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('dispatchedDate');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('Dispatched Date');
        expect(scope.shipmentsGrid.columnDefs[13].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[13].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('estimatedDelivery');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Est Delivery Date');
        expect(scope.shipmentsGrid.columnDefs[14].width).toEqual('4%');
        expect(scope.shipmentsGrid.columnDefs[14].cellTemplate).toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<span data-ng-bind="_.isUndefined(row.entity.estimatedDelivery) ?\'N/A\':row.entity.estimatedDelivery|date:appDateFormat">' +
                '</span></div>');

        expect(scope.shipmentsGrid.columnDefs[15].field).toEqual('shipper');
        expect(scope.shipmentsGrid.columnDefs[15].displayName).toEqual('Shipper');
        expect(scope.shipmentsGrid.columnDefs[15].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[16].field).toEqual('originAddress');
        expect(scope.shipmentsGrid.columnDefs[16].displayName).toEqual('Origin');
        expect(scope.shipmentsGrid.columnDefs[16].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[16].cellFilter).toEqual('zip');

        expect(scope.shipmentsGrid.columnDefs[17].field).toEqual('consignee');
        expect(scope.shipmentsGrid.columnDefs[17].displayName).toEqual('Consignee');
        expect(scope.shipmentsGrid.columnDefs[17].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[18].field).toEqual('destinationAddress');
        expect(scope.shipmentsGrid.columnDefs[18].displayName).toEqual('Destination');
        expect(scope.shipmentsGrid.columnDefs[18].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[18].cellFilter).toEqual('zip');

        expect(scope.shipmentsGrid.columnDefs[19].field).toEqual('alertTypes');
        expect(scope.shipmentsGrid.columnDefs[19].displayName).toEqual('Alert');
        expect(scope.shipmentsGrid.columnDefs[19].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[20].field).toEqual('createdBy');
        expect(scope.shipmentsGrid.columnDefs[20].displayName).toEqual('Created By');
        expect(scope.shipmentsGrid.columnDefs[20].width).toEqual('5%');

        expect(scope.shipmentsGrid.columnDefs[21].field).toEqual('createdDateTime');
        expect(scope.shipmentsGrid.columnDefs[21].displayName).toEqual('Created Date');
        expect(scope.shipmentsGrid.columnDefs[21].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[21].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[22].field).toEqual('pickupWindowEnd');
        expect(scope.shipmentsGrid.columnDefs[22].displayName).toEqual('Late Pickup Date/Time');
        expect(scope.shipmentsGrid.columnDefs[22].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[22].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.shipmentsGrid.plugins.length).toBe(4);
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });
});