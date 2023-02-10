/**
 * Tests ShipmentDetailsSpecialitiesCtrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('ShipmentDetailsSpecialitiesCtrl (shipment-details-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //ShipmentDetailsSpecialitiesCtrl controller
    var controller = undefined;

    var shipment = {
        id: 1, status: 'DISPATCHED',
        finishOrder: {
            pickupDate: '2013-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false },
                { weight: 2000, commodityClass: 'CLASS_100', quantity: 100, packageType: 'PL', productCode: 'S1534-7',
                    productDescription: 'TVST', nmfc: 117354, hazmat: true, stackable: false }
            ],
            shipmentNotifications: [
                { emailAddress: 'test@test.com' }
            ]
        },
        details: {
            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.shipmentDetailsModel = {
                shipment: angular.copy(shipment),
                loadingIndicator: {},
                fullViewDocOption: {},
                emailOptions: {},
                customer: {id: 1, name: undefined},
                userId: 1
            };
            scope.closeDialogBeforeRedirect = function() { /*fake function*/ };
        });
        controller = $controller('ShipmentDetailsSpecialitiesCtrl', {$scope: scope});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.shipmentDetailsModel.hazmatInfoType).to.be.null();
        c_expect(scope.shipmentDetailsModel.hasHazmat).to.be.false();
        c_expect(scope.shipmentDetailsModel.hazmatProduct).to.be.an('object');
        c_expect(scope.shipmentDetailsModel.hazmatProduct).to.be.empty();
        c_expect(scope.shipmentDetailsModel.hazmatProducts).to.be.an('array');
        c_expect(scope.shipmentDetailsModel.hazmatProducts).to.be.empty();
        c_expect(scope.shipmentDetailsModel.productPrimarySort).to.be.null();
        c_expect(scope.shipmentDetailsModel.shipmentNotifications).to.be.an('array');
        c_expect(scope.shipmentDetailsModel.shipmentNotifications).to.be.empty();
        c_expect(scope.shipmentDetailsModel.selectedEmail).to.be.undefined;
        c_expect(scope.initTab).to.be.a('function');
        c_expect(scope.getEmails).to.be.a('function');
        c_expect(scope.isSelected).to.be.a('function');
        c_expect(scope.notificationStyle).to.be.a('function');
    });

    it('should call initTab properly', function() {
        scope.initTab();
        c_expect(scope.shipmentDetailsModel.shipmentNotifications).to.eql(scope.shipmentDetailsModel.shipment.finishOrder.shipmentNotifications);
        c_expect(scope.shipmentDetailsModel.hazmatProducts).not.to.be.empty();
        c_expect(scope.shipmentDetailsModel.hasHazmat).to.be.true();
        c_expect(scope.shipmentDetailsModel.hazmatProduct).to.eql(scope.shipmentDetailsModel.shipment.finishOrder.quoteMaterials[1]);
        c_expect(scope.shipmentDetailsModel.hazmatInfoType).to.equal('single_hazmat');
    });

    it('should call initTab on event', function() {
        spyOn(scope, 'initTab');
        scope.$broadcast('event:shipmentDetailsLoaded');
        c_expect(scope.initTab.calls.count()).to.equal(1);
    });

    it('should call getEmails properly', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipmentNotifications = [
                {emailAddress: 'test1@test.com'}
            ];
        });
        var result = scope.getEmails();
        c_expect(result).to.be.an('array');
        c_expect(result.length).to.equal(1);
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipmentNotifications = [
                {emailAddress: 'test1@test.com'},
                {emailAddress: 'test2@test.com'}
            ];
        });
        result = scope.getEmails();
        c_expect(result).to.be.an('array');
        c_expect(result.length).to.equal(2);
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipmentNotifications = [
                {emailAddress: 'test1@test.com'},
                {emailAddress: 'test2@test.com'},
                {emailAddress: 'test2@test.com'}
            ];
        });
        result = scope.getEmails();
        c_expect(result).to.be.an('array');
        c_expect(result.length).to.equal(2);
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipmentNotifications = [];
        });
        result = scope.getEmails();
        c_expect(result).to.be.an('array');
        c_expect(result).to.be.empty();
    });

    it('should check whether notification selected or not', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipmentNotifications = [
                {emailAddress: 'test1@test.com', notificationType: 'type1'}
            ];
            scope.shipmentDetailsModel.selectedEmail = 'test1@test.com';
        });
        c_expect(scope.isSelected('type1')).to.be.true();
        c_expect(scope.isSelected('type2')).to.be.false();
    });

    it('should return notification type based on selection', function() {
        c_expect(scope.notificationStyle(false)).to.eql({visibility: 'hidden'});
        c_expect(scope.notificationStyle(true)).to.be.an('object');
        c_expect(scope.notificationStyle(true)).to.be.empty();
    });
});