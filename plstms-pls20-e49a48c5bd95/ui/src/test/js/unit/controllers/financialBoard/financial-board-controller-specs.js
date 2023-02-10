describe('Financial board controller unit test', function() {
    var HTTP_OK = 200;

    var scope;
    var exportDataBuilder;
    var templateCache;

    var exportedData = {};
    var exportService = {
        exportData: function(success) {
            success(exportedData);
        }
    };
    var httpBackend;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($injector) {
        var $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        exportDataBuilder = $injector.get('ExportDataBuilder');
        templateCache = $injector.get('$templateCache');
        exportDataBuilder = $injector.get('ExportDataBuilder');

        var $controller = $injector.get('$controller');

        httpBackend = $injector.get('$httpBackend');
        httpBackend.when('GET', 'pages/content/sales-order/so-general-adjustment-information.html')
            .respond('so-general-adjustment-information.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-general-information.html')
            .respond('so-general-information.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-addresses.html')
            .respond('so-addresses.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-details.html')
            .respond('so-details.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-docs.html')
            .respond('so-docs.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-notes.html')
            .respond('so-notes.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-vendor-bills.html')
            .respond('so-vendor-bills.html');
        httpBackend.when('GET', 'pages/content/sales-order/so-tracking.html')
            .respond('so-tracking.html');
        httpBackend.when('GET', 'pages/content/sales-order/sales-order-customer-carrier.html')
            .respond('sales-order-customer-carrier.html');
        httpBackend.when('GET', 'pages/tpl/quote-address-tpl.html')
            .respond('quote-address-tpl.html');
        httpBackend.when('GET', 'pages/tpl/edit-shipment-details-tpl.html')
            .respond('edit-shipment-details-tpl.html');
        httpBackend.when('GET', 'pages/tpl/view-notes-tpl.html')
            .respond('view-notes-tpl.html');
        httpBackend.when('GET', 'pages/tpl/view-vendor-bill-tpl.html')
            .respond('view-vendor-bill-tpl.html');
        httpBackend.when('GET', 'pages/tpl/pls-zip-select-specific-tpl.html')
            .respond('pls-zip-select-specific-tpl.html');
        httpBackend.when('GET', 'pages/tpl/quote-price-info-tpl.html')
            .respond('quote-price-info-tpl.html');
        httpBackend.when('GET', 'pages/tpl/pls-add-note-tpl.html')
            .respond('pls-add-note-tpl.html');
        httpBackend.when('GET', 'pages/tpl/products-data-tpl.html')
            .respond('products-data-tpl.html');
        httpBackend.when('GET', 'pages/tpl/product-list-tpl.html')
            .respond('product-list-tpl.html');
        httpBackend.when('GET', 'pages/tpl/pls-bill-to-list-tpl.html')
            .respond('pls-bill-to-list-tpl.html');
        httpBackend.when('GET', 'pages/tpl/pls-location-list-tpl.html')
            .respond('pls-location-list-tpl.html');

        createController = function() {
            return $controller('FinancialBoardController', {
                '$scope': scope,
                'ExportDataBuilder': exportDataBuilder,
                'ExportService': exportService,
                '$templateCache': templateCache
            });
        };
    }));

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it ('should make sure template cache is pupulated', function() {
        httpBackend.expectGET('pages/content/sales-order/so-general-adjustment-information.html');
        httpBackend.expectGET('pages/content/sales-order/so-general-information.html');
        httpBackend.expectGET('pages/content/sales-order/so-addresses.html');
        httpBackend.expectGET('pages/content/sales-order/so-details.html');
        httpBackend.expectGET('pages/content/sales-order/so-docs.html');
        httpBackend.expectGET('pages/content/sales-order/so-notes.html');
        httpBackend.expectGET('pages/content/sales-order/so-vendor-bills.html');
        httpBackend.expectGET('pages/content/sales-order/so-tracking.html');
        httpBackend.expectGET('pages/content/sales-order/sales-order-customer-carrier.html');
        var controller = createController();
        httpBackend.flush();

        expect(templateCache.get('pages/content/sales-order/so-general-adjustment-information.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-general-adjustment-information.html')[1])
            .toEqual('so-general-adjustment-information.html');
        expect(templateCache.get('pages/content/sales-order/so-general-information.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-general-information.html')[1])
            .toEqual('so-general-information.html');
        expect(templateCache.get('pages/content/sales-order/so-addresses.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-addresses.html')[1])
            .toEqual('so-addresses.html');
        expect(templateCache.get('pages/content/sales-order/so-details.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-details.html')[1])
            .toEqual('so-details.html');
        expect(templateCache.get('pages/content/sales-order/so-docs.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-docs.html')[1])
            .toEqual('so-docs.html');
        expect(templateCache.get('pages/content/sales-order/so-notes.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-notes.html')[1])
            .toEqual('so-notes.html');
        expect(templateCache.get('pages/content/sales-order/so-vendor-bills.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-vendor-bills.html')[1])
            .toEqual('so-vendor-bills.html');
        expect(templateCache.get('pages/content/sales-order/so-tracking.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/so-tracking.html')[1])
            .toEqual('so-tracking.html');
        expect(templateCache.get('pages/content/sales-order/sales-order-customer-carrier.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/content/sales-order/sales-order-customer-carrier.html')[1])
            .toEqual('sales-order-customer-carrier.html');
        expect(templateCache.get('pages/tpl/quote-address-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/quote-address-tpl.html')[1])
            .toEqual('quote-address-tpl.html');
        expect(templateCache.get('pages/tpl/edit-shipment-details-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/edit-shipment-details-tpl.html')[1])
            .toEqual('edit-shipment-details-tpl.html');
        expect(templateCache.get('pages/tpl/view-notes-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/view-notes-tpl.html')[1])
            .toEqual('view-notes-tpl.html');
        expect(templateCache.get('pages/tpl/view-vendor-bill-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/view-vendor-bill-tpl.html')[1])
            .toEqual('view-vendor-bill-tpl.html');
        expect(templateCache.get('pages/tpl/pls-zip-select-specific-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/pls-zip-select-specific-tpl.html')[1])
            .toEqual('pls-zip-select-specific-tpl.html');
        expect(templateCache.get('pages/tpl/quote-price-info-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/quote-price-info-tpl.html')[1])
            .toEqual('quote-price-info-tpl.html');
        expect(templateCache.get('pages/tpl/pls-add-note-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/pls-add-note-tpl.html')[1])
            .toEqual('pls-add-note-tpl.html');
        expect(templateCache.get('pages/tpl/products-data-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/products-data-tpl.html')[1])
            .toEqual('products-data-tpl.html');
        expect(templateCache.get('pages/tpl/product-list-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/product-list-tpl.html')[1])
            .toEqual('product-list-tpl.html');
        expect(templateCache.get('pages/tpl/pls-bill-to-list-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/pls-bill-to-list-tpl.html')[1])
            .toEqual('pls-bill-to-list-tpl.html');
        expect(templateCache.get('pages/tpl/pls-location-list-tpl.html')[0]).toBe(HTTP_OK);
        expect(templateCache.get('pages/tpl/pls-location-list-tpl.html')[1])
            .toEqual('pls-location-list-tpl.html');
    });

    it ('should export invieces', function() {
        spyOn(scope, '$on').and.callThrough();
        spyOn(exportService, 'exportData');

        createController();
        httpBackend.flush();

        var options = {
            fileName: "Audit_Invoices_",
            grid: {
                columnDefs: [
                             { exportDisplayName: "Adjustment", field: "processingType"},
                             { displayName: "Load ID", field: "processingType"},
                             { displayName: "BOL", field: "processingType"},
                             { displayName: "PO#", field: "processingType"},
                             { displayName: "PU#", field: "processingType"}
                            ],
                $gridScope: {}
            },
            selectedRows: [{adjustmentId: 10,
                            approved: false,
                            billToId: 6,
                            billToName: "Liberty square",
                            bolNumber: "20121007-6-1",
                            carrierName: "ESTES EXPRESS LINES",
                            cost: 532.34,
                            currency: "USD",
                            customerName: "PLS SHIPPER",
                            deliveredDate: "2012-10-11",
                            doNotInvoice: true,
                            loadId: 156,
                            margin: 119.89,
                            networkName: "LTL",
                            processingType: "AUTOMATIC",
                            revenue: 652.23,
                            stringForCompare: "6:15610"}],
            sheetName: "Audit Invoices"
        };
        scope.$broadcast('event:exportInvoices', options);

        expect(scope.$on).toHaveBeenCalledWith('event:exportInvoices', jasmine.any(Function));
        expect(exportService.exportData).toHaveBeenCalled();
    });
});