/**
 * Tests RateQuoteCtrl controller.
 *
 * @author Sergey Kirichenko
 */
describe('RateQuoteCtrl (rate-quote-controllers) Controller Test.', function() {

    var mockAddressesListService = {
        listUserContacts : function() {
        }
    };

    var mockShipmentUtils = {
            addAddressNotificationsToLoadNotificationsWithoutDuplicates : function() {
            }
    };

    var timeout = function() {
    };
  
    // angular scope
    var scope = undefined;

    //RateQuoteCtrl controller
    var controller = undefined;

    var emptyShipment = {
        originDetails: {
            accessorials: []
        },
        destinationDetails: {
            accessorials: []
        },
        finishOrder: {
            quoteMaterials: [],
            pickupDate: JSON.stringify(new Date()).replace(/\"/g, '')
        },
        status: 'OPEN'
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller, LinkedListUtils) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.wizardData = {
                emptyShipment: angular.copy(emptyShipment),
                selectedCustomer: { id: 1, name: 'Test' },
                shipment: {
                    finishOrder: {
                        pickupDate: '2013-08-20T00:00:00.000',
                        quoteMaterials: [
                            { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                                productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
                        ]
                    },
                    originDetails: {
                        zip: {
                            zip: '22222',
                            country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                            state: 'VA', city: 'ARLINGTON'
                        }
                    },
                    destinationDetails: {
                        zip: {
                            zip: '10101',
                            country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                            state: 'NY', city: 'NEW YORK'
                        }
                    },
                    selectedProposition: { ref: 'test-reference'}
                }
            };
            scope.addressesForm = {};
            scope.wizardData.steps = LinkedListUtils.getLinkedList();
            scope.wizardData.steps.add('rate_quote');
            scope.wizardData.steps.add('select_carrier');
            scope.wizardData.steps.add('build_order');
            scope.wizardData.steps.add('finish_order');
            scope.wizardData.steps.add('finish_quote');
            scope.wizardData.step = scope.wizardData.steps.first();
            scope.pickup = { country : 'USA' };
            scope.deliver = { country : 'USA' };
        });
        controller = $controller('RateQuoteCtrl', 
                {$scope: scope, AddressesListService: mockAddressesListService, $timeout: timeout, ShipmentUtils: mockShipmentUtils});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.openCopyFromDialog).to.be.a('function');
        c_expect(scope.getQuote).to.be.a('function');
        c_expect(scope.clearAll).to.be.a('function');
    });

    it('should call openCopyFromDialog properly', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope, '$broadcast');
        scope.openCopyFromDialog();
        c_expect(scope.$broadcast.calls.count()).to.equal(1);
        c_expect(scope.$broadcast.calls.mostRecent().args).to.eql(['event:openCopyFromsDialog', scope.wizardData.selectedCustomer]);
    });

    it('should call getQuote properly', function() {
        c_expect(controller).to.be.an('object');
        scope.getQuote();
        c_expect(scope.wizardData.step).to.equal('select_carrier');
    });

    function checkEmitCall() {
        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args).to.eql(['event:application-error', 'Shipment validation failed!', 'Please fill necessary data.']);
    }

    it('should call getQuote with error (pickupZip is not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.originDetails.zip = undefined;
        });
        scope.getQuote();
        checkEmitCall();
    });

    it('should call getQuote with error (deliveryZip is not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.destinationDetails.zip = undefined;
        });
        scope.getQuote();
        checkEmitCall();
    });

    it('should call getQuote with error (materials are empty)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.quoteMaterials = [];
        });
        scope.getQuote();
        checkEmitCall();
    });

    it('should call getQuote with error (pickupDate is not defined)', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope.$root, '$emit');
        scope.$apply(function() {
            scope.wizardData.shipment.finishOrder.pickupDate = undefined;
        });
        scope.getQuote();
        checkEmitCall();
    });

    it('should call clearAll properly', function() {
        c_expect(controller).to.be.an('object');
        spyOn(scope, '$broadcast').and.callThrough();
        scope.$apply(function() {
            scope.pickup.country = 'CAN';
            scope.deliver.country = 'CAN';
            scope.wizardData.step = 'build_order';
            scope.rateQuoteDictionary = {};
            scope.$on('event:pls-clear-form-data', function() {
                scope.pickup.country = 'USA';
                scope.deliver.country = 'USA';
            });
        });
        scope.clearAll();
        c_expect(scope.wizardData.shipment).to.be.an('object');
        c_expect(scope.wizardData.shipment).to.eql(emptyShipment);
        c_expect(scope.pickup).to.be.an('object');
        c_expect(scope.pickup.country).to.equal('USA');
        c_expect(scope.deliver).to.be.an('object');
        c_expect(scope.deliver.country).to.equal('USA');
        c_expect(scope.wizardData.step).to.equal('rate_quote');
        c_expect(scope.$broadcast.calls.count()).to.equal(2);
        c_expect(scope.$broadcast.calls.mostRecent().args).to.eql(['event:pls-clear-form-data']);
    });
});