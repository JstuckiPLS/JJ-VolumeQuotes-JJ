angular.module('manualBol.controllers').controller('ManualBolAddressesController', ['$scope', '$timeout', 'manualBolModel', 'AddressesListService',
    function ($scope, $timeout, manualBolModel, AddressesListService) {
        'use strict';

        $scope.addressPage = {
            next: '/manual-bol/details',
            previous: '/manual-bol/general-information',
            bolModel: manualBolModel,
            showCustomsBroker: false
        };

        function setupCustomsBrokerFlag(origin, destination) {
            if (origin && origin.zip && origin.zip.country && destination && destination.zip && destination.zip.country) {
                $scope.addressPage.showCustomsBroker = origin.zip.country.id !== destination.zip.country.id;
            }
        }

        function updateAddress() {
            $timeout(function () {
                var transferObject = {
                    originAddress: $scope.addressPage.bolModel.shipment.originDetails.address,
                    destinationAddress: $scope.addressPage.bolModel.shipment.destinationDetails.address
                };

                $scope.$broadcast('event:addressIsBeingEdited', transferObject);
            });
        }

        function getlistUserContacts(selectedCustomerId) {
            AddressesListService.listUserContacts({customerId: selectedCustomerId, type: 'SHIPPING,BOTH'}, function (addresses) {
                $scope.addresses = addresses;
                $scope.addressPage.bolModel.addresses = addresses;
            });
        }

        $scope.init = function () {
            $scope.addressPage.bolModel.billToForm = {};
            $scope.addressPage.bolModel.locationForm = {};
            $scope.addressPage.bolModel.origForm = {};
            $scope.addressPage.bolModel.destForm = {};

            if (!$scope.addressPage.bolModel.addresses) {
                getlistUserContacts($scope.addressPage.bolModel.selectedCustomer.id);
            } else {
                $scope.addresses = $scope.addressPage.bolModel.addresses;
            }

            if (!_.isEmpty($scope.addressPage.bolModel.shipment.originDetails.address)
                    || !_.isEmpty($scope.addressPage.bolModel.shipment.destinationDetails.address)) {
                updateAddress();
            }
        };

        $scope.$on('event:addressAltered', function (event, address) {
            var transferObject = {};

            AddressesListService.listUserContacts({
                customerId: $scope.addressPage.bolModel.selectedCustomer.id,
                type: 'SHIPPING,BOTH'
            }, function (addresses) {
                transferObject = {
                    newAddress: address,
                    allAddresses: addresses
                };

                $scope.$broadcast('event:refreshAddresses', transferObject);
            });
        });

        $scope.updateOriginAddress = function (addressToUpdate) {
            if (addressToUpdate) {
                $scope.addressPage.bolModel.shipment.originDetails.address = addressToUpdate;
            }
        };

        $scope.updateDestinationAddress = function (addressToUpdate) {
            if (addressToUpdate) {
                $scope.addressPage.bolModel.shipment.destinationDetails.address = addressToUpdate;
            }
        };

        $scope.addAddress = function () {
            $scope.$broadcast('event:showAddEditAddress', {
                selectedCustomerId: $scope.addressPage.bolModel.selectedCustomer.id,
                validateWarning: true,
                hideTypesSelection: true
            });
        };

        $scope.editAddress = function (address, isOrigin) {
            $scope.$broadcast('event:showAddEditAddress', {
                selectedCustomerId: $scope.addressPage.bolModel.selectedCustomer.id,
                validateWarning: true,
                addressId: address.id,
                hideTypesSelection: true,
                address: address.id ? undefined : address,
                isOrigin: isOrigin
            });
        };

        $scope.$on('event:editSuccess', function (event, transferObj, isOrigin) {
            if (isOrigin) {
                $scope.addressPage.bolModel.shipment.originDetails.address = transferObj;
            } else {
                $scope.addressPage.bolModel.shipment.destinationDetails.address = transferObj;
            }
            updateAddress();
        });
        $scope.openGoogleMaps = function(isOrigin) {
            $scope.$broadcast('event:showGoogleMaps', {
                isOrigin: isOrigin
            });
        };

        $scope.$watch('addressPage.bolModel.shipment.billTo', function (newValue, oldValue) {
            if (newValue && (!oldValue || oldValue.id !== newValue.id)) {
                $scope.addressPage.bolModel.shipment.customsBroker = {
                    name: newValue.customsBroker,
                    phone: newValue.brokerPhone
                };
            }
        });

        $scope.canNextStep = function () {
            var validateCustomerBroker = $scope.addressPage.showCustomsBroker ? $scope.addressPage.bolModel.shipment.customsBroker
            && $scope.addressPage.bolModel.shipment.customsBroker.name
            && $scope.addressPage.bolModel.shipment.customsBroker.phone
            && $scope.addressPage.bolModel.shipment.customsBroker.phone.countryCode
            && $scope.addressPage.bolModel.shipment.customsBroker.phone.areaCode
            && $scope.addressPage.bolModel.shipment.customsBroker.phone.number : true;

            var validateForm = !_.isEmpty($scope.addressPage.bolModel.origForm)
                    && !_.isEmpty($scope.addressPage.bolModel.destForm)
                    && !$scope.addressPage.bolModel.origForm.invalid
                    && !$scope.addressPage.bolModel.destForm.invalid
                    && !$scope.addressPage.bolModel.billToForm.invalid
                    && !$scope.addressPage.bolModel.locationForm.invalid;

            return validateCustomerBroker && validateForm;
        };

        $scope.$watch('addressPage.bolModel.shipment.originDetails.address', function (newOriginAddress, oldOriginAddress) {
            if (newOriginAddress && $scope.addressPage.bolModel.shipment && $scope.addressPage.bolModel.shipment.finishOrder) {
                    if (newOriginAddress.pickupWindowFrom !== undefined) {
                        $scope.addressPage.bolModel.shipment.finishOrder.pickupWindowFrom = newOriginAddress.pickupWindowFrom;
                    }

                    if (newOriginAddress.pickupWindowFrom !== undefined) {
                        $scope.addressPage.bolModel.shipment.finishOrder.pickupWindowTo = newOriginAddress.pickupWindowTo;
                    }

                if (newOriginAddress && (!oldOriginAddress || (newOriginAddress.addressId !== oldOriginAddress.addressId))) {
                    $scope.addressPage.bolModel.shipment.finishOrder.pickupWindowFrom = newOriginAddress.pickupWindowFrom;
                    $scope.addressPage.bolModel.shipment.finishOrder.pickupWindowTo = newOriginAddress.pickupWindowTo;
                }
            }

            setupCustomsBrokerFlag($scope.addressPage.bolModel.shipment.originDetails.address,
                    $scope.addressPage.bolModel.shipment.destinationDetails.address);
        }, true);

        $scope.$watch('addressPage.bolModel.shipment.destinationDetails.address', function (newDestinationAddress, oldDestinationAddress) {
                if (newDestinationAddress && newDestinationAddress.deliveryWindowFrom !== undefined) {
                    $scope.addressPage.bolModel.shipment.finishOrder.deliveryWindowFrom = newDestinationAddress.deliveryWindowFrom;
                }

                if (newDestinationAddress && newDestinationAddress.deliveryWindowTo !== undefined) {
                    $scope.addressPage.bolModel.shipment.finishOrder.deliveryWindowTo = newDestinationAddress.deliveryWindowTo;
                }

            if (newDestinationAddress && (!oldDestinationAddress || (newDestinationAddress.addressId !== oldDestinationAddress.addressId))) {
                $scope.addressPage.bolModel.shipment.finishOrder.deliveryWindowFrom = newDestinationAddress.deliveryWindowFrom;
                $scope.addressPage.bolModel.shipment.finishOrder.deliveryWindowTo = newDestinationAddress.deliveryWindowTo;
            }

            setupCustomsBrokerFlag($scope.addressPage.bolModel.shipment.originDetails.address,
                    $scope.addressPage.bolModel.shipment.destinationDetails.address);
        }, true);
    }
]);