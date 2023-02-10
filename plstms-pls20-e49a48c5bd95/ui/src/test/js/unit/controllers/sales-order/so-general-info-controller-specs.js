/**
 * Tests for sales order general info controller.
 *
 * @author Nikita Cherevko
 */
describe('Tests for sales order general info(so-general-info-controller) controller', function() {

    var scope = undefined;
    var rootScope = undefined;

    var controller = undefined;

    var shipmentStatusEnum = { 
            OPEN: "Open",
            BOOKED: "Booked",
            DISPATCHED: "Dispatched",
            IN_TRANSIT: "In-Transit",
            OUT_FOR_DELIVERY: "Out for Delivery",
            DELIVERED: "Delivered",
            CANCELLED: "Cancelled"
        };

    var dictionaryValues = [Math.random(), Math.random()];

    dictionaryValues.shipmentStatusEnum = shipmentStatusEnum;

    var guaranteedTimeOptions = function (defaultOption) {
        var timeOptions = [];

        timeOptions.push(defaultOption);
        timeOptions.push(1000);
        timeOptions.push(1030);
        timeOptions.push(1200);
        timeOptions.push(1400);
        timeOptions.push(1500);
        timeOptions.push(1530);
        timeOptions.push(1700);
        return timeOptions;
    };

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return dictionaryValues;
        },
        getGuaranteedTimeOptions : function() {
            return guaranteedTimeOptions;
        }
    };

    var mockCustomerInternalNoteService = {
            get: function (params, successCallback) {
                successCallback(successCallback);
            }
        };

    var initWizardData = function (scope) {
        scope.wizardData = {
            breadCrumbs: {
                map: {
                    general_information: {
                        id: 'general_information',
                        label: 'General Information',
                        validNext: undefined,
                        nextAction: undefined
                    }
                }
            },
            shipment: {
                finishOrder: {
                    quoteMaterials: [
                        { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                            productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false, productId: 2 }
                    ],
                    glNumber: 'glNumber',
                    poNumber: 'poNumber',
                    puNumber: 'puNumber',
                    ref: 'ref',
                    soNumber: 'soNumber',
                    trailerNumber: 'trailerNumber'
                },
                originDetails: {
                    accessorials: []
                },
                destinationDetails: {
                    accessorials: []
                },
                selectedProposition: {
                    costDetailItems: [{costDetailOwner: "S", refType: "ECA", subTotal: 12},
                        {costDetailOwner: "C", refType: "ECA", subTotal: 112}]
                },
                bolNumber: 'bol',
                cargoValue: 'cargo',
                proNumber: 'proNumber'
            }
        }
    };

    var enterShipmentDetails = function(scope) {
        scope.wizardData.shipment.selectedProposition.carrier = {
            id: undefined,
            name: "AVERITT EXPRESS",
            scac: "AVRT"
        };
        scope.wizardData.shipment.originDetails.zip = {
            city: "ABBEVILLE",
            zip: "36310"
        };
        scope.wizardData.shipment.destinationDetails.zip = {
            city: "AFTON",
            zip: "49705"
        };
    };

    var addedCostDetails = {
        cost: 333,
        description: "Canadian Border Fee",
        refType: 'CBF',
        revenue: 234
    };

    beforeEach(module('plsApp'));
    beforeEach(module('plsApp', function($provide) {
        $provide.factory('DictionaryService', function() {
            return {
                getPackageTypes: function() {
                    return {
                        success: function(handler) {
                            handler([{code: "BOX", label: "Boxes"}, {code: "ENV", label: "Envelopes"}, {code: "PLT", label: "Pallet"}]);
                        }
                    };
                }
            };
        });
    }));

    beforeEach(inject(function($rootScope, $q, $controller, $injector) {
        scope = $rootScope.$new();
        rootScope = $rootScope;
        $rootScope.accessorialTypes = [];
        initWizardData(scope);

        scope.createController = function (scope) {
            return $controller('SOGeneralInfoCtrl', {$scope: scope, $q: $q, ShipmentUtils: mockShipmentUtils,
                CustomerInternalNoteService:mockCustomerInternalNoteService});
        };

        controller = scope.createController(scope);

        var httpBackend = $injector.get('$httpBackend');
        httpBackend.expectGET('/restful/customer/address/defaultFreightBillPayTo').respond();
        scope.$digest();
    }));

    it('should controller initialize properly', function () {
        c_expect(controller).to.be.defined;
        c_expect(scope.rateQuoteDictionary).to.deep.equal(dictionaryValues);
        scope.init();
        scope.$apply(function () {
            scope.selectedCustomer = 'customer';
        });
        
        //check the customer change
        c_expect(scope.wizardData.shipment.finishOrder.quoteMaterials[0].productId).to.equal(2);
        scope.$apply(function() {
            enterShipmentDetails(scope);
            scope.selectedCustomer = 'newCustomer';
        });
        c_expect(scope.wizardData.shipment.selectedProposition.carrier.name).to.eql("AVERITT EXPRESS");
        c_expect(scope.wizardData.shipment.destinationDetails.zip.city).to.eql("AFTON");
        c_expect(scope.wizardData.shipment.originDetails.zip.city).to.eql("ABBEVILLE");
    });

    it('should cost details buttons work properly', function() {
        scope.addCostDetails();
        c_expect(scope.isEdit).to.equal(false);

        scope.$apply(function() {
            scope.selectedCostDetails = [scope.wizardData.shipment.selectedProposition.costDetailItems];
            scope.editCostDetails();
        });

        c_expect(scope.isEdit).to.equal(true);
        c_expect(scope.wizardData.shipment.selectedProposition.costDetailItems.length).to.equal(2);
        c_expect(scope.costDetailsForGrid.length).to.equal(1);

        scope.$apply(function() {
            rootScope.$broadcast('event:saveCostDetails', addedCostDetails);
        });
        c_expect(scope.wizardData.shipment.selectedProposition.costDetailItems.length).to.equal(4);
        c_expect(scope.costDetailsForGrid.length).to.equal(2);

        //delete select cost detail
        scope.$apply(function() {
            scope.selectedCostDetails = [scope.wizardData.shipment.selectedProposition.costDetailItems[0]];
            scope.deleteSelectedCostDetails();
        });
        c_expect(scope.wizardData.shipment.selectedProposition.costDetailItems.length).to.equal(2);
        c_expect(scope.costDetailsForGrid.length).to.equal(1);

        //edit existing cost detail
        c_expect(scope.wizardData.shipment.selectedProposition.costDetailItems[1].subTotal).to.equal(333);
        addedCostDetails.cost = 100;
        scope.$apply(function() {
            scope.selectedCostDetails = [scope.wizardData.shipment.selectedProposition.costDetailItems[0]];
            rootScope.$broadcast('event:saveCostDetails', addedCostDetails);
        });
        c_expect(scope.wizardData.shipment.selectedProposition.costDetailItems[1].subTotal).to.equal(100);

    });

    it('should initialize statuses list properly', function () {
        c_expect(controller).to.be.defined;
        scope.init();

        scope.wizardData.shipment.status = 'CANCELLED';
        scope.createController(scope);
        c_expect(scope.shipmentStatuses.hasOwnProperty("CANCELLED")).to.equal(true);

        scope.wizardData.shipment.status = 'BOOKED';
        scope.createController(scope);
        c_expect(scope.shipmentStatuses.hasOwnProperty("CANCELLED")).to.equal(false);
    });

    it ('should clear data after change customer', function () {
        scope.init();
        spyOn(scope.$root, '$broadcast').and.callThrough();
        expect(scope.wizardData.shipment.bolNumber).toBe('bol');
        expect(scope.wizardData.shipment.cargoValue).toBe('cargo');
        expect(scope.wizardData.shipment.proNumber).toBe('proNumber');
        expect(scope.wizardData.shipment.finishOrder.glNumber).toBe('glNumber');
        expect(scope.wizardData.shipment.finishOrder.poNumber).toBe('poNumber');
        expect(scope.wizardData.shipment.finishOrder.puNumber).toBe('puNumber');
        expect(scope.wizardData.shipment.finishOrder.ref).toBe('ref');
        expect(scope.wizardData.shipment.finishOrder.soNumber).toBe('soNumber');
        expect(scope.wizardData.shipment.finishOrder.trailerNumber).toBe('trailerNumber');

        scope.$apply(function() {
            scope.wizardData.selectedCustomer = { name: 'newCustomer', id: 2 };
        });
        expect(scope.wizardData.shipment.finishOrder.quoteMaterials[0]).toBeUndefined();
        expect(scope.wizardData.shipment.organizationId).toBe(2);

        expect(scope.wizardData.shipment.bolNumber).toBeUndefined();
        expect(scope.wizardData.shipment.cargoValue).toBeUndefined();
        expect(scope.wizardData.shipment.proNumber).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.glNumber).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.poNumber).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.puNumber).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.ref).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.soNumber).toBeUndefined();
        expect(scope.wizardData.shipment.finishOrder.trailerNumber).toBeUndefined();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:customer-changed');
    });
});
