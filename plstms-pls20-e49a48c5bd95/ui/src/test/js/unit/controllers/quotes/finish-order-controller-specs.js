/**
 * Tests FinishOrderCtrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('FinishOrderCtrl (rate-quote-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    //FinishOrderCtrl controller
    var controller = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller, LinkedListUtils) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.wizardData = {
                shipment: {
                    cargoValue: 'cargo-num-1',
                    finishOrder: {
                        pickupDate: '2013-08-20T00:00:00.000',
                        poNumber: 'po-num-1',
                        puNumber: 'pu-num-1',
                        soNumber: 'so-num-1',
                        glNumber: 'gl-num-1',
                        requestedBy: 'Some dude',
                        trailerNumber: 'trailer-num-1',
                        pickupWindowFrom: { hours: 2, minutes: 30, am: true },
                        pickupWindowTo: { hours: 7, minutes: 30, am: true },
                        deliveryWindowFrom: { hours: 2, minutes: 30, am: true },
                        deliveryWindowTo: { hours: 7, minutes: 30, am: true },
                        quoteMaterials: [
                            { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                                productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
                        ]
                    },
                    details: {
                        pickupZip: {
                            zip: '22222',
                            country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                            state: 'VA', city: 'ARLINGTON'
                        },
                        deliverZip: {
                            zip: '10101',
                            country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                            state: 'NY', city: 'NEW YORK'
                        }
                    },
                    selectedProposition: { ref: 'test-reference'},
                    bolNumber : 'bol-num-1'
                }
            };
            scope.wizardData.steps = LinkedListUtils.getLinkedList();
            scope.wizardData.steps.add('rate_quote');
            scope.wizardData.steps.add('select_carrier');
            scope.wizardData.steps.add('build_order');
            scope.wizardData.steps.add('finish_order');
            scope.wizardData.steps.add('finish_quote');
            scope.wizardData.step = scope.wizardData.steps.find('finish_order');
            scope.isFieldRequired = function() {
                return true;
            };
            scope.invalidIdentifier = {};
        });
        controller = $controller('FinishOrderCtrl', {$scope: scope});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.back).to.be.a('function');
        c_expect(scope.showPickupMessage).to.be.a('function');
        c_expect(scope.next).to.be.a('function');
    });

    it('should call back properly', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.wizardData.step).to.equal('finish_order');
        scope.back();
        c_expect(scope.wizardData.step).to.equal('build_order');
    });

    it('should call showPickupMessage properly', function() {
        c_expect(controller).to.be.an('object');
        scope.$apply(function() {
            scope.pickUpWindowOpen = false;
        });
        scope.showPickupMessage();
        c_expect(scope.pickUpWindowOpen).to.be.true();
    });

    it('should call next properly', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.wizardData.step).to.equal('finish_order');
        scope.next();
        c_expect(scope.wizardData.step).to.equal('finish_quote');
    });

    it('should call next with showing pickup message', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope, 'showPickupMessage').and.callThrough();
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.pickupWindowTo = { hours: 2, minutes: 45, am: true };
            scope.pickUpWindowOpen = false;
        });
        scope.next();
        c_expect(scope.showPickupMessage.calls.count()).to.equal(1);
        c_expect(scope.pickUpWindowOpen).to.be.true();
        c_expect(scope.wizardData.step).to.equal('finish_order');
    });

    it('should call showPickupMessage properly', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope, 'showPickupMessage').and.callThrough();
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.pickupWindowTo = { hours: 3, minutes: 30, am: true };
            scope.pickUpWindowOpen = false;
        });
        scope.next();
        c_expect(scope.showPickupMessage.calls.count()).to.equal(0);
        c_expect(scope.pickUpWindowOpen).to.be.false();
    });

    function checkEmitCall() {
        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args).to.eql(['event:application-error', 'Shipment validation failed!',
            'Required fields are not specified.']);
    }

    it('should call next with error (ref number is mandatory and not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.selectedProposition.ref = undefined;
            scope.invalidIdentifier.sr = true;
        });
        scope.next();
        checkEmitCall();
    });

    it('should call next with error (po number is mandatory and not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.poNumber = undefined;
            scope.invalidIdentifier.po = true;
        });
        scope.next();
        checkEmitCall();
    });

    it('should call next with error (pu number is mandatory and not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.puNumber = undefined;
            scope.invalidIdentifier.pu = true;
        });
        scope.next();
        checkEmitCall();
    });
});