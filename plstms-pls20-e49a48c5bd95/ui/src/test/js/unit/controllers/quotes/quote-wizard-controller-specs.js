/**
 * Tests QuoteWizard controller.
 *
 * @author Sergey Kirichenko
 */
describe('QuoteWizard (rate-quote-controllers) Controller Test.', function() {

    // ANGULAR SERVICES
    var scope = undefined;
    var rootParams = undefined;
    var filter = undefined;

    //QuoteWizard controller
    var controller = undefined;

    var accessorialsService = undefined;

    var shipment = {
        id: 7073, createdUserId: 2, organizationId: 1, createdDate: '2013-08-20', status: 'BOOKED',
        finishOrder: {
            pickupDate: '2013-08-20',
            quoteMaterials: [
                { id: 7171, weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ],
            ref: 'test-reference'
        },
        originDetails: {
            zip: {
                zip: '22222',
                country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                state: 'VA', city: 'ARLINGTON'
            },
            address: { id: -1, addressName: 'ADDR_NAME135' },
            accessorials: [
                { id: 'LFC', name: 'Liftgate', group: 'PICKUP' },
            ]
        },
        destinationDetails: {
            zip: {
                zip: '10101',
                country: { id: 'USA', name: 'United States of America', dialingCode: '001' },
                state: 'NY', city: 'NEW YORK'
            },
            address: { id: -1, addressName: 'ADDR_NAME107' },
            accessorials: [
                { id: 'IDL', name: 'Inside', group: 'DELIVERY' },
                { id: 'RES', name: 'Residential', group: 'DELIVERY' }
            ]
        }
    };

    var mockShipmentOperationsService = {
        getCopiedShipment: function(params, success) {
            success(angular.copy(shipment));
        },
        getShipment: function(params, success) {
            success(angular.copy(shipment));
        }
    };

    var mockSavedQuotesService = {
        get: function(params, success) {
            success({shipmentDTO: angular.copy(shipment)});
        }
    };
    
    var mockCustomerLabelResource = {
        get: function(params, success) {
            success();
        }
    };

    var accessorialTypeServiceMock = {
        listAccessorialsByGroup: function() {
        }
    };

    beforeEach(module('plsApp'));
    beforeEach(module('plsApp', function($provide) {
        $provide.factory('ShipmentDictionaryService', function() {
            return {
                getPackageTypes: function() {
                    return {
                        success: function(handler) {
                            handler([{code: "BOX", label: "Boxes"}, {code: "ENV", label: "Envelopes"}, {code: "PLT", label: "Pallet"}]);
                        }
                    };
                },
                getBillToRequiredField: function() {
                    return {
                        success: function() {
                        }
                    };
                }
            };
        });
    }));

    beforeEach(inject(function($rootScope, $routeParams, $filter, PLSAccessorialsService) {
        scope = $rootScope.$new();
        rootParams = $routeParams;
        filter = $filter;
        accessorialsService = PLSAccessorialsService;
        scope.$apply(function() {
            scope.authData = {
                organization: {orgId:1, name: 'Test Organization'},
                personId: 1,
                fullName: 'Test User',
                plsUser: false,
                customerUser: true
            };
        });
    }));

    var emptyShipment = function(pickupDate) {
        return {
            originDetails: {
                accessorials: [],
                address: undefined
            },
            destinationDetails: {
                accessorials: [],
                address: undefined
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: filter('date')(pickupDate, 'yyyy-MM-dd'),
            },
            status: 'OPEN'
        };
    };

    var checkCommonParams = function(customerExists) {
        c_expect(controller).to.be.an('object');
        c_expect(scope.wizardData).to.be.an('object');
        c_expect(scope.wizardData.steps).to.be.an('object');
        c_expect(scope.wizardData.steps._size).to.equal(5);
        c_expect(scope.wizardData.steps.first()).to.equal('rate_quote');
        c_expect(scope.wizardData.steps.next()).to.equal('select_carrier');
        c_expect(scope.wizardData.steps.next()).to.equal('build_order');
        c_expect(scope.wizardData.steps.next()).to.equal('finish_order');
        c_expect(scope.wizardData.steps.next()).to.equal('finish_quote');
        c_expect(scope.wizardData.steps.next()).to.be.null();
        c_expect(scope.wizardData.allPickupAccessorials.length).to.equal(0);
        c_expect(scope.wizardData.allDeliveryAccessorials.length).to.equal(0);
        c_expect(scope.wizardData.minPickupDate).to.be.a('Date');
        c_expect(scope.wizardData.showCustomsBroker).to.be.false();
        c_expect(scope.isGuaranteed).to.be.a('function');
        c_expect(scope.getAvailableGuaranteedSorted).to.be.a('function');
        c_expect(scope.getCostDetailItemsForTotal).to.be.a('function');
        c_expect(scope.getSelectedGuaranteed).to.be.a('function');
        c_expect(scope.getSimilarCostDetailItem).to.be.a('function');
        c_expect(scope.getTotalCost).to.be.a('function');
        c_expect(scope.getCarrierTotalCost).to.be.a('function');
        c_expect(scope.calculateCost).to.be.a('function');
        c_expect(scope.getBaseRate).to.be.a('function');
        c_expect(scope.getAccessorials).to.be.a('function');

        c_expect(scope.wizardData.selectedCustomer).to.be.an('object');
        expect(scope.wizardData.selectedCustomer).toBeDefined();
        if (customerExists) {
            expect(scope.wizardData.selectedCustomer.id).toBeUndefined();
            expect(scope.wizardData.selectedCustomer.name).toBeUndefined();
        } else {
            c_expect(scope.wizardData.selectedCustomer.id).to.eql(1);
        }
        c_expect(scope.wizardData.emptyShipment).to.be.an('object');
        c_expect(scope.wizardData.emptyShipment).to.eql(emptyShipment(scope.wizardData.minPickupDate));
        c_expect(scope.rateQuoteDictionary).to.be.an('object');
    };

    describe('Default behaviour (without path params) for PLS User.', function() {
        beforeEach(inject(function($controller) {
            scope.authData.plsUser = true;
            controller = $controller('QuoteWizard', {$scope: scope, $routeParams: rootParams,
                ShipmentOperationsService: mockShipmentOperationsService, CustomerLabelResource: mockCustomerLabelResource,
                PLSAccessorialsService: accessorialsService, SavedQuotesService: mockSavedQuotesService, 
                AccTypesServices: accessorialTypeServiceMock});
        }));

        it('should load rate quote dictionary for PLS user when customer is changed', function() {
            c_expect(controller).to.be.an('object');
            c_expect(scope.rateQuoteDictionary).to.be.an('object');
            c_expect(scope.rateQuoteDictionary.classes).to.be.defined;
        });
    });

    describe('Default behaviour (without path params).', function() {
        beforeEach(inject(function($controller) {
            spyOn(accessorialsService, 'set').and.callThrough();
            spyOn(accessorialsService, 'isSet').and.callThrough();

            controller = $controller('QuoteWizard', {$scope: scope, $routeParams: rootParams,
                ShipmentOperationsService: mockShipmentOperationsService, CustomerLabelResource: mockCustomerLabelResource,
                PLSAccessorialsService: accessorialsService, SavedQuotesService: mockSavedQuotesService,
                AccTypesServices: accessorialTypeServiceMock});
        }));

        it('should be initialized with default parameters', function() {
            checkCommonParams(true);
            c_expect(scope.wizardData.shipment).to.be.an('object');
            c_expect(scope.wizardData.shipment).to.eql(emptyShipment(new Date()));
            c_expect(scope.pickup).to.be.an('object');
            c_expect(scope.pickup).to.eql({country : 'USA'});
            c_expect(scope.deliver).to.be.an('object');
            c_expect(scope.deliver).to.eql({country : 'USA'});
            c_expect(scope.wizardData.step).to.equal('rate_quote');
        });

        it('should load rate quote dictionary', function() {
            c_expect(controller).to.be.an('object');
            c_expect(scope.rateQuoteDictionary).to.be.an('object');
            c_expect(scope.rateQuoteDictionary.classes).to.be.defined;
        });
    });

    describe('Edit shipment (route params shipmentId is defined).', function() {
        beforeEach(inject(function($controller) {
            rootParams.shipmentId = 1;
            rootParams.stepName = 'finish_order';
            spyOn(mockShipmentOperationsService, 'getShipment').and.callThrough();
            controller = $controller('QuoteWizard', {$scope: scope, $routeParams: rootParams,
                CustomerLabelResource: mockCustomerLabelResource, PLSAccessorialsService: accessorialsService, 
                SavedQuotesService: mockSavedQuotesService, AccTypesServices: accessorialTypeServiceMock, ShipmentOperationsService: mockShipmentOperationsService});
        }));

        afterEach(function() {
            mockShipmentOperationsService.getShipment.calls.reset();
        });

        it('should be initialized with edited shipment', function() {
            c_expect(mockShipmentOperationsService.getShipment.calls.count()).to.equal(1);
            c_expect(mockShipmentOperationsService.getShipment.calls.mostRecent().args[0]).to.eql({customerId: 1, shipmentId: 1});
            c_expect(mockShipmentOperationsService.getShipment.calls.mostRecent().args[1]).to.be.a('function');
            checkCommonParams();
            c_expect(scope.wizardData.shipment).to.be.an('object');
            c_expect(scope.wizardData.shipment).to.eql(shipment);
            c_expect(scope.pickup).to.be.an('object');
            c_expect(scope.pickup).to.have.property('country');
            c_expect(scope.pickup.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
            c_expect(scope.deliver).to.be.an('object');
            c_expect(scope.deliver).to.have.property('country');
            c_expect(scope.deliver.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
            c_expect(scope.wizardData.step).to.equal('finish_order');
        });
    });

    describe('Copy shipment (route params copyShipmentId is defined).', function() {
        beforeEach(inject(function($controller) {
            rootParams.copyShipmentId = 1;
            spyOn(mockShipmentOperationsService, 'getCopiedShipment').and.callThrough();
            controller = $controller('QuoteWizard', {$scope: scope, $routeParams: rootParams,
                ShipmentOperationsService: mockShipmentOperationsService, CustomerLabelResource: mockCustomerLabelResource,
                PLSAccessorialsService: accessorialsService, SavedQuotesService: mockSavedQuotesService,
                AccTypesServices: accessorialTypeServiceMock});
        }));

        afterEach(function() {
            mockShipmentOperationsService.getCopiedShipment.calls.reset();
        });

        it('should be initialized with copied shipment', function() {
            c_expect(mockShipmentOperationsService.getCopiedShipment.calls.count()).to.equal(1);
            c_expect(mockShipmentOperationsService.getCopiedShipment.calls.mostRecent().args[0]).to.eql({customerId: 1, shipmentId: 1});
            c_expect(mockShipmentOperationsService.getCopiedShipment.calls.mostRecent().args[1]).to.be.a('function');
            checkCommonParams();
            c_expect(scope.wizardData.shipment).to.be.an('object');
            var shipmentToCheck = angular.copy(shipment);
            c_expect(scope.wizardData.shipment).to.have.property('finishOrder');
            c_expect(scope.wizardData.shipment.finishOrder).to.have.property('pickupDate');
            c_expect(scope.wizardData.shipment.finishOrder.pickupDate).not.to.equal(shipmentToCheck.finishOrder.pickupDate);
            shipmentToCheck.finishOrder.pickupDate = scope.wizardData.shipment.finishOrder.pickupDate;
            c_expect(scope.wizardData.shipment).to.eql(shipmentToCheck);
            c_expect(scope.pickup).to.be.an('object');
            c_expect(scope.pickup).to.have.property('country');
            c_expect(scope.pickup.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
            c_expect(scope.deliver).to.be.an('object');
            c_expect(scope.deliver).to.have.property('country');
            c_expect(scope.deliver.country).to.eql({ id: 'USA', name: 'United States of America', dialingCode: '001' });
            c_expect(scope.wizardData.step).to.equal('rate_quote');
        });
    });

    describe('Saved quote (route params savedQuoteId is defined).', function() {
        beforeEach(inject(function($controller) {
            rootParams.savedQuoteId = 1;
            rootParams.stepName = 'rate_quote';
            spyOn(mockSavedQuotesService, 'get').and.callThrough();
            controller = $controller('QuoteWizard', {$scope: scope, $routeParams: rootParams,
                ShipmentOperationsService: mockShipmentOperationsService, CustomerLabelResource: mockCustomerLabelResource,
                PLSAccessorialsService: accessorialsService, SavedQuotesService: mockSavedQuotesService,
                AccTypesServices: accessorialTypeServiceMock});
        }));

        afterEach(function() {
            mockSavedQuotesService.get.calls.reset();
        });

        it('should be initialized with copied shipment', function() {
            c_expect(mockSavedQuotesService.get.calls.count()).to.equal(1);
            c_expect(mockSavedQuotesService.get.calls.mostRecent().args[0]).to.eql({customerId: 1, propositionId: 1});
            c_expect(mockSavedQuotesService.get.calls.mostRecent().args[1]).to.be.a('function');
            checkCommonParams();
            c_expect(scope.wizardData.shipment).to.be.an('object');
            var shipmentToCheck = angular.copy(shipment);
            c_expect(scope.wizardData.shipment).to.have.property('finishOrder');
            c_expect(scope.wizardData.shipment.finishOrder).to.have.property('pickupDate');
            c_expect(scope.wizardData.shipment.finishOrder.pickupDate).not.to.equal(shipmentToCheck.finishOrder.pickupDate);
            shipmentToCheck.finishOrder.pickupDate = scope.wizardData.shipment.finishOrder.pickupDate;
            c_expect(scope.wizardData.shipment).to.eql(shipmentToCheck);
            c_expect(scope.pickup).to.be.an('object');
            c_expect(scope.wizardData.step).to.equal('rate_quote');
        });
    });
});
