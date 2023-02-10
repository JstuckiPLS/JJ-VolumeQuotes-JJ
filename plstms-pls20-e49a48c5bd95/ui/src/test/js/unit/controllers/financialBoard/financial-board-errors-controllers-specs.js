/**
 * Tests FinancialBoardErrorsController controller.
 *
 * @author Denis Zhupinsky
 */
describe('FinancialBoardErrorsController (financial-board-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //FinancialBoardErrorsController controller
    var controller = undefined;

    var localUrlConfig = undefined;

    var invoiceErrors = [
        {dateTime : "09/25/2013 09:30:00", userName : "test user", event : "event1", message : "message1"},
        {dateTime : "09/25/2013 09:00:00", userName : "test user", event : "event2", message : "message2"}
    ];

    var reProcessErrorsSuccessfulCallback = {
        invoiceNumbers: [{invoiceNumber: "CB-LTL-0001033"}]
    };

    var getEmailSubjectForReprocessErrorSuccess = {
        data: "PLS-T-18007800-0000"
    };

    var windowService = {
        open: function() {}
    };

    var errorsCount = 10;

    var mockFinancialBoardInvoiceErrorsService = {
        invoiceErrors: function(params, success) {
            success(invoiceErrors);
        },
        errorsCount: function(params, success) {
            success({value : errorsCount});
        },
        cancelError: function(params, success) {
            success();
        },
        reProcessErrors: function(params, success) {
            success(reProcessErrorsSuccessfulCallback);
        },
        getEmailSubjectForReprocessError: function(params, success) {
            if (params.errorId == 1) {
                success(getEmailSubjectForReprocessErrorSuccess);
            } else {
                success({});
            }
        }
    };

    var mockNgGridPluginFactory = {
        progressiveSearchPlugin: function() {},
        plsGrid: function() {},
        hideColumnPlugin: function() {}
    };

    var exportService = {
        exportData: function(exportData) {
            windowService.open('/restful/export/report?uuid=' + 'fakeUuid', '_blank');
        }
    };

    var mockSelectedError = {id: Math.floor((Math.random() * 100) + 1)};

    var pluginFactory, compileService;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller, $compile, urlConfig, NgGridPluginFactory) {
        scope = $rootScope.$new();

        pluginFactory = NgGridPluginFactory;
        compileService = $compile;

        localUrlConfig = urlConfig;
        controller = $controller('FinancialBoardErrorsController', {
            $scope: scope,
            urlConfig: localUrlConfig,
            $window: windowService,
            FinancialBoardInvoiceErrorsService: mockFinancialBoardInvoiceErrorsService,
            NgGridPluginFactory: mockNgGridPluginFactory,
            ExportService: exportService
        });
        scope.$digest();
    }));


    it('should refresh table', function() {
        spyOn(mockFinancialBoardInvoiceErrorsService, 'invoiceErrors').and.callThrough();
        scope.loadInvoiceErrors();

        c_expect(mockFinancialBoardInvoiceErrorsService.invoiceErrors.calls.count()).to.equal(1);
        c_expect(mockFinancialBoardInvoiceErrorsService.invoiceErrors.calls.mostRecent().args[0]).to.be.empty();

        c_expect(scope.errors).to.equal(invoiceErrors);
    });

    it('should init properly', function() {
        spyOn(scope, 'loadInvoiceErrors').and.callThrough();
        scope.$digest();

        scope.init();
        c_expect(scope.loadInvoiceErrors.calls.count()).to.equal(1);
    });

    it('should refresh table with existing data and sorting, filtering', function() {
        spyOn(mockFinancialBoardInvoiceErrorsService, 'invoiceErrors').and.callThrough();
        scope.loadInvoiceErrors();

        c_expect(mockFinancialBoardInvoiceErrorsService.invoiceErrors.calls.count()).to.equal(1);

        c_expect(scope.errors).to.eql(invoiceErrors);
    });

    it('should cancel error correctly', function() {
        spyOn(mockFinancialBoardInvoiceErrorsService, 'cancelError').and.callThrough();
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(scope, 'loadInvoiceErrors').and.callThrough();
        scope.$apply(function () {
            scope.selectedErrors = [mockSelectedError];
        });

        scope.cancelError();

        c_expect(mockFinancialBoardInvoiceErrorsService.cancelError.calls.count()).to.equal(1);
        c_expect(mockFinancialBoardInvoiceErrorsService.cancelError.calls.mostRecent().args[0]).to.eql({errorId: mockSelectedError.id});

        c_expect(scope.$root.$broadcast.calls.count()).to.equal(5);
        c_expect(scope.$root.$broadcast.calls.mostRecent().args[0]).to.equal('event:financialBoardErrorsChanged');

        c_expect(scope.loadInvoiceErrors.calls.count()).to.equal(1);
        c_expect(scope.loadInvoiceErrors.calls.mostRecent().args).to.be.empty();
    });

    var initializeHelper = function(gridOptions) {
        if ($('body').find('#content').length) {
            $('div[data-ng-show="showGridTooltip"]').remove();
            $('#content').remove();
        }
        var elm = angular.element('<div id="content"><div data-ng-grid="gridOptions" style="width: 1000px; height: 1000px"></div></div>');
        scope.myData = [{}];

        scope.gridOptions = $.extend(true, { data: 'myData' }, gridOptions);

        $('body').append(compileService(elm)(scope));
        scope.$digest();
        scope.errorsGrid.ngGrid = scope.gridOptions;
    };

    it('should export errors', function() {
        spyOn(windowService, 'open').and.callThrough();
        spyOn(scope, '$emit');
        scope.$apply(function () {
            scope.errors = [{id:1}];
        });

        var gridOptions = {
            columnDefs: scope.errorsGrid.columnDefs,
            progressiveSearch: true,
            useExternalSorting: true,
            plugins: [pluginFactory.progressiveSearchPlugin()]
        };
        initializeHelper(gridOptions);

        c_expect(scope.$emit.calls.count()).to.equal(17);
        scope.exportErrors();
        c_expect(scope.$emit.calls.count()).to.equal(18);
    });

    it('should reprocess errors', function() {
        spyOn(mockFinancialBoardInvoiceErrorsService, 'reProcessErrors').and.callThrough();
        scope.$apply(function () {
            scope.selectedErrors = [mockSelectedError];
        });

        scope.reprocessInvoice();

        var result = {
            invoiceProcessingDetails: [{
                subject: undefined,
                comments: undefined
            }],
            errorId: mockSelectedError.id
        };
        if (mockSelectedError.id != 1) {
            c_expect(mockFinancialBoardInvoiceErrorsService.reProcessErrors.calls.count()).to.equal(1);
            c_expect(mockFinancialBoardInvoiceErrorsService.reProcessErrors.calls.mostRecent().args[0]).to.eql(result);
            c_expect(scope.reprocessDialog.show).to.be.false();
        }
    });
});