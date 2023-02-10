/**
 * Tests for build order controller from quote wizard.
 * @author: Alexander Kirichenko
 */
describe('BuildOrderCtrl (rate-quote-controllers) Controller Test.', function () {
    var scope = undefined;
    var promiseProvider = undefined;

    var deffereds = [];
    var mockBillTos = {}, mockProducts = [], mockLocations = [];

    var mockProductService = {
        list: function (params, successCallback) {
            successCallback(mockProducts);
        }
    };

    var mockCustomerInternalNoteService = {
        get: function (params, successCallback) {
            successCallback(successCallback);
        }
    };

    var mockAddressNameService = {
        isAddressUnique: function (addressName, params) {
            var defer = promiseProvider.defer();
            deffereds.push(defer);
            return defer.promise;
        }
    };

    var dictionaryValues = [Math.random(), Math.random()];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return {paymentTerms: dictionaryValues};
        },
        addAddressNotificationsToLoadNotificationsWithoutDuplicates : function() {
        }
    };

    var mockOrigAddress = {
        id: null,
        addressName: "ADDR_NAME135",
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

    var initScope = function (scope, LinkedListUtils) {
        scope.wizardData = {};
        scope.wizardData.steps = LinkedListUtils.getLinkedList();
        scope.wizardData.steps.add('rate_quote');
        scope.wizardData.steps.add('select_carrier');
        scope.wizardData.steps.add('build_order');
        scope.wizardData.steps.add('finish_order');
        scope.wizardData.steps.add('finish_quote');
        scope.wizardData.packageTypes = [];
        scope.wizardData.classes = [];
        scope.wizardData.minPickupDate = new Date();
        scope.wizardData.showCustomsBroker = false;

        scope.wizardData.selectedCustomer = {
            id: 1,
            name: 'WORTHINGTON INDUSTRIES'
        };

        scope.wizardData.emptyShipment = {
            originDetails: {
                accessorials: []
            },
            destinationDetails: {
                accessorials: []
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: JSON.stringify(scope.wizardData.minPickupDate).replace(/\"/g, '')
            },
            status: 'OPEN'
        };
        scope.wizardData.shipment = {
            originDetails: {
                "accessorials": [],
                "zip": {
                    "zip": "12345",
                    "country": {
                        "id": "USA",
                        "name": "United States of America",
                        "dialingCode": "001"
                    },
                    "state": "NY",
                    "city": "SCHENECTADY",
                    "timeZone": null
                },
            },
            destinationDetails: {
                "accessorials": [],
                "zip": {
                    "zip": "44136",
                    "country": {
                        "id": "USA",
                        "name": "United States of America",
                        "dialingCode": "001"
                    },
                    "state": "OH",
                    "city": "STRONGSVILLE",
                    "timeZone": null
                }
            },
            "finishOrder": {
                "quoteMaterials": [
                    {
                        "hazmat": false,
                        "stackable": false,
                        "weightUnit": "LBS",
                        "dimensionUnit": "INCH",
                        "weight": 1000,
                        "length": "",
                        "width": "",
                        "height": "",
                        "quantity": 12,
                        "productId": 30,
                        "commodityClass": "CLASS_100",
                        "productCode": "7A12345-WHT-XL",
                        "productDescription": "Miller",
                        "packageType": "BOX",
                        "hazmatClass": null,
                        "packingGroup": null,
                        "unNum": null,
                        "nmfc": "15500",
                        "emergencyResponseCompany": null,
                        "emergencyResponseContractNumber": null,
                        "emergencyResponseInstructions": null
                    }
                ],
                "pickupDate": "2013-08-19T00:00:00.000Z",
                "estimatedDelivery": "2013-08-21T00:00:00.000"
            },
            "status": "OPEN",
            "guaranteedBy": 2400,
            "guid": "ee4abea7-b01e-5777-4011-23624ec84583",
            "selectedProposition": {
                "guid": "bbd31e2a-fbe6-4508-a459-0b31f1562ada",
                "estimatedTransitDate": "2013-08-21T00:00:00.000",
                "estimatedTransitTime": 2880,
                "serviceType": "INDIRECT",
                "carrier": {
                    "id": 15,
                    "scac": "DAFG",
                    "name": "DAYTON FREIGHT LINES",
                    "logoPath": "resources/img/carrier-logo/dayton-freight-logo.png",
                    "specialMessage": "Max LTL weight 25,000 lbs",
                    "currencyCode": "USD"
                },
                "ref": null,
                "newLiability": 25000,
                "usedLiability": 100,
                "prohibited": "Drugs / Narcotics",
                "costDetailItems": [
                    {
                        "refType": "SRA",
                        "description": "Shipper Base Rate",
                        "subTotal": 474.32,
                        "costDetailOwner": "S",
                        "guaranteedBy": null
                    },
                    {
                        "refType": "DS",
                        "description": "Discount",
                        "subTotal": -358.59,
                        "costDetailOwner": "S",
                        "guaranteedBy": null
                    },
                    {
                        "refType": "FS",
                        "description": "Fuel Surcharge",
                        "subTotal": 13.53,
                        "costDetailOwner": "S",
                        "guaranteedBy": null
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 165,
                        "costDetailOwner": "S",
                        "guaranteedBy": 0
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 110,
                        "costDetailOwner": "S",
                        "guaranteedBy": 900
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 55,
                        "costDetailOwner": "S",
                        "guaranteedBy": 930
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 44,
                        "costDetailOwner": "S",
                        "guaranteedBy": 1130
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 33,
                        "costDetailOwner": "S",
                        "guaranteedBy": 1600
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 22,
                        "costDetailOwner": "S",
                        "guaranteedBy": 1730
                    },
                    {
                        "refType": "GD",
                        "description": "Guaranteed",
                        "subTotal": 5.5,
                        "costDetailOwner": "S",
                        "guaranteedBy": 2200
                    },
                    {
                        "refType": "SBR",
                        "description": "Benchmark",
                        "subTotal": 500,
                        "costDetailOwner": "B",
                        "guaranteedBy": null
                    }
                ]
            },
            paymentTerms: 'PREPAID',
            shipmentDirection: 'O'
        };

        mockBillTos = [
            {"id": 1, "address": {"id": 100501, "addressName": "Haynes International, INC.", "contactName": "Delfina Belvins",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "Inverness 2", "address2": null, "zip": {"zip": "62344",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "CO", "city": "Denver", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "176", "countryCode": "1", "number": "8185813", "type": "VOICE"},
                "fax": {"areaCode": "688", "countryCode": "1", "number": "2691322", "type": "FAX"}, "email": "Delfina.Belvins@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": null,
                "longitude": null},
                "customsBroker": "Hayley Stoecker",
                "brokerPhone": {"areaCode": "593", "countryCode": "1", "number": "8576854", "type": "VOICE"}, "defaultNode": false},
            {"id": 2, "address": {"id": 12, "addressName": "Stripco Inc", "contactName": "Wei Bartow",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "Renton City Hall 1055 S", "address2": "400 S Second Street", "zip": {"zip": "21172",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "WA", "city": "Renton", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "593", "countryCode": "1", "number": "8576854", "type": "VOICE"},
                "fax": {"areaCode": "343", "countryCode": "1", "number": "5375286", "type": "FAX"}, "email": "Wei.Bartow@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": 47.4356,
                "longitude": -122.1141}, "customsBroker": "Tomasa Bomgardner",
                "brokerPhone": {"areaCode": "034", "countryCode": "1", "number": "1258843", "type": "VOICE"}, "defaultNode": true},
            {"id": 3, "address": {"id": 14, "addressName": "C/G Electrodes LLC", "contactName": "Belia Prinz",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "711 River Road", "address2": "6001 Airport Boulevard", "zip": {"zip": "21174",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "TX", "city": "Austin", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "034", "countryCode": "1", "number": "1258843", "type": "VOICE"},
                "fax": {"areaCode": "603", "countryCode": "1", "number": "7915004", "type": "FAX"}, "email": "Belia.Prinz@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": 30.2852,
                "longitude": -97.7354}, "customsBroker": "Hayley Stoecker",
                "brokerPhone": {"areaCode": "791", "countryCode": "1", "number": "3546157", "type": "VOICE"}, "defaultNode": false},
            {"id": 10, "address": {"id": 17, "addressName": "Seacom", "contactName": "Alexandra Neveu",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "1000 West 39th Street", "address2": "7300 Hart Lane", "zip": {"zip": "21180",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "NB", "city": "Fredericton", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "161", "countryCode": "1", "number": "0274724", "type": "VOICE"},
                "fax": {"areaCode": "522", "countryCode": "1", "number": "0426546", "type": "FAX"}, "email": "Alexandra.Neveu@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": 45.95,
                "longitude": -66.6333}, "customsBroker": "Hayley Stoecker",
                "brokerPhone": {"areaCode": "158", "countryCode": "1", "number": "7323492", "type": "VOICE"}, "defaultNode": false},
            {"id": 11, "address": {"id": 8, "addressName": "3M Company", "contactName": "Madison Kirwan",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "8888 University Drive", "address2": "515 West Hastings Street", "zip": {"zip": "21167",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "BC", "city": "Burnaby", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "158", "countryCode": "1", "number": "7323492", "type": "VOICE"},
                "fax": {"areaCode": "483", "countryCode": "1", "number": "2366598", "type": "FAX"}, "email": "Madison.Kirwan@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": 49.25,
                "longitude": -122.95}, "customsBroker": "Andria Spina",
                "brokerPhone": {"areaCode": "176", "countryCode": "1", "number": "2379426", "type": "VOICE"}, "defaultNode": false},
            {"id": 12, "address": {"id": 20, "addressName": "II-VI, Inc.", "contactName": "Bruce Cronk",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "19 Interpro Road Madison", "address2": "4 Second Street", "zip": {"zip": "123115",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "QC", "city": "Palmarolle", "timeZone": null},
                "addressCode": null, "phone": {"areaCode": "176", "countryCode": "1", "number": "2379426", "type": "VOICE"},
                "fax": {"areaCode": "148", "countryCode": "1", "number": "2601586", "type": "FAX"}, "email": "Bruce.Cronk@yahoo.com",
                "pickupWindowFrom": null, "pickupWindowTo": null, "pickupNotes": null, "deliveryNotes": null, "latitude": 48.6667,
                "longitude": -79.2}, "customsBroker": "Scarlet Desouza",
                "brokerPhone": {"areaCode": "952", "countryCode": "1", "number": "0156928", "type": "VOICE"}, "defaultNode": false}
        ];
        mockProducts = [
            {"id": 7032, "packageType": null, "weight": null, "hazmat": false, "hazmatClass": null, "hazmatPackingGroup": null,
                "hazmatUnNumber": null, "pieces": null, "nmfc": "567567", "nmfcSubNum": "345", "productCode": null,
                "commodityClass": "CLASS_150", "description": "563723547", "hazmatEmergencyCompany": null, "hazmatEmergencyContract": null,
                "hazmatEmergencyPhone": null, "hazmatInstructions": null},
            {"id": 7033, "packageType": null, "weight": null, "hazmat": false, "hazmatClass": null, "hazmatPackingGroup": null,
                "hazmatUnNumber": null, "pieces": null, "nmfc": "345", "nmfcSubNum": null, "productCode": null,
                "commodityClass": "CLASS_400", "description": "36583568", "hazmatEmergencyCompany": null, "hazmatEmergencyContract": null,
                "hazmatEmergencyPhone": null, "hazmatInstructions": null},
            {"id": 7042, "packageType": null, "weight": null, "hazmat": true, "hazmatClass": "2", "hazmatPackingGroup": "12312313",
                "hazmatUnNumber": "12313123", "pieces": null, "nmfc": null, "nmfcSubNum": null,
                "productCode": "mnm1232121nmmnmn123", "commodityClass": "CLASS_60", "description": "super new super product super duper",
                "hazmatEmergencyCompany": null, "hazmatEmergencyContract": null, "hazmatEmergencyPhone": null, "hazmatInstructions": null},
            {"id": 7071, "packageType": null, "weight": null, "hazmat": false, "hazmatClass": null, "hazmatPackingGroup": null,
                "hazmatUnNumber": null, "pieces": null, "nmfc": null, "nmfcSubNum": null, "productCode": null,
                "commodityClass": "CLASS_60", "description": "lolipop", "hazmatEmergencyCompany": null, "hazmatEmergencyContract": null,
                "hazmatEmergencyPhone": null, "hazmatInstructions": null},
            {"id": 7073, "packageType": null, "weight": null, "hazmat": false, "hazmatClass": null, "hazmatPackingGroup": null,
                "hazmatUnNumber": null, "pieces": null, "nmfc": null, "nmfcSubNum": null, "productCode": null,
                "commodityClass": "CLASS_70", "description": "TEST", "hazmatEmergencyCompany": null, "hazmatEmergencyContract": null,
                "hazmatEmergencyPhone": null, "hazmatInstructions": null},
            {"id": 7068, "packageType": null, "weight": null, "hazmat": true, "hazmatClass": "4", "hazmatPackingGroup": "23434324",
                "hazmatUnNumber": "839237849", "pieces": null, "nmfc": "3434", "nmfcSubNum": "345345",
                "productCode": "dddj3333jkklj3k333kj3", "commodityClass": "CLASS_70", "description": "new", "hazmatEmergencyCompany": null,
                "hazmatEmergencyContract": null, "hazmatEmergencyPhone": null, "hazmatInstructions": null}
        ];
        mockLocations = [
            {id: 2, name: 'location2', billToId: 1},
            {id: 1, name: 'location1'},
            {id: 3, name: 'location3', billToId: 3, defaultNode: true}
        ];
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

    var accessorialTypeServiceMock = {
        listAccessorialsByGroup: function() {
        }
    };

    beforeEach(inject(function ($rootScope, $controller, $q, LinkedListUtils) {
        promiseProvider = $q;
        scope = $rootScope.$new();
        deffereds = [];

        $controller('QuoteWizard', {$scope: scope, ShipmentDetailsService: {}, ShipmentDetailsService: {}, ShipmentUtils: mockShipmentUtils, 
            SavedQuotesService: {}, CustomerLabelResource: {}, AccTypesServices: accessorialTypeServiceMock});

        initScope(scope, LinkedListUtils);

        $controller('BuildOrderCtrl', {$scope: scope, $q: $q, ProductService: mockProductService,
            AddressNameService: mockAddressNameService, ShipmentUtils: mockShipmentUtils, CustomerInternalNoteService:mockCustomerInternalNoteService});
    }));

    describe('Test scenarios for init build order step', function() {

        it('Test build order init step.', function () {
            c_expect(scope.wizardData.shipment.originDetails.address).to.not.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address).to.not.exist;
            c_expect(scope.wizardData.shipment.billTo).to.not.exist;
            spyOn(mockProductService, 'list').and.callThrough();
            spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
            scope.initStep();
            scope.$apply();
            expect(mockProductService.list).toHaveBeenCalled();
            c_expect(mockProductService.list.calls.mostRecent().args[0]).to.eql({customerId: 1});
            c_expect(scope.products).to.eql(mockProducts);
            c_expect(scope.wizardData.shipment.originDetails.address).to.exist;
            c_expect(scope.wizardData.shipment.originDetails.address).to.eql({
                addressName: '',
                phone: {},
                fax: {type: 'FAX'},
                zip: scope.wizardData.shipment.originDetails.zip
            });
            c_expect(scope.wizardData.shipment.destinationDetails.address).to.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address).to.eql({
                addressName: '',
                phone: {},
                fax: {type: 'FAX'},
                zip: scope.wizardData.shipment.destinationDetails.zip
            });
        });

        it('Should correct correct zip in both addresses according to origin and destination.', function() {
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.address = {
                    zip: {
                        zip: "16066",
                        country: {
                            id: "USA",
                            name: "United States of America",
                            dialingCode: "001"
                        },
                        state: "PA",
                        city: "CRANBERRY TWP",
                        timeZone: null                }
                };
                scope.wizardData.shipment.destinationDetails.address = {
                    zip: {
                        zip: "12345",
                        country: {
                            id: "USA",
                            name: "United States of America",
                            dialingCode: "001"
                        },
                        state: "NY",
                        city: "SCHENECTADY",
                        timeZone: null
                    }
                };
            });
            c_expect(scope.wizardData.shipment.originDetails.address.zip).to.not.eql(scope.wizardData.shipment.originDetails.zip);
            c_expect(scope.wizardData.shipment.destinationDetails.address.zip).to.not.eql(scope.wizardData.shipment.destinationDetails.zip);
            scope.initStep();
            c_expect(scope.wizardData.shipment.originDetails.address.zip).to.eql(scope.wizardData.shipment.originDetails.zip);
            c_expect(scope.wizardData.shipment.destinationDetails.address.zip).to.eql(scope.wizardData.shipment.destinationDetails.zip);
        });

        it('Should create empty phone/fax object if it does not exist.', function() {
            c_expect(scope.wizardData.shipment.originDetails.address).to.not.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address).to.not.exist;
            scope.initStep();
            c_expect(scope.wizardData.shipment.originDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.originDetails.address.fax).to.eql({type: 'FAX'});
            c_expect(scope.wizardData.shipment.destinationDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.destinationDetails.address.fax).to.eql({type: 'FAX'});
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.address = {
                    phone: undefined,
                    fax: undefined
                };
                scope.wizardData.shipment.destinationDetails.address = {
                    phone: undefined,
                    fax: undefined
                };
            });
            c_expect(scope.wizardData.shipment.originDetails.address).to.exist;
            c_expect(scope.wizardData.shipment.originDetails.address.phone).to.not.exist;
            c_expect(scope.wizardData.shipment.originDetails.address.fax).to.not.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address).to.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address.phone).to.not.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address.fax).to.not.exist;
            scope.initStep();
            c_expect(scope.wizardData.shipment.originDetails.address.phone).to.exist;
            c_expect(scope.wizardData.shipment.originDetails.address.fax).to.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address.phone).to.exist;
            c_expect(scope.wizardData.shipment.destinationDetails.address.fax).to.exist;
            c_expect(scope.wizardData.shipment.originDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.originDetails.address.fax).to.eql({type: 'FAX'});
            c_expect(scope.wizardData.shipment.destinationDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.destinationDetails.address.fax).to.eql({type: 'FAX'});
        });

        it('Should create empty phone/fax object if different zip.', function() {
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.address = {
                    zip: {
                        zip: "16066",
                        country: {
                            id: "USA",
                            name: "United States of America",
                            dialingCode: "001"
                        },
                        state: "PA",
                        city: "CRANBERRY TWP",
                        timeZone: null
                    },
                    phone: {
                        countryCode: '1',
                        areaCode: '123',
                        number: '1234567',
                        type: 'VOICE'
                    },
                    fax: {
                        countryCode: '1',
                        areaCode: '123',
                        number: '1234567',
                        type: 'FAX'
                    }
                };
                scope.wizardData.shipment.destinationDetails.address = {
                    zip: {
                        zip: "12345",
                        country: {
                            id: "USA",
                            name: "United States of America",
                            dialingCode: "001"
                        },
                        state: "NY",
                        city: "SCHENECTADY",
                        timeZone: null
                    },
                    phone: {
                        countryCode: '1',
                        areaCode: '123',
                        number: '1234567',
                        type: 'VOICE'
                    },
                    fax: {
                        countryCode: '1',
                        areaCode: '123',
                        number: '1234567',
                        type: 'FAX'
                    }
                };
            });
            scope.initStep();
            c_expect(scope.wizardData.shipment.originDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.originDetails.address.fax).to.eql({type: 'FAX'});
            c_expect(scope.wizardData.shipment.destinationDetails.address.phone).to.eql({});
            c_expect(scope.wizardData.shipment.destinationDetails.address.fax).to.eql({type: 'FAX'});
        });

        it('Should show customer broker info if addresses has different countries', function() {
            scope.initStep();
            c_expect(scope.wizardData.showCustomsBroker).to.be.false();
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.zip = {
                    zip: "123115",
                    country: {id: "CAN", name: "Canada", dialingCode: "001"},
                    state: "QC",
                    city: "Palmarolle",
                    timeZone: null
                };
                scope.wizardData.shipment.destinationDetails.zip = {
                    zip: "44136",
                    country: {
                        id: "USA",
                        name: "United States of America",
                        dialingCode: "001"
                    },
                    state: "OH",
                    city: "STRONGSVILLE",
                    timeZone: null
                }
            });
            scope.initStep();
            c_expect(scope.wizardData.showCustomsBroker).to.be.true();
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.zip = {
                    zip: "85776",
                    country: {
                        id: "MEX",
                        name: "Mexico",
                        dialingCode: "052"
                    },
                    state: "SO",
                    city: "MOCUZARI",
                    timeZone: null
                };
                scope.wizardData.shipment.destinationDetails.zip = {
                    zip: "123115",
                    country: {id: "CAN", name: "Canada", dialingCode: "001"},
                    state: "QC",
                    city: "Palmarolle",
                    timeZone: null
                }
            });
            scope.initStep();
            c_expect(scope.wizardData.showCustomsBroker).to.be.true();
            scope.$apply(function() {
                scope.wizardData.shipment.originDetails.zip = {
                    zip: "123116",
                    country: {id: "CAN", name: "Canada", dialingCode: "001"},
                    state: "QC",
                    city: "Palmarolle+",
                    timeZone: null
                };
                scope.wizardData.shipment.destinationDetails.zip = {
                    zip: "123115",
                    country: {id: "CAN", name: "Canada", dialingCode: "001"},
                    state: "QC",
                    city: "Palmarolle",
                    timeZone: null
                }
            });
            scope.initStep();
            c_expect(scope.wizardData.showCustomsBroker).to.be.false();
        });

        it('Should change customer broker info accordingly to billto change', function() {
            scope.$apply(function() {
                scope.initStep();
            });
            scope.$apply(function() {
                scope.wizardData.shipment.billTo = mockBillTos[0];
            });
            c_expect(scope.wizardData.shipment.customsBroker).to.exist;
            c_expect(scope.wizardData.shipment.customsBroker).to.eql({name: mockBillTos[0].customsBroker, phone: mockBillTos[0].brokerPhone});
            scope.$apply(function() {
                scope.wizardData.shipment.billTo = mockBillTos[2];
            });
            c_expect(scope.wizardData.shipment.customsBroker).to.eql({name: mockBillTos[2].customsBroker, phone: mockBillTos[2].brokerPhone});
        });
    });

    describe('Test scenarios for moving to the next step', function() {
        beforeEach(function () {
            scope.$apply(function () {
                scope.initStep();
                scope.wizardData.step = scope.wizardData.steps.find('build_order');
            });
        });

        it('Should pass to the next step without validation.', function () {
            scope.$apply(function () {
                scope.wizardData.shipment.originDetails.address = angular.copy(mockOrigAddress);
                scope.wizardData.shipment.originDetails.address.id = 1;
                scope.wizardData.shipment.destinationDetails.address = angular.copy(mockDestinationAddress);
                scope.wizardData.shipment.destinationDetails.address.id = 2;
                scope.next();
            });
            c_expect(scope.wizardData.step).to.equal('finish_order');
        });
    });
});