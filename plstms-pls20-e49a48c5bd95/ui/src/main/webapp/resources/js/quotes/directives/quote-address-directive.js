/**
 * AngularJS directive which displays address information with possibility to edit it.
 *
 * @author: Alexander Kirichenko
 * Date: 4/30/13
 * Time: 10:07 AM
 */
angular.module('plsApp').directive('plsQuoteAddress', ['AddressesListService', 'AddressNameService',
    function (AddressesListService, AddressNameService) {
        return {
            restrict: 'A',
            scope: {
                address: '=plsQuoteAddress',
                addressForm: '=',
                customerId: '=',
                parentDialog: '@',
                lazyLoading: '=',
                origin: '='
            },
            replace: false,
            templateUrl: 'pages/tpl/quote-address-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.selectedAddressName = $scope.address.addressName || null;
                $scope.selectedAddressCode = $scope.address.addressCode || null;
                $scope.customerId = $scope.customerId || $scope.$root.authData.organization.orgId;
                $scope.addressMap = {};
                $scope.addressNameSet = [];
                $scope.addressBookEntryExist = false;

                $scope.canEditAddress = function () {
                    return ($scope.selectedAddressName && $scope.selectedAddressCode);
                };

                $scope.checkAddressBookEntryExist = function () {
                    if ($scope.selectedAddressName && $scope.selectedAddressCode) {
                        AddressNameService.isAddressUnique($scope.selectedAddressName, $scope.selectedAddressCode, {
                            customerId: $scope.customerId
                        }).then(function (data) {
                            $scope.addressBookEntryExist = !(angular.isString(data.data) ? data.data === 'true' : data.data);
                        });
                    }
                };

                $scope.$root.$on('event:editSuccess', function (event, transferObj, isOrigin) {
                    $scope.selectedAddressName = $scope.address.addressName;
                    $scope.selectedAddressCode = $scope.address.addressCode;
                });

                $scope.fetchAddressBookEntries = function () {
                    $scope.addressMap = {};
                    $scope.addressNameSet = [];

                    if ($scope.address && $scope.address.zip && $scope.address.zip.zip) {
                        return AddressesListService.findAddressBookByZip({
                            customerId: $scope.customerId,
                            country: $scope.address.zip.country.id,
                            zip: $scope.address.zip.zip,
                            city: $scope.address.zip.city,
                            type: 'SHIPPING,BOTH'
                        }, function (data) {
                            angular.forEach(data, function (item) {
                                if (!$scope.addressMap[item.addressName]) {
                                    $scope.addressMap[item.addressName] = [];
                                    $scope.addressNameSet.push(item.addressName);
                                }
                                $scope.addressMap[item.addressName].push(item);
                            });
                        }).$promise;
                    }
                    return {
                        then: function () {
                        }
                    };
                };

                if ($scope.lazyLoading) {
                    $scope.$on('event:load-address-book', function () {
                        $scope.fetchAddressBookEntries();
                        $scope.checkAddressBookEntryExist();
                    });
                } else {
                    $scope.fetchAddressBookEntries();
                    $scope.checkAddressBookEntryExist();
                }

                $scope.findAddress = function (filterValue) {
                    var trimmedFilterValue = angular.uppercase(filterValue.trim());
                    var filtered = [];

                    if (angular.isArray($scope.addressNameSet)) {
                        if (trimmedFilterValue.length === 0) {
                            return $scope.addressNameSet;
                        }

                        return _.filter($scope.addressNameSet, function (item) {
                            return angular.uppercase(item).indexOf(trimmedFilterValue) !== -1;
                        });
                    }

                    return filtered;
                };

                $scope.findAddressCode = function (filterValue) {
                    var trimmedFilterValue = angular.uppercase(filterValue.trim());
                    var result = [];

                    if ($scope.selectedAddressName && $scope.addressMap[$scope.selectedAddressName]) {
                        result = _.pluck($scope.addressMap[$scope.selectedAddressName], 'addressCode');
                    }

                    return _.filter(result, function (item) {
                        return angular.uppercase(item).indexOf(trimmedFilterValue) !== -1;
                    });
                };

                $scope.$watch('address', function (address, oldValue) {
                    if (address !== oldValue) {
                        if (address && address.addressName) {
                            $scope.selectedAddressName = address.addressName;
                            $scope.selectedAddressCode = address.addressCode;
                        }

                        else {
                            $scope.selectedAddressName = null;
                            $scope.selectedAddressCode = null;
                        }

                        if (!address || !oldValue || !address.zip || !oldValue.zip || address.zip.zip !== oldValue.zip.zip) {
                            $scope.fetchAddressBookEntries();
                        }
                    }
                });

                $scope.$watch('selectedAddressName', function (selectedAddressName) {
                    var formId = ('#addressDirective' + $scope.origin).trim();
                    var formEl = angular.element(formId);
                    var input = formEl.find('.addressNameInp');

                    var inputModel = input.controller('ngModel');

                    if (inputModel) {
                        inputModel.$setValidity('required-model', $scope.selectedAddressName);
                    }

                    if (selectedAddressName && $scope.addressMap[selectedAddressName]) {
                        var addressListByName = $scope.addressMap[selectedAddressName];

                        if (addressListByName && addressListByName.length > 0) {
                            $scope.selectedAddressCode = addressListByName[0].addressCode;
                        }
                    }
                });

                $scope.$watch('selectedAddressCode', function (selectedAddressCode, prevValue) {
                    var formId = ('#addressDirective' + $scope.origin).trim();
                    var formEl = angular.element(formId);
                    var input = formEl.find('.addressCode');

                    var inputModel = input.controller('ngModel');

                    if (inputModel) {
                        inputModel.$setValidity('required-model', $scope.selectedAddressCode);
                    }

                    if (selectedAddressCode) {
                        if (selectedAddressCode !== prevValue) {
                            var newAddress = _.findWhere($scope.addressMap[$scope.selectedAddressName], {addressCode: selectedAddressCode});

                            if (newAddress) {
                                $scope.address = newAddress;
                                $scope.addressBookEntryExist = true;
                            }
                        }
                    }
                });

                $scope.updateAddressHandler = function (address) {
                    if (address) {
                        $scope.fetchAddressBookEntries().then(function () {
                            delete address.$promise;
                            delete address.$resolved;
                            $scope.address = address;
                            $scope.selectedAddressName = $scope.address.addressName;
                            $scope.selectedAddressCode = $scope.address.addressCode;

                            if ($scope.addressDirectiveForm) {
                                $scope.addressForm.invalid = $scope.addressDirectiveForm.$invalid || $scope.isRequiredPhoneMissing();
                            }
                        });
                    }
                };

                $scope.addAddressEntry = function (isOrigin) {
                    $scope.$root.$broadcast('event:showAddEditAddress', {
                        parentDialog: $scope.parentDialog, zipCodeReadOnly: true, zip: $scope.address.zip,
                        selectedCustomerId: $scope.customerId,
                        closeHandler: $scope.updateAddressHandler,
                        hideTypesSelection: true,
                        isOrigin: isOrigin
                    });
                };

                $scope.editAddressEntry = function (isOrigin) {
                    $scope.$root.$broadcast('event:showAddEditAddress', {
                        parentDialog: $scope.parentDialog, addressName: $scope.address.addressName,
                        addressCode: $scope.address.addressCode,
                        address: !$scope.addressBookEntryExist ? $scope.address : undefined,
                        zipCodeReadOnly: true, zip: $scope.address.zip,
                        selectedCustomerId: $scope.customerId,
                        closeHandler: $scope.updateAddressHandler,
                        hideTypesSelection: true,
                        isOrigin: isOrigin
                    });
                };

                $scope.openGoogleMaps = function() {
                    $scope.$root.$broadcast('event:showGoogleMaps', {
                        parentDialog: $scope.parentDialog,
                        isOrigin: $scope.origin
                    });
                };

                $scope.$watch('addressDirectiveForm.$invalid', function (newValue) {
                    if ($scope.addressForm) {
                        $scope.addressForm.invalid = newValue || $scope.isRequiredPhoneMissing();
                    }
                });

                $scope.isRequiredPhoneMissing = function () {
                    return $scope.$root.isFieldRequired('REQUIRE_PHONE_NUMBERS_FOR_ORDERS') && $scope.address && $scope.address.addressCode
                            && (!$scope.address.phone
                            || (!$scope.address.phone.areaCode
                            || !$scope.address.phone.countryCode
                            || !$scope.address.phone.number));
                };
            }]
        };
    }
]);