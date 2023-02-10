/**
 * Tests for pls process invoice dialog directive.
 *
 * @author: Dmitry Nikolaenko
 */
describe('PLS process invoice dialog directive test.', function () {
    var rootScope = undefined;
    var scope = undefined;
    var element = undefined;
    var promiseProvider = undefined;

    var selectedInvoices = [{
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
    }];

    var emails = [
        {"key":9,"value":"Denver.Takemoto@test.com"},
        {"key":19,"value":"Melissia.Ordway@test.com"}
    ];
    var billToList = [
        {id:1,name:"Haynes International, INC.",processType:"TRANSACTIONAL"}
    ];

    var invoiceCallback = [{
        adjustmentId:9,
        billToId:6,
        bol:"20121007-6-1",
        cost:174.45,
        doNotInvoice:true,
        finalizationStatus:"ACCOUNTING_BILLING_RELEASE",
        invoiceNumber:"T-155-AD01",
        loadId:155,
        rebill:false,
        revenue:234.23
    }];

    var mockBillToEmailService = {
        getEmails: function (params, successCallback) {
            successCallback(emails);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    var mockFinancialBoardService = {
        processInvoices: function (params, successCallback) {
            successCallback(invoiceCallback);
        }
    };

    var mockFinancialBoardCBIService = {
        getNextInvoiceNumberForCBI: function (params, successCallback) {
            successCallback(invoiceNumber);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/process-invoice-dialog.html', function ($provide) {
        $provide.factory('BillToEmailService', function () {
            return mockBillToEmailService;
        });
        $provide.factory('FinancialBoardService', function () {
            return mockFinancialBoardService;
        });
        $provide.factory('FinancialBoardCBIService', function () {
            return mockFinancialBoardCBIService;
        });
    }));

    beforeEach(inject(function ($compile, $rootScope, $q) {
        rootScope = $rootScope;
        promiseProvider = $q;

        element = angular.element('<div data-pls-process-invoice-dialog></div>');
        $compile(element)(rootScope);
        rootScope.$digest();
        scope = element.isolateScope();
    }));

    it('should check process invoice dialog', function() {
        expect(scope.processConfirmationDialog).toBeDefined();
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].headerCellTemplate).toEqual('pages/cellTemplate/adjustment-header-cell.html');
        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.processConfirmationDialog.emails).toEqual('');
        expect(scope.processConfirmationDialog.sendEmail).toBeTruthy();
        expect(scope.processConfirmationDialog.invoicesGrid).toBeDefined();
        expect(scope.processConfirmationDialog.invoicesGrid.enableColumnResize).toBeTruthy();
        expect(scope.processConfirmationDialog.invoicesGrid.data).toEqual('processingInvoices');
        expect(scope.processConfirmationDialog.invoicesGrid.enableRowSelection).toBeFalsy();
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs.length).toBe(3);
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].field).toEqual('compoundSortField');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].headerClass).toEqual('cellToolTip');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].cellClass).toEqual('cellToolTip');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].width).toEqual('6%');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].searchable).toBeFalsy();
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[0].sortable).toBeTruthy();
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[1].field).toEqual('loadId');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[1].displayName).toEqual('Load ID');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[1].width).toBe('47%');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[2].field).toEqual('bolNumber');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[2].displayName).toEqual('BOL');
        expect(scope.processConfirmationDialog.invoicesGrid.columnDefs[2].width).toBe('47%');
        expect(scope.processConfirmationDialog.invoicesGrid.plugins.length).toBe(1);
        expect(scope.processConfirmationDialog.invoicesGrid.progressiveSearch).toBeFalsy();
    });

    it('should check process result dialog', function() {
        expect(scope.processResultsDialog).toBeDefined();
        expect(scope.processResultsDialog.show).toBeFalsy();
        expect(scope.processResultsDialog.invoicesGrid).toBeDefined();
        expect(scope.processResultsDialog.invoicesGrid.enableColumnResize).toBeTruthy();
        expect(scope.processResultsDialog.invoicesGrid.data).toEqual('processResultsDialog.resultsForSelectedBillTo');
        expect(scope.processResultsDialog.invoicesGrid.enableRowSelection).toBeFalsy();
        expect(scope.processResultsDialog.invoicesGrid.columnDefs.length).toBe(6);
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].field).toEqual('compoundSortField');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].headerClass).toEqual('cellToolTip');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].cellClass).toEqual('cellToolTip');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].width).toEqual('5%');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].searchable).toBeFalsy();
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[0].sortable).toBeTruthy();
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[1].field).toEqual('invoiceNumber');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[1].displayName).toEqual('Invoice#');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[1].width).toBe('15%');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[2].field).toEqual('loadId');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[2].displayName).toEqual('Load ID');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[2].width).toBe('15%');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[3].field).toEqual('bol');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[3].displayName).toEqual('BOL');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[3].width).toBe('20%');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[4].field).toEqual('errorMessage ? "Failed" : "Successful"');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[4].displayName).toEqual('Status');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[4].width).toBe('10%');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[5].field).toEqual('errorMessage');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[5].displayName).toEqual('Error');
        expect(scope.processResultsDialog.invoicesGrid.columnDefs[5].width).toBe('35%');
        expect(scope.processResultsDialog.invoicesGrid.plugins.length).toBe(1);
        expect(scope.processResultsDialog.invoicesGrid.progressiveSearch).toBeFalsy();
    });

    it('should open and update process invoice dialog', function () {
        spyOn(mockBillToEmailService, 'getEmails').and.callThrough();
        scope.$apply(function() {
            scope.$broadcast('openProcessInvoiceDialog', {
                customer: selectedInvoices[0].customerName,
                allInvoices: selectedInvoices,
                selectedBillToList: billToList
            });
        });

        expect(scope.processConfirmationDialog.customer).toEqual(selectedInvoices[0].customerName);
        expect(scope.allInvoices).toEqual(selectedInvoices);
        expect(scope.processConfirmationDialog.invoicesCount).toBe(selectedInvoices.length);
        expect(scope.selectedBillToList).toEqual(billToList);
        expect(scope.processConfirmationDialog.billTo).toBe(billToList[0]);
        expect(scope.invoiceDate).toBeUndefined();
        expect(scope.selectedBillToList[0].subject).toBeDefined();
        expect(scope.selectedBillToList[0].subject).toEqual('PLS-Invoice-T-' + selectedInvoices[0].loadId + '-AD00');
        expect(scope.selectedBillToList[0].sendEmail).toBeDefined();

        expect(scope.processConfirmationDialog.sendEmail).toBeDefined();
        expect(scope.processConfirmationDialog.emails).toBeDefined();
        expect(scope.processConfirmationDialog.subject).toBeDefined();
        expect(mockBillToEmailService.getEmails).toHaveBeenCalled();
        expect(scope.processConfirmationDialog.show).toBeTruthy();
    });

    it('should process invoices to finance', function() {
        scope.selectedBillToList = billToList;
        scope.allInvoices.push({billToId: 1});
        spyOn(mockFinancialBoardService, 'processInvoices').and.callThrough();

        scope.processInvoicesToFinance();

        expect(mockFinancialBoardService.processInvoices).toHaveBeenCalled();
        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.processResultsDialog.results).toBeDefined();
        expect(scope.processResultsDialog.successfulCount).toBe(1);
    });

    it('should close process results dialog', function() {
        scope.processResultsDialog.show = true;
        scope.processConfirmationDialog.comments = 'test comments';
        spyOn(scope, '$emit').and.callThrough();

        scope.closeProcessResultsDialog();

        expect(scope.processResultsDialog.show).toBeFalsy();
        expect(scope.processConfirmationDialog.comments).toBe('');
        expect(scope.$emit).toHaveBeenCalledWith('event:updateDataAfterProcessing');
    });

    xit('should process invoices to finance with failed result', function() {
        scope.cbiModel.selectedCBIInvoices = [{
            billToId: 7
        }];
        scope.selectedInvoices = [{},{}];
        processCBIData = [{
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 10
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_HOLD',
            errorMessage: 'test',
            revenue: 7,
            cost: 6
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 5
        }];
        var processCBIDataCopy = angular.copy(processCBIData);
        scope.processConfirmationDialog.emails = 'test@email.com';
        scope.cbiModel.filterDate = '06/11/2015';

        scope.processInvoicesToFinance();
        timeoutService.flush();

        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.selectedInvoices.length).toBe(0);
        c_expect(scope.processResultsDialog.results).to.deep.equal(processCBIDataCopy);
        expect(scope.processResultsDialog.successfulCount).toBe(2);
        expect(scope.processResultsDialog.failedCount).toBe(1);
        expect(scope.processResultsDialog.invoiceNumber).toBeUndefined();
        expect(scope.processResultsDialog.totalRevenue).toBe(20);
        expect(scope.processResultsDialog.totalCost).toBe(15);
        expect(scope.processResultsDialog.show).toBeTruthy();
    });

    xit('should process invoices to finance with successful result', function() {
        scope.cbiModel.selectedCBIInvoices = [{
            billToId: 7
        }];
        scope.selectedInvoices = [{},{}];
        processCBIData = [{
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 10,
            invoiceNumber: 'C-12345-235'
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 7,
            cost: 6
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 5
        }];
        var processCBIDataCopy = angular.copy(processCBIData);
        scope.processConfirmationDialog.emails = 'test@email.com';
        scope.cbiModel.filterDate = '06/11/2015';

        scope.processInvoicesToFinance();
        timeoutService.flush();

        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.selectedInvoices.length).toBe(0);
        c_expect(scope.processResultsDialog.results).to.deep.equal(processCBIDataCopy);
        expect(scope.processResultsDialog.successfulCount).toBe(3);
        expect(scope.processResultsDialog.failedCount).toBe(0);
        expect(scope.processResultsDialog.invoiceNumber).toBe('C-12345');
        expect(scope.processResultsDialog.totalRevenue).toBe(27);
        expect(scope.processResultsDialog.totalCost).toBe(21);
        expect(scope.processResultsDialog.show).toBeTruthy();
    });

    xit('should process invoices to finance with incorrect CBI number', function() {
        scope.cbiModel.selectedCBIInvoices = [{
            billToId: 7
        }];
        scope.selectedInvoices = [{},{}];
        processCBIData = [{
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 10,
            invoiceNumber: 'C-12345235'
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 7,
            cost: 6
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 5
        }];
        var processCBIDataCopy = angular.copy(processCBIData);
        scope.processConfirmationDialog.emails = 'test@email.com';
        scope.cbiModel.filterDate = '06/11/2015';

        scope.processInvoicesToFinance();
        timeoutService.flush();

        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.selectedInvoices.length).toBe(0);
        c_expect(scope.processResultsDialog.results).to.deep.equal(processCBIDataCopy);
        expect(scope.processResultsDialog.successfulCount).toBe(3);
        expect(scope.processResultsDialog.failedCount).toBe(0);
        expect(scope.processResultsDialog.invoiceNumber).toBe('C-12345235');
        expect(scope.processResultsDialog.totalRevenue).toBe(27);
        expect(scope.processResultsDialog.totalCost).toBe(21);
        expect(scope.processResultsDialog.show).toBeTruthy();
    });

    xit('should process results with different finalization statuses', function() {
        scope.cbiModel.selectedCBIInvoices = [{
            billToId: 7
        }];
        scope.selectedInvoices = [{},{}];
        processCBIData = [{
            finalizationStatus: 'ACCOUNTING_BILLING_RELEASE',
            revenue: 10,
            cost: 10,
            invoiceNumber: 'C-12345235-1'
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING',
            revenue: 7,
            cost: 6
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_HOLD',
            revenue: 10,
            errorMessage: 'error1',
            cost: 5
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL',
            revenue: 10,
            cost: 5
        }, {
            finalizationStatus: 'ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL',
            revenue: 10,
            errorMessage: 'error2',
            cost: 5
        }];
        var processCBIDataCopy = angular.copy(processCBIData);
        scope.processConfirmationDialog.emails = 'test@email.com';
        scope.cbiModel.filterDate = '06/11/2015';

        scope.processInvoicesToFinance();
        timeoutService.flush();

        expect(scope.processConfirmationDialog.show).toBeFalsy();
        expect(scope.selectedInvoices.length).toBe(0);

        processCBIDataCopy[1].errorMessage = 'Unexpected Error Occurred';
        processCBIDataCopy[3].errorMessage = 'Unexpected Error Occurred';

        c_expect(scope.processResultsDialog.results).to.deep.equal(processCBIDataCopy);
        expect(scope.processResultsDialog.successfulCount).toBe(1);
        expect(scope.processResultsDialog.failedCount).toBe(4);
        expect(scope.processResultsDialog.invoiceNumber).toBeUndefined();
        expect(scope.processResultsDialog.totalRevenue).toBe(10);
        expect(scope.processResultsDialog.totalCost).toBe(10);
        expect(scope.processResultsDialog.show).toBeTruthy();
    });
});