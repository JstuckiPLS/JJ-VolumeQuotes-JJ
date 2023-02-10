/**
 * Tests CopyFromCntrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('CopyFromCntrl (copy-from-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //CopyFromCntrl controller
    var controller = undefined;

    var createCopyLis = function(count) {
        var result = [];
        for (i = 1; i <= count; i++) {
            result.push({shipmentId: i, bolNumber: 'BOL:' + i, status: 'DISPATCHED'});
        }
        return result;
    };

    var shipment = {
        finishOrder: {
            pickupDate: '2013-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ]
        },
        originDetails: {
            zip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
        },
        destinationDetails: {
            zip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };

    var mockShipmentDetailsService = {
        last: function(params, success) {
            success({list: createCopyLis(params.count)});
        }
    };
    var mockShipmentOperationsService = {
            getCopiedShipment: function(params, success) {
                success(angular.copy(shipment));
            }
        };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller, LinkedListUtils) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.pickup = {};
            scope.deliver = {};
            scope.wizardData = {};
            scope.authData = {};
            scope.authData.organization = {
                orgId: 1,
                name: 'KMC Bicycle chain'
            };
        });
        controller = $controller('CopyFromCntrl', {$scope: scope, ShipmentDetailsService: mockShipmentDetailsService, ShipmentOperationsService: mockShipmentOperationsService});
        scope.wizardData.selectedCustomer = {
            id: 1
        };
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.closeCopyFrom).to.be.a('function');
        c_expect(scope.copyFromItems).to.be.an('array');
        c_expect(scope.copyFromItem).to.be.a('function');
        c_expect(scope.copyFromGridOptions).to.be.an('object');
    });

    it('should open dialog by event and close properly', function() {
        spyOn(mockShipmentDetailsService, 'last').and.callThrough();
        c_expect(scope.showCopyFromDialog).not.to.be.true();
        scope.$apply(function() {
            scope.$root.$broadcast('event:openCopyFromsDialog');
        });
        c_expect(scope.showCopyFromDialog).to.be.true();
        c_expect(scope.rangeType).to.equal('25');
        c_expect(mockShipmentDetailsService.last.calls.count()).to.equal(1);
        c_expect(mockShipmentDetailsService.last.calls.mostRecent().args[0]).to.eql({customerId:1,count:'25'});
        c_expect(mockShipmentDetailsService.last.calls.mostRecent().args[1]).to.be.a('function');
        scope.closeCopyFrom();
        c_expect(scope.showCopyFromDialog).not.to.be.true();
    });

    it('should load data when range is changed', function() {
        scope.$apply(function() {
            scope.$root.$broadcast('event:openCopyFromsDialog');
        });
        c_expect(scope.showCopyFromDialog).to.be.true();
        c_expect(scope.rangeType).to.equal('25');
        spyOn(mockShipmentDetailsService, 'last').and.callThrough();
        scope.$apply(function() {
            scope.rangeType = '30';
        });
        c_expect(mockShipmentDetailsService.last.calls.count()).to.equal(1);
        c_expect(mockShipmentDetailsService.last.calls.mostRecent().args[0]).to.eql({customerId:1,count:'30'});
        c_expect(mockShipmentDetailsService.last.calls.mostRecent().args[1]).to.be.a('function');
    });

    it('should call copy item properly', function() {
        scope.$apply(function() {
            scope.copyFromItems = createCopyLis(1);
        });
        spyOn(mockShipmentOperationsService, 'getCopiedShipment').and.callThrough();
        c_expect(scope.wizardData.shipment).not.to.be.difined;
        scope.copyFromItem();
        c_expect(mockShipmentOperationsService.getCopiedShipment.calls.count()).to.equal(1);
        c_expect(mockShipmentOperationsService.getCopiedShipment.calls.mostRecent().args[0]).to.eql({customerId:1,shipmentId:1});
        c_expect(mockShipmentOperationsService.getCopiedShipment.calls.mostRecent().args[1]).to.be.a('function');
        c_expect(scope.wizardData.shipment).to.be.an('object');
        c_expect(scope.wizardData.shipment.finishOrder).to.be.an('object');
        c_expect(scope.wizardData.shipment.finishOrder.pickupDate).to.be.a('string');
        var shipmentToCompare = angular.copy(shipment);
        shipmentToCompare.finishOrder.pickupDate = scope.wizardData.shipment.finishOrder.pickupDate;
        c_expect(scope.wizardData.shipment).to.eql(shipmentToCompare);
        c_expect(scope.pickup.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
        c_expect(scope.deliver.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
        c_expect(scope.showCopyFromDialog).to.be.false();
    });

    it('should not call copy item without selection', function() {
        spyOn(mockShipmentOperationsService, 'getCopiedShipment').and.callThrough();
        scope.copyFromItem();
        c_expect(mockShipmentOperationsService.getCopiedShipment.calls.count()).to.equal(0);
    });

    it('should copy form data', function() {
        spyOn(mockShipmentDetailsService, 'last').and.callThrough();

        scope.loadCopyFromData();

        expect(mockShipmentDetailsService.last).toHaveBeenCalledWith(scope.lastQueryParams, jasmine.any(Function));
        expect(scope.copyFromGridData).toBeDefined(); 
        expect(scope.copyFromGridData.list.length).toBe(25);
    });

    it('should reload grid when customer is changed', function() {
        spyOn(scope, 'loadCopyFromData');
        scope.wizardData.selectedCustomer.id = 3;
        scope.$apply(function() {
            scope.$root.$broadcast('event:openCopyFromsDialog');
        });

        scope.$digest();

        expect(scope.loadCopyFromData).toHaveBeenCalled();
    });
});