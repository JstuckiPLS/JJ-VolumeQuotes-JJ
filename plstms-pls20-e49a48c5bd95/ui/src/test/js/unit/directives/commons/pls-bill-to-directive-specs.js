/**
 * Tests for pls bill to directive.
 *
 * @author: Dmitry Nikolaenko
 */
describe('PLS Bill To (pls-bill-to-directive) Directive Test.', function () {
    var scope = undefined;
    var element = undefined;
    var promiseProvider = undefined;

    var mockBillTos = [
        {
            "id": 1,
            "address": {
                "id": 100501,
                "addressName": "Haynes International, INC.",
                "contactName": "Delfina Belvins",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "Inverness 2",
                "address2": null,
                "zip": {
                    "zip": "62344",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "CO",
                    "city": "Denver",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "176", "countryCode": "1", "number": "8185813", "type": "VOICE"},
                "fax": {"areaCode": "688", "countryCode": "1", "number": "2691322", "type": "FAX"},
                "email": "Delfina.Belvins@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": null,
                "longitude": null
            },
            "billToRequiredFields": [],
            "customsBroker": "Hayley Stoecker",
            "brokerPhone": {"areaCode": "593", "countryCode": "1", "number": "8576854", "type": "VOICE"},
            "defaultNode": false
        },
        {
            "id": 2,
            "address": {
                "id": 12,
                "addressName": "Stripco Inc",
                "contactName": "Wei Bartow",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "Renton City Hall 1055 S",
                "address2": "400 S Second Street",
                "zip": {
                    "zip": "21172",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "WA",
                    "city": "Renton",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "593", "countryCode": "1", "number": "8576854", "type": "VOICE"},
                "fax": {"areaCode": "343", "countryCode": "1", "number": "5375286", "type": "FAX"},
                "email": "Wei.Bartow@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": 47.4356,
                "longitude": -122.1141
            },
            "billToRequiredFields": [],
            "customsBroker": "Tomasa Bomgardner",
            "brokerPhone": {"areaCode": "034", "countryCode": "1", "number": "1258843", "type": "VOICE"},
            "defaultNode": true
        },
        {
            "id": 3,
            "address": {
                "id": 14,
                "addressName": "C/G Electrodes LLC",
                "contactName": "Belia Prinz",
                "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                "address1": "711 River Road",
                "address2": "6001 Airport Boulevard",
                "zip": {
                    "zip": "21174",
                    "country": {"id": "USA", "name": "United States of America", "dialingCode": "001"},
                    "state": "TX",
                    "city": "Austin",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "034", "countryCode": "1", "number": "1258843", "type": "VOICE"},
                "fax": {"areaCode": "603", "countryCode": "1", "number": "7915004", "type": "FAX"},
                "email": "Belia.Prinz@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": 30.2852,
                "longitude": -97.7354
            },
            "billToRequiredFields": [],
            "customsBroker": "Hayley Stoecker",
            "brokerPhone": {"areaCode": "791", "countryCode": "1", "number": "3546157", "type": "VOICE"},
            "defaultNode": false
        },
        {
            "id": 10,
            "address": {
                "id": 17,
                "addressName": "Seacom",
                "contactName": "Alexandra Neveu",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "1000 West 39th Street",
                "address2": "7300 Hart Lane",
                "zip": {
                    "zip": "21180",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "NB",
                    "city": "Fredericton",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "161", "countryCode": "1", "number": "0274724", "type": "VOICE"},
                "fax": {"areaCode": "522", "countryCode": "1", "number": "0426546", "type": "FAX"},
                "email": "Alexandra.Neveu@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": 45.95,
                "longitude": -66.6333
            },
            "billToRequiredFields": [],
            "customsBroker": "Hayley Stoecker",
            "brokerPhone": {"areaCode": "158", "countryCode": "1", "number": "7323492", "type": "VOICE"},
            "defaultNode": false
        },
        {
            "id": 11,
            "address": {
                "id": 8,
                "addressName": "3M Company",
                "contactName": "Madison Kirwan",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "8888 University Drive",
                "address2": "515 West Hastings Street",
                "zip": {
                    "zip": "21167",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "BC",
                    "city": "Burnaby",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "158", "countryCode": "1", "number": "7323492", "type": "VOICE"},
                "fax": {"areaCode": "483", "countryCode": "1", "number": "2366598", "type": "FAX"},
                "email": "Madison.Kirwan@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": 49.25,
                "longitude": -122.95
            },
            "billToRequiredFields": [],
            "customsBroker": "Andria Spina",
            "brokerPhone": {"areaCode": "176", "countryCode": "1", "number": "2379426", "type": "VOICE"},
            "defaultNode": false
        },
        {
            "id": 12,
            "address": {
                "id": 20,
                "addressName": "II-VI, Inc.",
                "contactName": "Bruce Cronk",
                "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                "address1": "19 Interpro Road Madison",
                "address2": "4 Second Street",
                "zip": {
                    "zip": "123115",
                    "country": {"id": "CAN", "name": "Canada", "dialingCode": "001"},
                    "state": "QC",
                    "city": "Palmarolle",
                    "timeZone": null
                },
                "addressCode": null,
                "phone": {"areaCode": "176", "countryCode": "1", "number": "2379426", "type": "VOICE"},
                "fax": {"areaCode": "148", "countryCode": "1", "number": "2601586", "type": "FAX"},
                "email": "Bruce.Cronk@yahoo.com",
                "pickupWindowFrom": null,
                "pickupWindowTo": null,
                "pickupNotes": null,
                "deliveryNotes": null,
                "latitude": 48.6667,
                "longitude": -79.2
            },
            "billToRequiredFields": [],
            "customsBroker": "Scarlet Desouza",
            "brokerPhone": {"areaCode": "952", "countryCode": "1", "number": "0156928", "type": "VOICE"},
            "defaultNode": false
        }
    ];

    var mockLocations = [
        {id: 2, name: 'Location2', billToId: 1},
        {id: 1, name: 'Location1'},
        {id: 3, name: 'Location3', billToId: 3, defaultNode: true}
    ];

    var mockRequiredFields = [
        {"label": "BOL#", "value": "BOL"},
        {"label": "GL#", "value": "GL"},
        {"label": "PO#", "value": "PO"},
        {"label": "PU#", "value": "PU"},
        {"label": "SO#", "value": "SO"},
        {"label": "Shipper Ref#", "value": "SR"},
        {"label": "Trailer#", "value": "TR"},
        {"label": "Job#", "value": "JOB"},
        {"label": "Requested By", "value": "RB"},
        {"label": "Pro#", "value": "PRO"},
        {"label": "Cargo value", "value": "CARGO"}
    ];

    var billTos = {list:[]}, locations = [];
    var dictionaryValues = [{0: 'COLLECT'}];

    var notifications = [{userId : 1, email : 'customerservice@plslogistics.com', notifications :['DETAILS']}];

    var mockShipmentUtils = {
        getDictionaryValues: function () {
            return {
                paymentTerms: dictionaryValues,
                billToRequiredField: mockRequiredFields
            };
        },
    removeAllNotificationsByType: function (notifications, notificationType) {
        _.remove(notifications, function(notification) {
        return notification.notificationSource === notificationType;
            });
        }
    };

    var mockAddressService = {
        listUserBillToAddresses: function (params, successCallback) {
            successCallback(billTos);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    var mocklocationService = {
        getAllForCustomer: function (params, successCallback) {
            successCallback(locations);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    var mockUserNotificationsService = {
        getUserNotifications: function (params, successCallback) {
            successCallback(notifications);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/bill-to-template.html', function ($provide) {
        $provide.factory('AddressService', function () {
            return mockAddressService;
        });
        $provide.factory('LocationsService', function () {
            return mocklocationService;
        });
        $provide.factory('ShipmentUtils', function () {
            return mockShipmentUtils;
        });
        $provide.factory('UserNotificationsService', function () {
            return mockUserNotificationsService;
        });
    }));

    beforeEach(inject(function ($compile, $httpBackend, $templateCache, $rootScope, $q) {
        scope = $rootScope;
        promiseProvider = $q;

        $httpBackend.when('GET', 'pages/tpl/pls-location-list-tpl.html').respond($templateCache.get('pages/tpl/pls-location-list-tpl.html'));
        $httpBackend.when('GET', 'pages/tpl/pls-bill-to-list-tpl.html').respond($templateCache.get('pages/tpl/pls-bill-to-list-tpl.html'));

        scope.$apply(function () {
            scope.selectedCustomer = {
                id: '1',
                name: 'PLS SHIPPER'
            };
            scope.shipment = {
                paymentTerms: 'PREPAID'
            };
        });

        element = angular.element('<div data-pls-bill-to data-shipment="shipment" data-customer="selectedCustomer" data-bill-to-form="billToForm" ' +
                'data-location-form="locationForm"></div>');
        $compile(element)(scope);
        scope.$digest();
        isoScope = element.isolateScope();

    }));

    it('Should select default location', function () {
        locations = mockLocations;

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.location).to.exist;
        c_expect(scope.shipment.location).to.eql(mockLocations[2]);
    });

    it('Should select location if Customer has only one location', function () {
        locations = [mockLocations[2]];

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.location).to.exist;
        c_expect(scope.shipment.location).to.eql(mockLocations[2]);
    });

    it('Should not select location', function () {
        locations = [mockLocations[0], mockLocations[1]];

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.location).not.to.exist;
    });

    it('Should select bill to from location', function () {
        locations = [mockLocations[2]];
        billTos = mockBillTos;

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.billTo).to.exist;
        c_expect(scope.shipment.billTo).to.eql(mockBillTos[2]);
    });

    it('Should select customer default bill to', function () {
        locations = [];
        billTos = mockBillTos;

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.billTo).to.exist;
        c_expect(scope.shipment.billTo).to.eql(mockBillTos[1]);
    });

    it('Should select bill to if Customer has only one bill to', function () {
        locations = [];
        billTos = [mockBillTos[4]];

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.billTo).to.exist;
        c_expect(scope.shipment.billTo).to.eql(mockBillTos[4]);
    });

    it('should not select location', function () {
        locations = [];

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.location).not.to.exist;
    });

    it('should not select bill to', function () {
        billTos = [];

        scope.$apply(function () {
            scope.$broadcast('event:initBillTo');
        });

        c_expect(scope.shipment.billTo).not.to.exist;
    });

    it('should change defualt notification after change location', function () {
        scope.$apply(function () {
            scope.shipment = {
                paymentTerms: 'PREPAID',
                finishOrder: {
                    shipmentNotifications: [
                        {emailAddress: 'customerservice@plslogistics.com', notificationType: 'DELIVERED', notificationSource: 'LOCATION_DEFAULT_NOTIFICATIONS'},
                        {emailAddress: 'customerservice@plslogistics.com', notificationType: 'PICK_UP', notificationSource: 'LOCATION_DEFAULT_NOTIFICATIONS'}
                    ]
                },
                location: {id: 1}
            };
        });

        isoScope.locationChange();
        c_expect(scope.shipment.finishOrder.shipmentNotifications).to.exist;
        c_expect(scope.shipment.finishOrder.shipmentNotifications).to.eql([
            {emailAddress: 'customerservice@plslogistics.com', notificationType: 'DETAILS', notificationSource: 'LOCATION_DEFAULT_NOTIFICATIONS'}
        ]);
    });
});