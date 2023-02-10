/**
 * Tests ShipmentDetailsTrackingCtrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('ShipmentDetailsTrackingCtrl (shipment-details-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //ShipmentDetailsTrackingCtrl controller
    var controller = undefined;

    var shipment = {
        id: 1, status: 'DISPATCHED',
        finishOrder: {
            pickupDate: '2013-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ]
        },
        details: {
            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };
    var events = [
        {date: new Date(), fullName: 'User 1', event: 'Event 1'},
        {date: new Date(), fullName: 'User 2', event: 'Event 2'},
        {date: new Date(), fullName: 'User 3', event: 'Event 3'}
    ];

    var mockShipmentDetailsService = {
        findShipmentEvents: function(params, success) {
            success(events);
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.shipmentDetailsModel = {
                shipment: angular.copy(shipment),
                loadingIndicator: {},
                selectedCustomer: {id: 1, name: undefined}
            };
        });
        controller = $controller('ShipmentDetailsTrackingCtrl', {$scope: scope, ShipmentDetailsService: mockShipmentDetailsService});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.shipmentDetailsModel.selectedRows).to.be.an('array');
        c_expect(scope.shipmentDetailsModel.selectedRows).to.be.empty();
        c_expect(scope.shipmentDetailsModel.shipmentTrackingGridOptions).to.be.an('object');
    });

    it('should call initTab properly', function() {
        spyOn(mockShipmentDetailsService, 'findShipmentEvents').and.callThrough();
        scope.initTab();
        c_expect(mockShipmentDetailsService.findShipmentEvents.calls.count()).to.equal(1);
        c_expect(mockShipmentDetailsService.findShipmentEvents.calls.mostRecent().args[0]).to.eql({customerId:1,shipmentId:1});
        c_expect(mockShipmentDetailsService.findShipmentEvents.calls.mostRecent().args[1]).to.be.a('function');
        c_expect(scope.shipmentDetailsModel.shipmentTrackingGridData).to.eql(events);
    });

    it('should call initTab on event', function() {
        spyOn(scope, 'initTab');
        scope.$broadcast('event:shipmentDetailsLoaded');
        c_expect(scope.initTab.calls.count()).to.equal(1);
    });
});