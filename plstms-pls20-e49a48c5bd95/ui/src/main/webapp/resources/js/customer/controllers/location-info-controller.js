angular.module('customer').controller('LocationInfoController', ['$scope', 'CustomerLocationsService', 'NgGridPluginFactory',
    function ($scope, CustomerLocationsService, NgGridPluginFactory) {
        'use strict';

        $scope.showLocationInfo = false;

        $scope.locationsGrid = {
            enableColumnResize: true,
            data: 'locationInfo',
            multiSelect: false,
            primaryKey: 'id',
            columnDefs: [
                {
                    field: 'location',
                    displayName: 'Location Name'
                },
                {
                    field: 'accountExecutive',
                    displayName: 'Account Executive'
                }
            ],
            progressiveSearch: false,
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.closeLocationInfoDialog = function () {
            $scope.showLocationInfo = false;
        };

        $scope.$on('event:openAccountExecutiveDialog', function (event, id, multipleAccountExecitive, accountExecutive, parentDialog) {
            CustomerLocationsService.getListForCustomer({customerId: id}, function (data) {
                if (data) {
                    $scope.locationInfo = data;
                    $scope.locationsGrid.columnDefs[1].visible = multipleAccountExecitive;
                    $scope.accountExecutive = accountExecutive;
                    $scope.showLocationInfo = true;
                } else {
                    $scope.$root.$emit('event:application-error', 'No Location Information!', 'No Location Information found for selected Customer.');
                }
            });
            $scope.locationInfoOptions = {
                parentDialog: parentDialog
            };
        });
    }
]);