describe('Unit test for FinancialBoardConsolidatedController controller', function() {
    var scope, parentScope, timeoutService;
    var httpBackend;
    var processCBIData = [];

    var financialBoardCbiService = {
        list: function(){},
        listLoads: function(){},
        approveAll: function(){},
        processCBI: function(requestParam, successCallback) {
            if (requestParam.billToId === 7 && requestParam.emails === 'test@email.com' && requestParam.invoiceDate === '06/11/2015') {
                successCallback(processCBIData);
            }
        }
    };

    var financialBoardService = {
        approve: function(){}
    };

    var auditRecords = [{
        loadId: 1,
        adjustmentId: 1
    }];

    var permissions = [];

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector, $rootScope, $controller, $timeout) {
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
        if (permissions) {
            $rootScope.authData = new AuthData({privilegies: permissions});
        }
        timeoutService = $timeout;

        spyOn(financialBoardCbiService, 'list');
        $controller('FinancialBoardConsolidatedController', {
            $scope: scope,
            $timeout: timeoutService,
            FinancialBoardCBIService: financialBoardCbiService,
            FinancialBoardService: financialBoardService
        });
        scope.cbiModel.consolidatedInvoicesGrid.ngGrid = {
            filteredRows: []
        };
        scope.cbiModel.consolidatedLoadsGrid.ngGrid = {
            filteredRows: []
        };
        scope.$digest();
    }));

    it('should check cbi model', function() {
        expect(scope.cbiModel.filterDate).toBeUndefined();
        expect(scope.cbiModel.minFilterDate).toBeDefined();
        expect(scope.cbiModel.maxFilterDate).toBeDefined();
        expect(scope.cbiModel.cbiInvoiceData.length).toBe(0);
        expect(scope.cbiModel.selectedCBIInvoices.length).toBe(0);
        expect(scope.cbiModel.cbiLoads.length).toBe(0);
        expect(scope.cbiModel.selectedCBILoads.length).toBe(0);
        expect(scope.cbiModel.reprtData).toBeUndefined();
    });

    it('should load cbi data', function() {
        expect(financialBoardCbiService.list).toHaveBeenCalled();
    });

    it('should load cbi loads', function() {
        scope.cbiModel.selectedCBIInvoices.push({data: 'test invoice'});
        spyOn(financialBoardCbiService, 'listLoads');

        scope.loadCBILoads();

        expect(financialBoardCbiService.listLoads).toHaveBeenCalled();
    });

    it('should approve all invoices', function() {
        spyOn(financialBoardCbiService, 'approveAll');
        spyOn(scope, 'recordsCustomersUpdate');

        var cbiEntry = {
            billToId: '1',
            approved: 'ALL'
        };
        scope.approveAll(cbiEntry);

        expect(financialBoardCbiService.approveAll).toHaveBeenCalled();
    });

    it('should approve load', function() {
        scope.cbiModel.selectedCBIInvoices.push({data: 'test invoice'});
        var invoice = {
            loadId: 1,
            adjustmentId: 1,
            approve: true
        };
        var event = {};

        spyOn(financialBoardService, 'approve');

        scope.approveLoad(invoice, event);

        expect(financialBoardService.approve).toHaveBeenCalled();
    });

    it('should check close handler', function() {
        scope.cbiModel.selectedCBIInvoices.push({billToId: 1});
        spyOn(financialBoardCbiService, 'listLoads');

        scope.closeHandler();

        expect(financialBoardCbiService.list).toHaveBeenCalled();
        expect(financialBoardCbiService.listLoads).toHaveBeenCalledWith({billToId: [1]}, jasmine.any(Function));
        expect(scope.cbiModel.selectedCBIInvoices.length).toBe(0);
    });

    it('should edit load', function() {
        scope.cbiModel.selectedCBILoads.push({
            loadId: 1
        });
        spyOn(scope, '$broadcast');

        scope.editLoad();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', {
            shipmentId: scope.cbiModel.selectedCBILoads[0].loadId,
            closeHandler: scope.closeHandler,
            isUnavailableCancel: false,
            isUnavailableCopy: false,
            selectedTab: scope.selectedTab
        });
    });

    it('should export loads', function() {
        scope.cbiModel.selectedCBIInvoices.push({customerName: 'Bob Cousy'});
        spyOn(scope, '$emit');

        scope.exportLoads();

        expect(scope.$emit).toHaveBeenCalledWith('event:exportInvoices', {
            sheetName : 'Consolidated_Invoice',
            fileName : scope.cbiModel.selectedCBIInvoices[0].customerName + '_',
            grid : scope.cbiModel.consolidatedLoadsGrid,
            selectedRows : false
        });
    });

    it('should send to audit', function() {
        scope.cbiModel.selectedCBILoads.push({loadId: 1, adjustmentId: 1});
        spyOn(scope, '$broadcast');

        scope.sendToInvoiceAudit();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:sendToAudit', {
            auditRecords : [{
                loadId: scope.cbiModel.selectedCBILoads[0].loadId,
                adjustmentId: scope.cbiModel.selectedCBILoads[0].adjustmentId
          }], isInvioceAudit: true});
    });

    it('should check consolidated invoice grid', function() {
        expect(scope.cbiModel.consolidatedInvoicesGrid).toBeDefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.enableColumnResize).toBeTruthy();
        expect(scope.cbiModel.consolidatedInvoicesGrid.data).toEqual('cbiModel.cbiInvoiceData');
        expect(scope.cbiModel.consolidatedInvoicesGrid.primaryKey).toEqual('billToId');
        expect(scope.cbiModel.consolidatedInvoicesGrid.multiSelect).toBeTruthy();
        expect(scope.cbiModel.consolidatedInvoicesGrid.selectedItem).toBeUndefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs.length).toBe(10);
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[0].field).toEqual('networkName');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[1].field).toEqual('customerName');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[2].field).toEqual('self');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[3].field).toEqual('invoiceDateInfo');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[3].cellTemplate).not.toBeDefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[4].field).toEqual('includeCarrierRate');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[5].field).toEqual('sendBy');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[6].field).toEqual('totalRevenue');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[7].field).toEqual('totalCost');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[8].field).toEqual('totalMargin');
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[9].field).toEqual('approved');
        expect(scope.cbiModel.consolidatedInvoicesGrid.filterOptions).toBeDefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.filterOptions.filterText).toEqual("");
        expect(scope.cbiModel.consolidatedInvoicesGrid.filterOptions.useExternalSorting).toBeFalsy();
        expect(scope.cbiModel.consolidatedInvoicesGrid.sortInfo).toBeDefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.sortInfo.fields).toContain('customerName');
        expect(scope.cbiModel.consolidatedInvoicesGrid.sortInfo.directions).toContain('asc');
        expect(scope.cbiModel.consolidatedInvoicesGrid.beforeSelectionChange).toEqual(jasmine.any(Function));
        expect(scope.cbiModel.consolidatedInvoicesGrid.plugins.length).toBe(3);
        expect(scope.cbiModel.consolidatedInvoicesGrid.useExternalSorting).toBeFalsy();
        expect(scope.cbiModel.consolidatedInvoicesGrid.progressiveSearch).toBeTruthy();
    });

    it('should set permission for next test', function(){
        permissions = ['CUSTOMER_PROFILE_VIEW'];
    });

    it('should apply custom cell template with link', function() {
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[3].cellTemplate).toBeDefined();
        expect(scope.cbiModel.consolidatedInvoicesGrid.columnDefs[3].cellTemplate).toEqual(
                '<div class="ngCellText text-center" data-ng-class="col.colIndex()">'
                + '<a href="#/customer/{{row.entity.customerId}}/billTo/{{row.entity.billToId}}">{{row.getProperty(col.field)}}</a>'
                + '</div>');
        permissions = undefined;
    });

    it('should check consolidated loads grid', function() {
        expect(scope.cbiModel.consolidatedLoadsGrid).toBeDefined();
        expect(scope.cbiModel.consolidatedLoadsGrid.enableColumnResize).toBeTruthy();
        expect(scope.cbiModel.consolidatedLoadsGrid.data).toEqual('cbiModel.cbiLoads');
        expect(scope.cbiModel.consolidatedLoadsGrid.multiSelect).toBeTruthy();
        expect(scope.cbiModel.consolidatedLoadsGrid.selectedItems.length).toBe(0);
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs.length).toBe(23);
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[0].field).toEqual('numberOfNotes');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[1].field).toEqual('adjustmentId');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[2].field).toEqual('self');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[3].field).toEqual('carrierName');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[4].field).toEqual('deliveredDate');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[5].field).toEqual('loadId');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[6].field).toEqual('bolNumber');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[7].field).toEqual('proNumber');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[8].field).toEqual('poNumber');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[9].field).toEqual('glNumber');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[10].field).toEqual('soNumber');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[11].field).toEqual('origin');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[12].field).toEqual('destination');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[13].field).toEqual('shipmentDirection');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[14].field).toEqual('paymentTerms');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[15].field).toEqual('acc');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[16].field).toEqual('fs');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[17].field).toEqual('revenue');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[18].field).toEqual('cost');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[19].field).toEqual('marginAmt');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[20].field).toEqual('margin');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[21].field).toEqual('paidAmount');
        expect(scope.cbiModel.consolidatedLoadsGrid.columnDefs[22].field).toEqual('approved');
        expect(scope.cbiModel.consolidatedLoadsGrid.filterOptions).toBeDefined();
        expect(scope.cbiModel.consolidatedLoadsGrid.filterOptions.filterText).toEqual("");
        expect(scope.cbiModel.consolidatedLoadsGrid.filterOptions.useExternalSorting).toBeFalsy();
        expect(scope.cbiModel.consolidatedLoadsGrid.sortInfo).toBeDefined();
        expect(scope.cbiModel.consolidatedLoadsGrid.sortInfo.fields).toContain('carrierName');
        expect(scope.cbiModel.consolidatedLoadsGrid.sortInfo.directions).toContain('asc');
        expect(scope.cbiModel.consolidatedLoadsGrid.plugins.length).toBe(5);
        expect(scope.cbiModel.consolidatedLoadsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.cbiModel.consolidatedLoadsGrid.useExternalSorting).toBeFalsy();
        expect(scope.cbiModel.consolidatedLoadsGrid.progressiveSearch).toBeTruthy();
    });

    it('should update records', function() {
        var item = {
            entity: {
                approved: true
            }
        };
        spyOn(scope, 'recordsCustomersUpdate');

        var approved = scope.recordsUpdate([item]);

        expect(approved).toBe(1);
        expect(scope.recordsCustomersUpdate).toHaveBeenCalled();
    });

    it('should update customer records', function() {
        scope.cbiModel.consolidatedInvoicesGrid.ngGrid = {
            filteredRows: [{
                selected: true,
                entity: {
                    approved: "ALL"
                }
            }]
        };

        scope.recordsCustomersUpdate();

        expect(scope.gridCustomers).toBe(1);
        expect(scope.gridCustomersApproved).toBe(1);
    });

    it('should trigger records update', function() {
        scope.cbiModel.consolidatedInvoicesGrid.ngGrid = {
            filteredRows: []
        };
        scope.cbiModel.consolidatedLoadsGrid.ngGrid = {
            filteredRows: [{},{}]
        };

        scope.$digest();

        scope.cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows.push({
            selected: false,
            entity: {
                approved: 'ALL'
            }
        });
        scope.cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows.push({
            selected: false,
            entity: {
                approved: 'SOME'
            }
        });
        scope.cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows.push({
            selected: false,
            entity: {
                approved: 'NONE'
            }
        });

        scope.$digest();

        expect(scope.gridCustomers).toBe(3);
        expect(scope.gridCustomersApproved).toBe(2);
        expect(scope.gridRecords).toBe(2);
    });

    it('should be able to process CBI for selected and approved invoice', function() {
        scope.cbiModel.selectedCBIInvoices.push({customerName: 'Bob Cousy', approved: 'SOME'});
        scope.cbiModel.filterDate = '06/11/2015';
        scope.cbiModel.cbiLoads.push({loadId: 1, adjustmentId: 1, approved: true});

        var processed = scope.canProcess();

        expect(processed).toBeTruthy();
    });

    it('should not be able to process CBI when no invoices selected', function() {
        scope.cbiModel.filterDate = '06/11/2015';
        scope.cbiModel.selectedCBIInvoices = [];
        var processed = scope.canProcess();

        expect(processed).toBeFalsy();
    });

    it('should not be able to process CBI when invoice is not approved', function() {
        scope.cbiModel.filterDate = '06/11/2015';
        scope.cbiModel.selectedCBIInvoices.push({customerName: 'Bob Cousy', approved: 'NONE'});

        var processed = scope.canProcess();

        expect(processed).toBeFalsy();
    });

    it('should not be able to process CBI when date is not specified', function() {
        scope.cbiModel.filterDate = undefined;
        scope.cbiModel.selectedCBIInvoices.push({customerName: 'Bob Cousy', approved: 'ALL'});

        var processed = scope.canProcess();

        expect(processed).toBeFalsy();
    });

    it('should be able to process CBI for selected and approved invoice', function() {
        scope.cbiModel.selectedCBIInvoices.push({customerName: 'Bob Cousy', approved: 'SOME'});
        scope.cbiModel.filterDate = '06/11/2015';
        scope.cbiModel.cbiLoads.push({loadId: 1, adjustmentId: 1, approved: true});

        var processed = scope.canProcess();

        expect(processed).toBeTruthy();
    });

    it('should open dialog for processing invoices', function() {
        scope.cbiModel.selectedCBIInvoices = [{
            customerName: 'customer1',
            billToName: 'billTo1',
            billToId: 7,
            approved: true
        }];
        scope.cbiModel.cbiLoads = [{
            loadId: 1,
            approved: true
        }];
        spyOn(scope, 'selectRebillAdjustment').and.callThrough();
        spyOn(scope, 'getSelectedUniqueRebillAdjustments').and.callThrough();
        spyOn(scope, '$broadcast');

        scope.processInvoices();

        expect(scope.selectRebillAdjustment).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(Object));
        expect(scope.getSelectedUniqueRebillAdjustments).toHaveBeenCalledWith(jasmine.any(Object));
        expect(scope.$broadcast).toHaveBeenCalledWith('openProcessInvoiceDialog', jasmine.any(Object));
    });
});