angular.module('plsApp').controller('AddressesListCtrl', ['$scope', '$window', 'urlConfig', 'NgGridPluginFactory', 'AddressService',
    'DeleteAddressService', 'AddressImportService', '$q', '$timeout', 'ShipmentUtils',
    function ($scope, $window, urlConfig, NgGridPluginFactory, AddressService, DeleteAddressService, AddressImportService, $q, $timeout,
        ShipmentUtils) {
        'use strict';

        $scope.pageModel = {
            columnFilters: undefined,
            pagingConfig: undefined,
            sortConfig: undefined,
            addressBookData: [],
            selectedAddressBook: [],
            selectedRowIndex: undefined,
            selectedCustomer: {
                id: undefined,
                name: undefined
            },
            showWarnings: true
        }; 

        function addressBookToGeocodeString(addressBook) {
            var addrString = '';
            if (addressBook) {
                if (addressBook.address1) {
                    addrString += addressBook.address1;
                }
                if (addressBook.address2) {
                    if (addrString.length > 0) {
                        addrString += ', ';
                    }
                    addrString += addressBook.address2;
                }
                if (addressBook.zip) {
                    if (addrString.length > 0) {
                        addrString += ', ';
                    }
                    addrString += addressBook.zip.city;
                    if (addressBook.zip.state) {
                        addrString += ', ' + addressBook.zip.state;
                    }
                    addrString += ', ' + addressBook.zip.zip;
                }
            }
            return addrString;
        }

        $scope.loadAddressBook = function () {
            return AddressService.getAddressBookList({customerId: $scope.pageModel.selectedCustomer.id},
                    function (data) {
                        if (data) {
                            $scope.pageModel.addressBookData = data;
                        }
                    }).$promise;
        };

        var clearMapMarkers = function (markers) {
            angular.forEach(markers, function (marker) {
                marker.setMap(null);
            });
        };

        function clearSmallMap() {
            $scope.mapOptions.mapIsReady = false;
            clearMapMarkers($scope.mapOptions.mapMarkers);
            $scope.mapOptions.mapMarkers = undefined;
        }

        function centerSmallMap(entity) {
            if (entity) {
                $scope.mapOptions.mapIsReady = true;
                if (entity.latitude && entity.longitude) {
                    $scope.mapOptions.center = new google.maps.LatLng(entity.latitude, entity.longitude);
                    if ($scope.mapOptions.myMap) {
                        $scope.mapOptions.myMap.setCenter($scope.mapOptions.center);
                    }
                    $timeout(function () {
                        $scope.mapOptions.mapIsReady = true;
                    });
                } else {
                    new google.maps.Geocoder().geocode({
                        address: addressBookToGeocodeString(entity),
                        componentRestrictions: {country: entity.country.name}
                    }, function (geocodeResults) {
                        if (geocodeResults.length > 0) {
                            $scope.mapOptions.center = geocodeResults[0].geometry.location;
                        }
                        if ($scope.mapOptions.myMap) {
                            $scope.mapOptions.myMap.panTo($scope.mapOptions.center || new google.maps.LatLng(0, 0));
                        }
                        $scope.mapOptions.mapIsReady = true;
                        $scope.$digest();
                    });
                }
            }
        }

        function clearSelectedNotificationTypes() {
            _.each($scope.notificationTypes, function (notificationType) {
                notificationType.selected = false;
            });
        }

        $scope.addressBookGrid = {
            options: {
                data: 'pageModel.addressBookData',
                selectedItems: $scope.pageModel.selectedAddressBook,
                columnDefs: [
                    {
                        referenceId: 'addressNameColumn',
                        field: 'addressName',
                        displayName: 'Name',
                        width: '15%'
                    },
                    {
                        referenceId: 'addressCodeColumn',
                        field: 'addressCode',
                        displayName: 'Code',
                        width: '10%'
                    },
                    {
                        referenceId: 'address1Column',
                        field: 'address1',
                        displayName: 'Address',
                        width: '15%'
                    },
                    {
                        referenceId: 'cityColumn',
                        field: 'zip.city',
                        displayName: 'City',
                        width: '15%'
                    },
                    {
                        referenceId: 'stateColumn',
                        field: 'zip.state',
                        displayName: 'ST',
                        width: '10%'
                    },
                    {
                        referenceId: 'zipColumn',
                        field: 'zip.zip',
                        displayName: 'ZIP',
                        width: '5%',
                        cellTemplate: '<div data-ng-if="pageModel.showWarnings && row.entity.zip.warning" class="ngCellText text-center">'
                        + '{{row.entity.zip.zip}} <i data-pls-popover="warning{{row.entity.addressId}}" '
                        + 'data-placement="top" class="fa fa-exclamation-circle fa-lg color-warning"></i>'
                        + '<div data-ng-attr-id="warning{{row.entity.addressId}}">'
                        + '<div data-ng-include="\'pages/content/address-book/po-box-warning-tooltip.html\'"></div>'
                        + '</div></div>'
                        + '<div data-ng-if="!pageModel.showWarnings || !row.entity.zip.warning" class="ngCellText text-center">'
                        + '{{row.entity.zip.zip}}</div>'
                    },
                    {
                        referenceId: 'contactNameColumn',
                        field: 'contactName',
                        displayName: 'Contact Name',
                        width: '15%'
                    },
                    {
                        referenceId: 'typeColumn',
                        field: 'type',
                        displayName: 'Type',
                        width: '10%',
                        visible: $scope.$root.isPlsPermissions('CAN_SELECT_FRT_BILL_TO'),
                        //TODO use filter instead of cell template
                        cellTemplate: '<div data-ng-switch="row.entity.type" class="ngCellText text-center">' +
                        '<span data-ng-switch-when="SHIPPING">Shipping</span>' +
                        '<span data-ng-switch-when="FREIGHT_BILL">Freight Bill</span>' +
                        '<span data-ng-switch-when="BOTH">Both</span></div>'
                    },
                    {
                        referenceId: 'defaultColumn',
                        field: 'isDefault',
                        displayName: 'Default',
                        width: '5%',
                        visible: $scope.$root.isPlsPermissions('CAN_SELECT_FRT_BILL_TO'),
                        cellTemplate: 'pages/cellTemplate/checked-cell.html'
                    }
                ],
                beforeSelectionChange: function () {
                    clearSmallMap();
                    return true;
                },
                afterSelectionChange: function (rowItem) {
                    clearSelectedNotificationTypes();
                    if (rowItem && rowItem.entity) {
                        centerSmallMap(rowItem.entity);
                        if(rowItem.entity.shipmentNotifications && rowItem.entity.shipmentNotifications[0]) {
                            $scope.selectEmailAddress(rowItem.entity.shipmentNotifications[0].emailAddress, rowItem.entity.shipmentNotifications);
                        }
                    }
                },
                action: function () {
                    $scope.editAddress();
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
                enableColumnResize: true,
                multiSelect: false,
                progressiveSearch: true,
                sortInfo: {
                    fields: ['addressName'],
                    directions: ['asc']
                }
            }
        };

        $scope.selectEmailAddress = function (email, notifications) {
            $scope.selectedEmail = email;
            $scope.notificationTypes = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
            _.each($scope.notificationTypes, function (notificationType) {
                notificationType.selected = false;
            });
            var shipmentNotifications;
            if (notifications) {
                shipmentNotifications = notifications; 
            } else {
                shipmentNotifications = $scope.pageModel.selectedAddressBook[0].shipmentNotifications;
            }

            _.each(shipmentNotifications, function (notification) {
                if (email === notification.emailAddress) {
                    _.findWhere($scope.notificationTypes, {value: notification.notificationType}).selected = true;
                }
            });
        };
        $scope.showInternalNotes = false;

        $scope.shouldShowInternalNotes = function(isShow) {
            $scope.showInternalNotes = isShow;
        };

        $scope.getSelectedNotificationDirection = function () {
            if(!$scope.pageModel.selectedAddressBook || !$scope.pageModel.selectedAddressBook[0]){
                return;
            }
            var selectedEmail = _.findWhere($scope.pageModel.selectedAddressBook[0].shipmentNotifications, {emailAddress: $scope.selectedEmail});
            
            if (selectedEmail) {
                switch (selectedEmail.direction) {
                    case 'ORIGIN': return 'Origin';
                    case 'DESTINATION': return 'Destination';
                    case 'BOTH': return 'Both';
                    default: return null;
                }
            }
        };

        $scope.getEmails = function () {
            if ($scope.pageModel.selectedAddressBook[0] && $scope.pageModel.selectedAddressBook[0].shipmentNotifications) {
                return _.uniq(_.pluck($scope.pageModel.selectedAddressBook[0].shipmentNotifications, 'emailAddress'));
            } else {
                return [];
            }
        };

        $scope.addAddress = function () {
            $scope.$broadcast('event:showAddEditAddress', {selectedCustomerId: $scope.pageModel.selectedCustomer.id});
        };

        $scope.editAddress = function () {
            if ($scope.pageModel.selectedAddressBook && $scope.pageModel.selectedAddressBook.length === 1 &&
                    $scope.$root.isFieldRequired('ADD_EDIT_ADDRESS_BOOK_PAGE')) {
                $scope.$broadcast('event:showAddEditAddress',
                        {
                            addressId: $scope.pageModel.selectedAddressBook[0].id,
                            selectedCustomerId: $scope.pageModel.selectedCustomer.id
                        });
            }
        };

        var updateTableAndMasterDetail = function (shipmentNotifications) {
            clearSmallMap();
            var addressBook = $scope.loadAddressBook();
            if($scope.pageModel.selectedAddressBook && $scope.pageModel.selectedAddressBook[0]) {
                $scope.selectEmailAddress($scope.selectedEmail, shipmentNotifications);
            } else {
                clearSelectedNotificationTypes();
            }
            return addressBook;
        };

        $scope.$watch('pageModel.selectedCustomer', function (newValue, oldValue) {
            if (newValue && (newValue !== oldValue)) {
                $scope.pageModel.selectedAddressBook.length = 0;
                updateTableAndMasterDetail();
            }
        });

        $scope.$on('event:addressSavedOrUpdated', function (event, shipmentNotifications) {
            var defer = $q.defer();
            defer.promise.then(function () {
                return updateTableAndMasterDetail(shipmentNotifications);
            }).then(function () {
                if ($scope.pageModel.selectedAddressBook[0]) {
                    if (_.isEmpty($scope.addressBookGrid.options.ngGrid.columnFilters)) {
                        $scope.addressBookGrid.options.ngGrid.prevScrollTop = 0;
                        $scope.addressBookGrid.options.ngGrid.prevScrollIndex = 0;
                    } else {
                        $scope.pageModel.selectedAddressBook[0] = _.findWhere($scope.pageModel.addressBookData, {
                            id: $scope.pageModel.selectedAddressBook[0].id
                        });
                        centerSmallMap($scope.pageModel.selectedAddressBook[0]);
                    }
                }
                return defer.promise;
            });
            defer.resolve();
        });

        $scope.$on('ngGridEventData', function () {
            if ($scope.pageModel.selectedAddressBook[0] && $scope.addressBookGrid.options.ngGrid
                    && _.isEmpty($scope.addressBookGrid.options.ngGrid.columnFilters)) {
                angular.forEach($scope.addressBookGrid.options.ngGrid.filteredRows, function (data, index) {
                    if (data.entity.id === $scope.pageModel.selectedAddressBook[0].id) {
                        if (data.entity.version > $scope.pageModel.selectedAddressBook[0].version) {
                            $scope.addressBookGrid.options.selectRow(index, true);
                        }
                    }
                });
            }
        });

        $scope.deleteAddress = function () {
            if ($scope.pageModel.selectedAddressBook && $scope.pageModel.selectedAddressBook.length === 1) {
                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Delete Address',
                    message: "Are you sure you want to delete Address " + $scope.pageModel.selectedAddressBook[0].addressName + "?",
                    okFunction: $scope.deleteAddressDialog.deleteAddress
                });
            }
        };

        $scope.importAddresses = function () {
            $scope.importAddressOptions.showDialog = true;
        };

        $scope.exportAddresses = function () {
            $window.open('/restful/customer/' + $scope.pageModel.selectedCustomer.id + '/address/export', '_blank');
        };

        $scope.geocodeResults = [];

        $scope.chooseGeocodeResult = function (geocodeResult) {
            if ($scope.bigMapOptions.map) {
                $scope.bigMapOptions.map.setCenter(geocodeResult.geometry.location);
            }
        };

        $scope.mapOptions = {
            myMap: undefined,
            mapMarkers: undefined,
            mapIsReady: false,
            zoom: 15,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            disableDoubleClickZoom: true,
            draggable: false,
            keyboardShortcuts: false,
            panControl: false,
            rotateControl: false,
            streetViewControl: false
        };

        $scope.bigMapOptions = {
            positionWasChanged: false,
            chosenGeoResult: undefined,
            chosenMarker: undefined,
            confirmedMarker: undefined,
            map: undefined,
            infoWindow: undefined,
            mapMarkers: undefined,
            mapIsReady: false,
            zoom: 15,
            noClear: true,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        $scope.getMarkerImageUrl = function (index, title) {
            var markerState = 'active';
            if (title && $scope.bigMapOptions.confirmedMarker && title !== $scope.bigMapOptions.confirmedMarker.title) {
                markerState = 'inactive';
            }
            return 'resources/img/map_markers/' + markerState + '-marker' + String.fromCharCode(97 + index).toUpperCase() + '.png';
        };

        $scope.onMapClick = function () {
            $scope.fullMapDialog.show = true;
            var addrString = addressBookToGeocodeString($scope.pageModel.selectedAddressBook[0]);
            if (!_.isEmpty($scope.pageModel.selectedAddressBook)) {
                if (!$scope.pageModel.selectedAddressBook[0].latitude && !$scope.pageModel.selectedAddressBook[0].longitude) {
                    new google.maps.Geocoder().geocode({
                        address: addrString,
                        componentRestrictions: {country: $scope.pageModel.selectedAddressBook[0].country.name}
                    }, function (geocodeResults) {
                        $scope.geocodeResults = geocodeResults;
                        $scope.bigMapOptions.chosenGeoResult = geocodeResults[0];
                        $scope.bigMapOptions.mapIsReady = true;
                        $scope.$digest();
                    });
                }
                else {
                    $scope.bigMapOptions.center = new google.maps.LatLng($scope.pageModel.selectedAddressBook[0].latitude,
                            $scope.pageModel.selectedAddressBook[0].longitude);
                    $scope.bigMapOptions.chosenGeoResult = {
                        formatted_address: addrString,
                        geometry: {
                            location: $scope.bigMapOptions.center
                        }
                    };
                    $scope.geocodeResults = [$scope.bigMapOptions.chosenGeoResult];
                    $timeout(function () {
                        $scope.bigMapOptions.mapIsReady = true;
                    }, 0);
                }
            }
        };

        $scope.$on('event:dialogIsOpen', function () {
            $scope.bigMapOptions.mapIsReady = true;
        });

        var createMarkersForBigMap = function () {
            $scope.bigMapOptions.mapMarkers = [];
            angular.forEach($scope.geocodeResults, function (geocodeResult, index) {
                var marker = new google.maps.Marker({
                    map: $scope.bigMapOptions.map,
                    position: geocodeResult.geometry.location,
                    draggable: true,
                    title: geocodeResult.formatted_address,
                    icon: $scope.getMarkerImageUrl(index, geocodeResult.formatted_address)
                });
                marker.origGeoResult = geocodeResult;
                $scope.bigMapOptions.mapMarkers.push(marker);
            });
        };

        $scope.onBigMapIdle = function () {
            if ($scope.bigMapOptions.mapMarkers === undefined) {
                createMarkersForBigMap();
            }
        };

        $scope.onMapIdle = function () {
            if ($scope.mapOptions.mapMarkers === undefined) {
                $scope.mapOptions.mapMarkers = [new google.maps.Marker({
                    map: $scope.mapOptions.myMap,
                    position: $scope.mapOptions.center
                })];
            }
        };

        $scope.doMapSearch = function () {
            new google.maps.Geocoder().geocode({
                address: $scope.fullMapDialog.mapSearchString,
                region: 'US'
            }, function (geocodeResults) {
                clearMapMarkers($scope.bigMapOptions.mapMarkers);
                $scope.bigMapOptions.mapMarkers = undefined;
                $scope.geocodeResults = geocodeResults;
                if ($scope.bigMapOptions.chosenGeoResult) {
                    $scope.geocodeResults.unshift($scope.bigMapOptions.chosenGeoResult);
                }
                createMarkersForBigMap();
                $scope.bigMapOptions.map.panTo($scope.bigMapOptions.mapMarkers[0].position);
                $scope.$digest();
            });
        };

        $scope.bigMapMarkerClicked = function (marker) {
            $scope.bigMapOptions.chosenMarker = marker;
            $scope.bigMapOptions.infoWindow.open($scope.bigMapOptions.map, marker);
        };

        $scope.confirmSelectedMarker = function (selectedMarker) {
            if ($scope.bigMapOptions.confirmedMarker === undefined ||
                    ($scope.bigMapOptions.confirmedMarker && !$scope.bigMapOptions.confirmedMarker.position.equals(selectedMarker.position))) {
                $scope.bigMapOptions.confirmedMarker = selectedMarker;
                selectedMarker.origGeoResult.geometry.location = selectedMarker.position;
                clearMapMarkers($scope.bigMapOptions.mapMarkers);
                $scope.bigMapOptions.mapMarkers = undefined;
                createMarkersForBigMap();
                $scope.pageModel.selectedAddressBook[0].latitude = selectedMarker.position.lat();
                $scope.pageModel.selectedAddressBook[0].longitude = selectedMarker.position.lng();
                new AddressService($scope.pageModel.selectedAddressBook[0]).$save({
                    customerId: $scope.pageModel.selectedCustomer.id
                }, function () {
                    $scope.bigMapOptions.positionWasChanged = true;
                });
                $scope.bigMapOptions.map.panTo(selectedMarker.position);
            }
            $scope.bigMapOptions.infoWindow.close();
        };

        $scope.fullMapDialog = {
            show: false,
            close: function () {
                $scope.fullMapDialog.show = false;
                $scope.bigMapOptions.map = undefined;
                $scope.bigMapOptions.mapIsReady = false;
                $scope.bigMapOptions.mapMarkers = undefined;
                $scope.bigMapOptions.chosenGeoResult = undefined;
                $scope.bigMapOptions.chosenMarker = undefined;
                $scope.bigMapOptions.confirmedMarker = undefined;
                $scope.fullMapDialog.mapSearchString = '';
                if ($scope.bigMapOptions.positionWasChanged) {
                    updateTableAndMasterDetail();
                }
                $scope.bigMapOptions.positionWasChanged = false;
            }
        };
        $scope.deleteAddressDialog = {
            deleteAddress: function () {
                DeleteAddressService.deleteAddressById({
                    customerId: $scope.pageModel.selectedCustomer.id,
                    addressId: $scope.pageModel.selectedAddressBook[0].id
                }).success(function (data) {
                    var wasDeleted = angular.isString(data) ? data === 'true' : data;
                    if (wasDeleted) {
                        $scope.pageModel.selectedAddressBook.length = 0;
                        updateTableAndMasterDetail();
                    } else {
                        $scope.$root.$emit('event:application-error', 'Address delete failed!', 'Can\'t delete address with such id: ' +
                                $scope.pageModel.selectedAddressBook[0].id);
                    }
                });
            }
        };
        $scope.importAddressOptions = {
            label: 'Import Address',
            isAddress: true,
            showDialog: false,
            importUrl: function () {
                return urlConfig.shipment + '/customer/' + $scope.pageModel.selectedCustomer.id + '/address/import';
            },
            fixUrl: function () {
                return urlConfig.shipment + '/customer/' + $scope.pageModel.selectedCustomer.id +
                        '/address/import_fix_now_doc/';
            },
            removeFixDoc: function (docId) {
                AddressImportService.removeImportFixDoc({
                    customerId: $scope.pageModel.selectedCustomer.id,
                    docId: docId
                });
            },
            closeCallback: function () {
                updateTableAndMasterDetail();
            }
        };
    }
]);

