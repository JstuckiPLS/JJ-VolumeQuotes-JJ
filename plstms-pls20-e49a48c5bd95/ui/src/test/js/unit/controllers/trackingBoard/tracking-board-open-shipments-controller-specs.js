describe('Unit test for TrackingBoardOpenController', function() {
    var scope, parentScope;
    var trackingBoardService;
    var pluginFactory;
    var shipmentsService;
    var dateTimeUtils;
    var shipmentStatus;
    var httpBackend;
    var shipmentOperationService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($rootScope, $injector, $controller) {
        trackingBoardService = $injector.get('TrackingBoardService');
        pluginFactory = $injector.get('NgGridPluginFactory');
        shipmentsService = $injector.get('ShipmentDetailsService');
        shipmentOperationService = $injector.get('ShipmentOperationsService');
        dateTimeUtils = $injector.get('DateTimeUtils');
        shipmentStatus = $injector.get('ShipmentStatus');

        var rootScope = $injector.get('$rootScope');
        parentScope = $rootScope.$new();
        $controller('TrackingBoardController', {
            $scope: parentScope
        });
        scope = parentScope.$new();
        spyOn(parentScope, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        $controller('TrackingBoardOpenController', {
            $scope: scope,
            'TrackingBoardService': trackingBoardService,
            'NgGridPluginFactory': pluginFactory,
            'ShipmentDetailsService': shipmentsService,
            'DateTimeUtils': dateTimeUtils,
            'ShipmentStatus': shipmentStatus,
            'ShipmentOperationService': shipmentOperationService

        });

        httpBackend = $injector.get('$httpBackend');
        httpBackend.expect('GET', '/restful/shipment/dictionary/packageType').respond([]);
        httpBackend.expect('GET', '/restful/dictionary/notificationTypes').respond([]);
        httpBackend.expect('GET', '/restful/shipment/dictionary/billToReqField').respond([]);
        httpBackend.expect('GET', 'pages/content/sales-order/so-general-adjustment-information.html')
            .respond('so-general-adjustment-information.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-general-information.html')
            .respond('so-general-information.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-addresses.html')
            .respond('so-addresses.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-details.html')
            .respond('so-details.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-docs.html')
            .respond('so-docs.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-notes.html')
            .respond('so-notes.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-vendor-bills.html')
            .respond('so-vendor-bills.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-tracking.html')
            .respond('so-tracking.html');
        httpBackend.expect('GET', 'pages/content/sales-order/sales-order-customer-carrier.html')
            .respond('sales-order-customer-carrier.html');
        httpBackend.expect('GET', 'pages/tpl/quote-address-tpl.html').respond('quote-address-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/edit-shipment-details-tpl.html').respond('edit-shipment-details-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/view-notes-tpl.html').respond('view-notes-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/view-vendor-bill-tpl.html').respond('view-vendor-bill-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/pls-zip-select-specific-tpl.html').respond('pls-zip-select-specific-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/quote-price-info-tpl.html').respond('quote-price-info-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/pls-add-note-tpl.html').respond('pls-add-note-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/products-data-tpl.html').respond('products-data-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/product-list-tpl.html').respond('product-list-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/pls-bill-to-list-tpl.html').respond('pls-bill-to-list-tpl.html');
        httpBackend.expect('GET', 'pages/tpl/pls-location-list-tpl.html').respond('pls-location-list-tpl.html');
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(0);
        expect(scope.detailsGrid).toEqual({});
        expect(scope.fromDate).toBeNull();
        expect(scope.toDate).toBeNull();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should check delete order dialog', function() {
        expect(scope.deleteOrderDialog).toBeDefined();
        expect(scope.deleteOrderDialog.open).toEqual(jasmine.any(Function));
    });

    it('should open confirmation dialog', function() {
        spyOn(scope.$root, '$broadcast');
        scope.selectedShipments.push({bol: 1});
        scope.deleteOrderDialog.open();
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showConfirmation', jasmine.any(Object));
    });

    it('should search shipments', function() {
        spyOn(scope, 'getShipments');
        scope.searchShipments();
        expect(scope.getShipments).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should clear filters', function() {
        spyOn(scope, '$broadcast');
        scope.clearFilters();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
        expect(scope.selectedShipments.length).toBe(0);
        expect(scope.shipments.length).toBe(0);
    });

    it('should get shipments', function() {
        spyOn(trackingBoardService, 'open');
        scope.getShipments();
        expect(trackingBoardService.open)
            .toHaveBeenCalledWith({fromDate : null, toDate : null}, jasmine.any(Function));
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

    it('should delete shipment', function() {
        var shipment = {id: 1, status: 'OPEN'};
        spyOn(shipmentOperationService, 'cancelShipment');
        scope.selectedShipments.push(shipment);
        scope.deleteShipment();

        expect(shipmentOperationService.cancelShipment).toHaveBeenCalledWith({customerId: jasmine.any(Object), shipmentId: 1},
                jasmine.any(Function), jasmine.any(Function));
    });

    it('should return bold font style for non api capable entity', function() {
        var entity = {apiCapable: false};
        expect('bold').toEqual(scope.getRowFontStyle(entity));
    });

    it('should return normal font style for api capable entity', function() {
        var entity = {apiCapable: true};
        expect('normal').toEqual(scope.getRowFontStyle(entity));
    });

    it('should show tooltip', function() {
        spyOn(shipmentsService, 'getTooltipData');
        var row = {
           entity: {id: 1}
        };
        scope.onShowTooltip(row);
        expect(shipmentsService.getTooltipData).toHaveBeenCalled();
    });

    it('should check shipments grid', function() {
        expect(scope.shipmentsGrid).toBeDefined();
        expect(scope.shipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.shipmentsGrid.data).toEqual('shipments');
        expect(scope.shipmentsGrid.multiSelect).toBeFalsy();
        expect(scope.shipmentsGrid.selectedItems.length).toEqual(0);
        expect(scope.shipmentsGrid.columnDefs.length).toBe(16);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');
        expect(scope.shipmentsGrid.columnDefs[0].cellClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[0].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('network');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Business Unit');
        expect(scope.shipmentsGrid.columnDefs[1].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('accountExecutive');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('Account Executive');
        expect(scope.shipmentsGrid.columnDefs[2].width).toEqual('5%');

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('id');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('Load ID');
        expect(scope.shipmentsGrid.columnDefs[3].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('bol');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('BOL');
        expect(scope.shipmentsGrid.columnDefs[4].width).toEqual('7%');
        expect(scope.shipmentsGrid.columnDefs[4].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[4].cellClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[4].showTooltip).toBeTruthy();

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('pieces');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('QTY');
        expect(scope.shipmentsGrid.columnDefs[5].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('weight');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('Total Weight');
        expect(scope.shipmentsGrid.columnDefs[6].width).toEqual('4%');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Customer');
        expect(scope.shipmentsGrid.columnDefs[7].width).toEqual('8%');
        expect(scope.shipmentsGrid.columnDefs[7].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[7].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('pickupDate');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Estimated Pickup Date');
        expect(scope.shipmentsGrid.columnDefs[8].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[8].cellFilter).toEqual('date:wideAppDateFormat');
        expect(scope.shipmentsGrid.columnDefs[8].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[8].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('pickupWindowEnd');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('Late Pickup Date/Time');
        expect(scope.shipmentsGrid.columnDefs[9].width).toEqual('7%');
        expect(scope.shipmentsGrid.columnDefs[9].cellFilter).toEqual('date:appDateTimeFormat');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('shipper');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('Shipper');
        expect(scope.shipmentsGrid.columnDefs[10].width).toEqual('10%');
        expect(scope.shipmentsGrid.columnDefs[10].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[10].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('origin');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Origin');
        expect(scope.shipmentsGrid.columnDefs[11].width).toEqual('11%');
        expect(scope.shipmentsGrid.columnDefs[11].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[11].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('consignee');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Consignee');
        expect(scope.shipmentsGrid.columnDefs[12].width).toEqual('10%');
        expect(scope.shipmentsGrid.columnDefs[12].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[12].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('destination');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('Destination');
        expect(scope.shipmentsGrid.columnDefs[13].width).toEqual('11%');
        expect(scope.shipmentsGrid.columnDefs[13].headerClass).toEqual('text-center');
        expect(scope.shipmentsGrid.columnDefs[13].cellClass).toEqual('text-center');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('createdBy');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Created By');
        expect(scope.shipmentsGrid.columnDefs[14].width).toEqual('6%');

        expect(parentScope.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(parentScope.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.shipmentsGrid.plugins.length).toBe(5);
        expect(scope.shipmentsGrid.filterOptions.filterText).toEqual('');
        expect(scope.shipmentsGrid.filterOptions.useExternalFilter).toBeFalsy();
        expect(scope.shipmentsGrid.tooltipOptions.url).toEqual('pages/content/quotes/shipments-grid-tooltip.html');
        expect(scope.shipmentsGrid.tooltipOptions.onShow).toBeTruthy();
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });
});