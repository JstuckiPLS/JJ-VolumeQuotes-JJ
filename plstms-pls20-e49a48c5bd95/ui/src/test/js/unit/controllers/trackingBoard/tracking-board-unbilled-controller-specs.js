describe('Unit test for TrackingBoardUnbilledController', function() {
    var scope;
    var location;
    var trackingBoardService;
    var pluginFactory;
    var shipmentsService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        location = $injector.get('$location');
        trackingBoardService = $injector.get('TrackingBoardService');
        pluginFactory = $injector.get('NgGridPluginFactory');
        shipmentsService = $injector.get('ShipmentDetailsService');

        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        var controller = $injector.get('$controller');
        controller('TrackingBoardUnbilledController', {
            '$scope': scope,
            '$location': location,
            'TrackingBoardService': trackingBoardService,
            'NgGridPluginFactory': pluginFactory,
            'ShipmentDetailsService': shipmentsService
        });
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(0);
        expect(scope.selectedShipments.length).toBe(0);
        expect(scope.detailsGrid).toEqual({});
    });

    it('should init shipments', function() {
        spyOn(scope, 'loadUnbilledShipments');
        scope.init();
        expect(scope.loadUnbilledShipments).toHaveBeenCalled();
    });

    it('should load unbilled shipments', function() {
        spyOn(trackingBoardService, 'unbilled');
        scope.loadUnbilledShipments();
        expect(trackingBoardService.unbilled).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should check close handler', function() {
        spyOn(scope, 'loadUnbilledShipments');
        scope.closeHandler();
        expect(scope.loadUnbilledShipments).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should check shipmrns grid', function() {
        expect(scope.shipmentsGrid).toBeDefined();
        expect(scope.shipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.shipmentsGrid.data).toEqual('shipmentsData');
        expect(scope.shipmentsGrid.multiSelect).toBeFalsy();
        expect(scope.shipmentsGrid.selectedItems.length).toEqual(0);
        expect(scope.shipmentsGrid.columnDefs.length).toBe(21);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');
        expect(scope.shipmentsGrid.columnDefs[0].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('shipmentId');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Load ID');
        expect(scope.shipmentsGrid.columnDefs[1].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('bolNumber');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('BOL');
        expect(scope.shipmentsGrid.columnDefs[2].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[2].cellClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[2].showTooltip).toBeTruthy();

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('pieces');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('QTY');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('weight');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('Total Weight');

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('proNumber');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('PRO#');
        expect(scope.shipmentsGrid.columnDefs[5].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[5].cellClass).toEqual('text-center');
        
        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('network');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('Business Unit');
        expect(scope.shipmentsGrid.columnDefs[6].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[6].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('accountExecutive');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Account Executive');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Customer');
        expect(scope.shipmentsGrid.columnDefs[8].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[8].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('carrier');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('Carrier');
        expect(scope.shipmentsGrid.columnDefs[9].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[9].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('shipper');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('Shipper');
        expect(scope.shipmentsGrid.columnDefs[10].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[10].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('origin');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Origin');
        expect(scope.shipmentsGrid.columnDefs[11].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[11].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('consignee');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Consignee');
        expect(scope.shipmentsGrid.columnDefs[12].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[12].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('destination');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('Destination');
        expect(scope.shipmentsGrid.columnDefs[13].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[13].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('deliveryDate');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Del. Date');
        expect(scope.shipmentsGrid.columnDefs[14].cellFilter).toEqual('date:wideAppDateFormat');
        expect(scope.shipmentsGrid.columnDefs[14].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[15].field).toEqual('billToRecieved');
        expect(scope.shipmentsGrid.columnDefs[15].displayName).toEqual('Reason');

        expect(scope.shipmentsGrid.columnDefs[16].field).toEqual('createdBy');
        expect(scope.shipmentsGrid.columnDefs[16].displayName).toEqual('Created By');

        expect(scope.shipmentsGrid.columnDefs[17].field).toEqual('createdDate');
        expect(scope.shipmentsGrid.columnDefs[17].displayName).toEqual('Created Date');
        expect(scope.shipmentsGrid.columnDefs[17].cellFilter).toEqual('date:wideAppDateFormat');

        expect(scope.shipmentsGrid.columnDefs[18].field).toEqual('revenue');
        expect(scope.shipmentsGrid.columnDefs[18].displayName).toEqual('Revenue');
        expect(scope.shipmentsGrid.columnDefs[18].cellFilter).toEqual('plsCurrency');

        expect(scope.shipmentsGrid.columnDefs[19].field).toEqual('total');
        expect(scope.shipmentsGrid.columnDefs[19].displayName).toEqual('Cost');
        expect(scope.shipmentsGrid.columnDefs[19].cellFilter).toEqual('plsCurrency');

        expect(scope.shipmentsGrid.columnDefs[20].field).toEqual('margin');
        expect(scope.shipmentsGrid.columnDefs[20].displayName).toEqual('Margin');
        expect(scope.shipmentsGrid.columnDefs[20].cellFilter).toEqual('plsCurrency');

        expect(scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.shipmentsGrid.plugins.length).toBe(5);
        expect(scope.shipmentsGrid.filterOptions.filterText).toEqual('');
        expect(scope.shipmentsGrid.filterOptions.useExternalFilter).toBeFalsy();
        expect(scope.shipmentsGrid.tooltipOptions.url).toEqual('pages/content/quotes/shipments-grid-tooltip.html');
        expect(scope.shipmentsGrid.tooltipOptions.onShow).toBeTruthy();
        expect(scope.shipmentsGrid.sortInfo.fields).toContain('deliveryDate');
        expect(scope.shipmentsGrid.sortInfo.directions).toContain('asc');
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });
});