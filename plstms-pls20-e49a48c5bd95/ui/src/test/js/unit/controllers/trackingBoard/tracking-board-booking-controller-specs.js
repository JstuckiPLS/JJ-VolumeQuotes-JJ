describe('Unit test for TrackingBoardBookedController', function() {
    var scope;
    var trackingBoardService;
    var shipmentStatus;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        trackingBoardService = $injector.get('TrackingBoardService');
        shipmentStatus = $injector.get('ShipmentStatus');

        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        var controller = $injector.get('$controller');
        controller('TrackingBoardBookedController', {
            '$scope': scope,
            'TrackingBoardService': trackingBoardService,
            'ShipmentStatus': shipmentStatus
        });
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(0);
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should init booked shipments', function() {
        spyOn(scope, 'loadBookedShipments');
        scope.init();

        expect(scope.loadBookedShipments).toHaveBeenCalled();
    });

    it('should check close handler', function() {
        spyOn(scope, 'loadBookedShipments');
        scope.closeHandler();

        expect(scope.loadBookedShipments).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should load booked shipments', function() {
        spyOn(trackingBoardService, 'booked');
        scope.loadBookedShipments();

        expect(trackingBoardService.booked).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should view shipment', function() {
        spyOn(scope, 'viewShipmentDetails');
        scope.selectedShipments.push({});
        scope.viewShipment();

        expect(scope.viewShipmentDetails).toHaveBeenCalledWith({});
    });

    it('should open terminal dialog', function() {
        spyOn(scope, '$broadcast');
        scope.openTerminalInfoModalDialog(3);

        expect(scope.$broadcast).toHaveBeenCalledWith('event:openTerminalInfoDialog', 3);
    });

    it('should dispatch shipment', function() {
        scope.selectedShipments.push({id: 1, apiCapable: false});
        spyOn(scope.$root, '$emit');
        spyOn(shipmentStatus, 'dispatch');
        scope.dispatchShipment();

        expect(scope.$root.$emit).not.toHaveBeenCalled();
        expect(shipmentStatus.dispatch).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function), jasmine.any(Function));
    });

    it('should not dispatch shipment and show alert notification', function() {
        scope.selectedShipments.push({id: 1, apiCapable: true});
        spyOn(scope.$root, '$emit');
        spyOn(shipmentStatus, 'dispatch');
        scope.dispatchShipment();

        expect(scope.$root.$emit).toHaveBeenCalledWith('event:application-warning', 'Dispatch shipment',
                'Shipment can\'t be dispatched automatically. ');
        expect(shipmentStatus.dispatch).not.toHaveBeenCalled();
    });

    it('should return normal font style for api capable entity', function() {
        var entity = {apiCapable: true};
        expect('normal').toEqual(scope.getRowFontStyle(entity));
    });

    it('should return bold font style for non api capable entity', function() {
        var entity = {apiCapable: false};
        expect('bold').toEqual(scope.getRowFontStyle(entity));
    });

    it('should check shipments grid', function() {
        expect(scope.shipmentsGrid).toBeDefined();
        expect(scope.shipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.shipmentsGrid.data).toEqual('shipments');
        expect(scope.shipmentsGrid.multiSelect).toBeFalsy();
        expect(scope.shipmentsGrid.selectedItems.length).toEqual(0);
        expect(scope.shipmentsGrid.rowTemplate).toEqual("<div data-ng-style=\"{ 'cursor': row.cursor, 'font-weight': getRowFontStyle(row.entity)  }\" data-ng-repeat=\"col " +
                "in renderedColumns\" ng-class=\"col.colIndex()\" class=\"ngCell {{col.cellClass}}\" ng-cell></div>");
        expect(scope.shipmentsGrid.columnDefs.length).toBe(18);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');
        expect(scope.shipmentsGrid.columnDefs[0].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('network');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Business Unit');
        expect(scope.shipmentsGrid.columnDefs[1].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('accountExecutive');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('Account Executive');
        expect(scope.shipmentsGrid.columnDefs[2].width).toEqual('5%');

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('id');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('Load ID');
        expect(scope.shipmentsGrid.columnDefs[3].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('bol');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('BOL');
        expect(scope.shipmentsGrid.columnDefs[4].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('pieces');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('QTY');
        expect(scope.shipmentsGrid.columnDefs[5].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('weight');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('Total Weight');
        expect(scope.shipmentsGrid.columnDefs[6].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Customer');
        expect(scope.shipmentsGrid.columnDefs[7].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('carrierName');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Carrier');
        expect(scope.shipmentsGrid.columnDefs[8].width).toEqual('7%');
        expect(scope.shipmentsGrid.columnDefs[8].cellTemplate).toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">'+
                '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>'+
                '</div>');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('integrationType');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('EDI');
        expect(scope.shipmentsGrid.columnDefs[9].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[9].cellFilter).toEqual('integrationTypeFilter');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('pickupDate');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('Pickup Date');
        expect(scope.shipmentsGrid.columnDefs[10].width).toEqual('7%');
        expect(scope.shipmentsGrid.columnDefs[10].cellFilter).toEqual('date:wideAppDateFormat');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('shipper');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Shipper');
        expect(scope.shipmentsGrid.columnDefs[11].width).toEqual('9%');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('origin');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Origin');
        expect(scope.shipmentsGrid.columnDefs[12].width).toEqual('7%');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('consignee');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('Consignee');
        expect(scope.shipmentsGrid.columnDefs[13].width).toEqual('8%');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('destination');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Destination');
        expect(scope.shipmentsGrid.columnDefs[14].width).toEqual('7%');

        expect(scope.shipmentsGrid.columnDefs[15].field).toEqual('createdBy');
        expect(scope.shipmentsGrid.columnDefs[15].displayName).toEqual('Created By');
        expect(scope.shipmentsGrid.columnDefs[15].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[16].field).toEqual('createdDate');
        expect(scope.shipmentsGrid.columnDefs[16].displayName).toEqual('Created Date');
        expect(scope.shipmentsGrid.columnDefs[16].width).toEqual('4%');
        expect(scope.shipmentsGrid.columnDefs[16].cellFilter).toEqual('date:appDateFormat');

        expect(scope.shipmentsGrid.columnDefs[17].field).toEqual('pickupWindowEnd');
        expect(scope.shipmentsGrid.columnDefs[17].displayName).toEqual('Late Pickup Date/Time');
        expect(scope.shipmentsGrid.columnDefs[17].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[17].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.shipmentsGrid.plugins.length).toBe(4);
        expect(scope.shipmentsGrid.filterOptions.filterText).toEqual('');
        expect(scope.shipmentsGrid.filterOptions.useExternalFilter).toBeFalsy();
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });
});