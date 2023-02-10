describe('Unit test for FinancialBoardHistoryController', function() {
    var scope;
    var window;
    var filter;
    var urlConfig = {};
    var financialBoardService; 
    var gridFactory;
    var dateTimeUtils;
    var rateQuoteCustomerService;
    var billToEmails;
    var billToEmailService = {
        getEmails: function(params, success) {
            success(billToEmails);
        }
    };
    beforeEach(module('plsApp'));

    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        window = $injector.get('$window');
        filter = $injector.get('$filter');
        financialBoardService = $injector.get('FinancialBoardService');
        gridFactory = $injector.get('NgGridPluginFactory');
        dateTimeUtils = $injector.get('DateTimeUtils');
        rateQuoteCustomerService = $injector.get('RateQuoteCustomerService');

        var controller = $injector.get('$controller');
        var createController = function() {
            return controller('FinancialBoardHistoryController', {
                '$scope': scope,
                '$window': window,
                '$filter': filter,
                'urlConfig': urlConfig,
                'FinancialBoardService': financialBoardService,
                'NgGridPluginFactory': gridFactory,
                'DateTimeUtils': dateTimeUtils,
                'RateQuoteCustomerService': rateQuoteCustomerService,
                'BillToEmailService' : billToEmailService,
            });
        };

        createController();
    }));

    it('should check initial model state', function() {
        expect(scope.invoices.length).toBe(0);
        expect(scope.selectedInvoices.length).toBe(0);
        expect(scope.cbiItems.length).toBe(0);
        expect(scope.selectedCbiItems.length).toBe(0);
        expect(scope.showSendMailDialog).toBeFalsy();
        expect(scope.reprocessDialog.show).toBeFalsy();
    });

    it('should return "true" if bol number, invoice number, pro number and load id are undefined', function() {
        scope.bolNumber = undefined;
        scope.loadId = undefined;
        scope.proNumber = undefined;
        scope.invoiceNumber = undefined;

        expect(scope.invoiceBolProOrLoadIdSpecified()).toBeTruthy();
    });

    it('should return "false" if load id is specified', function() {
        scope.bolNumber = undefined;
        scope.proNumber = undefined;
        scope.invoiceNumber = undefined;
        scope.loadId = 1;

        expect(scope.invoiceBolProOrLoadIdSpecified()).toBeFalsy();
    });

    it('should return "false" if bol number is specified', function() {
        scope.bolNumber = 1;
        scope.loadId = undefined;
        scope.proNumber = undefined;
        scope.invoiceNumber = undefined;

        expect(scope.invoiceBolProOrLoadIdSpecified()).toBeFalsy();
    });

    it('should return "false" if invoice number is specified', function() {
        scope.bolNumber = undefined;
        scope.loadId = undefined;
        scope.proNumber = undefined;
        scope.invoiceNumber = 1;

        expect(scope.invoiceBolProOrLoadIdSpecified()).toBeFalsy();
    });

    it('should return "false" if pro number is specified', function() {
        scope.bolNumber = undefined;
        scope.loadId = undefined;
        scope.proNumber = 1;
        scope.invoiceNumber = undefined;

        expect(scope.invoiceBolProOrLoadIdSpecified()).toBeFalsy();
    });

    it('should check cbi details popup model', function() {
        expect(scope.cbiDetailsPopup).toBeDefined();
        expect(scope.cbiDetailsPopup.show).toBeFalsy();
        expect(scope.cbiDetailsPopup.close).toEqual(jasmine.any(Function));
    });

    it('should check reprocessDialog model', function() {
        expect(scope.reprocessDialog.show).toBeFalsy();
        expect(scope.reprocessDialog.financial).toBeFalsy();
        expect(scope.reprocessDialog.customerEmail).toBeFalsy();
        expect(scope.reprocessDialog.sendEmailAllowed).toBeFalsy();
        expect(scope.reprocessDialog.emails).toBeUndefined();
        expect(scope.reprocessDialog.customerEDI).toBeFalsy();
        expect(scope.reprocessDialog.close).toEqual(jasmine.any(Function));
    });

    it('should refresh table', function() {
        spyOn(financialBoardService, 'historyInvoices');
        spyOn(scope.$root, '$emit').and.callThrough();

        scope.refreshTable();

        expect(financialBoardService.historyInvoices).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(Function), jasmine.any(Function));
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it('should view shipment with parent dialog', function() {
        scope.selectedCbiItems.push({loadId: 1});
        spyOn(scope, '$broadcast').and.callThrough();

        scope.viewShipment(true);

        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', {
            shipmentId: 1, 
            formDisabled: true, 
            parentDialog: 'cbi-details'});
    });

    it('should view shipment without parent dialog', function() {
        scope.selectedInvoices.push({loadId: 1});
        spyOn(scope, '$broadcast').and.callThrough();

        scope.viewShipment(false);

        expect(scope.$broadcast).toHaveBeenCalledWith('event:showEditSalesOrder', {
            shipmentId: 1, 
            formDisabled: true});
    });

    it('should view shipment', function() {
        scope.selectedInvoices.push({loadId: '1'});
        spyOn(scope, 'viewShipment');
        spyOn(financialBoardService, 'historyCBIDetails');

        scope.viewDetails();

        expect(scope.viewShipment).toHaveBeenCalled();
        expect(financialBoardService.historyCBIDetails).not.toHaveBeenCalled();
    });

    it('should view shipment', function() {
        scope.selectedInvoices = [];
        scope.selectedInvoices.push({invoiceType: 'CBI', invoiceId: 1, invoiceNumber: 2});
        spyOn(scope, 'viewShipment');
        spyOn(financialBoardService, 'historyCBIDetails');

        scope.viewDetails();

        expect(scope.viewShipment).not.toHaveBeenCalled();
        expect(financialBoardService.historyCBIDetails).toHaveBeenCalledWith({invoiceId: 1, groupInvoiceNumber: 2}, jasmine.any(Function), jasmine.any(Function));
    });

    it('should show reprocessing dialog for transactional invoice', function() {
        scope.selectedInvoices.push({invoiceId: 1, billToId: '351', invoiceType: 'TRANSACTIONAL', invoiceInFinancials: false});
        expect(scope.reprocessDialog.show).toBeFalsy();
        billToEmails = {key: 351, value: 'test@email.com'};
        spyOn(billToEmailService, 'getEmails');

        scope.showReprocessingDialog();

        expect(billToEmailService.getEmails).toHaveBeenCalledWith({billToId: '351'}, jasmine.any(Function), jasmine.any(Function));
    });

    it('should show reprocessing dialog for consolidated invoce', function() {
        scope.selectedInvoices.push({invoiceId: 1, invoiceNumber: 2, billToId: '351', invoiceType: 'CBI', invoiceInFinancials: true});
        expect(scope.reprocessDialog.show).toBeFalsy();
        spyOn(financialBoardService, 'historyCBIDetails');

        scope.showReprocessingDialog();

        expect(financialBoardService.historyCBIDetails).toHaveBeenCalledWith({invoiceId: 1, groupInvoiceNumber: 2}, jasmine.any(Function), jasmine.any(Function));
    });

    it('should not show reprocessing dialog if no loads selected', function() {
        scope.selectedInvoices.push({ invoiceId: 1});
        spyOn(financialBoardService, 'historyCBIDetails');

        scope.showReprocessingDialog();

        expect(financialBoardService.historyCBIDetails).not.toHaveBeenCalled();
    });

    xit('should reprocess invoice', function() {
        scope.selectedInvoices.push({invoiceId: 7000, invoiceNumber: 'T-7000-0000', loadId: '7000', invoiceType: 'TRANSACTIONAL', invoiceInFinancials: false, ediCapable: false});
        scope.reprocessDialog.customerEmail = true;
        scope.reprocessDialog.emails = 'test@email.com';
        scope.showReprocessingDialog();
        spyOn(financialBoardService, 'reProcessHistory');
        spyOn(scope.$root, '$emit');
        scope.reprocessInvoice();

        expect(financialBoardService.reProcessHistory)
            .toHaveBeenCalledWith({invoiceId: 7000, subject: 'PLS-Invoice-T-7000-0000', comments: '', financial: false, customerEmail: true,
                emails: 'test@email.com', customerEDI:false,
                loadIds: ['7000']},
                    jasmine.any(Function), jasmine.any(Function));
        expect(scope.reprocessDialog.close).toEqual(jasmine.any(Function));
        expect(scope.reprocessFinancialPopup.close).toEqual(jasmine.any(Function));
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it('should trigger "fromDate" watch and initialize "min" and "max" dates', function() {
       scope.invoicesGrid.ngGrid = {
           filteredRows: [{
               selected: true,
           }]
       };
       scope.fromDate = '2015-03-03';
       scope.$digest();

       expect(scope.minToDate.toString()).toContain('Tue Mar 03 2015 00:00:00');
       expect(scope.maxToDate.toString()).toContain('Wed Jun 03 2015 00:00:00');
    });

    it('should trigger "toDate" watch and initialize "min" and "max" dates', function() {
        scope.invoicesGrid.ngGrid = {
            filteredRows: [{
                selected: true,
            }]
        };
        scope.toDate = '2015-03-03';
        scope.$digest();

        expect(scope.maxFromDate.toString()).toContain('Tue Mar 03 2015 00:00:00');
        expect(scope.minFromDate.toString()).toContain('Wed Dec 03 2014 00:00:00');
     });

    it('should check "search" button is disabled when bol number is not specified', function() {
        scope.bolNumber = undefined;
        expect(scope.isSearchAvailable()).toBeFalsy();

        scope.bolNumber = 1;
        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should check "search" button is disabled when loadId is not specified', function() {
        scope.loadId = undefined;
        expect(scope.isSearchAvailable()).toBeFalsy();

        scope.loadId = 1;
        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should check "search" button is enabled when customer and dates are provided', function() {
        scope.selectedCustomer = {name: 'Bob Cousey'};
        scope.fromDate = '2015-03-03';
        scope.toDate = '2015-03-04';
        scope.plsHistorySearchForm = {
            $valid: true
        };

        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should check "search" button is enabled when customer and invoice number are provided', function() {
        scope.selectedCustomer = {name: 'Bob Cousey'};
        scope.invoiceNumber = 1;

        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should check "search" button is enabled when customer and bol number are provided', function() {
        scope.selectedCustomer = {name: 'Bob Cousey'};
        scope.bolNumber = 1;

        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should check "search" button is enabled when customer and pro number are provided', function() {
        scope.selectedCustomer = {name: 'Bob Cousey'};
        scope.proNumber = 1;

        expect(scope.isSearchAvailable()).toBeTruthy();
    });

    it('should reset search', function() {
        scope.invoices.push({invoiceNumber: 1});
        scope.selectedInvoices.push({invoiceNumber: 1});
        spyOn(scope, '$broadcast');

        scope.resetSearch();

        expect(scope.selectedInvoices.length).toBe(0);
        expect(scope.invoices.length).toBe(0);
        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
    });

    it('should export invoices', function() {
        spyOn(scope, '$emit');

        scope.exportInvoices();

        expect(scope.$emit).toHaveBeenCalledWith('event:exportInvoices', 
                {
                    sheetName : 'History Invoices',
                    grid : scope.invoicesGrid,
                    selectedRows : false,
                    fileName : 'History_Invoices_'});
    });

    it('should check invoices grid', function() {
        expect(scope.invoicesGrid).toBeDefined();
        expect(scope.invoicesGrid.enableColumnResize).toBeTruthy();
        expect(scope.invoicesGrid.data).toEqual('invoices');
        expect(scope.invoicesGrid.multiSelect).toBeFalsy();
        expect(scope.invoicesGrid.selectedItems).toEqual(scope.selectedInvoices);
        expect(scope.invoicesGrid.columnDefs.length).toBe(14);

        expect(scope.invoicesGrid.columnDefs[0].field).toEqual('adjustment');

        expect(scope.invoicesGrid.columnDefs[1].field).toEqual('invoiceDate');
        expect(scope.invoicesGrid.columnDefs[1].displayName).toEqual('Invoice Date');
        expect(scope.invoicesGrid.columnDefs[1].cellFilter).toEqual('date:wideAppDateFormat');
        expect(scope.invoicesGrid.columnDefs[1].width).toEqual('6%');

        expect(scope.invoicesGrid.columnDefs[2].field).toEqual('invoiceNumber');
        expect(scope.invoicesGrid.columnDefs[2].displayName).toEqual('Invoice #');
        expect(scope.invoicesGrid.columnDefs[2].width).toEqual('9%');

        expect(scope.invoicesGrid.columnDefs[3].field).toEqual('userName');
        expect(scope.invoicesGrid.columnDefs[3].displayName).toEqual('User Name');
        expect(scope.invoicesGrid.columnDefs[3].width).toEqual('9%');

        expect(scope.invoicesGrid.columnDefs[4].field).toEqual('loadId');
        expect(scope.invoicesGrid.columnDefs[4].displayName).toEqual('Load ID');
        expect(scope.invoicesGrid.columnDefs[4].width).toEqual('7%');

        expect(scope.invoicesGrid.columnDefs[5].field).toEqual('bol');
        expect(scope.invoicesGrid.columnDefs[5].displayName).toEqual('BOL');
        expect(scope.invoicesGrid.columnDefs[5].width).toEqual('7%');

        expect(scope.invoicesGrid.columnDefs[6].field).toEqual('pro');
        expect(scope.invoicesGrid.columnDefs[6].displayName).toEqual('PRO #');
        expect(scope.invoicesGrid.columnDefs[6].width).toEqual('7%');

        expect(scope.invoicesGrid.columnDefs[7].field).toEqual('networkName');
        expect(scope.invoicesGrid.columnDefs[7].displayName).toEqual('Business Unit');
        expect(scope.invoicesGrid.columnDefs[7].width).toEqual('7%');

        expect(scope.invoicesGrid.columnDefs[8].field).toEqual('customerName');
        expect(scope.invoicesGrid.columnDefs[8].displayName).toEqual('Customer');
        expect(scope.invoicesGrid.columnDefs[8].width).toEqual('8%');

        expect(scope.invoicesGrid.columnDefs[9].field).toEqual('carrierName');
        expect(scope.invoicesGrid.columnDefs[9].displayName).toEqual('Carrier');
        expect(scope.invoicesGrid.columnDefs[9].width).toEqual('8%');

        expect(scope.invoicesGrid.columnDefs[10].field).toEqual('invoiceAmount');
        expect(scope.invoicesGrid.columnDefs[10].displayName).toEqual('Invoice Amount');
        expect(scope.invoicesGrid.columnDefs[10].cellFilter).toEqual('plsCurrency');
        expect(scope.invoicesGrid.columnDefs[10].width).toEqual('9%');

        expect(scope.invoicesGrid.columnDefs[11].field).toEqual('paidAmount');
        expect(scope.invoicesGrid.columnDefs[11].displayName).toEqual('Paid Amount');
        expect(scope.invoicesGrid.columnDefs[11].cellFilter).toEqual('plsCurrency');
        expect(scope.invoicesGrid.columnDefs[11].width).toEqual('8%');

        expect(scope.invoicesGrid.columnDefs[12].field).toEqual('paidDue');
        expect(scope.invoicesGrid.columnDefs[12].displayName).toEqual('Paid Due');
        expect(scope.invoicesGrid.columnDefs[12].cellFilter).toEqual('plsCurrency');
        expect(scope.invoicesGrid.columnDefs[12].width).toEqual('8%');

        expect(scope.invoicesGrid.columnDefs[13].field).toEqual('dueDate');
        expect(scope.invoicesGrid.columnDefs[13].displayName).toEqual('Due Date');
        expect(scope.invoicesGrid.columnDefs[13].cellFilter).toEqual('date:wideAppDateFormat');
        expect(scope.invoicesGrid.columnDefs[13].width).toEqual('7%');

        expect(scope.invoicesGrid.plugins.length).toBe(3);
    });

    it('should check cbi details grid', function() {
        expect(scope.cbiDetailsGrid).toBeDefined();
        expect(scope.cbiDetailsGrid.enableColumnResize).toBeTruthy();
        expect(scope.cbiDetailsGrid.data).toEqual('cbiItems');
        expect(scope.cbiDetailsGrid.multiSelect).toBeFalsy();
        expect(scope.cbiDetailsGrid.selectedItems).toEqual(scope.selectedInvoices);
        expect(scope.cbiDetailsGrid.columnDefs.length).toBe(15);

        expect(scope.cbiDetailsGrid.columnDefs[0].field).toEqual('adjustment');

        expect(scope.cbiDetailsGrid.columnDefs[1].field).toEqual('invoiceNumber');
        expect(scope.cbiDetailsGrid.columnDefs[1].displayName).toEqual('Invoice #');
        expect(scope.cbiDetailsGrid.columnDefs[1].width).toEqual('6%');

        expect(scope.cbiDetailsGrid.columnDefs[2].field).toEqual('loadId');
        expect(scope.cbiDetailsGrid.columnDefs[2].displayName).toEqual('Load ID');
        expect(scope.cbiDetailsGrid.columnDefs[2].width).toEqual('5%');

        expect(scope.cbiDetailsGrid.columnDefs[3].field).toEqual('bol');
        expect(scope.cbiDetailsGrid.columnDefs[3].displayName).toEqual('BOL');
        expect(scope.cbiDetailsGrid.columnDefs[3].width).toEqual('7%');

        expect(scope.cbiDetailsGrid.columnDefs[4].field).toEqual('pro');
        expect(scope.cbiDetailsGrid.columnDefs[4].displayName).toEqual('PRO #');
        expect(scope.cbiDetailsGrid.columnDefs[4].width).toEqual('7%');

        expect(scope.cbiDetailsGrid.columnDefs[5].field).toEqual('po');
        expect(scope.cbiDetailsGrid.columnDefs[5].displayName).toEqual('PO #');
        expect(scope.cbiDetailsGrid.columnDefs[5].width).toEqual('7%');

        expect(scope.cbiDetailsGrid.columnDefs[6].field).toEqual('glNumber');
        expect(scope.cbiDetailsGrid.columnDefs[6].displayName).toEqual('GL #');
        expect(scope.cbiDetailsGrid.columnDefs[6].width).toEqual('6%');

        expect(scope.cbiDetailsGrid.columnDefs[7].field).toEqual('origin');
        expect(scope.cbiDetailsGrid.columnDefs[7].displayName).toEqual('Origin');
        expect(scope.cbiDetailsGrid.columnDefs[7].width).toEqual('12%');

        expect(scope.cbiDetailsGrid.columnDefs[8].field).toEqual('destination');
        expect(scope.cbiDetailsGrid.columnDefs[8].displayName).toEqual('Dest.');
        expect(scope.cbiDetailsGrid.columnDefs[8].width).toEqual('12%');

        expect(scope.cbiDetailsGrid.columnDefs[9].field).toEqual('carrierName');
        expect(scope.cbiDetailsGrid.columnDefs[9].displayName).toEqual('Carrier');
        expect(scope.cbiDetailsGrid.columnDefs[9].width).toEqual('10%');

        expect(scope.cbiDetailsGrid.columnDefs[10].field).toEqual('acc');
        expect(scope.cbiDetailsGrid.columnDefs[10].displayName).toEqual('Acc Charges');
        expect(scope.cbiDetailsGrid.columnDefs[10].cellFilter).toEqual('plsCurrency');
        expect(scope.cbiDetailsGrid.columnDefs[10].width).toEqual('5%');
        
        expect(scope.cbiDetailsGrid.columnDefs[11].field).toEqual('fs');
        expect(scope.cbiDetailsGrid.columnDefs[11].displayName).toEqual('FS');
        expect(scope.cbiDetailsGrid.columnDefs[11].cellFilter).toEqual('plsCurrency');
        expect(scope.cbiDetailsGrid.columnDefs[11].width).toEqual('5%');

        expect(scope.cbiDetailsGrid.columnDefs[12].field).toEqual('totalRevenue');
        expect(scope.cbiDetailsGrid.columnDefs[12].displayName).toEqual('Total Revenue');
        expect(scope.cbiDetailsGrid.columnDefs[12].cellFilter).toEqual('plsCurrency');
        expect(scope.cbiDetailsGrid.columnDefs[12].width).toEqual('5%');
        
        expect(scope.cbiDetailsGrid.columnDefs[13].field).toEqual('totalCost');
        expect(scope.cbiDetailsGrid.columnDefs[13].displayName).toEqual('Total Cost');
        expect(scope.cbiDetailsGrid.columnDefs[13].cellFilter).toEqual('plsCurrency');
        expect(scope.cbiDetailsGrid.columnDefs[13].width).toEqual('5%');

        expect(scope.cbiDetailsGrid.columnDefs[14].field).toEqual('paidAmount');
        expect(scope.cbiDetailsGrid.columnDefs[14].displayName).toEqual('Paid');
        expect(scope.cbiDetailsGrid.columnDefs[14].cellFilter).toEqual('plsCurrency');
        expect(scope.cbiDetailsGrid.columnDefs[14].width).toEqual('5%');
        expect(scope.cbiDetailsGrid.columnDefs[14].searchable).toBeFalsy();

        expect(scope.cbiDetailsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.cbiDetailsGrid.filterOptions).toBeDefined();
        expect(scope.cbiDetailsGrid.filterOptions.filterText).toEqual("");
        expect(scope.cbiDetailsGrid.filterOptions.useExternalFilter).toBeFalsy();

        expect(scope.cbiDetailsGrid.plugins.length).toBe(3);
        expect(scope.cbiDetailsGrid.progressiveSearch).toBeTruthy();
    });

    it('should update grid records', function() {
        scope.invoicesGrid.ngGrid = {
            filteredRows: [{
                selected: true,
            }]
        };
        scope.recordsUpdate();

        expect(scope.gridRecords).toBe(1);
    });

    it('should trigger "filteredRows" watch', function() {
        scope.invoicesGrid.ngGrid = {
            filteredRows: [{
                selected: true,
            }]
        };
        scope.$digest();

        expect(scope.gridRecords).toBe(1);
    });
});