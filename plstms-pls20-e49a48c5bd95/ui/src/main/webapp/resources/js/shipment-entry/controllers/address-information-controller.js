angular.module('shipmentEntry').controller('AddressInformationController', ['$scope', '$timeout', 'AddressesListService', 
    'ShipmentUtils', 'CustomerInternalNoteService',
    function ($scope, $timeout, AddressesListService, ShipmentUtils, CustomerInternalNoteService) {
        'use strict';

        var clearCarrierPropositions = function (oldZip, newZip) {
            if (oldZip && newZip) {
                if (!_.isEqual(oldZip, newZip)) {
                    $scope.$root.$broadcast('event:clearCarrierPropositions');
                }
            }
        };

        $scope.updateOriginAddress = function (addressToUpdate) {
            if (addressToUpdate) {
                if ($scope.shipmentEntryData.shipment.originDetails.address) {
                    clearCarrierPropositions($scope.shipmentEntryData.shipment.originDetails.address.zip, addressToUpdate.zip);
                }

                $scope.shipmentEntryData.shipment.originDetails.address = addressToUpdate;
                ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications,
                    addressToUpdate.shipmentNotifications, true);
            }
        };

        $scope.updateDestinationAddress = function (addressToUpdate) {
            if (addressToUpdate) {
                if ($scope.shipmentEntryData.shipment.destinationDetails.address) {
                    clearCarrierPropositions($scope.shipmentEntryData.shipment.destinationDetails.address.zip, addressToUpdate.zip);
                }

                $scope.shipmentEntryData.shipment.destinationDetails.address = addressToUpdate;
                ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications,
                    addressToUpdate.shipmentNotifications, false);
            }
        };

        $scope.customerNotSelected = function () {
            return _.isUndefined($scope.shipmentEntryData.selectedCustomer);
        };

        $scope.addAddress = function (isOrigin) {
            $scope.$broadcast('event:showAddEditAddress', {
                selectedCustomerId: $scope.shipmentEntryData.selectedCustomer.id,
                validateWarning: true, hideTypesSelection: true, isOrigin: isOrigin
            });
        };

        $scope.editAddress = function (address, isOrigin) {
            $scope.$broadcast('event:showAddEditAddress', {
                selectedCustomerId: $scope.shipmentEntryData.selectedCustomer.id,
                address: address.id ? undefined : address,
                validateWarning: true,
                addressId: address.id,
                hideTypesSelection: true,
                isOrigin: isOrigin
            });
        };

        $scope.openGoogleMaps = function(isOrigin) {
            $scope.$broadcast('event:showGoogleMaps', {
                isOrigin: isOrigin
            });
        };

        $scope.$on('event:addressAltered', function (event, address, isOrigin) {
            var transferObject = {};
            AddressesListService.listUserContacts({
                customerId: $scope.shipmentEntryData.selectedCustomer.id,
                type: 'SHIPPING,BOTH'
            }, function (addresses) {
                transferObject = {
                    newAddress: address,
                    allAddresses: addresses
                };

                $scope.$broadcast('event:refreshAddresses', transferObject);
                ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications,
                    address.shipmentNotifications, isOrigin);
            });
        });

        var updateAddress = function () {
            $timeout(function () {
                var transferObject = {
                    originAddress: $scope.shipmentEntryData.shipment.originDetails.address,
                    destinationAddress: $scope.shipmentEntryData.shipment.destinationDetails.address
                };

                $scope.$broadcast('event:addressIsBeingEdited', transferObject);
            });
        };

        $scope.$on('event:editSuccess', function (event, transferObj, isOrigin) {
            if (isOrigin) {
                $scope.shipmentEntryData.shipment.originDetails.address = transferObj;
            } else {
                $scope.shipmentEntryData.shipment.destinationDetails.address = transferObj;
            }
            updateAddress();
        });

        var getlistUserContacts = function (selectedCustomerId) {
            AddressesListService.listUserContacts({customerId: selectedCustomerId, type: 'SHIPPING,BOTH'}, function (addresses) {
                $scope.addresses = addresses;

                if ($scope.shipmentEntryData.shipment.originDetails.address.zip.zip
                        && $scope.shipmentEntryData.shipment.destinationDetails.address.zip.zip) {
                    updateAddress();
                }
            });
        };

        $scope.openCopyFromDialog = function () {
            $scope.$broadcast('event:openCopyFromsDialog', $scope.shipmentEntryData.selectedCustomer);
        };

        if ($scope.shipmentEntryData.selectedCustomer && $scope.shipmentEntryData.selectedCustomer.id) {
            getlistUserContacts($scope.shipmentEntryData.selectedCustomer.id);
        }

        function getInternalNote(selectedCustomer) {
            if (selectedCustomer && selectedCustomer.id) {
                CustomerInternalNoteService.get({
                    customerId: selectedCustomer.id
                }, function (data) {
                    selectedCustomer.internalNote = data.data;
                });
            }
        }

        function clearShipmentEntryPage() {
            var selectedCustomer = $scope.shipmentEntryData.selectedCustomer;
            $scope.$root.$broadcast('event:productChanged');
            $scope.$broadcast('event:clearAddresses');
            $scope.$broadcast('event:updateNotifications', $scope.shipmentEntryData.selectedCustomer);
            $scope.$root.$broadcast('event:pls-clear-form-data');
            $scope.init();
            $scope.updateStatus();
            $scope.shipmentEntryData.selectedCustomer = selectedCustomer;
            getlistUserContacts($scope.shipmentEntryData.selectedCustomer.id);
            $scope.setConfirmedTermsOfAgreement(false);
            getInternalNote($scope.shipmentEntryData.selectedCustomer);
        }

        function revertCustomer() {
            $scope.shipmentEntryData.selectedCustomer = $scope.previouslySelectedCustomer;
        }

        function fireCustomerChangeDialog() {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Warning!',
                message: "This action will clear all fields on screen. Would you like to proceed?",
                okFunction: clearShipmentEntryPage,
                closeFunction: revertCustomer,
                confirmButtonLabel: "Yes",
                closeButtonLabel: "No"
            });
        }

        function isShipmentChanged() {
            // ignore freightBillPayTo
            var shipment = _.omit($scope.shipmentEntryData.shipment, 'freightBillPayTo');
            return _.isEqual(shipment, $scope.shipmentEntryData.emptyShipment);
        }

        $scope.$on('event:changeCustomer', function (event, selectedCustomer, previouslySelectedCustomer) {
            $scope.$broadcast('event:clearAddresses');

            if (_.isUndefined(selectedCustomer) || _.isUndefined(selectedCustomer.id) || _.isEqual(selectedCustomer, previouslySelectedCustomer)
                    || isShipmentChanged()) {
                if (selectedCustomer && selectedCustomer.id) {
                    getlistUserContacts(selectedCustomer.id);
                }
                getInternalNote(selectedCustomer);
                return;
            }

            if ($scope.isFormClean()) {
                $timeout(function() {
                    clearShipmentEntryPage();
                });
            } else {
                $scope.previouslySelectedCustomer = previouslySelectedCustomer;
                fireCustomerChangeDialog();
            }
        });

        $scope.$on('event:editShipment', function () {
            if (!$scope.addresses) {
                getlistUserContacts($scope.shipmentEntryData.selectedCustomer.id);
            }
        });

        $timeout(function() {
            document.getElementById('customerInput').focus();
        }, 1000);

        if ($scope.shipmentEntryData.shipment.id || $scope.$root.authData.assignedOrganization) {
            $timeout(function() {
                document.getElementById('addressCopyFromBtn').setAttribute('tabindex', '-1');
                document.getElementsByClassName('addressAddBtn')[0].focus();
            }, 2000);
        }

    }
]);