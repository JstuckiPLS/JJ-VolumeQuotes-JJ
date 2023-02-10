describe('Unit test for TrackingBoardUndeliveredController', function() {
    var scope, parentScope;
    var permissions = ['VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY'];

    var shipments = [{
        "bolNumber":"123","carrier":"KINGSWAY TRANSPORT","createdDate":"2010-12-29",
        "customerId":1,"revenue":225.5211,"margin":20.501, "cost" : 14.811
    }];

    var trackingBoardService = {
        undelivered : function(params, success) {
            success(shipments);
        }
    };

    beforeEach(module('plsApp'));
    beforeEach(inject(function($rootScope, $injector, $controller) {
        var rootScope = $injector.get('$rootScope');
        rootScope.authData = new AuthData({personId: 1, firstName: 'sysadmin', fullName: 'sysadmin', plsUser: true,
            privilegies: permissions});

        parentScope = $rootScope.$new();
        spyOn(parentScope, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        $controller('TrackingBoardController', {
            $scope: parentScope
        });
        scope = parentScope.$new();

        var controller = $injector.get('$controller');
        controller('TrackingBoardUndeliveredController', {
            '$scope': scope,
            'TrackingBoardService': trackingBoardService
        });
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(1);
        expect(scope.selectedShipments.length).toBe(0);
        expect(scope.personId).toEqual(1);
    });

    it('should get shipments', function() {
        scope.shipments = undefined;
        spyOn(trackingBoardService, 'undelivered').and.callThrough();
        scope.getShipments();
        expect(trackingBoardService.undelivered).toHaveBeenCalledWith({}, jasmine.any(Function));
        expect(scope.shipments.length).toBe(1);
        expect(scope.totals.totalRevenue).toBe('225.52');
        expect(scope.totals.totalCost).toBe('14.81');
        expect(scope.totals.totalMargin).toBe('20.50');
    });

    it('should view shipment', function() {
        spyOn(scope, 'viewShipmentDetails');
        scope.selectedShipments.push({});
        scope.viewShipment();

        expect(scope.viewShipmentDetails).toHaveBeenCalledWith(scope.selectedShipments[0]);
    });

    it('should check close handler', function() {
        spyOn(scope, 'getShipments');
        scope.closeHandler();
        expect(scope.getShipments).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should open terminal dialog', function() {
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
        expect(scope.shipmentsGrid.selectedItems.length).toEqual(0);
        expect(scope.shipmentsGrid.columnDefs.length).toBe(26);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');

        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('id');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Load ID');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('bol');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('BOL');

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('pieces');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('QTY');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('weight');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('Total Weight');

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('pro');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('PRO #');

        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('network');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('Business Unit');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('accountExecutive');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Account Executive');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Customer');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('carrierName');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('Carrier');
        expect(scope.shipmentsGrid.columnDefs[9].cellTemplate)
            .toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">'+
                        '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>'+
                    '</div>');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('integrationType');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('EDI');
        expect(scope.shipmentsGrid.columnDefs[10].cellFilter).toEqual('integrationTypeFilter');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('status');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Status');
        expect(scope.shipmentsGrid.columnDefs[11].cellFilter).toEqual('shipmentStatus');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('estimatedDelivery');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Est Delivery');
        expect(scope.shipmentsGrid.columnDefs[12].cellTemplate).toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<span data-ng-bind="_.isUndefined(row.entity.estimatedDelivery) ?\'N/A\':row.entity.estimatedDelivery|date:appDateFormat">' +
                '</span></div>');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('shipper');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('Shipper');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('origin');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Origin');

        expect(scope.shipmentsGrid.columnDefs[15].field).toEqual('consignee');
        expect(scope.shipmentsGrid.columnDefs[15].displayName).toEqual('Consignee');

        expect(scope.shipmentsGrid.columnDefs[16].field).toEqual('destination');
        expect(scope.shipmentsGrid.columnDefs[16].displayName).toEqual('Destination');

        expect(scope.shipmentsGrid.columnDefs[17].field).toEqual('createdBy');
        expect(scope.shipmentsGrid.columnDefs[17].displayName).toEqual('Created By');

        expect(scope.shipmentsGrid.columnDefs[18].field).toEqual('createdDate');
        expect(scope.shipmentsGrid.columnDefs[18].displayName).toEqual('Created Date');
        expect(scope.shipmentsGrid.columnDefs[18].cellFilter).toEqual('date:wideAppDateFormat');

        expect(scope.shipmentsGrid.columnDefs[19].field).toEqual('pickupWindowEnd');
        expect(scope.shipmentsGrid.columnDefs[19].displayName).toEqual('Late Pickup Date/Time');
        expect(scope.shipmentsGrid.columnDefs[19].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[20].field).toEqual('dispatchedDate');
        expect(scope.shipmentsGrid.columnDefs[20].displayName).toEqual('Dispatched Date');
        expect(scope.shipmentsGrid.columnDefs[20].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[21].field).toEqual('glNumber');
        expect(scope.shipmentsGrid.columnDefs[21].displayName).toEqual('GL #');

        expect(scope.shipmentsGrid.columnDefs[22].field).toEqual('srNumber');
        expect(scope.shipmentsGrid.columnDefs[22].displayName).toEqual('Shipper Reference #');

        expect(scope.shipmentsGrid.columnDefs[23].field).toEqual('poNumber');
        expect(scope.shipmentsGrid.columnDefs[23].displayName).toEqual('PO #');

        expect(scope.shipmentsGrid.columnDefs[24].field).toEqual('puNumber');
        expect(scope.shipmentsGrid.columnDefs[24].displayName).toEqual('PU #');

        expect(scope.shipmentsGrid.columnDefs[25].field).toEqual('revenue');
        expect(scope.shipmentsGrid.columnDefs[25].displayName).toEqual('Total');
        expect(scope.shipmentsGrid.columnDefs[25].cellFilter).toEqual('plsCurrency');

        expect(parentScope.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(parentScope.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.shipmentsGrid.plugins.length).toBe(4);
        expect(scope.shipmentsGrid.filterOptions.filterText).toEqual('');
        expect(scope.shipmentsGrid.filterOptions.useExternalFilter).toBeFalsy();
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });

    it('should set permission for next test', function(){
        permissions = ['VIEW_ACTIVE_SHIPMENTS_COST_DETAILS', 'VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY'];
    });

    it('should check shipments grid for PLS User', function() {
        expect(scope.shipmentsGrid.columnDefs.length).toBe(28);

        expect(scope.shipmentsGrid.columnDefs[25].field).toEqual('revenue');
        expect(scope.shipmentsGrid.columnDefs[25].displayName).toEqual('Revenue');
        expect(scope.shipmentsGrid.columnDefs[25].cellFilter).toEqual('plsCurrency');

        expect(scope.shipmentsGrid.columnDefs[26].field).toEqual('cost');
        expect(scope.shipmentsGrid.columnDefs[26].displayName).toEqual('Cost');
        expect(scope.shipmentsGrid.columnDefs[26].cellFilter).toEqual('plsCurrency');
        expect(scope.shipmentsGrid.columnDefs[26].hideForReport).toEqual(scope.exportWithoutCostMargin());

        expect(scope.shipmentsGrid.columnDefs[27].field).toEqual('margin');
        expect(scope.shipmentsGrid.columnDefs[27].displayName).toEqual('Margin');
        expect(scope.shipmentsGrid.columnDefs[27].cellFilter).toEqual('plsCurrency');
        expect(scope.shipmentsGrid.columnDefs[27].hideForReport).toEqual(scope.exportWithoutCostMargin());
    });
});