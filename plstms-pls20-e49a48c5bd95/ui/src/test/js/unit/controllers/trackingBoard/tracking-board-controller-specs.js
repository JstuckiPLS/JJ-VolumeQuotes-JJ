describe('Unit test for TrackingBoardController', function() {
    var rootScope;
    var scope;
    var templateCache;
    var location;
    var shipmentUtils;
    var shipmentsService;
    var trackingBoardService;
    var exportDataBuilder;
    var exportService;
    var httpBackend;
    var shipmentOperationService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        templateCache = $injector.get('$templateCache');
        location = $injector.get('$location');
        shipmentUtils = $injector.get('ShipmentUtils');
        shipmentsService = $injector.get('ShipmentDetailsService');
        shipmentOperationService = $injector.get('ShipmentOperationsService');
        trackingBoardService = $injector.get('TrackingBoardService');
        exportDataBuilder = $injector.get('ExportDataBuilder');
        exportService = $injector.get('ExportService');

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

        var controller = $injector.get('$controller');
        controller('TrackingBoardController', {
            '$scope': scope,
            '$templateCache': templateCache,
            '$location': location,
            'ShipmentUtils': shipmentUtils,
            'ShipmentDetailsService': shipmentsService,
            'TrackingBoardService': trackingBoardService,
            'ExportDataBuilder': exportDataBuilder,
            'ExportService': exportService
        });

        scope.$digest();
        httpBackend.flush();
    }));

    it('should open edit sales order dialog', function() {
        spyOn(scope, '$broadcast');
        var options = {};
        scope.openEditSalesOrderDialog(options);
        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', options);
    });

    it('should check all templates persist in cache', function() {
        expect(templateCache.get('pages/content/sales-order/so-general-adjustment-information.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-general-adjustment-information.html')[1]).toEqual('so-general-adjustment-information.html');
        
        expect(templateCache.get('pages/content/sales-order/so-general-information.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-general-information.html')[1]).toEqual('so-general-information.html');
        
        expect(templateCache.get('pages/content/sales-order/so-addresses.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-addresses.html')[1]).toEqual('so-addresses.html');
        
        expect(templateCache.get('pages/content/sales-order/so-details.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-details.html')[1]).toEqual('so-details.html');
        
        expect(templateCache.get('pages/content/sales-order/so-docs.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-docs.html')[1]).toEqual('so-docs.html');
        
        expect(templateCache.get('pages/content/sales-order/so-notes.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-notes.html')[1]).toEqual('so-notes.html');
        
        expect(templateCache.get('pages/content/sales-order/so-vendor-bills.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-vendor-bills.html')[1]).toEqual('so-vendor-bills.html');
        
        expect(templateCache.get('pages/content/sales-order/so-tracking.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/so-tracking.html')[1]).toEqual('so-tracking.html');
        
        expect(templateCache.get('pages/content/sales-order/sales-order-customer-carrier.html')[0]).toBe(200);
        expect(templateCache.get('pages/content/sales-order/sales-order-customer-carrier.html')[1]).toEqual('sales-order-customer-carrier.html');
        
        expect(templateCache.get('pages/tpl/quote-address-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/quote-address-tpl.html')[1]).toEqual('quote-address-tpl.html');
        
        expect(templateCache.get('pages/tpl/edit-shipment-details-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/edit-shipment-details-tpl.html')[1]).toEqual('edit-shipment-details-tpl.html');

        expect(templateCache.get('pages/tpl/view-notes-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/view-notes-tpl.html')[1]).toEqual('view-notes-tpl.html');
        
        expect(templateCache.get('pages/tpl/view-vendor-bill-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/view-vendor-bill-tpl.html')[1]).toEqual('view-vendor-bill-tpl.html');
        
        expect(templateCache.get('pages/tpl/pls-zip-select-specific-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/pls-zip-select-specific-tpl.html')[1]).toEqual('pls-zip-select-specific-tpl.html');
        
        expect(templateCache.get('pages/tpl/quote-price-info-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/quote-price-info-tpl.html')[1]).toEqual('quote-price-info-tpl.html');
        
        expect(templateCache.get('pages/tpl/pls-add-note-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/pls-add-note-tpl.html')[1]).toEqual('pls-add-note-tpl.html');
        
        expect(templateCache.get('pages/tpl/products-data-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/products-data-tpl.html')[1]).toEqual('products-data-tpl.html');
        
        expect(templateCache.get('pages/tpl/product-list-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/product-list-tpl.html')[1]).toEqual('product-list-tpl.html');
        
        expect(templateCache.get('pages/tpl/pls-bill-to-list-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/pls-bill-to-list-tpl.html')[1]).toEqual('pls-bill-to-list-tpl.html');
        
        expect(templateCache.get('pages/tpl/pls-location-list-tpl.html')[0]).toBe(200);
        expect(templateCache.get('pages/tpl/pls-location-list-tpl.html')[1]).toEqual('pls-location-list-tpl.html');
    });

    it('should navigate to shipment entry page', function() {
        spyOn(scope.$root, 'ignoreLocationChange');
        spyOn(scope.$root, 'redirectToUrl');
        scope.toShipmentEntry(1);
        expect(scope.$root.redirectToUrl).toHaveBeenCalledWith('/shipment-entry/1', {from: ''});
    });

    it('should copy shipment', function() {
        spyOn(scope.$root, 'ignoreLocationChange');
        spyOn(scope.$root, 'redirectToUrl');
        scope.copyShipment(1,1);

        expect(scope.$root.ignoreLocationChange).toHaveBeenCalled();
        expect(scope.$root.redirectToUrl).toHaveBeenCalledWith("/shipment-entry", {loadId : 1, customerId: 1, from: ''});
    });

    it('should return false if user has no permissions to cancel shipment', function() {
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();;
        expect(scope.isPermittedToCancelShipment()).toBeFalsy();
    });

    it('should return true if user has "cancel_shipment_before_dispatched" permission', function() {
        rootScope.authData.privilegies.push('CANCEL_SHIPMENT_BEFORE_DISPATCHED');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToCancelShipment()).toBeTruthy();
    });

    it('should return false if no shipment selected', function() {
        var shipment = undefined;
        spyOn(shipmentUtils, 'isShipmentCancellable').and.callThrough();
        var isCancallable = scope.isShipmentCancellable(shipment);
        expect(isCancallable).toBeFalsy();
        expect(shipmentUtils.isShipmentCancellable).not.toHaveBeenCalled();
    });

    it('should return false if user does not have permissions to copy shipment', function() {
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToCopyShipment()).toBeFalsy();
    });

    it('should return true if user has "ADD_SHIPMENT_ENTRY" permission', function() {
        rootScope.authData.privilegies.push('ADD_SHIPMENT_ENTRY');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToCopyShipment()).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('ADD_SHIPMENT_ENTRY');
    });

    it('should return false if user does not have permissions to export shipment', function() {
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToExport()).toBeFalsy();
    });

    it('should return true if user has "EXPORT_SHIPMENT_LIST" permission', function() {
        rootScope.authData.privilegies.push('EXPORT_SHIPMENT_LIST');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToExport()).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EXPORT_SHIPMENT_LIST');
    });

    it('should hide "view/edit" button if no shipments selected', function() {
        expect(scope.isPermittedToViewEditButton([])).toBeFalsy();
    });

    it('should return false if no shipments selected', function() {
        expect(scope.isPermittedToEdit([])).toBeFalsy();
    });
 
    it('should return true if user has "EDIT_SHIPMENT_BEFORE_PICKUP" permission and shipment status is "OPEN" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_BEFORE_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'OPEN'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_BEFORE_PICKUP');
    });

    it('should return true if user has "EDIT_SHIPMENT_BEFORE_PICKUP" permission and shipment status is "BOOKED" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_BEFORE_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'BOOKED'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_BEFORE_PICKUP');
    });

    it('should return true if user has "EDIT_SHIPMENT_BEFORE_PICKUP" permission and shipment status is "DISPATCHED" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_BEFORE_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'DISPATCHED'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_BEFORE_PICKUP');
    });

    it('should return true if user has "EDIT_SHIPMENT_AFTER_PICKUP" permission and shipment status is "IN_TRANSIT" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_AFTER_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'IN_TRANSIT'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_AFTER_PICKUP');
    });

    it('should return true if user has "EDIT_SHIPMENT_AFTER_PICKUP" permission and shipment status is "OUT_FOR_DELIVERY" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_AFTER_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'OUT_FOR_DELIVERY'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_AFTER_PICKUP');
    });

    it('should return true if user has "EDIT_SHIPMENT_AFTER_PICKUP" permission and shipment status is "DELIVERED" ', function() {
        rootScope.authData.privilegies.push('EDIT_SHIPMENT_AFTER_PICKUP');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        expect(scope.isPermittedToEdit([{status: 'DELIVERED'}, {status: 'DUMMY'}])).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('EDIT_SHIPMENT_AFTER_PICKUP');
    });

    it('should open confirmation dialog and calcel not picked up shipments', function() {
        var shipment = {shipmentId: 1, status: 'BOOKED'};
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(shipmentOperationService, 'cancelShipment');
        spyOn(trackingBoardService, 'getContactInfo');

        scope.cancelShipment(shipment);

        expect(scope.$root.$broadcast).toHaveBeenCalled();
        expect(trackingBoardService.getContactInfo).not.toHaveBeenCalled();
    });

    it('should not cancel shipment with status "IN_TRANSIT" but show notification instead', function() {
        var shipment = {shipmentId: 1, status: 'IN_TRANSIT'};
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(shipmentOperationService, 'cancelShipment');
        spyOn(trackingBoardService, 'getContactInfo');
        scope.cancelShipment(shipment);
        expect(scope.$root.$broadcast).not.toHaveBeenCalled();
        expect(trackingBoardService.getContactInfo).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function), jasmine.any(Function));
        expect(shipmentOperationService.cancelShipment).not.toHaveBeenCalled();
    });

    it('should not cancel shipment with status "DELIVERED" but show notification instead', function() {
        var shipment = {shipmentId: 1, status: 'DELIVERED'};
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(shipmentOperationService, 'cancelShipment');
        spyOn(trackingBoardService, 'getContactInfo');
        scope.cancelShipment(shipment);
        expect(scope.$root.$broadcast).not.toHaveBeenCalled();
        expect(trackingBoardService.getContactInfo).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function), jasmine.any(Function));
        expect(shipmentOperationService.cancelShipment).not.toHaveBeenCalled();
    });

    it('should not cancel shipment with status "CANCELLED" but show notification instead', function() {
        var shipment = {shipmentId: 1, status: 'CANCELLED'};
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(shipmentOperationService, 'cancelShipment');
        spyOn(trackingBoardService, 'getContactInfo');
        scope.cancelShipment(shipment);
        expect(scope.$root.$broadcast).not.toHaveBeenCalled();
        expect(trackingBoardService.getContactInfo).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function), jasmine.any(Function));
        expect(shipmentOperationService.cancelShipment).not.toHaveBeenCalled();
    });

    it('should export all shipments', function() {
        scope.shipmentGrid = {
                ngGrid : {
                    filteredRows: [{
                        selected: true,
                }]
            }
        };
        spyOn(exportDataBuilder, 'getColumnNames');
        spyOn(exportDataBuilder, 'buildTotalsFooterData');
        spyOn(exportDataBuilder, 'buildExportData');
        spyOn(exportService, 'exportData');

        scope.exportAllShipments(scope.shipmentGrid);

        expect(exportDataBuilder.getColumnNames).toHaveBeenCalled();
        expect(exportDataBuilder.buildTotalsFooterData).toHaveBeenCalled();
        expect(exportDataBuilder.buildExportData).toHaveBeenCalled();
        expect(exportService.exportData).toHaveBeenCalled();
    });
});