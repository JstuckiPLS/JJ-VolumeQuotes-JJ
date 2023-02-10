describe('Financial board audit unit test case', function() {
    var gridFactory;
    var httpBackend;
    var financialBoardService;
    var $scope, parentScope;

    var shipments = [{
        accExecName: "admin sysadmin",
        bol: "20121206-2-1",
        carrierName: "FEDEX FREIGHT ECONOMY",
        cost: 880.41,
        costDiscrepancy: 0,
        customerName: "PLS SHIPPER",
        deliveredDate: "2012-12-10",
        loadId: 439,
        margin: -400.01,
        networkName: "LTL",
        po: "20121206-2-2",
        reason: "",
        revenue: 176.08,
        adjustmentId: 1
    }];

    var auditRecords = [{
       loadId: 439,
       adjustmentId: 1
    }];

    beforeEach(module('plsApp'));
    beforeEach(module('financialBoardServices'));

    beforeEach(inject(function($injector, $rootScope, $controller) {
        httpBackend = $injector.get('$httpBackend');
        httpBackend.expect('GET', 'pages/content/sales-order/so-general-adjustment-information.html')
            .respond('so-general-adjustment-information.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-general-information.html')
            .respond('so-general-information.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-addresses.html').respond('so-addresses.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-details.html').respond('so-details.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-docs.html').respond('so-docs.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-notes.html').respond('so-notes.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-vendor-bills.html').respond('so-vendor-bills.html');
        httpBackend.expect('GET', 'pages/content/sales-order/so-tracking.html').respond('so-tracking.html');
        httpBackend.expect('GET', 'pages/content/sales-order/sales-order-customer-carrier.html').respond('sales-order-customer-carrier.html');
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

        gridFactory = $injector.get('NgGridPluginFactory');
        financialBoardService = $injector.get('FinancialBoardService');

        parentScope = $rootScope.$new();
        $controller('FinancialBoardController', {
            $scope: parentScope
        });
        $scope = parentScope.$new();

        $controller('FinancialBoardAuditController', {
           '$scope': $scope
        });
    }));

    it('it should check initial controller state', function() {
        expect($scope.shipments.length).toBe(0);
        expect($scope.selectedShipments.length).toBe(0);
        expect($scope.isCommentAvailable).toBeFalsy();
    });

    it('should init invoice audit', function() {
        spyOn($scope, 'loadAuditData');

        $scope.loadAuditData();

        expect($scope.loadAuditData).toHaveBeenCalled();
    });

    it('should load audit data', function() {
        spyOn(financialBoardService, 'lowBenefit');

        $scope.loadAuditData();

        expect(financialBoardService.lowBenefit).toHaveBeenCalled();
        expect($scope.selectedShipments.length).toBe(0);
    });

    it('should close handler', function() {
        spyOn($scope, 'loadAuditData');

        $scope.closeHandler();

        expect($scope.loadAuditData).toHaveBeenCalled();
        expect($scope.selectedShipments.length).toBe(0);
    });

    it('should export invoices', function() {
        spyOn($scope, '$emit');

        $scope.exportInvoices();

        expect($scope.$emit).toHaveBeenCalledWith('event:exportInvoices', jasmine.any(Object));
    });

    it('should edit shipment', function() {
        spyOn($scope, '$broadcast');
        $scope.selectedShipments.push(shipments[0]);

        $scope.editShipment();

        expect($scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', jasmine.any(Object));
    });

    it('should approve audit', function() {
        spyOn(financialBoardService, 'approveAudit');
        $scope.selectedShipments.push(shipments[0]);

        $scope.approve();

        expect(financialBoardService.approveAudit).toHaveBeenCalledWith({
            auditRecords : auditRecords
        }, jasmine.any(Function), jasmine.any(Function));
    });

    it('should check shipment grid is defined', function() {
        expect($scope.shipmentsGrid).toBeDefined();
    });

    it('should update records', function() {
        expect($scope.gridRecords).toBeUndefined();

        $scope.shipmentsGrid.ngGrid = {
            filteredRows: [{
                rowIndex: 0,
                selected: false,
                entity: shipments[0]
            }]
        };

        $scope.recordsUpdate();

        expect($scope.gridRecords).toBeDefined();
        expect($scope.gridRecords).toBe(1);
    });

    it('should call "recordsUpdate" when filtering is performed on grid', function() {
        $scope.shipmentsGrid.ngGrid = {
            filteredRows: []
        };

        $scope.shipmentsGrid.ngGrid.filteredRows.push(shipments[0]);
        $scope.$digest();

        expect($scope.gridRecords).toBe(1);
    });
});