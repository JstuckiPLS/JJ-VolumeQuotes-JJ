/**
 * @author dnefedchenko
 *
 *  Autocomplete directive for address suggestion. It utilizes bootstrap3-typeahed component as base(see
 * https://github.com/bassjobsen/Bootstrap-3-Typeahead for more details).
 *
 *  Example of usage:
 *
 *  <data-pls-address-autocomplete data-num-items="15" data-ng-model="address" data-items="addresses"
 *          data-is-origin-address="true" data-on-address-update="updateOriginAddress(addressToUpdate);"
 *          data-on-address-create="addAddress();" data-on-address-edit="editAddress(addressToEdit);"/>
 *
 * Options:
 *
 * --> num-items - amount of suggested items(constant value)
 * --> items - actual addresses which are being used as source array(javascript array)
 * --> is-origin-address - whether or not address is origin(basically any not-null value is considered as true but to
 *      be more precise and explicit "true/false" is used. It's sufficient to specify "true" if address is origin and
 *      omit this value otherwise.)
 * --> on-address-update - parent controller's function to be invoked to set selected address
 * --> on-address-create - parent controller's function to be invoked to create new address
 * --> on-address-edit - parent controller's function to be invoked to edit existent address
 */
angular.module('plsApp.directives').directive('plsAddressAutocomplete', ['$parse', '$timeout',
    function ($parse, $timeout) {
    return {
        restrict: 'EA',
        scope: {
            isOriginAddress: '@',
            numberOfSuggestedItems: '@numItems',
            items: '=',
            addressForm: '=',
            onAddressUpdate: '&',
            onAddressCreate: '&',
            onAddressEdit: '&',
            onMapOpen: '&'
        },
        require: 'ngModel',
        replace: true,
        templateUrl: 'pages/tpl/address-autocomplete-template.html',
        link: function (scope, element, attributes, ngModel) {
            'use strict';

            function getSourceItemIdentifiers(items) {
                scope.idToCityStateZipMap = {};
                var sourceItems = _.map(items, function (item) {
                    var cityStateZip = item.zip.city + ', ' + item.zip.state + ', ' + item.zip.zip;
                    var fullAddress = item.addressName + ', ' + item.contactName + ', '
                            + item.address1 + ', '+ cityStateZip + ', ' + item.addressCode;

                    scope.idToCityStateZipMap[fullAddress] = item.id;
                    return fullAddress;
                });
                return sourceItems;
            }

            scope.updateAutocompleteSource = function (newSource) {
                scope.autocompleteElement.data('typeahead').source = getSourceItemIdentifiers(newSource);
            };

            function setFormInvalid(invalid) {
                if (invalid) {
                    scope.autocompleteElement.addClass('ng-invalid');
                } else {
                    scope.autocompleteElement.removeClass('ng-invalid');
                }
                scope.addressForm.invalid = invalid;
            }

            scope.$watch(function () {
                return scope[attributes.ngModel];
            }, function (newValue) {
                if (scope.autocompleteElement && newValue && newValue.zip.warning) {
                    scope.$root.$emit('event:application-warning',
                            'This zip code indicates a PO Box: ' + newValue.zip.zip,
                            'For the most accurate quote, please change to a non-PO Box zip code if one is available.');
                } else if (scope.autocompleteElement && !newValue) {
                    setFormInvalid(true);
                }
            });

            scope.getSuggestion = function (selectedItem) {
                if (!_.isEmpty(selectedItem.zip.country)) {
                    setFormInvalid(false);
                    var zip = selectedItem.zip;
                    var cityStateZip = zip.city + ', ' + zip.state + ', ' + zip.zip;
                    return selectedItem.addressName + ', ' + selectedItem.contactName + ', ' + selectedItem.address1
                            + ', ' + cityStateZip + ', ' + selectedItem.addressCode;
                }
            };

            scope.updateSelection = function (item) {
                var id = scope.idToCityStateZipMap[item];
                var selectedItem = _.find(scope.source, function (item) {
                    return item.id === id;
                });
                scope.addressName = item;

                if (selectedItem) {
                    $timeout(function () {
                        scope.$apply(function () {
                            scope[attributes.ngModel] = selectedItem;
                            scope.onAddressUpdate({addressToUpdate: selectedItem});
                        });
                    });
                }

                scope.autocompleteElement.val(item);
                setFormInvalid(false);
                return item;
            };

            scope.initAutocomplete = function (items) {
                scope.autocompleteElement = $(element.find('#autocomplete' + scope.$id)).typeahead({
                    items: scope.numberOfSuggestedItems,
                    source: getSourceItemIdentifiers(items),
                    matcher: function (item) {
                        scope.address = undefined;
                        return !_.isNull(item.toUpperCase().match(this.query.toUpperCase())) &&
                                item.toUpperCase().match(this.query.toUpperCase()).length > 0;
                    },
                    sorter: function (items) {
                        return items;
                    },
                    highlighter: function (item) {
                        var regex = new RegExp('(' + this.query + ')', 'gi');
                        scope.address = undefined;
                        scope.$apply();
                        return item.replace(regex, "<strong>$1</strong>");
                    },
                    updater: function (item) {
                        return scope.updateSelection(item);
                    }
                });
                setFormInvalid(_.isUndefined(scope.address) || _.isEmpty(scope.address));
            };

            $(element.find('#autocomplete' + scope.$id)).bind('blur', function () {
                setFormInvalid((_.isUndefined(scope.address) && !_.isEmpty(scope.source))
                        || scope.isRequiredPhoneMissing());
                scope.$apply();
            });
        },
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.refreshAutocomplete = function (addresses) {
                $scope.source = addresses;
                $scope.initAutocomplete(addresses);
                $scope.updateAutocompleteSource(addresses);
            };

            $scope.addressSelected = function (addressToUpdate) {
                return !_.isUndefined($scope.address);
            };

            var defineAddress = function () {
                $scope.anAddress = $scope.isOriginAddress === "true" ? 'origin' : 'destination';
            };

            $scope.addAddress = function () {
                defineAddress();
                $scope.onAddressCreate($scope.isOriginAddress);
            };

            function getFullAddress(address) {
                return _.find($scope.source, function (fullAddress) {
                    return _.isEqual(fullAddress.addressName, address.addressName) && _.isEqual(fullAddress.addressCode, address.addressCode);
                });
            }

            $scope.isEditDisabled = function () {
                return !$scope.addressSelected() || !$scope.$root.isFieldRequired('ADD_EDIT_ADDRESS_BOOK_PAGE');
            };

            $scope.editAddress = function () {
                defineAddress();
                var addressFromAddressBook = getFullAddress($scope.address);
                if (!$scope.address.id && addressFromAddressBook){
                    $scope.address = addressFromAddressBook;
                }
                var addressBackup = angular.copy($scope.address);
                $scope.onAddressEdit({addressToEdit: addressBackup, isOrigin: $scope.isOriginAddress});

            };

            $scope.openGoogleMaps = function() {
                $scope.onMapOpen({isOrigin: $scope.isOriginAddress});
            };

            $scope.updateWithSuggestion = function (suggestion) {
                $scope.updateSelection(suggestion);
                $scope.anAddress = undefined;
            };

            $scope.getSuggestion = function (selectedItem) {
                var zip = selectedItem.zip;
                var cityStateZip = zip.city + ', ' + zip.state + ', ' + zip.zip;
                return selectedItem.addressName + ', ' + selectedItem.address1 + ', ' + cityStateZip + ', ' + selectedItem.addressCode;
            };

            $scope.$on('event:refreshAddresses', function (event, transferObject) {
                $scope.source = transferObject.allAddresses;
                $scope.updateAutocompleteSource(transferObject.allAddresses);
                if ($scope.anAddress === 'origin') {
                    $scope.updateWithSuggestion($scope.getSuggestion(transferObject.newAddress));
                }
                if ($scope.anAddress === 'destination') {
                    $scope.updateWithSuggestion($scope.getSuggestion(transferObject.newAddress));
                }
            });

            var clearAddressForm = function () {
                $scope.address = undefined;
                if ($scope.autocompleteElement) {
                    $scope.autocompleteElement.val('');
                }
            };

            $scope.$on('event:pls-clear-form-data', function () {
                clearAddressForm();
                $scope.customerIsSelected = $scope.$root.authData.assignedOrganization;
            });

            $scope.isDisabled = function () {
                return true;
            };

            $scope.$on('event:clearAddresses', function () {
                delete $scope.originAddress;
                delete $scope.destinationAddress;
            });

            $scope.$on('event:closeAddressDialog', function () {
                $scope.anAddress = undefined;
            });

            var refreshAddresses = function () {
                $scope.customerIsSelected = true;
                clearAddressForm();
                if (!angular.isUndefined($scope.autocompleteElement)) {
                    if (!angular.isUndefined($scope.originAddress) && $scope.isOriginAddress === 'true') {
                        $scope.address = $scope.originAddress;
                        $scope.autocompleteElement.val($scope.getSuggestion($scope.address));
                        $scope.originAddress = undefined;
                    } else if (!angular.isUndefined($scope.destinationAddress)) {
                        $scope.address = $scope.destinationAddress;
                        $scope.autocompleteElement.val($scope.getSuggestion($scope.address));
                        $scope.destinationAddress = undefined;
                    }
                }
            };

            $scope.$watch('items', function (newArray) {
                if (_.isUndefined(newArray)) {
                    return;
                }
                $scope.refreshAutocomplete(newArray);
                refreshAddresses();
            });

            $scope.cleanAddress = function () {
                if (_.isEmpty($scope.addressName)) {
                    clearAddressForm();
                }
            };

            $scope.$on('event:addressIsBeingEdited', function (event, transferObject) {
                $timeout(function () {
                    $scope.originAddress = transferObject.originAddress;
                    $scope.destinationAddress = transferObject.destinationAddress;
                    refreshAddresses();
                });
            });

            $scope.isRequiredPhoneMissing = function () {
                return $scope.$root.isFieldRequired('REQUIRE_PHONE_NUMBERS_FOR_ORDERS') && $scope.address && !_.isEmpty($scope.address)
                        && (!$scope.address.phone || (!$scope.address.phone.areaCode || !$scope.address.phone.countryCode
                        || !$scope.address.phone.number));
            };
            
        }]
    };
}]);