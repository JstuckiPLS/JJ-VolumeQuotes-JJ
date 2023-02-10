/**
 * Tests SalesOrderForVendorBillController controller.
 *
 * @author Denis Zhupinsky
 */
describe('SalesOrderForVendorBillController (vendor-bill-controllers) Controller Test.', function () {
    // angular scope
    var scope = undefined;

    //SalesOrderForVendorBillController controller
    var controller = undefined;

    var salesOrders = [
        {},
        {}
    ];

    var mockVendorBillService = {
        getMatchedSalesOrders: function (params, success) {
            success(salesOrders);
        },
        match: function (params, success) {
            success();
        },
        get: function (params, success) {
            success(salesOrders);
        }
    };

    var mockNgGridPluginFactory = {
        progressiveSearchPlugin: function () {
        },
        actionPlugin: function () {
        },
        plsGrid: function() {
        }
    };

    var mockShipmentUtils = {
            showUnmatchCarrierWarning : function() {
            }
        };

    var mockSelectedSalesOrder = {shipmentId: Math.floor((Math.random() * 100) + 1)};

    var mockVendorBillId = Math.floor((Math.random() * 100) + 1);

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        controller = $controller('SalesOrderForVendorBillController', {
            $scope: scope,
            VendorBillService: mockVendorBillService,
            NgGridPluginFactory: mockNgGridPluginFactory,
            ShipmentUtils: mockShipmentUtils
        });

        scope.$apply(function () {
            scope.searchSalesOrderModel.vendorBillId = mockVendorBillId;

            scope.searchSalesOrderModel.selectedSaleOrders[0] = mockSelectedSalesOrder;
            scope.searchSalesOrderModel.vendorBill = {
                    carrier: 1,
                    scac: "AXA"
            }
        });
    }));

    it('should refresh table', function () {
        spyOn(mockVendorBillService, 'getMatchedSalesOrders').and.callThrough();
        scope.$digest();

        scope.findSalesOrders();

        c_expect(mockVendorBillService.getMatchedSalesOrders.calls.count()).to.equal(1);
        c_expect(mockVendorBillService.getMatchedSalesOrders.calls.mostRecent().args[0]).to.eql({vendorBillId: mockVendorBillId});

        c_expect(scope.salesOrders).to.equal(salesOrders);
    });

    it('should refresh table - search', function () {
        spyOn(mockVendorBillService, 'getMatchedSalesOrders').and.callThrough();
        var bolNumber = '1111';
        var proNumber = '2222';
        scope.searchSalesOrderModel.search = {bol : bolNumber, pro : proNumber};

        scope.$digest();

        scope.findSalesOrders();

        c_expect(mockVendorBillService.getMatchedSalesOrders.calls.count()).to.equal(1);
        c_expect(mockVendorBillService.getMatchedSalesOrders.calls.mostRecent().args[0]).to.eql({
            vendorBillId: mockVendorBillId, bol : bolNumber, pro: proNumber
        });

        c_expect(scope.salesOrders).to.equal(salesOrders);
    });

    it('should react on launch event', function () {
        var mockCloseHandler = function () {
        };

        spyOn(scope, 'findSalesOrders').and.callThrough();
        scope.$digest();

        scope.$broadcast('event:showSearchSalesOrder', {vendorBillId: 1, closeHandler: mockCloseHandler });

        c_expect(scope.findSalesOrders.calls.count()).to.equal(1);
        c_expect(scope.searchSalesOrderModel.vendorBillId).to.equal(1);
        c_expect(scope.searchSalesOrderModel.closeHandler).to.equal(mockCloseHandler);
        c_expect(scope.searchSalesOrderModel.showDialog).to.be.true();
    });

    it('should close dialog', function () {
        scope.$apply(function () {
            scope.searchSalesOrderModel.showDialog = true;
        });

        scope.closeSearchDialog();
        c_expect(scope.searchSalesOrderModel.showDialog).to.be.false();
    });

    it('should do "Attach Vendor Bill"', function () {
        spyOn(mockVendorBillService, 'match').and.callThrough();
        spyOn(scope, 'closeSearchDialog').and.callThrough();

        scope.$apply(function () {
            scope.searchSalesOrderModel.closeHandler = function () {
            };
        });

        spyOn(scope.searchSalesOrderModel, 'closeHandler').and.callThrough();
        scope.$digest();

        scope.attachVendorBill();

        c_expect(mockVendorBillService.match.calls.count()).to.equal(1);
        c_expect(mockVendorBillService.match.calls.mostRecent().args[0]).to.eql({
            vendorBillId: mockVendorBillId, shipmentId: mockSelectedSalesOrder.shipmentId
        });

        c_expect(scope.closeSearchDialog.calls.count()).to.equal(1);
        c_expect(scope.searchSalesOrderModel.closeHandler.calls.count()).to.equal(1);
    });

    it('should view sales order', function () {
        spyOn(scope.$root, '$broadcast').and.callThrough();
        scope.$digest();

        scope.viewOrder();

        c_expect(scope.$root.$broadcast.calls.count()).to.equal(1);
        c_expect(scope.$root.$broadcast.calls.mostRecent().args[0]).to.equal('event:showEditSalesOrder');
        c_expect(scope.$root.$broadcast.calls.mostRecent().args[1]).to.eql({
            shipmentId: mockSelectedSalesOrder.shipmentId,
            formDisabled: true,
            parentDialog: "searchSalesOrderDialog",
            closeHandler : scope.findSalesOrders
        });
    });
});