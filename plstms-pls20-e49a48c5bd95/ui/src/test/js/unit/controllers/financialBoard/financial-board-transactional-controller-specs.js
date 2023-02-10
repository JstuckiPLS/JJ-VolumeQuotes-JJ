describe('Unit test for FinancialBoardTransactionalController controller', function() {
    var scope, parentScope;
    var window;
    var urlConfig;
    var financialBoardService;
    var gridFactory;
    var stringUtils;
    var httpBackend;

    var invoices = [{
            adjustmentId: 15,
            approved: false,
            billToId: 1,
            billToName: "Haynes International, INC.",
            bolNumber: "02-1",
            carrierName: "Vitran Express",
            cost: 10.15,
            currency: "USD",
            customerName: "PLS SHIPPER",
            deliveredDate: "2013-12-16",
            doNotInvoice: false,
            loadId: 724,
            margin: 5.35,
            networkName: "LTL",
            processingType: "AUTOMATIC",
            revenue: 15.5,
            stringForCompare: "1:72415"
        },
        {
            acc: 0,
            adjustmentId:7527,
            adjustmentRevision:1,
            approved:false,
            billToId:9,
            billToName:"Belrun",
            bolNumber:"q1",
            carrierName:"UPS FREIGHT A",
            compoundSortField:1,
            cost:0,
            customerName:"PLS SHIPPER",
            deliveredDate:"2016-10-07",
            doNotInvoice:true,
            fs:-24.44,
            loadId:7058,
            margin:100,
            marginAmt:-135.55,
            missingPaymentsTerms:false,
            networkName:"LTL",
            noteComment:"Invoice Audit:  Reason Freight Class Comment:  -",
            noteCreatedDate:"2016-10-18T14:35:28Z",
            noteModifiedBy:"admin sysadmin",
            numberOfNotes:118,
            paidAmount:0,
            paymentTerms:"PPD",
            proNumber:"q1",
            rebill:true,
            revenue:-135.55,
            shipmentDirection:"O"
        },
        {
            acc:0,
            adjustmentId:7528,
            adjustmentRevision:2,
            approved:false,
            billToId:1,
            billToName:"Haynes International, INC.",
            bolNumber:"q1",
            carrierName:"UPS FREIGHT A",
            compoundSortField:1,
            cost:0,
            customerName:"PLS SHIPPER",
            deliveredDate:"2016-10-07",
            doNotInvoice:true,
            fs:24.44,
            loadId:7058,
            margin:100,
            marginAmt:135.55,
            missingPaymentsTerms:false,
            networkName:"LTL",
            noteComment:"Invoice Audit:  Reason Freight Class Comment:  -",
            noteCreatedDate:"2016-10-18T14:35:28Z",
            noteModifiedBy:"admin sysadmin",
            numberOfNotes:118,
            paidAmount:0,
            paymentTerms:"PPD",
            proNumber:"q1",
            rebill:true,
            revenue:135.55,
            shipmentDirection:"O"
    }];

    var selectedBillToList = [{
            email:"Denver.Takemoto@test.com",
            id:9,
            name:"Belrun",
            processType:"TRANSACTIONAL",
            sendEmail:false
        },
        {
            email:"Delfina.Belvins@test.com",
            id:1,
            name:"Haynes International, INC.",
            processType:"TRANSACTIONAL",
            sendEmail:false
    }];

    var auditRecords = [{
        loadId: invoices[0].loadId,
        adjustmentId: invoices[0].adjustmentId
    }];

    var invoiceReportCallback = {invoiceNumbers: [], success: true, sendEmail: false, edi: false};

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector, $rootScope, $controller) {
        financialBoardService = {
            transactional: function(params, success) {
                success(invoices)
            },
            approve: function(params, success, error) {
                success();
            },
            getReason: function(params, success) {},
            processTransactionalInvoices: function(params, success, error) {
                success(invoiceReportCallback);
            }
        };
        gridFactory = $injector.get('NgGridPluginFactory');
        stringUtils = $injector.get('StringUtils');
        window = $injector.get('$window');
        urlConfig = {};

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
        $controller('FinancialBoardTransactionalController', {
            '$scope': scope,
            '$window': window,
            'urlConfig': urlConfig,
            'FinancialBoardService': financialBoardService,
            'NgGridPluginFactory': gridFactory,
            'StringUtils': stringUtils
        });

    }));

    it('should check initial model state', function() {
        expect(scope.invoices.length).toBe(0);
        expect(scope.selectedInvoices.length).toBe(0);
        expect(scope.gridRecords).toBe(0);
        expect(scope.gridApproved).toBe(0);
        expect(scope.gridSelected).toBe(0);
    });

    it('makes sure invoiceGrid is initialized', function() {
        expect(scope.invoiceGrid).toBeDefined();
        expect(scope.invoiceGrid.enableColumnResize).toBeTruthy();
        expect(scope.invoiceGrid.multiSelect).toBeTruthy();
        expect(scope.invoiceGrid.data).toContain([]);
        expect(scope.invoiceGrid.selectedItems.length).toBe(0);
        expect(scope.invoiceGrid.primaryKey).toEqual('loadId');
        expect(scope.invoiceGrid.headerRowHeight).toBe(36);
        expect(scope.invoiceGrid.columnDefs.length).toBe(14);
        expect(scope.invoiceGrid.columnDefs[0].field).toEqual('numberOfNotes');
        expect(scope.invoiceGrid.columnDefs[1].field).toEqual('compoundSortField');
        expect(scope.invoiceGrid.columnDefs[2].field).toEqual('networkName');
        expect(scope.invoiceGrid.columnDefs[3].field).toEqual('customerName');
        expect(scope.invoiceGrid.columnDefs[4].field).toEqual('billToName + ", " + row.entity.currency');
        expect(scope.invoiceGrid.columnDefs[5].field).toEqual('deliveredDate');
        expect(scope.invoiceGrid.columnDefs[6].field).toEqual('loadId');
        expect(scope.invoiceGrid.columnDefs[7].field).toEqual('bolNumber');
        expect(scope.invoiceGrid.columnDefs[8].field).toEqual('proNumber');
        expect(scope.invoiceGrid.columnDefs[9].field).toEqual('carrierName');
        expect(scope.invoiceGrid.columnDefs[10].field).toEqual('revenue');
        expect(scope.invoiceGrid.columnDefs[11].field).toEqual('cost');
        expect(scope.invoiceGrid.columnDefs[12].field).toEqual('marginAmt');
        expect(scope.invoiceGrid.beforeSelectionChange).toEqual(jasmine.any(Function));
        expect(scope.invoiceGrid.plugins.length).toBe(4);
        expect(scope.invoiceGrid.progressiveSearch).toBeTruthy();
    });

    it('should open dialog for processing invoices', function() {
        scope.selectedInvoices = [invoices[1]];
        scope.invoices = invoices;
        spyOn(scope, 'selectRebillAdjustment').and.callThrough();
        spyOn(scope, 'getSelectedUniqueRebillAdjustments').and.callThrough();
        spyOn(scope, '$broadcast');

        scope.processInvoices();

        expect(scope.selectRebillAdjustment).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(Object));
        expect(scope.selectedInvoicesForProcessing.length).toBe(2);
        expect(scope.selectedInvoices.length).toBe(2);
        expect(scope.selectedBillToList.length).toBe(2);
        expect(scope.getSelectedUniqueRebillAdjustments).toHaveBeenCalledWith(jasmine.any(Object));

        expect(scope.$broadcast).toHaveBeenCalledWith('openProcessInvoiceDialog', jasmine.any(Object));
    });

    it('should raise application error if schedule not approved', function() {
        scope.selectedInvoices = [invoices[1], invoices[2]];
        scope.selectedInvoices[0].approved = true;
        scope.selectedInvoices[1].approved = true;
        spyOn(scope.$root, '$emit').and.callThrough();

        scope.processInvoices();

        expect(scope.$root.$emit).toHaveBeenCalledWith('event:application-error', 'Process invoices error!', 
                'You must uncheck the "Process on Schedule" before clicking the Override Scheduled Process button.');
    });

    it('should load transactional invoices', function() {
        spyOn(financialBoardService, 'transactional').and.callThrough();

        scope.loadTransactionalInvoices();

        expect(financialBoardService.transactional).toHaveBeenCalled();
        expect(scope.invoices.length).toBe(3);
        expect(scope.invoices[0]).toEqual(invoices[0]);
    });

    it('should check permissions are initialized properly', function() {
        expect(scope.pageModel.processAllowed).toBeFalsy();
        expect(scope.pageModel.approveAllowed).toBeFalsy();
        expect(scope.pageModel.exportAllowed).toBeFalsy();
        expect(scope.pageModel.sendToAuditAllowed).toBeFalsy();
    });

    it('should edit close shipment handler', function() {
        scope.selectedInvoices = [];
        scope.selectedInvoices.push({adjustmentId: undefined, loadId: 1});
        spyOn(financialBoardService, 'getReason');
        spyOn(scope, 'loadTransactionalInvoices');

        scope.editShipmentCloseHandler();

        expect(scope.loadTransactionalInvoices).toHaveBeenCalled();
        expect(scope.invoices.length).toBe(0);
        expect(scope.selectedInvoices.length).toBe(0);
    });

    it('should approve invoice', function() {
        spyOn(scope, 'setProgressText');
        spyOn(financialBoardService, 'approve').and.callThrough();
        spyOn(scope, 'recordsUpdate');

        scope.approve(invoices[0]);

        expect(scope.setProgressText).toHaveBeenCalledWith('Approving invoice. Please wait...');
        expect(financialBoardService.approve).toHaveBeenCalled();
        var params = {loadId : invoices[0].loadId, adjustmentId : invoices[0].adjustmentId, approve : !invoices[0].approved}
        expect(invoices[0].approved).toBeTruthy();
        expect(scope.gridApproved).toBe(0);
        expect(scope.recordsUpdate).toHaveBeenCalled();
    });

    it('should send to audit', function() {
        scope.selectedInvoices[0] = invoices[0];
        spyOn(scope, 'isSelectedRebillAdjustment').and.callThrough();
        spyOn(scope, 'selectRebillAdjustment').and.callThrough();
        spyOn(scope, '$broadcast');

        scope.sendToInvoiceAudit();

        expect(scope.isSelectedRebillAdjustment).toHaveBeenCalled();
        expect(scope.selectRebillAdjustment).toHaveBeenCalled();
        var transferObject = {auditRecords : [{loadId: invoices[0].loadId, adjustmentId: invoices[0].adjustmentId}], isInvioceAudit: true};
        expect(scope.$broadcast).toHaveBeenCalledWith('event:sendToAudit', transferObject);
    });

    it('should edit shipment', function() {
        spyOn(scope, '$broadcast');
        scope.selectedInvoices.push({
            adjustmentId: 15,
            doNotInvoice: false,
            loadId: 724,
        });

        scope.editShipment();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', jasmine.any(Object));
    });

    it('should update records', function() {
        scope.invoiceGrid.ngGrid = {
            filteredRows: [{
                selected: true,
                entity: {
                    approved: true
                }
            }]
        };

        scope.recordsUpdate();

        expect(scope.gridRecords).toBe(1);
        expect(scope.gridSelected).toBe(1);
        expect(scope.gridApproved).toBe(1);
    });

    it('should trigger records update', function() {
        scope.invoiceGrid.ngGrid = {
            filteredRows: []
        };

        scope.invoiceGrid.ngGrid.filteredRows.push({
            selected: true,
            entity: {
                approved: true
            }});
        scope.$digest();

        expect(scope.gridRecords).toBe(1);
        expect(scope.gridSelected).toBe(1);
        expect(scope.gridApproved).toBe(1);
    });

    it('should watch selected invoice items', function() {
        scope.invoiceGrid.ngGrid = {
            filteredRows: []
        };

        scope.invoiceGrid.ngGrid.filteredRows.push({
            selected: true,
            entity: {
                approved: true
            }});

        scope.invoiceGrid.selectedItems = [];

        scope.invoiceGrid.selectedItems.push(invoices[0]);
        scope.$digest();

        scope.$digest();

        expect(scope.gridRecords).toBe(1);
        expect(scope.gridSelected).toBe(1);
        expect(scope.gridApproved).toBe(1);
    });

    it('should export invoices', function() {
        spyOn(scope, '$emit');

        scope.exportInvoices();

        expect(scope.$emit).toHaveBeenCalledWith('event:exportInvoices', jasmine.any(Object));
    });
});