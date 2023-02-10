/**
 * Tests AccountHistoryCtrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('AccountHistoryCtrl (account-history-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //AccountHistoryCtrl controller
    var controller = undefined;

    var historyResult = [
        { bolNumber: "20121222-6-1", carrier: "FedEx", createdDate: "2013-10-23T00:00:00.000", deliveryDate: "2013-09-26T00:00:00.000",
            destination: "CLEVELAND, OH, 44111", glNumber: "00000001", origin: "BATON ROUGE, LA, 70816", pickupDate: "2013-08-23T00:00:00.000",
            poNumber: "20121222-6-2", puNumber: "20121222-6-4", refNumber: "20121222-6-5", shipmentId: 514, soNumber: "00000002",
            status: "Delivered", total: 164.77, totalString: "$164.77", trailer: "00000003", userName: "RICH LITTON" },
        { bolNumber: "20130923-11-30", carrier: "AVERITT EXPRESS", createdDate: "2012-12-29T00:00:00.000", deliveryDate: "2013-09-23T00:00:00.000",
            destination: "INDIANAPOLIS, IN, 46218", glNumber: "00000001", origin: "BLAINE, MN, 55449", pickupDate: "2013-09-23T00:00:00.000",
            poNumber: "20130923-11-30", puNumber: "20130923-11-30", refNumber: "20130923-11-30", shipmentId: 718, soNumber: "00000002",
            status: "Delivered", total: 100, totalString: "$100", trailer: "00000003", userName: "PEDRO GOMEZ" }
    ];

    var windowService = {
        open: function() { /*fake function*/ }
    };

    var locationService = {
        url: function() {
            return {
                search: function() { /*fake function*/ }
            };
        }
    };

    var accountHistoryService = {
        get: function(params, success) {
            success(angular.copy(historyResult));
        }
    };

    var shipmentsService = {
        getTooltipData: function(params, success) {
            success({});
        }
    };

    var exportService = {
        exportData: function(exportData) {
            windowService.open('/restful/export/report?uuid=' + 'fakeUuid', '_blank');
        }
    };

    var initializeGrid = function(gridOptions) {
        if ($('body').find('#content').length) {
            $('div[data-ng-show="showGridTooltip"]').remove();
            $('#content').remove();
        }
        var elm = angular.element('<div id="content"><div data-ng-grid="accountHistoryGrid" style="width: 1000px; height: 1000px"></div></div>');
        scope.myData = historyResult;

        scope.accountHistoryGrid = $.extend(true, { data: 'myData' }, gridOptions);

        $('body').append(compileService(elm)(scope));
        scope.$digest();
    };

    var pluginFactory, compileService;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller, NgGridPluginFactory, $compile) {
        scope = $rootScope.$new();

        scope.$apply(function() {
            scope.$root.authData.organization.orgId = 1;
            scope.$root.authData.personId = 1;
            scope.origin = "BATON ROUGE, LA, 70816";
            scope.destination = "CLEVELAND, OH, 44111";
            scope.carrier = "FedEx";
            scope.fromDate = "2013-09-23T00:00:00.000";
            scope.toDate = "2013-10-23T00:00:00.000";
        });
        spyOn(scope, '$on').and.callThrough();

        pluginFactory = NgGridPluginFactory;
        compileService = $compile;

        controller = $controller('AccountHistoryCtrl', {$scope: scope, $window: windowService, $location: locationService,
            AccountHistoryService: accountHistoryService, ShipmentDetailsService: shipmentsService, ExportService: exportService});
        scope.$digest();
    }));

    var clearSearchCriteria = function() {
        scope.keywords = null;
        scope.sortConfig = null;
        scope.accountHistoryEntries.length = 0;
        scope.selectedEntries.length = 0;
        scope.dateRange = 'MONTH';
        scope.dateRangeType = 'MONTH';
        scope.fromDate = undefined;
        scope.toDate = undefined;
        scope.origin = undefined;
        scope.destination = undefined;
        scope.carrier = undefined;
        scope.pro = undefined;
        scope.bol = undefined;
    };

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.manualRangeType).to.equal('DEFAULT');
        c_expect(scope.defaultSelectionRangeType).to.equal('MONTH');
        c_expect(scope.totalSumm).to.equal(0.0);
        c_expect(scope.accountHistoryEntries).to.be.an('array');
        c_expect(scope.accountHistoryEntries).to.be.empty();
        c_expect(scope.selectedEntries).to.be.an('array');
        c_expect(scope.selectedEntries).to.be.empty();
        c_expect(scope.sortInfo).to.eql({ columns: [], fields: [], directions: [] });
        c_expect(scope.searchAccountHistoryEntries).to.be.a('function');
        c_expect(scope.clearSearchCriteria).to.be.a('function');
        c_expect(scope.$on.calls.count()).to.equal(2);
        c_expect(scope.$on.calls.argsFor(0)[0]).to.equal('event:shipmentDataUpdated');
        c_expect(scope.$on.calls.argsFor(1)[0]).to.equal('event:closeAndRedirect');
        c_expect(scope.view).to.be.a('function');
        c_expect(scope.exportAccountHistory).to.be.a('function');
        c_expect(scope.keyPressed).to.be.a('function');
        c_expect(scope.accountHistoryGrid).to.be.an('object');
        c_expect(scope.accountHistoryGrid).not.to.be.empty();
        c_expect(scope.detailsGrid).to.be.an('object');
        c_expect(scope.detailsGrid).to.be.empty();
    });

    it('should call search properly', function() {
        spyOn(accountHistoryService, 'get').and.callThrough();
        scope.selectedEntries.push(historyResult[0]);
        c_expect(scope.selectedEntries).not.to.be.empty();
        scope.searchAccountHistoryEntries();
        c_expect(accountHistoryService.get.calls.count()).to.equal(1);
        c_expect(scope.accountHistoryEntries).to.eql(historyResult);
        c_expect(scope.selectedEntries).to.be.empty();
        c_expect(scope.progressPanelOptions.showPanel).to.be.false();
    });

    it('should not execute search account history api call', function() {
        spyOn(accountHistoryService, 'get').and.callThrough();
        scope.selectedEntries.push(historyResult[0]);
        c_expect(scope.selectedEntries).not.to.be.empty();
        clearSearchCriteria();
        c_expect(scope.selectedEntries).to.be.empty();
        scope.searchAccountHistoryEntries();
        c_expect(accountHistoryService.get.calls.count()).to.equal(0);
    });

    it('should call clear search properly', function() {
        scope.$apply(function() {
            scope.keywords = 'search data';
            scope.sortConfig = 'sort data';
            scope.accountHistoryEntries = angular.copy(historyResult);
            scope.selectedEntries.push(historyResult[0]);
            scope.dateRange = 'new range';
            scope.dateRangeType = 'new range type';
        });
        clearSearchCriteria();
        c_expect(scope.keywords).to.be.null();
        c_expect(scope.sortConfig).to.be.null();
        c_expect(scope.accountHistoryEntries).to.be.empty();
        c_expect(scope.selectedEntries).to.be.empty();
        c_expect(scope.dateRange).to.equal(scope.defaultSelectionRangeType);
        c_expect(scope.dateRangeType).to.equal(scope.defaultSelectionRangeType);
    });

    it('should reload list when shipment change event is raised', function() {
        spyOn(scope, 'searchAccountHistoryEntries').and.callFake(function() {});
        scope.$broadcast('event:shipmentDataUpdated');
        c_expect(scope.searchAccountHistoryEntries.calls.count()).to.equal(1);
    });

    it('should raise show shipment event when view is called', function() {
        scope.$apply(function() {
            scope.selectedEntries.push(angular.copy(historyResult[0]));
        });
        var eventParams;
        scope.$on('event:showShipmentDetails', function(event, dialogDetails) {
            eventParams = dialogDetails;
        });
        scope.view();
        c_expect(eventParams).to.be.an('object');
        c_expect(eventParams).to.eql({shipmentId: 514, selectedTab: 'tracking'});
    });

    it('should export history properly', function() {
        var gridOptions = {
            columnDefs: scope.accountHistoryGrid.columnDefs,
            progressiveSearch: true,
            useExternalSorting: true,
            plugins: [pluginFactory.progressiveSearchPlugin()]
        };
        initializeGrid(gridOptions);

        spyOn(windowService, 'open').and.callThrough();
        scope.$apply(function() {
            scope.sortInfo.fields = ['bolNumber'];
            scope.sortInfo.directions = ['ASC'];
            scope.keywords = 'test';
            scope.dateRange = 'WEEK';
        });
        scope.exportAccountHistory();
        c_expect(windowService.open.calls.count()).to.equal(1);
    });

    it('should redirect when close event is raised', function() {
        var searchParams;
        spyOn(locationService, 'url').and.callFake(function() {
            return { search: function(params) {
                searchParams = params;
            }};
        });
        scope.$broadcast('event:closeAndRedirect', 'test url', {param1: 1, param2: 2});
        c_expect(locationService.url.calls.count()).to.equal(1);
        c_expect(locationService.url.calls.mostRecent().args).to.eql(['test url']);
        c_expect(searchParams).to.eql({param1: 1, param2: 2});
    });

    it('should total value updated properly', function() {
        c_expect(scope.totalSumm).to.eql(0.0);
        var gridOptions = {
            columnDefs: scope.accountHistoryGrid.columnDefs,
            progressiveSearch: true,
            useExternalSorting: true,
            plugins: [pluginFactory.progressiveSearchPlugin()]
        };
        initializeGrid(gridOptions);
        c_expect(scope.totalSumm).to.eql(264.77);
        scope.$apply(function() {
            scope.accountHistoryGrid.ngGrid.filteredRows = [];
        });
        c_expect(scope.totalSumm).to.eql(0.0);
    });
});