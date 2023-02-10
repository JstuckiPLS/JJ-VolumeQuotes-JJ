describe('Unit test for TrackingBoardAllShipmentsController', function() {
    var scope;
    var rootScope;
    var urlConfig = {};
    var window;
    var filter;
    var location;
    var trackingBoardService;
    var shipmentsService;
    var pluginFactory;
    var dateTimeUtils;
    var customerService;

    var httpBackend;

    var shipments = {
        "bolNumber":"123","carrier":"KINGSWAY TRANSPORT","createdDate":"2010-12-29",
        "customerId":1,"revenue":225.5211,"margin":20.501, "total" : 14.811
    };

    var trackingBoardService = {
        all : function(params, success) {
            success({
                result: shipments
            });
        }
    };

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        window = $injector.get('$window');
        filter = $injector.get('$filter');
        location = $injector.get('$location');
        shipmentsService = $injector.get('ShipmentDetailsService');
        pluginFactory = $injector.get('NgGridPluginFactory');
        dateTimeUtils = $injector.get('DateTimeUtils');
        customerService = $injector.get('CustomerService');

        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        rootScope.authData.privilegies.push('CAN_VIEW_BUSINESS_UNIT');
        var controller = $injector.get('$controller');
        controller('TrackingBoardAllShipmentsController', {
            '$scope': scope,
            '$rootScope': rootScope,
            'urlConfig': urlConfig,
            '$window': window,
            '$filter': filter,
            '$location': location,
            'TrackingBoardService': trackingBoardService,
            'ShipmentDetailsService': shipmentsService, 
            'NgGridPluginFactory': pluginFactory,
            'DateTimeUtils': dateTimeUtils,
            'CustomerService': customerService
        });

        httpBackend = $injector.get('$httpBackend');
    }));

    xit('should check alert count is zero', function() {
        spyOn(customerService, 'getAccountExecutives');
        httpBackend.when('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();
        expect(customerService.getAccountExecutives).toHaveBeenCalled();
    });

    it('should check initial controller state', function() {
        expect(scope.searchDateSelectorValues.length).toBe(3);
        expect(scope.searchDateSelectorValues[0]).toEqual({code : 'PICKUP', name :'Pickup Date'});
        expect(scope.searchDateSelectorValues[1]).toEqual({code : 'CREATED', name :'Created Date'});
        expect(scope.searchDateSelectorValues[2]).toEqual({code : 'DELIVERY', name :'Delivery Date'});

        expect(scope.searchDateSelector).toEqual('PICKUP');

        expect(scope.dateRangeSelectorValues.length).toBe(5);
        expect(scope.dateRangeSelectorValues[0]).toEqual({code : 'YESTERDAY', name :'Yesterday'});
        expect(scope.dateRangeSelectorValues[1]).toEqual({code : 'TODAY', name :'Today'});
        expect(scope.dateRangeSelectorValues[2]).toEqual({code : 'THIS_WEEK', name :'This week'});
        expect(scope.dateRangeSelectorValues[3]).toEqual({code : 'THIS_MONTH', name :'This month'});
        expect(scope.dateRangeSelectorValues[4]).toEqual({code : 'CUSTOM', name :'Custom range'});

        expect(scope.dateRangeSelector).toEqual('CUSTOM');
        expect(rootScope.isAllShipmentsPage).toBeTruthy();
        expect(scope.manualRangeType).toEqual('DEFAULT');
        expect(scope.defaultSelectionRangeType).toEqual('MONTH');
        expect(scope.totalSumm).toBe(0.0);

        expect(scope.allShipmentsEntries.length).toBe(0);
        expect(scope.selectedEntries.length).toBe(0);
        expect(scope.toDateAlreadyExists).toBeFalsy();
        expect(scope.validData).toBeTruthy();
        expect(scope.toDateForPeriod).toEqual(scope.toDate);
        expect(scope.fromDateForPeriod).toEqual(scope.fromDate);

        expect(scope.sortInfo).toEqual({columns: [], fields: [], directions: []});
    });

    it('should return true when custom date range selector selected', function() {
        scope.dateRangeSelector = 'CUSTOM';
        expect(scope.isCustomDateRangeSelected()).toBeTruthy();
    });

    it('should return false when other than custom date range selector selected', function() {
        scope.dateRangeSelector = 'OTHER';
        expect(scope.isCustomDateRangeSelected()).toBeFalsy();
    });

    it('should set date to yesterday', function() {
        scope.dateRangeSelector = 'YESTERDAY';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        var today = new Date();
        expect(scope.toDate.toString()).toEqual(dateTimeUtils.addDays(today, -1).toString());
        expect(scope.fromDate.toString()).toEqual(dateTimeUtils.addDays(today, -1).toString());
    });

    it('should set leave today"s date', function() {
        scope.dateRangeSelector = 'TODAY';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        var today = new Date();
        expect(scope.toDate.toString()).toEqual(today.toString());
        expect(scope.fromDate.toString()).toEqual(today.toString());
    });

    it('should set date to this week', function() {
        scope.dateRangeSelector = 'THIS_WEEK';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        var today = new Date();
        expect(scope.toDate.toString()).toEqual(today.toString());
        expect(scope.fromDate.toString()).toEqual(dateTimeUtils.addDays(today, -(today.getDay())).toString());
    });

    it('should set date to this month', function() {
        scope.dateRangeSelector = 'THIS_MONTH';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        var today = new Date();
        expect(scope.toDate.toString()).toEqual(today.toString());
        expect(scope.fromDate.toString()).toEqual(dateTimeUtils.addDays(today, -(today.getDate()-1)).toString());
    });

    it('should leave custom date unchanged', function() {
        scope.dateRangeSelector = 'CUSTOM';
        scope.fromDate = '2015-02-03';
        scope.toDate = '2015-03-03';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        expect(scope.fromDate.toString()).toEqual('2015-02-03');
        expect(scope.toDate.toString()).toEqual('2015-03-03');
    });

    it('should trigger fromDate watch', function() {
        scope.fromDate = '2015-03-12';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        scope.$digest();

        expect(scope.minToDate).toBeDefined();
        expect(scope.maxToDate).toBeDefined();
    });

    it('should trigger fromDate watch', function() {
        scope.fromDate = '2015-03-12';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        spyOn(scope, 'updatePickupDateRangeIsEmpty');
        scope.$digest();

        expect(scope.minToDate).toBeDefined();
        expect(scope.maxToDate).toBeDefined();
        expect(scope.updatePickupDateRangeIsEmpty).toHaveBeenCalled();
    });

    it('should trigger toDate watch', function() {
        scope.toDate = '2015-05-12';
        httpBackend.expect('GET', '/restful/customer/accountExec').respond({});
        spyOn(scope, 'updatePickupDateRangeIsEmpty');
        scope.$digest();

        expect(scope.minFromDate).toBeDefined();
        expect(scope.maxFromDate).toBeDefined();
        expect(scope.updatePickupDateRangeIsEmpty).toHaveBeenCalled();
    });

    it('should update pickup date range', function() {
        scope.toDate = undefined;
        scope.updatePickupDateRangeIsEmpty();

        expect(scope.toDateAlreadyExists).toBeFalsy();
        expect(scope.validData).toBeTruthy();
    });

    it('should clear form validation', function() {
        scope.toDate = '2015-03-12';
        scope.updatePickupDateRangeIsEmpty();

        expect(scope.toDateAlreadyExists).toBeFalsy();
        expect(scope.validData).toBeTruthy();
    });

    it('should set toDate to true if carrier filter selected only', function() {
        scope.toDateAlreadyExists = false;
        scope.loadId = '1';
        scope.carrier = 'carrier';
        scope.fromDate = undefined;
        scope.toDate = undefined;
        scope.pro = undefined;
        scope.searchAllShipmentsEntries();

        expect(scope.toDateAlreadyExists).toBeTruthy();
    });

    it('should set toDate to true if origin and destination filters selected', function() {
        scope.toDateAlreadyExists = false;
        scope.loadId = '1';
        scope.origin = 'origin';
        scope.destination = 'destination';
        scope.fromDate = undefined;
        scope.toDate = undefined;
        scope.pro = undefined;
        scope.searchAllShipmentsEntries();

        expect(scope.toDateAlreadyExists).toBeTruthy();
    });

    it('should set toDate to true if origin filter selected only', function() {
        scope.toDateAlreadyExists = false;
        scope.loadId = '1';
        scope.origin = 'origin';
        scope.fromDate = undefined;
        scope.toDate = undefined;
        scope.pro = undefined;
        scope.searchAllShipmentsEntries();

        expect(scope.toDateAlreadyExists).toBeTruthy();
    });

    it('should set toDate to true if destination filter selected only', function() {
        scope.toDateAlreadyExists = false;
        scope.loadId = '1';
        scope.destination = 'destination';
        scope.fromDate = undefined;
        scope.toDate = undefined;
        scope.pro = undefined;
        scope.searchAllShipmentsEntries();

        expect(scope.toDateAlreadyExists).toBeTruthy();
    });

    it('should fetch all shipment entries', function() {
        scope.loadId = '2';
        scope.selectedEntries.push({name: 'test entry'});
        spyOn(trackingBoardService, 'all').and.callThrough();
        scope.searchAllShipmentsEntries();

        expect(trackingBoardService.all).toHaveBeenCalled();
        expect(scope.selectedEntries.length).toBe(0);
        expect(scope.totals.totalRevenue).toBe('225.52');
        expect(scope.totals.totalCost).toBe('14.81');
        expect(scope.totals.totalMargin).toBe('20.50');
    });

    it('should clear search criteria', function() {
        spyOn(scope, '$broadcast');
        scope.clearSearchCriteria();

        expect(scope.keywords).toBeNull();
        expect(scope.sortConfig).toBeNull();
        expect(scope.accountExecutive).toBeUndefined();
        expect(scope.allShipmentsEntries.length).toBe(0);
        expect(scope.selectedEntries.length).toBe(0);
        expect(scope.totalSumm).toBe(0.0);
        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
    });

    it('should search all shipments when data updated', function() {
        spyOn(scope, 'searchAllShipmentsEntries');
        scope.$broadcast('event:shipmentDataUpdated');

        expect(scope.searchAllShipmentsEntries).toHaveBeenCalled();
    });

    it('should view shipment details', function() {
        var entry = {name: 'test entry'};
        scope.selectedEntries.push(entry);
        spyOn(scope, 'viewShipmentDetails');
        scope.view();

        expect(scope.viewShipmentDetails).toHaveBeenCalledWith(entry);
    });

    it('should check close handler', function() {
        spyOn(scope, 'searchAllShipmentsEntries');
        scope.closeHandler();

        expect(scope.searchAllShipmentsEntries).toHaveBeenCalled();
        expect(scope.selectedEntries.length).toBe(0);
    });

    it('should open terminal info dialog', function() {
        spyOn(scope, '$broadcast');
        var shipmentId = 1;
        scope.openTerminalInfoModalDialog(shipmentId);

        expect(scope.$broadcast).toHaveBeenCalledWith('event:openTerminalInfoDialog', shipmentId);
    });

    it('should check search is triggered by hitting enter', function() {
        var event = {which: 13};
        spyOn(scope, 'searchAllShipmentsEntries');

        scope.keyPressed(event);

        expect(scope.searchAllShipmentsEntries).toHaveBeenCalled();
    });

    it('should check all shipments grid', function() {
        expect(scope.allShipmentsGrid).toBeDefined();
        expect(scope.allShipmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.allShipmentsGrid.data).toEqual('allShipmentsEntries');
        expect(scope.allShipmentsGrid.multiSelect).toBeFalsy();
        expect(scope.allShipmentsGrid.selectedItems.length).toEqual(0);

        expect(scope.allShipmentsGrid.columnDefs[0].field).toEqual('indicators');
        expect(scope.allShipmentsGrid.columnDefs[0].displayName).toEqual('Indicators');

        expect(scope.allShipmentsGrid.columnDefs[1].field).toEqual('shipmentId');
        expect(scope.allShipmentsGrid.columnDefs[1].displayName).toEqual('Load ID');

        expect(scope.allShipmentsGrid.columnDefs[2].field).toEqual('bolNumber');
        expect(scope.allShipmentsGrid.columnDefs[2].displayName).toEqual('BOL');
        expect(scope.allShipmentsGrid.columnDefs[2].showTooltip).toBeTruthy();

        expect(scope.allShipmentsGrid.columnDefs[3].field).toEqual('pieces');
        expect(scope.allShipmentsGrid.columnDefs[3].displayName).toEqual('QTY');

        expect(scope.allShipmentsGrid.columnDefs[4].field).toEqual('weight');
        expect(scope.allShipmentsGrid.columnDefs[4].displayName).toEqual('Total Weight');

        expect(scope.allShipmentsGrid.columnDefs[5].field).toEqual('soNumber');
        expect(scope.allShipmentsGrid.columnDefs[5].displayName).toEqual('SO#');

        expect(scope.allShipmentsGrid.columnDefs[6].field).toEqual('glNumber');
        expect(scope.allShipmentsGrid.columnDefs[6].displayName).toEqual('GL#');

        expect(scope.allShipmentsGrid.columnDefs[7].field).toEqual('proNumber');
        expect(scope.allShipmentsGrid.columnDefs[7].displayName).toEqual('Pro#');

        expect(scope.allShipmentsGrid.columnDefs[8].field).toEqual('refNumber');
        expect(scope.allShipmentsGrid.columnDefs[8].displayName).toEqual('Shipper Ref#');

        expect(scope.allShipmentsGrid.columnDefs[9].field).toEqual('network');
        expect(scope.allShipmentsGrid.columnDefs[9].displayName).toEqual('Business Unit');

        expect(scope.allShipmentsGrid.columnDefs[10].field).toEqual('accountExecutive');
        expect(scope.allShipmentsGrid.columnDefs[10].displayName).toEqual('Account Executive');

        expect(scope.allShipmentsGrid.columnDefs[11].field).toEqual('poNumber');
        expect(scope.allShipmentsGrid.columnDefs[11].displayName).toEqual('PO#');

        expect(scope.allShipmentsGrid.columnDefs[12].field).toEqual('puNumber');
        expect(scope.allShipmentsGrid.columnDefs[12].displayName).toEqual('PU#');

        expect(scope.allShipmentsGrid.columnDefs[13].field).toEqual('jobNumber');
        expect(scope.allShipmentsGrid.columnDefs[13].displayName).toEqual('JOB#');

        expect(scope.allShipmentsGrid.columnDefs[14].field).toEqual('shipper');
        expect(scope.allShipmentsGrid.columnDefs[14].displayName).toEqual('Shipper');
        expect(scope.allShipmentsGrid.columnDefs[14].headerClass).toEqual('text-center');
        expect(scope.allShipmentsGrid.columnDefs[14].cellClass).toEqual('text-center');

        expect(scope.allShipmentsGrid.columnDefs[15].field).toEqual('origin');
        expect(scope.allShipmentsGrid.columnDefs[15].displayName).toEqual('Origin');
        expect(scope.allShipmentsGrid.columnDefs[15].cellFilter).toEqual('zip');

        expect(scope.allShipmentsGrid.columnDefs[16].field).toEqual('consignee');
        expect(scope.allShipmentsGrid.columnDefs[16].displayName).toEqual('Consignee');
        expect(scope.allShipmentsGrid.columnDefs[16].headerClass).toEqual('text-center');
        expect(scope.allShipmentsGrid.columnDefs[16].cellClass).toEqual('text-center');

        expect(scope.allShipmentsGrid.columnDefs[17].field).toEqual('destination');
        expect(scope.allShipmentsGrid.columnDefs[17].displayName).toEqual('Destination');
        expect(scope.allShipmentsGrid.columnDefs[17].cellFilter).toEqual('zip');

        expect(scope.allShipmentsGrid.columnDefs[18].field).toEqual('carrier');
        expect(scope.allShipmentsGrid.columnDefs[18].displayName).toEqual('Carrier');

        expect(scope.allShipmentsGrid.columnDefs[19].field).toEqual('integrationType');
        expect(scope.allShipmentsGrid.columnDefs[19].displayName).toEqual('EDI');
        expect(scope.allShipmentsGrid.columnDefs[19].cellFilter).toEqual('integrationTypeFilter');

        expect(scope.allShipmentsGrid.columnDefs[20].field).toEqual('pickupDate');
        expect(scope.allShipmentsGrid.columnDefs[20].displayName).toEqual('Pickup Date');
        expect(scope.allShipmentsGrid.columnDefs[20].cellFilter).toEqual('date:wideAppDateFormat');

        expect(scope.allShipmentsGrid.columnDefs[21].field).toEqual('dispatchedDate');
        expect(scope.allShipmentsGrid.columnDefs[21].displayName).toEqual('Dispatched Date');

        expect(scope.allShipmentsGrid.columnDefs[22].field).toEqual('deliveryDate');
        expect(scope.allShipmentsGrid.columnDefs[22].displayName).toEqual('Del. Date');
        expect(scope.allShipmentsGrid.columnDefs[22].cellTemplate).toEqual('<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<span data-ng-bind="_.isUndefined(row.entity.deliveryDate) ?\'N/A\':row.entity.deliveryDate|date:appDateFormat">' +
                '</span></div>');

        expect(scope.allShipmentsGrid.columnDefs[23].field).toEqual('status');
        expect(scope.allShipmentsGrid.columnDefs[23].displayName).toEqual('Ops. Status');
        expect(scope.allShipmentsGrid.columnDefs[23].cellFilter).toEqual('shipmentStatus');

        expect(scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')).toBeTruthy();
        expect(scope.$root.isFieldRequired).toHaveBeenCalledWith('CAN_VIEW_BUSINESS_UNIT');

        expect(scope.allShipmentsGrid.action).toEqual(jasmine.any(Function));
        expect(scope.allShipmentsGrid.refreshTable).toEqual(jasmine.any(Function));
        expect(scope.allShipmentsGrid.plugins.length).toBe(5);
        expect(scope.allShipmentsGrid.tooltipOptions.url).toEqual('pages/content/quotes/shipments-grid-tooltip.html');
        expect(scope.allShipmentsGrid.tooltipOptions.onShow).toBeTruthy();
        expect(scope.allShipmentsGrid.progressiveSearch).toBeTruthy();
        expect(scope.allShipmentsGrid.sortInfo).toBeTruthy();
    });
});