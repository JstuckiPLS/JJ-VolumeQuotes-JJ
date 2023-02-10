describe('Unit test for FinancialBoardPriceController', function() {
    var scope, parentScope;
    var httpBackend;
    var financialBoardService;

    var auditRecords = [{
        loadId: 1,
        adjustmentId: 2
    }];

    beforeEach(module('plsApp'));
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

        parentScope = $rootScope.$new();
        $controller('FinancialBoardController', {
            $scope: parentScope
        });
        scope = parentScope.$new();
        financialBoardService = $injector.get('FinancialBoardService');

        $controller('FinancialBoardPriceController', {
            '$scope': scope,
            'FinancialBoardService': financialBoardService
        });
    }));

    it('should check initial controller state', function() {
        expect(scope.shipments.length).toBe(0);
        expect(scope.selectedShipments.length).toBe(0);
        expect(scope.isCommentAvailable).toBeFalsy();
    });

    it('should init price audit', function() {
        spyOn(scope, 'loadPriceAuditData');
        scope.initPriceAudit();
        expect(scope.loadPriceAuditData).toHaveBeenCalled();
    });

    it('should load price audit data', function() {
        spyOn(financialBoardService, 'priceAudit');
        scope.loadPriceAuditData();
        expect(financialBoardService.priceAudit).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should check shipments grid', function() {
        expect(scope.shipmentsGrid).toBeDefined();
        expect(scope.shipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.shipmentsGrid.data).toEqual('shipments');
        expect(scope.shipmentsGrid.selectedItems.length).toBe(0);
        expect(scope.shipmentsGrid.multiSelect).toBeTruthy();
        expect(scope.shipmentsGrid.columnDefs.length).toBe(15);

        expect(scope.shipmentsGrid.columnDefs[0].field).toEqual('diffDays');
        expect(scope.shipmentsGrid.columnDefs[0].displayName).toEqual('Days on Hold');
        expect(scope.shipmentsGrid.columnDefs[0].width).toEqual('6%');
        
        expect(scope.shipmentsGrid.columnDefs[1].field).toEqual('loadId');
        expect(scope.shipmentsGrid.columnDefs[1].displayName).toEqual('Load ID');
        expect(scope.shipmentsGrid.columnDefs[1].width).toEqual('7%');

        expect(scope.shipmentsGrid.columnDefs[2].field).toEqual('customerName');
        expect(scope.shipmentsGrid.columnDefs[2].displayName).toEqual('Customer');
        expect(scope.shipmentsGrid.columnDefs[2].width).toEqual('9%');

        expect(scope.shipmentsGrid.columnDefs[3].field).toEqual('bol');
        expect(scope.shipmentsGrid.columnDefs[3].displayName).toEqual('BOL');
        expect(scope.shipmentsGrid.columnDefs[3].width).toEqual('10%');

        expect(scope.shipmentsGrid.columnDefs[4].field).toEqual('pro');
        expect(scope.shipmentsGrid.columnDefs[4].displayName).toEqual('Pro #');
        expect(scope.shipmentsGrid.columnDefs[4].width).toEqual('9%');

        expect(scope.shipmentsGrid.columnDefs[5].field).toEqual('deliveryDate');
        expect(scope.shipmentsGrid.columnDefs[5].displayName).toEqual('Actual Delivery Date');
        expect(scope.shipmentsGrid.columnDefs[5].width).toEqual('7%');

        expect(scope.shipmentsGrid.columnDefs[6].field).toEqual('scac');
        expect(scope.shipmentsGrid.columnDefs[6].displayName).toEqual('SCAC');
        expect(scope.shipmentsGrid.columnDefs[6].width).toEqual('5%');

        expect(scope.shipmentsGrid.columnDefs[7].field).toEqual('networkName');
        expect(scope.shipmentsGrid.columnDefs[7].displayName).toEqual('Business Unit');
        expect(scope.shipmentsGrid.columnDefs[7].width).toEqual('5%');

        expect(scope.shipmentsGrid.columnDefs[8].field).toEqual('numberOfNotes');
        expect(scope.shipmentsGrid.columnDefs[8].displayName).toEqual('Notes');
        expect(scope.shipmentsGrid.columnDefs[8].width).toEqual('3%');

        expect(scope.shipmentsGrid.columnDefs[9].field).toEqual('reason');
        expect(scope.shipmentsGrid.columnDefs[9].displayName).toEqual('Reason');
        expect(scope.shipmentsGrid.columnDefs[9].width).toEqual('6%');

        expect(scope.shipmentsGrid.columnDefs[10].field).toEqual('priceAuditDate');
        expect(scope.shipmentsGrid.columnDefs[10].displayName).toEqual('Billing Hold Date');
        expect(scope.shipmentsGrid.columnDefs[10].width).toEqual('7%');

        expect(scope.shipmentsGrid.columnDefs[11].field).toEqual('cost');
        expect(scope.shipmentsGrid.columnDefs[11].displayName).toEqual('Cost');
        expect(scope.shipmentsGrid.columnDefs[11].width).toEqual('5%');
        expect(scope.shipmentsGrid.columnDefs[13].cellFilter).toEqual('plsCurrency');

        expect(scope.shipmentsGrid.columnDefs[12].field).toEqual('margin');
        expect(scope.shipmentsGrid.columnDefs[12].displayName).toEqual('Margin');
        expect(scope.shipmentsGrid.columnDefs[12].width).toEqual('6%');
        expect(scope.shipmentsGrid.columnDefs[12].cellFilter).toEqual('appendSuffix:"%"');

        expect(scope.shipmentsGrid.columnDefs[13].field).toEqual('vendorBillAmount');
        expect(scope.shipmentsGrid.columnDefs[13].displayName).toEqual('VB Amount');
        expect(scope.shipmentsGrid.columnDefs[13].width).toEqual('7%');
        expect(scope.shipmentsGrid.columnDefs[13].cellFilter).toEqual('plsCurrency');

        expect(scope.shipmentsGrid.columnDefs[14].field).toEqual('accExecName');
        expect(scope.shipmentsGrid.columnDefs[14].displayName).toEqual('Account Executive');
        expect(scope.shipmentsGrid.columnDefs[14].width).toEqual('8%');

        expect(scope.shipmentsGrid.action).toEqual(jasmine.any(Function));

        expect(scope.shipmentsGrid.filterOptions.filterText).toEqual('');
        expect(scope.shipmentsGrid.filterOptions.useExternalFilter).toBeFalsy();

        expect(scope.shipmentsGrid.tooltipOptions.url).toEqual('pages/content/financialBoard/invoice-audit-grid-tooltip.html');
        expect(scope.shipmentsGrid.tooltipOptions.onShowTooltip).toBeFalsy();
        expect(scope.shipmentsGrid.tooltipOptions.showIf).toEqual(jasmine.any(Function));

        expect(scope.shipmentsGrid.plugins.length).toBe(5);
        expect(scope.shipmentsGrid.progressiveSearch).toBeTruthy();
    });

    it('should check close handler', function() {
        spyOn(scope, 'loadPriceAuditData');
        scope.closeHandler();

        expect(scope.loadPriceAuditData).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });

    it('should export invoices', function() {
        spyOn(scope, '$emit');
        scope.exportInvoices();

        expect(scope.$emit).toHaveBeenCalledWith("event:exportInvoices", {
            sheetName : 'Price Invoices',
            grid : scope.shipmentsGrid,
            selectedRows : true,
            fileName : 'Price_Invoices_'
        });
    });

    it('should edit shipment', function() {
        scope.selectedShipments.push({loadId: 1});
        spyOn(scope, '$broadcast');
        scope.editShipment();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', {
            shipmentId: scope.selectedShipments[0].loadId, 
            closeHandler: scope.closeHandler,
            isUnavailableCancel: false, 
            isUnavailableCopy: false,
            selectedTab: scope.selectedTab
        });
    });

    it('should approve audit', function() {
        scope.selectedShipments.push({loadId: 1, adjustmentId: 2});
        spyOn(financialBoardService, 'approveAudit');
        spyOn(scope.$root, '$emit');

        scope.approve();
        expect(financialBoardService.approveAudit).toHaveBeenCalledWith({
            auditRecords : scope.selectedShipments,
        }, jasmine.any(Function), jasmine.any(Function));
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it('should update records', function() {
        scope.shipmentsGrid.ngGrid = {
            filteredRows: [{selected: true}]
        };
        scope.recordsUpdate();

        expect(scope.gridRecords).toBe(1);
    });

    it('should send to invoice audit', function() {
        scope.selectedShipments.push({loadId: 1, adjustmentId: 2});
        spyOn(scope, '$broadcast');

        var auditRecords = [{
            loadId: 1,
            adjustmentId: 2
        }];
        
        scope.sendToInvoiceAudit();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:sendToAudit', {
            auditRecords: scope.selectedShipments,
            isInvioceAudit: true});
    });

    it('should trigger "filteredRows" watch', function() {
        scope.shipmentsGrid.ngGrid = {
            filteredRows: [{
                selected: true
            }]
        };
        scope.$digest();

        expect(scope.gridRecords).toBe(1);
    });

    it('should update data after sending audit', function() {
        spyOn(scope, 'loadPriceAuditData');

        scope.$broadcast('event:updateDataAfterSendToAudit');

        expect(scope.loadPriceAuditData).toHaveBeenCalled();
        expect(scope.selectedShipments.length).toBe(0);
    });
});