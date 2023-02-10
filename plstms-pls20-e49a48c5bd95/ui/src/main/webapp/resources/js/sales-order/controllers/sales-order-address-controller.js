angular.module('pls.controllers').controller('SOAddressesCtrl', ['$scope', 'ShipmentMileageService', 'ShipmentUtils',
                                                                 function ($scope, ShipmentMileageService, ShipmentUtils) {
    'use strict';

    $scope.wizardData.showCustomsBroker = false;

    function setupCustomsBrokerFlag(origin, destination) {
        if (origin && origin.zip && origin.zip.country && destination && destination.zip && destination.zip.country) {
            $scope.wizardData.showCustomsBroker = origin.zip.country.id !== destination.zip.country.id;
        }
    }

    $scope.$root.$on('event:editSuccess', function (event, transferObj, isOrigin) {
        if (isOrigin){
            $scope.wizardData.shipment.originDetails.address = transferObj;
        } else {
            $scope.wizardData.shipment.destinationDetails.address = transferObj;
        }
    });

    function prepareShipmentMileageFilter() {
        var origin = $scope.wizardData.shipment.originDetails.address;
        var destination = $scope.wizardData.shipment.destinationDetails.address;

        if (!origin || !destination) {
            return;
        }

        var shipmentMileageData = {
            originAddress: {
                address1: origin.address1,
                address2: origin.address2,
                city: origin.zip.city,
                stateCode: origin.zip.state,
                postalCode: origin.zip.zip,
                countryCode: origin.zip.country.id
            },
            destinationAddress: {
                address1: destination.address1,
                address2: destination.address2,
                city: destination.zip.city,
                stateCode: destination.zip.state,
                postalCode: destination.zip.zip,
                countryCode: destination.zip.country.id
            }
        };

        if ($scope.wizardData.shipment.selectedProposition.mileage && _.isEqual($scope.wizardData.mileageFilter, shipmentMileageData)) {
            return false; // data to fetch shipment mileage is not changed
        }

        $scope.wizardData.mileageFilter = shipmentMileageData;
        return true; // shipment mileage should be updated
    }

    prepareShipmentMileageFilter();

    function updateShipmentMileage() {
        if (prepareShipmentMileageFilter()) {
            return ShipmentMileageService.getShipmentMileage($scope.wizardData.mileageFilter).then(function (response) {
                $scope.wizardData.shipment.selectedProposition.mileage = response && response.data ? response.data : 0;
            }, function () {
                $scope.wizardData.shipment.selectedProposition.mileage = 0;
            });
        }

        return true;
    }

    $scope.init = function () {
        if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map) {
            var stepObject = $scope.wizardData.breadCrumbs.map.addresses;
            stepObject.nextAction = updateShipmentMileage;
            stepObject.validNext = $scope.validateForm;
        }

        var originAddress = $scope.wizardData.shipment.originDetails.address;

        if (!originAddress || !_.isEqual(originAddress.zip, $scope.wizardData.shipment.originDetails.zip)) {
            originAddress = {};
            originAddress.addressName = '';
            originAddress.phone = {};
            originAddress.fax = {type: 'FAX'};
            originAddress.zip = $scope.wizardData.shipment.originDetails.zip;
            $scope.wizardData.shipment.originDetails.address = originAddress;
            $scope.wizardData.origForm = {};
            $scope.wizardData.shipment.finishOrder.pickupWindowFrom = undefined;
            $scope.wizardData.shipment.finishOrder.pickupWindowTo = undefined;
        }

        var destinationAddress = $scope.wizardData.shipment.destinationDetails.address;

        if (!destinationAddress || !_.isEqual(destinationAddress.zip, $scope.wizardData.shipment.destinationDetails.zip)) {
            destinationAddress = {};
            destinationAddress.addressName = '';
            destinationAddress.phone = {};
            destinationAddress.fax = {type: 'FAX'};
            destinationAddress.zip = $scope.wizardData.shipment.destinationDetails.zip;
            $scope.wizardData.shipment.destinationDetails.address = destinationAddress;
            $scope.wizardData.destForm = {};
        }

        $scope.wizardData.billToForm = {};
        $scope.wizardData.locationForm = {};
        setupCustomsBrokerFlag(originAddress, destinationAddress);
    };

    $scope.$watch('wizardData.shipment.billTo', function (newValue, oldValue) {
        if (newValue && (!oldValue || oldValue.id !== newValue.id) && (!$scope.editSalesOrderModel || !$scope.editSalesOrderModel.formDisabled)) {
            $scope.wizardData.shipment.customsBroker = {
                name: newValue.customsBroker,
                phone: newValue.brokerPhone
            };
        }
        if(oldValue && newValue && oldValue.id !== newValue.id){
            $scope.$root.$broadcast('event:updateFreightBillDate');
        }
    });

    $scope.validateForm = function () {
        var validateCustomerBroker = $scope.wizardData.showCustomsBroker ? $scope.wizardData.shipment.customsBroker
        && $scope.wizardData.shipment.customsBroker.name && $scope.wizardData.shipment.customsBroker.phone
        && $scope.wizardData.shipment.customsBroker.phone.countryCode && $scope.wizardData.shipment.customsBroker.phone.areaCode
        && $scope.wizardData.shipment.customsBroker.phone.number : true;

        var validateForm = !_.isUndefined($scope.wizardData.origForm.invalid) && !_.isUndefined($scope.wizardData.destForm.invalid)
                && !$scope.wizardData.origForm.invalid && !$scope.wizardData.destForm.invalid && !$scope.wizardData.billToForm.invalid
                && !$scope.wizardData.locationForm.invalid;

        return validateCustomerBroker && validateForm;
    };

    function isShipmentNotInTransitYet(status) {
        var statuses = {
            OPEN: "OPEN", BOOKED: "BOOKED", DISPATCHED: "DISPATCHED"
        };

        return _.contains(Object.keys(statuses), status);
    }

    $scope.$watch('wizardData.shipment.originDetails.address', function (newOriginAddress, oldOriginAddress) {
        if (newOriginAddress && $scope.wizardData.shipment && $scope.wizardData.shipment.finishOrder) {
            if (!$scope.wizardData.shipment.finishOrder.pickupWindowFrom || isShipmentNotInTransitYet($scope.wizardData.shipment.status)) {
                if (newOriginAddress.pickupWindowFrom !== undefined) {
                    $scope.wizardData.shipment.finishOrder.pickupWindowFrom = newOriginAddress.pickupWindowFrom;
                }
            }

            if (!$scope.wizardData.shipment.finishOrder.pickupWindowTo || isShipmentNotInTransitYet($scope.wizardData.shipment.status)) {
                if (newOriginAddress.pickupWindowFrom !== undefined) {
                    $scope.wizardData.shipment.finishOrder.pickupWindowTo = newOriginAddress.pickupWindowTo;
                }
            }

            if (newOriginAddress && (!oldOriginAddress || (newOriginAddress.addressId !== oldOriginAddress.addressId))) {
                $scope.wizardData.shipment.finishOrder.pickupWindowFrom = newOriginAddress.pickupWindowFrom;
                $scope.wizardData.shipment.finishOrder.pickupWindowTo = newOriginAddress.pickupWindowTo;
            }
        }
        setupCustomsBrokerFlag($scope.wizardData.shipment.originDetails.address, $scope.wizardData.shipment.destinationDetails.address);
        if (newOriginAddress !== oldOriginAddress) {
            ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                $scope.wizardData.shipment.finishOrder.shipmentNotifications,
                newOriginAddress.shipmentNotifications, true);
        }
    }, true);

    $scope.$watch('wizardData.shipment.destinationDetails.address', function (newDestinationAddress, oldDestinationAddress) {
        if (newDestinationAddress && (!oldDestinationAddress || (newDestinationAddress.addressId !== oldDestinationAddress.addressId))) {
            $scope.wizardData.shipment.finishOrder.deliveryWindowFrom = newDestinationAddress.deliveryWindowFrom;
            $scope.wizardData.shipment.finishOrder.deliveryWindowTo = newDestinationAddress.deliveryWindowTo;
        }

        setupCustomsBrokerFlag($scope.wizardData.shipment.originDetails.address, $scope.wizardData.shipment.destinationDetails.address);
        if(newDestinationAddress !== oldDestinationAddress) {
            ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                $scope.wizardData.shipment.finishOrder.shipmentNotifications,
                newDestinationAddress.shipmentNotifications, false);
        }
    }, true);

    if (!$scope.editSalesOrderModel) {
        $scope.$broadcast('event:initBillTo');
    } else {
        $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
            if (tabId === 'addresses') {
                $scope.$broadcast('event:initBillTo');

                if (!$scope.addressBookFetched) {
                    $scope.$broadcast('event:load-address-book');
                    $scope.addressBookFetched = true;
                }
            }
        });
    }
}]);