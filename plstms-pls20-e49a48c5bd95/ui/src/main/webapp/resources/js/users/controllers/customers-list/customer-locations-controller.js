angular.module('users.controllers').controller('CustomerLocationCtrl', ['$scope', 'ShipmentUtils', 'NgGridPluginFactory', 'CustomerLocationsService',
    function ($scope, ShipmentUtils, NgGridPluginFactory, CustomerLocationsService) {
        'use strict';

        var selectedCustomerId, userId;

        $scope.selectedLocation = [];
        $scope.allNotifications = undefined;
        $scope.editCustomerLocationDialogOptions = {};

        $scope.editCustomerLocationModel = {
            showCustomersLocationDialog: false,
            locationsData: []
        };

        function updateLocations(newLocations) {
            _.each($scope.locationsModel.locationsData, function (location) {
                var matchedLocation = _.findWhere(newLocations, {locationId: location.locationId});

                if (matchedLocation) {
                    location.isChecked = true;
                    location.notifications = matchedLocation.notifications;
                }
            });
        }

        $scope.closeEditCustomerLocationDialog = function () {
            $scope.editCustomerLocationModel.showCustomersLocationDialog = false;
            $scope.locationsModel.locationsData = [];
        };

        $scope.calculateSelectAll = function () {
            var maxCount = $scope.locationsModel.locationsData.length;
            var checkedlocationsCount = _.where($scope.locationsModel.locationsData, {isChecked: true}).length;
            $scope.locationsModel.selectAll = !checkedlocationsCount ? 'NONE' : (maxCount === checkedlocationsCount ? 'ALL' : 'SOME');
        };

        $scope.locationsModel = {
            selectAll: 'NONE',
            toggleSelectAll: function () {
                _.each($scope.locationsModel.locationsData, function (location) {
                    location.isChecked = $scope.locationsModel.selectAll === 'ALL';
                });
            },
            locationsData: []
        };

        $scope.customerLocationGrid = {
            data: 'locationsModel.locationsData',
            selectedItems: $scope.selectedLocation,
            multiSelect: false,
            columnDefs: [{
                field: 'isChecked',
                width: '10%',
                headerCellTemplate: '<div class="ngCellText text-center">'
                + '<input type="checkbox" data-ng-model="locationsModel.selectAll" data-ng-change="locationsModel.toggleSelectAll()"'
                + ' data-pls-tri-state-checkbox data-ng-true-value="ALL" data-ng-false-value="NONE"/></div>',
                cellTemplate: '<div class="ngCellText text-center"><input type="checkbox" data-ng-model="row.entity.isChecked"'
                + ' data-ng-change="calculateSelectAll()"/></div>'
            }, {
                field: 'locationName',
                displayName: 'Locations',
                width: '20%',
                cellClass: 'text-center'
            }, {
                field: 'defaultNode',
                displayName: 'Default Location',
                cellTemplate: 'pages/cellTemplate/checked-cell.html',
                cellClass: 'cellToolTip',
                width: '12%'
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                width: '20%',
                cellClass: 'text-center'
            }, {
                field: 'modifiedBy',
                displayName: 'Last Modified by',
                width: '20%',
                cellClass: 'text-center'
            }, {
                field: 'modifiedDate',
                displayName: 'Last Modified Date',
                width: '18%',
                cellClass: 'text-center'
            }],
            sortInfo: {fields: ['locationName'], directions: ['asc']},
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        function getCustomerLocations(locations) {
            CustomerLocationsService.getListForAssociatedCustomer({
                customerId: selectedCustomerId,
                userId: userId
            }, function (data) {
                $scope.locationsModel.locationsData = angular.copy(data);
                updateLocations(locations);
                $scope.calculateSelectAll();
            });
        }

        $scope.$on('event:openlocationDialog', function (event, transferObject) {
            $scope.selectedLocation.length = 0;
            selectedCustomerId = transferObject.customerId;
            userId = transferObject.userId;
            $scope.allNotifications = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
            $scope.editCustomerLocationModel.showCustomersLocationDialog = true;
            getCustomerLocations(transferObject.locations);
            $scope.editCustomerLocationDialogOptions.parentDialog = transferObject.parentDialog;
        });

        $scope.updateCustomersLocations = function () {
            var selectedLocations = _.where($scope.locationsModel.locationsData, {isChecked: true});

            var transferObject = {
                customerId: selectedCustomerId,
                locations: _.map(selectedLocations, function (location) {
                    return {locationId: location.locationId, notifications: location.notifications};
                })
            };

            $scope.$root.$broadcast('event:updateCustomerlocation', transferObject);
            $scope.closeEditCustomerLocationDialog();
        };
    }
]);