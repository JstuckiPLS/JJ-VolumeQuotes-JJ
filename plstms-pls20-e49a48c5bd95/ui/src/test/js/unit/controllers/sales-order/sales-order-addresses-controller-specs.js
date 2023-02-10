/**
 * Test scenarios for sales order address controller.
 * @author: Alexander Kirichenko
 */
describe('SOAddressesCtrl (sales-order) Test.', function () {
    // ANGULAR SERVICES
    var scope = undefined;

    //SOAddressesCtrl controller
    var controller = undefined;

    var promiseProvider = undefined;

    var deffers = [];

    var mockDictionaryService = {
        getPaymentTerms: function() {
            var defer = promiseProvider.defer();
            return defer.promise;
        }
    };

    var mockShipmentsProposalService = {
        getFreightBillPayTo: function (params, success) {
            success({});
            return this;
        }
    };

    var mockOrigAddress = {
        id: null,
        addressName: "ADDR_NAME10",
        contactName: "Carisa Hartlage",
        country: {
            id: "USA",
            name: "United States of America",
            dialingCode: "001"
        },
        address1: "No Data",
        address2: null,
        zip: {
            zip: "22222",
            country: {
                id: "USA",
                name: "United States of America",
                dialingCode: "001"
            },
            state: "VA",
            city: "ARLINGTON",
            timeZone: null
        },
        addressCode: null,
        phone: {
            areaCode: "194",
            countryCode: "1",
            number: "6715476",
            type: "VOICE"
        },
        fax: {
            areaCode: "293",
            countryCode: "1",
            number: "5593277",
            type: "VOICE"
        },
        email: "test134@test134.com",
        notify: null,
        pickupWindowFrom: null,
        pickupWindowTo: null,
        pickupNotes: null,
        deliveryNotes: null,
        latitude: 38.85551,
        longitude: -77.05032
    };

    var mockDestinationAddress = {
        id: null,
        addressName: "ADDR_NAME107",
        contactName: "Catrice Raglin",
        country: {
            id: "USA",
            name: "United States of America",
            dialingCode: "001"
        },
        address1: "PS 59 & HS OF ART & DESIGN",
        address2: null,
        zip: {
            zip: "10101",
            country: {
                id: "USA",
                name: "United States of America",
                dialingCode: "001"
            },
            state: "NY",
            city: "NEW YORK",
            timeZone: null
        },
        addressCode: null,
        phone: {
            areaCode: "937",
            countryCode: "1",
            number: "1917933",
            type: "VOICE"
        },
        fax: {
            areaCode: "807",
            countryCode: "1",
            number: "4837310",
            type: "VOICE"
        },
        email: "test106@test106.com",
        notify: null,
        pickupWindowFrom: null,
        pickupWindowTo: null,
        pickupNotes: null,
        deliveryNotes: null,
        latitude: 40.76612,
        longitude: -73.98744
    };

    var mockShipmentMileageData = {
        originAddress : {
            address1 : "1",
            address2 : "2",
            zip: {
                zip: "11000",
                state: "NY",
                city: "New York",
                country: {
                    id: "USA"
                }
            }
        },
        destinationAddress : {
            address1 : "3",
            address2 : "4",
            zip: {
                zip: "00111",
                state: "M",
                city: "Moscow",
                country: {
                    id: "RUSSIA"
                }
            }
        }
    };

    var mockShipmentMileageService = {
        getShipmentMileage: function(params) {
            return {
                then : function(successCallback) {
                    successCallback({data: 123});
                }
            };
        }
    };

    var mockShipmentDocumentEmailService = {
        
    };

    var accessorialTypeServiceMock = {
        
    };

    var dictionaryValues = [Math.random(), Math.random()];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return dictionaryValues;
        },
        addAddressNotificationsToLoadNotificationsWithoutDuplicates : function() {
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, $q) {
        scope = $rootScope.$new();

        $controller('BaseSalesOrderCtrl', {$scope: scope,
            ShipmentsProposalService: mockShipmentsProposalService, ShipmentDocumentEmailService: mockShipmentDocumentEmailService});

        $controller('CreateSalesOrderCtrl', {$scope: scope, $routeParams: {step: 'addresses'}, ShipmentsProposalService: {
            getFreightBillPayTo: function(){}
        }, ShipmentUtils: mockShipmentUtils});
        scope.init();

        promiseProvider = $q;
        scope.editSalesOrderModel = {};
        controller = $controller('SOAddressesCtrl', {$scope: scope, 
            AccTypesServices : accessorialTypeServiceMock, DictionaryService: mockDictionaryService, 
            ShipmentMileageService: mockShipmentMileageService, ShipmentUtils: mockShipmentUtils });
        scope.$digest();

        scope.$apply(function () {
            scope.init();
        });
    }));

    it('should controller initialize properly', function () {
        c_expect(controller).to.be.defined;
        c_expect(scope.wizardData.showCustomsBroker).to.equal(false);
        c_expect(scope.wizardData.shipment.originDetails.address).to.be.defined;
        c_expect(scope.wizardData.shipment.destinationDetails.address).to.be.defined;
    });

    it('should update shipment mileage', function() {
        c_expect(controller).to.be.defined;
        spyOn(mockShipmentMileageService, 'getShipmentMileage').and.callThrough();

        scope.$apply(function () {
            scope.wizardData.shipment.originDetails.address = mockShipmentMileageData.originAddress;
            scope.wizardData.shipment.destinationDetails.address = mockShipmentMileageData.destinationAddress;
            scope.wizardData.shipment.selectedProposition.mileage = undefined;
        });

        var stepObject = scope.wizardData.breadCrumbs.map.addresses;
        stepObject.nextAction();

        c_expect(mockShipmentMileageService.getShipmentMileage.calls.count()).to.equal(1);
        c_expect(scope.wizardData.shipment.selectedProposition.mileage).to.equal(123);
        c_expect(mockShipmentMileageService.getShipmentMileage.calls.mostRecent().args[0]).to.eql(
            {
                originAddress : {
                    address1 : "1",
                    address2 : "2",
                    city : "New York",
                    stateCode : "NY",
                    postalCode : "11000",
                    countryCode : "USA"
                },
                destinationAddress : {
                    address1 : "3",
                    address2 : "4",
                    city : "Moscow",
                    stateCode : "M",
                    postalCode : "00111",
                    countryCode : "RUSSIA"
                }
            });
    });
});