angular.module('plsApp').controller('AddEditAddressCtrl', [
    '$scope', '$filter', 'AddressService', 'AddressNameService', 'DateTimeUtils', 'CountryService', 'ShipmentUtils',
    function ($scope, $filter, AddressService, AddressNameService, DateTimeUtils, CountryService, ShipmentUtils) {
        'use strict';

        var zipAutoCompleteCountry = ['CAN', 'MEX', 'USA'];
        var defaultPhone = {countryCode: '1'};

        $scope.editAddressModel = {
            showEditAddress: false,
            hideTypesSelection: false,
            label: '',
            address: {
                contactName: '',
                phone: angular.copy(defaultPhone),
                fax: angular.copy(defaultPhone)
            },
            previousCountry: undefined,
            zipAutoComplete: true,
            addressNameExist: false,
            locationCodeExist: false,
            originalAddressName: undefined,
            originalAddressCode: undefined,
            warningShowed: false,
            selectedCustomerId: $scope.authData.organization.orgId
        };

        var closeAddressDialog = function (data) {
            $scope.editAddressModel.address = {
                country: {
                    id: 'USA',
                    name: 'United States of America',
                    dialingCode: '1'
                },
                phone: angular.copy(defaultPhone),
                fax: angular.copy(defaultPhone)
            };
            $scope.$broadcast('event:cleaning-input');
            if ($scope.editAddressModel.closeHandler && angular.isFunction($scope.editAddressModel.closeHandler)) {
                $scope.editAddressModel.closeHandler(data);
            }
            $scope.editAddressModel.showEditAddress = false;
        };

        $scope.closeEditAddressDialog = function (data) {
            closeAddressDialog(data);
            $scope.$root.$broadcast('event:closeAddressDialog');
        };

        var saveAddress = function () {
            if ($scope.editAddressModel.address.type === 'SHIPPING') {
                $scope.editAddressModel.address.isDefault = false;
            }
            if ($scope.editAddressModel.address.fax && !$scope.editAddressModel.address.fax.number) {
                $scope.editAddressModel.address.fax = undefined;
            }
            if ($scope.editAddressModel.address.phone && !$scope.editAddressModel.address.phone.number) {
                $scope.editAddressModel.address.phone = undefined;
            }
            new AddressService($scope.editAddressModel.address).$save({
                customerId: $scope.editAddressModel.selectedCustomerId
            }, function (data) {
                if($scope.pageModel && $scope.pageModel.selectedAddressBook 
                        && !$scope.editAddressModel.address.addressId) {
                    $scope.pageModel.selectedAddressBook.length=0;
                }
                $scope.$emit('event:addressSavedOrUpdated', $scope.editAddressModel.address.shipmentNotifications);
                $scope.$emit('event:addressAltered', data, $scope.editAddressModel.isOrigin);
                closeAddressDialog(data);
            }, function (data) {
                if (data.status === 412) {
                    $scope.editAddressModel.locationCodeExist = true;
                } else if (data.status !== 500) {
                    $scope.$root.$emit('event:application-error', 'Address save failed!', data.data.message);
                }
                if (!$scope.editAddressModel.address.fax) {
                    $scope.editAddressModel.address.fax = angular.copy(defaultPhone);
                }
                if (!$scope.editAddressModel.address.phone) {
                    $scope.editAddressModel.address.phone = angular.copy(defaultPhone);
                }
            });
        };

        $scope.isAddressInvalid = function() {
            return !$scope.addEditAddressForm || $scope.addEditAddressForm.$invalid || $scope.editAddressModel.zipInconsistency;
        };

        $scope.saveEditAddress = function () {
            $scope.locationCodeErrorText = 'Loc. Code is duplicated';
            var pickupWindowDiff = DateTimeUtils.pickupWindowDifference($scope.editAddressModel.address.pickupWindowFrom,
                    $scope.editAddressModel.address.pickupWindowTo);
            var deliveryWindowDiff = DateTimeUtils.pickupWindowDifference($scope.editAddressModel.address.deliveryWindowFrom,
                    $scope.editAddressModel.address.deliveryWindowTo);
            if ((pickupWindowDiff !== undefined && pickupWindowDiff < 0.5) || (deliveryWindowDiff !== undefined && deliveryWindowDiff < 0.5)) {
                $scope.showPickupMessage();
                return;
            }
            $scope.editAddressModel.addressNameExist = false;
            $scope.editAddressModel.locationCodeExist = false;

            //this check is needed for correct working in IE10
            if (!$scope.editAddressModel.address.addressCode) {
                $scope.editAddressModel.address.addressCode = undefined;
            }
            if ($scope.addressBookEntryIsAbsent) {
                delete $scope.editAddressModel.address.addressId;
                $scope.$emit('event:editSuccess', $scope.editAddressModel.address, $scope.editAddressModel.isOrigin);
                closeAddressDialog();
                return;
            }
            if ($scope.editAddressModel.address.id && $scope.editAddressModel.originalAddressName === $scope.editAddressModel.address.addressName
                    && $scope.editAddressModel.originalAddressCode === $scope.editAddressModel.address.addressCode) {
                saveAddress();
            } else {
                AddressNameService.isAddressUnique($scope.editAddressModel.address.addressName, $scope.editAddressModel.address.addressCode,
                        {customerId: $scope.editAddressModel.selectedCustomerId})
                        .then(function (data) {
                            if (angular.isString(data.data) ? data.data === 'true' : data.data) {
                                saveAddress();
                            } else {
                                $scope.editAddressModel.addressNameExist = true;
                                $scope.editAddressModel.locationCodeExist = true;
                                $scope.$root.$emit('event:application-error', 'Address name and code isn\'t unique!', 'Address with the name \''
                                        + $scope.editAddressModel.address.addressName + '\' and with the code \'' +
                                        $scope.editAddressModel.address.addressCode + '\' already exist within you company!');
                            }
                        }, function () {
                            $scope.$root.$emit('event:application-error', 'Error on checking address name and code isn\'t uniqueness!',
                                    'Error on checking address name and code isn\'t uniqueness!');
                        });
            }
        };

        $scope.isDeliveryWindowRequired = function () {
            //XOR
            return (_.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.deliveryWindowTo"]').val()) ||
                            _.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.deliveryWindowFrom"]').val())
                    )
                    && !(_.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.deliveryWindowTo"]').val()) &&
                            _.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.deliveryWindowFrom"]').val())
                    );
        };

        $scope.isPickupWindowRequired = function () {
            //XOR
            return (_.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.pickupWindowTo"]').val()) ||
                            _.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.pickupWindowFrom"]').val())
                    )
                    && !(_.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.pickupWindowTo"]').val()) &&
                            _.isEmpty(angular.element('[data-pls-pickup-window="editAddressModel.address.pickupWindowFrom"]').val())
                    );
        };

        $scope.$watch('editAddressModel.address.country', function () {
            if ($scope.editAddressModel.address.country) {
                $scope.editAddressModel.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, $scope.editAddressModel.address.country.id) !== -1;
                if (!$scope.editAddressModel.zipCodeReadOnly && $scope.editAddressModel.previousCountry &&
                        $scope.editAddressModel.address.country.id !== $scope.editAddressModel.previousCountry.id) {
                    $scope.editAddressModel.address.zip = undefined;
                    $scope.editAddressModel.address.phone = {countryCode: $scope.editAddressModel.address.country.dialingCode};
                    $scope.editAddressModel.address.fax = {countryCode: $scope.editAddressModel.address.country.dialingCode};
                }
                $scope.editAddressModel.previousCountry = angular.copy($scope.editAddressModel.address.country);
            }
        });

        $scope.$watch('[editAddressModel.address.pickupWindowFrom , editAddressModel.address.pickupWindowTo]',
                function (values) {
                    if ($scope.editAddressModel.isCopyPickupWindow && !_.isEmpty(values[0])
                            && _.isEmpty($scope.editAddressModel.address.deliveryWindowFrom)) {
                        $scope.editAddressModel.address.deliveryWindowFrom = $scope.editAddressModel.address.pickupWindowFrom;
                    }
                    if ($scope.editAddressModel.isCopyPickupWindow && _.isEmpty($scope.editAddressModel.address.deliveryWindowTo)
                            && !_.isEmpty(values[1])) {
                        $scope.editAddressModel.address.deliveryWindowTo = $scope.editAddressModel.address.pickupWindowTo;
                    }
                    $scope.editAddressModel.isCopyPickupWindow = true;
                }, true);

        $scope.isNewNotification = function () {
            return _.find($scope.editAddressModel.address.shipmentNotifications, function (notification) {
                      return notification.emailAddress === $scope.editAddressModel.editedEmail;
                  }) === undefined;
        };

        $scope.isAnyNotificationSelected = function () {
            return _.findWhere($scope.notificationTypes, {selected: true}) !== undefined;
        };

        function clearSelectedNotificationTypes() {
            _.each($scope.notificationTypes, function (notificationType) {
                notificationType.selected = false;
            });
        }

        $scope.addEmailNotifications = function () {
            _.each($scope.notificationTypes, function (notificationType) {
                  if (notificationType.selected) {
                      $scope.editAddressModel.address.shipmentNotifications.push({
                          emailAddress: $scope.editAddressModel.editedEmail,
                          notificationType: notificationType.value,
                          direction: $scope.editAddressModel.editedEmailDirection
                      });
                }
            });
            $scope.editAddressModel.editedEmail = undefined;
            $scope.editAddressModel.editedEmailDirection = 'BOTH';
            clearSelectedNotificationTypes();
        };

        $scope.removeEmailNotifications = function () {
            $scope.editAddressModel.address.shipmentNotifications = _.filter($scope.editAddressModel.address.shipmentNotifications,
                  function (notification) {
                      return notification.emailAddress !== $scope.editAddressModel.editedEmail;
                  });

            $scope.editAddressModel.editedEmail = undefined;
            if ($scope.editAddressModel.address.shipmentNotifications.length === 0) {
                $scope.editAddressModel.editedEmailDirection = 'BOTH';
            }
            clearSelectedNotificationTypes();
        };

        $scope.openAddressesListDialog = function () {
            clearSelectedNotificationTypes();
            $scope.editAddressModel.editedEmail = '';
            $scope.$root.$broadcast('event:show-customer-notification-list', 'addressSelector');
        };

        $scope.selectEmailAddress = function (email) {
            if (!email || email === 'null') {
                clearSelectedNotificationTypes();
                return;
            }

            $scope.editAddressModel.editedEmail = email;

            if (!$scope.isNewNotification()) {
                clearSelectedNotificationTypes();

                _.each($scope.editAddressModel.address.shipmentNotifications, function (notification) {
                    if ($scope.editAddressModel.editedEmail === notification.emailAddress) {
                        _.findWhere($scope.notificationTypes, {value: notification.notificationType}).selected = true;
                        $scope.editAddressModel.editedEmailDirection = notification.direction;
                    }
                });
            }
        };

        $scope.getEmails = function () {
            if ($scope.editAddressModel.address.shipmentNotifications) {
                return _.uniq(_.pluck($scope.editAddressModel.address.shipmentNotifications, 'emailAddress'));
            } else {
                return [];
            }
        };

        $scope.changeNotificationType = function (notificationType) {
            if ($scope.editAddressModel.address.shipmentNotifications && !$scope.isNewNotification()) {
                if (notificationType.selected) {
                    $scope.editAddressModel.address.shipmentNotifications.push({
                        emailAddress: $scope.editAddressModel.editedEmail,
                        notificationType: notificationType.value,
                        direction: $scope.editAddressModel.editedEmailDirection
                    });
                } else {
                    $scope.editAddressModel.address.shipmentNotifications = _.filter($scope.editAddressModel.address.shipmentNotifications,
                          function (notification) {
                              return notification.emailAddress !== $scope.editAddressModel.editedEmail
                                      || notification.notificationType !== notificationType.value;
                          }
                    );
                }
            }
        };

        $scope.changeDirection = function (selectedEmail, direction) {
            var selectedNotifications = _.filter($scope.editAddressModel.address.shipmentNotifications, function (notification) {
                return notification.emailAddress === selectedEmail;
            });
            _.each (selectedNotifications, function (notification) {
                notification.direction = direction;
            });
        };

        $scope.$on('event:customer-notification-selected', function (event, addressItem, dialogId) {
            if(dialogId && dialogId !== 'addressSelector') {
                return;
            }
            $scope.selectEmailAddress(addressItem.email);
        });

        $scope.$on('event:showAddEditAddress', function (event, options) {
            $scope.notificationTypes = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
            $scope.editAddressModel.address.type = 'SHIPPING';
            $scope.editAddressModel.showEditAddress = true;
            $scope.editAddressModel.showEditAddressOptions = {};
            $scope.editAddressModel.previousCountry = {id: 'USA', name: 'United States of America', dialingCode: '1'};
            $scope.editAddressModel.addressNameExist = false;
            $scope.editAddressModel.locationCodeExist = false;
            $scope.editAddressModel.originalAddressName = undefined;
            $scope.editAddressModel.originalAddressCode = undefined;
            $scope.editAddressModel.zipCodeReadOnly = false;
            $scope.editAddressModel.label = 'Add Address';
            $scope.editAddressModel.zipInconsistency = false;
            $scope.editAddressModel.isEditAddress = false;
            $scope.editAddressModel.isCopyPickupWindow = false;
            $scope.editAddressModel.editedEmailDirection = 'BOTH';
            if (options) {
                $scope.addressBookEntryIsAbsent  = !_.isUndefined(options.address);
                $scope.editAddressModel.isOrigin = options.isOrigin;
                $scope.editAddressModel.selectedCustomerId = options.selectedCustomerId || $scope.editAddressModel.selectedCustomerId;
                $scope.editAddressModel.zipCodeReadOnly = options.zipCodeReadOnly === true;
                $scope.editAddressModel.validateWarning = options.validateWarning;
                $scope.editAddressModel.hideTypesSelection = options.hideTypesSelection;
                $scope.editAddressModel.closeHandler = options.closeHandler;
                $scope.editAddressModel.isEditAddress = (options.addressName && options.addressCode)
                                    || options.addressId || $scope.addressBookEntryIsAbsent;
                $scope.editAddressModel.showEditAddressOptions.parentDialog = options.parentDialog;
                if ($scope.editAddressModel.isEditAddress) {
                    $scope.editAddressModel.label = 'Edit Address';
                    if (options.address) {
                        $scope.editAddressModel.address = options.address;
                    } else {
                        AddressService.findAddress({
                            customerId: $scope.editAddressModel.selectedCustomerId,
                            addressCode: options.addressCode,
                            addressName: options.addressName,
                            subPath: options.addressId ? options.addressId : -1
                        }, function (data) {
                            if (options.zip && data.zip.zip !== options.zip.zip) {
                                $scope.$root.$emit('event:application-error', 'Address data inconsistency!',
                                        'You can\'t edit address in this mode because of zip inconsistency!');
                                $scope.editAddressModel.zipInconsistency = true;
                            }
                            $scope.editAddressModel.address = data;
                            //select first notification if exist
                            if ($scope.editAddressModel.address.shipmentNotifications &&
                                $scope.editAddressModel.address.shipmentNotifications.length > 0) {
                                  $scope.editAddressModel.address.selectedEmail = 
                                          $scope.editAddressModel.address.shipmentNotifications[0].emailAddress;
                            }
                            if (!$scope.editAddressModel.address.shipmentNotifications) {
                                $scope.editAddressModel.address.shipmentNotifications = [];
                            }
                            if (_.isEmpty($scope.editAddressModel.address.fax) || !$scope.editAddressModel.address.fax.countryCode) {
                                $scope.editAddressModel.address.fax = {countryCode: Number($scope.editAddressModel.address.country.dialingCode)};
                            }
                            if (_.isEmpty($scope.editAddressModel.address.phone) || !$scope.editAddressModel.address.phone.countryCode) {
                                $scope.editAddressModel.address.phone = {countryCode: Number($scope.editAddressModel.address.country.dialingCode)};
                            }
                            $scope.editAddressModel.previousCountry = angular.copy($scope.editAddressModel.address.country);
                            $scope.editAddressModel.originalAddressName = angular.copy($scope.editAddressModel.address.addressName);
                            $scope.editAddressModel.originalAddressCode = angular.copy($scope.editAddressModel.address.addressCode);
                            $scope.addEditAddressForm.zipInp.$setValidity('mistyped', !$scope.editAddressModel.zipInconsistency);
                        }, function () {
                            var errorMessage = 'Can\'t load address with ID ' + options.addressId;
                            if (options.addressName && options.addressCode) {
                                errorMessage = 'Can\'t load address with name: ' + options.addressName + ' and code: ' + options.addressCode;
                            }
                            $scope.$root.$emit('event:application-error', 'Address load failed!', errorMessage);
                        });
                    }
                } else {
                    if (options.zip) {
                        $scope.editAddressModel.address.zip = options.zip;
                        $scope.editAddressModel.address.country = options.zip.country || $scope.editAddressModel.address.country;
                        if ($scope.editAddressModel.address.country
                                && !$scope.editAddressModel.address.country.countryCode
                                && $scope.editAddressModel.address.country.id) {

                            CountryService.searchCountries($scope.editAddressModel.address.country.id, 1).then(function (data) {
                                $scope.editAddressModel.address.country = data[0];
                                $scope.editAddressModel.address.phone = {
                                    countryCode: $scope.editAddressModel.address.country.dialingCode
                                };
                                $scope.editAddressModel.address.fax = {
                                    countryCode: $scope.editAddressModel.address.country.dialingCode
                                };
                            });
                        }
                    }
                    $scope.editAddressModel.address.sharedAddress = true;
                    $scope.editAddressModel.address.shipmentNotifications = [];
                }
            }
        });

        $scope.showPickupMessage = function () {
            $scope.pickUpWindowOpen = true;
        };
    }
]);
