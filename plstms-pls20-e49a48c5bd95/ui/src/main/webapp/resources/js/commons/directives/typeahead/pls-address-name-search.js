/**
 * AngularJS directive which displays type-ahead input for address name search.
 */
angular.module('plsApp.directives').directive('plsAddressNameSearch', [function () {
    return {
        restrict: 'A',
        scope: {
            plsAddressValues: '=',
            addressNameUpdate: '&',
            isDeliveryAddress: '=',
            currentAddressName: '=',
            selectedCustomer: '='
        },
        replace: true,
        template: '<input data-ng-model="selectedAddress" data-typeahead-auto-select="true" ' +
        'data-pls-typeahead="address as address.name for address in findAddress($viewValue)" type="text">',
        controller: ['$scope', function ($scope) {
            'use strict';

            var NUMBER_OF_ADDRESS_RECORDS = 20;

            $scope.findAddress = function (criteria) {
                var countAddress = 0;

                if (criteria && $scope.source) {
                    return _.filter($scope.source, function (item) {
                        if (countAddress > NUMBER_OF_ADDRESS_RECORDS) {
                            return false;
                        }

                        if (item.name.toLowerCase().indexOf(criteria.toLowerCase()) !== -1) {
                            countAddress = countAddress + 1;
                            return true;
                        }
                        return false;
                    });
                }
                return {};
            };

            var getSourceItemIdentifiers = function (items) {
                return _.map(items, function (item) {
                    return {
                        id: item.id,
                        name: item.addressCode + ', ' + item.addressName + ', ' + item.contactName + ', ' + item.address1 + ', '
                            + item.zip.city + ', ' + item.zip.state + ', ' + item.zip.zip
                    };
                });
            };

            var updateSelection = function (addressId) {
                var selectedItem = _.find($scope.addressSource, function (item) {
                    return item.id === addressId;
                });

                $scope.selectedAddress = selectedItem.addressName;

                $scope.addressNameUpdate()({
                    addressToUpdate: selectedItem,
                    isDeliveryAddress: $scope.isDeliveryAddress
                });
            };

            $scope.$watch("plsAddressValues", function (address) {
                if (address) {
                    address.$promise.then(function (address) {
                        $scope.source = getSourceItemIdentifiers(address);
                        $scope.addressSource = address;
                    });
                }
            });

            $scope.$watch("selectedAddress", function (newVal, oldVal) {
                if (newVal && newVal.id) {
                    updateSelection(newVal.id);
                }

                if (oldVal && _.isUndefined(newVal)) {
                    $scope.cleanAddress();
                    $scope.addressNameUpdate()({clearFreightBill: true});
                }
            });

            $scope.$watch("currentAddressName.zip", function (newVal, oldVal) {
                if ($scope.selectedAddress && oldVal && _.isUndefined(newVal)) {
                    $scope.cleanAddress();
                }

                if (newVal && newVal.country) {
                    $scope.$emit('event:isChangedCountry', {
                        selectedCountry: newVal.country,
                        countryIndex: $scope.isDeliveryAddress ? 2 : 1
                    });
                }
            });

            $scope.$watch("currentAddressName.address", function (newValue) {
                if (newValue && newValue.addressName) {
                    $scope.selectedAddress = newValue.addressName;
                }
            });

            $scope.$watch("currentAddressName.company", function (newValue) {
                $scope.selectedAddress = newValue;
            });

            $scope.cleanAddress = function () {
                $scope.selectedAddress = undefined;
                $scope.addressNameUpdate()({
                    addressToUpdate: undefined,
                    isDeliveryAddress: $scope.isDeliveryAddress
                });
            };
        }]
    };
}]);
