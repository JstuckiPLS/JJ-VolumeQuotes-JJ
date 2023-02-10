angular.module('editCustomer').controller('EditCustomerLocationsCtrl', ['$scope', 'CustomerLocationsService', 'NgGridPluginFactory',
    function ($scope, CustomerLocationsService, NgGridPluginFactory) {
        'use strict';

        $scope.locationsModel = {
            selectedLocations: []
        };

        function getLocations() {
            CustomerLocationsService.getListForCustomer({
                customerId: $scope.editCustomerModel.customerId
            }, function (data) {
                if (data) {
                    $scope.locationsModel.locationsData = data;
                }
            }, function () {
                $scope.$root.$emit('event:application-error', 'Locations load failed!',
                        'Can\'t load locations for customer with ID ' + $scope.editCustomerModel.customerId);
            });
        }

        getLocations();

        $scope.$on('event:locationSaved', function () {
            $scope.locationsModel.selectedLocations.length = 0;
            getLocations();
        });

        $scope.locationsGrid = {
            options: {
                data: 'locationsModel.locationsData',
                selectedItems: $scope.locationsModel.selectedLocations,
                columnDefs: [
                    {
                        field: 'defaultNode',
                        displayName: '',
                        cellTemplate: 'pages/cellTemplate/checked-cell.html',
                        width: '2%'
                    },
                    {
                        field: 'location',
                        displayName: 'Name',
                        width: '20%'
                    },
                    {
                        field: 'accountExecutive',
                        displayName: 'Account Exec.',
                        width: '20%'
                    },
                    {
                        field: 'startDate',
                        displayName: 'Start Date',
                        cellFilter: 'date:wideAppDateFormat',
                        width: '18%',
                        visible: $scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')
                    },
                    {
                        field: 'endDate',
                        displayName: 'End Date',
                        cellFilter: 'date:wideAppDateFormat',
                        width: '18%',
                        visible: $scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')
                    },
                    {
                        field: 'billTo',
                        displayName: 'Bill To',
                        width: '20%'
                    }
                ],
                enableColumnResize: true,
                action: function () {
                    $scope.editLocation();
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()],
                multiSelect: false,
                sortInfo: {
                    fields: ['location'],
                    directions: ['asc']
                }
            }
        };

        $scope.addLocation = function () {
            $scope.$broadcast('event:showAddEditLocationDialog');
        };

        $scope.editLocation = function () {
            if ($scope.locationsModel.locationsData && $scope.locationsModel.selectedLocations.length === 1
                    && !_.isEmpty($scope.locationsModel.selectedLocations[0])) {
                $scope.$broadcast('event:showAddEditLocationDialog', $scope.locationsModel.selectedLocations[0]);
            }
        };
    }
]);