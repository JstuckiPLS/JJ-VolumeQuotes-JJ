describe('Shipment Entry address information controller unit test', function() {
    var $scope, controller;

    var mockAddressesListService = {
            listUserContacts : function() {
                return {addresses: []};
            }
        };

    var mockShipentUtils = {
        addAddressNotificationsToLoadNotificationsWithoutDuplicates : function() {
        }
    };

    var mockCustomerInternalNoteService = {
            get: function (params, successCallback) {
                successCallback(successCallback);
            }
        };

    beforeEach(module('shipmentEntry'));
    beforeEach(inject(function($controller, $rootScope, $timeout) {
        $scope = $rootScope.$new();
        $scope.$root = {
            authData: {
                assignedOrdanization: false
            }
        };

        $scope.shipmentEntryData = {};
        $scope.shipmentEntryData.shipment = {
            originDetails: {},
            destinationDetails: {},
            finishOrder: {}
        };

        $scope.shipmentEntryData.selectedCustomer = {
                id : 1,
                name : 'PLS SHIPPER'
            };

        controller = $controller('AddressInformationController', {
            $scope: $scope,
            $timeuot: $timeout,
            AddressesListService: mockAddressesListService,
            ShipmentUtils: mockShipentUtils,
            CustomerInternalNoteService:mockCustomerInternalNoteService
            });

        spyOn($scope, '$broadcast').and.callThrough();
    }));

    it ('should update origin address', function() {
        expect($scope.shipmentEntryData.shipment.originDetails.address).toBeUndefined();
        var originAddress = {id: '1', addressName: 'Banana Republic'};
        $scope.updateOriginAddress(originAddress);
        expect($scope.shipmentEntryData.shipment.originDetails.address).toBeDefined();
        expect($scope.shipmentEntryData.shipment.originDetails.address).toEqual(originAddress);
    });

    it ('should update destination address', function() {
        expect($scope.shipmentEntryData.shipment.destinationDetails.address).toBeUndefined();
        var destinationAddress = {id: '2', addressName: 'Flower School'};
        $scope.updateDestinationAddress(destinationAddress);
        expect($scope.shipmentEntryData.shipment.destinationDetails.address).toBeDefined();
        expect($scope.shipmentEntryData.shipment.destinationDetails.address).toEqual(destinationAddress);
    });

    it ('should return true if customer not selected', function() {
        $scope.shipmentEntryData.selectedCustomer = undefined;
        expect($scope.customerNotSelected()).toBeTruthy();
        $scope.shipmentEntryData.selectedCustomer = {id: '01'};
        expect($scope.customerNotSelected()).toBeFalsy();
    });

    it ('should open add/edit address dialog on addAddress invocation', function() {
        $scope.selectedCustomer = {
            id: 1
        };
        $scope.addAddress();
        expect($scope.$broadcast).toHaveBeenCalledWith('event:showAddEditAddress', {selectedCustomerId: $scope.selectedCustomer.id,
            validateWarning: true, hideTypesSelection: true, isOrigin:undefined});
    });

    it ('should open add/edit address dialog on editAddress invocation', function() {
        $scope.selectedCustomer = {
            id: 1,
        };
        var transferObject = {
            selectedCustomerId: 1,
            address: undefined,
            addressId: 1,
            validateWarning: true,
            hideTypesSelection: true,
            isOrigin: undefined
        };
        $scope.editAddress({id: 1});
        expect($scope.$broadcast).toHaveBeenCalledWith('event:showAddEditAddress', transferObject);
    });

    it ('should clear Addresses when changed customer', function() {
        $scope.$broadcast('event:changeCustomer');
        expect($scope.$broadcast).toHaveBeenCalledWith('event:clearAddresses');
    });

    it ('should open copy Froms Dialog', function() {
        $scope.openCopyFromDialog();
        expect($scope.$broadcast).toHaveBeenCalledWith('event:openCopyFromsDialog', $scope.shipmentEntryData.selectedCustomer);
    });
});